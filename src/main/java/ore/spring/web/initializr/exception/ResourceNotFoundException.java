package ore.spring.web.initializr.exception;

/**
 * The type Resource not found exception.
 */
public class ResourceNotFoundException extends ResourceException {

    private static final String MESSAGE = "Missing resource with ID: %s";

    /**
     * Instantiates a new Resource not found exception.
     *
     * @param id the id
     */
    public ResourceNotFoundException(Object id) {
        super(String.format(MESSAGE, id));
    }
}
