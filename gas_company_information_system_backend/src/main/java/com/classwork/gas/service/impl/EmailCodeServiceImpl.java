package com.classwork.gas.service.impl;

import com.classwork.gas.service.EmailCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class EmailCodeServiceImpl implements EmailCodeService {

    private static final Logger log = LoggerFactory.getLogger(EmailCodeServiceImpl.class);

    private final RabbitTemplate rabbitTemplate;
    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;
    private final String mailFrom;
    private final long expireMinutes;
    private final String queueName;
    private final Random random = new Random();

    public EmailCodeServiceImpl(RabbitTemplate rabbitTemplate,
                                StringRedisTemplate redisTemplate,
                                JavaMailSender mailSender,
                                @Value("${email.code.expire-minutes:10}") long expireMinutes,
                                @Value("${email.queue:email-codes}") String queueName,
                                @Value("${spring.mail.username:}") String mailFrom) {
        this.rabbitTemplate = rabbitTemplate;
        this.redisTemplate = redisTemplate;
        this.mailSender = mailSender;
        this.expireMinutes = expireMinutes;
        this.queueName = queueName;
        this.mailFrom = mailFrom;
    }

    @Override
    public void sendCode(String email, String purpose) {
        String redisKey = buildKey(email, purpose);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            throw new ResponseStatusException(BAD_REQUEST, "验证码未使用，请稍后再试");
        }
        String code = String.format("%06d", random.nextInt(1_000_000));
        LocalDateTime now = LocalDateTime.now();
        redisTemplate.opsForValue().set(redisKey, code, expireMinutes, TimeUnit.MINUTES);
        Map<String, Object> payload = Map.of(
                "email", email,
                "purpose", purpose,
                "code", code,
                "expiresAt", now.plusMinutes(expireMinutes).toString()
        );
        rabbitTemplate.convertAndSend(queueName, payload);
        log.info("[demo] dispatched email code message to queue {} for {} => {}", queueName, email, code);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(email);
            message.setSubject("验证码");
            message.setText("您的验证码是 " + code + "，有效期" + expireMinutes + "分钟。若非本人操作请忽略。");
            mailSender.send(message);
            log.info("[demo] smtp mail sent to {}", email);
        } catch (Exception ex) {
            log.warn("[demo] failed to send smtp mail to {}: {}", email, ex.getMessage());
        }
    }

    @Override
    public void verifyCode(String email, String purpose, String code) {
        String redisKey = buildKey(email, purpose);
        String cached = redisTemplate.opsForValue().get(redisKey);
        if (cached == null) {
            throw new ResponseStatusException(BAD_REQUEST, "验证码不存在或已过期");
        }
        if (!cached.equals(code)) {
            throw new ResponseStatusException(BAD_REQUEST, "验证码错误");
        }
        redisTemplate.delete(redisKey);
    }

    private String buildKey(String email, String purpose) {
        return "email:code:" + purpose + ":" + email;
    }
}
