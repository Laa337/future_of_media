package hu.futureofmedia.mediortest.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import hu.futureofmedia.mediortest.dao.entities.UserEntity;

public class FoMUserDetails implements UserDetails {
    private UserEntity user;


//    private  PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(11);

    public FoMUserDetails( UserEntity user ) {
        this.user = user;
        String secret = new BCryptPasswordEncoder(11).encode("secret");
        System.out.println( secret + "\n" + user.getPassword());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }
}
