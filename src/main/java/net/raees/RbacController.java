package net.raees;

import io.grpc.stub.StreamObserver;
import net.raees.permissions.AddRoleResponse;
import net.raees.permissions.BindUserRequest;
import net.raees.permissions.Empty;
import net.raees.permissions.RBACGrpc;
import net.raees.permissions.RemoveRoleRequest;
import net.raees.permissions.ResourceAuthorizationRequest;
import net.raees.permissions.ResourceAuthorizationResponse;
import net.raees.permissions.Role;
import net.raees.permissions.User;

import java.sql.SQLException;

public final class RbacController extends RBACGrpc.RBACImplBase {

  private final RbacService service;

  RbacController(RbacService service) {
    this.service = service;
  }

  @Override
  public void addRole(Role role, StreamObserver<AddRoleResponse> responseObserver) {
    try {
      var uuid = service.addRole(role);
      responseObserver.onNext(AddRoleResponse.newBuilder().setUuid(uuid.toString()).build());
      responseObserver.onCompleted();
    } catch (SQLException sqlException) {
      responseObserver.onError(sqlException);
    }
  }

  @Override
  public void addUser(User user, StreamObserver<Empty> responseObserver) {
    try {
      service.addUser(user);
      responseObserver.onNext(Empty.getDefaultInstance());
      responseObserver.onCompleted();
    } catch (SQLException sqlException) {
      responseObserver.onError(sqlException);
    }
  }

  @Override
  public void bindUser(BindUserRequest request, StreamObserver<Empty> responseObserver) {
    try {
      service.bindUser(request);
      responseObserver.onNext(Empty.getDefaultInstance());
      responseObserver.onCompleted();
    } catch (SQLException sqlException) {
      responseObserver.onError(sqlException);
    }
  }

  @Override
  public void isAuthorized(ResourceAuthorizationRequest request, StreamObserver<ResourceAuthorizationResponse> responseObserver) {
    super.isAuthorized(request, responseObserver);
  }

  @Override
  public void removeRole(RemoveRoleRequest request, StreamObserver<Empty> responseObserver) {
    super.removeRole(request, responseObserver);
  }

  @Override
  public void removeUser(User request, StreamObserver<Empty> responseObserver) {
    super.removeUser(request, responseObserver);
  }

  @Override
  public void unbindUser(BindUserRequest request, StreamObserver<Empty> responseObserver) {
    super.unbindUser(request, responseObserver);
  }
}
