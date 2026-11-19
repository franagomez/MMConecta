package cl.mmconecta.backend.controller;

import cl.mmconecta.backend.model.LoginRequest;
import cl.mmconecta.backend.model.LoginResponse;
import cl.mmconecta.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // POST /api/usuarios/login  { "username": "...", "password": "..." }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        boolean ok = usuarioService.validar(request.getUsername(), request.getPassword());
        if (ok) {
            return ResponseEntity.ok(new LoginResponse(true, "Inicio de sesion exitoso"));
        }
        return ResponseEntity.status(401)
                .body(new LoginResponse(false, "Usuario o contrasena incorrectos"));
    }
}
