package vn.hoidanit.jobhunter.domain.response.email;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class RespEmailJob {
    private String name;
    private double salary;
    private CompanyName company;
    private List<SkillName> skills;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyName {
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillName {
        private String name;
    }
}
