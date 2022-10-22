package net.raees;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class GrpcServer {
  private static final Logger logger = Logger.getLogger(GrpcServer.class.getName());

  private final Server server;

  private GrpcServer(Server server) {
    this.server = server;
  }

  public static GrpcServer newGrpcServer(int port, boolean blockThread, List<BindableService> services) throws IOException, InterruptedException {
    var serverBuilder = ServerBuilder.forPort(port);
    for (var service : services) {
      serverBuilder.addService(service);
    }
    var grpcServer = new GrpcServer(serverBuilder.build().start());
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      logger.info("*** shutting down gRPC server since JVM is shutting down");
      try {
        grpcServer.stop();
      } catch (InterruptedException e) {
        e.printStackTrace(System.err);
      }
      logger.info("*** server shut down");
    }));

    if (blockThread) {
      grpcServer.blockUntilShutdown();
    }

    return grpcServer;
  }

  public static GrpcServer newGrpcServer(int grpcServerPort, boolean blockThread, String databaseUrl, String databaseUser) throws IOException, InterruptedException {
    var controller = new RbacController(new RbacService(new RbacDataStore(databaseUrl, databaseUser)));
    var controllers = new ArrayList<BindableService>();
    controllers.add(controller);

    return newGrpcServer(grpcServerPort, blockThread, controllers);
  }

  public void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }
}
