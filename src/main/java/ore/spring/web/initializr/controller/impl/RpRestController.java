package ore.spring.web.initializr.controller.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collectors;
import ore.spring.web.initializr.controller.api.ResourcePersistableRestController;
import ore.spring.web.initializr.service.api.ResourcePersistableService;

public interface RpRestController<D, I extends Serializable> extends ResourcePersistableRestController<D, I> {

  ResourcePersistableService<D, I> getService();

  @Override
  default D create(final D resourcePersistable) {
    return getService().insert(resourcePersistable);
  }

  @Override
  default D read(final I id) {
    return getService().findOrThrow(id);
  }

  @Override
  default Collection<D> readAll() {
    return getService().findAll().collect(Collectors.toList());
  }

  @Override
  default D update(final D resourcePersistable) {
    return getService().update(resourcePersistable);
  }

  @Override
  default void delete(final I id) {
    getService().deleteById(id);
  }
}
