package ore.spring.web.initializr.controller;

import ore.spring.web.initializr.domain.ResourcePersistable;
import ore.spring.web.initializr.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ore.spring.web.initializr.exception.ResourceException;
import ore.spring.web.initializr.exception.ResourceNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * The type Resource controller.
 *
 * @param <R>   the type parameter
 * @param <ID>  the type parameter
 * @param <RF>  the type parameter
 * @param <RSF> the type parameter
 */
public abstract class ResourceController<R extends ResourcePersistable<ID>, ID extends Serializable, RF, RSF> implements InformativeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class.getName());

    /**
     * The Resource class.
     */
    protected final Class<R> resourceClass;
    /**
     * The Resource form class.
     */
    protected final Class<RF> resourceFormClass;
    /**
     * The Resource search form class.
     */
    protected final Class<RSF> resourceSearchFormClass;
    /**
     * The Resource service.
     */
    protected ResourceService<R, RSF, ID> resourceService;

    /**
     * Instantiates a new Resource controller.
     *
     * @param resourceService the resource service
     */
    @SuppressWarnings("unchecked")
    public ResourceController(ResourceService<R, RSF, ID> resourceService) {
        this.resourceService = resourceService;

        Type[] types = ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments();
        this.resourceClass = (Class<R>) types[0];
        this.resourceFormClass = (Class<RF>) types[2];
        this.resourceSearchFormClass = (Class<RSF>) types[3];
    }

    @InitBinder
    private void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * Gets resource view.
     *
     * @param model the model
     * @return the resource view
     */
    public String getResourceView(Model model) {
        if (!model.containsAttribute(getResourceFormHolder())) {
            model.addAttribute(getResourceFormHolder(), createInstanceOf(resourceFormClass));
        }
        if (!model.containsAttribute(getResourceSearchFormHolder())) {
            model.addAttribute(getResourceSearchFormHolder(), createInstanceOf(resourceSearchFormClass));
        }
        return getResourceViewPath();
    }

    /**
     * Create resource string.
     *
     * @param resourceForm       the resource form
     * @param bindingResult      the binding result
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @return the string
     */
    public String createResource(RF resourceForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            sendBindingErrors(redirectAttributes, bindingResult, getResourceFormHolder(), resourceForm);
            return redirectTo(getResourceBaseUri());
        }

        try {
            R resourceToSave = resourceFormToResource(resourceForm);
            resourceService.insert(resourceToSave);
            sendInfoMessage(model, getResourceCreatedMessage());
            model.asMap().remove(getResourceFormHolder());
            return getResourceView(model);
        } catch (ResourceException exception) {
            redirectAttributes.addFlashAttribute(getResourceFormHolder(), resourceForm);
            redirectErrorMessage(redirectAttributes, exception.getMessage());
            return redirectTo(getResourceBaseUri());
        }
    }

    /**
     * Delete resource string.
     *
     * @param resourceId         the resource id
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @return the string
     */
    public String deleteResource(ID resourceId, Model model, RedirectAttributes redirectAttributes) {
        try {
            resourceService.deleteById(resourceId);
            sendInfoMessage(model, getResourceDeletedMessage());
            return getResourceView(model);
        } catch (ResourceException exception) {
            redirectErrorMessage(redirectAttributes, exception.getMessage());
            return redirectTo(getResourceBaseUri());
        }
    }

    /**
     * Gets edit resource view.
     *
     * @param resourceId         the resource id
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @return the edit resource view
     */
    public String getEditResourceView(ID resourceId, Model model, RedirectAttributes redirectAttributes) {
        if (model.containsAttribute(getResourceFormHolder())) {
            return getEditResourceViewPath();
        }
        try {
            R resource = resourceService.findOrThrow(resourceId);
            RF resourceForm = resourceToResourceForm(resource);
            model.addAttribute(getResourceFormHolder(), resourceForm);
            return getEditResourceViewPath();
        } catch (ResourceNotFoundException exception) {
            redirectErrorMessage(redirectAttributes, exception.getMessage());
            return redirectTo(getResourceBaseUri());
        }
    }

    /**
     * Edit resource string.
     *
     * @param resourceId         the resource id
     * @param resourceForm       the resource form
     * @param bindingResult      the binding result
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @param httpServletRequest the http servlet request
     * @return the string
     */
    public String editResource(ID resourceId, RF resourceForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            sendBindingErrors(redirectAttributes, bindingResult, getResourceFormHolder(), resourceForm);
            return redirectTo(httpServletRequest.getRequestURI());
        }
        try {
            R user = resourceFormToResource(resourceForm);
            resourceService.update(user);
            redirectInfoMessage(redirectAttributes, getResourceUpdatedMessage());
            return redirectTo(getResourceBaseUri());
        } catch (ResourceException exception) {
            redirectAttributes.addFlashAttribute(getResourceFormHolder(), resourceForm);
            redirectErrorMessage(redirectAttributes, exception.getMessage());
            return redirectTo(httpServletRequest.getRequestURI());
        }
    }

    /**
     * Search by string.
     *
     * @param resourceSearchForm the resource search form
     * @param bindingResult      the binding result
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @return the string
     */
    public String searchBy(RSF resourceSearchForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            sendBindingErrors(redirectAttributes, bindingResult, getResourceSearchFormHolder(), resourceSearchForm);
            redirectTo(getResourceBaseUri());
        }
        model.addAttribute(getResourceListHolder(), resourceService.searchBy(resourceSearchForm));
        return getResourceView(model);
    }

    private <E> E createInstanceOf(Class<E> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("Failed to instantiate: {} | Message: {}", clazz.getName(), e.getMessage());
            return null;
        }
    }

    /**
     * Gets resource created message.
     *
     * @return the resource created message
     */
    protected String getResourceCreatedMessage() {
        return "Resource was created";
    }

    /**
     * Gets resource updated message.
     *
     * @return the resource updated message
     */
    protected String getResourceUpdatedMessage() {
        return "Resource was updated";
    }

    /**
     * Gets resource deleted message.
     *
     * @return the resource deleted message
     */
    protected String getResourceDeletedMessage() {
        return "Resource was deleted";
    }

    /**
     * Resource form to resource r.
     *
     * @param resourceForm the resource form
     * @return the r
     */
    protected abstract R resourceFormToResource(RF resourceForm);

    /**
     * Resource to resource form rf.
     *
     * @param resource the resource
     * @return the rf
     */
    protected abstract RF resourceToResourceForm(R resource);

    /**
     * Gets resource base uri.
     *
     * @return the resource base uri
     */
    protected abstract String getResourceBaseUri();

    /**
     * Gets resource view path.
     *
     * @return the resource view path
     */
    protected abstract String getResourceViewPath();

    /**
     * Gets edit resource view path.
     *
     * @return the edit resource view path
     */
    protected abstract String getEditResourceViewPath();

    /**
     * Gets resource form holder.
     *
     * @return the resource form holder
     */
    protected abstract String getResourceFormHolder();

    /**
     * Gets resource search form holder.
     *
     * @return the resource search form holder
     */
    protected abstract String getResourceSearchFormHolder();

    /**
     * Gets resource list holder.
     *
     * @return the resource list holder
     */
    protected abstract String getResourceListHolder();

}
