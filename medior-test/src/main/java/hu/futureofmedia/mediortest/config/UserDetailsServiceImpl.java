package hu.futureofmedia.mediortest.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import hu.futureofmedia.mediortest.services.UserService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;

    UserDetailsServiceImpl( UserService userService ) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername( String username ) {
        return new FoMUserDetails(userService.findUserByName(username).orElseThrow(() -> {
            throw new UsernameNotFoundException(username);
        }));
    }
}
