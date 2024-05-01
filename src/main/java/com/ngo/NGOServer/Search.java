package com.ngo.NGOServer;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;

import com.ngo.NGOServer.appUser.AppUser;
import com.ngo.NGOServer.appUser.AppUserRepository;
import com.ngo.NGOServer.donations.Donations;
import com.ngo.NGOServer.donations.DonationsRepository;

@CrossOrigin(origins = {"http://localhost:3000", "https://sanjivaningo.web.app", "https://sanjivaningo.firebaseapp.com"})
@RestController
@RequestMapping(path = "/api/v1/search")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class Search {

    private final AppUserRepository appUserRepository;
    private final DonationsRepository donationsRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public Search(AppUserRepository appUserRepository, DonationsRepository donationsRepository, MongoTemplate mongoTemplate) {
        this.appUserRepository = appUserRepository;
        this.donationsRepository = donationsRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping("/{query}")
    public ResponseEntity<?> dynamicSearchEmail(@PathVariable("query") String query, Principal principal) {
        // System.out.println(query);
        List<String> searchResults = appUserRepository.findbyEmailContainingAndAppUserRole(query, "ROLE_USER");
        List<String> emails = new ArrayList<>();
        for (String jsonString : searchResults) {
            JSONObject jsonObject = new JSONObject(jsonString);
            emails.add(jsonObject.getString("email"));
        }
        return ResponseEntity.ok(emails);
    }

    @GetMapping("/allDonations")
    public ResponseEntity<?> getDonations(){
        // List<Donations> donations = donationsRepository.findByStatus("paid");
        GroupOperation groupByDonerEmail = Aggregation.group("donerEmail")
            .addToSet("orderId").as("orderIds")
            .push("amount").as("amounts")
            .addToSet("donationDate").as("dates")
            .sum("amount").as("totalAmount");

        TypedAggregation<Donations> aggregation = Aggregation.newAggregation(Donations.class, groupByDonerEmail);

        @SuppressWarnings("rawtypes")
        List<Map> donationSummaries = mongoTemplate.aggregate(aggregation, Map.class).getMappedResults();
        
        return ResponseEntity.ok(donationSummaries);
    }

    @GetMapping("/donations")
    public ResponseEntity<?> getUserInfo(@RequestParam("email") String mail) {
        List<Donations> donations = donationsRepository.findByDonerEmailAndStatus(mail, "paid");
        AppUser user = appUserRepository.findByEmail(mail).get();
        List<Map<String, Object>> donated = new ArrayList<>();
        double totalAmount = 0;
        for (Donations json : donations) {
            Map<String, Object> donationMap = new HashMap<>();
            donationMap.put("orderId", json.getOrderId().toString());
            donationMap.put("amount", json.getAmount());
            totalAmount+=json.getAmount();
            donationMap.put("date", json.getDonationDate().toString());
            donated.add(donationMap);
        }
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalAmount", totalAmount);
        overview.put("logs", user.getLogs());
        overview.put("isEnabled", user.getEnabled());
        donated.add(0, overview);
        return ResponseEntity.ok(donated);
    }

    @GetMapping
    public ResponseEntity<?> searchUsers(@RequestParam(required = false) String email) {
        if (email != null) {
            Optional<AppUser> userOptional = appUserRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                AppUser user = userOptional.get();
                Map<String, Object> response = new HashMap<>();
                response.put("email", user.getEmail());
                response.put("logs", user.getLogs()); // Assuming logs is a List<Log> field in User entity
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            List<AppUser> users = appUserRepository.findAll();
            List<Map<String, Object>> responseList = new ArrayList<>();
            for (AppUser user : users) {
                if (user.getAppUserRole().toString() == "ROLE_ADMIN") {
                    continue;
                }
                Map<String, Object> response = new HashMap<>();
                response.put("email", user.getEmail().toString());
                response.put("logs", user.getLogs()); // Assuming logs is a List<Log> field in User entity
                responseList.add(response);
            }
            return ResponseEntity.ok(responseList);
        }
    }
}
