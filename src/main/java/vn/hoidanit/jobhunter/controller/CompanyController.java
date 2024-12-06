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
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company reqCompany) {
        Company company = this.companyService.handleCreateCompany(reqCompany);
        return ResponseEntity.status(HttpStatus.CREATED).body(company);
    }

    @GetMapping("/companies")
    @ApiMessage(Value = "fetch all companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompany(
            @Filter Specification<Company> spec,
            Pageable pageable) {

        return ResponseEntity.ok(this.companyService.handleGetAllCompany(spec, pageable));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) {
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@RequestBody Company updateCompany) {
        Company company = this.companyService.handleUpdateCompany(updateCompany);
        return ResponseEntity.ok(company);
    }

    @GetMapping("/companies/{id}")
    @ApiMessage(Value = "fetch companie")
    public ResponseEntity<Company> getACompany(@PathVariable("id") Long id) throws IdInvalidException {
        Company company = this.companyService.handleGetCompany(id);
        return ResponseEntity.ok(company);
    }
}
