package ore.spring.web.initializr.exception.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpValidationError {

    private HttpStatus status;
    private String message;
    private List<RpFieldError> fieldErrors;
}
