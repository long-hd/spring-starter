package vn.hoidanit.jobhunter.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO handleGetAllCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO dto = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getTotalElements());

        dto.setMeta(meta);
        dto.setResult(pageCompany.getContent());
        return dto;
    }

    public void handleDeleteCompany(long id) {
        Company company = this.companyRepository.findById(id).orElse(null);
        if (company != null) {
            List<User> users = this.userRepository.findByCompany(company);
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }

    public Company handleUpdateCompany(Company updateCompany) {
        Company company = this.companyRepository.findById(updateCompany.getId()).orElse(null);
        if (company != null) {
            company.setName(updateCompany.getName());
            company.setAddress(updateCompany.getAddress());
            company.setDescription(updateCompany.getDescription());
            company.setLogo(updateCompany.getLogo());
            return this.companyRepository.save(company);
        }
        return company;
    }

    public Company handleGetCompany(Long id) throws IdInvalidException {
        return this.companyRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Company id = " + id + " khong ton tai"));
    }
}
