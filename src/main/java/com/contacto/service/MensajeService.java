package com.contacto.service;

import com.contacto.model.Mensaje;
import com.contacto.repository.MensajeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MensajeService {

    private final MensajeRepository mensajeRepository;

    public MensajeService(MensajeRepository mensajeRepository) {
        this.mensajeRepository = mensajeRepository;
    }

    public void guardarMensaje(String contenido, String usuario) {
        Mensaje mensaje = new Mensaje();
        mensaje.setContenido(contenido);
        mensaje.setUsuario(usuario);
        mensaje.setFecha(LocalDateTime.now());
        mensajeRepository.save(mensaje);
    }

    public List<Mensaje> obtenerHistorial() {
        return mensajeRepository.findAllByOrderByFechaDesc();
    }

    public void borrarHistorial() {
        mensajeRepository.deleteAll();
    }
}
