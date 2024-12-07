package vn.hoidanit.jobhunter.controller;

import java.net.ResponseCache;

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
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.RespCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.RespResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.RespUpdateResumeDTO;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping
    public ResponseEntity<RespCreateResumeDTO> createResume(@Valid @RequestBody Resume reqResume)
            throws IdInvalidException {
        RespCreateResumeDTO dto = this.resumeService.handleCreateResume(reqResume);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping
    public ResponseEntity<RespUpdateResumeDTO> upadateResume(@RequestBody Resume reqResume) throws IdInvalidException {
        RespUpdateResumeDTO dto = this.resumeService.handleUpdateResume(reqResume);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") Long resume_id) throws IdInvalidException {
        this.resumeService.handleDeleteResume(resume_id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("{id}")
    public ResponseEntity<RespResumeDTO> getResume(@PathVariable("id") Long id) throws IdInvalidException {
        RespResumeDTO dto = this.resumeService.handleGetResume(id);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getAllResume(@Filter Specification<Resume> spec, Pageable pageable) {
        ResultPaginationDTO dto = this.resumeService.handleGetAllResume(spec, pageable);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping("/by-user")
    @ApiMessage(Value = "Get resume by user")
    public ResponseEntity<ResultPaginationDTO> getResumeByUser(Pageable pageable) {
        ResultPaginationDTO dto = this.resumeService.handleGetResumeByUser(pageable);
        return ResponseEntity.ok().body(dto);
    }
}
