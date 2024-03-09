package com.nhnacademy.springboot.domain;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @NotNull
    @Column(name = "content_id", nullable = false)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "content", nullable = false)
    private String content;
}
