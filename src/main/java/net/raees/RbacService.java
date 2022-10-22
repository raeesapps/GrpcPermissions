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
}
