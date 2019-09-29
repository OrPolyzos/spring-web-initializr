package ore.spring.web.initializr.exception;

/**
 * The type Resource not found exception.
 */
public class ResourceNotFoundRuntimeException extends ResourceRuntimeException {

    private static final String MESSAGE = "No Resource found with ID: %s";

    private Object resourcePersistableId;

    /**
     * @param resourcePersistableId the resourcePersistableId
     */
    public ResourceNotFoundRuntimeException(Object resourcePersistableId) {
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
