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

    public void addRole(Role role) throws SQLException {
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
    }
}
