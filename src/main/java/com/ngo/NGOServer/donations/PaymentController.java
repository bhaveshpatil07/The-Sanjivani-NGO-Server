package com.ngo.NGOServer.donations;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
// import com.razorpay.Utils;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"http://localhost:3000", "https://sanjivaningo.web.app", "https://sanjivaningo.firebaseapp.com"})
@RestController
@RequestMapping(path = "/api/v1/donate")
@PreAuthorize("hasRole('ROLE_USER')")
public class PaymentController {

    private final RazorPayClientConfig razorPayClientConfig;
    private final DonationsRepository donationsRepository;

    @Autowired
    public PaymentController(RazorPayClientConfig razorPayClientConfig, DonationsRepository donationsRepository) {
        this.razorPayClientConfig = razorPayClientConfig;
        this.donationsRepository = donationsRepository;
    }

    @GetMapping
    public String getKey() {
        return razorPayClientConfig.getKey();
    }

    @PostMapping(path = "/pay/verify")
    @ResponseBody
    public String verifyPayment(@RequestBody Map<String, Object> data) {
        Optional<Donations> fundsOptional = donationsRepository.findByOrderId(data.get("orderId").toString());
        if (fundsOptional.isPresent()) {
            try {
                // RazorpayClient client = new RazorpayClient(razorPayClientConfig.getKey(), razorPayClientConfig.getSecret());

                // JSONObject options = new JSONObject();
                // options.put("razorpay_order_id", data.get("orderId").toString());
                // options.put("razorpay_payment_id", data.get("paymentId").toString());
                // options.put("razorpay_signature", data.get("signature").toString());

                // boolean status = Utils.verifyPaymentSignature(options, razorPayClientConfig.getSecret());
                if (true) {
                    Donations donation = fundsOptional.get();
                    donation.setStatus("paid");
                    donation.setPaymentId(data.get("paymentId").toString());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy @HH:mm:ss");
                    donation.setDonationDate(LocalDateTime.now().format(formatter));
                    donationsRepository.save(donation);
                    return "updated";
                }
                // return "Invalid Razorpay Signature!";
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return "Razorpay Exception!";
            }
        }
        return "Invalid OrderId: " + data.get("order_id").toString();
    }

    @PostMapping(path = "/pay")
    @ResponseBody
    public String payment(@RequestBody Map<String, Object> data, Principal principal) throws RazorpayException {
        int amt = Integer.parseInt(data.get("amount").toString());
        RazorpayClient client = new RazorpayClient(razorPayClientConfig.getKey(), razorPayClientConfig.getSecret());

        JSONObject orderReq = new JSONObject();
        orderReq.put("amount", amt * 100);
        orderReq.put("currency", "INR");
        orderReq.put("receipt", "TXN_4523");

        Order order = client.orders.create(orderReq);
        System.out.println(order);

        // Save order data to our DB
        Donations funds = new Donations(principal.getName(),
                order.get("id"),
                (Integer) order.get("amount") / 100.0,
                order.get("receipt"),
                "created",
                "N/A");

        donationsRepository.insert(funds);
        return order.toString();
    }
}
