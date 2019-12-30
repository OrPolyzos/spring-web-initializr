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

__Generic Type Parameters__
* **I** stands for the class of the field representing the primary key of the ResourcePersistable (e.g. Long)
* **R** stands for the class of the ResourcePersistable (e.g. User)
* **D** stands for the class of the DTO that is going to be exposed instead of the ResourcePersistable (e.g. UserDto)

__ResourcePersistable\<I\>__
* Should be implemented by the corresponding Entity (e.g. User)  
* There is only a single method to be implemented  
    * `I getRpId();` - Should return the primary key field

__RpService\<R extends ResourcePersistable\<I\>, I extends Serializable, D\> extends ResourcePersistableService\<D, I\>__
* Should be implemented by the @Service for the corresponding ResourcePersistable (e.g. UserService)
* It is responsible for the communication with the corresponding CrudRepository<R,I>
* There are 3 methods to be implemented
    * `CrudRepository<R, I> getRepository();` - Should return the corresponding CrudRepository<R,I> (e.g. UserRepository)
    * `Function<R, D> getEntityToDtoConverter();` - Should provide a way for the ResourcePersistable to be converted to the DTO
    * `Function<D, R> getDtoToEntityConverter();` - Should provide a way for the DTO to be converted to the ResourcePersistable


__NoDtoRpService\<R extends ResourcePersistable\<I\>, I extends Serializable\> extends RpService\<R, I, R\>__
* NoDtoRpService should be used if we want to expose directly the ResourcePersistable
* Everything from RpService applies to NoDtoRpService, with the difference of 2 already implemented methods
    * `default Function<R, R> getEntityToDtoConverter() { return Function.identity(); }`
    * `default Function<R, R> getDtoToEntityConverter() { return Function.identity(); }`

__RpRestController\<D, I extends Serializable> extends ResourcePersistableRestController\<D, I\>__
* Provides a RESTful API for the CRUD operations of the ResourcePersistable
* It is responsible for the communication with the corresponding ResourcePersistableService<R, I>
* Should be implemented by the @RestController for the corresponding ResourcePersistable (e.g. UserRestController)
* There is only a single method to be implemented  
    * `ResourcePersistableService<D, I> getService();` - Should return the corresponding ResourcePersistableService<D,I> (e.g. UserService)

__RpViewController\<D, I extends Serializable\> extends ResourcePersistableViewController\<D, I\>__
* Provides a MVC API for the CRUD operations of the ResourcePersistable
* It is responsible for the communication with the corresponding ResourcePersistableService<R, I, D>
* Should be implemented by the @Controller for the corresponding ResourcePersistable (e.g. UserController)
* There is only a single method to be implemented  
    * `ResourcePersistableService<D, I> getService();` - Should return the corresponding ResourcePersistableService<D,I> (e.g. UserService)
    
Examples
--------
In the following examples the ResourcePersistable will be a User and we are going to provide  
<details>
    <summary>
      <b>General Structure</b>
    </summary>
    
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
</details>

<details>
    <summary>
      <b>Example A - RESTful API exposing User</b>
    </summary>
    

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
public class UserRestController implements RpRestController<User, Long> {

    private final UserService userService;

    @Override
    public ResourcePersistableService<User, Long> getService() {
        return userService;
    }
}
```
</details>

<details>
    <summary>
      <b>Example B - RESTful API exposing UserDto</b>
    </summary>

_UserDtoService_
```java
@Service
public class UserDtoService implements RpService<User, Long, UserDto> {

    private final UserRepository userRepository;

    @Override
    public CrudRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    public Function<User, UserDto> getEntityToDtoConverter() {
        return user -> UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public Function<UserDto, User> getDtoToEntityConverter() {
        return userDto -> User.builder()
                .id(userDto.getId())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .build();
    }
}

```

_UserDtoRestController_
```java
@RequestMapping("/api/user/dto")
@Controller
public class UserDtoRestController implements RpRestController<UserDto, Long> {

    private final UserDtoService userDtoService;

    @Override
    public ResourcePersistableService<UserDto, Long> getService() {
        return userDtoService;
    }
}
```
</details>

<details>
    <summary>
      <b>Example C - MVC API exposing User</b>
    </summary>

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

_UserViewController_
```java
@RequestMapping("/view/user")
@Controller
public class UserViewController implements RpViewController<User, Long> {

  private final UserService userService;

  @Override
  public NoDtoRpService<User, Long> getService() {
    return userService;
  }
}
```
</details>


<details>
    <summary>
      <b>Example D - MVC API exposing UserDto</b>
    </summary>

_UserDtoService_
```java
@Service
public class UserDtoService implements RpService<User, Long, UserDto> {

    private final UserRepository userRepository;

    @Override
    public CrudRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    public Function<User, UserDto> getEntityToDtoConverter() {
        return user -> UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public Function<UserDto, User> getDtoToEntityConverter() {
        return userDto -> User.builder()
                .id(userDto.getId())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .build();
    }
}

```

_UserDtoRestController_
```java
@RequestMapping("/view/user/dto")
@Controller
public class UserDtoViewController implements RpViewController<UserDto, Long> {

  private final UserDtoService userService;

  @Override
  public RpService<User, Long, UserDto> getService() {
    return userService;
  }

}

```
</details>


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
