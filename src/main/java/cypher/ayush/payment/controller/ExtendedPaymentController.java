package cypher.ayush.payment.controller;

import cypher.ayush.payment.model.*;
import cypher.ayush.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/payments")
public class ExtendedPaymentController {

    private static final String STORAGE_DIR = "uploaded_images";

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public PaymentType createPayment(@RequestParam("payment_type") String type,
                                     @RequestPart("data") Map<String, String> data,
                                     @RequestPart("file") MultipartFile file) throws IOException {
        Files.createDirectories(Paths.get(STORAGE_DIR));
        String filename = STORAGE_DIR + "/" + StringUtils.cleanPath(file.getOriginalFilename());
        file.transferTo(new File(filename));

        PaymentType payment = switch (type.toUpperCase()) {
            case "CREDIT_CARD" -> {
                CreditCard cc = new CreditCard();
                cc.setCardNumber(data.get("cardNumber"));
                cc.setExpiryDate(data.get("expiryDate"));
                yield cc;
            }
            case "UPI" -> {
                UPIPay upi = new UPIPay();
                upi.setUpiId(data.get("upiId"));
                yield upi;
            }
            default -> throw new IllegalArgumentException("Unsupported payment type ->: " + type);
        };
        payment.setAmount(Double.parseDouble(data.get("amount")));
        payment.setPayee(data.get("payee"));
        return paymentRepository.save(payment);
    }

    @GetMapping("/transactions")
    public List<PaymentType> getTransactions() {
        System.err.println("Fetching all transactions");
        return paymentRepository.findAll();
    }

    @GetMapping
    public List<String> getSupportedPayments() {
        return List.of("CREDIT_CARD", "UPI");
    }

    @GetMapping("/{id}")
    public PaymentType getTransaction(@PathVariable Long id) {
        return paymentRepository.findById(id).orElse(null);
    }
}
