package ore.spring.web.initializr.controller.api;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.Serializable;

public interface ResourcePersistableViewController<R, ID extends Serializable> {

    @GetMapping
    String getBaseView(Model model);

    @GetMapping("/{id}/edit")
    String getUpdateView(@PathVariable("id") ID resourcePersistableId, Model model);

    @PostMapping
    String create(@ModelAttribute R resourcePersistableDto, BindingResult bindingResult, Model model);

    @PostMapping(path = "/{id}/edit")
    String update(@PathVariable("id") ID resourcePersistableId, @ModelAttribute R resourcePersistableDto, BindingResult bindingResult, Model model);

    @PostMapping(path = "/{id}/delete")
    String delete(@PathVariable("id") ID resourcePersistableId, Model model);

    @PostMapping(path = "/search")
    String readAllBy(@ModelAttribute R resourcePersistableDto, BindingResult bindingResult, Model model);

}
