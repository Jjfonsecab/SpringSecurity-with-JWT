package com.jwt.Spring.Security.Jwt;

import com.jwt.Spring.Security.Jwt.models.ERole;
import com.jwt.Spring.Security.Jwt.models.RoleEntity;
import com.jwt.Spring.Security.Jwt.models.UserEntity;
import com.jwt.Spring.Security.Jwt.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class SpringSecurityJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityJwtApplication.class, args);
	}

	//Usuarios seteados para cuando se levante la aplicacion

	@Autowired//Codificamos el password
	PasswordEncoder passwordEncoder;
	@Autowired
	UserRepository userRepository;//para insertar el usuario en la base de datos

	@Bean
	CommandLineRunner init(){
		return args -> {

			UserEntity userEntity = UserEntity.builder()
					.email("josefa@josefina.com")
					.username("ADMIN")
					.password(passwordEncoder.encode("1234"))
					.roles(Set.of(RoleEntity.builder()
							.name(ERole.valueOf(ERole.ADMIN.name()))
							.build()))
					.build();

			UserEntity userEntity2 = UserEntity.builder()
					.email("deyanira@deyanisbby.com")
					.username("USER")
					.password(passwordEncoder.encode("1234"))
					.roles(Set.of(RoleEntity.builder()
							.name(ERole.valueOf(ERole.USER.name()))
							.build()))
					.build();

			UserEntity userEntity3 = UserEntity.builder()
					.email("miguel@miguel.com")
					.username("INVITED")
					.password(passwordEncoder.encode("1234"))
					.roles(Set.of(RoleEntity.builder()
							.name(ERole.valueOf(ERole.INVITED.name()))
							.build()))
					.build();

			//Registro de los usuarios en la base de datos bby
			userRepository.save(userEntity);
			userRepository.save(userEntity2);
			userRepository.save(userEntity3);
		};
	}

}
