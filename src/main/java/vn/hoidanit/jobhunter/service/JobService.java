package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.RespCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.RespUpdateJobDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public RespCreateJobDTO handleCreateJob(Job reqJob) {
        RespCreateJobDTO dto = new RespCreateJobDTO();
        // set list skill
        List<String> listSkillName = new ArrayList<>();
        if (reqJob.getSkills() != null) {
            List<Long> listSkillId = reqJob.getSkills().stream().map(job -> job.getId()).toList();
            List<Skill> skills = this.skillRepository.findAllById(listSkillId);
            reqJob.setSkills(skills);
            listSkillName = skills.stream().map(skill -> skill.getName()).toList();
        }

        // set company
        if (reqJob.getCompany() != null) {
            Company company = this.companyRepository.findById(reqJob.getCompany().getId())
                    .orElse(null);
            reqJob.setCompany(company);
        }

        Job job = this.jobRepository.save(reqJob);

        dto.setName(job.getName());
        dto.setLocation(job.getLocation());
        dto.setSalary(job.getSalary());
        dto.setQuantity(job.getQuantity());
        dto.setLevel(job.getLevel());
        dto.setDescription(job.getDescription());
        dto.setStartDate(job.getStartDate());
        dto.setEndDate(job.getEndDate());
        dto.setActive(job.isActive());
        dto.setSkills(listSkillName);

        return dto;
    }

    public RespUpdateJobDTO handleUpdateJob(Job reqJob) throws IdInvalidException {
        // ==> check job not exist
        Job job = this.jobRepository.findById(reqJob.getId())
                .orElseThrow(() -> new IdInvalidException("Job không tồn tại"));

        // set list skill
        if (reqJob.getSkills() != null) {
            List<Long> listSkillId = reqJob.getSkills().stream().map(j -> j.getId()).toList();
            List<Skill> skills = this.skillRepository.findAllById(listSkillId);
            job.setSkills(skills);
        }
        List<String> listSkillName = job.getSkills().stream().map(skill -> skill.getName()).toList();

        // set company
        if (reqJob.getCompany() != null) {
            Company company = this.companyRepository.findById(reqJob.getCompany().getId())
                    .orElse(job.getCompany());
            job.setCompany(company);
        }

        // save update
        job.setName(reqJob.getName());
        job.setLocation(reqJob.getLocation());
        job.setSalary(reqJob.getSalary());
        job.setQuantity(reqJob.getQuantity());
        job.setLevel(reqJob.getLevel());
        job.setDescription(reqJob.getDescription());
        job.setStartDate(reqJob.getStartDate());
        job.setEndDate(reqJob.getEndDate());
        job.setActive(reqJob.isActive());
        job = this.jobRepository.save(job);

        // set response
        RespUpdateJobDTO dto = new RespUpdateJobDTO();
        dto.setId(job.getId());
        dto.setName(job.getName());
        dto.setLocation(job.getLocation());
        dto.setSalary(job.getSalary());
        dto.setQuantity(job.getQuantity());
        dto.setLevel(job.getLevel());
        dto.setDescription(job.getDescription());
        dto.setStartDate(job.getStartDate());
        dto.setEndDate(job.getEndDate());
        dto.setActive(job.isActive());
        dto.setUpdatedAt(job.getUpdatedAt());
        dto.setUpdatedBy(job.getUpdatedBy());
        dto.setSkills(listSkillName);

        return dto;
    }

    public void handleDeleteJob(Long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO handleGetAllJob(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO dto = new ResultPaginationDTO();

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageJob.getNumber() + 1);
        meta.setPageSize(pageJob.getSize());
        meta.setPages(pageJob.getTotalPages());
        meta.setTotal(pageJob.getTotalElements());

        dto.setMeta(meta);
        dto.setResult(pageJob.getContent());

        return dto;
    }

    public Job handleGetJob(Long id) throws IdInvalidException {
        return this.jobRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy job"));
    }

}
