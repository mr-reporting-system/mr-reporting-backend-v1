package com.mrreporting.backend.controller;

import com.mrreporting.backend.entity.Contact;
import com.mrreporting.backend.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = "http://localhost:5173")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping("/submit")
    public String submitContact(@RequestBody Contact contact) {
        // Pass the form data to the service layer to be saved
        return contactService.saveContact(contact);
    }
}