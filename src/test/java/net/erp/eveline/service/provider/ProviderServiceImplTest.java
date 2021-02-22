package net.erp.eveline.service.provider;

import config.ServiceTestConfiguration;
import net.erp.eveline.common.TransactionService;
import net.erp.eveline.common.exception.RetryableException;
import net.erp.eveline.data.entity.Provider;
import net.erp.eveline.data.repository.ProviderRepository;
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

import static net.erp.eveline.common.mapper.ProviderMapper.toModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        verify(providerRepository, times(1))
                .findAll();
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

    List<Provider> mockProviderList(int length) {
        return IntStream.rangeClosed(0, length - 1)
                .mapToObj(this::mockProvider)
                .collect(Collectors.toList());
    }

    Provider mockProvider(int idx) {
        return new Provider()
                .setProviderId("p" + idx)
                .setDescription("description")
                .setEmail("email")
                .setLastUser("user");
    }


}