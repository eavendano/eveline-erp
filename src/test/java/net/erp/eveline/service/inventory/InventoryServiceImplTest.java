package net.erp.eveline.service.inventory;

import config.ServiceTestConfiguration;
import net.erp.eveline.common.TransactionService;
import net.erp.eveline.common.exception.BadRequestException;
import net.erp.eveline.common.exception.NonRetryableException;
import net.erp.eveline.common.exception.NotFoundException;
import net.erp.eveline.common.exception.RetryableException;
import net.erp.eveline.data.entity.Brand;
import net.erp.eveline.data.entity.Inventory;
import net.erp.eveline.data.entity.Product;
import net.erp.eveline.data.entity.Warehouse;
import net.erp.eveline.data.repository.InventoryRepository;
import net.erp.eveline.data.repository.ProductRepository;
import net.erp.eveline.data.repository.WarehouseRepository;
import net.erp.eveline.model.ActiveInventoryModel;
import net.erp.eveline.model.BrandModel;
import net.erp.eveline.model.InventoryModel;
import net.erp.eveline.model.ProductModel;
import net.erp.eveline.model.WarehouseModel;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static net.erp.eveline.common.mapper.InventoryMapper.toActiveModel;
import static net.erp.eveline.common.mapper.InventoryMapper.toEntity;
import static net.erp.eveline.common.mapper.InventoryMapper.toModel;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ServiceTestConfiguration.class})
public class InventoryServiceImplTest {


    @Autowired
    private TransactionService transactionService;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    @Spy
    private final InventoryServiceImpl service = new InventoryServiceImpl();


    @BeforeEach
    void setUp() {
        service.setTransactionService(transactionService);
    }


    @Test
    void findAllSuccessful() {
        //Initialization
        final Inventory expectedInventory = mockInventory("i00001");
        final List<Inventory> expectedInventoryList = List.of(expectedInventory);

        //Set up
        when(inventoryRepository.findAll()).thenReturn(expectedInventoryList);

        //Execution
        final Set<InventoryModel> inventoryList = service.findAll();

        //Validation
        assertEquals(expectedInventoryList.size(), inventoryList.size());
        assertTrue(inventoryList.contains(toModel(expectedInventory)));
        verify(inventoryRepository, times(1))
                .findAll();
    }


    @Test
    void findAllByWarehouseSuccessful() {
        //Initialization
        final Inventory expectedInventory = mockInventory("i00001");
        final Set<Inventory> expectedInventoryList = Set.of(expectedInventory);

        //Set up
        when(inventoryRepository.findByWarehouseWarehouseId(anyString())).thenReturn(expectedInventoryList);
        when(warehouseRepository.findById(anyString())).thenReturn(ofNullable(mockWarehouse("w00001")));

        //Execution
        final Set<InventoryModel> inventoryList = service.findAllByWarehouse("w00001");

        //Validation
        assertEquals(expectedInventoryList.size(), inventoryList.size());
        assertTrue(inventoryList.contains(toModel(expectedInventory)));
        verify(inventoryRepository, times(1)).findByWarehouseWarehouseId("w00001");
        verify(warehouseRepository, times(1)).findById("w00001");
    }

    @Test
    void findAllByWarehouseIdThrowsBadRequestExceptionOnInvalidWarehouseId() {
        //Set up
        assertThrows(BadRequestException.class,
                () -> service.findAllByWarehouse("w0001"));

        //Validation
        assertThrows(BadRequestException.class, () -> service.findAllByWarehouse("w0001"));
    }

    @Test
    void findAllByWarehouseReturnsEmptySetOnNotFoundWarehouse() {
        //Set up
        when(warehouseRepository.findById(anyString())).thenReturn(empty());

        //Execution
        final Set<InventoryModel> inventoryList = service.findAllByWarehouse("w00001");

        //Validation
        assertEquals(0, inventoryList.size());
        verify(warehouseRepository, times(1))
                .findById(anyString());
        verify(inventoryRepository, times(0))
                .findByWarehouseWarehouseId(anyString());

    }


    @Test
    void findAllByWarehouseThrowsServiceExceptionOnFindByIdAfterRetries() {
        //Initialization
        final String warehouseId = "w00001";

        //Set up
        when(warehouseRepository.findById(anyString()))
                .thenThrow(new OptimisticLockException("Optimistic lock test"));

        //Execution
        assertThrows(RetryableException.class, () -> service.findAllByWarehouse(warehouseId));

        //Validation
        verify(warehouseRepository, times(4))
                .findById(anyString());
        verify(inventoryRepository, times(0))
                .findByWarehouseWarehouseId(anyString());
    }

    @Test
    void findAllByProviderThrowsServiceExceptionOnNonRetryableExceptionAtFindById() {
        //Initialization
        final String warehouseId = "w00001";

        //Set up
        when(warehouseRepository.findById(anyString()))
                .thenThrow(new PermissionDeniedDataAccessException("Optimistic lock test",
                        new Throwable()));

        //Execution
        assertThrows(NonRetryableException.class, () -> service.findAllByWarehouse(warehouseId));

        //Validation
        verify(warehouseRepository, times(1))
                .findById(anyString());
        verify(inventoryRepository, times(0))
                .findByWarehouseWarehouseId(anyString());
    }

    @Test
    void findAllByWarehouseThrowsServiceExceptionOnFindByWarehouseAfterRetries() {
        //Initialization
        final Warehouse expectedWarehouse = mockWarehouse("w00001");

        //Set up
        when(warehouseRepository.findById(anyString())).thenReturn(of(expectedWarehouse));
        when(inventoryRepository.findByWarehouseWarehouseId(anyString()))
                .thenThrow(new OptimisticLockException("Optimistic lock test"));

        //Execution
        assertThrows(RetryableException.class,
                () -> service.findAllByWarehouse(expectedWarehouse.getWarehouseId()));

        //Validation
        verify(warehouseRepository, times(4))
                .findById(anyString());
        verify(inventoryRepository, times(4))
                .findByWarehouseWarehouseId(anyString());
    }

    @Test
    void findAllByWarehouseThrowsServiceExceptionOnNonRetryableExceptionAtFindByWarehouse() {
        //Initialization
        final Warehouse expectedWarehouse = mockWarehouse("w00001");

        //Set up
        when(warehouseRepository.findById(anyString())).thenReturn(of(expectedWarehouse));
        when(inventoryRepository.findByWarehouseWarehouseId(anyString()))
                .thenThrow(new PermissionDeniedDataAccessException("Optimistic lock test",
                        new Throwable()));

        //Execution
        assertThrows(NonRetryableException.class,
                () -> service.findAllByWarehouse(expectedWarehouse.getWarehouseId()));

        //Validation
        verify(warehouseRepository, times(1))
                .findById(anyString());
        verify(inventoryRepository, times(1))
                .findByWarehouseWarehouseId(anyString());
    }

    @Test
    void getInventoryModelSuccessful() {
        //Initialization
        final Inventory expectedInventory = mockInventory("i00001");

        //Set up
        when(inventoryRepository.findById(anyString())).thenReturn(of(expectedInventory));

        //Execution
        final InventoryModel actualInventory = service.getInventoryModel(expectedInventory.getInventoryId());

        //Validation
        assertEquals(toModel(expectedInventory), actualInventory);
        verify(inventoryRepository, times(1))
                .findById(anyString());
    }

    @Test
    void getInventoryModelThrowsBadRequestExceptionOnInvalidInventoryId() {
        //Initialization
        final String inventoryId = "i0001";

        //Execution
        assertThrows(BadRequestException.class,
                () -> service.getInventoryModel(inventoryId));
        //Validation
        verify(inventoryRepository, times(0))
                .findByWarehouseWarehouseId(anyString());
    }

    @Test
    void getInventoryModelThrowsNonRetryableExceptionOnNotFoundInventory() {
        //Set up
        when(inventoryRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        //Execution
        assertThrows(NonRetryableException.class,
                () -> service.getInventoryModel("i00001"));
        //Validation
        verify(inventoryRepository, times(0))
                .findByWarehouseWarehouseId(anyString());
    }

    @Test
    void getInventoryModelThrowsServiceExceptionAfterRetries() {
        //Initialization
        final String inventoryId = "i00001";

        //Set up
        when(inventoryRepository.findById(anyString()))
                .thenThrow(new OptimisticLockException("Optimistic lock test"));

        //Execution
        assertThrows(RetryableException.class, () -> service.getInventoryModel(inventoryId));

        //Validation
        verify(inventoryRepository, times(4))
                .findById(anyString());
    }

    @Test
    void getInventoryModelThrowsServiceExceptionOnNonRetryableException() {
        //Initialization
        final String inventoryId = "i00001";

        //Set up
        when(inventoryRepository.findById(anyString()))
                .thenThrow(new PermissionDeniedDataAccessException("Optimistic lock test",
                        new Throwable()));

        //Execution
        assertThrows(NonRetryableException.class, () -> service.getInventoryModel(inventoryId));

        //Validation
        verify(inventoryRepository, times(1))
                .findById(anyString());
    }

    @Test
    void insertInventoryModelSuccessful() {
        //Initialization

        final InventoryModel inventory = mockInventoryModel(null);
        //Set up
        when(warehouseRepository.existsById(anyString()))
                .thenReturn(true);
        when(productRepository.existsById(anyString()))
                .thenReturn(true);
        when(inventoryRepository.save(any()))
                .thenReturn(toEntity(inventory));

        //Execution
        final InventoryModel actualInventory = service.upsertInventoryModel(inventory);

        //Validation
        assertEquals(inventory, actualInventory);
        verify(warehouseRepository, times(1))
                .existsById(any());
        verify(productRepository, times(1))
                .existsById(any());
        verify(inventoryRepository, times(0))
                .findById(any());
        verify(inventoryRepository, times(1))
                .save(any());
    }

    @Test
    void upsertInventoryModelSuccessful() {
        //Initialization
        final InventoryModel inventory = mockInventoryModel("i00001");
        //Set up
        when(warehouseRepository.existsById(anyString()))
                .thenReturn(true);
        when(productRepository.existsById(anyString()))
                .thenReturn(true);
        when(inventoryRepository.findById(any()))
                .thenReturn(Optional.of(toEntity(inventory)));
        when(inventoryRepository.save(any()))
                .thenReturn(toEntity(inventory));

        //Execution
        final InventoryModel actualInventory = service.upsertInventoryModel(inventory);

        //Validation
        assertEquals(inventory, actualInventory);
        verify(warehouseRepository, times(1))
                .existsById(any());
        verify(productRepository, times(1))
                .existsById(any());
        verify(inventoryRepository, times(1))
                .findById(any());
        verify(inventoryRepository, times(1))
                .save(any());
    }

    @Test
    void upsertInventoryModelThrowsNPEOnNullModel() {
        //Execution
        assertThrows(NullPointerException.class,
                () -> service.upsertInventoryModel(null));
        //Validation
        verify(warehouseRepository, times(0))
                .existsById(any());
        verify(productRepository, times(0))
                .existsById(any());
        verify(inventoryRepository, times(0))
                .findById(any());
        verify(inventoryRepository, times(0))
                .save(any());
    }

    @Test
    void upsertInventoryModelThrowsNonRetryableExceptionOnNotFoundProvider() {
        //Initialization
        final InventoryModel inventory = mockInventoryModel("i00001");
        //Set up
        when(warehouseRepository.existsById(anyString()))
                .thenReturn(true);
        when(productRepository.existsById(anyString()))
                .thenReturn(true);
        when(inventoryRepository.findById(any()))
                .thenReturn(Optional.empty());

        //Execution
        assertThrows(NonRetryableException.class,
                () -> service.upsertInventoryModel(inventory));

        //Validation
        verify(warehouseRepository, times(1))
                .existsById(any());
        verify(productRepository, times(1))
                .existsById(any());
        verify(inventoryRepository, times(1))
                .findById(any());
        verify(inventoryRepository, times(0))
                .save(any());
    }

    @Test
    void upsertInventoryModelThrowsBadRequestExceptionOnInvalidModel() {
        //Initialization
        final InventoryModel inventory = mockInventoryModel("invalidId");
        //Set up
        when(warehouseRepository.existsById(anyString()))
                .thenReturn(true);
        when(productRepository.existsById(anyString()))
                .thenReturn(true);
        when(inventoryRepository.findById(any()))
                .thenReturn(Optional.of(toEntity(inventory)));
        when(inventoryRepository.save(any()))
                .thenReturn(toEntity(inventory));

        //Execution
        assertThrows(BadRequestException.class,
                () -> service.upsertInventoryModel(inventory));

        //Validation
        verify(warehouseRepository, times(0))
                .existsById(any());
        verify(productRepository, times(0))
                .existsById(any());
        verify(inventoryRepository, times(0))
                .findById(any());
        verify(inventoryRepository, times(0))
                .save(any());
    }

    @Test
    void upsertProductModelThrowsNonRetryableExceptionOnUpsertWithNotFoundWarehouse() {
        //Initialization
        final InventoryModel inventory = mockInventoryModel("i00001");
        //Set up
        when(warehouseRepository.existsById(anyString()))
                .thenReturn(false);
        when(productRepository.existsById(anyString()))
                .thenReturn(true);
        //Execution
        assertThrows(NonRetryableException.class,
                () -> service.upsertInventoryModel(inventory));

        //Validation
        verify(warehouseRepository, times(1))
                .existsById(any());
        verify(productRepository, times(1))
                .existsById(any());
        verify(inventoryRepository, times(0))
                .findById(any());
        verify(inventoryRepository, times(0))
                .save(any());
    }

    @Test
    void upsertProductModelThrowsNonRetryableExceptionOnUpsertWithNotFoundProduct() {
        //Initialization
        final InventoryModel inventory = mockInventoryModel("i00001");
        //Set up
        when(warehouseRepository.existsById(anyString()))
                .thenReturn(true);
        when(productRepository.existsById(anyString()))
                .thenReturn(false);

        //Execution
        assertThrows(NonRetryableException.class,
                () -> service.upsertInventoryModel(inventory));

        verify(warehouseRepository, times(1))
                .existsById(any());
        verify(productRepository, times(1))
                .existsById(any());
        verify(inventoryRepository, times(0))
                .findById(any());
        verify(inventoryRepository, times(0))
                .save(any());
    }


    @Test
    void upsertInventoryModelThrowsServiceExceptionOnExistByIdAfterRetries() {
        //Initialization
        final InventoryModel inventory = mockInventoryModel("i00001");
        //Set up
        when(warehouseRepository.existsById(anyString()))
                .thenThrow(new OptimisticLockException("Optimistic lock test"));

        //Execution
        assertThrows(RetryableException.class, () -> service.upsertInventoryModel(inventory));

        //Validation
        verify(warehouseRepository, times(4))
                .existsById(any());
        verify(productRepository, times(0))
                .existsById(any());
        verify(inventoryRepository, times(0))
                .findById(any());
        verify(inventoryRepository, times(0))
                .save(any());
    }


    @Test
    void upsertInventoryModelThrowsServiceExceptionOnFindByIdAfterRetries() {
        //Initialization
        final InventoryModel inventory = mockInventoryModel("i00001");
        //Set up
        when(warehouseRepository.existsById(anyString()))
                .thenReturn(true);
        when(productRepository.existsById(anyString()))
                .thenReturn(true);
        when(inventoryRepository.findById(any()))
                .thenThrow(new OptimisticLockException("Optimistic lock test"));

        //Execution
        assertThrows(RetryableException.class, () -> service.upsertInventoryModel(inventory));

        //Validation
        verify(warehouseRepository, times(4))
                .existsById(any());
        verify(productRepository, times(4))
                .existsById(any());
        verify(inventoryRepository, times(4))
                .findById(any());
        verify(inventoryRepository, times(0))
                .save(any());
    }

    @Test
    void upsertInventoryModelThrowsServiceExceptionOnSaveAfterRetries() {
        //Initialization
        final InventoryModel inventory = mockInventoryModel("i00001");
        //Set up
        when(warehouseRepository.existsById(anyString()))
                .thenReturn(true);
        when(productRepository.existsById(anyString()))
                .thenReturn(true);
        when(inventoryRepository.findById(any()))
                .thenReturn(Optional.of(toEntity(inventory)));
        when(inventoryRepository.save(any()))
                .thenThrow(new OptimisticLockException("Optimistic lock test"));

        //Execution
        assertThrows(RetryableException.class, () -> service.upsertInventoryModel(inventory));

        //Validation
        verify(warehouseRepository, times(4))
                .existsById(any());
        verify(productRepository, times(4))
                .existsById(any());
        verify(inventoryRepository, times(4))
                .findById(any());
        verify(inventoryRepository, times(4))
                .save(any());
    }


    @Test
    void upserInventoryModelExistsByIdThrowsServiceExceptionOnNonRetryableException() {
        //Initialization
        final InventoryModel inventory = mockInventoryModel("i00001");
        //Set up
        when(warehouseRepository.existsById(anyString()))
                .thenThrow(new PermissionDeniedDataAccessException("Optimistic lock test",
                        new Throwable()));

        //Execution
        assertThrows(NonRetryableException.class, () -> service.upsertInventoryModel(inventory));

        //Validation
        verify(warehouseRepository, times(1))
                .existsById(any());
        verify(productRepository, times(0))
                .existsById(any());
        verify(inventoryRepository, times(0))
                .findById(any());
        verify(inventoryRepository, times(0))
                .save(any());
    }

    @Test
    void upsertProductModelFindByIdThrowsServiceExceptionOnNonRetryableException() {
        //Initialization
        final InventoryModel inventory = mockInventoryModel("i00001");
        //Set up
        when(warehouseRepository.existsById(anyString()))
                .thenReturn(true);
        when(productRepository.existsById(anyString()))
                .thenReturn(true);
        when(inventoryRepository.findById(any()))
                .thenThrow(new PermissionDeniedDataAccessException("Optimistic lock test",
                        new Throwable()));

        //Execution
        assertThrows(NonRetryableException.class, () -> service.upsertInventoryModel(inventory));

        //Validation
        verify(warehouseRepository, times(1))
                .existsById(any());
        verify(productRepository, times(1))
                .existsById(any());
        verify(inventoryRepository, times(1))
                .findById(any());
        verify(inventoryRepository, times(0))
                .save(any());
    }

    @Test
    void upsertProductModelSaveThrowsServiceExceptionOnNonRetryableException() {
        //Initialization
        final InventoryModel inventory = mockInventoryModel("i00001");
        //Set up
        when(warehouseRepository.existsById(anyString()))
                .thenReturn(true);
        when(productRepository.existsById(anyString()))
                .thenReturn(true);
        when(inventoryRepository.findById(any()))
                .thenReturn(Optional.of(toEntity(inventory)));
        when(inventoryRepository.save(any()))
                .thenThrow(new PermissionDeniedDataAccessException("Optimistic lock test",
                        new Throwable()));
        //Execution
        assertThrows(NonRetryableException.class, () -> service.upsertInventoryModel(inventory));

        //Validation
        verify(warehouseRepository, times(1))
                .existsById(any());
        verify(productRepository, times(1))
                .existsById(any());
        verify(inventoryRepository, times(1))
                .findById(any());
        verify(inventoryRepository, times(1))
                .save(any());
    }

    @Test
    void activateInventorySuccessful() {
        //Initialization
        final ActiveInventoryModel activeInventoryModel = mockActiveInventoryModel();

        final Inventory expectedInventory = toEntity(
                mockInventory("i00001")
                        .setEnabled(false),
                activeInventoryModel);

        //Set up
        when(inventoryRepository.findById(anyString())).thenReturn(of(expectedInventory));
        when(inventoryRepository.save(any())).thenReturn(expectedInventory);

        //Execution
        final var actualInventoryModel = service.activateInventory(activeInventoryModel);

        //Validation
        assertEquals(toActiveModel(expectedInventory), actualInventoryModel);
        assertTrue(actualInventoryModel.isEnabled());
        verify(inventoryRepository, times(1))
                .findById(any());
        verify(inventoryRepository, times(1))
                .save(any());
    }

    @Test
    void activateInventoryThrowsNPEOnNullModel() {
        //Execution
        assertThrows(NullPointerException.class,
                () -> service.activateInventory(null));
        //Validation
        verify(inventoryRepository, times(0))
                .findById(any());
        verify(inventoryRepository, times(0))
                .save(any());
    }

    @Test
    void activateInventoryThrowsBadRequestExceptionOnInvalidModel() {
        //Initialization
        final ActiveInventoryModel activateInventoryModel = new ActiveInventoryModel();

        //Execution
        assertThrows(BadRequestException.class,
                () -> service.activateInventory(activateInventoryModel));
        //Validation
        verify(inventoryRepository, times(0))
                .findById(any());
        verify(inventoryRepository, times(0))
                .save(any());
    }

    @Test
    void activateInventoryThrowsNonRetryableExceptionOnNotFoundInventory() {
        //Initialization
        final ActiveInventoryModel activateInventoryModel = mockActiveInventoryModel();

        //Set up
        when(inventoryRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        //Execution
        assertThrows(NonRetryableException.class,
                () -> service.activateInventory(activateInventoryModel));

        //Validation
        verify(inventoryRepository, times(1))
                .findById(anyString());
        verify(inventoryRepository, times(0))
                .save(any());
    }

    @Test
    void activateInventoryFailFindByIdThrowsRetryableException() {
        final var activeInventoryModel = new ActiveInventoryModel()
                .setEnabled(true)
                .setId("i00001")
                .setLastUser("testUser");

        when(inventoryRepository.findById(anyString())).thenThrow(new OptimisticLockException("Optimistic Lock Exception"));

        var ex = assertThrows(RetryableException.class, () -> service.activateInventory(activeInventoryModel));
        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(inventoryRepository, times(4)).findById(any());
        verify(inventoryRepository, times(0)).save(any());
    }

    @Test
    void activateInventoryFailFindByIdThrowsNonRetryableException() {
        final var activeInventoryModel = new ActiveInventoryModel()
                .setEnabled(true)
                .setId("i00001")
                .setLastUser("testUser");

        when(inventoryRepository.findById(anyString())).thenThrow(new RuntimeException("Runtime Exception"));

        var ex = assertThrows(NonRetryableException.class, () -> service.activateInventory(activeInventoryModel));
        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(inventoryRepository, times(1)).findById(any());
        verify(inventoryRepository, times(0)).save(any());
    }

    @Test
    void activateInventoryFailFindByIdThrowsNotFoundException() {
        final var activeInventoryModel = new ActiveInventoryModel()
                .setEnabled(true)
                .setId("i00001")
                .setLastUser("testUser");

        when(inventoryRepository.findById(anyString())).thenReturn(empty());

        var ex = assertThrows(NonRetryableException.class, () -> service.activateInventory(activeInventoryModel));
        assertEquals(NotFoundException.class, getRootCause(ex).getClass());
        verify(inventoryRepository, times(1)).findById(any());
        verify(inventoryRepository, times(0)).save(any());
    }

    @Test
    void activateInventoryFailSaveThrowsRetryableException() {
        final var activeInventoryModel = new ActiveInventoryModel()
                .setEnabled(true)
                .setId("i00001")
                .setLastUser("testUser");

        final Inventory expectedInventory = toEntity(
                mockInventory("i00001")
                        .setEnabled(false),
                activeInventoryModel);

        when(inventoryRepository.findById(anyString())).thenReturn(of(expectedInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenThrow(new OptimisticLockException("Optimistic Lock Exception"));

        var ex = assertThrows(RetryableException.class, () -> service.activateInventory(activeInventoryModel));
        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(inventoryRepository, times(4)).findById(any());
        verify(inventoryRepository, times(4)).save(any());
    }

    @Test
    void activateInventoryFailSaveThrowsNonRetryableException() {
        final var activeInventoryModel = new ActiveInventoryModel()
                .setEnabled(true)
                .setId("i00001")
                .setLastUser("testUser");

        final Inventory expectedInventory = toEntity(
                mockInventory("i00001")
                        .setEnabled(false),
                activeInventoryModel);

        when(inventoryRepository.findById(anyString())).thenReturn(of(expectedInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenThrow(new RuntimeException("Runtime Exception"));

        var ex = assertThrows(NonRetryableException.class, () -> service.activateInventory(activeInventoryModel));
        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(inventoryRepository, times(1)).findById(any());
        verify(inventoryRepository, times(1)).save(any());
    }

    Brand mockBrand() {
        return new Brand()
                .setBrandId("b00001")
                .setName("name")
                .setDescription("description")
                .setLastUser("testUser")
                .setEnabled(true);
    }

    private Inventory mockInventory(String inventoryId) {
        return new Inventory()
                .setInventoryId(inventoryId)
                .setProduct(mockProduct("p00001"))
                .setQuantity(1)
                .setWarehouse(mockWarehouse("w00001"))
                .setEnabled(true)
                .setLastUser("testUser");
    }

    private Product mockProduct(String productId) {
        return new Product()
                .setProductId(productId)
                .setBrand(mockBrand())
                .setUpc("123456789012")
                .setBrand(mockBrand())
                .setSanitaryRegistryNumber(null)
                .setLastUser("testUser")
                .setProviderSet(Set.of())
                .setEnabled(true);
    }

    private Warehouse mockWarehouse(String warehouseId) {
        return new Warehouse()
                .setWarehouseId("w00001")
                .setName("warehouse")
                .setDescription("Description")
                .setAddress1("address")
                .setTelephone1("12345678")
                .setNotes("notes")
                .setLastUser("testUser")
                .setEnabled(true);
    }

    BrandModel mockBrandModel() {
        return new BrandModel()
                .setId("b00001")
                .setName("name")
                .setDescription("description")
                .setLastUser("testUser")
                .setEnabled(true);
    }

    private InventoryModel mockInventoryModel(String inventoryId) {
        return new InventoryModel()
                .setId(inventoryId)
                .setProductModel(mockProductModel("s00001"))
                .setQuantity(1)
                .setWarehouseModel(mockWarehouseModel("w00001"))
                .setEnabled(true)
                .setLastUser("testUser");
    }

    private ProductModel mockProductModel(String productId) {
        return new ProductModel()
                .setId(productId)
                .setBrand(mockBrandModel())
                .setUpc("123456789012")
                .setSanitaryRegistryNumber(null)
                .setLastUser("testUser")
                .setProviderSet(Set.of())
                .setEnabled(true);
    }

    private WarehouseModel mockWarehouseModel(String warehouseId) {
        return new WarehouseModel()
                .setId("w00001")
                .setName("warehouse")
                .setDescription("Description")
                .setAddress1("address")
                .setTelephone1("12345678")
                .setNotes("notes")
                .setLastUser("testUser")
                .setEnabled(true);
    }

    private ActiveInventoryModel mockActiveInventoryModel() {
        return new ActiveInventoryModel()
                .setEnabled(true)
                .setId("i00001")
                .setLastUser("testUser");
    }

}
