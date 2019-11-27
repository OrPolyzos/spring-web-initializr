package ore.spring.web.initializr.exception;

import lombok.AllArgsConstructor;
import ore.spring.web.initializr.exception.base.RpRuntimeException;

@AllArgsConstructor
public class RpForbiddenResourceException extends RpRuntimeException {

    private static final String MESSAGE = "User with id [%s] has no access to the resource with id [%s]";
    private final Long id;
    private final Long ownerId;

    public String getDisplayMessage() {
        return String.format(MESSAGE, this.ownerId, this.id);
    }
}
