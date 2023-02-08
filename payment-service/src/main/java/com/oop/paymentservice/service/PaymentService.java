package com.oop.paymentservice.service;

import com.oop.paymentservice.dto.OrderLineItemsDto;
import com.oop.paymentservice.dto.PaymentRequest;
import com.oop.paymentservice.model.OrderLineItems;
import com.oop.paymentservice.model.Payment;
import com.oop.paymentservice.model.PaymentStatus;
import com.oop.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public String createPayment(PaymentRequest paymentRequest) {
        Payment payment = new Payment();
        payment.setId_order(paymentRequest.getId_order());
        payment.setStatus(PaymentStatus.UNPAID);
        List<OrderLineItems> orderLineItems = paymentRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        payment.setOrderLineItemsList(orderLineItems);

        paymentRepository.save(payment);
        return "success";
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
