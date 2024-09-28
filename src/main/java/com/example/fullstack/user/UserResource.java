package com.example.fullstack.user;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/api/v1/users")
@RolesAllowed("admin")
public class UserResource {

  private final UserService userService;

  public UserResource(UserService userService) {
    this.userService = userService;
  }

  @GET
  public Uni<List<User>> getUsers() {
    return userService.list();
  }

  @POST
  @ResponseStatus(RestResponse.StatusCode.CREATED)
  public Uni<User> create(User user) {
    return userService.create(user);
  }

  @GET
  @Path("{id}")
  public Uni<User> getUser(@PathParam("id") long id) {
    return userService.findById(id);
  }

  @PUT
  @Path("{id}")
  public Uni<User> update(@PathParam("id") long id, User user) {
    user.id = id;

    return userService.update(user);
  }

  @PUT
  @Path("self/password")
  @RolesAllowed("user")
  public Uni<User> changePassword(PasswordChange passwordChange) {
    return userService.changePassword(passwordChange.currentPassword(),
        passwordChange.newPassword());
  }

  @DELETE
  @Path("{id}")
  public Uni<Void> delete(@PathParam("id") long id) {
    return userService.delete(id);
  }

  @GET
  @Path("self")
  @RolesAllowed("user")
  public Uni<User> getSelf() {
    return userService.getCurrentUser();
  }
}
