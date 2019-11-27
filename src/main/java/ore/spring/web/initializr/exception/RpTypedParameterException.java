package ore.spring.web.initializr.exception;

import ore.spring.web.initializr.exception.base.RpRuntimeException;

public class RpTypedParameterException extends RpRuntimeException {

    private static final String MESSAGE = "Invalid Typed Parameter index (%s). Expected < %s";

    private final int typedParameterIndex;
    private final int maxTypesParameters;

    public RpTypedParameterException(int typedParameterIndex, int maxTypesParameters) {
        this.typedParameterIndex = typedParameterIndex;
        this.maxTypesParameters = maxTypesParameters;
    }

    public RpTypedParameterException(String message, int typedParameterIndex, int maxTypesParameters) {
        super(message);
        this.typedParameterIndex = typedParameterIndex;
        this.maxTypesParameters = maxTypesParameters;
    }

    public RpTypedParameterException(String message, Throwable cause, int typedParameterIndex, int maxTypesParameters) {
        super(message, cause);
        this.typedParameterIndex = typedParameterIndex;
        this.maxTypesParameters = maxTypesParameters;
    }

    public RpTypedParameterException(Throwable cause, int typedParameterIndex, int maxTypesParameters) {
        super(cause);
        this.typedParameterIndex = typedParameterIndex;
        this.maxTypesParameters = maxTypesParameters;
    }

    public RpTypedParameterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int typedParameterIndex, int maxTypesParameters) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.typedParameterIndex = typedParameterIndex;
        this.maxTypesParameters = maxTypesParameters;
    }

    public String getDisplayMessage() {
        return String.format(MESSAGE, this.typedParameterIndex, this.maxTypesParameters);
    }

}
