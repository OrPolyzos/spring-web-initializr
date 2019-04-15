package ore.spring.web.initializr.service;

import ore.spring.web.initializr.domain.ResourcePersistable;
import ore.spring.web.initializr.exception.DuplicateResourceException;
import ore.spring.web.initializr.exception.ResourceException;
import ore.spring.web.initializr.exception.ResourceNotFoundException;
import ore.spring.web.initializr.exception.ResourceRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The type Resource service.
 *
 * @param <R>   the type parameter used for the ResourcePersistable
 * @param <RSF> the type parameter used for the Resource Search Form class
 * @param <ID>  the type parameter used for the Id of the ResourcePersistable
 */
public abstract class ResourceService<R extends ResourcePersistable<ID>, RSF, ID extends Serializable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceService.class.getName());

    /**
     * The CrudRepository to be used for the ResourcePersistable
     */
    protected CrudRepository<R, ID> crudRepository;

    /**
     * Instantiates a new ResourceService.
     *
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
     * Find an optional of a ResourcePersistable based on the resourceId
     * Catching NoSuchMethodError to maintain compatibility with spring-boot-starter-parent less than 2.0.0-RELEASE
     *
     * @param resourceId the resourceId
     * @return the optional of a ResourcePersistable
     */
    @SuppressWarnings("unchecked")
    public Optional<R> findOptional(ID resourceId) {
        try {
            return crudRepository.findById(resourceId);
        } catch (NoSuchMethodError e) {
            LOGGER.warn("Caught (expected) NoSuchMethodError. Most probably you are using a spring-boot-starter-parent version lower than 2.0.0.RELEASE. It is strongly advised to override this method and use findOne() instead or update your spring-boot-starter-parent-version to (at least) 2.0.0.RELEASE.");
            try {
                // Fallback for spring-boot-starter-parent version less than 2.0.0-RELEASE
                Method findOneMethod = crudRepository.getClass().getMethod("findOne", Serializable.class);
                return Optional.ofNullable((R) findOneMethod.invoke(crudRepository, resourceId));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                LOGGER.error(e.getMessage());
                throw new ResourceRuntimeException("Caught unexpected NoSuchMethodException during reflection. It is strongly advised to consult the readme file at the Github Repository.");
            }
        }
    }

    /**
     * Find a ResourcePersistable based on the resourceId or throw a ResourceNotFoundException
     *
     * @param resourceId the resourceId
     * @return the resourcePersistable that was found
     * @throws ResourceNotFoundException the resourceNotFoundException that may be thrown
     */
    public R findOrThrow(ID resourceId) throws ResourceNotFoundException {
        return findOptional(resourceId).orElseThrow(() -> new ResourceNotFoundException(resourceId));
    }

    /**
     * Find all ResourcePersistables
     *
     * @return the iterable of ResourcePersistables that were found
     */
    public Iterable<R> findAll() {
        return crudRepository.findAll();
    }

    /**
     * Search for ResourcePersistables by a ResourceSearchForm
     *
     * @param resourceSearchForm the resourceSearchForm to be used for search criteria
     * @return the iterable of ResourcePersistables that were found
     */
    public Iterable<R> searchBy(RSF resourceSearchForm) {
        return findAll();
    }

    /**
     * Insert a ResourcePersistable
     *
     * @param resource the resourcePersistable
     * @return the inserted resourcePersistable
     * @throws ResourceException the resourceException that may be thrown
     */
    public R insert(R resource) throws ResourceException {
        validateBeforeInsertOrThrow(resource);
        return crudRepository.save(resource);
    }

    /**
     * Update a ResourcePersistable
     *
     * @param resource the resourcePersistable to be updated
     * @return the updated resourcePersistable
     * @throws ResourceException the resourceException that may be thrown
     */
    public R update(R resource) throws ResourceException {
        validateBeforeUpdateOrThrow(resource);
        return crudRepository.save(resource);
    }

    /**
     * Delete a ResourcePersistable by resourceId
     *
     * @param resourceId the resourceId to be used for deletion
     * @throws ResourceException the resourceException that may be thrown
     */
    public void deleteById(ID resourceId) throws ResourceException {
        R actualEntity = findOrThrow(resourceId);
        crudRepository.delete(actualEntity);
    }

    /**
     * Validate before insert or throw the resourceException
     *
     * @param resource the resourcePersistable to be inserted
     * @throws ResourceException the resourceException
     */
    protected void validateBeforeInsertOrThrow(R resource) throws ResourceException {
        if (resource.getResourcePersistableId() != null && findOptional(resource.getResourcePersistableId()).isPresent()) {
            throw new DuplicateResourceException(resource.getResourcePersistableId());
        }
    }

    /**
     * Validate before update or throw the resourceException
     *
     * @param resource the resourcePersistable to be updated
     * @throws ResourceException the resourceException
     */
    protected void validateBeforeUpdateOrThrow(R resource) throws ResourceException {
        if (resource.getResourcePersistableId() == null || !findOptional(resource.getResourcePersistableId()).isPresent()) {
            throw new ResourceNotFoundException(resource.getResourcePersistableId());
        }
    }

    /**
     * Map an optional of a ResourcePersistable to a Empty/Singleton List of ResourcePersistable
     *
     * @param optionalEntity the optional entity
     * @return the list
     */
    protected List<R> mapOptionalToList(Optional<R> optionalEntity) {
        return optionalEntity.map(Collections::singletonList).orElse(Collections.emptyList());
    }
}
