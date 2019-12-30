package ore.spring.web.initializr.exception;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import ore.spring.web.initializr.exception.base.RpRuntimeException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(UNPROCESSABLE_ENTITY)
public class RpDuplicateResourceException extends RpRuntimeException {

  private static final String MESSAGE = "%s with field [%s]: [%s] exists already";

  public RpDuplicateResourceException(String resourceName, String fieldName, String fieldValue) {
    super(String.format(MESSAGE, resourceName, fieldName, fieldValue));
  }

}
