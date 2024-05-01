package com.ngo.NGOServer.donations;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "donations")
public class Donations {
    @Id
    private String id;
    private String donerEmail;
    private String orderId;
    private double amount;
    private String receipt;
    private String status;
    private String paymentId;
    private String donationDate;

    public Donations(String donerEmail, String orderId, double amount, String receipt, String status,
            String paymentId) {
        this.donerEmail = donerEmail;
        this.orderId = orderId;
        this.amount = amount;
        this.receipt = receipt;
        this.status = status;
        this.paymentId = paymentId;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy @HH:mm:ss");
        this.donationDate = formatter.format(new Date());
    }
    
}
