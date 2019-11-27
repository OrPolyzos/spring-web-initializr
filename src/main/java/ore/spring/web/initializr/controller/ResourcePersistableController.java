package ore.spring.web.initializr.controller;

import ore.spring.web.initializr.domain.ResourcePersistable;

import java.io.Serializable;

/**
 * The ResourcePersistableController is an intermediate layer of between the View (Freemarker) and the Service (ResourceService)
 * It is meant to be extended by a concrete class
 *
 * @param <R>   the type parameter used for the ResourcePersistable
 * @param <ID>  the type parameter used for the Id of the ResourcePersistable
 * @param <RF>  the type parameter used for the ResourcePersistableForm
 * @param <RSF> the type parameter used for the ResourcePersistableSearchForm
 */
public abstract class ResourcePersistableController<R extends ResourcePersistable<ID>, ID extends Serializable, RF, RSF> implements InformativeController {
//     * Handle RPRuntimeExceptions
//     *
//     * @param exception the RPRuntimeException
//     * @param model     the Model
//     * @return the ResourcePersistableBaseView
//     */
//    @ExceptionHandler({RPRuntimeException.class})
//    public String handleRPRuntimeException(RPRuntimeException exception, Model model) {
//        sendErrorMessage(model, exception.getMessage());
//        return getResourcePersistableBaseView(model);
//    }
//
//    /**
//     * Handle Exception
//     *
//     * @param exception the Exception
//     * @param model     the Model
//     * @return the ResourcePersistableBaseView
//     */
//    @ExceptionHandler({Exception.class})
//    public String handleException(Exception exception, Model model) {
//        LOGGER.error(exception.getMessage());
//        sendErrorMessage(model, GENERIC_ERROR_MESSAGE);
//        return getResourcePersistableBaseView(model);
//    }
//
//    private <E> E createInstanceOf(Class<E> clazz) {
//        try {
//            return clazz.getDeclaredConstructor().newInstance();
//        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
//            LOGGER.error("Failed to instantiate: {} | Message: {}", clazz.getName(), e.getMessage());
//            LOGGER.warn("Consider declaring a default constructor for Class: {}", clazz.getName());
//            return null;
//        }
//    }
//
//    /**
//     * Gets the SuccessfulCreateMessage, used when a ResourcePersistable is successfully created
//     *
//     * @return the SuccessfulCreateMessage
//     */
//    protected String getSuccessfulCreateMessage() {
//        return "Created successfully!";
//    }
//
//    /**
//     * Gets the SuccessfulUpdateMessage, used when a ResourcePersistable is successfully updated
//     *
//     * @return the SuccessfulUpdateMessage
//     */
//    protected String getSuccessfulUpdateMessage() {
//        return "Updated successfully";
//    }
//
//    /**
//     * Gets the SuccessfulDeleteMessage, used when a ResourcePersistable is successfully deleted
//     *
//     * @return the SuccessfulDeleteMessage
//     */
//    protected String getSuccessfulDeleteMessage() {
//        return "Deleted successfully!";
//    }
//
//    /**
//     * Creates a ResourcePersistable from a ResourcePersistableForm
//     *
//     * @param resourcePersistableForm the ResourcePersistableForm
//     * @return the ResourcePersistable
//     */
//    protected abstract R resourcePersistableFormToResourcePersistable(RF resourcePersistableForm);
//
//    /**
//     * Creates a ResourcePersistableForm from a ResourcePersistable
//     *
//     * @param resourcePersistable the ResourcePersistable
//     * @return the ResourcePersistableForm
//     */
//    protected abstract RF resourcePersistableToResourcePersistableForm(R resourcePersistable);
//
//    /**
//     * Gets the base URI for the ResourcePersistableController
//     *
//     * @return the ResourcePersistableBaseUri
//     */
//    protected abstract String getResourcePersistableBaseUri();
//
//    /**
//     * Gets the path to the ResourcePersistableBaseView (Freemarker) file
//     *
//     * @return the ResourcePersistableBaseViewPath
//     */
//    protected abstract String getResourcePersistableBaseViewPath();
//
//    /**
//     * Gets the path to the ResourcePersistableEditView (Freemarker) file
//     *
//     * @return the ResourcePersistableEditViewPath
//     */
//    protected abstract String getResourcePersistableEditViewPath();
//
//    /**
//     * Gets the ResourcePersistableForm variable name used in the Views
//     *
//     * @return the ResourcePersistableFormHolder
//     */
//    protected abstract String getResourcePersistableFormHolder();
//
//    /**
//     * Gets the ResourcePersistableSearchForm variable name used in the Views
//     *
//     * @return the ResourcePersistableSearchFormHolder
//     */
//    protected abstract String getResourcePersistableSearchFormHolder();
//
//    /**
//     * Gets the ResourcePersistableList variable name used in the Views
//     *
//     * @return the ResourcePersistableListHolder
//     */
//    protected abstract String getResourcePersistableListHolder();

}
