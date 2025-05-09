package com.oncf.gare_app.exception;

public class SectionNotFoundException extends RuntimeException {

    public SectionNotFoundException(Long id) {
        super("Section introuvable avec l'ID: " + id);
    }

    public SectionNotFoundException(String nom) {
        super("Section introuvable avec le nom: " + nom);
    }
}