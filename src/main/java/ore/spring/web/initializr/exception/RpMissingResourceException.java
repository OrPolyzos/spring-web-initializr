package ore.spring.web.initializr.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import ore.spring.web.initializr.exception.base.RpRuntimeException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(NOT_FOUND)
public class RpMissingResourceException extends RpRuntimeException {

  private static final String MESSAGE = "Resource with id [%s] was not found";

  public RpMissingResourceException(Object id) {
    super(String.format(MESSAGE, id));
  }

}
