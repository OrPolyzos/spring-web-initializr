package ore.spring.web.initializr.controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface InformativeController {

    String INFO_MESSAGE_HOLDER = "infoMessage";
    String ERROR_MESSAGE_HOLDER = "errorMessage";
    String BINDING_RESULT_PREFIX = "org.springframework.validation.BindingResult.";

    default String redirectTo(String uri) {
        return "redirect:" + uri;
    }

    default void sendInfoMessage(Model model, String message) {
        model.addAttribute(INFO_MESSAGE_HOLDER, message);
    }

    default void sendErrorMessage(Model model, String message) {
        model.addAttribute(ERROR_MESSAGE_HOLDER, message);
    }

    default void redirectInfoMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute(INFO_MESSAGE_HOLDER, message);
    }

    default void redirectErrorMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute(ERROR_MESSAGE_HOLDER, message);
    }

    default void sendBindingErrors(RedirectAttributes redirectAttributes, BindingResult bindingResult, String itemHolder, Object actualObject) {
        redirectAttributes.addFlashAttribute(BINDING_RESULT_PREFIX + itemHolder, bindingResult);
        redirectAttributes.addFlashAttribute(itemHolder, actualObject);
    }

}
