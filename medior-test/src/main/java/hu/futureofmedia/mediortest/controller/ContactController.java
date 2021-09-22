package hu.futureofmedia.mediortest.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.futureofmedia.mediortest.dao.dto.Contact;
import hu.futureofmedia.mediortest.dao.dto.ContactListItem;
import hu.futureofmedia.mediortest.services.ContactService;
import hu.futureofmedia.mediortest.services.EmailService;
import hu.futureofmedia.mediortest.services.page.PagedRequest;

/**
 * A controller accepting request for contact CRUD operations. It provides all the basic functions
 * usually found in other crud controllers, namely:
 *    -  sends back a list of items in the form of listitems.
 *    -  sends back a detailed information of one particular item
 *    -  allows the front-end user to create a new item.
 *    -  allows the front-end user to update an existing item.
 *    -  allows the front-end user to logically delete an existing item.
 */
@RestController
@RequestMapping(path = "contact")
public class ContactController {

    private final ContactService contactService;
    private final EmailService emailService;

    /**
     * The intended only constructor for the class. Gets its parameters by spring autowiring mechanism.
     * @param contactService the service for controlling the flow of all contact crud operation
     * @param emailService   the service for email operations. Currently only used for sending greeting emails.
     */
    public ContactController( ContactService contactService, EmailService emailService ) {
        this.contactService = contactService;
        this.emailService = emailService;
    }

    /**
     * Rest endpoint for creating a new contact.
     * @param contact must be a valid Contact object.
     * @return  the created contact object, that is built from the saved entity. Differs from incoming contact object with having id.
     */
    @PostMapping(path = "create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contact> create( @Valid @RequestBody Contact contact ) {
        Contact createdContact = contactService.create(contact);
        emailService.sendSimpleMessage(createdContact.getEmail(), "Udvozlet", String.format("Üdv, %s", createdContact.getFirstName()));
        return ResponseEntity.ok(createdContact);
    }

    /**
     * Rest endpoint for updating an existing contact.
     * @param contact must be a valid Contact object.
     * @return  the updated contact object, that is built from the updated entity.
     */
    @PostMapping(path = "update", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contact> update( @Valid @RequestBody Contact contact ) {
        return ResponseEntity.ok(contactService.update(contact));
    }

    /**
     * Rest endpoint for listing all active contacts.
     * The query to retrieve the items is paginated and the list always contains 10 elements.
     * This list can be sorted and filtered.
     * @param request <pre> a pagedRequest object, that contains the following fields:
     *                            - pageNumber: pagenumber*10 - pageNumber*10+10 items are selected.
     *                            - sortField:  optional. The field for which the sort will be applied in asc order. Defaults to fullname.
     *                            - filter: optional. a string value which filters a result by all field with contains constraint.
     *                </pre>
     * @return  a paginated - and optionally sorted and filtered - list of 10 elements. The elements are contactListItem objects.
     */
    @PostMapping(path = "all-active", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ContactListItem>> getAllActive( @RequestBody PagedRequest request ) {
        List<ContactListItem> allActive = contactService.findAllActive(request);
        return ResponseEntity.ok(allActive);
    }

    /**
     * Rest endpoint, that returns  detailed information of one element.
     * @param id the id of the element we quesry
     * @return  the found item as Contact object, or a not-found answer.
     */
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contact> getContact( @PathVariable Long id ) {
        return contactService.findContactById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Rest controller for logically deleting a choosen element.
     * @param id the id of the element to be deleted.
     */
    @DeleteMapping(path = "/{id}")
    public void delete( @PathVariable Long id ) {
        contactService.delete(id);
    }
}
