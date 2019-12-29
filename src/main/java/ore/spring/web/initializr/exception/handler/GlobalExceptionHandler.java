package ore.spring.web.initializr.exception.handler;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import ore.spring.web.initializr.exception.RpDuplicateResourceException;
import ore.spring.web.initializr.exception.RpMissingResourceException;
import ore.spring.web.initializr.exception.error.RpError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RpMissingResourceException.class)
  public ResponseEntity<RpError> handleResourceNotFound(final RpMissingResourceException e) {
    RpError rpError = new RpError(NOT_FOUND, e.getMessage());
    return ResponseEntity.status(rpError.getErrorHttpStatus()).body(rpError);
  }

  @ExceptionHandler(RpDuplicateResourceException.class)
  public ResponseEntity<RpError> handleDuplicateResource(final RpDuplicateResourceException e) {
    RpError rpError = new RpError(UNPROCESSABLE_ENTITY, e.getMessage());
    return ResponseEntity.status(rpError.getErrorHttpStatus()).body(rpError);
  }

}
