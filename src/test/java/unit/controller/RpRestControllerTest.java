package unit.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import unit.domain.User;
import ore.spring.web.initializr.controller.impl.RpRestController;
import ore.spring.web.initializr.exception.RpDuplicateResourceException;
import ore.spring.web.initializr.exception.RpMissingResourceException;
import ore.spring.web.initializr.service.impl.NoDtoRpService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.repository.CrudRepository;
import unit.repository.UserMockRepository;

public class RpRestControllerTest {

  private RpRestController<User, Long> rpRestController;

  private User userA = User.builder()
      .firstName("userA")
      .lastName("userA")
      .email("userA@userA.com")
      .build();

  private User userB = User.builder()
      .firstName("userB")
      .lastName("userB")
      .email("userB@userB.com")
      .build();

  @Before
  public void setup() {
    CrudRepository<User, Long> crudRepository = new UserMockRepository();
    crudRepository.save(userA);
    crudRepository.save(userB);

    NoDtoRpService<User, Long> noDtoUserService = () -> crudRepository;

    rpRestController = () -> noDtoUserService;
  }

  @Test
  public void getService() {
    assertThat(rpRestController.getService() != null);
  }

  @Test
  public void readAll() {
    assertThat(rpRestController.readAll().size()).isEqualTo(2);
  }

  @Test(expected = RpMissingResourceException.class)
  public void readAndThrow() {
    rpRestController.read(2L);
  }

  @Test
  public void readAndNotThrow() {
    User nullableUserA = rpRestController.read(0L);
    assertThat(nullableUserA).isNotNull();
    assertFieldsForUsers(nullableUserA, userA);

    User nullableUserB = rpRestController.read(1L);
    assertThat(nullableUserB).isNotNull();
    assertFieldsForUsers(nullableUserB, userB);
  }

  @Test
  public void createNewUser() {
    User newUser = User.builder()
        .firstName("userC")
        .lastName("userC")
        .email("userC@userC.com")
        .build();

    User insertedUser = rpRestController.create(newUser);
    assertThat(insertedUser).isNotNull();
    assertFieldsForUsers(newUser, insertedUser);
    assertThat(rpRestController.read(insertedUser.getId())).isNotNull();
  }

  @Test(expected = RpDuplicateResourceException.class)
  public void insertExistingUser() {
    User existingUser = User.builder()
        .id(1L)
        .firstName("userC")
        .lastName("userC")
        .email("userC@userC.com")
        .build();

    rpRestController.create(existingUser);
  }

  @Test
  public void updateExistingUser() {
    User newUser = User.builder()
        .id(1L)
        .firstName("userC")
        .lastName("userC")
        .email("userC@userC.com")
        .build();

    User updatedUser = rpRestController.update(newUser);
    assertThat(updatedUser).isNotNull();
    assertFieldsForUsers(newUser, updatedUser);
    assertThat(rpRestController.read(updatedUser.getId())).isNotNull();
  }

  @Test(expected = RpMissingResourceException.class)
  public void updateNonExistingUser() {
    User newUser = User.builder()
        .id(2L)
        .firstName("userC")
        .lastName("userC")
        .email("userC@userC.com")
        .build();

    rpRestController.update(newUser);
  }


  @Test
  public void deleteExistingUser() {
    rpRestController.delete(0L);
    assertThat(rpRestController.readAll().size()).isEqualTo(1L);
  }

  @Test(expected = RpMissingResourceException.class)
  public void deleteNonExistingUser() {
    rpRestController.delete(2L);
  }

  private void assertFieldsForUsers(User user, User otherUser) {
    assertThat(user.getFirstName()).isEqualTo(otherUser.getFirstName());
    assertThat(user.getLastName()).isEqualTo(otherUser.getLastName());
    assertThat(user.getEmail()).isEqualTo(otherUser.getEmail());
  }

}
