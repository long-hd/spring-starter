package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.PermissionService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping
    @ApiMessage(Value = "Create new permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission reqPermission)
            throws IdInvalidException {
        Permission permission = this.permissionService.handleCreatePermission(reqPermission);
        return ResponseEntity.status(HttpStatus.CREATED).body(permission);
    }

    @PutMapping
    @ApiMessage(Value = "Update permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission reqPermission)
            throws IdInvalidException {
        Permission permission = this.permissionService.handleUpdatePermission(reqPermission);
        return ResponseEntity.ok(permission);
    }

    @GetMapping
    @ApiMessage(Value = "Get pagination permission")
    public ResponseEntity<ResultPaginationDTO> getAllPermission(@Filter Specification<Permission> spec,
            Pageable pageable) {
        ResultPaginationDTO dto = this.permissionService.handleGetAllPermission(spec, pageable);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("{id}")
    @ApiMessage(Value = "Delete permission")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") Long id) throws IdInvalidException {
        this.permissionService.handleDeletePermission(id);
        return ResponseEntity.ok(null);
    }
}
