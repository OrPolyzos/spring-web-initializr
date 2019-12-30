Spring Web Initializr
==========
[![release-2.0.0][shield-release]](#)
[![build-passing][shield-build]](#)
[![code-coverage-92%][shield-coverage]](#)  
[![jdk-8][shield-jdk]](#)
[![spring-boot-2.0.0.RELEASE][shield-spring]](#)
[![MIT licensed][shield-license]](#)

Spring Web Initializr _(will be referenced Swi from now on)_ is a library that helps you easily create Web Apps with Spring Boot.  
It was initially developed in order to support the [Swip (Spring Web Initializr Plugin)](https://plugins.jetbrains.com/plugin/12239-swip-spring-web-initializr-) 
built for IntelliJ IDEA, but was extended beyond that usage and can be obviously used independently.

Table of Contents
-----------------
  * [Description](#Description)
  * [Examples](#Examples)
  * [Contributing](#Contributing)
  * [License](#License)
  
Description
-----------

Swi is essentially providing interfaces that contain the base logic for the Create, Read, Update & Delete  operations of an Entity (ResourcePersistable).  
It is able to provide out of the box implementations for
*  RESTful Application (exposing directly the ResourcePersistable or exposing a DTO instead)
*  Web Application based on server-side rendering with a Template Engine (exposing directly the ResourcePersistable or exposing a DTO instead)

Generic Type Parameters
* **I** stands for the class of the field representing the primary key of the ResourcePersistable (e.g. Long)
* **R** stands for the class of the ResourcePersistable (e.g. User)
* **D** stands for the class of the DTO that is going to be exposed instead of the ResourcePersistable (e.g. UserDto)

_ResourcePersistable\<I\>_
* Should be implemented by an Entity of your application (e.g. User)  
* There is only a single method to be implemented  
    * `I getRpId();` - Should return the primary key field

_RpService\<R extends ResourcePersistable\<I\>, I extends Serializable, D\> extends ResourcePersistableService\<D, I\>_
* Should be implemented by the @Service for the corresponding ResourcePersistable (e.g. UserService)
* It is responsible for the communication with the corresponding CrudRepository<R,I>
* There are 3 methods to be implemented
    * `CrudRepository<R, I> getRepository();` - Should return the corresponding CrudRepository<R,I> (e.g. UserRepository)
    * `Function<R, D> getEntityToDtoConverter();` - Should provide a way for the ResourcePersistable to be converted to the DTO
    * `Function<D, R> getDtoToEntityConverter();` - Should provide a way for the DTO to be converted to the ResourcePersistable


_NoDtoRpService\<R extends ResourcePersistable\<I\>, I extends Serializable\> extends RpService\<R, I, R\>_
* NoDtoRpService should be used if we want to expose directly the ResourcePersistable
* Everything from RpService applies to NoDtoRpService, with the difference of 2 already implemented methods
    * `default Function<R, R> getEntityToDtoConverter() { return Function.identity(); }`
    * `default Function<R, R> getDtoToEntityConverter() { return Function.identity(); }`

_RpRestController\<D, I extends Serializable> extends ResourcePersistableRestController\<D, I\>_
* Provides a RESTful API for the CRUD operations of the ResourcePersistable
* It is responsible for the communication with the corresponding ResourcePersistableService<R, I>
* Should be implemented by the @RestController for the corresponding ResourcePersistable (e.g. UserRestController)
* There is only a single method to be implemented  
    * `ResourcePersistableService<D, I> getService();` - Should return the corresponding ResourcePersistableService<D,I> (e.g. UserService)

_RpViewController\<D, I extends Serializable\> extends ResourcePersistableViewController\<D, I\>_
* Provides a MVC API for the CRUD operations of the ResourcePersistable
* It is responsible for the communication with the corresponding ResourcePersistableService<R, I, D>
* Should be implemented by the @Controller for the corresponding ResourcePersistable (e.g. UserController)
* There is only a single method to be implemented  
    * `ResourcePersistableService<D, I> getService();` - Should return the corresponding ResourcePersistableService<D,I> (e.g. UserService)
    
Examples
--------
In the following examples the ResourcePersistable will be a User and we are going to provide
  * [RESTful API exposing User](#ExampleA)
  * [RESTful API exposing UserDto](#ExampleB)
  * [MVC API exposing User](#ExampleC)
  * [MVC API exposing UserDto](#ExampleD)

_pom.xml_
```xml
<dependency>
    <groupId>io.github.orpolyzos</groupId>
    <artifactId>spring-web-initializr</artifactId>
    <version>2.0.0</version>
</dependency>
```
 
_User_ (Getters/Setters omitted)
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
  public Long getRpId() {
    return this.id;
  }
}
```

_UserDto_ (Getters/Setters omitted)
```java
@Entity(name = "user")
public class UserDto {
  
  private Long id;
  private String firstName;
  private String lastName;
  
  @NotBlank
  @Email
  private String email;
}
```

_UserRepository_
```java
@Repository
public interface UserRepository extends CrudRepository<User, Long> { }
```

ExampleA
--------
_UserService_
```java
@Service
public class UserService implements NoDtoRpService<User, Long> {

    private final UserRepository userRepository;

    @Override
    public CrudRepository<User, Long> getRepository() {
        return userRepository;
    }
}
```

_UserRestController_
```java
@RequestMapping("/api/user")
@Controller
public class UserNoDtoRestController implements RpRestController<User, Long> {

    private final UserService userService;

    @Override
    public ResourcePersistableService<User, Long> getService() {
        return userService;
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

[shield-release]: https://img.shields.io/badge/release-2.0.0-blue.svg
[shield-build]: https://img.shields.io/badge/build-passing-brightgreen.svg
[shield-coverage]: https://img.shields.io/badge/coverage-92%25-brightgreen.svg
[shield-jdk]: https://img.shields.io/badge/jdk-8-blue.svg
[shield-spring]: https://img.shields.io/badge/spring-2.2.1-blue.svg
[shield-license]: https://img.shields.io/badge/license-MIT-blue.svg
