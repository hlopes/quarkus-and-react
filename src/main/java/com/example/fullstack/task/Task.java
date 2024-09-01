package com.example.fullstack.task;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.fullstack.project.Project;
import com.example.fullstack.user.User;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "tasks")
public class Task extends PanacheEntity {
    @Column(nullable = false)
    public String title;

    @Column(length = 1000)
    public String description;

    @ManyToOne(optional = false)
    public User user;

    @ManyToOne
    public Project project;

    public Integer priority;

    public ZonedDateTime complete;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    public ZonedDateTime created;

    @Version
    public int version;
}
