package vn.hoidanit.jobhunter.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.RespCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.RespResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.RespUpdateResumeDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.ResumeRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository,
            JobRepository jobRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public RespCreateResumeDTO handleCreateResume(Resume reqResume) throws IdInvalidException {
        // ==> check user and job not exist
        if (reqResume.getUser() == null || reqResume.getJob() == null) {
            throw new IdInvalidException("User/Job khong ton tai");
        }
        User user = this.userRepository.findById(reqResume.getUser().getId())
                .orElseThrow(() -> new IdInvalidException("User/Job khong ton tai"));
        Job job = this.jobRepository.findById(reqResume.getJob().getId())
                .orElseThrow(() -> new IdInvalidException("User/Job khong ton tai"));

        reqResume.setUser(user);
        reqResume.setJob(job);
        Resume resume = this.resumeRepository.save(reqResume);

        RespCreateResumeDTO dto = new RespCreateResumeDTO();
        dto.setId(resume.getId());
        dto.setCreatedAt(resume.getCreatedAt());
        dto.setCreatedBy(resume.getCreatedBy());

        return dto;
    }

    public RespUpdateResumeDTO handleUpdateResume(Resume reqResume) throws IdInvalidException {
        // ==> check if resume exist
        Resume resume = this.resumeRepository.findById(reqResume.getId())
                .orElseThrow(() -> new IdInvalidException("Resume khong ton tai"));

        resume.setStatus(reqResume.getStatus());
        this.resumeRepository.save(resume);

        RespUpdateResumeDTO dto = new RespUpdateResumeDTO();
        dto.setCreatedAt(resume.getCreatedAt());
        dto.setCreatedBy(resume.getCreatedBy());

        return dto;
    }

    public void handleDeleteResume(Long id) throws IdInvalidException {
        Resume resume = this.resumeRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Resume khong ton tai"));
        this.resumeRepository.deleteById(id);
    }

    public RespResumeDTO handleGetResume(Long id) throws IdInvalidException {
        Resume resume = this.resumeRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Resume khong ton tai"));

        RespResumeDTO dto = toRespResumeDTO(resume);
        return dto;
    }

    public ResultPaginationDTO handleGetAllResume(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageResume.getNumber());
        meta.setPageSize(pageResume.getSize());
        meta.setPages(pageResume.getTotalPages());
        meta.setTotal(pageResume.getTotalElements());
        res.setMeta(meta);
        res.setResult(pageResume.getContent().stream().map(item -> toRespResumeDTO(item)).toList());

        return res;
    }

    private RespResumeDTO toRespResumeDTO(Resume item) {
        RespResumeDTO dto = new RespResumeDTO();
        dto.setId(item.getId());
        dto.setCreatedAt(item.getCreatedAt());
        dto.setCreatedBy(item.getCreatedBy());
        dto.setEmail(item.getEmail());
        dto.setUrl(item.getUrl());
        RespResumeDTO.JobOfResume jobOfResume = new RespResumeDTO.JobOfResume(item.getJob().getId(),
                item.getJob().getName());
        dto.setJob(jobOfResume);
        RespResumeDTO.UserOfResume userOfResume = new RespResumeDTO.UserOfResume(item.getUser().getId(),
                item.getUser().getName());
        dto.setUser(userOfResume);
        dto.setStatus(item.getStatus());
        dto.setUpdatedAt(item.getUpdatedAt());
        dto.setUpdatedBy(item.getUpdatedBy());
        return dto;
    }
}
