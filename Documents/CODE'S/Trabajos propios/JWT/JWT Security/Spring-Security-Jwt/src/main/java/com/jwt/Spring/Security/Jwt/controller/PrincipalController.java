package com.jwt.Spring.Security.Jwt.controller;

import com.jwt.Spring.Security.Jwt.models.ERole;
import com.jwt.Spring.Security.Jwt.models.RoleEntity;
import com.jwt.Spring.Security.Jwt.models.UserEntity;
import com.jwt.Spring.Security.Jwt.repositories.UserRepository;
import com.jwt.Spring.Security.Jwt.controller.request.CreateUserDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class PrincipalController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;// llamamos el repositorio para poder guardar
    @GetMapping("/hello")
    public String hello(){
        return "Hello World no secured";
    }
    @GetMapping("/helloSecured")
    public String helloSecured(){
        return "Hello World secured";
    }
    @PostMapping("/createUser")//end point para crear un usuario
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDTO createUserDTO){
        //Recuperando los roles
        Set<RoleEntity>roles = createUserDTO.getRoles().stream()//necesitamos convertir el role
                .map(role -> RoleEntity.builder()// Recibimos un set de string con los nombres de nuestros roles y lo convertimos en un set role entity para insertar en la base de datos
                        .name(ERole.valueOf(role))
                        .build())
                .collect(Collectors.toSet());

        UserEntity userEntity = UserEntity.builder()    //Construyendo el objeto por partes
                .username(createUserDTO.getUsername())
              //.password(createUserDTO.getPassword()) //Configuracion usada para el password predefinido
                .password(passwordEncoder.encode(createUserDTO.getPassword()))// Aca encriptamos el password y lo enviamos
                .email(createUserDTO.getEmail())
                .roles(roles)//enviamos los roles
                .build();

        userRepository.save(userEntity);

        return ResponseEntity.ok(userEntity);
    }
    @DeleteMapping("/deleteUser")
    public String deleteUser(@RequestParam String id){
        userRepository.deleteById(Long.parseLong(id));// Parseamos el String a long
        return "Eliminado el User con id: ".concat(id);
    }



}
