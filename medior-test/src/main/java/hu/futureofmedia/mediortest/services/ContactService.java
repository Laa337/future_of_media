package hu.futureofmedia.mediortest.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.futureofmedia.mediortest.dao.dto.Contact;
import hu.futureofmedia.mediortest.dao.entities.ContactEntity;
import hu.futureofmedia.mediortest.dao.repositories.CompanyRepository;
import hu.futureofmedia.mediortest.dao.repositories.ContactRepository;
import lombok.SneakyThrows;

@Service
@Transactional
public class ContactService {

    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;

    public ContactService( ContactRepository contactRepository, CompanyRepository companyRepository ) {
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
    }

    public Contact create( Contact contact ) {
        ContactEntity contactEntity = contactRepository.save(mapToEntity(contact));

        return mapToDto(contactEntity);
    }

    private ContactEntity mapToEntity(Contact contact) {
        return mapToEntity(contact, new ContactEntity());
    }

    @SneakyThrows
    private ContactEntity mapToEntity( Contact contact, ContactEntity entity ) {
        entity.setId(contact.getId());
        entity.setFirstName(contact.getFirstName());
        entity.setLastName(contact.getLastName());
        entity.setEmail(contact.getEmail());
        entity.setPhoneNumber(contact.getPhoneNumber());
        entity.setCompany(companyRepository.findByname(contact.getCompany()).orElseThrow( () -> new Exception("Company name not found")));
        entity.setNote(contact.getNote());
        entity.setStatus(contact.getStatus());

        return entity;
    }

    private Contact mapToDto(ContactEntity entity) {
        Contact dto = new Contact();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setCompany(entity.getCompany().getName());
        dto.setNote(entity.getNote());
        dto.setStatus(entity.getStatus());
        dto.setCreated(entity.getCreated());
        dto.setLastModified(entity.getLastModified());

        return dto;
    }
}
