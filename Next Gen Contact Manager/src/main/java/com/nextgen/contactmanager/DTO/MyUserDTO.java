package com.nextgen.contactmanager.DTO;

import java.util.List;

public class MyUserDTO {
    private String firstname;
    private String lastname;
    private String email;
    private List<ContactDTO> contacts;

    // Constructor
    public MyUserDTO(String firstname, String lastname, String email, List<ContactDTO> contacts) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.contacts = contacts;
    }

    // Getters and Setters
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<ContactDTO> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactDTO> contacts) {
        this.contacts = contacts;
    }
}
