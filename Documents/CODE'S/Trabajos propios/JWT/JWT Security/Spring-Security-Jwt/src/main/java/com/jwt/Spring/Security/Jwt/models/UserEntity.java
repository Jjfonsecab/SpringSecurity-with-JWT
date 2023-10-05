package com.jwt.Spring.Security.Jwt.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

    private Long id;
    private String email;
    private String username;
    private String password;
}
