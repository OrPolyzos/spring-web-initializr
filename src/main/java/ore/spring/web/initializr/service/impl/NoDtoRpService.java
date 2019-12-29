package ore.spring.web.initializr.service.impl;

import java.io.Serializable;
import java.util.function.Function;
import ore.spring.web.initializr.domain.ResourcePersistable;

public interface NoDtoRpService<R extends ResourcePersistable<I>, I extends Serializable> extends RpService<R, I, R> {

  @Override
  default Function<R, R> getEntityToDtoConverter() {
    return Function.identity();
  }

  @Override
  default Function<R, R> getDtoToEntityConverter() {
    return Function.identity();
  }
}
