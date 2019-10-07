package ore.spring.web.initializr.controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * The interface InformativeController
 */
public interface InformativeController {

    /**
     * The constant BINDING_RESULT_PREFIX
     */
    String BINDING_RESULT_PREFIX = "org.springframework.validation.BindingResult.";

    /**
     * Gets InfoMessageHolder
     *
     * @return the InfoMessageHolder
     */
    default String getInfoMessageHolder() {
        return "infoMessage";
    }

    /**
     * Gets ErrorMessageHolder
     *
     * @return the ErrorMessageHolder
     */
    default String getErrorMessageHolder() {
        return "errorMessage";
    }

    /**
     * Add the redirect prefix needed to do a redirect request
     *
     * @param uri the uri to redirect
     * @return the redirect string
     */
    default String redirectTo(String uri) {
        return "redirect:" + uri;
    }

    /**
     * Add information message to the model
     *
     * @param model   the Model
     * @param message the InfoMessage
     */
    default void sendInfoMessage(Model model, String message) {
        model.addAttribute(getInfoMessageHolder(), message);
    }

    /**
     * Add error message to the model
     *
     * @param model   the Model
     * @param message the ErrorMessage
     */
    default void sendErrorMessage(Model model, String message) {
        model.addAttribute(getErrorMessageHolder(), message);
    }

    /**
     * Add information message to the redirect attributes
     *
     * @param redirectAttributes the RedirectAttributes
     * @param message            the InfoMessage
     */
    default void redirectInfoMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute(getInfoMessageHolder(), message);
    }

    /**
     * Add error message to the redirect attributes
     *
     * @param redirectAttributes the RedirectAttributes
     * @param message            the ErrorMessage
     */
    default void redirectErrorMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute(getErrorMessageHolder(), message);
    }

    /**
     * Add binding errors to the redirect attributes
     *
     * @param redirectAttributes the RedirectAttributes
     * @param bindingResult      the BindingResult
     * @param itemHolder         the ItemHolder
     * @param actualObject       the ActualObject
     */
    default void sendBindingErrors(RedirectAttributes redirectAttributes, BindingResult bindingResult, String itemHolder, Object actualObject) {
        redirectAttributes.addFlashAttribute(BINDING_RESULT_PREFIX + itemHolder, bindingResult);
        redirectAttributes.addFlashAttribute(itemHolder, actualObject);
    }

}
