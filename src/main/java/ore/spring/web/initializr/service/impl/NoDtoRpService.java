package ore.spring.web.initializr.service.impl;

import ore.spring.web.initializr.domain.ResourcePersistable;

import java.io.Serializable;
import java.util.function.Function;

public interface NoDtoRpService<R extends ResourcePersistable<ID>, ID extends Serializable> extends DtoRpService<R, ID, R> {

    @Override
    default Function<R, R> getEntityToDtoConverter() {
        return Function.identity();
    }

    @Override
    default Function<R, R> getDtoToEntityConverter() {
        return Function.identity();
    }
}
