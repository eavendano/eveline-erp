package net.erp.eveline.common.predicate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class CommonPredicatesTest {
    @Test
    void isDescriptionValid() {
        final String validDescription = "áéíóúÁÉÍÓÚüÜñÑ$₡€@%|();,&/.:'<>_+-{}¡!¿?";
        final String validEmptyDescription = "";
        assertTrue(CommonPredicates.isDescriptionValid().test(validDescription));
        assertTrue(CommonPredicates.isDescriptionValid().test(validEmptyDescription));
        assertTrue(CommonPredicates.isDescriptionValid().test(null));
    }

    @Test
    void isDescriptionValidFailsOnInvalidInput() {
        final String invalidSymbol = "ö";
        assertFalse(CommonPredicates.isDescriptionValid().test(invalidSymbol));
    }

    @Test
    void isLastUserValid() {
        final String validString = "username.com";
        assertTrue(CommonPredicates.isLastUserValid().test(validString));
    }

    @Test
    void isLastUserValidFailsOnInvalidInput() {
        final String invalidUsername = "-90";
        final String invalidEmptyUsername = "";
        assertFalse(CommonPredicates.isLastUserValid().test(invalidUsername));
        assertFalse(CommonPredicates.isLastUserValid().test(invalidEmptyUsername));
    }
}
