package ore.spring.web.initializr.exception;

/**
 * The type Duplicate resource exception.
 */
public class DuplicateResourceException extends ResourceException {

    private static final String MESSAGE = "Duplicate resource found with ID: %s";

    /**
     * Instantiates a new Duplicate resource exception.
     *
     * @param resourceId the resource id
     */
    public DuplicateResourceException(Object resourceId) {
        super(String.format(MESSAGE, resourceId));
    }
}
