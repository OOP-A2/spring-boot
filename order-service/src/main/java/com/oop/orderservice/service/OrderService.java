package com.oop.orderservice.service;

import com.oop.orderservice.dto.InventoryResponse;
import com.oop.orderservice.dto.OrderLineItemsDto;
import com.oop.orderservice.dto.OrderRequest;
import com.oop.orderservice.dto.PaymentRequest;
import com.oop.orderservice.model.Order;
import com.oop.orderservice.model.OrderLineItems;
import com.oop.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);
        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        InventoryResponse[] inventoryResponsArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsInStock = Arrays.stream(Objects.requireNonNull(inventoryResponsArray))
                .allMatch(InventoryResponse::isInStock);

        if (allProductsInStock) {
            orderRepository.save(order);
            Optional<Order> resultOrder = orderRepository.findById(order.getId());
            if (resultOrder.isPresent()) {
                Order savedOrder = resultOrder.get();
                PaymentRequest paymentRequest = new PaymentRequest();
                paymentRequest.setId_order(savedOrder.getId());
                paymentRequest.setOrderLineItemsDtoList(orderRequest.getOrderLineItemsDtoList());

                String response = webClientBuilder.build().post()
                        .uri("http://payment-service/api/payment")
                        .bodyValue(paymentRequest)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                if (!Objects.equals(response, "success")) try {
                    throw new Exception("Unable to create payment for order");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else {
            throw new IllegalArgumentException("Product has no stock left.");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
