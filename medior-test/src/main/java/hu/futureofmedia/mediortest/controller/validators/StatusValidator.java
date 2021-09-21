package hu.futureofmedia.mediortest.controller.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StatusValidator implements ConstraintValidator<EnumExists, Enum<?>> {
    private String enumValues;
    private Pattern pattern;

    @Override
    public void initialize( EnumExists annotation ) {
        enumValues = annotation.enumValues();

        try {
            pattern = Pattern.compile(annotation.enumValues());
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Given regex is invalid", e);
        }

    }

    @Override
    public boolean isValid( Enum<?> value, ConstraintValidatorContext constraintValidatorContext ) {
        if ( value == null ) {
            return true;
        }

        Matcher m = pattern.matcher(value.name());
        return m.matches();
    }

}
