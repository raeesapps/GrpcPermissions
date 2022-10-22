CREATE TABLE rbac.users (id uuid PRIMARY KEY);

CREATE TABLE rbac.roles (
    id uuid PRIMARY KEY,
    rName text,
    resources text[],
    verbs text[]
);

CREATE TABLE rbac.bindings (
    userId uuid,
    roleId uuid,
    PRIMARY KEY (userId, roleId),
    CONSTRAINT userId_id FOREIGN KEY (userId) REFERENCES users (id),
    CONSTRAINT roleId_id FOREIGN KEY (roleId) REFERENCES roles (id)
);
