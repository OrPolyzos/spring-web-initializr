package ore.spring.web.initializr.controller.api;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Serializable;

public interface ResourcePersistableViewController<R, ID extends Serializable> {



    String getBaseView(Model model);

    String getUpdateView(ID resourcePersistableId, Model model);

    String create(R resourcePersistableDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes);

    String update(ID resourcePersistableId, R resourcePersistableDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes);

    String delete(ID resourcePersistableId, Model model);

    String readAll(R resourcePersistableDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes);

}
