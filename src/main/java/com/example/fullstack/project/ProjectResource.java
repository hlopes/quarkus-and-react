package com.example.fullstack.project;

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

@Path("/api/v1/projects")
@RolesAllowed("user")
public class ProjectResource {

  private final ProjectService projectService;

  public ProjectResource(ProjectService projectService) {
    this.projectService = projectService;
  }

  @GET
  public Uni<List<Project>> getProjects() {
    return projectService.listForUser();
  }

  @POST
  @ResponseStatus(RestResponse.StatusCode.CREATED)
  public Uni<Project> create(Project project) {
    return projectService.create(project);
  }

  @GET
  @Path("{id}")
  public Uni<Project> getProject(@PathParam("id") long id) {
    return projectService.findById(id);
  }

  @PUT
  @Path("{id}")
  public Uni<Project> update(@PathParam("id") long id, Project project) {
    project.id = id;

    return projectService.update(project);
  }

  @DELETE
  @Path("{id}")
  public Uni<Void> delete(@PathParam("id") long id) {
    return projectService.delete(id);
  }
}
