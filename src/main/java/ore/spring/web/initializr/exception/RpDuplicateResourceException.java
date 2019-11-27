package ore.spring.web.initializr.exception;

import ore.spring.web.initializr.exception.base.RpRuntimeException;

public class RpDuplicateResourceException extends RpRuntimeException {

    private static final String MESSAGE = "%s with field [%s]: [%s] exists already";

    private final String resourceName;
    private final String fieldName;
    private final String fieldValue;

    public RpDuplicateResourceException(String resourceName, String fieldName, String fieldValue) {
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public RpDuplicateResourceException(String message, String resourceName, String fieldName, String fieldValue) {
        super(message);
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public RpDuplicateResourceException(String message, Throwable cause, String resourceName, String fieldName, String fieldValue) {
        super(message, cause);
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public RpDuplicateResourceException(Throwable cause, String resourceName, String fieldName, String fieldValue) {
        super(cause);
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public RpDuplicateResourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String resourceName, String fieldName, String fieldValue) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getDisplayMessage() {
        return String.format(MESSAGE, this.resourceName, this.fieldName, this.fieldValue);
    }

}
