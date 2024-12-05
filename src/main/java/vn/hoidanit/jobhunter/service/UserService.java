package vn.hoidanit.jobhunter.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.RespCreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.RespUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.RespUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RespCreateUserDTO handleCreateUser(User user) throws IdInvalidException {
        // ==> check email exist
        if (this.userRepository.existsByEmail(user.getEmail())) {
            throw new IdInvalidException("Email " + user.getEmail() + " da ton tai, vui long su dung email khac");
        }

        user = this.userRepository.save(user);

        RespCreateUserDTO dto = new RespCreateUserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAddress(user.getAddress());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());
        dto.setCreatedAt(user.getCreatedAt());

        return dto;
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public RespUserDTO handleGetUser(long id) {
        RespUserDTO dto = toRespUserDTO(this.userRepository.findById(id).orElse(null));
        return dto;
    }

    public ResultPaginationDTO handleGetAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUsers = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO dto = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageUsers.getNumber() + 1);
        meta.setPageSize(pageUsers.getSize());
        meta.setPages(pageUsers.getTotalPages());
        meta.setTotal(pageUsers.getTotalElements());

        dto.setMeta(meta);
        dto.setResult(pageUsers.getContent().stream().map((user) -> toRespUserDTO(user)).toList());

        return dto;
    }

    public RespUpdateUserDTO handleUpdateUser(User updateUser) throws IdInvalidException {
        User user = this.userRepository.findById(updateUser.getId())
                .orElseThrow(() -> new IdInvalidException("User voi id = " + updateUser.getId() + " khong ton tai."));

        user.setName(updateUser.getName());
        user.setAddress(updateUser.getAddress());
        user.setAge(updateUser.getAge());
        user.setGender(updateUser.getGender());
        // update
        user = this.userRepository.save(user);
        // create response
        RespUpdateUserDTO dto = new RespUpdateUserDTO();
        dto.setName(user.getName());
        dto.setId(user.getId());
        dto.setAddress(user.getAddress());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());
        dto.setUpdatedAt(user.getUpdatedAt());

        return dto;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    private RespUserDTO toRespUserDTO(User user) {
        RespUserDTO dto = new RespUserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setAddress(user.getAddress());
        dto.setAge(user.getAge());
        dto.setEmail(user.getEmail());
        dto.setGender(user.getGender());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getCreatedAt());
        return dto;
    }

    public void updateUserRefreshToken(String email, String token) {
        User user = this.handleGetUserByUsername(email);
        if (user != null) {
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }

    public User getByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public void deleteRefreshToken(User user) {
        user.setRefreshToken(null);
        this.userRepository.save(user);
    }
}
