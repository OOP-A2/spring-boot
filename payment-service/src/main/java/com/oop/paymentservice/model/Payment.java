package com.oop.paymentservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    private Long id;
    private Long id_order;
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderLineItems> orderLineItemsList;
    private Long quantity;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @CreatedDate
    @LastModifiedDate

    @PrePersist
    protected void onCreate() {
        setId(System.nanoTime());
    }
}
