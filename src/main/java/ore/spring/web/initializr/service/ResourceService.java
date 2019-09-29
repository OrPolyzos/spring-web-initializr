package ore.spring.web.initializr.service;

import ore.spring.web.initializr.domain.ResourcePersistable;
import ore.spring.web.initializr.exception.DuplicateResourceRuntimeException;
import ore.spring.web.initializr.exception.ResourceNotFoundRuntimeException;
import ore.spring.web.initializr.exception.ResourceRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * The ResourceService is a layer of abstraction to communicate with the CrudRepository.
 *
 * @param <R>   the type parameter used for the ResourcePersistable
 * @param <RSF> the type parameter used for the ResourceSearchForm
 * @param <ID>  the type parameter used for the Id of the ResourcePersistable
 */
public abstract class ResourceService<R extends ResourcePersistable<ID>, RSF, ID extends Serializable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceService.class);

    /**
     * The CrudRepository to be used for the ResourcePersistable
     */
    protected CrudRepository<R, ID> crudRepository;

    /**
     * @param crudRepository the CrudRepository
     */
    public ResourceService(CrudRepository<R, ID> crudRepository) {
        this.crudRepository = crudRepository;
    }

    /**
     * Finds a ResourcePersistable based on the resourceId (will be null if it was not found)
     *
     * @param resourceId the resourceId
     * @return the resourcePersistable that was found
     */
    public R find(ID resourceId) {
        return findOptional(resourceId).orElse(null);
    }

    /**
     * Find an optional of ResourcePersistable based on the resourcePersistableId
     * Catching NoSuchMethodError to maintain compatibility with spring-boot-starter-parent versions less than 2.0.0-RELEASE
     *
     * @param resourcePersistableId the resourcePersistableId
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
                throw new ResourceRuntimeException("Caught unexpected NoSuchMethodException during reflection.");
            }
        }
    }

    /**
     * Find a ResourcePersistable based on the resourcePersistableId or throw a ResourceNotFoundRuntimeException
     *
     * @param resourcePersistableId the resourcePersistableId
     * @return the resourcePersistable that was found
     * @throws ResourceNotFoundRuntimeException the ResourceNotFoundException that may be thrown
     */
    public R findOrThrow(ID resourcePersistableId) {
        return findOptional(resourcePersistableId)
                .orElseThrow(() -> new ResourceNotFoundRuntimeException(resourcePersistableId));
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
     * Search for ResourcePersistables by a ResourceSearchForm
     *
     * @param resourceSearchForm the ResourceSearchForm to be used for search criteria
     * @return the List of ResourcePersistables that were found
     */
    public List<R> searchBy(RSF resourceSearchForm) {
        return findAll();
    }

    /**
     * Insert a ResourcePersistable
     *
     * @param resourcePersistable the resourcePersistable
     * @return the resourcePersistable to be saved
     */
    public R insert(R resourcePersistable) {
        validateBeforeInsertOrThrow(resourcePersistable);
        return crudRepository.save(resourcePersistable);
    }

    /**
     * Update a ResourcePersistable
     *
     * @param resourcePersistable the resourcePersistable to be updated
     * @return the updated resourcePersistable
     */
    public R update(R resourcePersistable) {
        validateBeforeUpdateDeleteOrThrow(resourcePersistable.getResourcePersistableId());
        return crudRepository.save(resourcePersistable);
    }

    /**
     * Delete a ResourcePersistable by resourcePersistableId
     *
     * @param resourcePersistableId the resourceId to be used for deletion
     */
    public void deleteById(ID resourcePersistableId) {
        validateBeforeUpdateDeleteOrThrow(resourcePersistableId);
        crudRepository.deleteById(resourcePersistableId);
    }

    /**
     * Validate before insert or throw a DuplicateResourceException
     *
     * @param resourcePersistable the resourcePersistable to be inserted
     * @throws DuplicateResourceRuntimeException the duplicateResourceException
     */
    protected void validateBeforeInsertOrThrow(R resourcePersistable) {
        if (resourcePersistable.getResourcePersistableId() != null && findOptional(resourcePersistable.getResourcePersistableId()).isPresent()) {
            throw new DuplicateResourceRuntimeException(resourcePersistable.getResourcePersistableId());
        }
    }

    /**
     * Validate before update/delete or throw a resourceNotFoundRuntimeException
     *
     * @param resourcePersistableId the resourcePersistableId of the resourcePersistable to be updated/deleted
     * @throws ResourceNotFoundRuntimeException the resourceNotFoundRuntimeException
     */
    protected void validateBeforeUpdateDeleteOrThrow(ID resourcePersistableId) {
        if (resourcePersistableId == null || !findOptional(resourcePersistableId).isPresent()) {
            throw new ResourceNotFoundRuntimeException(resourcePersistableId);
        }
    }
}
