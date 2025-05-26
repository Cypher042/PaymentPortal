package cypher.ayush.payment.repository;

import cypher.ayush.payment.model.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentType, Long> {
}
