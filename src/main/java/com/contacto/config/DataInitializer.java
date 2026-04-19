package com.contacto.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.contacto.model.Necesidad;
import com.contacto.model.User;
import com.contacto.repository.NecesidadRepository;
import com.contacto.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner createDefaultAdmin(UserRepository userRepository, NecesidadRepository necesidadRepository) {
        return args -> {
            createUserIfMissing(userRepository, "admin", "1234", "ADMIN");
            createUserIfMissing(userRepository, "usuario", "1234", "USUARIO");
            createUserIfMissing(userRepository, "profesional", "1234", "PROFESIONAL");

            createNeedIfMissing(necesidadRepository, "Agua", "💧", "200", "Necesito agua", "need-water");
            createNeedIfMissing(necesidadRepository, "Comer", "🍽️", "200,100,200", "Necesito comer", "need-food");
            createNeedIfMissing(necesidadRepository, "Baño", "🚽", "400", "Necesito baño", "need-bathroom");
            createNeedIfMissing(necesidadRepository, "Ayuda", "🆘", "500,200,500", "Necesito ayuda", "need-help");
            createNeedIfMissing(necesidadRepository, "Dolor", "⚠️", "", "Tengo dolor", "need-pain");
            createNeedIfMissing(necesidadRepository, "Si", "✅", "", "Si", "need-yes");
            createNeedIfMissing(necesidadRepository, "No", "❌", "", "No", "need-no");
        };
    }

    private void createUserIfMissing(UserRepository userRepository, String username, String password, String rol) {
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRol(rol);
        userRepository.save(user);
    }

    private void createNeedIfMissing(
            NecesidadRepository necesidadRepository,
            String nombre,
            String icono,
            String patronVibracion,
            String mensajeVoz,
            String cssClass
    ) {
        if (necesidadRepository.findByNombreIgnoreCase(nombre).isPresent()) {
            return;
        }
        Necesidad necesidad = new Necesidad();
        necesidad.setNombre(nombre);
        necesidad.setIcono(icono);
        necesidad.setPatronVibracion(patronVibracion);
        necesidad.setMensajeVoz(mensajeVoz);
        necesidad.setCssClass(cssClass);
        necesidad.setActiva(true);
        necesidadRepository.save(necesidad);
    }
}
