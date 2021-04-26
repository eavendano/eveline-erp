package net.erp.eveline.service.warehouse;

import config.ServiceTestConfiguration;
import net.erp.eveline.common.TransactionService;
import net.erp.eveline.common.exception.BadRequestException;
import net.erp.eveline.common.exception.NonRetryableException;
import net.erp.eveline.common.exception.NotFoundException;
import net.erp.eveline.common.exception.RetryableException;
import net.erp.eveline.common.mapper.WarehouseMapper;
import net.erp.eveline.data.entity.Warehouse;
import net.erp.eveline.data.repository.WarehouseRepository;
import net.erp.eveline.model.ActiveWarehouseModel;
import net.erp.eveline.model.WarehouseModel;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static net.erp.eveline.common.mapper.WarehouseMapper.toActiveModel;
import static net.erp.eveline.common.mapper.WarehouseMapper.toModel;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anySet;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ServiceTestConfiguration.class})
public class WarehouseServiceImplTest {

    @Mock
    private WarehouseRepository warehouseRepository;


    @Autowired
    private TransactionService transactionService;

    @InjectMocks
    @Spy
    private final WarehouseServiceImpl service = new WarehouseServiceImpl();

    @BeforeEach
    void setUp() {
        service.setTransactionService(transactionService);
    }

    @Test
    void getWarehouseModelsSuccessful() {
        //Initialization
        final int expectedLength = 2;

        //Set up
        final var warehouseList = mockWarehouseList(expectedLength);
        when(warehouseRepository.findAll()).thenReturn(warehouseList);

        //Execution
        final Set<WarehouseModel> actualWarehouseModels = service.findAll();

        //Validation
        verify(warehouseRepository, times(1))
                .findAll();
        assertEquals(expectedLength, actualWarehouseModels.size());

        assertTrue(toModel(Set.copyOf(mockWarehouseList(expectedLength))).containsAll(actualWarehouseModels));
    }

    @Test
    void getWarehouseModelsSuccessfulWithEmptyList() {
        //Initialization
        final int expectedLength = 0;

        //Set up
        when(warehouseRepository.findAll()).thenReturn(mockWarehouseList(expectedLength));

        //Execution
        final Set<WarehouseModel> actualWarehouseModels = service.findAll();

        //Validation
        verify(warehouseRepository, times(1)).findAll();
        assertEquals(expectedLength, actualWarehouseModels.size());
        assertEquals(WarehouseMapper.toModel(Set.copyOf(mockWarehouseList(expectedLength))), actualWarehouseModels);
    }

    @Test
    void getWarehouseModelsThrowsServiceExceptionAfterRetries() {
        //Set up
        when(warehouseRepository.findAll()).thenThrow(new OptimisticLockException("Optimistic lock test"));

        //Execution
        assertThrows(RetryableException.class, service::findAll);

        //Validation
        verify(warehouseRepository, times(4))
                .findAll();
    }

    @Test
    void getWarehouseModelSuccessful() {
        final String warehouseId = "w00001";
        final Warehouse warehouse = mockWarehouseList(1).get(0);
        when(warehouseRepository.findById(warehouseId)).thenReturn(ofNullable(warehouse));

        final WarehouseModel resultWarehouseModel = service.getWarehouseModel(warehouseId);
        assertEquals(toModel(warehouse), resultWarehouseModel);
        verify(warehouseRepository, times(1)).findById(any(String.class));
    }

    @Test
    void getWarehouseModelNotFoundEntityThrowsNotFoundException() {
        final String warehouseId = "w00001";
        when(warehouseRepository.findById(warehouseId)).thenReturn(empty());

        var ex = assertThrows(NonRetryableException.class, () -> service.getWarehouseModel(warehouseId));
        assertEquals(NotFoundException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(1)).findById(any(String.class));
    }

    @Test
    void getWarehouseModelWithNullIdThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> service.getWarehouseModel(null));
        verify(warehouseRepository, times(0)).findById(any(String.class));
    }

    @Test
    void getWarehouseModelWithEmptyIdThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> service.getWarehouseModel(""));
        verify(warehouseRepository, times(0)).findById(any(String.class));
    }

    @Test
    void getWarehouseModelWithSmallIdThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> service.getWarehouseModel("w0001"));
        verify(warehouseRepository, times(0)).findById(any(String.class));
    }

    @Test
    void getWarehouseModelWithLargeIdThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> service.getWarehouseModel("w000001"));
        verify(warehouseRepository, times(0)).findById(any(String.class));
    }

    @Test
    void upsertWarehouseModelWithNullModelThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> service.upsertWarehouseModel(null));
        verify(warehouseRepository, times(0)).save(any(Warehouse.class));
        verify(warehouseRepository, times(0)).findById(any(String.class));
    }

    @Test
    void upsertWarehouseModelForInsertSuccessfulRequest() {
        final var warehouseModel = new WarehouseModel()
                .setName("warehouse")
                .setLastUser("user")
                .setTelephone1("12345678")
                .setTelephone2("87654321")
                .setAddress1("Address1")
                .setAddress2("Address2")
                .setDescription("warehouse description")
                .setNotes("notes")
                .setEnabled(true);

        final Warehouse warehouse = mockWarehouseList(1).get(0);

        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        var resultModel = service.upsertWarehouseModel(warehouseModel);
        assertEquals(WarehouseMapper.toModel(warehouse), resultModel);
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
        verify(warehouseRepository, times(0)).findById(any(String.class));
    }

    @Test
    void upsertWarehouseModelForInsertWithInvalidModelThrowsBadRequestException() {
        final var warehouseModel = new WarehouseModel()
                .setId("w00001")
                .setName("")
                .setLastUser("")
                .setTelephone1("")
                .setTelephone2("")
                .setAddress1("")
                .setAddress2("")
                .setDescription("")
                .setNotes("")
                .setEnabled(true);

        assertThrows(BadRequestException.class, () -> service.upsertWarehouseModel(warehouseModel));
        verify(warehouseRepository, times(0)).save(any(Warehouse.class));
        verify(warehouseRepository, times(0)).findById(any(String.class));
    }

    @Test
    void upsertWarehouseModelForInsertSaveFailsThrowsRetryableException() {
        final var warehouseModel = new WarehouseModel()
                .setName("warehouse")
                .setLastUser("user")
                .setTelephone1("12345678")
                .setTelephone2("87654321")
                .setAddress1("Address1")
                .setAddress2("Address2")
                .setDescription("warehouse description")
                .setNotes("notes")
                .setEnabled(true);

        when(warehouseRepository.save(any(Warehouse.class))).thenThrow(new OptimisticLockException("Optimistic lock test"));

        var ex = assertThrows(RetryableException.class, () -> service.upsertWarehouseModel(warehouseModel));
        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(4)).save(any(Warehouse.class));
        verify(warehouseRepository, times(0)).findById(any(String.class));
    }


    @Test
    void upsertWarehouseModelForUpdateSaveFailsThrowsNonRetryableException() {
        final var warehouseModel = new WarehouseModel()
                .setId("w00001")
                .setName("warehouse")
                .setLastUser("user")
                .setTelephone1("12345678")
                .setTelephone2("87654321")
                .setAddress1("Address1")
                .setAddress2("Address2")
                .setDescription("warehouse description")
                .setNotes("notes")
                .setEnabled(true);
        when(warehouseRepository.existsById("w00001")).thenReturn(true);
        when(warehouseRepository.save(any(Warehouse.class))).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.upsertWarehouseModel(warehouseModel));
        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
        verify(warehouseRepository, times(1)).existsById(any(String.class));
    }

    @Test
    void upsertWarehouseModelForUpdateFindByIdFailsThrowsRetryableException() {
        final var warehouseModel = new WarehouseModel()
                .setId("w00001")
                .setName("warehouse")
                .setLastUser("user")
                .setTelephone1("12345678")
                .setTelephone2("87654321")
                .setAddress1("Address1")
                .setAddress2("Address2")
                .setDescription("warehouse description")
                .setNotes("notes")
                .setEnabled(true);

        when(warehouseRepository.existsById("w00001")).thenThrow(new OptimisticLockException("Optimistic lock test"));

        var ex = assertThrows(RetryableException.class, () -> service.upsertWarehouseModel(warehouseModel));
        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(0)).save(any(Warehouse.class));
        verify(warehouseRepository, times(4)).existsById(any(String.class));
    }

    @Test
    void upsertWarehouseModelForUpdateFindByIdFailsThrowsNonRetryableException() {
        final var warehouseModel = new WarehouseModel()
                .setId("w00001")
                .setName("warehouse")
                .setLastUser("user")
                .setTelephone1("12345678")
                .setTelephone2("87654321")
                .setAddress1("Address1")
                .setAddress2("Address2")
                .setDescription("warehouse description")
                .setNotes("notes")
                .setEnabled(true);
        when(warehouseRepository.existsById("w00001")).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.upsertWarehouseModel(warehouseModel));
        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(0)).save(any(Warehouse.class));
        verify(warehouseRepository, times(1)).existsById(any(String.class));
    }

    @Test
    void activateWarehouseSuccessfulRequest() {
        var activeWarehouseModel = new ActiveWarehouseModel()
                .setId("w00001")
                .setLastUser("user")
                .setEnabled(true);

        final var mockWarehouse = mockWarehouseList(1).get(0);

        final var resultMock = mockWarehouseList(1).get(0);
        resultMock.setLastUser("user");
        resultMock.setEnabled(true);

        when(warehouseRepository.findById("w00001")).thenReturn(of(mockWarehouse));
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(resultMock);

        var result = service.activateWarehouse(activeWarehouseModel);

        assertEquals(toActiveModel(resultMock), result);
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
        verify(warehouseRepository, times(1)).findById(any(String.class));
    }

    @Test
    void activateWarehouseNullModelThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> service.activateWarehouse(null));
        verify(warehouseRepository, times(0)).save(any(Warehouse.class));
        verify(warehouseRepository, times(0)).findById(any(String.class));
    }

    @Test
    void activateWarehouseInvalidModelModelThrowsBadRequest() {
        var activeWarehouseModel = new ActiveWarehouseModel()
                .setId("")
                .setLastUser("")
                .setEnabled(null);

        var ex = assertThrows(BadRequestException.class, () -> service.activateWarehouse(activeWarehouseModel));

        assertEquals(BadRequestException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(0)).save(any(Warehouse.class));
        verify(warehouseRepository, times(0)).findById(any(String.class));
    }

    @Test
    void activateWarehouseFindByIdFailsThrowsRetryableException() {
        var activeWarehouseModel = new ActiveWarehouseModel()
                .setId("w00001")
                .setLastUser("user")
                .setEnabled(true);

        when(warehouseRepository.findById("w00001")).thenThrow(new OptimisticLockException("Optimistic lock exception"));

        var ex = assertThrows(RetryableException.class, () -> service.activateWarehouse(activeWarehouseModel));

        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(0)).save(any(Warehouse.class));
        verify(warehouseRepository, times(4)).findById(any(String.class));
    }

    @Test
    void activateWarehouseFindByIdFailsThrowsNonRetryableException() {
        var activeWarehouseModel = new ActiveWarehouseModel()
                .setId("w00001")
                .setLastUser("user")
                .setEnabled(true);

        when(warehouseRepository.findById("w00001")).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.activateWarehouse(activeWarehouseModel));

        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(0)).save(any(Warehouse.class));
        verify(warehouseRepository, times(1)).findById(any(String.class));
    }

    @Test
    void activateWarehouseFindByIdFailsThrowsNotFoundException() {
        var activeWarehouseModel = new ActiveWarehouseModel()
                .setId("w00001")
                .setLastUser("user")
                .setEnabled(true);

        when(warehouseRepository.findById("w00001")).thenReturn(empty());

        var ex = assertThrows(NonRetryableException.class, () -> service.activateWarehouse(activeWarehouseModel));

        assertEquals(NotFoundException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(0)).save(any(Warehouse.class));
        verify(warehouseRepository, times(1)).findById(any(String.class));
    }

    @Test
    void activateWarehouseSaveFailsThrowsRetryableException() {
        var activeWarehouseModel = new ActiveWarehouseModel()
                .setId("w00001")
                .setLastUser("user")
                .setEnabled(true);

        final var mockWarehouse = mockWarehouseList(1).get(0);

        when(warehouseRepository.findById("w00001")).thenReturn(of(mockWarehouse));
        when(warehouseRepository.save(any(Warehouse.class))).thenThrow(new OptimisticLockException("Optimistic lock exception"));

        var ex = assertThrows(RetryableException.class, () -> service.activateWarehouse(activeWarehouseModel));

        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(4)).save(any(Warehouse.class));
        verify(warehouseRepository, times(4)).findById(any(String.class));
    }

    @Test
    void activateWarehouseSaveFailsThrowsNonRetryableException() {
        var activeWarehouseModel = new ActiveWarehouseModel()
                .setId("w00001")
                .setLastUser("user")
                .setEnabled(true);

        final var mockWarehouse = mockWarehouseList(1).get(0);

        when(warehouseRepository.findById("w00001")).thenReturn(of(mockWarehouse));

        when(warehouseRepository.save(any(Warehouse.class))).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.activateWarehouse(activeWarehouseModel));

        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
        verify(warehouseRepository, times(1)).findById(any(String.class));
    }

    @Test
    void activateWarehouseSetSuccessfulRequest() {
        var activeWarehouseModel1 = new ActiveWarehouseModel()
                .setId("w00000")
                .setLastUser("user")
                .setEnabled(true);

        var activeWarehouseModel2 = new ActiveWarehouseModel()
                .setId("w00001")
                .setLastUser("user")
                .setEnabled(false);

        final var warehouseList = mockWarehouseList(2);
        final var mockWarehouse1 = warehouseList.get(0);
        final var mockWarehouse2 = warehouseList.get(1);

        final var resultWarehouseList = mockWarehouseList(2);
        final var resultMock1 = resultWarehouseList.get(0);
        resultMock1.setLastUser("user");
        resultMock1.setEnabled(true);

        final var resultMock2 = resultWarehouseList.get(1);
        resultMock1.setLastUser("user");
        resultMock1.setEnabled(true);

        when(warehouseRepository.existsAllByWarehouseIdIn(anySet())).thenReturn(true);
        when(warehouseRepository.findAllById(anySet())).thenReturn(List.of(mockWarehouse1, mockWarehouse2));
        when(warehouseRepository.saveAll(anySet())).thenReturn(List.of(resultMock1, resultMock2));

        var result = service.activateWarehouseSet(Set.of(activeWarehouseModel1, activeWarehouseModel2));

        assertIterableEquals(toActiveModel(Set.of(resultMock1, resultMock2)), result);
        verify(warehouseRepository, times(1)).existsAllByWarehouseIdIn(anySet());
        verify(warehouseRepository, times(1)).findAllById(anySet());
        verify(warehouseRepository, times(1)).saveAll(anySet());
    }

    @Test
    void activateWarehouseSetWithEmptySetReturnsEmptySet() {
        var result = service.activateWarehouseSet(emptySet());

        assertIterableEquals(emptySet(), result);
        verify(warehouseRepository, times(0)).existsAllByWarehouseIdIn(anySet());
        verify(warehouseRepository, times(0)).findAllById(anySet());
        verify(warehouseRepository, times(0)).saveAll(anySet());
    }

    @Test
    void activateWarehouseSetNullModelThrowsNullPointerException() {
        var ex = assertThrows(NullPointerException.class, () -> service.activateWarehouseSet(null));
        verify(warehouseRepository, times(0)).existsAllByWarehouseIdIn(anySet());
        verify(warehouseRepository, times(0)).findAllById(anySet());
        verify(warehouseRepository, times(0)).saveAll(anySet());
    }

    @Test
    void activateWarehouseSetThrowsBadRequestException() {
        var activeWarehouseModel = new ActiveWarehouseModel()
                .setId("")
                .setLastUser("")
                .setEnabled(null);

        var ex = assertThrows(BadRequestException.class, () -> service.activateWarehouseSet(Set.of(activeWarehouseModel)));

        assertEquals(BadRequestException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(0)).existsAllByWarehouseIdIn(anySet());
        verify(warehouseRepository, times(0)).findAllById(anySet());
        verify(warehouseRepository, times(0)).saveAll(anySet());
    }

    @Test
    void activateWarehouseSetExistByIdFailsThrowsRetryableException() {
        var activeWarehouseModel = new ActiveWarehouseModel()
                .setId("w00001")
                .setLastUser("user")
                .setEnabled(true);

        when(warehouseRepository.existsAllByWarehouseIdIn(Set.of("w00001"))).thenThrow(new OptimisticLockException("Optimistic lock exception"));

        var ex = assertThrows(RetryableException.class, () -> service.activateWarehouseSet(Set.of(activeWarehouseModel)));

        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(4)).existsAllByWarehouseIdIn(anySet());
        verify(warehouseRepository, times(0)).findAllById(anySet());
        verify(warehouseRepository, times(0)).saveAll(anySet());
    }

    @Test
    void activateWarehouseExistsByIdFailsThrowsNonRetryableException() {
        var activeWarehouseModel = new ActiveWarehouseModel()
                .setId("w00001")
                .setLastUser("user")
                .setEnabled(true);

        when(warehouseRepository.existsAllByWarehouseIdIn(Set.of("w00001"))).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.activateWarehouseSet(Set.of(activeWarehouseModel)));

        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(1)).existsAllByWarehouseIdIn(anySet());
        verify(warehouseRepository, times(0)).findAllById(anySet());
        verify(warehouseRepository, times(0)).saveAll(anySet());
    }

    @Test
    void activateWarehouseExistsByIdFailsThrowsNotFoundException() {
        var activeWarehouseModel = new ActiveWarehouseModel()
                .setId("w00001")
                .setLastUser("user")
                .setEnabled(true);

        when(warehouseRepository.existsAllByWarehouseIdIn(Set.of("w00001"))).thenReturn(false);

        var ex = assertThrows(NonRetryableException.class, () -> service.activateWarehouseSet(Set.of(activeWarehouseModel)));

        assertEquals(NotFoundException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(1)).existsAllByWarehouseIdIn(anySet());
        verify(warehouseRepository, times(0)).findAllById(anySet());
        verify(warehouseRepository, times(0)).saveAll(anySet());
    }

    @Test
    void activateWarehouseSetFindAllByIdFailsThrowsRetryableException() {
        var activeWarehouseModel = new ActiveWarehouseModel()
                .setId("w00001")
                .setLastUser("user")
                .setEnabled(true);

        when(warehouseRepository.existsAllByWarehouseIdIn(anySet())).thenReturn(true);
        when(warehouseRepository.findAllById(anySet())).thenThrow(new OptimisticLockException("Optimistic lock exception"));

        var ex = assertThrows(RetryableException.class, () -> service.activateWarehouseSet(Set.of(activeWarehouseModel)));

        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(4)).existsAllByWarehouseIdIn(anySet());
        verify(warehouseRepository, times(4)).findAllById(anySet());
        verify(warehouseRepository, times(0)).saveAll(anySet());
    }

    @Test
    void activateWarehouseFindAllByIdFailsThrowsNonRetryableException() {
        var activeWarehouseModel = new ActiveWarehouseModel()
                .setId("w00001")
                .setLastUser("user")
                .setEnabled(true);

        when(warehouseRepository.existsAllByWarehouseIdIn(anySet())).thenReturn(true);
        when(warehouseRepository.findAllById(anySet())).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.activateWarehouseSet(Set.of(activeWarehouseModel)));

        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(1)).existsAllByWarehouseIdIn(anySet());
        verify(warehouseRepository, times(1)).findAllById(anySet());
        verify(warehouseRepository, times(0)).saveAll(anySet());
    }

    @Test
    void activateWarehouseFindAllByIdFailsThrowsNotFoundException() {
        var activeWarehouseModel = new ActiveWarehouseModel()
                .setId("w00001")
                .setLastUser("user")
                .setEnabled(true);

        when(warehouseRepository.existsAllByWarehouseIdIn(Set.of("w00001"))).thenReturn(true);
        when(warehouseRepository.findAllById(anySet())).thenReturn(emptyList());

        var ex = assertThrows(NonRetryableException.class, () -> service.activateWarehouseSet(Set.of(activeWarehouseModel)));

        assertEquals(NotFoundException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(1)).existsAllByWarehouseIdIn(anySet());
        verify(warehouseRepository, times(1)).findAllById(anySet());
        verify(warehouseRepository, times(0)).saveAll(anySet());
    }

    @Test
    void activateWarehouseSaveAllFailsThrowsRetryableException() {
        var activeWarehouseModel = new ActiveWarehouseModel()
                .setId("w00000")
                .setLastUser("user")
                .setEnabled(true);

        final var mockWarehouse = mockWarehouseList(1).get(0);

        when(warehouseRepository.existsAllByWarehouseIdIn(anySet())).thenReturn(true);
        when(warehouseRepository.findAllById(anySet())).thenReturn(List.of(mockWarehouse));
        when(warehouseRepository.saveAll(anySet())).thenThrow(new OptimisticLockException("Optimistic lock exception"));

        var ex = assertThrows(RetryableException.class, () -> service.activateWarehouseSet(Set.of(activeWarehouseModel)));

        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(4)).existsAllByWarehouseIdIn(anySet());
        verify(warehouseRepository, times(4)).findAllById(anySet());
        verify(warehouseRepository, times(4)).saveAll(anySet());
    }

    @Test
    void activateWarehouseSaveAllFailsThrowsNonRetryableException() {
        var activeWarehouseModel = new ActiveWarehouseModel()
                .setId("w00000")
                .setLastUser("user")
                .setEnabled(true);

        final var mockWarehouse = mockWarehouseList(1).get(0);

        when(warehouseRepository.existsAllByWarehouseIdIn(anySet())).thenReturn(true);
        when(warehouseRepository.findAllById(anySet())).thenReturn(List.of(mockWarehouse));
        when(warehouseRepository.saveAll(anySet())).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.activateWarehouseSet(Set.of(activeWarehouseModel)));

        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(warehouseRepository, times(1)).existsAllByWarehouseIdIn(anySet());
        verify(warehouseRepository, times(1)).findAllById(anySet());
        verify(warehouseRepository, times(1)).saveAll(anySet());
    }

    private List<Warehouse> mockWarehouseList(int length) {
        return IntStream.rangeClosed(0, length - 1)
                .mapToObj(this::mockWarehouse)
                .collect(Collectors.toList());
    }

    private Warehouse mockWarehouse(int idx) {
        return new Warehouse()
                .setWarehouseId("w0000" + idx)
                .setName("warehouse")
                .setLastUser("user")
                .setTelephone1("12345678")
                .setTelephone2("87654321")
                .setAddress1("Address1")
                .setAddress2("Address2")
                .setDescription("warehouse description")
                .setNotes("notes")
                .setEnabled(true);
    }
}
