package hu.futureofmedia.mediortest.dao.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import hu.futureofmedia.mediortest.dao.entities.ContactEntity;
import hu.futureofmedia.mediortest.dao.entities.Status;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, Long> , JpaSpecificationExecutor<ContactEntity> {

        List<ContactEntity> findAllByStatus( Status status,  Pageable pageable );

        @Modifying
        @Query("update ContactEntity c set c.status = :status where c.id = :id ")
        void inActivateContact(long id, Status status);
}
