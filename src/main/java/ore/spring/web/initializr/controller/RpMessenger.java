package ore.spring.web.initializr.controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

public interface RpMessenger {

  default String getInfoMessageHolder() {
    return "infoMessage";
  }

  default String getErrorMessageHolder() {
    return "errorMessage";
  }

  default void sendSuccessMessage(Model model) {
    model.addAttribute(getInfoMessageHolder(), "Success!");
  }

  default void sendErrorMessage(Model model) {
    model.addAttribute(getErrorMessageHolder(), "Error!");
  }

  default void sendBindingErrors(Model model, BindingResult bindingResult, String itemHolder, Object actualObject) {
    model.addAttribute(BindingResult.MODEL_KEY_PREFIX + itemHolder, bindingResult);
    model.addAttribute(itemHolder, actualObject);
  }

}
