package com.covoiturage.model;

public class Admin extends User {

    public Admin(String identifiant, String nom, String email, String password) {
        super(identifiant, nom, email, password, UserRole.ADMIN);
    }
}
