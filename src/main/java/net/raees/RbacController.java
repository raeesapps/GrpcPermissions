package net.raees;

import io.grpc.stub.StreamObserver;
import net.raees.permissions.*;

public final class RbacController extends RBACGrpc.RBACImplBase {

    private final RbacService service;

    RbacController(RbacService service) {
        this.service = service;
    }

    @Override
    public void addRole(Role request, StreamObserver<AddRoleResponse> responseObserver) {
        super.addRole(request, responseObserver);
    }

    @Override
    public void addUser(User request, StreamObserver<Empty> responseObserver) {
        super.addUser(request, responseObserver);
    }

    @Override
    public void bindUser(BindUserRequest request, StreamObserver<Empty> responseObserver) {
        super.bindUser(request, responseObserver);
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
