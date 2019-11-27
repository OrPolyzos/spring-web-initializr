package ore.spring.web.initializr.util;

import ore.spring.web.initializr.exception.RpTypedParameterException;

public interface TypedParameterExtractor {

    default Class getClassFromTypeParams(int typeIndex) {
        try {
            return this.getClass().getTypeParameters()[typeIndex].getClass();
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            throw new RpTypedParameterException(typeIndex, this.getClass().getTypeParameters().length);
        }
    }
}
