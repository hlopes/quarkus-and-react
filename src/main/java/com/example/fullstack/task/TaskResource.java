package com.example.fullstack.task;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import java.util.List;

@Path("/api/v1/tasks")
@RolesAllowed("user")
public class TaskResource {

  private final TaskService taskService;

  public TaskResource(TaskService taskService) {
    this.taskService = taskService;
  }

  @GET
  public Uni<List<Task>> getTasks() {
    return taskService.listForUser();
  }

  @GET
  @Path("{id}")
  public Uni<Task> getTask(@PathParam("id") long id) {
    return taskService.findById(id);
  }

  @POST
  public Uni<Task> create(Task task) {
    return taskService.create(task);
  }

  @PUT
  @Path("{id}")
  public Uni<Task> update(@PathParam("id") long id, Task task) {
    task.id = id;

    return taskService.update(task);
  }

  @DELETE
  @Path("{id}")
  public Uni<Void> delete(@PathParam("id") long id) {
    return taskService.delete(id);
  }

  @PATCH
  @Path("{id}")
  public Uni<Boolean> setComplete(@PathParam("id") long id, boolean complete) {
    return taskService.setComplete(id, complete);
  }
}
