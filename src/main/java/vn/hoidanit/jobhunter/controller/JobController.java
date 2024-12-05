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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.RespCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.RespUpdateJobDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<RespCreateJobDTO> createJob(@RequestBody Job reqJob) {
        RespCreateJobDTO dto = this.jobService.handleCreateJob(reqJob);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping
    public ResponseEntity<RespUpdateJobDTO> updateJob(@RequestBody Job reqJob) throws IdInvalidException {
        RespUpdateJobDTO dto = this.jobService.handleUpdateJob(reqJob);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("{id}")
    @ApiMessage(Value = "Xoá job")
    public ResponseEntity<RespUpdateJobDTO> deleteJob(@PathVariable("id") Long id) {
        this.jobService.handleDeleteJob(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping
    @ApiMessage(Value = "Lấy tất cả các job")
    public ResponseEntity<ResultPaginationDTO> getAllJob(@Filter Specification<Job> spec, Pageable pageable) {
        ResultPaginationDTO dto = this.jobService.handleGetAllJob(spec, pageable);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("{id}")
    public ResponseEntity<Job> getJob(@PathVariable("id") Long id) throws IdInvalidException {
        Job job = this.jobService.handleGetJob(id);
        return ResponseEntity.ok(job);
    }
}
