package com.nhnacademy.springboot.validator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProjectStateValidator.class)
public @interface ProjectState {
    String message() default "Invalid project state.";
    Class[] groups() default {};
    Class[] payload() default {};
}
