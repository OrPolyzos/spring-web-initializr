Spring Web Initializr
==========

[![build][shield-build]](#)
[![tests][shield-tests]](#)  
[![code-coverage-92%][shield-coverage]](#)

[![release-2.0.0][shield-release]](#)
[![jdk-8][shield-jdk]](#)
[![spring-boot-2.0.0.RELEASE][shield-spring]](#)
[![MIT licensed][shield-license]](#)  

Spring Web Initializr _(will be referenced Swi from now on)_ is a library that helps you easily create Web Apps with Spring Boot.  
It was initially developed in order to support the [Swip (Spring Web Initializr Plugin)](https://plugins.jetbrains.com/plugin/12239-swip-spring-web-initializr-) 
built for IntelliJ IDEA, but was extended beyond that usage and can be obviously used independently.

TL;DR
-----
```xml
<dependency>
    <groupId>io.github.orpolyzos</groupId>
    <artifactId>spring-web-initializr</artifactId>
    <version>2.0.0</version>
</dependency>
```
**Fully working examples can be found at [Swi(p) Demo](https://github.com/OrPolyzos/swip-demo)**

Table of Contents
-----------------
  * [Description](#Description)
  * [Examples](#Examples)
  * [Contributors](#Contributors)
  * [License](#License)
  
Description
-----------
Swi is providing implementations for the Create, Read, Update & Delete (CRUD) operations of an Entity (ResourcePersistable)  
*  CRUD REST API
   *  exposing directly the ResourcePersistable
   *  exposing a DTO instead of the ResourcePersistable
*  CRUD MVC API (ServerSide Rendering using a compatible Template Engine of your choice)
   *  exposing directly the ResourcePersistable
   *  exposing a DTO instead of the ResourcePersistable

__Generic Type Parameters__
* **R** stands for the class of the ResourcePersistable, the one annotated with @Entity (e.g. User)
* **I** stands for the class of the primary key of the ResourcePersistable, the one annotated with @Id (e.g. Long)
* **D** stands for the class of the DTO that is going to be exposed instead of the ResourcePersistable (e.g. UserDto)

__ResourcePersistable\<I\>__
* Should be implemented by the corresponding Entity (e.g. User)  
* There is only a single method to be implemented  
    * `I getRpId();` - Should return the primary key field

__RpService\<R extends ResourcePersistable\<I\>, I extends Serializable, D\> extends ResourcePersistableService\<D, I\>__
* It is responsible for the communication with the corresponding CrudRepository<R,I>
* Should be implemented by the @Service for the corresponding ResourcePersistable (e.g. UserService)
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
* It is responsible for the communication with the corresponding ResourcePersistableService<R, I>
* Provides a REST API for the CRUD operations of the ResourcePersistable
* Should be implemented by the @RestController for the corresponding ResourcePersistable (e.g. UserRestController)
* There is only a single method to be implemented  
    * `ResourcePersistableService<D, I> getService();` - Should return the corresponding ResourcePersistableService<D,I> (e.g. UserService)

__RpViewController\<D, I extends Serializable\> extends ResourcePersistableViewController\<D, I\>__
* It is responsible for the communication with the corresponding ResourcePersistableService<R, I, D>
* Provides a MVC API for the CRUD operations of the ResourcePersistable
* Should be implemented by the @Controller for the corresponding ResourcePersistable (e.g. UserController)
* There is only a single method to be implemented  
    * `ResourcePersistableService<D, I> getService();` - Should return the corresponding ResourcePersistableService<D,I> (e.g. UserService)
    
Examples
--------
* [CRUD REST API exposing the Entity](https://github.com/OrPolyzos/spring-web-initializr/wiki/Example:-CRUD-REST-API-exposing-the-Entity)
* [CRUD REST API exposing the Data Transfer Object (DTO)](https://github.com/OrPolyzos/spring-web-initializr/wiki/Example:-CRUD-REST-API-exposing-the-Data-Transfer-Object-(DTO))
* [CRUD MVC API exposing the Entity](https://github.com/OrPolyzos/spring-web-initializr/wiki/Example:-CRUD-MVC-API-exposing-the-Entity)
* [CRUD MVC API exposing the Data Transfer Object (DTO)](https://github.com/OrPolyzos/spring-web-initializr/wiki/Example:-CRUD-MVC-API-exposing-the-Data-Transfer-Object-(DTO))


Contributors
------------
<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore -->
<table>
  <tr>
    <td align="center">
      <a href="https://github.com/orpolyzos">
        <img src="https://avatars0.githubusercontent.com/u/27271443?s=460&v=4" width="100px;" alt="Orestes Polyzos"/>
        <br />
        <sub><b>Orestes Polyzos</b></sub>
      </a>
     </td>
    <td align="center">
      <a href="https://github.com/terminatorbill">
        <img src="https://avatars3.githubusercontent.com/u/5015605?s=400&v=4" width="100px;" alt="BlueMagic"/>
        <br />
        <sub><b>BlueMagic</b></sub>
      </a>
     </td>     
  </tr>
</table>

<!-- ALL-CONTRIBUTORS-LIST:END -->  
__To contribute to Spring Web Initializr, follow the instructions in our [contributing guide](/contributing.md)__

License
-------
Spring Web Initializr is licensed under the [MIT](/license.md) license.  
Copyright &copy; 2019, Orestes Polyzos

[shield-release]: https://img.shields.io/badge/release-2.0.0-blue.svg
[shield-jdk]: https://img.shields.io/badge/jdk-8-blue.svg
[shield-spring]: https://img.shields.io/badge/spring-2.2.1-blue.svg
[shield-license]: https://img.shields.io/badge/license-MIT-blue.svg
[shield-build]: https://github.com/OrPolyzos/spring-web-initializr/workflows/build/badge.svg
[shield-tests]: https://github.com/OrPolyzos/spring-web-initializr/workflows/tests/badge.svg
[shield-coverage]: https://img.shields.io/badge/coverage-92%25-brightgreen.svg
