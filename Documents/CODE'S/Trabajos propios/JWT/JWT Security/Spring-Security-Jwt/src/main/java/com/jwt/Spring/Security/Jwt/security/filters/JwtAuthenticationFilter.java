package com.jwt.Spring.Security.Jwt.security.filters;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.Spring.Security.Jwt.models.UserEntity;
import com.jwt.Spring.Security.Jwt.security.jwt.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//No se define como un bean porque tenemos que enviarle varios argumentos y setearle varios atributos
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    //Autnticacion de tipo Post
    private JwtUtils jwtUtils;//Lo inyectamos con el constructor
    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }


    //Intentar autenticarse
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        //Necesitamos recuperar el usuario que intento autenticarse
        UserEntity userEntity = null;
        String username = "";
        String password = "";

        try {//Aca recuperamos el usuario, tomando lo parametros user y password para mapear a un UserEntity
            userEntity = new ObjectMapper().readValue(request.getInputStream(), UserEntity.class);//Aca vamos a tratar el JSON mediante un mapeo
            username = userEntity.getUsername();// tenemos el usernME
            password = userEntity.getPassword();
        }catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password); //Con este nos autenticamos en la aplicacion

        return getAuthenticationManager().authenticate(authenticationToken);//lista la autenticacion
    }

    //Autenticacion correcta, aca llega despues del Authentication
    @Override//Una vez que la autenticacion se ha dado de manera correcta, debemos generar el token
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user = (User) authResult.getPrincipal(); //Obtenemos el objeto que contiene todos los detalles del usuario, lo casteamos
        String username = user.getUsername();
        String token = jwtUtils.generateAccessToken(user.getUsername());//pide el usuario que va a generar el token, aca se da el acceso a los otros puntos
        //Vamos a responder con el token de acceso
        response.addHeader("Authorization", token);// el token lo enviamos en el header de la respuesta

        Map<String, Object> httpResponse = new HashMap<>();//Aca vamos a mapear la respuesta y la convertimos en un JSON
        httpResponse.put("token", token);//devolvemos el token
        httpResponse.put("Message", "Autenticacion Correcta");
        httpResponse.put("Username", user.getUsername());//En la respuesta vamos a enviar los 3 datos token message y username

        //Vamos a convertir el mapa a un JSON para ponerlo en la respuesta
        response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));//aca mandamos el mapa como un JSON ne la respuesta
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);//cual va a ser el contenido de la respuesta?
        response.getWriter().flush();//para garantizar que se escriba de forma correcta el token

        super.successfulAuthentication(request, response, chain, authResult);
    }
}
