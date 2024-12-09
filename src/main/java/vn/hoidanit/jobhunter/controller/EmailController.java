package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.service.EmailService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final EmailService emailService;

    private EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/email")
    @ApiMessage(Value = "Send simple email")
    public String sendMail() {
        // this.emailService.sendSimpleEmail();
        // this.emailService.sendEmailSync("longvladimir1032002@gmail.com", "test send
        // mail",
        // "<h1> <b> send email successful </b> </h1>", false, true);
        this.emailService.sendEmailFromTemplateSync("longvladimir1032002@gmail.com", "test send mail", "job");
        return "ok";
    }

}
