package com.ngo.NGOServer.appUser;

// import com.example.demo.registration.token.ConfirmationToken;
// import com.example.demo.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ngo.NGOServer.registration.EmailValidator;
import com.ngo.NGOServer.registration.RegistrationRequest;
import com.ngo.NGOServer.registration.token.ConfirmationToken;
import com.ngo.NGOServer.registration.token.ConfirmationTokenService;


@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "User with this email %s NOT found!";

    @Autowired
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailValidator emailValidator;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String userExist(String email) {
        boolean isValidEmail = emailValidator.test(email);
        if(!isValidEmail){
                return "Invalid Email: " + email;
        }
        boolean exists = appUserRepository.findByEmail(email).isPresent();
        return exists?"Login":"SignUp";
    }

    public String signUpUser(AppUser appUser) {
        boolean userExists = appUserRepository
                .findByEmail(appUser.getEmail())
                .isPresent();

        if (userExists) {
                // TODO check of attributes are the same and
                // TODO if email not confirmed send confirmation email.
                throw new IllegalStateException("Email is Already Taken!!!");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUserRepository.save(appUser);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        //TODO: Send Email
        
        return token;
    }

    public ResponseEntity<String> loginUser(RegistrationRequest request) {
        Optional<AppUser> userOptional = appUserRepository
                .findByEmail(request.getEmail());

        if (userOptional.isPresent()) {
                AppUser user = userOptional.get();
                if(user.getEnabled()){
                        if(bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())){
                                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy @HH:mm:ss");
                                user.getLogs().add(0, formatter.format(new Date()));
                                appUserRepository.save(user);
                                return ResponseEntity.ok("Login Successful!");
                        }
                        return ResponseEntity.status(401).body("Invalid Credentials!");
                }
                return ResponseEntity.status(403).body("Email Not verified!");
        }

        return ResponseEntity.status(404).body("User not found");
    }

    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }

    public String getRecentLogin(String email) {
        if(!email.isEmpty()){
                return appUserRepository.findByEmail(email).get().getLogs().get(1);
        }
        return "";
    }
}
