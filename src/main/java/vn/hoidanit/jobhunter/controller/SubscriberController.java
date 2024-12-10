package vn.hoidanit.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/subscribers")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping
    @ApiMessage(Value = "Theo dõi kĩ năng")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber reqSubscriber)
            throws IdInvalidException {
        Subscriber subscriber = this.subscriberService.handleCreateSubscriber(reqSubscriber);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriber);
    }

    @PutMapping
    @ApiMessage(Value = "Cập nhật theo dõi")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber reqSubscriber)
            throws IdInvalidException {
        Subscriber subscriber = this.subscriberService.handleUpdateSubscriber(reqSubscriber);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriber);
    }

    @GetMapping("/skills")
    @ApiMessage(Value = "Get subscriber skills")
    public ResponseEntity<Subscriber> getSubscriberSkills() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        Subscriber subscriber = this.subscriberService.findByEmail(email);
        return ResponseEntity.ok().body(subscriber);
    }
}
