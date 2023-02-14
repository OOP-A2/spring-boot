package com.oop.paymentservice.service;

import com.oop.paymentservice.dto.InventoryRemoveStock;
import com.oop.paymentservice.dto.OrderLineItemsDto;
import com.oop.paymentservice.dto.PaymentRequest;
import com.oop.paymentservice.model.OrderLineItems;
import com.oop.paymentservice.model.Payment;
import com.oop.paymentservice.model.PaymentStatus;
import com.oop.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final WebClient.Builder webClientBuilder;


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
        Optional<Payment> optPayment = paymentRepository.findById(payment.getId());
        if (optPayment.isPresent()) {
            List<InventoryRemoveStock> inventoryRemoveStockList = new ArrayList<>();
            paymentRequest.getOrderLineItemsDtoList().forEach(ol -> {
                InventoryRemoveStock inventoryRemoveStock = new InventoryRemoveStock();
                inventoryRemoveStock.setQuantity(ol.getQuantity());
                inventoryRemoveStock.setSkuCode(ol.getSkuCode());
                inventoryRemoveStockList.add(inventoryRemoveStock);
            });

            ResponseEntity<Void> block = webClientBuilder.build().post()
                    .uri("http://inventory-service/api/inventory/removeStock")
                    .bodyValue(inventoryRemoveStockList)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            if (Objects.requireNonNull(block).getStatusCode().is2xxSuccessful()) return "success";
        }
        return "fail";
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
