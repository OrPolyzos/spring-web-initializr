package ore.spring.web.initializr.service.impl;

import ore.spring.web.initializr.domain.ResourcePersistable;
import ore.spring.web.initializr.exception.runtime.RPRuntimeException;
import ore.spring.web.initializr.exception.runtime.RPRuntimeNotFoundException;
import ore.spring.web.initializr.service.api.RpService;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface DtoRpService<R extends ResourcePersistable<ID>, ID extends Serializable, D> extends RpService<D, ID> {

    CrudRepository<R, ID> getRepository();

    Function<R, D> getEntityToDtoConverter();

    Function<D, R> getDtoToEntityConverter();

    @Override
    default D find(ID id) {
        return findOptional(id).orElse(null);
    }

    @Override
    default D findOrThrow(ID id) {
        return findOptional(id).orElseThrow(() -> new RPRuntimeNotFoundException(id));
    }

    @SuppressWarnings("unchecked")
    @Override
    default Optional<D> findOptional(ID id) {
        try {
            return getRepository().findById(id).map(getEntityToDtoConverter());
        } catch (NoSuchMethodError e) {
//            LOGGER.warn("Caught (expected) NoSuchMethodError. Most probably you are using a spring-boot-starter-parent version lower than 2.0.0.RELEASE. It is strongly advised to override this method and use findOne() instead or update your spring-boot-starter-parent-version to (at least) 2.0.0.RELEASE.");
            try {
                // Fallback for spring-boot-starter-parent version less than 2.0.0-RELEASE
                Method findOneMethod = getRepository().getClass().getMethod("findOne", Serializable.class);
                return Optional.ofNullable(getEntityToDtoConverter().apply((R) findOneMethod.invoke(getRepository(), id)));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
//                LOGGER.error(e.getMessage());
                throw new RPRuntimeException("Caught unexpected NoSuchMethodException during reflection.");
            }
        }
    }

    @Override
    default Stream<D> findAll() {
        return StreamSupport.stream(getRepository().findAll().spliterator(), false)
                .map(getEntityToDtoConverter());
    }

    @Override
    default Stream<D> findAllBy(Map<String, String> criteria) {
        return StreamSupport.stream(getRepository().findAll().spliterator(), false)
                .map(getEntityToDtoConverter());

    }

    @Override
    default D insert(D resourcePersistable) {
        return getEntityToDtoConverter().apply(getRepository().save(getDtoToEntityConverter().apply(resourcePersistable)));
    }

    @Override
    default D update(D resourcePersistable) {
        return getEntityToDtoConverter().apply(getRepository().save(getDtoToEntityConverter().apply(resourcePersistable)));
    }

    @Override
    default void deleteById(ID resourcePersistableId) {
        getRepository().deleteById(resourcePersistableId);
    }

}
