package br.com.finatech.infrastructure.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import br.com.finatech.domain.user.entity.User;
import br.com.finatech.domain.user.repository.UserRepository;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        return new UserDetailsImpl(user.getId(),user.getUserName());
    }

}
