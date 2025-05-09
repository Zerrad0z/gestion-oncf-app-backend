package com.oncf.gare_app.exception;

public class AntenneNotFoundException extends RuntimeException {

    public AntenneNotFoundException(Long id) {
        super("Antenne introuvable avec l'ID: " + id);
    }

    public AntenneNotFoundException(String nom, Long sectionId) {
        super("Antenne introuvable avec le nom: " + nom + " dans la section ID: " + sectionId);
    }
}