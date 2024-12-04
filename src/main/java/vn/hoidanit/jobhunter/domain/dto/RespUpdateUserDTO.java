package vn.hoidanit.jobhunter.domain.dto;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class RespUpdateUserDTO {
    private long id;
    private String name;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant updatedAt;
}