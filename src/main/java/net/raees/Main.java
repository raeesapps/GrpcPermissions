package net.raees;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            GrpcServer.newGrpcServer(
                    50012,
                    true,
                    System.getenv("PERMISSIONS_POSTGRES_URL"),
                    System.getenv("PERMISSIONS_POSTGRES_USER")
            );
        } catch (Exception e) {
            logger.log(Level.SEVERE, "an exception was thrown", e);
        }
    }
}
