package com.example.fullstack.user;

import com.example.fullstack.project.Project;
import com.example.fullstack.task.Task;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class UserService {

  private static final Logger log = LoggerFactory.getLogger(UserService.class);

  public Uni<User> findById(long id) {
    return User.<User>findById(id).onItem().ifNull()
        .failWith(() -> new ObjectNotFoundException(id, "User"));
  }

  public Uni<User> findByName(String name) {
    return User.find("name", name).firstResult();
  }

  public Uni<List<User>> list() {
    return User.listAll();
  }

  @Transactional
  public Uni<User> create(User user) {
    user.password = BcryptUtil.bcryptHash(user.password);

    return user.persistAndFlush();
  }

  @Transactional
  public Uni<User> update(User user) {
    return findById(user.id).chain(() -> User.getSession())
        .chain(sessionUser -> sessionUser.merge(user));
  }

  @Transactional
  public Uni<Void> delete(long id) {
    return findById(id).chain(user -> Uni.combine().all()
        .unis(Task.delete("user.id", user.id), Project.delete("user.id", user.id)).asTuple()
        .chain(user::delete));
  }

  public Uni<User> getCurrentUser() {
    // TODO: replace implementation with the security

    return User.find("order by ID").firstResult();
  }

  public static boolean matches(User user, String password) {
    return BcryptUtil.matches(password, user.password);
  }
}
