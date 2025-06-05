package cypher.ayush.payment.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cypher.ayush.payment.model.*;
import cypher.ayush.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;

@RestController
@RequestMapping("/v1/payments")
public class ExtendedPaymentController {

    @Value("${file.storage-dir}")
    private String storageDir;

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public PaymentType createPayment(
            @RequestParam("payment_type") String type,
            @RequestPart("data") String dataJSON,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
                
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> data = mapper.readValue(dataJSON, new TypeReference<>() {});

        Files.createDirectories(Paths.get(storageDir));
        if (file != null && !file.isEmpty()) {
            // private string hi = data.get("payee").trim();
            // Files.createDirectories(Paths.get(STORAGE_DIR));
            String filedir = storageDir + "/" + StringUtils.cleanPath(data.get("payee").replaceAll("\\s+", ""));
            Files.createDirectories(Paths.get(filedir));
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String safeFilename = originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

            String filename = storageDir + "/" + StringUtils.cleanPath(data.get("payee").replaceAll("\\s+", ""))+ "/" + StringUtils.cleanPath(safeFilename) + " " + StringUtils.cleanPath(data.get("amount").trim()) ;
            file.transferTo(new File(filename));
        }


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
