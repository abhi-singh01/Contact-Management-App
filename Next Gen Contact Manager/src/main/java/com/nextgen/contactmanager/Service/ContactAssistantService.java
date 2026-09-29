package com.nextgen.contactmanager.Service;

import com.nextgen.contactmanager.DTO.AssistantResponse;
import com.nextgen.contactmanager.DTO.ContactDTO;
import com.nextgen.contactmanager.Model.Contact;
import com.nextgen.contactmanager.Model.MyUser;
import com.nextgen.contactmanager.Repository.ContactRepository;
import com.nextgen.contactmanager.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Local AI-style assistant for the contact manager.
 * It understands natural-language requests without requiring a paid AI API key.
 * All data is loaded only for the authenticated user.
 */
@Service
public class ContactAssistantService {
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public ContactAssistantService(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public AssistantResponse ask(String userEmail, String message) {
        if (message == null || message.isBlank()) {
            return new AssistantResponse(
                    "Ask me something like 'show my work contacts', 'what is Rahul's phone number?', or 'how many favorites do I have?'",
                    "HELP", List.of());
        }

        MyUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        List<Contact> contacts = contactRepository.findByUserEmail(userEmail);
        String query = normalize(message);

        if (isGreeting(query)) {
            return response("Hello " + safe(user.getFirstname(), "there") + "! I can search your contacts, find phone numbers or emails, show favorites/categories, and give you contact statistics.", "GREETING", List.of());
        }

        if (isHelp(query)) {
            return response("Try: 'show all contacts', 'show my work contacts', 'show favorites', 'how many contacts do I have?', 'what is Rahul's phone?', or 'find rahul@example.com'.", "HELP", List.of());
        }

        String category = detectCategory(query);
        if (category != null && isCategoryRequest(query)) {
            List<Contact> result = contacts.stream()
                    .filter(c -> category.equalsIgnoreCase(safe(c.getCategory(), "OTHER")))
                    .sorted(Comparator.comparing(c -> safe(c.getName(), ""), String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());
            return response("I found " + result.size() + " " + category.toLowerCase(Locale.ROOT) + " contact" + plural(result.size()) + ".", "CATEGORY", toDto(result));
        }

        if (containsAny(query, "favorite", "favourite", "starred")) {
            List<Contact> result = contacts.stream().filter(Contact::isFavorite)
                    .sorted(Comparator.comparing(c -> safe(c.getName(), ""), String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());
            return response("You have " + result.size() + " favorite contact" + plural(result.size()) + ".", "FAVORITES", toDto(result));
        }

        if (isCountRequest(query)) {
            if (containsAny(query, "family")) return countCategory(contacts, "FAMILY");
            if (containsAny(query, "friend", "friends")) return countCategory(contacts, "FRIEND");
            if (containsAny(query, "work", "office")) return countCategory(contacts, "WORK");
            if (containsAny(query, "other")) return countCategory(contacts, "OTHER");
            return response("You have " + contacts.size() + " contact" + plural(contacts.size()) + " in total.", "COUNT", List.of());
        }

        if (isShowAllRequest(query)) {
            List<Contact> result = contacts.stream()
                    .sorted(Comparator.comparing(c -> safe(c.getName(), ""), String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());
            return response("Here are your " + result.size() + " contact" + plural(result.size()) + ".", "SHOW_ALL", toDto(result));
        }

        String requestedField = requestedField(query);
        Contact best = findBestContact(contacts, query);
        if (best != null) {
            ContactDTO dto = toDto(best);
            if ("PHONE".equals(requestedField)) {
                return response(best.getName() + "'s phone number is " + safe(best.getPhoneNumber(), "not available") + ".", "PHONE_LOOKUP", List.of(dto));
            }
            if ("EMAIL".equals(requestedField)) {
                return response(best.getName() + "'s email is " + safe(best.getEmailId(), "not available") + ".", "EMAIL_LOOKUP", List.of(dto));
            }
            if (containsAny(query, "details", "detail", "information", "info", "contact")) {
                return response(best.getName() + " is in your " + safe(best.getCategory(), "OTHER").toLowerCase(Locale.ROOT) + " category. Phone: " + safe(best.getPhoneNumber(), "not available") + ". Email: " + safe(best.getEmailId(), "not available") + ".", "CONTACT_DETAILS", List.of(dto));
            }
        }

        String keyword = extractSearchKeyword(query);
        if (!keyword.isBlank()) {
            List<Contact> matches = contacts.stream()
                    .filter(c -> containsNormalized(c.getName(), keyword) || containsNormalized(c.getPhoneNumber(), keyword) || containsNormalized(c.getEmailId(), keyword))
                    .sorted(Comparator.comparing(c -> safe(c.getName(), ""), String.CASE_INSENSITIVE_ORDER))
                    .limit(20)
                    .collect(Collectors.toList());
            if (!matches.isEmpty()) {
                return response("I found " + matches.size() + " matching contact" + plural(matches.size()) + ".", "SEARCH", toDto(matches));
            }
        }

        return response("I couldn't find a matching contact or understand that request. Try 'show my work contacts', 'what is Rahul's phone?', 'show favorites', or 'how many contacts do I have?'.", "UNKNOWN", List.of());
    }

    private AssistantResponse countCategory(List<Contact> contacts, String category) {
        long count = contacts.stream().filter(c -> category.equalsIgnoreCase(safe(c.getCategory(), "OTHER"))).count();
        return response("You have " + count + " " + category.toLowerCase(Locale.ROOT) + " contact" + plural(count) + ".", "CATEGORY_COUNT", List.of());
    }

    private Contact findBestContact(List<Contact> contacts, String query) {
        String cleaned = query.replace("what is", " ").replace("what's", " ").replace("who is", " ")
                .replace("phone number", " ").replace("phone", " ").replace("mobile number", " ")
                .replace("mobile", " ").replace("email address", " ").replace("email", " ")
                .replace("number of", " ").replace("details of", " ").replace("details for", " ")
                .replace("contact details", " ").replace("contact", " ").replace("for", " ").replace("of", " ")
                .replace("please", " ").replace("find", " ").replace("search", " ").replace("lookup", " ")
                .trim();
        Contact exact = null;
        int bestScore = 0;
        for (Contact contact : contacts) {
            String name = normalize(contact.getName());
            if (name.isBlank()) continue;
            int score = 0;
            if (cleaned.contains(name)) score = 100;
            else {
                String[] words = name.split(" ");
                for (String word : words) if (word.length() > 2 && cleaned.contains(word)) score += 20;
            }
            if (score > bestScore) { bestScore = score; exact = contact; }
        }
        return bestScore >= 20 ? exact : null;
    }

    private String requestedField(String query) {
        if (containsAny(query, "phone", "mobile", "number", "call", "dial")) return "PHONE";
        if (containsAny(query, "email", "mail")) return "EMAIL";
        return "DETAILS";
    }

    private boolean isCountRequest(String q) {
        return containsAny(q, "how many", "count", "total", "number of contacts", "how much contacts");
    }

    private boolean isShowAllRequest(String q) {
        return containsAny(q, "show all", "list all", "all my contacts", "my contacts", "display all contacts", "show contacts");
    }

    private boolean isCategoryRequest(String q) {
        return containsAny(q, "show", "list", "display", "find", "get", "contacts", "people", "members") || isCountRequest(q);
    }

    private boolean isGreeting(String q) { return containsAny(q, "hello", "hi", "hey", "good morning", "good afternoon", "good evening"); }
    private boolean isHelp(String q) { return containsAny(q, "help", "what can you do", "commands", "options"); }

    private String detectCategory(String q) {
        if (containsAny(q, "family", "families", "relative", "relatives")) return "FAMILY";
        if (containsAny(q, "friend", "friends")) return "FRIEND";
        if (containsAny(q, "work", "office", "colleague", "colleagues")) return "WORK";
        if (containsAny(q, "other")) return "OTHER";
        return null;
    }

    private String extractSearchKeyword(String q) {
        String cleaned = q.replaceAll("[^a-z0-9@. +_-]", " ").trim();
        String[] stop = {"find", "search", "look", "lookup", "show", "get", "contact", "contacts", "person", "people", "for", "me", "please", "the", "my", "details", "information"};
        for (String s : stop) cleaned = cleaned.replaceAll("\\b" + s + "\\b", " ");
        return cleaned.trim();
    }

    private boolean containsNormalized(String value, String keyword) { return normalize(value).contains(normalize(keyword)); }
    private boolean containsAny(String text, String... values) { for (String value : values) if (text.contains(value)) return true; return false; }
    private String normalize(String value) { return value == null ? "" : value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9@.]+", " ").trim(); }
    private String safe(String value, String fallback) { return value == null || value.isBlank() ? fallback : value; }
    private String plural(long count) { return count == 1 ? "" : "s"; }

    private AssistantResponse response(String reply, String intent, List<ContactDTO> contacts) {
        return new AssistantResponse(reply, intent, contacts);
    }

    private List<ContactDTO> toDto(List<Contact> contacts) {
        List<ContactDTO> result = new ArrayList<>();
        for (Contact c : contacts) result.add(new ContactDTO(c.getContactId(), c.getName(), c.getPhoneNumber(), c.getEmailId(), c.isFavorite(), c.getCategory()));
        return result;
    }

    private ContactDTO toDto(Contact c) {
        return new ContactDTO(c.getContactId(), c.getName(), c.getPhoneNumber(), c.getEmailId(), c.isFavorite(), c.getCategory());
    }
}
