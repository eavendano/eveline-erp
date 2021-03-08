package net.erp.eveline.service.provider;

import config.ServiceTestConfiguration;
import net.erp.eveline.common.TransactionService;
import net.erp.eveline.common.exception.BadRequestException;
import net.erp.eveline.common.exception.NonRetryableException;
import net.erp.eveline.common.exception.NotFoundException;
import net.erp.eveline.common.exception.RetryableException;
import net.erp.eveline.data.entity.Provider;
import net.erp.eveline.data.repository.ProviderRepository;
import net.erp.eveline.model.ActiveProviderModel;
import net.erp.eveline.model.ProviderModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.OptimisticLockException;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static net.erp.eveline.common.mapper.ProviderMapper.toActiveModel;
import static net.erp.eveline.common.mapper.ProviderMapper.toModel;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ServiceTestConfiguration.class})
class ProviderServiceImplTest {

    @Mock
    private ProviderRepository providerRepository;

    @Autowired
    private TransactionService transactionService;

    @InjectMocks
    @Spy
    private final ProviderServiceImpl service = new ProviderServiceImpl();

    @BeforeEach
    void setUp() {
        service.setTransactionService(transactionService);
    }

    @Test
    void getProviderModelsSuccessful() {
        //Initialization
        final int expectedLength = 2;

        //Set up
        final var mockProviderList = mockProviderList(expectedLength);
        when(providerRepository.findAll()).thenReturn(mockProviderList);

        //Execution
        final Set<ProviderModel> actualProviderModels = service.findAll();

        //Validation
        verify(providerRepository, times(1))
                .findAll();
        assertEquals(expectedLength, actualProviderModels.size());
        assertEquals(toModel(Set.copyOf(mockProviderList(expectedLength))).stream().sorted(Comparator.comparing(ProviderModel::getId)).collect(Collectors.toCollection(LinkedHashSet::new)).toString(), actualProviderModels.stream().sorted(Comparator.comparing(ProviderModel::getId)).collect(Collectors.toCollection(LinkedHashSet::new)).toString());
    }

    @Test
    void getProviderModelsSuccessfulWithEmptyList() {
        //Initialization
        final int expectedLength = 0;

        //Set up
        when(providerRepository.findAll()).thenReturn(mockProviderList(expectedLength));

        //Execution
        final Set<ProviderModel> actualProviderModels = service.findAll();

        //Validation
        verify(providerRepository, times(1)).findAll();
        assertEquals(expectedLength, actualProviderModels.size());
        assertEquals(toModel(Set.copyOf(mockProviderList(expectedLength))), actualProviderModels);
    }

    @Test
    void getProviderModelsThrowsServiceExceptionAfterRetries() {
        //Set up
        when(providerRepository.findAll()).thenThrow(new OptimisticLockException("Optimistic lock test"));

        //Execution
        assertThrows(RetryableException.class, service::findAll);

        //Validation
        verify(providerRepository, times(4))
                .findAll();
    }

    @Test
    void getProviderModelSuccessful() {
        final var providerId = "p00001";
        final var provider = mockIndividualProvider(providerId);
        when(providerRepository.findById(providerId)).thenReturn(ofNullable(provider));

        final var resultProviderModel = service.getProviderModel(providerId);
        assertEquals(toModel(provider), resultProviderModel);
        verify(providerRepository, times(1)).findById(any(String.class));
    }

    @Test
    void getProviderModelNotFoundEntityThrowsNotFoundException() {
        final var providerId = "p00001";
        when(providerRepository.findById(providerId)).thenReturn(empty());

        var ex = assertThrows(NonRetryableException.class, () -> service.getProviderModel(providerId));
        assertEquals(NotFoundException.class, getRootCause(ex).getClass());
        verify(providerRepository, times(1)).findById(any(String.class));
    }

    @Test
    void getProviderModelWithNullIdThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> service.getProviderModel(null));
        verify(providerRepository, times(0)).findById(any(String.class));
    }

    @Test
    void getProviderModelWithEmptyIdThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> service.getProviderModel(""));
        verify(providerRepository, times(0)).findById(any(String.class));
    }

    @Test
    void getProviderModelWithSmallIdThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> service.getProviderModel("p0001"));
        verify(providerRepository, times(0)).findById(any(String.class));
    }

    @Test
    void getProviderModelWithLargeIdThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> service.getProviderModel("p000001"));
        verify(providerRepository, times(0)).findById(any(String.class));
    }

    @Test
    void upsertProviderModelWithNullModelThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> service.upsertProviderModel(null));
        verify(providerRepository, times(0)).save(any(Provider.class));
        verify(providerRepository, times(0)).findById(any(String.class));
    }

    @Test
    void upsertProviderModelForInsertSuccessfulRequest() {
        final var providerModel = new ProviderModel()
                .setName("valid")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        final var provider = mockIndividualProvider("p00002");

        when(providerRepository.save(any(Provider.class))).thenReturn(provider);

        var resultModel = service.upsertProviderModel(providerModel);
        assertEquals(toModel(provider), resultModel);
        verify(providerRepository, times(1)).save(any(Provider.class));
        verify(providerRepository, times(0)).findById(any(String.class));
    }

    @Test
    void upsertProviderModelForInsertWithInvalidModelThrowsBadRequestException() {
        final var providerModel = new ProviderModel()
                .setName("")
                .setDescription("")
                .setEmail("")
                .setTelephone1("")
                .setLastUser("");

        var ex = assertThrows(BadRequestException.class, () -> service.upsertProviderModel(providerModel));
        verify(providerRepository, times(0)).save(any(Provider.class));
        verify(providerRepository, times(0)).findById(any(String.class));
    }

    @Test
    void upsertProviderModelForInsertSaveFailsThrowsRetryableException() {
        final var providerModel = new ProviderModel()
                .setName("valid")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        when(providerRepository.save(any(Provider.class))).thenThrow(new OptimisticLockException("Optimistic lock test"));

        var ex = assertThrows(RetryableException.class, () -> service.upsertProviderModel(providerModel));
        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(providerRepository, times(4)).save(any(Provider.class));
        verify(providerRepository, times(0)).findById(any(String.class));
    }

    @Test
    void upsertProviderModelForInsertSaveFailsThrowsNonRetryableException() {
        final var providerModel = new ProviderModel()
                .setName("valid")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        when(providerRepository.save(any(Provider.class))).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.upsertProviderModel(providerModel));
        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(providerRepository, times(1)).save(any(Provider.class));
        verify(providerRepository, times(0)).findById(any(String.class));
    }

    @Test
    void upsertProviderModelForUpdateSuccessfulRequest() {
        final var providerModel = new ProviderModel()
                .setId("p00003")
                .setName("valid")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        final var provider = mockIndividualProvider("p00003");

        when(providerRepository.findById("p00003")).thenReturn(of(provider));
        when(providerRepository.save(any(Provider.class))).thenReturn(mockIndividualProvider("p00003"));

        var resultModel = service.upsertProviderModel(providerModel);
        assertEquals(toModel(provider), resultModel);
        verify(providerRepository, times(1)).save(any(Provider.class));
        verify(providerRepository, times(1)).findById(any(String.class));
    }

    @Test
    void upsertProviderModelForUpdateWithInvalidModelThrowsBadRequestException() {
        final var providerModel = new ProviderModel()
                .setId("p00004")
                .setName("")
                .setDescription("")
                .setEmail("")
                .setTelephone1("")
                .setLastUser("");

        assertThrows(BadRequestException.class, () -> service.upsertProviderModel(providerModel));
        verify(providerRepository, times(0)).save(any(Provider.class));
        verify(providerRepository, times(0)).findById(any(String.class));
    }

    @Test
    void upsertProviderModelForUpdateProviderNotFoundThrowsNotFoundException() {
        final var providerModel = new ProviderModel()
                .setId("p00003")
                .setName("valid")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        when(providerRepository.findById("p00003")).thenReturn(empty());

        var ex = assertThrows(NonRetryableException.class, () -> service.upsertProviderModel(providerModel));

        assertEquals(NotFoundException.class, getRootCause(ex).getClass());
        verify(providerRepository, times(0)).save(any(Provider.class));
        verify(providerRepository, times(1)).findById(any(String.class));
    }

    @Test
    void upsertProviderModelForUpdateSaveFailsThrowsRetryableException() {
        final var providerModel = new ProviderModel()
                .setId("p00003")
                .setName("valid")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        when(providerRepository.findById("p00003")).thenReturn(ofNullable(mockIndividualProvider("p00003")));

        when(providerRepository.save(any(Provider.class))).thenThrow(new OptimisticLockException("Optimistic lock test"));

        var ex = assertThrows(RetryableException.class, () -> service.upsertProviderModel(providerModel));
        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(providerRepository, times(4)).save(any(Provider.class));
        verify(providerRepository, times(4)).findById(any(String.class));
    }

    @Test
    void upsertProviderModelForUpdateSaveFailsThrowsNonRetryableException() {
        final var providerModel = new ProviderModel()
                .setId("p00003")
                .setName("valid")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        when(providerRepository.findById("p00003")).thenReturn(ofNullable(mockIndividualProvider("p00003")));
        when(providerRepository.save(any(Provider.class))).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.upsertProviderModel(providerModel));
        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(providerRepository, times(1)).save(any(Provider.class));
        verify(providerRepository, times(1)).findById(any(String.class));
    }

    @Test
    void upsertProviderModelForUpdateFindByIdFailsThrowsRetryableException() {
        final var providerModel = new ProviderModel()
                .setId("p00003")
                .setName("valid")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        when(providerRepository.findById("p00003")).thenThrow(new OptimisticLockException("Optimistic lock test"));

        var ex = assertThrows(RetryableException.class, () -> service.upsertProviderModel(providerModel));
        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(providerRepository, times(0)).save(any(Provider.class));
        verify(providerRepository, times(4)).findById(any(String.class));
    }

    @Test
    void upsertProviderModelForUpdateFindByIdFailsThrowsNonRetryableException() {
        final var providerModel = new ProviderModel()
                .setId("p00003")
                .setName("valid")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");

        when(providerRepository.findById("p00003")).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.upsertProviderModel(providerModel));
        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(providerRepository, times(0)).save(any(Provider.class));
        verify(providerRepository, times(1)).findById(any(String.class));
    }

    @Test
    void activateProviderSuccessfulRequest() {
        var activeProviderModel = new ActiveProviderModel()
                .setId("p00001")
                .setLastUser("eavendano")
                .setEnabled(true);

        final var mockProvider = mockIndividualProvider("p00001");

        final var resultMock = mockIndividualProvider("p00001");
        resultMock.setLastUser("eavendano");
        resultMock.setEnabled(true);

        when(providerRepository.findById("p00001")).thenReturn(of(mockProvider));
        when(providerRepository.save(any(Provider.class))).thenReturn(resultMock);

        var result = service.activateProvider(activeProviderModel);

        assertEquals(toActiveModel(resultMock), result);
        verify(providerRepository, times(1)).save(any(Provider.class));
        verify(providerRepository, times(1)).findById(any(String.class));
    }

    @Test
    void activateProviderNullModelThrowsNullPointerException() {
        var ex = assertThrows(NullPointerException.class, () -> service.activateProvider(null));
        verify(providerRepository, times(0)).save(any(Provider.class));
        verify(providerRepository, times(0)).findById(any(String.class));
    }

    @Test
    void activateProviderInvalidModelModelThrowsBadRequest() {
        var activeProviderModel = new ActiveProviderModel()
                .setId("")
                .setLastUser("")
                .setEnabled(null);

        var ex = assertThrows(BadRequestException.class, () -> service.activateProvider(activeProviderModel));

        assertEquals(BadRequestException.class, getRootCause(ex).getClass());
        verify(providerRepository, times(0)).save(any(Provider.class));
        verify(providerRepository, times(0)).findById(any(String.class));
    }

    @Test
    void activateProviderFindByIdFailsThrowsRetryableException() {
        var activeProviderModel = new ActiveProviderModel()
                .setId("p00001")
                .setLastUser("eavendano")
                .setEnabled(true);

        when(providerRepository.findById("p00001")).thenThrow(new OptimisticLockException("Optimistic lock exception"));

        var ex = assertThrows(RetryableException.class, () -> service.activateProvider(activeProviderModel));

        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(providerRepository, times(0)).save(any(Provider.class));
        verify(providerRepository, times(4)).findById(any(String.class));
    }

    @Test
    void activateProviderFindByIdFailsThrowsNonRetryableException() {
        var activeProviderModel = new ActiveProviderModel()
                .setId("p00001")
                .setLastUser("eavendano")
                .setEnabled(true);

        when(providerRepository.findById("p00001")).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.activateProvider(activeProviderModel));

        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(providerRepository, times(0)).save(any(Provider.class));
        verify(providerRepository, times(1)).findById(any(String.class));
    }

    @Test
    void activateProviderFindByIdFailsThrowsNotFoundException() {
        var activeProviderModel = new ActiveProviderModel()
                .setId("p00001")
                .setLastUser("eavendano")
                .setEnabled(true);

        when(providerRepository.findById("p00001")).thenReturn(empty());

        var ex = assertThrows(NonRetryableException.class, () -> service.activateProvider(activeProviderModel));

        assertEquals(NotFoundException.class, getRootCause(ex).getClass());
        verify(providerRepository, times(0)).save(any(Provider.class));
        verify(providerRepository, times(1)).findById(any(String.class));
    }

    @Test
    void activateProviderSaveFailsThrowsRetryableException() {
        var activeProviderModel = new ActiveProviderModel()
                .setId("p00001")
                .setLastUser("eavendano")
                .setEnabled(true);

        final var mockProvider = mockIndividualProvider("p00001");

        when(providerRepository.findById("p00001")).thenReturn(of(mockProvider));
        when(providerRepository.save(any(Provider.class))).thenThrow(new OptimisticLockException("Optimistic lock exception"));

        var ex = assertThrows(RetryableException.class, () -> service.activateProvider(activeProviderModel));

        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(providerRepository, times(4)).save(any(Provider.class));
        verify(providerRepository, times(4)).findById(any(String.class));
    }

    @Test
    void activateProviderSaveFailsThrowsNonRetryableException() {
        var activeProviderModel = new ActiveProviderModel()
                .setId("p00001")
                .setLastUser("eavendano")
                .setEnabled(true);

        final var mockProvider = mockIndividualProvider("p00001");

        when(providerRepository.findById("p00001")).thenReturn(of(mockProvider));

        when(providerRepository.save(any(Provider.class))).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.activateProvider(activeProviderModel));

        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(providerRepository, times(1)).save(any(Provider.class));
        verify(providerRepository, times(1)).findById(any(String.class));
    }

    private List<Provider> mockProviderList(int length) {
        return IntStream.rangeClosed(0, length - 1)
                .mapToObj(this::mockProvider)
                .collect(Collectors.toList());
    }

    private Provider mockProvider(int idx) {
        return new Provider()
                .setProviderId("p" + idx)
                .setDescription("description")
                .setEmail("email")
                .setLastUser("user");
    }

    private Provider mockIndividualProvider(final String id) {
        return new Provider()
                .setProviderId(id)
                .setName("valid")
                .setDescription("Esta es una descripción totalmente válida. Por eso no puede fa$har.")
                .setEmail("test@test.com")
                .setTelephone1("12345678")
                .setLastUser("valid");
    }


}