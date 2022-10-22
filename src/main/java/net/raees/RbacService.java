package net.raees;

import net.raees.permissions.BindUserRequest;
import net.raees.permissions.Role;
import net.raees.permissions.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.stream.IntStream;

public final class RbacService {
  private final RbacDataStore dataStore;

  RbacService(RbacDataStore dataStore) {
    this.dataStore = dataStore;
  }

  public void addUser(User user) throws SQLException {
    try (var connection = dataStore.getConnection()) {
      var statement = connection.prepareStatement("INSERT INTO rbac.users (id) VALUES(?::uuid)");
      statement.setString(1, user.getUuid());
      statement.executeUpdate();
    }
  }

  public UUID addRole(Role role) throws SQLException {
    var uuid = UUID.randomUUID();

    var resources = IntStream
        .range(0, role.getResourcesCount())
        .mapToObj(i -> role.getResources(i))
        .toArray(String[]::new);

    var verbs = IntStream
        .range(0, role.getVerbsCount())
        .mapToObj(i -> role.getVerbs(i))
        .toArray(String[]::new);

    try (var connection = dataStore.getConnection()) {
      var statement = connection.prepareStatement("INSERT INTO rbac.roles (id, rName, resources, verbs) VALUES(?::uuid,?,?,?)");
      statement.setString(1, uuid.toString());
      statement.setString(2, role.getName());

      var resourcesSqlArray = connection.createArrayOf("text", resources);
      statement.setArray(3, resourcesSqlArray);

      var verbsSqlArray = connection.createArrayOf("text", verbs);
      statement.setArray(4, verbsSqlArray);

      statement.executeUpdate();
    }

    return uuid;
  }

  public void bindUser(BindUserRequest request) throws SQLException {
    var roleIds = IntStream
        .range(0, request.getRoleUuidsCount())
        .mapToObj(i -> request.getRoleUuids(i))
        .toArray(String[]::new);

    Connection connection = null;
    try {
      connection = dataStore.getConnection();
      connection.setAutoCommit(false);
      for (String roleId : roleIds) {
        var statement = connection.prepareStatement("INSERT INTO rbac.bindings (userId, roleId) VALUES(?::uuid,?::uuid)");
        statement.setString(1, request.getUserUuid());
        statement.setString(2, roleId);

        statement.executeUpdate();
        connection.commit();
      }
    } catch (SQLException sqlException) {
      connection.rollback();
      throw sqlException;
    } finally {
      connection.close();
    }
  }
}
