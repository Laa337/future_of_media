package hu.futureofmedia.mediortest.dao.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;


@Table(name = "fom_contact")
@Data
@Entity
@EntityListeners( AuditingEntityListener.class )
public class ContactEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id", nullable = false )
    protected Long id;

    private String lastName;
    private String firstName;
    private String email;
    private String phoneNumber;
    @OneToOne
    @JoinColumn(name = "company_id")
    private CompanyEntity company;
    private String note;
    @Enumerated(EnumType.STRING)
    private Status status;


    @Column
    @CreatedDate
    private LocalDateTime created;

    @Column
    @LastModifiedDate
    private LocalDateTime lastModified;
}
