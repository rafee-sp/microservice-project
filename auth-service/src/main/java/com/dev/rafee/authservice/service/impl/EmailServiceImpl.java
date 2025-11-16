package com.dev.rafee.authservice.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.dev.rafee.authservice.entity.UserVerification;
import com.dev.rafee.authservice.entity.Users;
import com.dev.rafee.authservice.repository.UserVerificationRepository;
import com.dev.rafee.authservice.service.EmailService;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService{

	private final JavaMailSender javaMailSender;
	private final UserVerificationRepository userVerificationRepository;

	@Async
	public void sendVerificationCode(Users user, String type) {

		try {
			
			log.info("send verification method called for {}", type);

			if(!type.equalsIgnoreCase("new")) {
				
				userVerificationRepository.deleteByUserId(user.getId());
			}

			log.info("sending code.. ");

			// Email sending
			String verificationCode = String.format("%06d", new SecureRandom().nextInt(1000000));

			UserVerification verificationRecord = new UserVerification();
			verificationRecord.setUser(user);
			verificationRecord.setVerificationCode(verificationCode);
			verificationRecord.setExpiryAt(LocalDateTime.now().plusHours(2));
			userVerificationRepository.save(verificationRecord);

			String htmlContent = buildHTMLContent(verificationCode);

			MimeMessage message = javaMailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message,true);

			helper.setTo(user.getEmail());
			helper.setSubject("Your email verification code ");
			helper.setText(htmlContent, true);

			log.info("code "+verificationCode);

			//	javaMailSender.send(message);

		} catch (Exception e) {
			log.info("An error occured {}",e);
		}

	}

	// MOVE TO DB
	private String buildHTMLContent(String otp) {
		return "<html>"
				+ "<head><style>"
				+ "body { font-family: Arial, sans-serif; }"
				+ ".container { text-align: center; padding: 20px; border: 1px solid #ccc; border-radius: 10px; background-color: #f4f4f4; max-width: 500px; margin: 0 auto; }"
				+ ".header { font-size: 24px; font-weight: bold; color: #333; }"
				+ ".otp-code { font-size: 36px; color: #007BFF; font-weight: bold; padding: 10px; margin-top: 20px; border: 2px solid #007BFF; border-radius: 5px; }"
				+ ".footer { font-size: 14px; color: #888; margin-top: 20px; }"
				+ "</style></head>"
				+ "<body>"
				+ "<div class='container'>"
				+ "<div class='header'>Your OTP Code</div>"
				+ "<div class='otp-code'>" + otp + "</div>"
				+ "<div class='footer'>"
				+ "<p>This OTP is valid for 10 minutes.</p>"
				+ "<p>If you didn't request this OTP, please ignore this email.</p>"
				+ "</div>"
				+ "</div>"
				+ "</body>"
				+ "</html>";
	}

}
