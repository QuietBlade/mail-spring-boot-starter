package org.example.mail.service;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: world
 * @date: 2022/5/30 17:52
 * @description:
 */
@Service
public class MailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送简单文本格式邮件
     * @param to 收件人
     * @param subject 主题
     * @param content 正文内容 (html语法会以字符串输出)
     */
    public void sendSimpleMail(String to, String subject, String content){
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);
    }

    /**
     * 发送HTMl模板并带附件的邮件 ( 不足: 邮件正文仍然是文本
     * @param to 收件人
     * @param subject 主题
     * @param html 正文(支持html格式)
     * @param pathToAttachment 附件信息, 接收Map对象, 且值必须是 File , InputStreamSource , DataSource 中的一种
     * @exception MessagingException 抛出发送邮件异常，可以做异步消息处理
     */
    public void sendMimeMail(String to, String subject, String html, Map<String, InputStreamSource> pathToAttachment) throws MessagingException {
        // 避免附件为null
        if (pathToAttachment == null) { pathToAttachment = new HashMap<>(0); }
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html,true);
        if (!pathToAttachment.isEmpty()){
            for (String s : pathToAttachment.keySet()) {
                helper.addAttachment(s, pathToAttachment.get(s));
            }
        }
        javaMailSender.send(mimeMessage);
    }

    /**
     * 发送验证码邮件
     * @param email 收件人
     * @param operaName 作为邮件主题和正文提示, 操作名称目前可选( 修改密码 、 忘记密码 、 修改邮箱地址 )
     * @param username 操作人(用户名称)
     * @param verifyCode 验证码
     * @exception MessagingException 抛出一个异常,可以捕获次异常做异步处理
     */
    public void sendVerifyCode(String email, String operaName, String username, String verifyCode) throws MessagingException {
        final ClassPathResource resource = new ClassPathResource("mail-templates/verify-code.html");
        String result = null;
        try {
            result = CharStreams.toString(new InputStreamReader(resource.getInputStream(), Charsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final String title = "统信平台 - " + operaName;
        // 此模板共4个参数, 依次分别是 serverName 操作名称,username 用户名,verifyCode 验证码,datetime 发送时间

        final String content = String.format(result, operaName, username, verifyCode, newDate());
        // 发送验证邮件
        this.sendMimeMail(email,title,content,null);
    }

    public String newDate(){
        final SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(new Date());
    }

}
