package com.ms.resources.user.services.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

//Entity class which is an object used to store into the database
//It reflects the database table
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;
    private String name;
    @Column(unique = true, nullable=false)
    private String username;
    private String password;
    @Column(unique = true, nullable=false)
    private String email;
    @Column(unique = true, nullable=false)
    private String contact;
}
