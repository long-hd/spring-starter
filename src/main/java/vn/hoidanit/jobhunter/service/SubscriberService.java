package vn.hoidanit.jobhunter.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository,
            UserRepository userRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }

    public Subscriber handleCreateSubscriber(Subscriber reqSubscriber) throws IdInvalidException {
        // ==> check if email of user not exist
        if (this.userRepository.findByEmail(reqSubscriber.getEmail()) == null) {
            throw new IdInvalidException("Email dùng để theo dõi không tồn tại");
        }

        List<Skill> skills = null;
        if (reqSubscriber.getSkills() != null) {
            List<Long> skillIds = reqSubscriber.getSkills().stream().map(item -> item.getId()).toList();
            skills = this.skillRepository.findAllById(skillIds);
            reqSubscriber.setSkills(skills);
        }

        return this.subscriberRepository.save(reqSubscriber);
    }

    public Subscriber handleUpdateSubscriber(Subscriber reqSubscriber) throws IdInvalidException {
        // ==> check if subscriber id not exist
        Subscriber subscriber = this.subscriberRepository.findById(reqSubscriber.getId()).orElse(null);
        if (subscriber == null) {
            throw new IdInvalidException("ID = " + reqSubscriber.getId() + " không tồn tại");
        }

        // ==> cập nhật danh sách theo dõi
        List<Skill> skills = null;
        if (reqSubscriber.getSkills() != null) {
            List<Long> skillIds = reqSubscriber.getSkills().stream().map(item -> item.getId()).toList();
            skills = this.skillRepository.findAllById(skillIds);
            subscriber.setSkills(skills);
        }

        return this.subscriberRepository.save(subscriber);
    }

}
