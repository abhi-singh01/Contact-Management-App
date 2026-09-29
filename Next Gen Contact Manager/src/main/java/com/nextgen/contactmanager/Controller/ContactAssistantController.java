package com.nextgen.contactmanager.Controller;

import com.nextgen.contactmanager.DTO.AssistantRequest;
import com.nextgen.contactmanager.DTO.AssistantResponse;
import com.nextgen.contactmanager.Service.ContactAssistantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/assistant")
public class ContactAssistantController {
    private final ContactAssistantService assistantService;

    public ContactAssistantController(ContactAssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping("/ask")
    public ResponseEntity<AssistantResponse> ask(@RequestBody AssistantRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(assistantService.ask(principal.getName(), request.getMessage()));
    }
}
