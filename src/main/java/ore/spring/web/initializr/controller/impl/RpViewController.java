package ore.spring.web.initializr.controller.impl;

import ore.spring.web.initializr.controller.InformativeController;
import ore.spring.web.initializr.controller.api.ResourcePersistableViewController;
import ore.spring.web.initializr.domain.ResourcePersistable;
import ore.spring.web.initializr.service.impl.NoDtoRpService;
import ore.spring.web.initializr.service.impl.RpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;

public interface RpViewController<D extends ResourcePersistable<ID>, ID extends Serializable> extends ResourcePersistableViewController<D, ID>, InformativeController {

    Logger RP_VIEW_CONTROLLER_LOG = LoggerFactory.getLogger(RpService.class);

    Class<D> getRpClass();

    NoDtoRpService<D, ID> getService();

    default String getBaseView(Model model) {
        if (!model.containsAttribute(getHolder())) {
            model.addAttribute(getHolder(), getRpDto());
        }
        if (!model.containsAttribute(getRpSearchDtoHolder())) {
            model.addAttribute(getRpSearchDtoHolder(), getRpDto());
        }
        return getBaseViewPath();
    }

    default String getUpdateView(ID resourcePersistableId, Model model) {
        if (!model.containsAttribute(getHolder())) {
            model.addAttribute(getHolder(), getService().findOrThrow(resourcePersistableId));
        }
        return getUpdateViewPath();
    }

    default String create(D resourcePersistableDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            sendBindingErrors(model, bindingResult, getHolder(), resourcePersistableDto);
            return getBaseView(model);
        }

        getService().insert(resourcePersistableDto);
        sendSuccessMessage(model);
        model.asMap().remove(getHolder());
        return getBaseView(model);
    }

    default String update(ID resourcePersistableId, D resourcePersistableDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            sendBindingErrors(model, bindingResult, getHolder(), resourcePersistableDto);
            return getUpdateView(resourcePersistableId, model);
        }
        getService().update(resourcePersistableDto);
        sendSuccessMessage(model);
        model.asMap().remove(getHolder());
        return getBaseView(model);
    }

    default String delete(ID resourcePersistableId, Model model) {
        getService().deleteById(resourcePersistableId);
        sendSuccessMessage(model);
        return getBaseView(model);
    }

    default String readAllBy(D resourcePersistableDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            sendBindingErrors(model, bindingResult, getRpSearchDtoHolder(), resourcePersistableDto);
            return getBaseView(model);
        }
        model.addAttribute(getRpDtoListHolder(), getService().findAll().collect(Collectors.toList()));
        return getBaseView(model);
    }

    default D getRpDto() {
        try {
            return getRpClass().getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            RP_VIEW_CONTROLLER_LOG.error("Failed to instantiate: {} | Message: {}", getRpClass().getName(), e.getMessage());
            RP_VIEW_CONTROLLER_LOG.warn("Consider declaring a default constructor for Class: {}", getRpClass().getName());
            return null;
        }
    }


    default String getBaseViewPath() {
        return String.format("/%s/%s", getHolder(), getHolder());
    }

    default String getUpdateViewPath() {
        return String.format("/%s/edit-%s", getHolder(), getHolder());
    }

    default String getHolder() {
        return getRpClass().getSimpleName().toLowerCase();
    }

    default String getRpSearchDtoHolder() {
        return getHolder().concat("Search");
    }

    default String getRpDtoListHolder() {
        return getHolder().concat("List");
    }

}
