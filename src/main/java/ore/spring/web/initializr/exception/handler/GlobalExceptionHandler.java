package ore.spring.web.initializr.exception.handler;

import ore.spring.web.initializr.exception.RpDuplicateResourceException;
import ore.spring.web.initializr.exception.RpMissingResourceException;
import ore.spring.web.initializr.exception.error.RpError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(RpMissingResourceException.class)
    public RpError handleResourceNotFound(final RpMissingResourceException e) {
        return RpError.builder()
                .error(NOT_FOUND)
                .status(NOT_FOUND.value())
                .message(e.getDisplayMessage())
                .build();
    }

    @ResponseBody
    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(RpDuplicateResourceException.class)
    public RpError handleDuplicateResource(final RpDuplicateResourceException e) {
        return RpError.builder()
                .error(UNPROCESSABLE_ENTITY)
                .status(UNPROCESSABLE_ENTITY.value())
                .message(e.getDisplayMessage())
                .build();
    }

}
