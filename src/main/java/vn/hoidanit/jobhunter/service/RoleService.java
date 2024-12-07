package vn.hoidanit.jobhunter.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role handleCreateRole(Role reqRole) throws IdInvalidException {
        // check role name exist
        if (this.roleRepository.existsByName(reqRole.getName())) {
            throw new IdInvalidException("Role " + reqRole.getName() + " đã tồn tại");
        }
        // set list permission
        if (reqRole.getPermissions() != null) {
            List<Long> listPermissionId = reqRole.getPermissions().stream()
                    .map(permission -> permission.getId()).toList();
            List<Permission> permissions = this.permissionRepository.findAllById(listPermissionId);
            reqRole.setPermissions(permissions);
        }

        return this.roleRepository.save(reqRole);
    }

    public Role handleUpdateRole(Role reqRole) throws IdInvalidException {
        // ==> check if role not exist
        Role roleDb = this.roleRepository.findById(reqRole.getId())
                .orElseThrow(() -> new IdInvalidException("Role không tồn tại"));

        // set list permission
        if (reqRole.getPermissions() != null) {
            List<Long> listPermissionId = reqRole.getPermissions().stream()
                    .map(permission -> permission.getId()).toList();
            List<Permission> permissions = this.permissionRepository.findAllById(listPermissionId);
            reqRole.setPermissions(permissions);
        }

        roleDb.setName(reqRole.getName());
        roleDb.setDescription(reqRole.getDescription());
        roleDb.setActive(reqRole.isActive());
        roleDb.setPermissions(reqRole.getPermissions());

        return this.roleRepository.save(roleDb);
    }

    public ResultPaginationDTO handleGetAllRole(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);

        ResultPaginationDTO dto = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageRole.getNumber() + 1);
        meta.setPageSize(pageRole.getSize());
        meta.setPages(pageRole.getTotalPages());
        meta.setTotal(pageRole.getTotalElements());
        dto.setMeta(meta);
        dto.setResult(pageRole.getContent());

        return dto;
    }

    public void handleDeleteRole(Long id) throws IdInvalidException {
        this.roleRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Role không tồn tại"));

        this.roleRepository.deleteById(id);
    }

    public Role handleGetRole(Long id) throws IdInvalidException {
        Role role = this.roleRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Role không tồn tai"));
        return role;
    }
}
