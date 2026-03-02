package com.mrreporting.backend.service;

import com.mrreporting.backend.entity.Contact;
import com.mrreporting.backend.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public String saveContact(Contact contact) {
        contactRepository.save(contact);
        return "Success! We will contact you soon.";
    }
}