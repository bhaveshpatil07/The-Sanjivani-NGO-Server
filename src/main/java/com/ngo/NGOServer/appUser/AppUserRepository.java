package com.ngo.NGOServer.appUser;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends MongoRepository<AppUser, String> {

    Optional<AppUser> findByEmail(String email);

    @Query(value = "{ 'email' : { $regex: ?0 }, 'appUserRole' : ?1 }", fields = "{ 'email' : 1, '_id':0 }")
    List<String> findbyEmailContainingAndAppUserRole(String keyword, String userRole);

    // Custom method to enable an AppUser by email
    default int enableAppUser(String email) {
        Optional<AppUser> appUser = findByEmail(email);
        if (appUser.isPresent()) {
            AppUser user = appUser.get();
            user.setEnabled(true);
            save(user);
            return 1; // Return 1 to indicate success
        }
        return 0; // Return 0 to indicate failure
    }
}
