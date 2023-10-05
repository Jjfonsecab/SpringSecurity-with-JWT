package com.jwt.Spring.Security.Jwt.security.filters;

import com.jwt.Spring.Security.Jwt.security.jwt.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    //Con esto estaremos obligados a enviar siempre el token de acceso para poder acceder a los recursos
    @Autowired
    private JwtUtils jwtUtils;//Con este validamos el token
    @Autowired
    private UserDetailsService userDetailsService;//Para consultar el usuario en la base de datos

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
    //Lo primero sera extraer el token de la peticion
        String tokenHeader = request.getHeader("Authorization");

        if(tokenHeader != null && tokenHeader.startsWith("Bearer ")){
            String token = tokenHeader.substring(7);//Con esto le quitamos el bearer: al token, podemos dejar solo el 7 y quitar el :  tokenHeader.length

            if (jwtUtils.isTokenValid(token)){//Validamos el token
                String username = jwtUtils.getUsernameFromToken(token);//recuperamos el usuario dentro del token
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);//recuperamos los detalles del usuario desde la base de datos
                //Una vez recuperados los permisos y la info del usuario, tenemos que autenticarnos
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
                //A este punto ya estamos autenticados, esto se guarda el el SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);//seteamos la autenticacion nueva del usuario especificando los roles de este

            }

        }
        filterChain.doFilter(request, response);//En caso de que no se ingrese al if anterior, este parametro continuara con el filtro de validacion en el core de spring security y al ver que no tiene token, denegara el acceso
    }
}
