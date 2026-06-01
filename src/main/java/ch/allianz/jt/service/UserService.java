package ch.allianz.jt.service;

import ch.allianz.jt.entity.User;
import java.util.List;

public interface UserService {

  User createUser(User user);

  List<User> getAllUsers();
}