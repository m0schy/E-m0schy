package com.m0schy.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	private Long id;
	
	private String userId;
	private String firstName;
	private String lastName;
	private String email;
	private String username;
	private String password;
	private String profileImgUrl;
	private Date lastLoginDate;
	private Date lastLoginDateDiplay;
	private Date joinDate;
	private String role;// ROLE_USER{update, delete}, ROLE_ADMIN
	private String[] authorities;// Delete, Update, Create, Read
	private boolean isActive;
	private boolean isNotLocked;

}
