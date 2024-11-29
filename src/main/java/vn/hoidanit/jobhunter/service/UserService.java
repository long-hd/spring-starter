package vn.hoidanit.jobhunter.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User handleGetUser(long id) {
        User user = this.userRepository.findById(id).orElse(null);
        return user;
    }

    public List<User> handleGetAllUser() {
        List<User> users = this.userRepository.findAll();
        return users;
    }

    public User handleUpdateUser(User updateUser) {
        User user = this.userRepository.findById(updateUser.getId()).orElse(null);

        if (user != null) {
            user.setName(updateUser.getName());
            user.setEmail(updateUser.getEmail());
            user.setPassword(updateUser.getPassword());
            // update
            user = this.userRepository.save(user);
        }

        return user;
    }
}
