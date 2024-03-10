package com.nhnacademy.springboot.repository;

import com.nhnacademy.springboot.domain.Project;
import com.nhnacademy.springboot.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends TaskRepositoryCustom, JpaRepository<Task, Long> {
    List<Task> findAllByProject(Project project);
    Task getTaskByTaskId(Long taskId);
}
