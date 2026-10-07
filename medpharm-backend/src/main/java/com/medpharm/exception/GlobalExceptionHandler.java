package com.medpharm.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ProblemDetail> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return construir(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage(), "recurso-no-encontrado");
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ProblemDetail> manejarStock(StockInsuficienteException ex) {
        return construir(HttpStatus.CONFLICT, "Stock insuficiente", ex.getMessage(), "stock-insuficiente");
    }

    @ExceptionHandler(RecetaNoModificableException.class)
    public ResponseEntity<ProblemDetail> manejarRecetaNoModificable(RecetaNoModificableException ex) {
        return construir(HttpStatus.CONFLICT, "Receta no modificable", ex.getMessage(), "receta-no-modificable");
    }

    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<ProblemDetail> manejarSolicitudInvalida(SolicitudInvalidaException ex) {
        return construir(HttpStatus.BAD_REQUEST, "Solicitud inválida", ex.getMessage(), "solicitud-invalida");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> manejarCredenciales(BadCredentialsException ex) {
        return construir(HttpStatus.UNAUTHORIZED, "Credenciales inválidas",
                "El usuario o la contraseña son incorrectos.", "credenciales-invalidas");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> manejarGenerica(Exception ex) {
        log.error("Error no controlado", ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno",
                "Ocurrió un error inesperado en el servidor.", "error-interno");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.putIfAbsent(e.getField(), e.getDefaultMessage()));

        ProblemDetail problema = crearProblema(HttpStatus.BAD_REQUEST, "Error de validación",
                "Uno o más campos no cumplen las reglas de validación.", "validacion");
        problema.setProperty("errores", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problema);
    }

    private ResponseEntity<ProblemDetail> construir(HttpStatus status, String titulo, String detalle, String tipo) {
        return ResponseEntity.status(status).body(crearProblema(status, titulo, detalle, tipo));
    }

    private ProblemDetail crearProblema(HttpStatus status, String titulo, String detalle, String tipo) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, detalle);
        problema.setTitle(titulo);
        problema.setType(URI.create("https://medpharm.local/errors/" + tipo));
        problema.setProperty("timestamp", Instant.now().toString());
        return problema;
    }
}
