package com.jwt.Spring.Security.Jwt.repositories;

import com.jwt.Spring.Security.Jwt.models.RoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {



}
