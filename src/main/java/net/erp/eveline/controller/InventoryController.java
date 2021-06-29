package net.erp.eveline.controller;

import net.erp.eveline.model.ActiveInventoryModel;
import net.erp.eveline.model.ActiveWarehouseModel;
import net.erp.eveline.model.InventoryModel;
import net.erp.eveline.model.WarehouseModel;
import net.erp.eveline.service.inventory.InventoryService;
import net.erp.eveline.service.warehouse.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/inventory")
@CrossOrigin
public class InventoryController {
    private InventoryService inventoryService;

    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Set<InventoryModel> getInventory() {
        return inventoryService.findAll();
    }

    @GetMapping(value = "/{inventoryId}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public InventoryModel getInventory(@PathVariable final String inventoryId){
        return inventoryService.getInventoryModel(inventoryId);
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public InventoryModel upsertWarehouse(@RequestBody final InventoryModel inventoryModel) {
        return inventoryService.upsertInventoryModel(inventoryModel);
    }

    @PutMapping(value = "/activate", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ActiveInventoryModel activateWarehouse(@RequestBody final ActiveInventoryModel activeInventoryModel) {
        return inventoryService.activateInventory(activeInventoryModel);
    }

    @Autowired
    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
}