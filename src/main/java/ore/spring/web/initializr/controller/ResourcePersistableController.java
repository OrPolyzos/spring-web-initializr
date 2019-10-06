package ore.spring.web.initializr.controller;

import ore.spring.web.initializr.domain.ResourcePersistable;
import ore.spring.web.initializr.service.ResourcePersistableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourcePersistableController.class.getName());

    /**
     * The ResourcePersistableForm Class is used to instantiate a ResourcePersistableForm
     * The ResourcePersistableForm is used to create and edit a ResourcePersistable
     */
    private final Class<RF> resourcePersistableFormClass;

    /**
     * The ResourcePersistableSearchForm Class is used to instantiate a ResourcePersistableSearchForm
     * The ResourcePersistableSearchForm is used to search for ResourcePersistables by criteria
     */
    private final Class<RSF> resourcePersistableSearchFormClass;

    /**
     * The ResourcePersistableService is used to Create, Read, Update, Delete ResourcePersistables
     */
    private ResourcePersistableService<R, RSF, ID> resourcePersistableService;

    /**
     * @param resourcePersistableService the ResourcePersistableService
     */
    @SuppressWarnings("unchecked")
    public ResourcePersistableController(ResourcePersistableService<R, RSF, ID> resourcePersistableService) {
        this.resourcePersistableService = resourcePersistableService;

        Type[] types = ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments();
        this.resourcePersistableFormClass = (Class<RF>) types[2];
        this.resourcePersistableSearchFormClass = (Class<RSF>) types[3];
    }

    @InitBinder
    private void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * Consider this the GET "/resourcePersistables" endpoint
     * It is meant to serve the page that will hold
     * 1) the ResourcePersistableForm in order to create a ResourcePersistable
     * 2) the ResourcePersistableSearchForm in order to search for ResourcePersistables with criteria
     * ResourcePersistableForm and ResourcePersistableSearchForm will be added to the Model independently, if they do not exist in the Model already
     *
     * @param model the Model
     * @return the ResourcePersistableBaseView
     */
    public String getResourcePersistableBaseView(Model model) {
        if (!model.containsAttribute(getResourcePersistableFormHolder())) {
            model.addAttribute(getResourcePersistableFormHolder(), createInstanceOf(resourcePersistableFormClass));
        }
        if (!model.containsAttribute(getResourcePersistableSearchFormHolder())) {
            model.addAttribute(getResourcePersistableSearchFormHolder(), createInstanceOf(resourcePersistableSearchFormClass));
        }
        return getResourcePersistableBaseViewPath();
    }

    /**
     * Consider this the POST "/resourcePersistables" endpoint
     * It is meant to accept a ResourcePersistableForm in the body of the HttpRequest, in order to insert a ResourcePersistable
     * It will then serve the ResourcePersistableBaseView, along with an informative message
     *
     * @param resourcePersistableForm the ResourcePersistableForm
     * @param bindingResult           the BindingResult
     * @param model                   the Model
     * @param redirectAttributes      the RedirectAttributes
     * @return the ResourcePersistableBaseView
     */
    public String createResourcePersistable(RF resourcePersistableForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            sendBindingErrors(redirectAttributes, bindingResult, getResourcePersistableFormHolder(), resourcePersistableForm);
            return redirectTo(getResourcePersistableBaseUri());
        }

        resourcePersistableService.insert(resourcePersistableFormToResourcePersistable(resourcePersistableForm));
        sendInfoMessage(model, getSuccessfulCreateMessage());
        model.asMap().remove(getResourcePersistableFormHolder());
        return getResourcePersistableBaseView(model);
    }

    /**
     * Consider this the DELETE "/resourcePersistables/{resourcePersistableId}" endpoint
     * It is meant to accept a ResourcePersistableId in a URI parameter and delete the corresponding ResourcePersistable
     * It will then serve the ResourcePersistableBaseView, along with an informative message
     *
     * @param resourcePersistableId the ResourcePersistableId
     * @param model                 the Model
     * @return the ResourcePersistableBaseView
     */
    public String deleteResourcePersistable(ID resourcePersistableId, Model model) {
        resourcePersistableService.deleteById(resourcePersistableId);
        sendInfoMessage(model, getSuccessfulDeleteMessage());
        return getResourcePersistableBaseView(model);
    }

    /**
     * Consider this the GET "/resourcePersistables/{resourcePersistableId}" endpoint
     * It is meant to accept a ResourcePersistableId in a URI parameter and find the corresponding ResourcePersistable
     * It will then transform the ResourcePersistable to a ResourcePersistableForm, add it to the Model and serve the ResourcePersistableEditView
     *
     * @param resourcePersistableId the ResourcePersistableId
     * @param model                 the Model
     * @return the ResourcePersistableEditView
     */
    public String getResourcePersistableEditView(ID resourcePersistableId, Model model) {
        if (model.containsAttribute(getResourcePersistableFormHolder())) {
            return getResourcePersistableEditViewPath();
        }

        RF resourcePersistableForm = resourcePersistableToResourcePersistableForm(resourcePersistableService.findOrThrow(resourcePersistableId));
        model.addAttribute(getResourcePersistableFormHolder(), resourcePersistableForm);
        return getResourcePersistableEditViewPath();

    }

    /**
     * Consider this the PUT "/resourcePersistables/{resourcePersistableId}" endpoint
     * It is meant to accept a ResourcePersistableId in a URI parameter and a ResourcePersistableForm in the body of the HttpRequest
     * It will update the corresponding ResourcePersistable
     * It will then serve the ResourcePersistableBaseView, along with an informative message
     *
     * @param resourcePersistableId   the ResourcePersistableId
     * @param resourcePersistableForm the ResourcePersistableForm
     * @param bindingResult           the BindingResult
     * @param model                   the Model
     * @param redirectAttributes      the RedirectAttributes
     * @return the ResourcePersistableBaseView
     */
    public String editResourcePersistable(ID resourcePersistableId, RF resourcePersistableForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            sendBindingErrors(redirectAttributes, bindingResult, getResourcePersistableFormHolder(), resourcePersistableForm);
            return redirectTo(String.format("%s/%s", getResourcePersistableBaseUri(), resourcePersistableId));
        }
        resourcePersistableService.update(resourcePersistableFormToResourcePersistable(resourcePersistableForm));
        sendInfoMessage(redirectAttributes, getSuccessfulUpdateMessage());
        model.asMap().remove(getResourcePersistableFormHolder());
        return getResourcePersistableBaseView(model);
    }

    /**
     * Consider this the GET "/resourcePersistables/search" endpoint
     * It is meant to accept a ResourcePersistableSearchForm in the body of the HttpRequest
     * It will use the ResourcePersistableSearchForm to find all the ResourcePersistables that meet the criteria
     * It will then add the ResourcePersistables List to the Model and serve the ResourcePersistableBaseView, along with an informative message
     *
     * @param resourcePersistableSearchForm the ResourcePersistableSearchForm
     * @param bindingResult                 the BindingResult
     * @param model                         the Model
     * @param redirectAttributes            the RedirectAttributes
     * @return the ResourcePersistableBaseView
     */
    public String searchResourcePersistablesBy(RSF resourcePersistableSearchForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            sendBindingErrors(redirectAttributes, bindingResult, getResourcePersistableSearchFormHolder(), resourcePersistableSearchForm);
            redirectTo(getResourcePersistableBaseUri());
        }
        model.addAttribute(getResourcePersistableListHolder(), resourcePersistableService.searchBy(resourcePersistableSearchForm));
        return getResourcePersistableBaseView(model);
    }

    private <E> E createInstanceOf(Class<E> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            LOGGER.error("Failed to instantiate: {} | Message: {}", clazz.getName(), e.getMessage());
            LOGGER.warn("Consider declaring a default constructor for Class: {}", clazz.getName());
            return null;
        }
    }

    /**
     * Gets the SuccessfulCreateMessage, used when a ResourcePersistable is successfully created
     *
     * @return the SuccessfulCreateMessage
     */
    protected String getSuccessfulCreateMessage() {
        return "Created successfully!";
    }

    /**
     * Gets the SuccessfulUpdateMessage, used when a ResourcePersistable is successfully updated
     *
     * @return the SuccessfulUpdateMessage
     */
    protected String getSuccessfulUpdateMessage() {
        return "Updated successfully";
    }

    /**
     * Gets the SuccessfulDeleteMessage, used when a ResourcePersistable is successfully deleted
     *
     * @return the SuccessfulDeleteMessage
     */
    protected String getSuccessfulDeleteMessage() {
        return "Deleted successfully!";
    }

    /**
     * Creates a ResourcePersistable from a ResourcePersistableForm
     *
     * @param resourcePersistableForm the ResourcePersistableForm
     * @return the ResourcePersistable
     */
    protected abstract R resourcePersistableFormToResourcePersistable(RF resourcePersistableForm);

    /**
     * Creates a ResourcePersistableForm from a ResourcePersistable
     *
     * @param resourcePersistable the ResourcePersistable
     * @return the ResourcePersistableForm
     */
    protected abstract RF resourcePersistableToResourcePersistableForm(R resourcePersistable);

    /**
     * Gets the base URI for the ResourcePersistableController
     *
     * @return the ResourcePersistableBaseUri
     */
    protected abstract String getResourcePersistableBaseUri();

    /**
     * Gets the path to the ResourcePersistableBaseView (Freemarker) file
     *
     * @return the ResourcePersistableBaseViewPath
     */
    protected abstract String getResourcePersistableBaseViewPath();

    /**
     * Gets the path to the ResourcePersistableEditView (Freemarker) file
     *
     * @return the ResourcePersistableEditViewPath
     */
    protected abstract String getResourcePersistableEditViewPath();

    /**
     * Gets the ResourcePersistableForm variable name used in the Views
     *
     * @return the ResourcePersistableFormHolder
     */
    protected abstract String getResourcePersistableFormHolder();

    /**
     * Gets the ResourcePersistableSearchForm variable name used in the Views
     *
     * @return the ResourcePersistableSearchFormHolder
     */
    protected abstract String getResourcePersistableSearchFormHolder();

    /**
     * Gets the ResourcePersistableList variable name used in the Views
     *
     * @return the ResourcePersistableListHolder
     */
    protected abstract String getResourcePersistableListHolder();

}
