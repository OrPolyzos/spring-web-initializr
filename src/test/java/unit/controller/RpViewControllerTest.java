package unit.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Collections;
import java.util.List;
import ore.spring.web.initializr.controller.impl.RpViewController;
import ore.spring.web.initializr.exception.RpDuplicateResourceException;
import ore.spring.web.initializr.exception.RpMissingResourceException;
import ore.spring.web.initializr.service.api.ResourcePersistableService;
import ore.spring.web.initializr.service.impl.NoDtoRpService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.support.BindingAwareConcurrentModel;
import unit.domain.User;
import unit.repository.UserMockRepository;

public class RpViewControllerTest {


  private RpViewController<User, Long> rpViewController;

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
    class UserRpViewController implements RpViewController<User, Long> {

      private UserMockRepository userMockRepository = new UserMockRepository();

      @Override
      public ResourcePersistableService<User, Long> getService() {
        return (NoDtoRpService<User, Long>) () -> userMockRepository;
      }
    }

    rpViewController = new UserRpViewController();
    rpViewController.getService().insert(userA);
    rpViewController.getService().insert(userB);
  }

  @Test
  public void getService() {
    assertThat(rpViewController.getService() != null);
  }

  @Test
  public void getHolder() {
    assertThat(rpViewController.getHolder()).isEqualTo("user");
  }

  @Test
  public void getListHolder() {
    assertThat(rpViewController.getListHolder()).isEqualTo("userList");
  }

  @Test
  public void getBaseViewPath() {
    assertThat(rpViewController.getBaseViewPath()).isEqualTo("/user/user");
  }

  @Test
  public void getUpdateViewPath() {
    assertThat(rpViewController.getUpdateViewPath()).isEqualTo("/user/edit-user");
  }

  @Test
  public void getRpDto() {
    assertThat(rpViewController.getRpDto().getId()).isNull();
    assertThat(rpViewController.getRpDto().getFirstName()).isNull();
    assertThat(rpViewController.getRpDto().getLastName()).isNull();
    assertThat(rpViewController.getRpDto().getEmail()).isNull();
  }

  @Test
  public void getBaseView() {
    Model model = new BindingAwareConcurrentModel();
    String path = rpViewController.getBaseView(model);
    assertThat(path).isEqualTo("/user/user");

    Object user = model.asMap().get(rpViewController.getHolder());
    assertThat(user).isNotNull();

    assertThat(((User) user).getId()).isNull();
    assertThat(((User) user).getFirstName()).isNull();
    assertThat(((User) user).getLastName()).isNull();
    assertThat(((User) user).getEmail()).isNull();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void readAll() {
    Model model = new BindingAwareConcurrentModel();
    String path = rpViewController.readAll(model);
    assertThat(path).isEqualTo("/user/user");

    Object userList = model.asMap().get(rpViewController.getListHolder());
    assertThat(userList).isNotNull();

    assertThat(((List<User>) userList).size()).isEqualTo(2);
  }

  @Test
  public void getUpdateViewForExistingUser() {
    Model model = new BindingAwareConcurrentModel();
    String path = rpViewController.getUpdateView(0L, model);
    assertThat(path).isEqualTo("/user/edit-user");

    Object user = model.asMap().get(rpViewController.getHolder());
    assertThat(user).isNotNull();
    assertFieldsForUsers((User) user, userA);
  }

  @Test
  public void getUpdateViewForExistingUserAlreadyInModel() {
    Model model = new BindingAwareConcurrentModel();
    model.addAttribute(rpViewController.getHolder(), userA);

    String path = rpViewController.getUpdateView(0L, model);
    assertThat(path).isEqualTo("/user/edit-user");

    Object user = model.asMap().get(rpViewController.getHolder());
    assertThat(user).isNotNull();
    assertFieldsForUsers((User) user, userA);
  }

  @Test
  public void createValidUser() {
    Model model = new BindingAwareConcurrentModel();
    BindingResult validationResult = new MapBindingResult(Collections.emptyMap(), "notImportant");

    User newUser = User.builder()
        .firstName("userC")
        .lastName("userC")
        .email("userC@userC.com")
        .build();

    String path = rpViewController.create(newUser, validationResult, model);

    assertThat(path).isEqualTo("/user/user");
    assertThat(model.getAttribute(rpViewController.getInfoMessageHolder())).isNotNull();
    assertThat(rpViewController.getService().findAll().count()).isEqualTo(3);
  }

  @Test(expected = RpDuplicateResourceException.class)
  public void createValidExistingUserAndThrow() {
    Model model = new BindingAwareConcurrentModel();
    BindingResult validationResult = new MapBindingResult(Collections.emptyMap(), "notImportant");

    User newUser = User.builder()
        .id(0L)
        .firstName("userC")
        .lastName("userC")
        .email("userC@userC.com")
        .build();

    rpViewController.create(newUser, validationResult, model);
  }

  @Test
  public void updateValidUser() {
    Model model = new BindingAwareConcurrentModel();
    BindingResult validationResult = new MapBindingResult(Collections.emptyMap(), "notImportant");

    User newUser = User.builder()
        .id(0L)
        .firstName("userC")
        .lastName("userC")
        .email("userC@userC.com")
        .build();

    String path = rpViewController.update(newUser.getId(), newUser, validationResult, model);

    assertThat(path).isEqualTo("/user/user");
    assertThat(model.getAttribute(rpViewController.getInfoMessageHolder())).isNotNull();
    assertThat(rpViewController.getService().findAll().count()).isEqualTo(2);
  }

  @Test(expected = RpMissingResourceException.class)
  public void updateValidExistingUserAndThrow() {
    Model model = new BindingAwareConcurrentModel();
    BindingResult validationResult = new MapBindingResult(Collections.emptyMap(), "notImportant");

    User newUser = User.builder()
        .id(3L)
        .firstName("userC")
        .lastName("userC")
        .email("userC@userC.com")
        .build();

    rpViewController.update(newUser.getId(), newUser, validationResult, model);
  }

  @Test
  public void deleteUser() {
    Model model = new BindingAwareConcurrentModel();
    String path = rpViewController.delete(0L, model);

    assertThat(path).isEqualTo("/user/user");
    assertThat(model.getAttribute(rpViewController.getInfoMessageHolder())).isNotNull();
    assertThat(rpViewController.getService().findAll().count()).isEqualTo(1);
  }


  @Test(expected = RpMissingResourceException.class)
  public void deleteUserAnThrow() {
    Model model = new BindingAwareConcurrentModel();
    rpViewController.delete(3L, model);
  }

  private void assertFieldsForUsers(User user, User otherUser) {
    assertThat(user.getFirstName()).isEqualTo(otherUser.getFirstName());
    assertThat(user.getLastName()).isEqualTo(otherUser.getLastName());
    assertThat(user.getEmail()).isEqualTo(otherUser.getEmail());
  }

}
