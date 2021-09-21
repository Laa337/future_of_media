package hu.futureofmedia.mediortest.controller;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.futureofmedia.mediortest.dao.dto.Contact;
import hu.futureofmedia.mediortest.services.ContactService;

@RestController
@RequestMapping(path = "contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController( ContactService contactService ) {
        this.contactService = contactService;
    }

    @PostMapping(path = "create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contact> create( @Valid @RequestBody Contact contact ) {
        return ResponseEntity.ok(contactService.create(contact));
    }
}
