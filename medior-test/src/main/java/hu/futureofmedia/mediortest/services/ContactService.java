package hu.futureofmedia.mediortest.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.futureofmedia.mediortest.dao.dto.Contact;
import hu.futureofmedia.mediortest.dao.dto.ContactListItem;
import hu.futureofmedia.mediortest.dao.entities.CompanyEntity_;
import hu.futureofmedia.mediortest.dao.entities.ContactEntity;
import hu.futureofmedia.mediortest.dao.entities.ContactEntity_;
import hu.futureofmedia.mediortest.dao.entities.Status;
import hu.futureofmedia.mediortest.dao.repositories.CompanyRepository;
import hu.futureofmedia.mediortest.dao.repositories.ContactRepository;
import hu.futureofmedia.mediortest.services.page.PagedRequest;
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

    public List<ContactListItem> findAllActive( PagedRequest request ) {
        Sort sort = (request.getSortField() == null || request.getSortField().equals("fullName")) ?
                Sort.by("lastName").and(Sort.by("firstName")) :
                Sort.by(request.getSortField());
        Pageable pageable =
                PageRequest.of(request.getPageNumber(), 10, sort);

        List<ContactEntity> allActive;
        if ( StringUtils.isEmpty(request.getFilter()) ) {
            allActive = contactRepository.findAllByStatus(Status.ACTIVE, pageable);
        } else {
            Page<ContactEntity> allActivePaged = contactRepository.findAll(contactFilter("d"), pageable);
            allActive = allActivePaged.getContent();
        }

        return allActive.stream().map(this::mapToListItem).collect(Collectors.toList());
    }

    private ContactEntity mapToEntity( Contact contact ) {
        return mapToEntity(contact, new ContactEntity());
    }

    @SneakyThrows
    private ContactEntity mapToEntity( Contact contact, ContactEntity entity ) {
        entity.setId(contact.getId());
        entity.setFirstName(contact.getFirstName());
        entity.setLastName(contact.getLastName());
        entity.setEmail(contact.getEmail());
        entity.setPhoneNumber(contact.getPhoneNumber());
        entity.setCompany(companyRepository.findByname(contact.getCompany()).orElseThrow(() -> new Exception("Company name not found")));
        entity.setNote(contact.getNote());
        entity.setStatus(contact.getStatus());

        return entity;
    }

    private Contact mapToDto( ContactEntity entity ) {
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

    public ContactListItem mapToListItem(ContactEntity entity) {
        return ContactListItem.builder()
                .id(entity.getId())
                .fullName(String.format("%s %s", entity.getLastName(), entity.getFirstName()))
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .company(entity.getCompany().getName())
                .build();
    }

    public static Specification<ContactEntity> contactFilter( String filter ) {
        return ( root, criteriaQuery, builder ) -> {
            Predicate statusEqual = builder.equal(root.get(ContactEntity_.STATUS), Status.ACTIVE);
            Predicate companyLike = builder.like(root.get(ContactEntity_.COMPANY).get(CompanyEntity_.NAME), makeContainable(filter));
            Predicate emailLike = builder.like(root.get(ContactEntity_.EMAIL), makeContainable(filter));
            Predicate firstNameLike = builder.like(root.get(ContactEntity_.FIRST_NAME), makeContainable(filter));
            Predicate lastNameLike = builder.like(root.get(ContactEntity_.LAST_NAME), makeContainable(filter));
            Predicate phoneNumberLike = builder.like(root.get(ContactEntity_.PHONE_NUMBER), makeContainable(filter));

            return builder.and(statusEqual, builder.or(companyLike, emailLike, firstNameLike, lastNameLike, phoneNumberLike));
        };
    }

    private static String makeContainable( String filter ) {
        return "%".concat(filter).concat("%");
    }

}
