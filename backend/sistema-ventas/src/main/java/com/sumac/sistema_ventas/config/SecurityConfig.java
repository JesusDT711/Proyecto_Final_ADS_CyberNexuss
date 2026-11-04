
package com.sumac.sistema_ventas.config;

import com.sumac.sistema_ventas.service.UsuarioDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public AuthenticationProvider authenticationProvider(
            UsuarioDetailsService usuarioDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(usuarioDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authenticationProvider(authenticationProvider)
            .authorizeHttpRequests(auth -> auth

                // Solo el administrador gestiona usuarios y roles.
                .requestMatchers(
                    "/api/usuarios/**",
                    "/api/roles/**"
                ).hasRole("ADMIN")

                // Solo el administrador gestiona proveedores.
                .requestMatchers(
                    "/api/proveedores/**",
                    "/api/solicitudes-abastecimiento/**",
                    "/api/detalles-abastecimiento/**"
                ).hasRole("ADMIN")

                // Administrador y empleado pueden consultar y gestionar
                // estas áreas por ahora; luego afinaremos cada operación.
                .requestMatchers(
                    "/api/clientes/**",
                    "/api/productos/**",
                    "/api/pedidos/**",
                    "/api/detalles-pedido/**",
                    "/api/movimientos-inventario/**",
                    "/api/entregas/**",
                    "/api/alertas-stock/**",
                    "/api/reportes/**"
                ).hasAnyRole("ADMIN", "EMPLEADO")

                // Cualquier ruta no contemplada requiere autenticación.
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
