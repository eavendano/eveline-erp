package net.erp.eveline.common.predicate;

import net.erp.eveline.model.WarehouseModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class WarehousePredicatesTest {

    @Test
    void isWarehouseIdValidSuccessful() {
        final String validId = "w00001";
        assertTrue(WarehousePredicates.isWarehouseIdValid().test(validId));
    }

    @Test
    void isWarehouseIdValidFailsOnInvalidInput() {
        final String invalidPrefix = "p00001";
        final String invalidLength = "w001";
        final String invalidFormat = "wxxxx1";
        assertFalse(WarehousePredicates.isWarehouseIdValid().test(invalidPrefix));
        assertFalse(WarehousePredicates.isWarehouseIdValid().test(invalidLength));
        assertFalse(WarehousePredicates.isWarehouseIdValid().test(invalidFormat));
    }

    @Test
    void isWarehouseNameValidSuccessful() {
        final String validTitle = "Example name for warehouse-with-dashes &";
        assertTrue(WarehousePredicates.isWarehouseNameValid().test(validTitle));
    }

    @Test
    void isWarehouseNameValidFailsOnInvalidInput() {
        final String invalidDots = "name with unaccepted.dots.on.text";
        final String invalidPercentage = "name with unaccepted %";
        final String invalidBackslash = "name with unaccepted \\text";

        assertFalse(WarehousePredicates.isWarehouseNameValid().test(invalidDots));
        assertFalse(WarehousePredicates.isWarehouseNameValid().test(invalidPercentage));
        assertFalse(WarehousePredicates.isWarehouseNameValid().test(invalidBackslash));
    }

    @Test
    void isWarehouseAddressValidSuccessful() {
        final String validAddress = "This is an example of a line address. 22!?-&";
        assertTrue(WarehousePredicates.isWarehouseAddressValid().test(validAddress));
    }

    @Test
    void isWarehouseAddressFailsOnInvalidInput() {
        final String emptyLine = "";
        final String invalidSymbols = "text\\text";

        assertFalse(WarehousePredicates.isWarehouseAddressValid().test(null));
        assertFalse(WarehousePredicates.isWarehouseAddressValid().test(emptyLine));
        assertFalse(WarehousePredicates.isWarehouseAddressValid().test(invalidSymbols));
    }

    @Test
    void isWarehouseOptionalAddressValidSuccessful() {
        final String emptyLine = "";
        final String validAddress = "This is an example of a line address. 22!?-&";
        assertTrue(WarehousePredicates.isWarehouseOptionalAddressValid().test(null));
        assertTrue(WarehousePredicates.isWarehouseOptionalAddressValid().test(emptyLine));
        assertTrue(WarehousePredicates.isWarehouseOptionalAddressValid().test(validAddress));
    }

    @Test
    void isWarehouseOptionalAddressFailsOnInvalidInput() {
        final String invalidSymbols = "text\\text";
        assertFalse(WarehousePredicates.isWarehouseOptionalAddressValid().test(invalidSymbols));
    }

    @Test
    void isWarehouseOptionalNotesValidSuccessful() {
        final String emptyLine = "";
        final String validNote = "This is an example of a note line. 22!?-&";
        assertTrue(WarehousePredicates.isWarehouseOptionalNotesValid().test(null));
        assertTrue(WarehousePredicates.isWarehouseOptionalNotesValid().test(emptyLine));
        assertTrue(WarehousePredicates.isWarehouseOptionalNotesValid().test(validNote));
    }

    @Test
    void isWarehouseOptionalNotesFailsOnInvalidInput() {
        final String invalidSymbols = "text\\text";
        assertFalse(WarehousePredicates.isWarehouseOptionalNotesValid().test(invalidSymbols));
    }

    @Test
    void isEnabledValid() {
        assertTrue(WarehousePredicates.isEnabledValid().test(true));
    }

    @Test
    void isEnabledValidFailsOnNull() {
        assertFalse(WarehousePredicates.isEnabledValid().test(null));
    }

    @Test
    void isWarehouseIdValidAtInsertSuccessful() {
        assertTrue(WarehousePredicates.isWarehouseIdValidAtInsert().test(null));
    }

    @Test
    void isWarehouseIdValidAtInsertFailsOnInvalidInput() {
        final String emptyId = "";
        final String validId = "w00001";
        assertFalse(WarehousePredicates.isWarehouseIdValidAtInsert().test(emptyId));
        assertFalse(WarehousePredicates.isWarehouseIdValidAtInsert().test(validId));
    }

    @Test
    void isWarehouseModelValidForInsertSuccessful() {
        final List<String> errorList = new ArrayList<>();
        final WarehouseModel warehouseModel = new WarehouseModel()
                .setName("name")
                .setAddress1("address")
                .setTelephone1("12345678")
                .setLastUser("valid")
                .setLongitude(0.0)
                .setLatitude(0.0);
        assertTrue(WarehousePredicates.isWarehouseModelValidForInsert(errorList).test(warehouseModel));
        assertEquals(0, errorList.size());
    }

    @Test
    void isWarehouseModelValidForInsertFailsOnNonNullId() {
        final List<String> errorList = new ArrayList<>();
        final WarehouseModel warehouseModel = new WarehouseModel()
                .setId("w00001")
                .setName("name")
                .setAddress1("address")
                .setTelephone1("12345678")
                .setLastUser("valid")
                .setLongitude(0.0)
                .setLatitude(0.0);
        assertFalse(WarehousePredicates.isWarehouseModelValidForInsert(errorList).test(warehouseModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isWarehouseModelValidForInsertFailsOnInvalidName() {
        final List<String> errorList = new ArrayList<>();
        final WarehouseModel warehouseModel = new WarehouseModel()
                .setName("+++++=====!")
                .setAddress1("address")
                .setTelephone1("12345678")
                .setLastUser("valid")
                .setLongitude(0.0)
                .setLatitude(0.0);
        assertFalse(WarehousePredicates.isWarehouseModelValidForInsert(errorList).test(warehouseModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isWarehouseModelValidForInsertFailsOnNullAddress1() {
        final List<String> errorList = new ArrayList<>();
        final WarehouseModel warehouseModel = new WarehouseModel()
                .setName("name")
                .setTelephone1("12345678")
                .setLastUser("valid")
                .setLongitude(0.0)
                .setLatitude(0.0);
        assertFalse(WarehousePredicates.isWarehouseModelValidForInsert(errorList).test(warehouseModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isWarehouseModelValidForInsertFailsInvalidAddress2() {
        final List<String> errorList = new ArrayList<>();
        final WarehouseModel warehouseModel = new WarehouseModel()
                .setName("name")
                .setAddress1("address")
                .setAddress2("\\")
                .setTelephone1("12345678")
                .setLastUser("valid")
                .setLongitude(0.0)
                .setLatitude(0.0);
        assertFalse(WarehousePredicates.isWarehouseModelValidForInsert(errorList).test(warehouseModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isWarehouseModelValidForInsertFailsOnNullTelephone1() {
        final List<String> errorList = new ArrayList<>();
        final WarehouseModel warehouseModel = new WarehouseModel()
                .setName("name")
                .setAddress1("address")
                .setLastUser("valid")
                .setLongitude(0.0)
                .setLatitude(0.0);
        assertFalse(WarehousePredicates.isWarehouseModelValidForInsert(errorList).test(warehouseModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isWarehouseModelValidForInsertFailsOnInvalidTelephone2() {
        final List<String> errorList = new ArrayList<>();
        final WarehouseModel warehouseModel = new WarehouseModel()
                .setName("name")
                .setAddress1("address")
                .setTelephone1("abcs")
                .setLastUser("valid")
                .setLongitude(0.0)
                .setLatitude(0.0);
        assertFalse(WarehousePredicates.isWarehouseModelValidForInsert(errorList).test(warehouseModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isWarehouseModelValidForInsertFailsOnInvalidNotes() {
        final List<String> errorList = new ArrayList<>();
        final WarehouseModel warehouseModel = new WarehouseModel()
                .setName("name")
                .setAddress1("address")
                .setNotes("\\")
                .setTelephone1("12345678")
                .setLastUser("valid")
                .setLongitude(0.0)
                .setLatitude(0.0);
        assertFalse(WarehousePredicates.isWarehouseModelValidForInsert(errorList).test(warehouseModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isWarehouseModelValidForInsertFailsOnNullLastUser() {
        final List<String> errorList = new ArrayList<>();
        final WarehouseModel warehouseModel = new WarehouseModel()
                .setName("name")
                .setTelephone1("12345678")
                .setAddress1("address")
                .setLongitude(0.0)
                .setLatitude(0.0);
        assertFalse(WarehousePredicates.isWarehouseModelValidForInsert(errorList).test(warehouseModel));
        assertEquals(1, errorList.size());
    }

    @Test
    void isWarehouseModelValidForUpdateSuccessful() {
        //Validation of evaluateModel is skipped, as it was tested on tests for isWarehouseModelValidForInsert
        final List<String> errorList = new ArrayList<>();
        final WarehouseModel warehouseModel = new WarehouseModel()
                .setId("w00001")
                .setName("name")
                .setAddress1("address")
                .setTelephone1("12345678")
                .setLastUser("valid")
                .setLongitude(0.0)
                .setLatitude(0.0);
        assertTrue(WarehousePredicates.isWarehouseModelValidForUpdate(errorList).test(warehouseModel));
        assertEquals(0, errorList.size());
    }

    @Test
    void isWarehouseModelValidForUpdateFailsOnNullId() {
        //Validation of evaluateModel is skipped, as it was tested on tests for isWarehouseModelValidForInsert
        final List<String> errorList = new ArrayList<>();
        final WarehouseModel warehouseModel = new WarehouseModel()
                .setName("name")
                .setAddress1("address")
                .setTelephone1("12345678")
                .setLastUser("valid")
                .setLongitude(0.0)
                .setLatitude(0.0);
        assertFalse(WarehousePredicates.isWarehouseModelValidForUpdate(errorList).test(warehouseModel));
        assertEquals(1, errorList.size());
    }

}
