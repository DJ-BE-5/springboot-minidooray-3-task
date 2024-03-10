package com.nhnacademy.springboot.domain;

import com.sun.istack.NotNull;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class TaskRequest {
    @NotNull
    @NotBlank
    String title;
    String content;
}
