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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface NoDtoRpService<R extends ResourcePersistable<ID>, ID extends Serializable> extends RpService<R, ID> {

    CrudRepository<R, ID> getRepository();

    @Override
    default R find(ID id) {
        return findOptional(id).orElse(null);
    }

    @Override
    default R findOrThrow(ID id) {
        return findOptional(id)
                .orElseThrow(() -> new RPRuntimeNotFoundException(id));
    }

    @SuppressWarnings("unchecked")
    @Override
    default Optional<R> findOptional(ID id) {
        try {
            return getRepository().findById(id);
        } catch (NoSuchMethodError e) {
//            LOGGER.warn("Caught (expected) NoSuchMethodError. Most probably you are using a spring-boot-starter-parent version lower than 2.0.0.RELEASE. It is strongly advised to override this method and use findOne() instead or update your spring-boot-starter-parent-version to (at least) 2.0.0.RELEASE.");
            try {
                // Fallback for spring-boot-starter-parent version less than 2.0.0-RELEASE
                Method findOneMethod = getRepository().getClass().getMethod("findOne", Serializable.class);
                return Optional.ofNullable((R) findOneMethod.invoke(getRepository(), id));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
//                LOGGER.error(e.getMessage());
                throw new RPRuntimeException("Caught unexpected NoSuchMethodException during reflection.");
            }
        }
    }

    @Override
    default Stream<R> findAll() {
        return StreamSupport.stream(getRepository().findAll().spliterator(), false);
    }

    @Override
    default Stream<R> searchBy(Map<String, String> criteria) {
        return StreamSupport.stream(getRepository().findAll().spliterator(), false);
    }

    @Override
    default R insert(R resourcePersistable) {
        return getRepository().save(resourcePersistable);
    }

    @Override
    default R update(R resourcePersistable) {
        return getRepository().save(resourcePersistable);
    }

    @Override
    default void deleteById(ID resourcePersistableId) {
        getRepository().deleteById(resourcePersistableId);
    }
}
