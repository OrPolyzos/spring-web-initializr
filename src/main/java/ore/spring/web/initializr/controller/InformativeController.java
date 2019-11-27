package ore.spring.web.initializr.controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;


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
     * Add success message to the model
     *
     * @param model the Model
     */
    default void sendSuccessMessage(Model model) {
        model.addAttribute(getInfoMessageHolder(), "Action was successful");
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
     * Add binding errors to the redirect attributes
     *
     * @param model         the RedirectAttributes
     * @param bindingResult the BindingResult
     * @param itemHolder    the ItemHolder
     * @param actualObject  the ActualObject
     */
    default void sendBindingErrors(Model model, BindingResult bindingResult, String itemHolder, Object actualObject) {
        model.addAttribute(BINDING_RESULT_PREFIX + itemHolder, bindingResult);
        model.addAttribute(itemHolder, actualObject);
    }

}
