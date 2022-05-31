package org.example.mail.controller;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.example.mail.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author: world
 * @date: 2022/5/30 17:52
 * @description: 邮件控制类
 */
@RestController
@RequestMapping("/api/mail")
public class MailController {
    @Autowired
    private MailService mailService;

    @GetMapping("send")
    public String send(){
        mailService.sendSimpleMail("demo@163.com","测试SpringBoot发送邮件","<h1>同上</h1>");
        return "发送成功";
    }

    @GetMapping("sendS")
    public String sendS() {

        try {
            mailService.sendVerifyCode("demo@163.com","修改密码","yuanzhangzcc",null);
        }catch (MessagingException e){
            return "发送失败";
        }
        return "发送成功";
    }
}
