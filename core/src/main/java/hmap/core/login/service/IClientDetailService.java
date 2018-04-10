package hmap.core.login.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface IClientDetailService {
    UserDetails loadUserByUsername(String username, String password) throws UsernameNotFoundException;
}
