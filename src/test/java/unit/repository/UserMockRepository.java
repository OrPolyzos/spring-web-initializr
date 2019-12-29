package unit.repository;

import unit.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.data.repository.CrudRepository;

public class UserMockRepository implements CrudRepository<User, Long> {

  private final List<User> users = new ArrayList<>();

  @SuppressWarnings("unchecked")
  @Override
  public <S extends User> S save(S entity) {
    User userToAdd = this.getNewUserObject(entity);
    if (userToAdd.getId() == null) {
      userToAdd.setId(count());
      OptionalLong max = users.stream().mapToLong(User::getId).max();
      max.ifPresent(e -> userToAdd.setId(e + 1));
      users.add(userToAdd);
    } else {
      for (int i = 0; i < users.size(); i++) {
        if (users.get(i).getId().equals(userToAdd.getId())) {
          users.set(i, userToAdd);
        }
      }
    }
    return (S) this.getNewUserObject(userToAdd);
  }

  @Override
  public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
    return StreamSupport.stream(entities.spliterator(), false)
        .map(this::save)
        .collect(Collectors.toSet());
  }

  @Override
  public Optional<User> findById(Long aLong) {
    return users.
        parallelStream()
        .filter(e -> e.getId().equals(aLong))
        .findFirst()
        .map(this::getNewUserObject);
  }

  @Override
  public boolean existsById(Long aLong) {
    return users.
        parallelStream()
        .anyMatch(e -> e.getId().equals(aLong));
  }

  @Override
  public Iterable<User> findAll() {
    return users.parallelStream()
        .map(this::getNewUserObject)
        .collect(Collectors.toList());
  }

  @Override
  public Iterable<User> findAllById(Iterable<Long> longs) {
    Set<Long> ids = StreamSupport
        .stream(longs.spliterator(), false)
        .collect(Collectors.toSet());
    return users
        .stream()
        .filter(e -> ids.contains(e.getId()))
        .map(this::getNewUserObject)
        .collect(Collectors.toSet());
  }

  @Override
  public long count() {
    return users.size();
  }

  @Override
  public void deleteById(Long aLong) {
    users.removeIf(user -> user.getId().equals(aLong));
  }

  @Override
  public void delete(User entity) {
    users.removeIf(user -> user.getId().equals(entity.getId()));
  }

  @Override
  public void deleteAll(Iterable<? extends User> entities) {
    Set<Long> ids = StreamSupport
        .stream(entities.spliterator(), false)
        .map(User::getId)
        .collect(Collectors.toSet());

    users.removeIf(user -> ids.contains(user.getId()));
  }

  @Override
  public void deleteAll() {
    users.clear();
  }

  private User getNewUserObject(User user) {
    User newUser = new User();
    newUser.setId(user.getId());
    newUser.setEmail(user.getEmail());
    newUser.setFirstName(user.getFirstName());
    newUser.setLastName(user.getLastName());
    return newUser;
  }
}
