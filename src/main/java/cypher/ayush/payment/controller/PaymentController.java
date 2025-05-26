package cypher.ayush.payment.controller;

import cypher.ayush.payment.model.CreditCard;
import cypher.ayush.payment.model.UPIPay;
import cypher.ayush.payment.model.PaymentType;
import cypher.ayush.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping("/credit")
    public PaymentType saveCredit(@RequestBody CreditCard payment) {
        return paymentRepository.save(payment);
    }

    @PostMapping("/upi")
    public PaymentType saveUpi(@RequestBody UPIPay payment) {
        return paymentRepository.save(payment);
    }

    @GetMapping
    public List<PaymentType> getAllPayments() {
        return paymentRepository.findAll();
    }
}
