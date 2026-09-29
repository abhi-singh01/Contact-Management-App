package com.nextgen.contactmanager.Service;

//purpose: Loads user details based on email for authentication
import java.util.Optional;

import com.nextgen.contactmanager.Model.MyUser;
import com.nextgen.contactmanager.Repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service  // used to mark a class as a service provider that holds business logic
public class MyUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository repo;
    
    @Override  //used to indicate that a subclass method overrides a method in its superclass
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MyUser> user = repo.findByEmail(username);
        
        if (user.isPresent()) {
            var userObj = user.get();
            return User.builder()
                    .username(userObj.getEmail())
                    .password(userObj.getPassword())
                    .roles(userObj.getRole())
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found with Email: " + username);
        }
    }
}
