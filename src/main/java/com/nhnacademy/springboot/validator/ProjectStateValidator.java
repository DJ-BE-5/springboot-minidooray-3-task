package com.nhnacademy.springboot.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class ProjectStateValidator implements ConstraintValidator<ProjectState, String> {
    @Override
    public boolean isValid(String state, ConstraintValidatorContext constraintValidatorContext) {
        if(Objects.isNull(state)) {
            return true;
        }
        if(state.equals("ACTIVE") || state.equals("SLEEP") || state.equals("CLOSE")) {
            return true;
        }
        return false;
    }
}
