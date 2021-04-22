package net.erp.eveline.common.predicate;

import net.erp.eveline.model.ActiveBrandModel;
import net.erp.eveline.model.ActiveProductModel;
import net.erp.eveline.model.BrandModel;
import net.erp.eveline.model.ProductModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static java.util.Set.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
class BrandPredicateTest {

    @Test
    void isBrandModelValidForInsertSuccessful() {
        final List<String> errorList = new ArrayList<>();
        final BrandModel brandModel = new BrandModel()
                .setName("brand name")
                .setDescription("brand description")
                .setLastUser("lastUser");

        assertTrue(BrandPredicates.isBrandModelValidForInsert(errorList).test(brandModel));
        assertEquals(0, errorList.size());
    }

    @Test
    void isBrandModelValidForInsertFailsOnNonNullId() {
        final List<String> errorList = new ArrayList<>();
        final BrandModel brandModel = new BrandModel()
                .setId("b00001")
                .setName("brand name")
                .setDescription("brand description")
                .setLastUser("lastUser");

        assertFalse(BrandPredicates.isBrandModelValidForInsert(errorList).test(brandModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isBrandModelValidForInsertFailsInvalidName() {
        final List<String> errorList = new ArrayList<>();
        final BrandModel brandModel = new BrandModel()
                .setName("+++======")
                .setDescription("brand description")
                .setLastUser("lastUser");

        assertFalse(BrandPredicates.isBrandModelValidForInsert(errorList).test(brandModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isBrandModelValidForInsertFailsNameNull() {
        final List<String> errorList = new ArrayList<>();
        final BrandModel brandModel = new BrandModel()
                .setDescription("brand description")
                .setLastUser("lastUser");

        assertFalse(BrandPredicates.isBrandModelValidForInsert(errorList).test(brandModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isBrandModelValidForInsertFailsInvalidDescription() {
        final List<String> errorList = new ArrayList<>();
        final BrandModel brandModel = new BrandModel()
                .setName("name")
                .setDescription("ö")
                .setLastUser("lastUser");

        assertFalse(BrandPredicates.isBrandModelValidForInsert(errorList).test(brandModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isBrandModelValidForInsertFailsLastUserNull() {
        final List<String> errorList = new ArrayList<>();
        final BrandModel brandModel = new BrandModel()
                .setName("name")
                .setDescription("description");

        assertFalse(BrandPredicates.isBrandModelValidForInsert(errorList).test(brandModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isBrandModelValidForInsertFailsLastUserInvalid() {
        final List<String> errorList = new ArrayList<>();
        final BrandModel brandModel = new BrandModel()
                .setName("name")
                .setDescription("description")
                .setLastUser("invalid user");

        assertFalse(BrandPredicates.isBrandModelValidForInsert(errorList).test(brandModel));
        assertEquals(1, errorList.size());
    }



    @Test
        //Validation of evaluateModel is skipped, as it was tested on tests for iBrandModelValidForInsert
    void isBrandModelValidForUpdateSuccessful() {
        final List<String> errorList = new ArrayList<>();
        final BrandModel brandModel = new BrandModel()
                .setId("b00001")
                .setName("name")
                .setDescription("description")
                .setLastUser("lastuser");

        assertTrue(BrandPredicates.isBrandModelValidForUpdate(errorList).test(brandModel));
        assertEquals(0, errorList.size());
    }

    @Test
        //Validation of evaluateModel is skipped, as it was tested on tests for isProductModelValidForInsert
    void isBrandModelValidForUpdateFailsOnNullId() {
        final List<String> errorList = new ArrayList<>();
        final BrandModel brandModel = new BrandModel()
                .setName("name")
                .setDescription("description")
                .setLastUser("lastuser");

        assertFalse(BrandPredicates.isBrandModelValidForUpdate(errorList).test(brandModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isBrandIdValidAtInsertSuccessful() {
        final String validString = "b12345";
        assertTrue(BrandPredicates.isBrandIdValid().test(validString));
    }

    @Test
    void iBrandIdValidAtInsertFailsOnInvalidInput() {
        final String emptyString = "";
        final String invalidString = "ssssss";
        assertFalse(BrandPredicates.isBrandIdValid().test(null));
        assertFalse(BrandPredicates.isBrandIdValid().test(emptyString));
        assertFalse(BrandPredicates.isBrandIdValid().test(invalidString));
    }

    @Test
    void isActiveBrandModelValid() {
        final ActiveBrandModel validModel = new ActiveBrandModel()
                .setId("b00001")
                .setEnabled(true)
                .setLastUser("user");
        assertTrue(BrandPredicates.isActiveBrandModelValid(List.of()).
                test(validModel));
    }

    @Test
    void isActiveBrandModelFailsOnNullEnabledValue() {
        final ActiveBrandModel validModel = new ActiveBrandModel()
                .setId("b00001")
                .setLastUser("user");
        final List<String> errorList = new ArrayList<>();
        assertFalse(BrandPredicates.isActiveBrandModelValid(errorList).
                test(validModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isActiveBrandModelFailsOnInvalidId() {
        final ActiveBrandModel validModel = new ActiveBrandModel()
                .setEnabled(true)
                .setLastUser("user");
        final List<String> errorList = new ArrayList<>();
        assertFalse(BrandPredicates.isActiveBrandModelValid(errorList).
                test(validModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isActiveBrandModelFailsOnInvalidUser() {
        final ActiveBrandModel validModel = new ActiveBrandModel()
                .setId("b00001")
                .setEnabled(true)
                .setLastUser("+++++");
        final List<String> errorList = new ArrayList<>();
        assertFalse(BrandPredicates.isActiveBrandModelValid(errorList).
                test(validModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isBrandIdValidFailsOnInvalidInput() {
        final String invalidPrefix = "p00001";
        final String invalidLength = "p001";
        final String invalidFormat = "pxxxx1";
        assertFalse(BrandPredicates.isBrandIdValid().test(invalidPrefix));
        assertFalse(BrandPredicates.isBrandIdValid().test(invalidLength));
        assertFalse(BrandPredicates.isBrandIdValid().test(invalidFormat));
    }


    @Test
    void isEnabledValid() {
        assertTrue(BrandPredicates.isEnabledValid().test(true));
    }

    @Test
    void isEnabledValidFailsOnNull() {
        assertFalse(BrandPredicates.isEnabledValid().test(null));
    }

    @Test
    void isProductUpcValid() {
        final String validUpc = "012345678987";
        assertTrue(ProductPredicates.isProductUpcValid().test(validUpc));
    }
}