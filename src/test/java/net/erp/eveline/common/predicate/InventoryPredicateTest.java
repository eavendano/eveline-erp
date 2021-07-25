package net.erp.eveline.common.predicate;

import net.erp.eveline.model.ActiveInventoryModel;
import net.erp.eveline.model.InventoryModel;
import net.erp.eveline.model.ProductModel;
import net.erp.eveline.model.WarehouseModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

    @Test
    void isInventoryModelValidForInsertFailsOnWarehouseIdInvalid() {
        final List<String> errorList = new ArrayList<>();
        final InventoryModel inventoryModel = new InventoryModel()
                .setId(null)
                .setProductModel(generateProductModel())
                .setWarehouseModel(generateWarehouseModel().setId("w01"))
                .setQuantity(3)
                .setEnabled(true)
                .setLastUser("testUser");

        assertFalse(InventoryPredicates.isInventoryModelValidForInsert(errorList).test(inventoryModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isInventoryModelValidForInsertFailsOnProductIdInvalid() {
        final List<String> errorList = new ArrayList<>();
        final InventoryModel inventoryModel = new InventoryModel()
                .setId(null)
                .setProductModel(generateProductModel().setId("s01"))
                .setWarehouseModel(generateWarehouseModel())
                .setQuantity(3)
                .setEnabled(true)
                .setLastUser("testUser");

        assertFalse(InventoryPredicates.isInventoryModelValidForInsert(errorList).test(inventoryModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isInventoryModelValidForInsertFailsOnQuantityInvalid() {
        final List<String> errorList = new ArrayList<>();
        final InventoryModel inventoryModel = new InventoryModel()
                .setId(null)
                .setProductModel(generateProductModel())
                .setWarehouseModel(generateWarehouseModel())
                .setQuantity(null)
                .setEnabled(true)
                .setLastUser("testUser");

        assertFalse(InventoryPredicates.isInventoryModelValidForInsert(errorList).test(inventoryModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isInventoryModelValidForInsertFailsOnLastUserInvalid() {
        final List<String> errorList = new ArrayList<>();
        final InventoryModel inventoryModel = new InventoryModel()
                .setId(null)
                .setProductModel(generateProductModel())
                .setWarehouseModel(generateWarehouseModel())
                .setQuantity(1)
                .setEnabled(true)
                .setLastUser(null);

        assertFalse(InventoryPredicates.isInventoryModelValidForInsert(errorList).test(inventoryModel));
        assertEquals(1, errorList.size());
    }

    @Test
        //Validation of evaluateModel is skipped, as it was tested on tests for isProductModelValidForInsert
    void isInventoryModelValidForUpdateSuccessful() {
        final List<String> errorList = new ArrayList<>();
        final InventoryModel inventoryModel = new InventoryModel()
                .setId("i00001")
                .setProductModel(generateProductModel())
                .setWarehouseModel(generateWarehouseModel())
                .setQuantity(3)
                .setEnabled(true)
                .setLastUser("testUser");


        assertTrue(InventoryPredicates.isInventoryModelValidForUpdate(errorList).test(inventoryModel));
        assertEquals(0, errorList.size());
    }

    @Test
        //Validation of evaluateModel is skipped, as it was tested on tests for isProductModelValidForInsert
    void isInventoryModelValidForUpdateFailsOnNullId() {
        final List<String> errorList = new ArrayList<>();
        final InventoryModel inventoryModel = new InventoryModel()
                .setId(null)
                .setProductModel(generateProductModel())
                .setWarehouseModel(generateWarehouseModel())
                .setQuantity(3)
                .setEnabled(true)
                .setLastUser("testUser");


        assertFalse(InventoryPredicates.isInventoryModelValidForUpdate(errorList).test(inventoryModel));
        assertEquals(1, errorList.size());
    }


    @Test
    void isInventoryIdValidAtInsertSuccessful() {
        final String validString = "i12345";
        assertTrue(InventoryPredicates.isInventoryIdValid().test(validString));
    }

    @Test
    void isInventoryIdValidAtInsertFailsOnInvalidInput() {
        final String emptyString = "";
        final String invalidString = "ssssss";
        assertFalse(InventoryPredicates.isInventoryIdValid().test(null));
        assertFalse(InventoryPredicates.isInventoryIdValid().test(emptyString));
        assertFalse(InventoryPredicates.isInventoryIdValid().test(invalidString));
    }

    @Test
    void isActiveInventoryModelValid() {
        final ActiveInventoryModel validModel = new ActiveInventoryModel()
                .setId("i00001")
                .setEnabled(true)
                .setLastUser("testUser");
        assertTrue(InventoryPredicates.isActiveInventoryModelValid(List.of()).
                test(validModel));
    }

    @Test
    void isActiveInventoryModelFailsOnNullEnabledValue() {
        final ActiveInventoryModel validModel = new ActiveInventoryModel()
                .setId("i00001")
                .setEnabled(null)
                .setLastUser("testUser");
        final List<String> errorList = new ArrayList<>();

        assertFalse(InventoryPredicates.isActiveInventoryModelValid(errorList).
                test(validModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isActiveInventoryModelFailsOnInvalidId() {
        final ActiveInventoryModel validModel = new ActiveInventoryModel()
                .setId("000001")
                .setEnabled(true)
                .setLastUser("testUser");
        final List<String> errorList = new ArrayList<>();

        assertFalse(InventoryPredicates.isActiveInventoryModelValid(errorList).
                test(validModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isActiveInventoryModelFailsOnInvalidUser() {
        final ActiveInventoryModel validModel = new ActiveInventoryModel()
                .setId("i00001")
                .setEnabled(true)
                .setLastUser("+++++");
        final List<String> errorList = new ArrayList<>();

        assertFalse(InventoryPredicates.isActiveInventoryModelValid(errorList).
                test(validModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isInventoryIdValidSuccessful() {
        final String validId = "i00001";
        assertTrue(InventoryPredicates.isInventoryIdValid().test(validId));
    }

    @Test
    void isInventoryIdValidFailsOnInvalidInput() {
        final String invalidPrefix = "p00001";
        final String invalidLength = "i001";
        final String invalidFormat = "ixxxx1";
        assertFalse(InventoryPredicates.isInventoryIdValid().test(invalidPrefix));
        assertFalse(InventoryPredicates.isInventoryIdValid().test(invalidLength));
        assertFalse(InventoryPredicates.isInventoryIdValid().test(invalidFormat));
    }

    @Test
    void isInventoryQuantityValidSuccess() {
        final Integer quantity = 1;
        assertTrue(InventoryPredicates.isQuantityValid().test(quantity));
    }

    @Test
    void isInventoryQuantityValidFailsOnInvalidInput() {
        final Integer negativeQuantity = -1;
        final Integer quantityNull = null;

        assertFalse(InventoryPredicates.isQuantityValid().test(negativeQuantity));
        assertFalse(InventoryPredicates.isQuantityValid().test(quantityNull));
    }

    @Test
    void isEnabledValid() {
        assertTrue(InventoryPredicates.isEnabledValid().test(true));
    }

    @Test
    void isEnabledValidFailsOnNull() {
        assertFalse(InventoryPredicates.isEnabledValid().test(null));
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
