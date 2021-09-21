package hu.futureofmedia.mediortest.controller.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hu.futureofmedia.mediortest.dao.repositories.CompanyRepository;

@Component
public class CompanyValidator implements ConstraintValidator<CompanyExists, String> {
    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public void initialize( CompanyExists constraintAnnotation ) {

    }

    @Override
    public boolean isValid( String name, ConstraintValidatorContext constraintValidatorContext ) {
        try {
            companyRepository.findByname(name).orElseThrow(() -> new Exception(""));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
