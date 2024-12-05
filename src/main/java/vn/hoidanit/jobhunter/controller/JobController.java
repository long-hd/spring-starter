package vn.hoidanit.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.response.RespCreateJobDTO;
import vn.hoidanit.jobhunter.service.JobService;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // @PostMapping
    // public ResponseEntity<RespCreateJobDTO> createJob(@RequestBody Job reqJob) {
    // RespCreateJobDTO dto = this.jobService.handleCreateJob(reqJob);
    // return ResponseEntity.status(HttpStatus.CREATED).body(null);
    // }
}
