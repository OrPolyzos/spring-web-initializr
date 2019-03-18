package ore.utils.initializrs.spring.web.initializr.exception;

public class ResourceNotFoundException extends ResourceException {

    private static final String MESSAGE = "Missing resource with ID: %s";

    public ResourceNotFoundException(Object id) {
        super(String.format(MESSAGE, id));
    }
}
