package ore.spring.web.initializr.exception.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpError {

  private HttpStatus errorStatus;
  private String message;

  public int getErrorStatus() {
    return errorStatus.value();
  }

  public HttpStatus getErrorHttpStatus() {
    return errorStatus;
  }
}
