package ore.spring.web.initializr.controller.impl;

import ore.spring.web.initializr.controller.Messenger;
import ore.spring.web.initializr.controller.api.ResourcePersistableViewController;
import ore.spring.web.initializr.service.api.ResourcePersistableService;
import ore.spring.web.initializr.service.impl.RpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.stream.Collectors;

public interface RpViewController<D, ID extends Serializable> extends ResourcePersistableViewController<D, ID>, Messenger {

    Logger RP_VIEW_CONTROLLER_LOG = LoggerFactory.getLogger(RpService.class);

    ResourcePersistableService<D, ID> getService();

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

    @SuppressWarnings("unchecked")
    default D getRpDto() {
        RP_VIEW_CONTROLLER_LOG.warn("It is strongly advised to override the RpViewController::getRpDto method " +
                "and provide your own implementation for creating a new instance, in order to avoid using reflection.");
        Class<D> rpDto = (Class<D>) ((ParameterizedType) this.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        try {
            return rpDto.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            RP_VIEW_CONTROLLER_LOG.error("Failed to instantiate: {} | Message: {}", rpDto.getName(), e.getMessage());
            RP_VIEW_CONTROLLER_LOG.error("No default constructor found for Class: {}.", rpDto.getName());
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
        return getRpDto().getClass().getSimpleName().toLowerCase();
    }

    default String getRpSearchDtoHolder() {
        return getHolder().concat("Search");
    }

    default String getRpDtoListHolder() {
        return getHolder().concat("List");
    }

}
