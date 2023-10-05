package com.jwt.Spring.Security.Jwt.repositories;

import com.jwt.Spring.Security.Jwt.models.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    //Metodo especial para poder buscar un usuario por el username
    Optional<UserEntity> findByUsername(String username); //Se trae un usuario y lo retorna como una entidad


    //Metodo Opcional en caso de que no se use el nombre predefinido findBy
    @Query("select u from UserEntity u where u.username = ?1")//consulta
    Optional<UserEntity> getName(String username);//firma del metodo


}


