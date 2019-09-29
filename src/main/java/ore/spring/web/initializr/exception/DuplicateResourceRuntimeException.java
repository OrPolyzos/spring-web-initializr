package ore.spring.web.initializr.exception;

/**
 * The type DuplicateResourceRuntimeException.
 */
public class DuplicateResourceRuntimeException extends ResourceRuntimeException {

    private static final String MESSAGE = "There is already a Resource with ID: %s";

    private Object resourcePersistableId;

    /**
     * @param resourcePersistableId the resource id
     */
    public DuplicateResourceRuntimeException(Object resourcePersistableId) {
        super(String.format(MESSAGE, resourcePersistableId));
        this.resourcePersistableId = resourcePersistableId;
    }

    /**
     * @return the resourcePersistableId of the ResourcePersistable that was not found
     */
    public Object getResourcePersistableId() {
        return resourcePersistableId;
    }
}
