package com.jwt.Spring.Security.Jwt.security;

import com.jwt.Spring.Security.Jwt.security.filters.JwtAuthenticationFilter;
import com.jwt.Spring.Security.Jwt.security.filters.JwtAuthorizationFilter;
import com.jwt.Spring.Security.Jwt.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)//vamos a habilitar las anotaciones de spring security para los controladores
public class SecurityConfig {
    @Autowired
    JwtUtils jwtUtils;//este es para inyectarlo en el JwtAuthenticationFilter
    @Autowired
    UserDetailsService userDetailsService;//inyectamos la implementacion
    @Autowired
    JwtAuthorizationFilter authorizationFilter;//inyectamos el filtro


    //Metodo para configurar la cadena de filtros, la seguridad.
    @Bean//Configura la seguridad de la aplicacion
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils);//nuestro filtro, lo traemos porque no tiene el @Bean
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);//seteando el authenticationManager, como un atributo, esta definido en la firma del metodo
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");//esta ruta no es necesario porque por defecto ya esta mapeado le login pero si se necesita una ruta diferente es aca donde se puede configurar

        return httpSecurity
                .csrf(config -> config.disable())
                .authorizeHttpRequests(auth ->{     //Configura el acceso a las url y a los endpoints
                    auth.requestMatchers("/hello","/login").permitAll();    //Se esta brindando acceso general al endpoint
                    //auth.requestMatchers("/accesAdmin").hasAnyRole("ADMIN","USER");//Le definimos al admin el acceso a los roles, el problema es que en caso de tener muchas url debemos configurar de la misma manera para cada una
                    auth.requestMatchers("/accesAdmin").hasRole("ADMIN"); // Solo usuarios con el rol "ADMIN"
                    auth.requestMatchers("/accesUser").hasRole("USER");   // Solo usuarios con el rol "USER"
                    auth.requestMatchers("/accesInvited").hasRole("INVITED");
                    auth.anyRequest().authenticated();//Cualquier otro endpoint debe de estar autenticado el usuario apra el acceso
                })//A continuacion la administracion de la sesion
                .sessionManagement(session ->{
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);// Cada solicitud se procesara de forma independiente y no se almacenaran datos de sesion en el servidor
                })//A continuacion configuramos una autenticacion basica
                /*.httpBasic()//autenticacion basica con un usuario en memoria
                .and()*///Removemos estas dos lineas porque ya no son necesarias, estas funcionaban para el usuario por defecto
                .addFilter(jwtAuthenticationFilter)//agregamos el filtro para la autenticacion
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)//Aca especificamos que queremos que el jwtAuthorizationFilter se ejecute antes
                .build();
    }

    /*
    //Creando un usuario de acceso a la api
    @Bean
    UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();//Creamos un usuario en la memoria.
        manager.createUser(User.withUsername("Maria")
                .password("54321")
                .roles()
                .build());

        return manager;
    }*/
    //Necesitamos un password encoder porque spring security necesita que encriptemos las contraseñas
    @Bean//Politica de encriptacion de contraseñas
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();//definimos el algoritmo de encriptacion a usar
        //return NoOpPasswordEncoder.getInstance();//esta configuracion es para cuando aun no se maneja la encriptacion
    }

    //Creamos un Authentication manager, este sera el objeto encargado de la autenticacion de los usuarios
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder) throws Exception {
        return  httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)//exige una contraseña encriptada
                .and().build();
    }

    public static void main(String[] args){//con este metodo vamos a codigicar la clave que tenemos seteada
        System.out.println(new BCryptPasswordEncoder().encode("1234"));
    }

}
