package spring.web.initializr.base.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring.web.initializr.base.domain.ResourcePersistable;
import spring.web.initializr.base.exception.ResourceException;
import spring.web.initializr.base.exception.ResourceNotFoundException;
import spring.web.initializr.base.service.ResourceService;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ResourceController<R extends ResourcePersistable<ID>, ID extends Serializable, RF, RSF> implements InformativeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class.getName());

    protected final Class<R> resourceClass;
    protected final Class<RF> resourceFormClass;
    protected final Class<RSF> resourceSearchFormClass;
    protected ResourceService<R, RSF, ID> resourceService;

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

    public String getResourceView(Model model) {
        if (!model.containsAttribute(getResourceFormHolder())) {
            model.addAttribute(getResourceFormHolder(), createInstanceOf(resourceFormClass));
        }
        if (!model.containsAttribute(getResourceSearchFormHolder())) {
            model.addAttribute(getResourceSearchFormHolder(), createInstanceOf(resourceSearchFormClass));
        }
        return getResourceViewPath();
    }

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

    protected String getResourceCreatedMessage() {
        return "Resource was created";
    }

    protected String getResourceUpdatedMessage() {
        return "Resource was updated";
    }

    protected String getResourceDeletedMessage() {
        return "Resource was deleted";
    }

    protected abstract R resourceFormToResource(RF resourceForm);

    protected abstract RF resourceToResourceForm(R resource);

    protected abstract String getResourceBaseUri();

    protected abstract String getResourceViewPath();

    protected abstract String getEditResourceViewPath();

    protected abstract String getResourceFormHolder();

    protected abstract String getResourceSearchFormHolder();

    protected abstract String getResourceListHolder();

}
