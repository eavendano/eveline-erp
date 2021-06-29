package net.erp.eveline.common.predicate;

import net.erp.eveline.model.InventoryModel;
import net.erp.eveline.model.ProductModel;
import net.erp.eveline.model.WarehouseModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static java.util.Set.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class InventoryPredicateTest {

    @Test
    void isInventoryModelValidForInsertSuccessful() {
        final List<String> errorList = new ArrayList<>();
        final InventoryModel inventoryModel = new InventoryModel()
                .setId(null)
                .setProductModel(generateProductModel())
                .setWarehouseModel(generateWarehouseModel())
                .setQuantity(3)
                .setEnabled(true)
                .setLastUser("testUser");

        assertTrue(InventoryPredicates.isInventoryModelValidForInsert(errorList).test(inventoryModel));
        assertEquals(0, errorList.size());
    }


    ProductModel generateProductModel() {
        return new ProductModel()
                .setId("s00001");
    }

    WarehouseModel generateWarehouseModel() {
        return new WarehouseModel()
                .setId("w00001");
    }

}
