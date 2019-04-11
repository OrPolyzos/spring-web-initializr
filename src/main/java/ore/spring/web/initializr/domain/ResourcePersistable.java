package ore.spring.web.initializr.domain;

/**
 * The interface Resource persistable.
 *
 * @param <ID> the type parameter used for the (primary key) of the entity
 */
public interface ResourcePersistable<ID> {

    /**
     * Gets resource persistable id.
     *
     * @return the resource persistable id
     */
    ID getResourcePersistableId();
}
