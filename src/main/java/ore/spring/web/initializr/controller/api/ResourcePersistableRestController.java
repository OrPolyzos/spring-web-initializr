package ore.spring.web.initializr.controller.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;
import java.util.Collection;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public interface ResourcePersistableRestController<R, ID extends Serializable> {

    @ResponseStatus(CREATED)
    @ResponseBody
    @PostMapping
    R create(@RequestBody R resourcePersistable);

    @ResponseStatus(OK)
    @ResponseBody
    @GetMapping(path = "/{id}")
    R read(@PathVariable("id") ID id);

    @ResponseStatus(OK)
    @ResponseBody
    @PutMapping
    R update(@RequestBody R resourcePersistable);

    @ResponseStatus(OK)
    @DeleteMapping(path = "/{id}")
    void delete(@PathVariable("id") ID id);

    @ResponseStatus(OK)
    @ResponseBody
    @GetMapping
    Collection<R> readAll();

}
