package ore.spring.web.initializr.service.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import ore.spring.web.initializr.domain.ResourcePersistable;
import ore.spring.web.initializr.exception.RpDuplicateResourceException;
import ore.spring.web.initializr.exception.RpMissingResourceException;
import ore.spring.web.initializr.service.api.ResourcePersistableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;

public interface RpService<R extends ResourcePersistable<I>, I extends Serializable, D> extends ResourcePersistableService<D, I> {

  Logger RP_SERVICE_LOGGER = LoggerFactory.getLogger(RpService.class);

  CrudRepository<R, I> getRepository();

  Function<R, D> getEntityToDtoConverter();

  Function<D, R> getDtoToEntityConverter();

  @Override
  default D findNullable(I id) {
    return findOptional(id).orElse(null);
  }

  @Override
  default D findOrThrow(I id) {
    return findOptional(id).orElseThrow(() -> new RpMissingResourceException(id));
  }

  @SuppressWarnings("unchecked")
  @Override
  default Optional<D> findOptional(I id) {
    try {
      return getRepository().findById(id).map(getEntityToDtoConverter());
    } catch (NoSuchMethodError e) {
      RP_SERVICE_LOGGER.warn("Caught (expected) NoSuchMethodError. "
          + "Most probably you are using a spring-boot-starter-parent version lower than 2.0.0.RELEASE. "
          + "It is strongly advised to override this method and use findOne() instead "
          + "or update your spring-boot-starter-parent-version to (at least) 2.0.0.RELEASE.");
      try {
        // Fallback for spring-boot-starter-parent version less than 2.0.0-RELEASE
        Method findOneMethod = getRepository().getClass().getMethod("findOne", Serializable.class);
        return Optional.ofNullable(getEntityToDtoConverter().apply((R) findOneMethod.invoke(getRepository(), id)));
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
        RP_SERVICE_LOGGER.error("Fallback mechanism for RpService::findOptional failed. Message: {}", e.getMessage());
        throw new RuntimeException("Caught unexpected NoSuchMethodException during reflection.");
      }
    }
  }

  @Override
  default Stream<D> findAll() {
    return StreamSupport.stream(getRepository().findAll().spliterator(), false)
        .map(getEntityToDtoConverter());
  }

  @Override
  default D insert(D resourcePersistableDto) {
    R resourcePersistable = getDtoToEntityConverter().apply(resourcePersistableDto);
    Optional.ofNullable(resourcePersistable.getRpId())
        .flatMap(id -> findOptional(resourcePersistable.getRpId()))
        .ifPresent(e -> {
          throw new RpDuplicateResourceException(resourcePersistable.getClass().getSimpleName(),
              "rpId", resourcePersistable.getRpId().toString());
        });
    return getEntityToDtoConverter().apply(getRepository().save(resourcePersistable));
  }

  @Override
  default D update(D resourcePersistableDto) {
    R resourcePersistable = getDtoToEntityConverter().apply(resourcePersistableDto);
    findOrThrow(resourcePersistable.getRpId());
    return getEntityToDtoConverter().apply(getRepository().save(resourcePersistable));
  }

  @Override
  default void deleteById(I resourcePersistableId) {
    findOrThrow(resourcePersistableId);
    getRepository().deleteById(resourcePersistableId);
  }

}
