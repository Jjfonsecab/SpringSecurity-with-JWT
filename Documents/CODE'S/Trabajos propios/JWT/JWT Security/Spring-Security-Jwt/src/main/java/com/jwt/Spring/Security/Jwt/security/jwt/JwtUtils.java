package com.jwt.Spring.Security.Jwt.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j // permite escribir registros en sus aplicaciones sin acoplar el código
public class JwtUtils {
    //Esta clase provee los elementos necesarios para poder trabajar con el token
    @Value("${jwt.secret.key}")//modo de acceso a la llave que esta en propperties
    private String secretKey;//Va a firmar el metodo, sirve para constatar la autenticacion
    @Value("${jwt.time.expiration}")
    private String timeExpiration;//tiempo de valides para el token

    //Metodo encargado de la creacion de token de acceso
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)//enviamos el usuario al que se le va a generar el token
                .setIssuedAt(new Date(System.currentTimeMillis()))//Fecha de creacion del token
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)))//Fecha de expiracion para el token
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256) //Firma del metodo y el algoritmo de enriptacion de esta, este se definio en el metodo de abajo
                .compact(); //Aca ya se genero el token
    }

    //Validando el token de acceso
    public boolean isTokenValid(String token) {//aca enviamos el token a validar
        try {
            Jwts.parserBuilder() //el parser builder leera el token codificado anterirmente
                    .setSigningKey(getSignatureKey())//Lo primero a verificar es que la firma sea correcta, de lo contrario el token sera invalido
                    .build()
                    .parseClaimsJws(token)
                    .getBody();//Si se arroja una excepcion el token no es valido
            return true;
        }catch (Exception e) {
            log.error("Token invalido, error : ".concat(e.getMessage()));//para esto sirve la anotacion de la clase
            return false;
        }
    }
    //Obtener username del token
    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);//Le decimos el metodo a usar para el retorno con el ::getSubject
    }

    //Metodo para obtener un solo Claim
    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction) {// con el generico <T> podremos retornar cualquier cosa
        Claims claims = extractAllClaims(token);//primero traemos la lista de los claims para poder devolver uno solo
        return claimsTFunction.apply(claims);
    }

    //Metodo para obtener las caracteristicas de todos los tokens, sus claims.
    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    //Metodo para Obtener firma del Token
    public Key getSignatureKey() {
        //Decodificamos la clave para despues encriptarla a otro algoritmo que nos permitira firmar nuestro token
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);//Enviamos el array de bytes
    }

}
