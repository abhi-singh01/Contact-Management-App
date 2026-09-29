package com.nextgen.contactmanager.Controller;

import com.nextgen.contactmanager.DTO.ContactDTO;
import com.nextgen.contactmanager.Service.ContactService;
import com.nextgen.contactmanager.Model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.security.Principal;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {
    @Autowired private ContactService contactService;

    @GetMapping("/user/{email}")
    public ResponseEntity<List<ContactDTO>> getContactsForUser(@PathVariable String email) {
        try { return ResponseEntity.ok(contactService.getContactsForUser(email)); }
        catch (Exception e) { return ResponseEntity.status(500).body(null); }
    }

    @PostMapping("/add")
    public ResponseEntity<ContactDTO> addContact(@RequestBody Contact contact) {
        try { return ResponseEntity.ok(contactService.addContactForUser(contact.getUser().getEmail(), contact)); }
        catch (Exception e) { return ResponseEntity.status(500).body(null); }
    }

    @GetMapping("/search")
    public ResponseEntity<List<ContactDTO>> advancedSearch(@RequestParam String keyword,
                                                             @RequestParam String searchType,
                                                             @RequestParam String userEmail) {
        try { return ResponseEntity.ok(contactService.advancedSearch(keyword, searchType, userEmail)); }
        catch (Exception e) { return ResponseEntity.status(500).body(null); }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ContactDTO>> getContactsByCategory(@PathVariable String category,
                                                                    @RequestParam String userEmail) {
        try { return ResponseEntity.ok(contactService.getContactsByCategory(userEmail, category)); }
        catch (Exception e) { return ResponseEntity.status(500).body(null); }
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importContacts(
            @RequestParam("file") MultipartFile file, Principal principal) {
        try {
            String userEmail = principal.getName();
            return ResponseEntity.ok(contactService.importContactsFromCsv(userEmail, file));
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new java.util.HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new java.util.HashMap<>();
            error.put("message", "Unable to import contacts.");
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportContacts(@RequestParam String userEmail) {
        try {
            byte[] csv = contactService.exportContactsAsCsv(userEmail).getBytes(StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contacts.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(csv);
        } catch (Exception e) { return ResponseEntity.status(500).body(null); }
    }

    @PutMapping("/update")
    public ResponseEntity<ContactDTO> updateContact(@RequestBody Contact contact) {
        try { return ResponseEntity.ok(contactService.updateContact(contact)); }
        catch (Exception e) { return ResponseEntity.status(500).body(null); }
    }

    @PostMapping("/favorite/{id}")
    public ResponseEntity<ContactDTO> toggleFavorite(@PathVariable Long id, @RequestParam String userEmail) {
        try { return ResponseEntity.ok(contactService.toggleFavorite(id, userEmail)); }
        catch (Exception e) { return ResponseEntity.status(500).body(null); }
    }

    @GetMapping("/favorites/{email}")
    public ResponseEntity<List<ContactDTO>> getFavoriteContacts(@PathVariable String email) {
        try { return ResponseEntity.ok(contactService.getFavoriteContacts(email)); }
        catch (Exception e) { return ResponseEntity.status(500).body(null); }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id, @RequestParam String userEmail) {
        try { contactService.deleteContact(id, userEmail); return ResponseEntity.noContent().build(); }
        catch (Exception e) { return ResponseEntity.status(500).build(); }
    }
}
