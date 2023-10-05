package com.jwt.Spring.Security.Jwt.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


public class TestRolesController {
    @GetMapping("/accesAdmin")
    @PreAuthorize("hasRole('ROLE ADMIN')")//definimos el rol de admin, esto se activo con la anotacion @EnableGlobalMethodSecurity(prePostEnabled = true) en SecurityConfig
    public String accessAdmin() {
        return "Hola, has accedido con el rol de ADMIN";
    }

    @GetMapping("/accesUser")
    @PreAuthorize("hasRole('ROLE_USER')")//Existen otras opciones aparte del hasRole, buscar
    public String accessUser() {
        return "Hola, has accedido con el rol de USER";
    }

    @GetMapping("/accesInvited")
    @PreAuthorize("hasRole('ROLE_INVITED')")
    public String accessInvited() {
        return "Hola, has accedido con el rol de INVITED";
    }

}
