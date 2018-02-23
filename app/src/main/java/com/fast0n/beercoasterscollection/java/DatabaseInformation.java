package com.fast0n.beercoasterscollection.java;

public class DatabaseInformation {

    /**
     * Prende le informazioni dal database
     */

    private String name;
    private String email;
    private String username;
    private int status;

    public DatabaseInformation() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
