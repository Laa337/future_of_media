package hu.futureofmedia.mediortest.controller;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hu.futureofmedia.mediortest.dao.dto.Contact;
import hu.futureofmedia.mediortest.dao.dto.ContactListItem;
import hu.futureofmedia.mediortest.services.ContactService;
import hu.futureofmedia.mediortest.services.EmailService;
import hu.futureofmedia.mediortest.services.page.PagedRequest;

@RestController
@RequestMapping(path = "contact")
public class ContactController {

    private final ContactService contactService;
    private final EmailService emailService;

    public ContactController( ContactService contactService, EmailService emailService ) {
        this.contactService = contactService;
        this.emailService = emailService;
    }

    @PostMapping(path = "create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contact> create( @Valid @RequestBody Contact contact ) {
        Contact createdContact = contactService.create(contact);
        emailService.sendSimpleMessage(createdContact.getEmail(), "Udvozlet", String.format("Üdv, %s", createdContact.getFirstName()));
        return ResponseEntity.ok(createdContact);
    }

    @PostMapping(path = "all-active", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<ContactListItem>> getAllActive( @RequestBody PagedRequest request ) {
        List<ContactListItem> allActive = contactService.findAllActive(request);
        return ResponseEntity.ok(allActive);
    }
}
