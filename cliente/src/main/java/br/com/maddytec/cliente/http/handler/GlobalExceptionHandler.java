package br.com.maddytec.cliente.http.handler;

import java.net.URI;
import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handle(ResponseStatusException ex, ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, ex.getReason());
        pd.setTitle(status.getReasonPhrase());
        pd.setInstance(URI.create(exchange.getRequest().getPath().value()));
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        pd.setProperty("requestId", exchange.getRequest().getId());
        return ResponseEntity.status(status).body(pd);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ProblemDetail> handleThrowable(Throwable ex, ServerWebExchange exchange) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        pd.setTitle("Internal Server Error");
        pd.setInstance(URI.create(exchange.getRequest().getPath().value()));
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        pd.setProperty("requestId", exchange.getRequest().getId());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(pd);
    }
}
