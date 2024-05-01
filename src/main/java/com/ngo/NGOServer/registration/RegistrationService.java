package com.ngo.NGOServer.registration;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ngo.NGOServer.appUser.AppUser;
import com.ngo.NGOServer.appUser.AppUserRole;
import com.ngo.NGOServer.appUser.AppUserService;
import com.ngo.NGOServer.email.EmailSender;
import com.ngo.NGOServer.registration.token.ConfirmationToken;
import com.ngo.NGOServer.registration.token.ConfirmationTokenService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) {
            logger.error("Invalid email: {}", request.getEmail());
            throw new IllegalStateException("Email is not Valid!");
        }
        
        String token = appUserService.signUpUser(
                new AppUser(
                        request.getEmail(),
                        request.getPassword(),
                        AppUserRole.ROLE_USER));

        String link = "https://the-sanjivani-ngo-server.onrender.com/api/v1/registration/confirm?token=" + token;
        String mail = request.getEmail();
        emailSender.send(request.getEmail(), buildEmail(mail.substring(0, mail.indexOf('@')), link));

        return token;
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(
                confirmationToken.getAppUser().getEmail());
        return "Confirmed!";
    }

    private String buildEmail(String name, String link) {
        return "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>Email Confirmation</title>" +
                "<style>" +
                "   body { font-family: Arial, sans-serif; }" +
                "   .container { background-color: #f4f4f4; padding: 20px; }" +
                "   .email-content { max-width: 600px; margin: 0 auto; background-color: #fff; border-radius: 10px; padding: 20px; }" +
                "   .button { display: inline-block; background-color: #007bff; color: #fff; text-decoration: none; padding: 10px 20px; border-radius: 8px; font-size: 15px; font-weight: 700; }" +
                "   .button:hover { background-color: #409eff; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "<div class=\"email-content\">" +
                "<h2 style=\"color: #333;font-size: 28px\">Confirm your email</h2>" +
                "<p style=\"color: #666;font-size: 18px\">Hi " + name + ",</p>" +
                "<p style=\"color: #666;font-size: 18px\">Thank you for registering. Please click on the below link to activate your account:</p>" +
                "<a href=\"" + link + "\" class=\"button\">Activate Now</a>" +
                "<p style=\"color: #ff0000; font-style: italic; font-size: 15px; font-weight: 550;\">The link will expire in 15 minutes.</p>" +
                "<p style=\"color: #666; font-size: 15px;\">If you did not request this email you can safely ignore it.</p>" +
                "<p style=\"color: #666; font-size: 15px;\">See you soon! Kind Regards,</p>" +
                "<p style=\"color: #666; font-size: 15px; margin:0;\">ADMIN,</p>" +
                "<p style=\"color: #666; font-size: 15px; margin:0;\">The Sanjivani NGO, Pune</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
    
}
