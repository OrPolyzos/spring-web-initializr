package unit.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import unit.domain.User;
import java.util.Optional;
import ore.spring.web.initializr.exception.RpDuplicateResourceException;
import ore.spring.web.initializr.exception.RpMissingResourceException;
import ore.spring.web.initializr.service.impl.NoDtoRpService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.repository.CrudRepository;
import unit.repository.UserMockRepository;

@RunWith(MockitoJUnitRunner.class)
public class NoDtoRpServiceTest {

  private NoDtoRpService<User, Long> noDtoUserService;

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

    noDtoUserService = () -> crudRepository;

  }

  @Test
  public void getRepository() {
    assertThat(noDtoUserService.getRepository() != null);
  }

  @Test
  public void findAll() {
    assertThat(noDtoUserService.findAll().count()).isEqualTo(2L);
  }

  @Test
  public void findNullableIsNotNull() {
    User nullableUserA = noDtoUserService.findNullable(0L);
    assertThat(nullableUserA).isNotNull();
    assertFieldsForUsers(nullableUserA, userA);

    User nullableUserB = noDtoUserService.findNullable(1L);
    assertThat(nullableUserB).isNotNull();
    assertFieldsForUsers(nullableUserB, userB);
  }

  @Test
  public void findNullableIsNull() {
    assertThat(noDtoUserService.findNullable(2L)).isNull();
  }

  @Test
  public void findOptionalIsNotEmpty() {
    Optional<User> userOptionalA = noDtoUserService.findOptional(0L);
    assertThat(userOptionalA).isNotEmpty();
    assertFieldsForUsers(userOptionalA.get(), userA);

    Optional<User> userOptionalB = noDtoUserService.findOptional(1L);
    assertThat(userOptionalB).isNotEmpty();
    assertFieldsForUsers(userOptionalB.get(), userB);
  }

  @Test(expected = RuntimeException.class)
  public void findOptionalAndThrowRuntime() throws NoSuchMethodException {
    UserMockRepository mockRepository = mock(UserMockRepository.class);
    when(mockRepository.findById(anyLong())).thenThrow(new NoSuchMethodError());
    noDtoUserService = () -> mockRepository;
    noDtoUserService.findOptional(0L);
  }

  @Test
  public void findOptionalIsEmpty() {
    assertThat(noDtoUserService.findOptional(2L)).isEmpty();
  }

  @Test(expected = RpMissingResourceException.class)
  public void findAndThrow() {
    noDtoUserService.findOrThrow(2L);
  }

  @Test
  public void findAndNotThrow() {
    User nullableUserA = noDtoUserService.findOrThrow(0L);
    assertThat(nullableUserA).isNotNull();
    assertFieldsForUsers(nullableUserA, userA);

    User nullableUserB = noDtoUserService.findOrThrow(1L);
    assertThat(nullableUserB).isNotNull();
    assertFieldsForUsers(nullableUserB, userB);
  }

  @Test
  public void insertNewUser() {
    User newUser = User.builder()
        .firstName("userC")
        .lastName("userC")
        .email("userC@userC.com")
        .build();

    User insertedUser = noDtoUserService.insert(newUser);
    assertThat(insertedUser).isNotNull();
    assertFieldsForUsers(newUser, insertedUser);
    assertThat(noDtoUserService.findOptional(insertedUser.getId())).isNotEmpty();
  }

  @Test(expected = RpDuplicateResourceException.class)
  public void insertExistingUser() {
    User newUser = User.builder()
        .id(1L)
        .firstName("userC")
        .lastName("userC")
        .email("userC@userC.com")
        .build();

    noDtoUserService.insert(newUser);
  }

  @Test
  public void updateExistingUser() {
    User newUser = User.builder()
        .id(1L)
        .firstName("userC")
        .lastName("userC")
        .email("userC@userC.com")
        .build();

    User updatedUser = noDtoUserService.update(newUser);
    assertThat(updatedUser).isNotNull();
    assertFieldsForUsers(newUser, updatedUser);
    assertThat(noDtoUserService.findOptional(updatedUser.getId())).isNotEmpty();
  }

  @Test(expected = RpMissingResourceException.class)
  public void updateNonExistingUser() {
    User newUser = User.builder()
        .id(2L)
        .firstName("userC")
        .lastName("userC")
        .email("userC@userC.com")
        .build();

    noDtoUserService.update(newUser);
  }


  @Test
  public void deleteExistingUser() {
    noDtoUserService.deleteById(0L);
    assertThat(noDtoUserService.findOptional(userA.getId())).isEmpty();
  }

  @Test(expected = RpMissingResourceException.class)
  public void deleteNonExistingUser() {
    noDtoUserService.deleteById(2L);
  }

  private void assertFieldsForUsers(User user, User otherUser) {
    assertThat(user.getFirstName()).isEqualTo(otherUser.getFirstName());
    assertThat(user.getLastName()).isEqualTo(otherUser.getLastName());
    assertThat(user.getEmail()).isEqualTo(otherUser.getEmail());
  }
}
