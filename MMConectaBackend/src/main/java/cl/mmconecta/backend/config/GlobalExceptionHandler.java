package cl.mmconecta.backend.config;

import cl.mmconecta.backend.model.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Convierte errores de validacion (@Valid) en una respuesta JSON simple
 * en vez del error por defecto de Spring, para que Retrofit siempre reciba
 * un cuerpo con el mismo formato (success/message).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public LoginResponse handleValidation(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("Datos invalidos");
        return new LoginResponse(false, mensaje);
    }
}
