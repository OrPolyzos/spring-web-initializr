package ore.spring.web.initializr.controller.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.stream.Collectors;
import ore.spring.web.initializr.controller.RpMessenger;
import ore.spring.web.initializr.controller.api.ResourcePersistableViewController;
import ore.spring.web.initializr.service.api.ResourcePersistableService;
import ore.spring.web.initializr.service.impl.RpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

public interface RpViewController<D, I extends Serializable> extends ResourcePersistableViewController<D, I>, RpMessenger {

  Logger RP_VIEW_CONTROLLER_LOG = LoggerFactory.getLogger(RpService.class);

  ResourcePersistableService<D, I> getService();

  default String getBaseView(Model model) {
    if (!model.containsAttribute(getHolder())) {
      model.addAttribute(getHolder(), getRpDto());
    }
    return getBaseViewPath();
  }

  default String getUpdateView(I resourcePersistableId, Model model) {
    if (!model.containsAttribute(getHolder())) {
      model.addAttribute(getHolder(), getService().findOrThrow(resourcePersistableId));
    }
    return getUpdateViewPath();
  }

  default String create(D resourcePersistableDto, BindingResult bindingResult, Model model) {
    if (bindingResult.hasErrors()) {
      sendBindingErrors(model, bindingResult, getHolder(), resourcePersistableDto);
      sendErrorMessage(model);
      return getBaseView(model);
    }

    getService().insert(resourcePersistableDto);
    sendSuccessMessage(model);
    model.asMap().remove(getHolder());
    return getBaseView(model);
  }

  default String update(I resourcePersistableId, D resourcePersistableDto, BindingResult bindingResult, Model model) {
    if (bindingResult.hasErrors()) {
      sendBindingErrors(model, bindingResult, getHolder(), resourcePersistableDto);
      sendErrorMessage(model);
      return getUpdateView(resourcePersistableId, model);
    }
    getService().update(resourcePersistableDto);
    sendSuccessMessage(model);
    model.asMap().remove(getHolder());
    return getBaseView(model);
  }

  default String delete(I resourcePersistableId, Model model) {
    getService().deleteById(resourcePersistableId);
    sendSuccessMessage(model);
    return getBaseView(model);
  }

  default String readAll(Model model) {
    model.addAttribute(getListHolder(), getService().findAll().collect(Collectors.toList()));
    return getBaseView(model);
  }

  @SuppressWarnings("unchecked")
  default D getRpDto() {
    Class<D> rpDto = (Class<D>) ((ParameterizedType) this.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    try {
      return rpDto.getDeclaredConstructor().newInstance();
    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
      RP_VIEW_CONTROLLER_LOG.error(
          "Failed to instantiate (missing default constructor): {} "
              + "| Please override the RpViewController::getRpDto method and provide your own implementation for creating a new instance", rpDto.getName());
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

  default String getListHolder() {
    return getHolder().concat("List");
  }

}
