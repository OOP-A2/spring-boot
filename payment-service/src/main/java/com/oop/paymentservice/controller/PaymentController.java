package com.oop.paymentservice.controller;

import com.oop.paymentservice.dto.InventoryRemoveStock;
import com.oop.paymentservice.dto.PaymentRequest;
import com.oop.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentService paymentService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createPayment(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.createPayment(paymentRequest);
    }
}
