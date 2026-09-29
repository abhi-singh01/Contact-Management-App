package com.nextgen.contactmanager.DTO;

public class DashboardDTO {
    private String firstname;
    private String lastname;
    private String email;
    private long totalContacts;
    private long favoriteContacts;
    private long familyContacts;
    private long friendContacts;
    private long workContacts;
    private long otherContacts;

    public DashboardDTO(String firstname, String lastname, String email, long totalContacts,
                        long favoriteContacts, long familyContacts, long friendContacts,
                        long workContacts, long otherContacts) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.totalContacts = totalContacts;
        this.favoriteContacts = favoriteContacts;
        this.familyContacts = familyContacts;
        this.friendContacts = friendContacts;
        this.workContacts = workContacts;
        this.otherContacts = otherContacts;
    }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public String getEmail() { return email; }
    public long getTotalContacts() { return totalContacts; }
    public long getFavoriteContacts() { return favoriteContacts; }
    public long getFamilyContacts() { return familyContacts; }
    public long getFriendContacts() { return friendContacts; }
    public long getWorkContacts() { return workContacts; }
    public long getOtherContacts() { return otherContacts; }
}
