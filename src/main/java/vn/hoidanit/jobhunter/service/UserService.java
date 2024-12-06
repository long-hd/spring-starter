package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.RespCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.RespUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.RespUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, CompanyRepository companyRepository,
            RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.roleRepository = roleRepository;
    }

    public RespCreateUserDTO handleCreateUser(User user) throws IdInvalidException {
        // ==> check email exist
        if (this.userRepository.existsByEmail(user.getEmail())) {
            throw new IdInvalidException("Email " + user.getEmail() + " da ton tai, vui long su dung email khac");
        }

        // ==> check if company exist, and set
        Company company = new Company();
        if (user.getCompany() != null) {
            company = this.companyRepository.findById(user.getCompany().getId()).orElse(null);
            user.setCompany(company);
        }

        // ==> check if role exist, and set
        Role role = new Role();
        if (user.getRole() != null) {
            role = this.roleRepository.findById(user.getRole().getId()).orElse(null);
            user.setRole(role);
        }

        user = this.userRepository.save(user);

        // create response
        RespCreateUserDTO dto = new RespCreateUserDTO();
        RespCreateUserDTO.CompanyOfUser companyOfUser = new RespCreateUserDTO.CompanyOfUser();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAddress(user.getAddress());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());
        dto.setCreatedAt(user.getCreatedAt());
        if (user.getCompany() != null) {
            companyOfUser.setId(user.getCompany().getId());
            companyOfUser.setName(user.getCompany().getName());
            dto.setCompany(companyOfUser);
        }

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
        // ==> check if user exist
        User user = this.userRepository.findById(updateUser.getId())
                .orElseThrow(() -> new IdInvalidException("User voi id = " + updateUser.getId() + " khong ton tai."));

        // ==> check if company exist
        Company company = new Company();
        if (updateUser.getCompany() != null) {
            company = this.companyRepository.findById(updateUser.getCompany().getId()).orElse(null);
            user.setCompany(company);
        }

        // ==> check if role exist, and set
        Role role = new Role();
        if (user.getRole() != null) {
            role = this.roleRepository.findById(updateUser.getRole().getId()).orElse(null);
            user.setRole(role);
        }

        user.setName(updateUser.getName());
        user.setAddress(updateUser.getAddress());
        user.setAge(updateUser.getAge());
        user.setGender(updateUser.getGender());
        // save update
        user = this.userRepository.save(user);
        // create response
        RespUpdateUserDTO dto = new RespUpdateUserDTO();
        RespUpdateUserDTO.CompanyOfUser companyOfUser = new RespUpdateUserDTO.CompanyOfUser();
        if (user.getCompany() != null) {
            companyOfUser.setId(user.getCompany().getId());
            companyOfUser.setName(user.getCompany().getName());
            dto.setCompany(companyOfUser);
        }
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
        RespUserDTO.CompanyOfUser companyOfUser = new RespUserDTO.CompanyOfUser();
        RespUserDTO.RoleOfUser roleOfUser = new RespUserDTO.RoleOfUser();

        if (user.getCompany() != null) {
            companyOfUser.setId(user.getCompany().getId());
            companyOfUser.setName(user.getCompany().getName());
            dto.setCompany(companyOfUser);
        }

        if (user.getRole() != null) {
            roleOfUser.setId(user.getRole().getId());
            roleOfUser.setName(user.getRole().getName());
            dto.setRole(roleOfUser);
        }

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

    // public void deleteRefreshToken(User user) {
    // user.setRefreshToken(null);
    // this.userRepository.save(user);
    // }
}
