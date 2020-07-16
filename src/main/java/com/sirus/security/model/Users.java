package com.sirus.security.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="Users", uniqueConstraints = @UniqueConstraint( columnNames = {"mobile"}))
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Users {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	@Column(length = 12)
	private String mobile;
	private String password;
}
