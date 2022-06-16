package com.shopme.shopmecommon.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;
	
	@NotBlank(message = "Please enter an email address.")
	@Email(message = "Please enter a valid email.")
	@Size(min = 1, max = 128)
	@Column(name = "email", unique = true, length  = 128)
	private String email;
	
	@Column(name = "enabled")
	private Boolean enabled;
	
	@NotBlank(message = "Please enter first name.")
	@Size(min = 1, max = 45)
	@Column(name = "first_name", length  = 45)
	private String firstName;
	
	@NotBlank(message = "Please enter last name.")
	@Size(min = 1, max = 45)
	@Column(name = "last_name", length  = 45)
	private String lastName;
	
	@Size(min = 1, max = 64)
	@Column(name = "password", length  = 128)
	private String password;
	
	@Size(max = 64)
	@Column(name = "photos", length  = 128)
	private String photos;

	@Column(name = "insert_date")
	private LocalDate joinDate;
	
	@ManyToMany(cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.PERSIST},
			fetch = FetchType.EAGER)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private List<Role> roles;
	
	public User() {
	}

	public User(String email, Boolean enabled, String firstName, String lastName, String password, String photos, LocalDate joinDate) {
		this.email = email;
		this.enabled = enabled;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.photos = photos;
		this.joinDate = joinDate;
	}
	
	public User(int id, String email, Boolean enabled, String firstName, String lastName, String password, String photos, LocalDate joinDate, List<Role> roles) {
		this.email = email;
		this.enabled = enabled;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.photos = photos;
		this.joinDate = joinDate;
		this.roles = roles;
	}

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

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
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

	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	public LocalDate getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(LocalDate joinDate) {
		this.joinDate = joinDate;
	}

	@Transient
	public String getPhotosImagePath() {
		if(photos == null) return null;
		
		return "/images/user-photos/" + id + "/" + photos;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", enabled=" + enabled + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", password=" + password + ", photos=" + photos + "]";
	}
	
}
