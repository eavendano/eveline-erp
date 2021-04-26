package net.erp.eveline.common.predicate;

import net.erp.eveline.model.ActiveProductModel;
import net.erp.eveline.model.BrandModel;
import net.erp.eveline.model.ProductModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static java.util.Set.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
class ProductPredicatesTest {

    @Test
    void isProductModelValidForInsertSuccessful() {
        final List<String> errorList = new ArrayList<>();
        final ProductModel productModel = new ProductModel()
                .setTitle("valid")
                .setBrand(generateBrandModel())
                .setUpc("123456789012")
                .setLastUser("valid")
                .setDescription("Valid Desc")
                .setProviderSet(of("p12345"));

        assertTrue(ProductPredicates.isProductModelValidForInsert(errorList).test(productModel));
        assertEquals(0, errorList.size());
    }

    @Test
    void isProductModelValidForInsertFailsOnNonNullId() {
        final List<String> errorList = new ArrayList<>();
        final ProductModel productModel = new ProductModel()
                .setId("s12345")
                .setBrand(generateBrandModel())
                .setTitle("valid")
                .setUpc("123456789012")
                .setLastUser("valid")
                .setDescription("Valid Desc")
                .setProviderSet(of("p12345"));

        assertFalse(ProductPredicates.isProductModelValidForInsert(errorList).test(productModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isProductModelValidForInsertFailsOnInvalidTitle() {
        final List<String> errorList = new ArrayList<>();
        final ProductModel productModel = new ProductModel()
                .setTitle("+++======")
                .setBrand(generateBrandModel())
                .setUpc("123456789012")
                .setLastUser("valid")
                .setDescription("Valid Desc")
                .setProviderSet(of("p12345"));

        assertFalse(ProductPredicates.isProductModelValidForInsert(errorList).test(productModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isProductModelValidForInsertFailsOnInvalidDescription() {
        final List<String> errorList = new ArrayList<>();
        final ProductModel productModel = new ProductModel()
                .setTitle("valid")
                .setBrand(generateBrandModel())
                .setUpc("123456789012")
                .setLastUser("valid")
                .setProviderSet(of("p12345"))
                .setDescription("ö");

        assertFalse(ProductPredicates.isProductModelValidForInsert(errorList).test(productModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isProductModelValidForInsertFailsOnInvalidUpc() {
        final List<String> errorList = new ArrayList<>();
        final ProductModel productModel = new ProductModel()
                .setTitle("valid")
                .setBrand(generateBrandModel())
                .setLastUser("valid")
                .setDescription("Valid Desc")
                .setProviderSet(of("p12345"))
                .setUpc("abc");

        assertFalse(ProductPredicates.isProductModelValidForInsert(errorList).test(productModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isProductModelValidForInsertFailsOnInvalidProvider() {
        final List<String> errorList = new ArrayList<>();
        final ProductModel productModel = new ProductModel()
                .setTitle("valid")
                .setBrand(generateBrandModel())
                .setLastUser("valid")
                .setProviderSet(of("p1234"))
                .setDescription("Valid Desc")
                .setUpc("123456789012");

        assertFalse(ProductPredicates.isProductModelValidForInsert(errorList).test(productModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isProductModelValidForInsertFailsOnInvalidLastUser() {
        final List<String> errorList = new ArrayList<>();
        final ProductModel productModel = new ProductModel()
                .setTitle("valid")
                .setBrand(generateBrandModel())
                .setProviderSet(of("p12345"))
                .setDescription("Valid Desc")
                .setUpc("123456789012");

        assertFalse(ProductPredicates.isProductModelValidForInsert(errorList).test(productModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isProductModelValidForInsertFailsOnInvalidBrandId() {
        final List<String> errorList = new ArrayList<>();
        BrandModel brandModel = generateBrandModel();
        brandModel.setId("b01");
        final ProductModel productModel = new ProductModel()
                .setTitle("valid")
                .setBrand(brandModel)
                .setProviderSet(of("p12345"))
                .setDescription("Valid Desc")
                .setLastUser("valid")
                .setUpc("123456789012");

        assertFalse(ProductPredicates.isProductModelValidForInsert(errorList).test(productModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isProductModelValidForInsertFailsOnBrandIdNull() {
        final List<String> errorList = new ArrayList<>();
        BrandModel brandModel = generateBrandModel();
        brandModel.setId(null);
        final ProductModel productModel = new ProductModel()
                .setTitle("valid")
                .setBrand(brandModel)
                .setProviderSet(of("p12345"))
                .setDescription("Valid Desc")
                .setLastUser("valid")
                .setUpc("123456789012");

        assertFalse(ProductPredicates.isProductModelValidForInsert(errorList).test(productModel));
        assertEquals(1, errorList.size());
    }

    @Test
        //Validation of evaluateModel is skipped, as it was tested on tests for isProductModelValidForInsert
    void isProductModelValidForUpdateSuccessful() {
        final List<String> errorList = new ArrayList<>();
        final ProductModel productModel = new ProductModel()
                .setId("s12345")
                .setBrand(generateBrandModel())
                .setTitle("valid")
                .setUpc("123456789012")
                .setLastUser("valid")
                .setDescription("Valid Desc")
                .setProviderSet(of("p12345"));

        assertTrue(ProductPredicates.isProductModelValidForUpdate(errorList).test(productModel));
        assertEquals(0, errorList.size());
    }

    @Test
        //Validation of evaluateModel is skipped, as it was tested on tests for isProductModelValidForInsert
    void isProductModelValidForUpdateFailsOnNullId() {
        final List<String> errorList = new ArrayList<>();
        final ProductModel productModel = new ProductModel()
                .setTitle("valid")
                .setBrand(generateBrandModel())
                .setUpc("123456789012")
                .setLastUser("valid")
                .setDescription("Valid Desc")
                .setProviderSet(of("p12345"));

        assertFalse(ProductPredicates.isProductModelValidForUpdate(errorList).test(productModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isProductIdValidAtInsertSuccessful() {
        final String validString = "s12345";
        assertTrue(ProductPredicates.isProductIdValid().test(validString));
    }

    @Test
    void isProductIdValidAtInsertFailsOnInvalidInput() {
        final String emptyString = "";
        final String invalidString = "ssssss";
        assertFalse(ProductPredicates.isProductIdValid().test(null));
        assertFalse(ProductPredicates.isProductIdValid().test(emptyString));
        assertFalse(ProductPredicates.isProductIdValid().test(invalidString));
    }

    @Test
    void isActiveProductModelValid() {
        final ActiveProductModel validModel = new ActiveProductModel()
                .setId("s00001")
                .setEnabled(true)
                .setLastUser("bmiranda");
        assertTrue(ProductPredicates.isActiveProductModelValid(List.of()).
                test(validModel));
    }

    @Test
    void isActiveProductModelFailsOnNullEnabledValue() {
        final ActiveProductModel validModel = new ActiveProductModel()
                .setId("s00001")
                .setEnabled(null)
                .setLastUser("bmiranda");
        final List<String> errorList = new ArrayList<>();

        assertFalse(ProductPredicates.isActiveProductModelValid(errorList).
                test(validModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isActiveProductModelFailsOnInvalidId() {
        final ActiveProductModel validModel = new ActiveProductModel()
                .setId("000001")
                .setEnabled(true)
                .setLastUser("bmiranda");
        final List<String> errorList = new ArrayList<>();

        assertFalse(ProductPredicates.isActiveProductModelValid(errorList).
                test(validModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isActiveProductModelFailsOnInvalidUser() {
        final ActiveProductModel validModel = new ActiveProductModel()
                .setId("s00001")
                .setEnabled(true)
                .setLastUser("+++++");
        final List<String> errorList = new ArrayList<>();

        assertFalse(ProductPredicates.isActiveProductModelValid(errorList).
                test(validModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isProductIdValidSuccessful() {
        final String validId = "s00001";
        assertTrue(ProductPredicates.isProductIdValid().test(validId));
    }

    @Test
    void isProductIdValidFailsOnInvalidInput() {
        final String invalidPrefix = "p00001";
        final String invalidLength = "p001";
        final String invalidFormat = "pxxxx1";
        assertFalse(ProductPredicates.isProductIdValid().test(invalidPrefix));
        assertFalse(ProductPredicates.isProductIdValid().test(invalidLength));
        assertFalse(ProductPredicates.isProductIdValid().test(invalidFormat));
    }

    @Test
    void isProductTitleValidSuccessful() {
        final String validTitle = "Example title for sku-with-dashes";
        assertTrue(ProductPredicates.isProductTitleValid().test(validTitle));
    }

    @Test
    void isProductTitleValidFailsOnInvalidInput() {
        final String invalidDots = "title with unaccepted.dots.on.text";
        final String invalidPercentage = "title with unaccepted %";
        final String invalidCR = "title with unaccepted /n";

        assertFalse(ProductPredicates.isProductTitleValid().test(invalidDots));
        assertFalse(ProductPredicates.isProductTitleValid().test(invalidPercentage));
        assertFalse(ProductPredicates.isProductTitleValid().test(invalidCR));
    }

    @Test
    void isEnabledValid() {
        assertTrue(ProductPredicates.isEnabledValid().test(true));
    }

    @Test
    void isEnabledValidFailsOnNull() {
        assertFalse(ProductPredicates.isEnabledValid().test(null));
    }

    @Test
    void isProductUpcValid() {
        final String validUpc = "012345678987";
        assertTrue(ProductPredicates.isProductUpcValid().test(validUpc));
    }

    @Test
    void isProductUpcValidFailsOnInvalidInput() {
        final String invalidLength = "01234567";
        final String invalidSymbols = "-1234567898a";
        final String invalidEmptyString = "";

        assertFalse(ProductPredicates.isProductUpcValid().test(invalidLength));
        assertFalse(ProductPredicates.isProductUpcValid().test(invalidSymbols));
        assertFalse(ProductPredicates.isProductUpcValid().test(invalidEmptyString));
        assertFalse(ProductPredicates.isProductUpcValid().test(null));
    }


    BrandModel generateBrandModel() {
        return new BrandModel()
                .setId("b00001")
                .setName("name")
                .setDescription("description");
    }
}