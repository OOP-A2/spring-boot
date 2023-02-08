package com.oop.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private Long id_order;
    private List<OrderLineItemsDto> orderLineItemsDtoList;
}
