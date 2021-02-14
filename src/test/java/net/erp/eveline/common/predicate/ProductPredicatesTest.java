package net.erp.eveline.common.predicate;

import net.erp.eveline.data.entity.Product;
import net.erp.eveline.model.ProductModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class ProductPredicatesTest {


    @Test
    void isProductModelValidForInsert() {
    }

    @Test
    void isProductModelValidForUpdate() {
    }

    @Test
    void isProductIdValidAtInsert() {
    }

    @Test
    void isActiveProductModelValid() {
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
    void isProviderTitleValidSuccessful() {
        final String validTitle = "Example title for sku-with-dashes";
        assertTrue(ProductPredicates.isProviderTitleValid().test(validTitle));
    }

    @Test
    void isProviderTitleValidFailsOnInvalidInput() {
        final String invalidDots = "title with unaccepted.dots.on.text";
        final String invalidPercentage = "title with unaccepted %";
        final String invalidCR = "title with unaccepted /n";

        assertFalse(ProductPredicates.isProviderTitleValid().test(invalidDots));
        assertFalse(ProductPredicates.isProviderTitleValid().test(invalidPercentage));
        assertFalse(ProductPredicates.isProviderTitleValid().test(invalidCR));
    }

    @Test
    void isProductDescriptionValid() {
    }

    @Test
    void isLastUserValid() {
    }

    @Test
    void isEnabledValid() {
    }

    @Test
    void isProductUpcValid() {
    }

    @Test
    void isProviderValid() {
    }
}