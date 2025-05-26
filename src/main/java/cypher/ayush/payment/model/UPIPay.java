package cypher.ayush.payment.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("UPI")
public class UPIPay extends PaymentType {

    private String upiId;

    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }
}
