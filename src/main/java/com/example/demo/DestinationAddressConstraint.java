package com.example.demo;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DestinationAddressValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DestinationAddressConstraint {
    String message() default "Address must be a valid Base58 or Bech32 address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
