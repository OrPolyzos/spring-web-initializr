package ore.spring.web.initializr.exception;

import ore.spring.web.initializr.exception.base.RpRuntimeException;


public class RpMissingResourceException extends RpRuntimeException {

  private static final String MESSAGE = "Resource with id [%s] was not found";

  public RpMissingResourceException(Object id) {
    super(String.format(MESSAGE, id));
  }

}
