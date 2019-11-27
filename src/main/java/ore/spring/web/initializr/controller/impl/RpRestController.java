package ore.spring.web.initializr.controller.impl;

import ore.spring.web.initializr.controller.api.ResourcePersistableRestController;
import ore.spring.web.initializr.service.api.ResourcePersistableService;

import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collectors;

public interface RpRestController<D, ID extends Serializable> extends ResourcePersistableRestController<D, ID> {

    ResourcePersistableService<D, ID> getService();

    @Override
    default D create(final D resourcePersistable) {
        return getService().insert(resourcePersistable);
    }

    @Override
    default D read(final ID id) {
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
    default void delete(final ID id) {
        getService().deleteById(id);
    }
}
