package hu.futureofmedia.mediortest.services;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.futureofmedia.mediortest.dao.entities.UserEntity;
import hu.futureofmedia.mediortest.dao.repositories.UserRepository;

@Service
@Transactional
public class UserService {

    private final UserRepository repository;

    public UserService(  UserRepository repository ) {
        this.repository = repository;
    }

    public Optional<UserEntity> findUserByName( String username ) {
        Optional<UserEntity> maybeUser = repository.findByUsername(username);
        return maybeUser;
    }
}
