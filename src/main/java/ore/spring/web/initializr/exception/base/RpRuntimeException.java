package ore.spring.web.initializr.exception.base;

public abstract class RpRuntimeException extends RuntimeException {

    public RpRuntimeException() {
    }

    public RpRuntimeException(String message) {
        super(message);
    }

    public RpRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpRuntimeException(Throwable cause) {
        super(cause);
    }

    public RpRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public abstract String getDisplayMessage();

}
