package com.example.fullstack.auth;

import com.example.fullstack.auth.dtos.AuthRequest;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/api/v1/auth")
public class AuthResource {

  private final AuthService authService;

  @Inject
  public AuthResource(AuthService authService) {
    this.authService = authService;
  }

  @WithSession
  @PermitAll
  @POST
  @Path("/login")
  public Uni<String> login(AuthRequest authRequest) {
    return authService.authenticate(authRequest);
  }
}
