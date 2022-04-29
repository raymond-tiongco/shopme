package com.shopme.admin.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "email")
    private String email;

    @Column(name = "enabled")
    private int enabled;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password")
    private String password;

    @Column(name = "photos")
    private byte[] photos;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles = new ArrayList<>();

    public User() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getPhotos() {
        return photos;
    }

    public void setPhotos(byte[] photos) {
        this.photos = photos;
    }

    public List<Role> getRoles() { return roles; }

    public User email(String email) {
        this.email = email;
        return this;
    }

    public User enabled(int enabled) {
        this.enabled = enabled;
        return this;
    }

    public User firstName(String fname) {
        firstName = fname;
        return this;
    }

    public User lastName(String lname) {
        lastName = lname;
        return this;
    }

    public User password(String password) {
        this.password = password;
        return this;
    }
}
