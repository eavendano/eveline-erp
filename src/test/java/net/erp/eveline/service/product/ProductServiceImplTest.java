package net.erp.eveline.service.product;

import config.ServiceTestConfiguration;
import net.erp.eveline.common.TransactionService;
import net.erp.eveline.common.exception.BadRequestException;
import net.erp.eveline.common.exception.NonRetryableException;
import net.erp.eveline.common.exception.NotFoundException;
import net.erp.eveline.common.exception.RetryableException;
import net.erp.eveline.data.entity.Product;
import net.erp.eveline.data.entity.Provider;
import net.erp.eveline.data.repository.ProductRepository;
import net.erp.eveline.data.repository.ProviderRepository;
import net.erp.eveline.model.ActiveProductModel;
import net.erp.eveline.model.ProductModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.OptimisticLockException;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static net.erp.eveline.common.mapper.ProductMapper.toActiveModel;
import static net.erp.eveline.common.mapper.ProductMapper.toEntity;
import static net.erp.eveline.common.mapper.ProductMapper.toModel;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ServiceTestConfiguration.class})
class ProductServiceImplTest {

    @Autowired
    private TransactionService transactionService;

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    @Spy
    private final ProductServiceImpl service = new ProductServiceImpl();

    @BeforeEach
    void setUp() {
        service.setTransactionService(transactionService);
    }


    @Test
    void findAllByProviderSuccessful() {
        //Initialization
        final Provider expectedProvider = mockProvider();
        final Product expectedProduct = mockProduct(expectedProvider);
        final Set<Product> expectedProductSet = Set.of(expectedProduct);

        //Set up
        when(providerRepository.findById(anyString())).thenReturn(of(expectedProvider));
        when(productRepository.findByProviderSetProviderId(anyString())).thenReturn(expectedProductSet);

        //Execution
        final Set<ProductModel> actualProducts = service.findAllByProvider(expectedProvider.getProviderId());

        //Validation
        assertEquals(expectedProductSet.size(), actualProducts.size());
        assertTrue(actualProducts.contains(toModel(expectedProduct)));
        verify(providerRepository, times(1))
                .findById(anyString());
        verify(productRepository, times(1))
                .findByProviderSetProviderId(anyString());
    }

    @Test
    void findAllByProviderThrowsBadRequestExceptionOnInvalidProviderId() {
        //Initialization
        final String providerId = "p0001";

        //Execution
        assertThrows(BadRequestException.class,
                () -> service.findAllByProvider(providerId));
        //Validation
        verify(providerRepository, times(0))
                .findById(anyString());
        verify(productRepository, times(0))
                .findByProviderSetProviderId(anyString());
    }

    @Test
    void findAllByProviderReturnsEmptySetOnNotFoundProvider() {
        //Initialization
        final String providerId = "p00001";

        //Set up
        when(providerRepository.findById(anyString())).thenReturn(Optional.empty());

        //Execution
        final Set<ProductModel> actualProducts = service.findAllByProvider(providerId);

        //Validation
        assertEquals(0, actualProducts.size());
        verify(providerRepository, times(1))
                .findById(anyString());
        verify(productRepository, times(0))
                .findByProviderSetProviderId(anyString());
    }

    @Test
    void findAllByProviderThrowsServiceExceptionAfterRetries() {
        //Initialization
        final String providerId = "p00001";

        //Set up
        when(providerRepository.findById(anyString()))
                .thenThrow(new OptimisticLockException("Optimistic lock test"));

        //Execution
        assertThrows(RetryableException.class, () -> service.findAllByProvider(providerId));

        //Validation
        verify(providerRepository, times(4))
                .findById(anyString());
        verify(productRepository, times(0))
                .findByProviderSetProviderId(anyString());
    }

    @Test
    void findAllByProviderThrowsServiceExceptionOnNonRetryableException() {
        //Initialization
        final String providerId = "p00001";

        //Set up
        when(providerRepository.findById(anyString()))
                .thenThrow(new PermissionDeniedDataAccessException("Optimistic lock test",
                        new Throwable()));

        //Execution
        assertThrows(NonRetryableException.class, () -> service.findAllByProvider(providerId));

        //Validation
        verify(providerRepository, times(1))
                .findById(anyString());
        verify(productRepository, times(0))
                .findByProviderSetProviderId(anyString());
    }

    @Test
    void getProductModelSuccessful() {
        //Initialization
        final Provider expectedProvider = mockProvider();
        final Product expectedProduct = mockProduct(expectedProvider);

        //Set up
        when(productRepository.findById(anyString())).thenReturn(of(expectedProduct));

        //Execution
        final ProductModel actualProduct = service.getProductModel(expectedProduct.getProductId());

        //Validation
        assertEquals(toModel(expectedProduct), actualProduct);
        verify(productRepository, times(1))
                .findById(anyString());
    }

    @Test
    void getProductModelThrowsBadRequestExceptionOnInvalidProductId() {
        //Initialization
        final String productId = "s0001";

        //Execution
        assertThrows(BadRequestException.class,
                () -> service.getProductModel(productId));
        //Validation
        verify(productRepository, times(0))
                .findByProviderSetProviderId(anyString());
    }

    @Test
    void getProductModelThrowsNonRetryableExceptionOnNotFoundProduct() {
        //Set up
        when(productRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        //Execution
        assertThrows(NonRetryableException.class,
                () -> service.getProductModel("s00001"));
        //Validation
        verify(productRepository, times(0))
                .findByProviderSetProviderId(anyString());
    }

    @Test
    void getProductModelThrowsServiceExceptionAfterRetries() {
        //Initialization
        final String productId = "s00001";

        //Set up
        when(productRepository.findById(anyString()))
                .thenThrow(new OptimisticLockException("Optimistic lock test"));

        //Execution
        assertThrows(RetryableException.class, () -> service.getProductModel(productId));

        //Validation
        verify(productRepository, times(4))
                .findById(anyString());
    }

    @Test
    void getProductModelThrowsServiceExceptionOnNonRetryableException() {
        //Initialization
        final String productId = "s00001";

        //Set up
        when(productRepository.findById(anyString()))
                .thenThrow(new PermissionDeniedDataAccessException("Optimistic lock test",
                        new Throwable()));

        //Execution
        assertThrows(NonRetryableException.class, () -> service.getProductModel(productId));

        //Validation
        verify(productRepository, times(1))
                .findById(anyString());
    }

    @Test
    void findByUpcSuccessful() {
        //Initialization
        final Provider expectedProvider = mockProvider();
        final Product expectedProduct = mockProduct(expectedProvider);

        //Set up
        when(productRepository.findByUpc(anyString())).thenReturn(of(expectedProduct));

        //Execution
        final ProductModel actualProduct = service.findByUpc(expectedProduct.getUpc());

        //Validation
        assertEquals(toModel(expectedProduct), actualProduct);
        verify(productRepository, times(1))
                .findByUpc(anyString());
    }

    @Test
    void findByUpcThrowsBadRequestExceptionOnInvalidUpc() {
        //Initialization
        final String upc = "abc";

        //Execution
        assertThrows(BadRequestException.class,
                () -> service.findByUpc(upc));
        //Validation
        verify(productRepository, times(0))
                .findByUpc(anyString());
    }

    @Test
    void findByUpcThrowsNonRetryableExceptionOnNotFoundProduct() {
        //Initialization
        final String upc = "123456789012";

        //Set up
        when(productRepository.findByUpc(anyString()))
                .thenReturn(Optional.empty());

        //Execution
        assertThrows(NonRetryableException.class,
                () -> service.findByUpc(upc));

        //Validation
        verify(productRepository, times(1))
                .findByUpc(anyString());
    }

    @Test
    void findByUpcThrowsServiceExceptionAfterRetries() {
        //Initialization
        final String upc = "123456789012";

        //Set up
        when(productRepository.findByUpc(anyString()))
                .thenThrow(new OptimisticLockException("Optimistic lock test"));

        //Execution
        assertThrows(RetryableException.class, () -> service.findByUpc(upc));

        //Validation
        verify(productRepository, times(4))
                .findByUpc(anyString());
    }

    @Test
    void findByUpcThrowsServiceExceptionOnNonRetryableException() {
        //Initialization
        final String upc = "123456789012";

        //Set up
        when(productRepository.findByUpc(anyString()))
                .thenThrow(new PermissionDeniedDataAccessException("Optimistic lock test",
                        new Throwable()));

        //Execution
        assertThrows(NonRetryableException.class, () -> service.findByUpc(upc));

        //Validation
        verify(productRepository, times(1))
                .findByUpc(anyString());
    }

    @Test
    void upsertProductModel() {
    }

    @Test
    void activateProductSuccessful() {
        //Initialization
        final var activeProductModel = new ActiveProductModel()
                .setEnabled(true)
                .setId("s00001")
                .setLastUser("testUser");

        final Product expectedProduct = toEntity(
                mockProduct(mockProvider())
                        .setEnabled(false),
                activeProductModel);

        //Set up
        when(productRepository.findById(anyString())).thenReturn(of(expectedProduct));
        when(productRepository.save(any())).thenReturn(expectedProduct);

        //Execution
        final var actualProductModel = service.activateProduct(activeProductModel);

        //Validation
        assertEquals(toActiveModel(expectedProduct), actualProductModel);
        assertTrue(actualProductModel.isEnabled());
        verify(productRepository, times(1)).findById(any());
        verify(productRepository, times(1)).save(any());
    }

    @Test
    void activateProductNullModelThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> service.activateProduct(null));
        verify(productRepository, times(0)).findById(any());
        verify(productRepository, times(0)).save(any());
    }

    @Test
    void activateProductInvalidModelThrowsBadRequestException() {
        final var activeProductModel = new ActiveProductModel()
                .setEnabled(null)
                .setId("")
                .setLastUser("");

        assertThrows(BadRequestException.class, () -> service.activateProduct(activeProductModel));
        verify(productRepository, times(0)).findById(any());
        verify(productRepository, times(0)).save(any());
    }

    @Test
    void activateProductFailFindByIdThrowsRetryableException() {
        final var activeProductModel = new ActiveProductModel()
                .setEnabled(true)
                .setId("s00001")
                .setLastUser("testUser");

        when(productRepository.findById(anyString())).thenThrow(new OptimisticLockException("Optimistic Lock Exception"));

        var ex = assertThrows(RetryableException.class, () -> service.activateProduct(activeProductModel));
        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(productRepository, times(4)).findById(any());
        verify(productRepository, times(0)).save(any());
    }

    @Test
    void activateProductFailFindByIdThrowsNonRetryableException() {
        final var activeProductModel = new ActiveProductModel()
                .setEnabled(true)
                .setId("s00001")
                .setLastUser("testUser");

        when(productRepository.findById(anyString())).thenThrow(new RuntimeException("Runtime Exception"));

        var ex = assertThrows(NonRetryableException.class, () -> service.activateProduct(activeProductModel));
        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(productRepository, times(1)).findById(any());
        verify(productRepository, times(0)).save(any());
    }

    @Test
    void activateProductFailFindByIdThrowsNotFoundException() {
        final var activeProductModel = new ActiveProductModel()
                .setEnabled(true)
                .setId("s00001")
                .setLastUser("testUser");

        when(productRepository.findById(anyString())).thenReturn(empty());

        var ex = assertThrows(NonRetryableException.class, () -> service.activateProduct(activeProductModel));
        assertEquals(NotFoundException.class, getRootCause(ex).getClass());
        verify(productRepository, times(1)).findById(any());
        verify(productRepository, times(0)).save(any());
    }

    @Test
    void activateProductFailSaveThrowsRetryableException() {
        final var activeProductModel = new ActiveProductModel()
                .setEnabled(true)
                .setId("s00001")
                .setLastUser("testUser");

        final Product expectedProduct = toEntity(
                mockProduct(mockProvider())
                        .setEnabled(false),
                activeProductModel);

        when(productRepository.findById(anyString())).thenReturn(of(expectedProduct));
        when(productRepository.save(any(Product.class))).thenThrow(new OptimisticLockException("Optimistic Lock Exception"));

        var ex = assertThrows(RetryableException.class, () -> service.activateProduct(activeProductModel));
        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(productRepository, times(4)).findById(any());
        verify(productRepository, times(4)).save(any());
    }

    @Test
    void activateProductFailSaveThrowsNonRetryableException() {
        final var activeProductModel = new ActiveProductModel()
                .setEnabled(true)
                .setId("s00001")
                .setLastUser("testUser");

        final Product expectedProduct = toEntity(
                mockProduct(mockProvider())
                        .setEnabled(false),
                activeProductModel);

        when(productRepository.findById(anyString())).thenReturn(of(expectedProduct));
        when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("Runtime Exception"));

        var ex = assertThrows(NonRetryableException.class, () -> service.activateProduct(activeProductModel));
        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(productRepository, times(1)).findById(any());
        verify(productRepository, times(1)).save(any());
    }

    private Provider mockProvider() {
        return new Provider()
                .setProviderId("p00001");
    }

    private Product mockProduct(final Provider expectedProvider) {
        return new Product()
                .setUpc("123456789012")
                .setProductId("s00001")
                .setProviderSet(Set.of(expectedProvider));
    }
}