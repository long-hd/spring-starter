package vn.hoidanit.jobhunter.domain.response.resume;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.ResumeStateEnum;

@Getter
@Setter
public class RespResumeDTO {
    private long id;
    private String email;
    private String url;
    private ResumeStateEnum status;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    private String companyName;
    private JobOfResume job;
    private UserOfResume user;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JobOfResume {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserOfResume {
        private long id;
        private String name;
    }
}
