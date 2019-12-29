package ore.spring.web.initializr.exception.base;

public abstract class RpRuntimeException extends RuntimeException {

  public RpRuntimeException(String message) {
    super(message);
  }
}
