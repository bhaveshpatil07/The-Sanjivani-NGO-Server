package com.ngo.NGOServer.donations;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationsRepository extends MongoRepository<Donations, String> {
    Optional<Donations> findByOrderId(String orderId);
    List<Donations> findByDonerEmailContaining(String keyword);
    List<Donations> findByDonerEmailAndStatus(String email, String status);
    List<Donations> findByStatus(String status);
}
