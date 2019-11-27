package ore.spring.web.initializr.service.api;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface ResourcePersistableService<R, ID extends Serializable> {

    R findNullable(ID id);

    R findOrThrow(ID id);

    Optional<R> findOptional(ID id);

    Stream<R> findAll();

    Stream<R> findAllBy(Map<String, String> criteria);

    R insert(R resourcePersistable);

    R update(R resourcePersistable);

    void deleteById(ID id);

}
