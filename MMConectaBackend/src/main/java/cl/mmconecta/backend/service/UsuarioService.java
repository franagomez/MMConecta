package cl.mmconecta.backend.service;

import org.springframework.stereotype.Service;

/**
 * Logica de autenticacion. Misma regla simple que LocalLoginRepository en
 * la app Android (usuario no vacio + contrasena de al menos 4 caracteres),
 * para que el comportamiento sea identico al migrar de repo local a Retrofit.
 */
@Service
public class UsuarioService {

    public boolean validar(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        return !username.isBlank() && password.length() >= 4;
    }
}
