package com.ngo.NGOServer.registration;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ngo.NGOServer.appUser.AppUserService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(path = "/api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final AppUserService appUserService;

    @PostMapping
    public String register(@RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }

    @GetMapping
    public String check(@RequestParam("email") String mail) {
        return appUserService.userExist(mail);
    }

    @PostMapping("/auth")
    public ResponseEntity<String> auth(@RequestBody RegistrationRequest request) {
        return appUserService.loginUser(request);
    }

    @GetMapping("/getLogs")
    public String getUserLogs(@RequestParam("email") String email) {
        return appUserService.getRecentLogin(email);
    }

    @GetMapping(path = "confirm")
    public String confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }

}
