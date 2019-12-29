package ore.spring.web.initializr.exception.error;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpValidationError {

  private HttpStatus status;
  private String message;
  private List<RpFieldError> fieldErrors;
}
