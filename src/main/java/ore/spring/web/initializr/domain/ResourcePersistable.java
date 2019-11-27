package ore.spring.web.initializr.domain;

import java.io.Serializable;

/**
 * The interface ResourcePersistable
 *
 * @param <ID> the type parameter used for the (primary key) of the entity
 */
public interface ResourcePersistable<ID extends Serializable> {

    /**
     * Get the resourcePersistableId
     *
     * @return the ResourcePersistableId
     */
    ID getRpId();
}
