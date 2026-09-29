package com.nextgen.contactmanager.Service;

import com.nextgen.contactmanager.DTO.DashboardDTO;
import com.nextgen.contactmanager.Model.Contact;
import com.nextgen.contactmanager.Model.MyUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DashboardService {
    private final UserService userService;

    public DashboardService(UserService userService) {
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public DashboardDTO getDashboard(String email) {
        MyUser user = userService.getByEmail(email);
        List<Contact> contacts = user.getContacts() == null ? List.of() : user.getContacts();
        long favorites = contacts.stream().filter(Contact::isFavorite).count();
        long family = count(contacts, "FAMILY");
        long friend = count(contacts, "FRIEND");
        long work = count(contacts, "WORK");
        long other = contacts.size() - family - friend - work;
        return new DashboardDTO(user.getFirstname(), user.getLastname(), user.getEmail(),
                contacts.size(), favorites, family, friend, work, other);
    }

    private long count(List<Contact> contacts, String category) {
        return contacts.stream().filter(c -> category.equalsIgnoreCase(c.getCategory())).count();
    }
}
