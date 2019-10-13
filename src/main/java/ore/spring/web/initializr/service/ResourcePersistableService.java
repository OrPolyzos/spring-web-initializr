package ore.spring.web.initializr.service;

import ore.spring.web.initializr.domain.ResourcePersistable;
import ore.spring.web.initializr.exception.runtime.RPRuntimeDuplicateException;
import ore.spring.web.initializr.exception.runtime.RPRuntimeNotFoundException;
import ore.spring.web.initializr.exception.runtime.RPRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * The ResourcePersistableService is a layer of abstraction to communicate with the CrudRepository
 * The ResourcePersistableService is an intermediate layer of between the ResourcePersistableController and the CrudRepository
 *
 * @param <R>   the type parameter used for the ResourcePersistable
 * @param <RSF> the type parameter used for the ResourcePersistableSearchForm
 * @param <ID>  the type parameter used for the ResourcePersistableId
 */
public abstract class ResourcePersistableService<R extends ResourcePersistable<ID>, ID extends Serializable, RSF> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourcePersistableService.class);

    /**
     * The CrudRepository to be used for the ResourcePersistable
     */
    private CrudRepository<R, ID> crudRepository;

    /**
     * @param crudRepository the CrudRepository
     */
    public ResourcePersistableService(CrudRepository<R, ID> crudRepository) {
        this.crudRepository = crudRepository;
    }

    /**
     * Find a ResourcePersistable based on its resourcePersistableId
     *
     * @param resourcePersistableId the ResourcePersistableId
     * @return the ResourcePersistable that was found or else null
     */
    public R find(ID resourcePersistableId) {
        return findOptional(resourcePersistableId).orElse(null);
    }

    /**
     * Find an optional of ResourcePersistable based on its resourcePersistableId
     * Using reflection as a fallback mechanism to maintain compatibility with spring-boot-starter-parent versions less than 2.0.0-RELEASE
     *
     * @param resourcePersistableId the ResourcePersistableId
     * @return the optional of a ResourcePersistable
     */
    @SuppressWarnings("unchecked")
    public Optional<R> findOptional(ID resourcePersistableId) {
        try {
            return crudRepository.findById(resourcePersistableId);
        } catch (NoSuchMethodError e) {
            LOGGER.warn("Caught (expected) NoSuchMethodError. Most probably you are using a spring-boot-starter-parent version lower than 2.0.0.RELEASE. It is strongly advised to override this method and use findOne() instead or update your spring-boot-starter-parent-version to (at least) 2.0.0.RELEASE.");
            try {
                // Fallback for spring-boot-starter-parent version less than 2.0.0-RELEASE
                Method findOneMethod = crudRepository.getClass().getMethod("findOne", Serializable.class);
                return Optional.ofNullable((R) findOneMethod.invoke(crudRepository, resourcePersistableId));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                LOGGER.error(e.getMessage());
                throw new RPRuntimeException("Caught unexpected NoSuchMethodException during reflection.");
            }
        }
    }

    /**
     * Find a ResourcePersistable based on its resourcePersistableId or throw a RPRuntimeNotFoundException
     *
     * @param resourcePersistableId the ResourcePersistableId
     * @return the ResourcePersistable that was found
     * @throws RPRuntimeNotFoundException if no ResourcePersistable was found
     */
    public R findOrThrow(ID resourcePersistableId) {
        return findOptional(resourcePersistableId)
                .orElseThrow(() -> new RPRuntimeNotFoundException(resourcePersistableId));
    }

    /**
     * Find all ResourcePersistables
     *
     * @return the List of ResourcePersistables that were found
     */
    public List<R> findAll() {
        return (List<R>) crudRepository.findAll();
    }

    /**
     * Search for ResourcePersistables by a ResourcePersistableSearchForm
     *
     * @param resourcePersistableSearchForm the ResourcePersistableSearchForm to be used for search criteria
     * @return the List of ResourcePersistables that were found
     */
    public List<R> searchBy(RSF resourcePersistableSearchForm) {
        return findAll();
    }

    /**
     * Insert a ResourcePersistable
     *
     * @param resourcePersistable the ResourcePersistable
     * @return the ResourcePersistable that was created
     */
    public R insert(R resourcePersistable) {
        validateBeforeInsertOrThrow(resourcePersistable);
        return crudRepository.save(resourcePersistable);
    }

    /**
     * Update a ResourcePersistable
     *
     * @param resourcePersistable the ResourcePersistable to be updated
     * @return the ResourcePersistable that was updated
     */
    public R update(R resourcePersistable) {
        validateBeforeUpdateDeleteOrThrow(resourcePersistable.getResourcePersistableId());
        return crudRepository.save(resourcePersistable);
    }

    /**
     * Delete a ResourcePersistable by its ResourcePersistableId
     *
     * @param resourcePersistableId the ResourcePersistableId to be used for deletion
     */
    public void deleteById(ID resourcePersistableId) {
        validateBeforeUpdateDeleteOrThrow(resourcePersistableId);
        crudRepository.deleteById(resourcePersistableId);
    }

    /**
     * Validate before insert or throw a RPRuntimeDuplicateException
     *
     * @param resourcePersistable the resourcePersistable to be inserted
     * @throws RPRuntimeDuplicateException if the ResourcePersistableId of the resourcePersistable exists already
     */
    protected void validateBeforeInsertOrThrow(R resourcePersistable) {
        if (resourcePersistable.getResourcePersistableId() != null && findOptional(resourcePersistable.getResourcePersistableId()).isPresent()) {
            throw new RPRuntimeDuplicateException(resourcePersistable.getResourcePersistableId());
        }
    }

    /**
     * Validate before update/delete or throw a RPRuntimeNotFoundException
     *
     * @param resourcePersistableId the ResourcePersistableId of the resourcePersistable to be updated/deleted
     * @throws RPRuntimeNotFoundException the RPRuntimeNotFoundException
     */
    protected void validateBeforeUpdateDeleteOrThrow(ID resourcePersistableId) {
        if (resourcePersistableId == null || !findOptional(resourcePersistableId).isPresent()) {
            throw new RPRuntimeNotFoundException(resourcePersistableId);
        }
    }
}
