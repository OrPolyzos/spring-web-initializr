package ore.spring.web.initializr.exception.runtime;

/**
 * The type RPRuntimeNotFoundException
 */
public class RPRuntimeNotFoundException extends RPRuntimeException {

    private static final String MESSAGE = "Nothing found with ID: %s";

    private Object resourcePersistableId;

    /**
     * Instantiates a new RPRuntimeNotFoundException
     *
     * @param resourcePersistableId the ResourcePersistableId
     */
    public RPRuntimeNotFoundException(Object resourcePersistableId) {
        super(String.format(MESSAGE, resourcePersistableId));
        this.resourcePersistableId = resourcePersistableId;
    }

    /**
     * @return the ResourcePersistableId of the ResourcePersistable that was not found
     */
    public Object getResourcePersistableId() {
        return resourcePersistableId;
    }
}
