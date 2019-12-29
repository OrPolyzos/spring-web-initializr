package ore.spring.web.initializr.exception;

import ore.spring.web.initializr.exception.base.RpRuntimeException;


public class RpDuplicateResourceException extends RpRuntimeException {

  private static final String MESSAGE = "%s with field [%s]: [%s] exists already";

  public RpDuplicateResourceException(String resourceName, String fieldName, String fieldValue) {
    super(String.format(MESSAGE, resourceName, fieldName, fieldValue));
  }

}
