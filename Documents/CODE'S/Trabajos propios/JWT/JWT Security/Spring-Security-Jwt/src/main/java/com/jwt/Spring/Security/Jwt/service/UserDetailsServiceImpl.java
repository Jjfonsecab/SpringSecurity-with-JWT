package com.jwt.Spring.Security.Jwt.service;

import com.jwt.Spring.Security.Jwt.models.UserEntity;
import com.jwt.Spring.Security.Jwt.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;//Para que se consulte el usuario a la base de datos
    @Override//Este metodo spring lo ejecuta por debajo para asegurarse de cual va a ser el usuario a autenticar
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {//Este metodo es muy importante para decirle a spring de donde va a sacar los usuarios

        UserEntity userEntity = userRepository.findByUsername(username)//Estamos recuperando el usuario de la base de datos
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe"));

        //Tenemos que crear un objeto del tipo user con el cual nos vamos a identificar, esto es para que spring security sepa cual esel usuasrio
        //Haciendo la conversion a GrantedAuthorities
        //Recibimos el rol y creamos una instancia de SimpledGranted authority que implementa la interfaz granted authority
        Collection<? extends GrantedAuthority> authorities = userEntity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_".concat(role.getName().name()))) //Con esto estamos generando un SimpledGranted authority , la autorizacion que neceita spring, es muy importante poner el ROLE
                .collect(Collectors.toSet());//Lo convertimos a una lista pero manejandolo como un set para evitar los duplicados

        return new User(userEntity.getUsername(),//aca retornamos un user pero de spring security
                userEntity.getPassword(),
                true,//boolean para usuario activo
                true,//boolean para expiracion de cuenta
                true,//boolean para expiracion de credenciales
                true,//boolean para cuenta bloqueada
                authorities);//permisos del usuario
        //le hemos definido a spring que el usuario que tiene que buscar lo debe de buscar en la base de datos
    }
}
