package net.erp.eveline.common.predicate;

import net.erp.eveline.model.ProviderModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;
import static net.erp.eveline.common.predicate.ProviderPredicates.isProviderModelValidForInsert;
import static net.erp.eveline.common.predicate.ProviderPredicates.isProviderModelValidForUpdate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class ProviderPredicateTest {

    @Test
    public void isProviderModelValidForInsertSuccessful() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("valid")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertTrue(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(0, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertIdShouldBeNull() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("valid")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnEmptyName() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnSmallName() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("PR")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnLargeName() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("ProviderNameProviderNameProviderNameProviderNameProviderNameProviderNameProviderNameProviderNameProviderName")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnIncompatibleName() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("ProviderName$")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnIncompatibleDescription() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente inválida.`")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnSmallEmail() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("te")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnLargeEmail() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test.test.testtest.test.testtest.test.testtest.test.testtest.test.testtest.test.testtest.test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnInvalidEmail() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test.test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnSmallPhone1() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("1234567")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnLargePhone1() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("123456789123456789123456789")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnIncompatiblePhone1() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("1234567a")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnNullOptionalPhone2() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setTelephone2(null)
                .setLastUser("valid");

        assertTrue(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(0, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnSmallOptionalPhone2() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setTelephone2("1234567")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnLargeOptionalPhone2() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setTelephone2("123456789123456789123456789")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnNullOptionalPhone3() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setTelephone3(null)
                .setLastUser("valid");

        assertTrue(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(0, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnSmallOptionalPhone3() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setTelephone3("1234567")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnLargeOptionalPhone3() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setTelephone3("123456789123456789123456789")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnSmallLastUser() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("va");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnLargeLastUser() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("validLastUservalidLastUservalidLastUservalidLastUservalidLastUservalidLastUservalidLastUservalidLastUser");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForInsertShouldFailOnInvalidLastUser() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("InvalidU$ser");

        assertFalse(isProviderModelValidForInsert(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderSetValidOnEmptySet() {
        assertTrue(ProviderPredicates.isProviderSetValid().test(emptySet()));
    }

    @Test
    public void isProviderSetValidOnSingleValueSet() {
        ProviderModel provider1 = new ProviderModel();
        provider1.setId("p00001");
        Set<ProviderModel> providerSet = Set.of(provider1);
        assertTrue(ProviderPredicates.isProviderSetValid().test(providerSet));
    }

    @Test
    public void isProviderSetValidOnMultipleValueSet() {
        ProviderModel provider1 = new ProviderModel();
        ProviderModel provider2 = new ProviderModel();
        ProviderModel provider3 = new ProviderModel();
        provider1.setId("p00001");
        provider2.setId("p00002");
        provider3.setId("p00003");
        Set<ProviderModel> providerSet = Set.of(provider1, provider2, provider3);
        assertTrue(ProviderPredicates.isProviderSetValid().test(providerSet));
    }

    @Test
    public void isProviderSetInvalidOnSingleValueSet() {
        ProviderModel provider1 = new ProviderModel();
        provider1.setId("p0001");
        Set<ProviderModel> providerSet = Set.of(provider1);
        assertFalse(ProviderPredicates.isProviderSetValid().test(providerSet));
    }

    @Test
    public void isProviderSetInvalidOnMiddleValueSet() {
        ProviderModel provider1 = new ProviderModel();
        ProviderModel provider2 = new ProviderModel();
        ProviderModel provider3 = new ProviderModel();
        provider1.setId("p00001");
        provider2.setId("p0002");
        provider3.setId("p00003");
        Set<ProviderModel> providerSet = Set.of(provider1, provider2, provider3);
        assertFalse(ProviderPredicates.isProviderSetValid().test(providerSet));
    }

    @Test
    public void isProviderSetInvalidOnLastValueSet() {
        ProviderModel provider1 = new ProviderModel();
        ProviderModel provider2 = new ProviderModel();
        ProviderModel provider3 = new ProviderModel();
        provider1.setId("p00001");
        provider2.setId("p00002");
        provider3.setId("p0003");
        Set<ProviderModel> providerSet = Set.of(provider1, provider2, provider3);
        assertFalse(ProviderPredicates.isProviderSetValid().test(providerSet));
    }

    /**
     *
     */

    @Test
    public void isProviderModelValidForUpdateSuccessful() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("valid")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertTrue(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(0, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateIdShouldNotBeNull() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setName("valid")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnSmallId() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p0001")
                .setName("valid")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnLargeId() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p000001")
                .setName("valid")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnInvalidId() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("000001")
                .setName("valid")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnEmptyName() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnSmallName() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("PR")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnLargeName() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("ProviderNameProviderNameProviderNameProviderNameProviderNameProviderNameProviderNameProviderNameProviderName")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnIncompatibleName() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("ProviderName$")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnIncompatibleDescription() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente inválida.`")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnSmallEmail() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("te")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnLargeEmail() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test.test.testtest.test.testtest.test.testtest.test.testtest.test.testtest.test.testtest.test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnInvalidEmail() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test.test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnSmallPhone1() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("1234567")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnLargePhone1() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("123456789123456789123456789")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnIncompatiblePhone1() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("1234567a")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnNullOptionalPhone2() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setTelephone2(null)
                .setLastUser("valid");

        assertTrue(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(0, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnSmallOptionalPhone2() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setTelephone2("1234567")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnLargeOptionalPhone2() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setTelephone2("123456789123456789123456789")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnNullOptionalPhone3() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setTelephone3(null)
                .setLastUser("valid");

        assertTrue(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(0, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnSmallOptionalPhone3() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setTelephone3("1234567")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnLargeOptionalPhone3() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setTelephone3("123456789123456789123456789")
                .setLastUser("valid");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnSmallLastUser() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("va");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnLargeLastUser() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("validLastUservalidLastUservalidLastUservalidLastUservalidLastUservalidLastUservalidLastUservalidLastUser");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }

    @Test
    public void isProviderModelValidForUpdateShouldFailOnInvalidLastUser() {
        final List<String> errorList = new ArrayList<>();
        final ProviderModel providerModel = new ProviderModel()
                .setId("p00001")
                .setName("ProviderName")
                .setDescription("Esta es una descripción totalmente válida.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("InvalidU$ser");

        assertFalse(isProviderModelValidForUpdate(errorList).test(providerModel));
        assertEquals(1, errorList.size());
    }
}
