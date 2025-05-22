package org.levalnik.detailService;

import lombok.RequiredArgsConstructor;
import org.levalnik.client.UserClient;
import org.levalnik.dto.userDto.UserResponse;
import org.levalnik.enums.userEnum.UserRole;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthDetailService implements UserDetailsService {
    private final UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserResponse user = userClient.getByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new User(user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    private List<SimpleGrantedAuthority> mapRolesToAuthorities(List<UserRole> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_ " + role.name()))
                .collect(Collectors.toList());
    }
}
