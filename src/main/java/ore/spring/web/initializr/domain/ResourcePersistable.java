package ore.spring.web.initializr.domain;

import java.io.Serializable;

public interface ResourcePersistable<I extends Serializable> {

  I getRpId();
}
