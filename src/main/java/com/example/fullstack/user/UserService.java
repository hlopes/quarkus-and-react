package com.example.fullstack.user;

import java.util.List;

import org.hibernate.ObjectNotFoundException;

import com.example.fullstack.project.Project;
import com.example.fullstack.task.Task;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserService {

    public Uni<User> findById(long id) {
        return User.<User>findById(id)
                .onItem()
                .ifNull()
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
        return findById(user.id)
                .chain(() -> User.getSession())
                .chain(sessionUser -> sessionUser.merge(user));
    }

    @Transactional
    public Uni<Void> delete(long id) {
        return findById(id)
                .chain(user -> Uni.combine().all().unis(
                        Task.delete("user.id", user.id),
                        Project.delete("user.id", user.id))
                        .asTuple()
                        .chain(user::delete));
    }

    public Uni<User> getCurrentUser() {
        // TODO: replace implementation with the security

        return User.find("order by ID").firstResult();
    }
}
