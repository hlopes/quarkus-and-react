package com.example.fullstack.auth;

import com.example.fullstack.auth.dtos.AuthRequest;
import com.example.fullstack.user.UserService;
import io.quarkus.security.AuthenticationFailedException;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashSet;

@ApplicationScoped
public class AuthService {

  private static final Logger log = LoggerFactory.getLogger(AuthService.class);
  private final String issuer;
  private final UserService userService;

  @Inject
  public AuthService(@ConfigProperty(name = "mp.jwt.verify.issuer") String issuer,
      UserService userService) {
    this.issuer = issuer;
    this.userService = userService;
  }

  public Uni<String> authenticate(AuthRequest authRequest) {
    return userService.findByName(authRequest.name()).onItem().transform(user -> {
      if (user == null || !UserService.matches(user, authRequest.password())) {
        throw new AuthenticationFailedException("Invalid Credentials");
      }

      return Jwt.issuer(issuer).upn(user.name).groups(new HashSet<>(user.roles))
          .expiresIn(Duration.ofHours(1L)).sign();
    });
  }
}
