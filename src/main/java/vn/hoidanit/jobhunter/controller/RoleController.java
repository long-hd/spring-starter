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
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    @ApiMessage(Value = "Create new role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role reqRole) throws IdInvalidException {
        Role role = this.roleService.handleCreateRole(reqRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @PutMapping
    @ApiMessage(Value = "Update role")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody Role reqRole) throws IdInvalidException {
        Role role = this.roleService.handleUpdateRole(reqRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @GetMapping
    @ApiMessage(Value = "Get pagination role")
    public ResponseEntity<ResultPaginationDTO> getAllRole(@Filter Specification<Role> spec, Pageable pageable) {
        ResultPaginationDTO dto = this.roleService.handleGetAllRole(spec, pageable);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") Long id) throws IdInvalidException {
        this.roleService.handleDeleteRole(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("{id}")
    @ApiMessage(Value = "Get role by id")
    public ResponseEntity<Role> getRole(@PathVariable("id") Long id) throws IdInvalidException {
        Role role = this.roleService.handleGetRole(id);
        return ResponseEntity.ok().body(role);
    }
}
