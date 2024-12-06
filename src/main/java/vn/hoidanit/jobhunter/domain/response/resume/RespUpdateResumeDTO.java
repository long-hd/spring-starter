package vn.hoidanit.jobhunter.domain.response.resume;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespUpdateResumeDTO {
    private Instant createdAt;
    private String createdBy;
}
