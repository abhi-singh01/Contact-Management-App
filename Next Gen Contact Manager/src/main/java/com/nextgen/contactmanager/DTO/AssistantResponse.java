package com.nextgen.contactmanager.DTO;

import java.util.List;

public class AssistantResponse {
    private String reply;
    private String intent;
    private List<ContactDTO> contacts;

    public AssistantResponse() {
    }

    public AssistantResponse(String reply, String intent, List<ContactDTO> contacts) {
        this.reply = reply;
        this.intent = intent;
        this.contacts = contacts;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public List<ContactDTO> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactDTO> contacts) {
        this.contacts = contacts;
    }
}
