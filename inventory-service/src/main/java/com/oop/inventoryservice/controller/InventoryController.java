package com.oop.inventoryservice.controller;

import com.oop.inventoryservice.dto.InventoryRemoveStock;
import com.oop.inventoryservice.dto.InventoryResponse;
import com.oop.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        return inventoryService.isInStock(skuCode);
    }

    @PostMapping(path = "/removeStock")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void removeStocks(@RequestBody List<InventoryRemoveStock> inventoryRemoveStockList) {
        inventoryService.removeStocks(inventoryRemoveStockList);
    }
}
