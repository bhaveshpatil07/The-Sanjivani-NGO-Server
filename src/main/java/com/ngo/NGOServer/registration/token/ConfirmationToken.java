package com.ngo.NGOServer.registration.token;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.ngo.NGOServer.appUser.AppUser;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "confirmationTokens")
public class ConfirmationToken {

    @Id
    private String id;

    @Field("token")
    private String token;

    @Field("createdAt")
    private LocalDateTime createdAt;

    @Field("expiresAt")
    private LocalDateTime expiresAt;

    @Field("confirmedAt")
    private LocalDateTime confirmedAt;

    @Field("appUser")
    private AppUser appUser;

    public ConfirmationToken(String token,
                             LocalDateTime createdAt,
                             LocalDateTime expiresAt,
                             AppUser appUser) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.appUser = appUser;
    }
}
