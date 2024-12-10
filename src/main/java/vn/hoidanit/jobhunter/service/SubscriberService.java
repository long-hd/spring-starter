package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.domain.response.email.RespEmailJob;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository,
            UserRepository userRepository, JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public Subscriber handleCreateSubscriber(Subscriber reqSubscriber) throws IdInvalidException {
        // ==> check if email is used to subscribe
        if (this.userRepository.findByEmail(reqSubscriber.getEmail()) == null) {
            throw new IdInvalidException("Email đã được dùng để theo dõi");
        }
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

    // @Scheduled(cron = "*/10 * * * * *")
    // public void testCron() {
    // System.out.println(">>> TEST CRON");
    // }

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

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {

                        List<RespEmailJob> arr = listJobs.stream().map(
                                job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());

                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }
    }

    private RespEmailJob convertJobToSendEmail(Job job) {
        RespEmailJob respEmailJob = new RespEmailJob();
        respEmailJob.setName(job.getName());
        respEmailJob.setSalary(job.getSalary());
        RespEmailJob.CompanyName companyName = new RespEmailJob.CompanyName(job.getName());
        respEmailJob.setCompany(companyName);
        List<RespEmailJob.SkillName> skills = job.getSkills().stream()
                .map(skill -> new RespEmailJob.SkillName(skill.getName()))
                .toList();
        respEmailJob.setSkills(skills);
        return respEmailJob;
    }

    public Subscriber findByEmail(String email) throws IdInvalidException {
        Subscriber subscriber = this.subscriberRepository.findByEmail(email);
        if (subscriber == null) {
            throw new IdInvalidException("Email " + email + " khong ton tai");
        }
        return subscriber;
    }

}
