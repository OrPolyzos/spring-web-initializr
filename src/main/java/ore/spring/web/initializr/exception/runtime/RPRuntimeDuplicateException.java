package ore.spring.web.initializr.exception.runtime;

/**
 * The type RPRuntimeDuplicateException
 */
public class RPRuntimeDuplicateException extends RPRuntimeException {

    private static final String MESSAGE = "ID: %s exists already";

    private Object resourcePersistableId;

    /**
     * Instantiates a new RPRuntimeDuplicateException
     *
     * @param resourcePersistableId the ResourcePersistableId
     */
    public RPRuntimeDuplicateException(Object resourcePersistableId) {
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
