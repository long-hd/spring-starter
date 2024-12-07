package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class PermissionService {
    public final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission handleCreatePermission(Permission reqPermission) throws IdInvalidException {
        // ==> check if permission exist
        if (this.permissionRepository.existsByApiPathAndMethodAndModule(
                reqPermission.getApiPath(), reqPermission.getMethod(),
                reqPermission.getModule())) {
            throw new IdInvalidException("Permission đã tồn tại");
        }

        // save
        return this.permissionRepository.save(reqPermission);
    }

    public Permission handleUpdatePermission(Permission reqPermission) throws IdInvalidException {
        // ==> check permission not exist
        Permission permissionDb = this.permissionRepository.findById(reqPermission.getId())
                .orElseThrow(() -> new IdInvalidException("Permission không tồn tại"));
        // ==> check if permission with (apiPath, method, module) exist
        if (this.permissionRepository.existsByApiPathAndMethodAndModule(
                reqPermission.getApiPath(), reqPermission.getMethod(),
                reqPermission.getModule())) {
            if (this.permissionRepository.existsByName(reqPermission.getName())) {
                throw new IdInvalidException("Permission đã tồn tại");
            }
        }

        // save
        permissionDb.setApiPath(reqPermission.getApiPath());
        permissionDb.setMethod(reqPermission.getMethod());
        permissionDb.setModule(reqPermission.getModule());
        return this.permissionRepository.save(permissionDb);
    }

    public ResultPaginationDTO handleGetAllPermission(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermission = this.permissionRepository.findAll(spec, pageable);

        ResultPaginationDTO dto = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pagePermission.getNumber() + 1);
        meta.setPageSize(pagePermission.getSize());
        meta.setPages(pagePermission.getTotalPages());
        meta.setTotal(pagePermission.getTotalElements());
        dto.setMeta(meta);
        dto.setResult(pagePermission.getContent());

        return dto;
    }

    public void handleDeletePermission(Long id) throws IdInvalidException {
        // ==> check if permission not exist
        Permission permission = this.permissionRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Permission không tồn tại"));

        // xoá permission trong từng role
        permission.getRoles().forEach(role -> role.getPermissions().remove(permission));

        // delete permission
        this.permissionRepository.delete(permission);
    }

}
