package com.example.fullstack;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/non-blocking")
public class NonBlockingResource {

    @GET
    public Uni<String> hello() {
        return Uni.createFrom().item("Hello from Quarkus REST");
    }
}
