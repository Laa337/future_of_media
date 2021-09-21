package hu.futureofmedia.mediortest.dao.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import hu.futureofmedia.mediortest.controller.validators.CompanyExists;
import hu.futureofmedia.mediortest.controller.validators.EnumExists;
import hu.futureofmedia.mediortest.controller.validators.PhoneValidE164;
import hu.futureofmedia.mediortest.dao.entities.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contact {

    private Long id;
    @NotBlank(message = "The lastname is required.")
    private String lastName;
    @NotBlank(message = "The firstname is required.")
    private String firstName;
    @NotBlank(message = "The email is required.")
    @Email(message = "Not a valid email")
    private String email;

    @PhoneValidE164
    private String phoneNumber;
    @CompanyExists
    @NotBlank(message = "The company name is required.")
    private String company;
    private String note;

    @EnumExists(enumValues = "ACTIVE|DELETED")
    private Status status;

    private LocalDateTime created;
    private LocalDateTime lastModified;
}
