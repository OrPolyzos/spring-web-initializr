package ore.spring.web.initializr.controller.api;

import java.io.Serializable;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public interface ResourcePersistableViewController<R, I extends Serializable> {

  @GetMapping
  String getBaseView(Model model);

  @GetMapping("/{id}/edit")
  String getUpdateView(@PathVariable("id") I resourcePersistableId, Model model);

  @PostMapping
  String create(@ModelAttribute R resourcePersistableDto, BindingResult bindingResult, Model model);

  @PostMapping(path = "/{id}/edit")
  String update(@PathVariable("id") I resourcePersistableId, @ModelAttribute R resourcePersistableDto, BindingResult bindingResult, Model model);

  @PostMapping(path = "/{id}/delete")
  String delete(@PathVariable("id") I resourcePersistableId, Model model);

  @PostMapping(path = "/search")
  String readAll(Model model);

}
