package net.raees;

import java.io.IOException;
import java.util.ArrayList;

public final class Main {
    public static void main(String[] args) {
        try {
            GrpcServer.newGrpcServer(50012, new ArrayList<>());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
