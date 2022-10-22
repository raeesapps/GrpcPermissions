package net.raees;

import io.grpc.ManagedChannelBuilder;
import net.raees.permissions.BindUserRequest;
import net.raees.permissions.RBACGrpc;
import net.raees.permissions.Role;
import net.raees.permissions.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public final class RbacIT {
  private static GrpcServer grpcServer;
  private static RBACGrpc.RBACBlockingStub blockingStub;

  @BeforeAll
  static void startServer() throws IOException, InterruptedException {
    var port = 50012;
    grpcServer = GrpcServer.newGrpcServer(
        port,
        false,
        System.getenv("PERMISSIONS_POSTGRES_URL"),
        System.getenv("PERMISSIONS_POSTGRES_USER")
    );

    var address = "localhost:" + port;
    blockingStub = RBACGrpc.newBlockingStub(
        ManagedChannelBuilder
            .forTarget(address)
            .usePlaintext()
            .build()
    );
  }

  @AfterAll
  static void stopServer() throws InterruptedException {
    grpcServer.stop();
  }

  private static ResultSet executeQuery(String query, Consumer<PreparedStatement> consumer) throws SQLException {
    try (var connection = DriverManager.getConnection(
        System.getenv("PERMISSIONS_POSTGRES_URL"),
        System.getenv("PERMISSIONS_POSTGRES_USER"),
        System.getenv("PERMISSIONS_POSTGRES_PASSWORD")
    )) {

      var statement = connection.prepareStatement(query);
      consumer.accept(statement);
      return statement.executeQuery();
    }
  }

  @Test
  public void addUser() throws SQLException {
    var uuid = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454";
    blockingStub.addUser(User.newBuilder().setUuid(uuid).build());

    var resultSet = executeQuery("SELECT ID FROM rbac.users WHERE id = ?::uuid", s -> {
      try {
        s.setString(1, uuid);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
    Assertions.assertTrue(resultSet.next());
    Assertions.assertEquals(uuid, resultSet.getString(1));
  }

  @Test
  public void addRole() throws SQLException {
    var roleName = "get-all-nodes";
    var role = Role.newBuilder().setName(roleName).addResources("nodes").addVerbs("get").build();
    blockingStub.addRole(role);

    var resultSet = executeQuery("SELECT rName, resources, verbs  FROM rbac.roles WHERE rName = ?", s -> {
      try {
        s.setString(1, roleName);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
    Assertions.assertTrue(resultSet.next());
    Assertions.assertEquals(roleName, resultSet.getString(1));
    Assertions.assertEquals("{nodes}", resultSet.getString(2));
    Assertions.assertEquals("{get}", resultSet.getString(3));
  }

  @Test
  public void bindUser() throws SQLException {
    var userUuid = "f9c3de3d-1fea-4d7c-a8b0-29f63c4c3454";
    blockingStub.addUser(User.newBuilder().setUuid(userUuid).build());
    var addRoleResponse = blockingStub.addRole(Role.newBuilder().setName("get-all-nodes").addResources("nodes").addVerbs("get").build());
    var roleUuid = addRoleResponse.getUuid();
    blockingStub.bindUser(BindUserRequest.newBuilder().setUserUuid(userUuid).addRoleUuids(roleUuid).build());

    var resultSet = executeQuery("SELECT userId, roleId FROM rbac.bindings", s -> {
    });
    Assertions.assertTrue(resultSet.next());
    Assertions.assertEquals(userUuid, resultSet.getString(1));
    Assertions.assertEquals(roleUuid, resultSet.getString(2));
  }
}
