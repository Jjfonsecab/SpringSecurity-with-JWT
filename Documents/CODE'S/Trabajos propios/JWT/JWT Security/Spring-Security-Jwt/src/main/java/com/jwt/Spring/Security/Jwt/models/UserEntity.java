package com.jwt.Spring.Security.Jwt.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(max = 30)
    private String username;

    @NotBlank
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, //Trae todos de una vez el LAZY trae uno por uno segun como sean necesarios
            targetEntity = RoleEntity.class, //Con esta entidad se esta estableciendo la relacion
            cascade = CascadeType.PERSIST)//Cuando ingresemos un usuario a la base de datos se van a insertar inmediatamente los roles, pero al eliminarse el usuario solo se elimina el usuario y no el rol por eso el PERSIST
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))//Foreign-key, tabla intermedia generada entre las tablas user y roles, tambien se define el nombre de las claves
    private Set<RoleEntity> roles;//Con el set evitamos tener elementos duplicados

}
