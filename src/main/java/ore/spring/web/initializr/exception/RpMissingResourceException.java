package ore.spring.web.initializr.exception;

import ore.spring.web.initializr.exception.base.RpRuntimeException;


public class RpMissingResourceException extends RpRuntimeException {

    private static final String MESSAGE = "%s with id [%s] was not found";

    private final Object id;
    private final String resourcePersistableName;

    public RpMissingResourceException(Object id, String resourcePersistableName) {
        this.id = id;
        this.resourcePersistableName = resourcePersistableName;
    }

    public RpMissingResourceException(String message, Object id, String resourcePersistableName) {
        super(message);
        this.id = id;
        this.resourcePersistableName = resourcePersistableName;
    }

    public RpMissingResourceException(String message, Throwable cause, Object id, String resourcePersistableName) {
        super(message, cause);
        this.id = id;
        this.resourcePersistableName = resourcePersistableName;
    }

    public RpMissingResourceException(Throwable cause, Object id, String resourcePersistableName) {
        super(cause);
        this.id = id;
        this.resourcePersistableName = resourcePersistableName;
    }

    public RpMissingResourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object id, String resourcePersistableName) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.id = id;
        this.resourcePersistableName = resourcePersistableName;
    }

    @Override
    public String getDisplayMessage() {
        return String.format(MESSAGE, this.resourcePersistableName, this.id);
    }
}
