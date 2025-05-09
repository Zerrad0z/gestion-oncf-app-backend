package com.oncf.gare_app.exception;

public class AntenneAlreadyExistsException extends RuntimeException {

    public AntenneAlreadyExistsException(String nom, Long sectionId) {
        super("Une antenne avec le nom '" + nom + "' existe déjà dans la section ID: " + sectionId);
    }
}