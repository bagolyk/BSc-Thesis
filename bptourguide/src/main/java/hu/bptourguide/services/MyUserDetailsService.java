package hu.bptourguide.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.bptourguide.entities.User;
import hu.bptourguide.repositories.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	 System.out.println("sajtsajt");
        Optional<User> oUser = userRepository.findByUsername(username);
        if (!oUser.isPresent()) {
            throw new UsernameNotFoundException(username);
        }
        User user = oUser.get();
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole()));
        grantedAuthorities.forEach(x -> System.out.println(x) );
       
        if (user.getRole().equals("ADMIN")) {
        	grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
        }
        
        //System.out.println(user.getUsername());
        //System.out.println(user.getRole());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), 
        											user.getPassword(), grantedAuthorities);
    }
}
