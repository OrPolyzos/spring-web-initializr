package ore.spring.web.initializr.controller.impl;

import ore.spring.web.initializr.controller.api.ResourcePersistableViewController;
import ore.spring.web.initializr.exception.RpTypedParameterException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Serializable;

public interface RpViewController<D, ID extends Serializable> extends ResourcePersistableViewController<D, ID> {

    default Class getClassFromTypeParams(int typeIndex) {
        try {
            return this.getClass().getTypeParameters()[typeIndex].getClass();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            throw new RpTypedParameterException(typeIndex, this.getClass().getTypeParameters().length);
        }
    }

    default String getBaseView(Model model) {
        if (!model.containsAttribute(getClassFromTypeParams(0).getSimpleName())) {
//            model.addAttribute(getResourcePersistableFormHolder(), createInstanceOf(resourcePersistableFormClass));
        }
        if (!model.containsAttribute(getClassFromTypeParams(0).getSimpleName())) {
//            model.addAttribute(getResourcePersistableSearchFormHolder(), createInstanceOf(resourcePersistableSearchFormClass));
        }
        return "asda";
    }

    String getUpdateView(ID resourcePersistableId, Model model);

    String create(D resourcePersistableDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes);

    String update(ID resourcePersistableId, D resourcePersistableDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes);

    String delete(ID resourcePersistableId, Model model);

    String readAll(D resourcePersistableDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes);

}
