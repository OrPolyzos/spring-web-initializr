package ore.spring.web.initializr.controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * The interface Informative controller.
 */
public interface InformativeController {

    /**
     * The constant INFO_MESSAGE_HOLDER.
     */
    String INFO_MESSAGE_HOLDER = "infoMessage";
    /**
     * The constant ERROR_MESSAGE_HOLDER.
     */
    String ERROR_MESSAGE_HOLDER = "errorMessage";
    /**
     * The constant BINDING_RESULT_PREFIX.
     */
    String BINDING_RESULT_PREFIX = "org.springframework.validation.BindingResult.";

    /**
     * Redirect to string.
     *
     * @param uri the uri
     * @return the string
     */
    default String redirectTo(String uri) {
        return "redirect:" + uri;
    }

    /**
     * Send info message.
     *
     * @param model   the model
     * @param message the message
     */
    default void sendInfoMessage(Model model, String message) {
        model.addAttribute(INFO_MESSAGE_HOLDER, message);
    }

    /**
     * Send error message.
     *
     * @param model   the model
     * @param message the message
     */
    default void sendErrorMessage(Model model, String message) {
        model.addAttribute(ERROR_MESSAGE_HOLDER, message);
    }

    /**
     * Redirect info message.
     *
     * @param redirectAttributes the redirect attributes
     * @param message            the message
     */
    default void redirectInfoMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute(INFO_MESSAGE_HOLDER, message);
    }

    /**
     * Redirect error message.
     *
     * @param redirectAttributes the redirect attributes
     * @param message            the message
     */
    default void redirectErrorMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute(ERROR_MESSAGE_HOLDER, message);
    }

    /**
     * Send binding errors.
     *
     * @param redirectAttributes the redirect attributes
     * @param bindingResult      the binding result
     * @param itemHolder         the item holder
     * @param actualObject       the actual object
     */
    default void sendBindingErrors(RedirectAttributes redirectAttributes, BindingResult bindingResult, String itemHolder, Object actualObject) {
        redirectAttributes.addFlashAttribute(BINDING_RESULT_PREFIX + itemHolder, bindingResult);
        redirectAttributes.addFlashAttribute(itemHolder, actualObject);
    }

}
