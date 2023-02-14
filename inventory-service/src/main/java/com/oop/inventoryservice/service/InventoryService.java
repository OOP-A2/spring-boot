package com.oop.inventoryservice.service;

import com.oop.inventoryservice.dto.InventoryRemoveStock;
import com.oop.inventoryservice.dto.InventoryResponse;
import com.oop.inventoryservice.model.Inventory;
import com.oop.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class InventoryService {

    private  final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode) {
        return inventoryRepository.findBySkuCodeIn(skuCode).stream().map(inventory ->
            InventoryResponse.builder().skuCode(inventory.getSkuCode())
                    .isInStock(inventory.getQuantity() > 0)
                    .build()
        ).toList();
    }

    public boolean removeStocks(List<InventoryRemoveStock> inventoryResponses) {
        List<String> skuCode = new ArrayList<>();
        for(InventoryRemoveStock iResp : inventoryResponses) skuCode.add(iResp.getSkuCode());

        List<Inventory> inventoryList = inventoryRepository.findBySkuCodeIn(skuCode).stream().toList();
        inventoryList.forEach(i -> {
            Optional<InventoryRemoveStock> inv = inventoryResponses.stream().filter(iRes -> i.getSkuCode().equals(iRes.getSkuCode())).findFirst();
            if (inv.isPresent()) {
                InventoryRemoveStock inres = inv.get();
                i.setQuantity(i.getQuantity() - inres.getQuantity());
            }
        });
        this.inventoryRepository.saveAll(inventoryList);
        return false;
    }
}
