package com.nextgen.contactmanager.DTO;

public class ContactDTO {
    private Long contactId;
    private String name;
    private String phoneNumber;
    private String emailId;
    private boolean favorite;
    private String category;

    public ContactDTO(Long contactId, String name, String phoneNumber, String emailId,
                      boolean favorite, String category) {
        this.contactId = contactId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emailId = emailId;
        this.favorite = favorite;
        this.category = category;
    }
    public Long getContactId() { return contactId; }
    public void setContactId(Long contactId) { this.contactId = contactId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
