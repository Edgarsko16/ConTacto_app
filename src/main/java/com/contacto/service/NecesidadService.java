package com.contacto.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.contacto.model.Necesidad;
import com.contacto.repository.NecesidadRepository;

@Service
public class NecesidadService {

    private final NecesidadRepository necesidadRepository;

    public NecesidadService(NecesidadRepository necesidadRepository) {
        this.necesidadRepository = necesidadRepository;
    }

    public List<Necesidad> obtenerNecesidadesActivas() {
        return necesidadRepository.findByActivaTrueOrderByIdAsc();
    }

    public List<Necesidad> obtenerTodas() {
        return necesidadRepository.findAll();
    }

    public void actualizarPatron(Long id, String patronVibracion, String mensajeVoz) {
        if (id == null) {
            return;
        }
        Optional<Necesidad> optional = necesidadRepository.findById(id);
        if (optional.isEmpty()) {
            return;
        }
        Necesidad necesidad = optional.get();
        necesidad.setPatronVibracion(normalizePattern(patronVibracion));
        if (mensajeVoz != null && !mensajeVoz.trim().isEmpty()) {
            necesidad.setMensajeVoz(mensajeVoz.trim());
        }
        necesidadRepository.save(necesidad);
    }

    public void crearNueva(String nombre, String icono, String patronVibracion, String mensajeVoz) {
        String normalizedName = nombre == null ? "" : nombre.trim();
        if (normalizedName.isEmpty()) {
            return;
        }

        Optional<Necesidad> existing = necesidadRepository.findByNombreIgnoreCase(normalizedName);
        if (existing.isPresent()) {
            Necesidad necesidad = existing.get();
            if (!necesidad.isActiva()) {
                necesidad.setIcono((icono == null || icono.trim().isEmpty()) ? "🔔" : icono.trim());
                necesidad.setPatronVibracion(normalizePattern(patronVibracion));
                String defaultMessage = "Necesito " + normalizedName.toLowerCase();
                necesidad.setMensajeVoz((mensajeVoz == null || mensajeVoz.trim().isEmpty()) ? defaultMessage : mensajeVoz.trim());
                necesidad.setCssClass("need-custom");
                necesidad.setActiva(true);
                necesidadRepository.save(necesidad);
            }
            return;
        }

        Necesidad nueva = new Necesidad();
        nueva.setNombre(normalizedName);
        nueva.setIcono((icono == null || icono.trim().isEmpty()) ? "🔔" : icono.trim());
        nueva.setPatronVibracion(normalizePattern(patronVibracion));
        String defaultMessage = "Necesito " + normalizedName.toLowerCase();
        nueva.setMensajeVoz((mensajeVoz == null || mensajeVoz.trim().isEmpty()) ? defaultMessage : mensajeVoz.trim());
        nueva.setCssClass("need-custom");
        nueva.setActiva(true);
        necesidadRepository.save(nueva);
    }

    public void desactivar(Long id) {

        if (id == null) {
            return;
        }

        Necesidad necesidad = necesidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Necesidad no encontrada"));
    
        necesidad.setActiva(false);
    
        necesidadRepository.save(necesidad);
    }

    private String normalizePattern(String pattern) {
        if (pattern == null) {
            return "";
        }
        return pattern.trim().replaceAll("\\s+", "");
    }
}
