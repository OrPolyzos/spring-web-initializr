package ore.spring.web.initializr.domain;

/**
 * The interface ResourcePersistable
 *
 * @param <ID> the type parameter used for the (primary key) of the entity
 */
public interface ResourcePersistable<ID> {

    /**
     * Get the resourcePersistableId
     *
     * @return the ResourcePersistableId
     */
    ID getResourcePersistableId();
}
