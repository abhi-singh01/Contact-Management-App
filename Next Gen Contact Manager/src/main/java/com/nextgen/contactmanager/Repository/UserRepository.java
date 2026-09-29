package com.nextgen.contactmanager.Repository;

import java.util.Optional;

import com.nextgen.contactmanager.Model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
@EnableWebMvc  // used for enabling Spring MVC in a application
public interface UserRepository extends JpaRepository<MyUser,String>{

	Optional<MyUser> findByEmail(String UserName);
}
