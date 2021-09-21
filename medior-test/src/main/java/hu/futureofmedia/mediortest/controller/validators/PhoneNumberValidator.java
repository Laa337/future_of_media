package hu.futureofmedia.mediortest.controller.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import lombok.SneakyThrows;

public class PhoneNumberValidator implements ConstraintValidator<PhoneValidE164, String> {
    private  PhoneNumberUtil phoneNumberUtil;

    @Override
    public void initialize( PhoneValidE164 constraintAnnotation ) {
        phoneNumberUtil = PhoneNumberUtil.getInstance();
    }


    @Override
    public boolean isValid( String phoneNumberInput, ConstraintValidatorContext constraintValidatorContext ) {
        if( StringUtils.isEmpty(phoneNumberInput) )
            return true;
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(phoneNumberInput, Phonenumber.PhoneNumber.CountryCodeSource.UNSPECIFIED.name());
            String format = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            return format.equals(phoneNumberInput) && phoneNumberUtil.isValidNumber(phoneNumber);
        } catch (NumberParseException e) {
            return false;
        }
    }
}
