package vn.hoidanit.jobhunter.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill handleCreateSkill(Skill skill) throws IdInvalidException {
        // ==> check skill name null
        if (skill.getName() == null) {
            throw new IdInvalidException("Tên skill không được để trống");
        }

        // ==> check skill name exist
        if (this.skillRepository.existsByIgnoreCaseName(skill.getName())) {
            throw new IdInvalidException("Skill đã tồn tại");
        }

        return this.skillRepository.save(skill);
    }

    public Skill handleUpdateSkill(Skill reqSkill) throws IdInvalidException {
        // ==> check skill name null
        if (reqSkill != null && reqSkill.getName() == null) {
            throw new IdInvalidException("Tên skill không được để trống");
        }
        // ==> check skill exist
        Skill skill = this.skillRepository.findById(reqSkill.getId()).orElse(null);
        if (skill == null) {
            throw new IdInvalidException("Skill không tồn tại");
        }

        // ==> check skill name exist
        if (this.skillRepository.existsByIgnoreCaseName(reqSkill.getName())) {
            throw new IdInvalidException("Skill name đã tồn tại");
        }

        skill.setName(reqSkill.getName());

        return this.skillRepository.save(skill);
    }

    public ResultPaginationDTO handleGetAllSkill(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageSkills = this.skillRepository.findAll(spec, pageable);

        ResultPaginationDTO dto = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageSkills.getNumber() + 1);
        meta.setPageSize(pageSkills.getSize());
        meta.setPages(pageSkills.getTotalPages());
        meta.setTotal(pageSkills.getTotalElements());
        dto.setMeta(meta);
        dto.setResult(pageSkills.toList());

        return dto;
    }

    public Skill handleGetSkill(long id) throws IdInvalidException {
        Skill skill = this.skillRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Skill với " + id + " không tồn tại"));

        return skill;
    }

}
