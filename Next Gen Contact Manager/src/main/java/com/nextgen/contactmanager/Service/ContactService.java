package com.nextgen.contactmanager.Service;
//Purpose: Core business logic for managing contacts.
import com.nextgen.contactmanager.DTO.ContactDTO;
import com.nextgen.contactmanager.Model.Contact;
import com.nextgen.contactmanager.Model.MyUser;
import com.nextgen.contactmanager.Repository.ContactRepository;
import com.nextgen.contactmanager.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.nextgen.contactmanager.Exception.ContactNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.springframework.web.multipart.MultipartFile;
import java.util.stream.Collectors;

@Service
public class ContactService {
    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

    @Autowired //injecting dependencies ,no need for manual configuration.
     private ContactRepository contactRepository;

    @Autowired
    private UserRepository myUserRepository;

    @Transactional    /*manages transactions and defines their scope
                    When a method is annotated with @Transactional, it will be executed within the context of a transaction.
                    If the transaction is successful, the changes made to the database will be committed.
                    If the transaction fails, all changes will be rolled back     */
    public List<ContactDTO> getContactsForUser(String userEmail) { //Fetch contacts for a specific user.
        MyUser user = myUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));

        // Force the contacts collection to be loaded (initialize the lazy-loaded collection)
        user.getContacts().size();  // This ensures contacts are loaded before returning

        return user.getContacts().stream()
                .map(this::convertToContactDTO)  // Map each Contact to ContactDTO
                .collect(Collectors.toList());
    }

    // Map Contact to ContactDTO
    private ContactDTO convertToContactDTO(Contact contact) {
        return new ContactDTO(contact.getContactId(), contact.getName(), contact.getPhoneNumber(), contact.getEmailId(), contact.isFavorite(), contact.getCategory());
    }

    // Add a new contact
    public Contact addContact(Contact contact) {
        try {
            return contactRepository.save(contact);
        } catch (Exception e) {
            logger.error("Error saving contact: {}", contact, e);
            throw new RuntimeException("Error saving contact.");
        }
    }

    @Transactional
    public List<ContactDTO> advancedSearch(String keyword, String searchType, String userEmail) {
        myUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));

        if (keyword == null) {
            keyword = "";
        }

        if (searchType == null || searchType.isBlank()) {
            searchType = "ALL";
        }

        searchType = searchType.toUpperCase();

        List<Contact> contacts = contactRepository.advancedSearch(userEmail, keyword.trim(), searchType);
        return contacts.stream()
                .map(this::convertToContactDTO)
                .collect(Collectors.toList());
    }

    // Add a contact for a specific user
    public ContactDTO addContactForUser(String userEmail, Contact contact) {
        try {
            MyUser user = myUserRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));
            contact.setUser(user);  // Associate the user with the contact
            contact.setCategory(normalizeCategory(contact.getCategory()));
            Contact savedContact = contactRepository.save(contact);
            return convertToContactDTO(savedContact);  // Return the saved contact as DTO
        } catch (Exception e) {
            logger.error("Error adding contact for user: {}", userEmail, e);
            throw new RuntimeException("Error adding contact for user.");
        }
    }
    @Transactional
    public ContactDTO updateContact(Contact contact) {
        String userEmail = contact.getEmail();
        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException("User email is required");
        }

        Contact existingContact = contactRepository.findByContactIdAndUserEmail(contact.getContactId(), userEmail)
                .orElseThrow(() -> new ContactNotFoundException("Contact not found for this user"));

        existingContact.setName(contact.getName());
        existingContact.setPhoneNumber(contact.getPhoneNumber());
        existingContact.setEmailId(contact.getEmailId());
        existingContact.setCategory(normalizeCategory(contact.getCategory()));

        Contact updatedContact = contactRepository.save(existingContact);
        return convertToContactDTO(updatedContact);
    }

    @Transactional
    public ContactDTO toggleFavorite(Long contactId, String userEmail) {
        Contact contact = contactRepository.findByContactIdAndUserEmail(contactId, userEmail)
                .orElseThrow(() -> new ContactNotFoundException("Contact not found"));

        contact.setFavorite(!contact.isFavorite());
        Contact updatedContact = contactRepository.save(contact);
        return convertToContactDTO(updatedContact);
    }

    @Transactional
    public List<ContactDTO> getFavoriteContacts(String userEmail) {
        List<Contact> contacts = contactRepository.findByUserEmailAndFavoriteTrue(userEmail);
        return contacts.stream()
                .map(this::convertToContactDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public List<ContactDTO> getContactsByCategory(String userEmail, String category) {
        myUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));
        if (category == null || category.isBlank() || category.equalsIgnoreCase("ALL")) {
            return getContactsForUser(userEmail);
        }
        return contactRepository.findByUserEmailAndCategoryIgnoreCase(userEmail, category.trim())
                .stream().map(this::convertToContactDTO).collect(Collectors.toList());
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) return "OTHER";
        String value = category.trim().toUpperCase();
        if (!value.equals("FAMILY") && !value.equals("FRIEND") && !value.equals("WORK") && !value.equals("OTHER")) {
            return "OTHER";
        }
        return value;
    }

    @Transactional
    public Map<String, Object> importContactsFromCsv(String userEmail, MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        int imported = 0;
        int skipped = 0;
        List<String> errors = new java.util.ArrayList<>();

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a CSV file.");
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("CSV file must be smaller than 2 MB.");
        }

        MyUser user = myUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));

        Set<String> existingPhones = new HashSet<>();
        Set<String> existingEmails = new HashSet<>();
        for (Contact contact : contactRepository.findByUserEmail(userEmail)) {
            existingPhones.add(normalizeValue(contact.getPhoneNumber()));
            existingEmails.add(normalizeValue(contact.getEmailId()));
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                throw new IllegalArgumentException("CSV file is empty.");
            }

            List<String> headers = parseCsvLine(headerLine);
            if (headers.size() < 3 || !headers.get(0).replace("\uFEFF", "").trim().equalsIgnoreCase("Name")
                    || !headers.get(1).trim().equalsIgnoreCase("Phone Number")
                    || !headers.get(2).trim().equalsIgnoreCase("Email")) {
                throw new IllegalArgumentException(
                        "Invalid CSV format. Use the template: Name,Phone Number,Email,Category,Favorite");
            }

            String line;
            int rowNumber = 1;
            while ((line = reader.readLine()) != null) {
                rowNumber++;
                if (line.isBlank()) continue;

                List<String> values = parseCsvLine(line);
                String name = getCsvValue(values, 0);
                String phone = getCsvValue(values, 1);
                String email = getCsvValue(values, 2);
                String category = getCsvValue(values, 3);
                String favoriteValue = getCsvValue(values, 4);

                if (name.isBlank() || phone.isBlank() || email.isBlank()) {
                    skipped++;
                    errors.add("Row " + rowNumber + ": Name, Phone Number and Email are required.");
                    continue;
                }

                if (!email.matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                    skipped++;
                    errors.add("Row " + rowNumber + ": Invalid email address.");
                    continue;
                }

                String phoneKey = normalizeValue(phone);
                String emailKey = normalizeValue(email);
                if (existingPhones.contains(phoneKey) || existingEmails.contains(emailKey)) {
                    skipped++;
                    errors.add("Row " + rowNumber + ": Contact already exists (phone or email).");
                    continue;
                }

                Contact contact = new Contact();
                contact.setName(name.trim());
                contact.setPhoneNumber(phone.trim());
                contact.setEmailId(email.trim());
                contact.setCategory(normalizeCategory(category));
                contact.setFavorite("true".equalsIgnoreCase(favoriteValue) || "yes".equalsIgnoreCase(favoriteValue));
                contact.setUser(user);
                contactRepository.save(contact);

                existingPhones.add(phoneKey);
                existingEmails.add(emailKey);
                imported++;
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error importing contacts for user: {}", userEmail, e);
            throw new RuntimeException("Could not read the CSV file.");
        }

        result.put("imported", imported);
        result.put("skipped", skipped);
        result.put("errors", errors);
        return result;
    }

    private String normalizeValue(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private String getCsvValue(List<String> values, int index) {
        return index < values.size() && values.get(index) != null ? values.get(index).trim() : "";
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (ch == ',' && !quoted) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        values.add(current.toString());
        return values;
    }

    public String exportContactsAsCsv(String userEmail) {
        List<Contact> contacts = contactRepository.findByUserEmail(userEmail);
        StringBuilder csv = new StringBuilder();
        csv.append("Name,Phone Number,Email,Category,Favorite\n");
        for (Contact contact : contacts) {
            csv.append(csvValue(contact.getName())).append(',')
               .append(csvValue(contact.getPhoneNumber())).append(',')
               .append(csvValue(contact.getEmailId())).append(',')
               .append(csvValue(contact.getCategory())).append(',')
               .append(contact.isFavorite()).append('\n');
        }
        return csv.toString();
    }

    private String csvValue(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    public void deleteContact(Long id, String userEmail) {
        // Ensure the contact belongs to the user before deleting
        Contact contact = contactRepository.findByContactIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ContactNotFoundException("Contact not found"));
        contactRepository.delete(contact);
    }

}
