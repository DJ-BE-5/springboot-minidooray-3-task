package com.nhnacademy.springboot.repository;

import com.nhnacademy.springboot.domain.ProjectMember;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryImplTest {
    @Autowired
    private TaskRepository taskRepository;



    @Test
    void test() {
        Object user1 = taskRepository.findProjectMemberByTaskIdAndAccountId(3L, "user1");
        System.out.println(user1);
    }

}