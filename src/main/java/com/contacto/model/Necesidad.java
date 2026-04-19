package com.contacto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "necesidades")
public class Necesidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100, unique = true)
    private String nombre;

    @Column(name = "icono", nullable = false, length = 8)
    private String icono;

    @Column(name = "patron_vibracion", length = 100)
    private String patronVibracion;

    @Column(name = "mensaje_voz", nullable = false, length = 150)
    private String mensajeVoz;

    @Column(name = "css_class", nullable = false, length = 50)
    private String cssClass;

    @Column(name = "activa", nullable = false)
    private boolean activa = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getPatronVibracion() {
        return patronVibracion;
    }

    public void setPatronVibracion(String patronVibracion) {
        this.patronVibracion = patronVibracion;
    }

    public String getMensajeVoz() {
        return mensajeVoz;
    }

    public void setMensajeVoz(String mensajeVoz) {
        this.mensajeVoz = mensajeVoz;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
    
}
