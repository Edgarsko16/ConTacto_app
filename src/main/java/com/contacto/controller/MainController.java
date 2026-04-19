package com.contacto.controller;

import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.contacto.model.User;
import com.contacto.service.AuthService;
import com.contacto.service.MensajeService;
import com.contacto.service.NecesidadService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class MainController {

    private final AuthService authService;
    private final MensajeService mensajeService;
    private final NecesidadService necesidadService;

    public MainController(AuthService authService, MensajeService mensajeService, NecesidadService necesidadService) {
        this.authService = authService;
        this.mensajeService = mensajeService;
        this.necesidadService = necesidadService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@Valid @ModelAttribute("user") User user, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "login";
        }

        return authService.authenticateUser(user).map(authenticatedUser -> {
            String rol = normalizeRole(authenticatedUser.getRol());
            session.setAttribute("username", authenticatedUser.getUsername());
            session.setAttribute("rol", rol);

            if ("PROFESIONAL".equals(rol)) {
                return "redirect:/historial";
            }
            return "redirect:/panel";
        }).orElseGet(() -> {
            model.addAttribute("errorMessage", "Credenciales invalidas.");
            model.addAttribute("user", user);
            return "login";
        });
    }

    @GetMapping("/panel")
    public String panel(Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            model.addAttribute("errorMessage", "Debes iniciar sesion para continuar.");
            model.addAttribute("user", new User());
            return "login";
        }

        String rol = getRole(session);
        if (!hasAnyRole(rol, "ADMIN", "USUARIO")) {
            model.addAttribute("errorMessage", "Tu rol no tiene acceso al panel.");
            model.addAttribute("user", new User());
            return "login";
        }

        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", rol);
        model.addAttribute("necesidades", necesidadService.obtenerNecesidadesActivas());
        return "panel";
    }

    @GetMapping("/historial")
    public String historial(Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            model.addAttribute("errorMessage", "Debes iniciar sesion para continuar.");
            model.addAttribute("user", new User());
            return "login";
        }

        String rol = getRole(session);
        if (!hasAnyRole(rol, "ADMIN", "PROFESIONAL")) {
            model.addAttribute("errorMessage", "Tu rol no tiene acceso al historial.");
            model.addAttribute("user", new User());
            return "panel";
        }

        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", rol);
        model.addAttribute("mensajes", mensajeService.obtenerHistorial());
        return "historial";
    }

    @GetMapping("/configuracion")
    public String configuracion(Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            model.addAttribute("errorMessage", "Debes iniciar sesion para continuar.");
            model.addAttribute("user", new User());
            return "login";
        }

        String rol = getRole(session);
        if (!hasAnyRole(rol, "ADMIN", "PROFESIONAL")) {
            model.addAttribute("errorMessage", "Tu rol no tiene acceso a configuracion.");
            return "redirect:/panel";
        }

        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", rol);
        model.addAttribute("necesidades", necesidadService.obtenerNecesidadesActivas());
        return "configuracion";
    }

    @PostMapping("/configuracion/patron")
    public String actualizarPatron(
            @RequestParam("id") Long id,
            @RequestParam("patron") String patron,
            @RequestParam("mensajeVoz") String mensajeVoz,
            HttpSession session,
            Model model
    ) {
        if (!isLoggedIn(session)) {
            model.addAttribute("errorMessage", "Debes iniciar sesion para continuar.");
            model.addAttribute("user", new User());
            return "login";
        }
        if (!hasAnyRole(getRole(session), "ADMIN", "PROFESIONAL")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        necesidadService.actualizarPatron(id, patron, mensajeVoz);
        return "redirect:/configuracion";
    }

    @PostMapping("/configuracion/nueva")
    public String crearNuevaNecesidad(
            @RequestParam("nombre") String nombre,
            @RequestParam("icono") String icono,
            @RequestParam("patron") String patron,
            @RequestParam("mensajeVoz") String mensajeVoz,
            HttpSession session,
            Model model
    ) {
        if (!isLoggedIn(session)) {
            model.addAttribute("errorMessage", "Debes iniciar sesion para continuar.");
            model.addAttribute("user", new User());
            return "login";
        }
        if (!hasAnyRole(getRole(session), "ADMIN", "PROFESIONAL")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        necesidadService.crearNueva(nombre, icono, patron, mensajeVoz);
        return "redirect:/configuracion";
    }

    @PostMapping("/configuracion/eliminar")
public String eliminarNecesidad(
        @RequestParam("id") Long id,
        HttpSession session,
        Model model
) {

    if (!isLoggedIn(session)) {
        model.addAttribute("errorMessage", "Debes iniciar sesion para continuar.");
        model.addAttribute("user", new User());
        return "login";
    }

    if (!hasAnyRole(getRole(session), "ADMIN", "PROFESIONAL")) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    necesidadService.desactivar(id);

    return "redirect:/configuracion";
}

    @PostMapping("/historial/borrar")
    public String borrarHistorial(Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            model.addAttribute("errorMessage", "Debes iniciar sesion para continuar.");
            model.addAttribute("user", new User());
            return "login";
        }

        String rol = getRole(session);
        if (!hasAnyRole(rol, "ADMIN")) {
            model.addAttribute("errorMessage", "Solo ADMIN puede borrar el historial.");
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("rol", rol);
            model.addAttribute("mensajes", mensajeService.obtenerHistorial());
            return "historial";
        }

        mensajeService.borrarHistorial();
        return "redirect:/historial";
    }

    @PostMapping("/api/mensajes")
    @ResponseBody
    public ResponseEntity<Void> guardarMensaje(@RequestBody Map<String, String> payload, HttpSession session) {
        if (!isLoggedIn(session)) {
            return ResponseEntity.status(401).build();
        }

        String rol = getRole(session);
        if (!hasAnyRole(rol, "ADMIN", "USUARIO")) {
            return ResponseEntity.status(403).build();
        }

        String contenido = payload.getOrDefault("contenido", "").trim();
        String usuario = String.valueOf(session.getAttribute("username")).trim();

        if (contenido.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        mensajeService.guardarMensaje(contenido, usuario);
        return ResponseEntity.ok().build();
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("username") != null && session.getAttribute("rol") != null;
    }

    private String getRole(HttpSession session) {
        Object rawRole = session.getAttribute("rol");
        if (rawRole == null) {
            return "";
        }
        return normalizeRole(String.valueOf(rawRole));
    }

    private boolean hasAnyRole(String role, String... allowed) {
        Set<String> allowedSet = Set.of(allowed);
        return allowedSet.contains(role);
    }

    private String normalizeRole(String role) {
        return role == null ? "" : role.trim().toUpperCase();
    }
}
