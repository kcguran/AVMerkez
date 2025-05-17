package com.avmerkez.campaigneventservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.lang.reflect.Field;

public class DateRangeValidator implements ConstraintValidator<DateRangeValid, Object> {
    private String startField;
    private String endField;

    @Override
    public void initialize(DateRangeValid constraintAnnotation) {
        this.startField = constraintAnnotation.start();
        this.endField = constraintAnnotation.end();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Field start = value.getClass().getDeclaredField(startField);
            Field end = value.getClass().getDeclaredField(endField);
            start.setAccessible(true);
            end.setAccessible(true);
            Object startObj = start.get(value);
            Object endObj = end.get(value);
            if (startObj == null || endObj == null) return true;
            if (!(startObj instanceof LocalDateTime) || !(endObj instanceof LocalDateTime)) return true;
            LocalDateTime startDate = (LocalDateTime) startObj;
            LocalDateTime endDate = (LocalDateTime) endObj;
            return endDate.isAfter(startDate);
        } catch (Exception e) {
            return true;
        }
    }
} 