package net.raees;

import io.grpc.ManagedChannelBuilder;
import net.raees.permissions.RBACGrpc;
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
}
