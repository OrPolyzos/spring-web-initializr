package ore.spring.web.initializr.service.api;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

public interface ResourcePersistableService<R, I extends Serializable> {

  R findNullable(I id);

  R findOrThrow(I id);

  Optional<R> findOptional(I id);

  Stream<R> findAll();

  R insert(R resourcePersistable);

  R update(R resourcePersistable);

  void deleteById(I id);

}
