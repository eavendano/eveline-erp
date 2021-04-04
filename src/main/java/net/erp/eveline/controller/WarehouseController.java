package net.erp.eveline.controller;

import net.erp.eveline.data.entity.Warehouse;
import net.erp.eveline.model.WarehouseModel;
import net.erp.eveline.service.warehouse.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/warehouse")
@CrossOrigin
public class WarehouseController {
    private WarehouseService warehouseService;



    @GetMapping(value = "/{warehouseId}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public WarehouseModel getWarehouse(@PathVariable final String warehouseId){
        return warehouseService.getWarehouseModel(warehouseId);
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public WarehouseModel upsertWarehouse(@RequestBody final WarehouseModel warehouseModel) {
        return warehouseService.upsertWarehouseModel(warehouseModel);
    }



    @Autowired
    public void setWarehouseService(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }
}
