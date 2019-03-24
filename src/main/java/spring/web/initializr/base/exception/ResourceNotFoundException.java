package spring.web.initializr.base.exception;

public class ResourceNotFoundException extends ResourceException {

    private static final String MESSAGE = "Missing resource with ID: %s";

    public ResourceNotFoundException(Object id) {
        super(String.format(MESSAGE, id));
    }
}
