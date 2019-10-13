Spring Web Initializr
==========
[![release-3.0.0][shield-release]](#)
[![build-passing][shield-build]](#)
[![code-coverage-100%][shield-coverage]](#)  
[![jdk-8][shield-jdk]](#)
[![spring-boot-2.0.0.RELEASE][shield-spring]](#)
[![MIT licensed][shield-license]](#)

Spring Web Initializr _(will be referenced Swi from now on)_ is a library that will help you easily create Web Apps with Spring Boot.  
It was mainly developed in order to support the [Swip (Spring Web Initializr Plugin)](https://plugins.jetbrains.com/plugin/12239-swip-spring-web-initializr-) built for IntelliJ IDEA, but can be obviously used independently.
However, you will understand better the usage and the purpose of the library, if you choose to use the Swip first.

Table of Contents
-----------------
  * [Description](#Description)
  * [Example](#Example)
  * [Contributing](#Contributing)
  * [License](#License)
  
Description
-----------

Swi is essentially providing interfaces/abstract classes that contain the base logic for the Create, Read, Update & Delete  operations of the ResourcePersistable Entity.  
It is taken for granted, that the user of the library is going to user a template engine, but there is no restriction to which one.

_ResourcePersistable<ID>_
* Meant to be implemented by an Entity of your application (e.g. User, Vehicle, etc...)  
* **ID** stands for the class of the field representing the primary key of the Entity (e.g. Long)
* There is only a single method to be implemented, which obviously should be returning the primary key field  
    ```
    ID getResourcePersistableId();
    ```

_ResourcePersistableController<R extends ResourcePersistable<ID>, ID extends Serializable, RF, RSF>_
* Meant to be extended by the Spring @Controller for the corresponding ResourcePersistable entity (e.g. UserController, VehicleController, etc...)
* **R extends ResourcePersistable<ID>** stands for the class ResourcePersistableEntity (e.g. User, Vehicle, etc...)
* **ID extends Serializable** stands for the class of the field representing the primary key of the Entity (e.g. Long)
* **RF** stands for the class of the ResourcePersistableForm, that should contain all the fields required in order to create a ResourcePersistable (e.g. UserForm, VehicleForm)
  * Might as well be the class of the ResourcePersistable itself
* **RSF** stands for the class of the ResourcePersistableSearchForm, that should contain all the fields in order to search for a ResourcePersistable (e.g. UserSearchForm, VehicleSearchForm)
  * Might as well be the class of the ResourcePersistable itself
* The methods that should be implemented are the ones that provide the endpoint & the resources paths as well as the transformation methods from a ResourcePersistable to a ResourcePersistableForm and vice versa.
  
_ResourcePersistableService<R extends ResourcePersistable<ID>, ID extends Serializable, RSF>_
* Meant to be extended by the Spring @Service for the corresponding ResourcePersistable entity (e.g. UserService, VehicleService, etc...)
* **R extends ResourcePersistable<ID>** stands for the class ResourcePersistableEntity (e.g. User, Vehicle, etc...)
* **ID extends Serializable** stands for the class of the field representing the primary key of the Entity (e.g. Long)
* **RSF** stands for the class of the ResourcePersistableSearchForm, that should contain all the fields in order to search for a ResourcePersistable (e.g. UserSearchForm, VehicleSearchForm)
  * Might as well be the class of the ResourcePersistable itself  


Example
-----
First of all add the dependency in your project:

_Maven_
```xml
<dependency>
    <groupId>io.github.orpolyzos</groupId>
    <artifactId>spring-web-initializr</artifactId>
    <version>3.0.0</version>
</dependency>
```
In this simplified example the ResourcePersistable will be the User Entity and we will reuse the same class for ResourcePersistableForm and ResourcePersistableSearchForm. 

_ResourcePersistable_ (Getters/Setters omitted)
```java
@Entity(name = "user")
public class User implements ResourcePersistable<Long> {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Override
    public Long getResourcePersistableId() {
        return id;
    }

}
```

_ResourcePersistableCrudRepository_
```java
@Repository
public interface UserResourcePersistableRepository extends CrudRepository<User, Long> { }
```

_ResourcePersistableService_
```java
@Service
public class UserResourcePersistableService extends ResourcePersistableService<User, Long, User> {

    private final CrudRepository<User, Long> userResourcePersistableRepository;

    @Autowired
    public UserResourcePersistableService(CrudRepository<User, Long> userResourcePersistableRepository) {
        super(userResourcePersistableRepository);
        this.userResourcePersistableRepository = userResourcePersistableRepository;
    }

}
```

_ResourcePersistableController_
```java
@Controller
public class UserResourcePersistableController extends ResourcePersistableController<User, Long, User, User> {
    
    private static final String RESOURCE_PERSISTABLE_BASE_URI = "/users";
    private static final String RESOURCE_PERSISTABLE_BASE_VIEW_PATH = "/user/users";
    private static final String RESOURCE_PERSISTABLE_EDIT_VIEW_PATH = "/user/edit-user";
    private static final String RESOURCE_PERSISTABLE_FORM_HOLDER = "userForm";
    private static final String RESOURCE_PERSISTABLE_SEARCH_FORM_HOLDER = "userSearchForm";
    private static final String RESOURCE_PERSISTABLE_LIST_HOLDER = "userList";
    
    private final ResourcePersistableService<User, Long, User> userResourcePersistableService;

    @Autowired
    public UserResourcePersistableController(ResourcePersistableService<User, Long, User> userResourcePersistableService) {
        super(userResourcePersistableService);
        this.userResourcePersistableService = userResourcePersistableService;
    }

    @Override
    protected String getResourcePersistableBaseUri() { return RESOURCE_PERSISTABLE_BASE_URI; }

    @Override
    protected String getResourcePersistableBaseViewPath() { return RESOURCE_PERSISTABLE_BASE_VIEW_PATH; }

    @Override
    protected String getResourcePersistableEditViewPath() { return RESOURCE_PERSISTABLE_EDIT_VIEW_PATH; }

    @Override
    protected String getResourcePersistableFormHolder() { return RESOURCE_PERSISTABLE_FORM_HOLDER; }

    @Override
    protected String getResourcePersistableSearchFormHolder() { return RESOURCE_PERSISTABLE_SEARCH_FORM_HOLDER; }

    @Override
    protected String getResourcePersistableListHolder() { return RESOURCE_PERSISTABLE_LIST_HOLDER; }

    @Override
    @GetMapping(RESOURCE_PERSISTABLE_BASE_URI)
    public String getResourcePersistableBaseView(Model model) {
        return super.getResourcePersistableBaseView(model);
    }

    @Override
    @PostMapping(RESOURCE_PERSISTABLE_BASE_URI)
    public String createResourcePersistable(@Valid @ModelAttribute(RESOURCE_PERSISTABLE_FORM_HOLDER) User resourcePersistableForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        return super.createResourcePersistable(resourcePersistableForm, bindingResult, model, redirectAttributes);
    }

    @Override
    @PostMapping("users/{resourcePersistableId}/delete")
    public String deleteResourcePersistable(@PathVariable("resourcePersistableId") Long resourcePersistableId, Model model) {
        return super.deleteResourcePersistable(resourcePersistableId, model);
    }

    @Override
    @GetMapping("users/{resourcePersistableId}/edit")
    public String getResourcePersistableEditView(@PathVariable("resourcePersistableId") Long resourcePersistableId, Model model) {
        return super.getResourcePersistableEditView(resourcePersistableId, model);
    }

    @Override
    @PostMapping("users/{resourcePersistableId}/edit")
    public String editResourcePersistable(@PathVariable("resourcePersistableId") Long resourcePersistableId, @Valid @ModelAttribute(RESOURCE_PERSISTABLE_FORM_HOLDER) User resourcePersistableForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        return super.editResourcePersistable(resourcePersistableId, resourcePersistableForm, bindingResult, model, redirectAttributes);
    }

    @Override
    @PostMapping("users/search")
    public String searchResourcePersistablesBy(@Valid @ModelAttribute(RESOURCE_PERSISTABLE_SEARCH_FORM_HOLDER) User resourcePersistableSearchForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        return super.searchResourcePersistablesBy(resourcePersistableSearchForm, bindingResult, model, redirectAttributes);
    }

    @Override
    protected User resourcePersistableFormToResourcePersistable(User user) {
        return user;
    }

    @Override
    protected User resourcePersistableToResourcePersistableForm(User user) {
        return user;
    }

}
```

Contributing
------------
To contribute to Spring Web Initializr, follow the instructions in our [contributing guide](/contributing.md).

License
-------
Spring Web Initializr is licensed under the [MIT](/license.md) license.  
Copyright &copy; 2019, Orestes Polyzos

[shield-release]: https://img.shields.io/badge/release-3.0.0-blue.svg
[shield-build]: https://img.shields.io/badge/build-passing-brightgreen.svg
[shield-coverage]: https://img.shields.io/badge/coverage-0%25-red.svg
[shield-jdk]: https://img.shields.io/badge/jdk-8-blue.svg
[shield-spring]: https://img.shields.io/badge/spring-2.0.0-blue.svg
[shield-license]: https://img.shields.io/badge/license-MIT-blue.svg
