package com.shopme.admin.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "email")
    @Pattern(message = "Email pattern must be valid",
            regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
    @Size(min = 8, max = 128, message = "Email should be 8-128 characters")
    @NotNull
    private String email;

    @Column(name = "enabled")
    private int enabled;

    @NotNull(message = "Firstname is required")
    @NotEmpty(message = "Firstname is empty")
    @NotBlank(message = "Firstname is blank")
    @Size(min = 2, max = 45, message = "Firstname should be 2-45 characters")
    @Column(name = "first_name")
    private String firstName;

    @NotNull(message = "Lastname is required")
    @NotEmpty(message = "Lastname is empty")
    @NotBlank(message = "Lastname is blank")
    @Size(min = 2, max = 45, message = "Lastname should be 2-45 characters")
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password")
    @NotNull(message = "Password is required")
    @NotEmpty(message = "Password is empty")
    @NotBlank(message = "Password is blank")
    private String password;

    @Column(name = "photos", columnDefinition = "MediumBlob")
    private byte[] photos;

    @Column(name = "filename")
    @Size(max = 64)
    private String filename;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

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

    public void enable() {
        setEnabled(1);
    }
    public void disable() {
        setEnabled(0);
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Set<Role> getRoles() { return roles; }

    public User id(int id) {
        this.id = id;
        return this;
    }

    public User email(String email) {
        this.email = email;
        return this;
    }

    public boolean isEnabled() {
        return enabled == 1;
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

    public User filename(String filename) {
        this.filename = filename;
        return this;
    }

    public User addRole(Role role) {
        roles.add(role);
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", filename='" + filename + '\'' +
                ", roles=" + roles +
                '}';
    }
}
