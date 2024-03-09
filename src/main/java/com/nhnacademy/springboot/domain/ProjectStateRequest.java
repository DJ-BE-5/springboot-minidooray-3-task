package com.nhnacademy.springboot.domain;

import com.sun.istack.NotNull;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectStateRequest {
    @NotNull
    @NotBlank
    String state;
}
