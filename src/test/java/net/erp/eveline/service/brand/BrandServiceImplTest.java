package net.erp.eveline.service.brand;

import config.ServiceTestConfiguration;
import net.erp.eveline.common.TransactionService;
import net.erp.eveline.common.exception.BadRequestException;
import net.erp.eveline.common.exception.NonRetryableException;
import net.erp.eveline.common.exception.NotFoundException;
import net.erp.eveline.common.exception.RetryableException;
import net.erp.eveline.data.entity.Brand;
import net.erp.eveline.data.repository.BrandRepository;
import net.erp.eveline.model.ActiveBrandModel;
import net.erp.eveline.model.BrandModel;
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
import static java.util.Optional.*;
import static java.util.Optional.of;
import static net.erp.eveline.common.mapper.BrandMapper.toActiveModel;
import static net.erp.eveline.common.mapper.BrandMapper.toModel;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ServiceTestConfiguration.class})
public class BrandServiceImplTest {

    @Mock
    private BrandRepository brandRepository;


    @Autowired
    private TransactionService transactionService;

    @InjectMocks
    @Spy
    private final BrandServiceImpl service = new BrandServiceImpl();

    @BeforeEach
    void setUp() {
        service.setTransactionService(transactionService);
    }

    @Test
    void getBrandModelsSuccessful() {
        //Initialization
        final int expectedLength = 1;

        //Set up
        final var brandList = mockBrandList(expectedLength);
        when(brandRepository.findAll()).thenReturn(brandList);

        //Execution
        final Set<BrandModel> actualBrandModels = service.findAll();

        //Validation
        verify(brandRepository, times(1))
                .findAll();
        assertEquals(expectedLength, actualBrandModels.size());

        assertTrue(toModel(Set.copyOf(mockBrandList(expectedLength))).containsAll(actualBrandModels));
    }

    @Test
    void getBrandModelsSuccessfulWithEmptyList() {
        //Initialization
        final int expectedLength = 0;

        //Set up
        when(brandRepository.findAll()).thenReturn(mockBrandList(expectedLength));

        //Execution
        final Set<BrandModel> actualBrandModels = service.findAll();

        //Validation
        verify(brandRepository, times(1)).findAll();
        assertEquals(expectedLength, actualBrandModels.size());
        assertEquals(toModel(Set.copyOf(mockBrandList(expectedLength))), actualBrandModels);
    }

    @Test
    void getBrandModelsThrowsServiceExceptionAfterRetries() {
        //Set up
        when(brandRepository.findAll()).thenThrow(new OptimisticLockException("Optimistic lock test"));

        //Execution
        assertThrows(RetryableException.class, service::findAll);

        //Validation
        verify(brandRepository, times(4))
                .findAll();
    }

    @Test
    void getBrandModelSuccessful() {
        final String brandId = "b00001";
        final Brand brand = mockBrandList(1).get(0);
        when(brandRepository.findById(brandId)).thenReturn(ofNullable(brand));

        final BrandModel resultBrandModel = service.getBrandModel(brandId);
        assertEquals(toModel(brand), resultBrandModel);
        verify(brandRepository, times(1)).findById(any(String.class));
    }

    @Test
    void getBrandModelNotFoundEntityThrowsNotFoundException() {
        final String brandId = "b00001";
        when(brandRepository.findById(brandId)).thenReturn(empty());

        var ex = assertThrows(NonRetryableException.class, () -> service.getBrandModel(brandId));
        assertEquals(NotFoundException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(1)).findById(any(String.class));
    }

    @Test
    void getBrandModelWithNullIdThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> service.getBrandModel(null));
        verify(brandRepository, times(0)).findById(any(String.class));
    }

    @Test
    void getBrandModelWithEmptyIdThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> service.getBrandModel(""));
        verify(brandRepository, times(0)).findById(any(String.class));
    }

    @Test
    void getBrandModelWithSmallIdThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> service.getBrandModel("b0001"));
        verify(brandRepository, times(0)).findById(any(String.class));
    }

    @Test
    void getBrandModelWithLargeIdThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> service.getBrandModel("b000001"));
        verify(brandRepository, times(0)).findById(any(String.class));
    }

    @Test
    void upsertBrandModelWithNullModelThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> service.upsertBrandModel(null));
        verify(brandRepository, times(0)).save(any(Brand.class));
        verify(brandRepository, times(0)).findById(any(String.class));
    }

    @Test
    void upsertBrandModelForInsertSuccessfulRequest() {
        final var brandModel = new BrandModel()
                .setName("brand")
                .setLastUser("user")
                .setDescription("brand description")
                .setEnabled(true);

        final Brand brand = mockBrandList(1).get(0);

        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        var resultModel = service.upsertBrandModel(brandModel);
        assertEquals(toModel(brand), resultModel);
        verify(brandRepository, times(1)).save(any(Brand.class));
        verify(brandRepository, times(0)).findById(any(String.class));
    }

    @Test
    void upsertBrandModelForInsertWithInvalidModelThrowsBadRequestException() {
        final var brandModel = new BrandModel()
                .setId("w00001")
                .setName("")
                .setLastUser("")
                .setDescription("")
                .setEnabled(true);

        assertThrows(BadRequestException.class, () -> service.upsertBrandModel(brandModel));
        verify(brandRepository, times(0)).save(any(Brand.class));
        verify(brandRepository, times(0)).findById(any(String.class));
    }

    @Test
    void upsertBrandModelForInsertSaveFailsThrowsRetryableException() {
        final var brandModel = new BrandModel()
                .setName("brand")
                .setLastUser("user")
                .setDescription("brand description")
                .setEnabled(true);

        when(brandRepository.save(any(Brand.class))).thenThrow(new OptimisticLockException("Optimistic lock test"));

        var ex = assertThrows(RetryableException.class, () -> service.upsertBrandModel(brandModel));
        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(4)).save(any(Brand.class));
        verify(brandRepository, times(0)).findById(any(String.class));
    }


    @Test
    void upsertBrandModelForUpdateSaveFailsThrowsNonRetryableException() {
        final var brandModel = new BrandModel()
                .setId("b00001")
                .setName("brand")
                .setLastUser("user")
                .setDescription("brand description")
                .setEnabled(true);
        when(brandRepository.existsById("b00001")).thenReturn(true);
        when(brandRepository.save(any(Brand.class))).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.upsertBrandModel(brandModel));
        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(1)).save(any(Brand.class));
        verify(brandRepository, times(1)).existsById(any(String.class));
    }

    @Test
    void upsertBrandModelForUpdateFindByIdFailsThrowsRetryableException() {
        final var brandModel = new BrandModel()
                .setId("b00001")
                .setName("brand")
                .setLastUser("user")
                .setDescription("brand description")
                .setEnabled(true);

        when(brandRepository.existsById("b00001")).thenThrow(new OptimisticLockException("Optimistic lock test"));

        var ex = assertThrows(RetryableException.class, () -> service.upsertBrandModel(brandModel));
        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(0)).save(any(Brand.class));
        verify(brandRepository, times(4)).existsById(any(String.class));
    }

    @Test
    void upsertBrandModelForUpdateFindByIdFailsThrowsNonRetryableException() {
        final var brandModel = new BrandModel()
                .setId("b00001")
                .setName("brand")
                .setLastUser("user")
                .setDescription("brand description")
                .setEnabled(true);
        when(brandRepository.existsById("b00001")).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.upsertBrandModel(brandModel));
        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(0)).save(any(Brand.class));
        verify(brandRepository, times(1)).existsById(any(String.class));
    }

    @Test
    void activateBrandSuccessfulRequest() {
        var activeBrandModel = new ActiveBrandModel()
                .setId("b00001")
                .setLastUser("user")
                .setEnabled(true);

        final var mockBrand = mockBrandList(1).get(0);

        final var resultMock = mockBrandList(1).get(0);
        resultMock.setLastUser("user");
        resultMock.setEnabled(true);

        when(brandRepository.findById("b00001")).thenReturn(of(mockBrand));
        when(brandRepository.save(any(Brand.class))).thenReturn(resultMock);

        var result = service.activateBrand(activeBrandModel);

        assertEquals(toActiveModel(resultMock), result);
        verify(brandRepository, times(1)).save(any(Brand.class));
        verify(brandRepository, times(1)).findById(any(String.class));
    }

    @Test
    void activateBrandNullModelThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> service.activateBrand(null));
        verify(brandRepository, times(0)).save(any(Brand.class));
        verify(brandRepository, times(0)).findById(any(String.class));
    }

    @Test
    void activateBrandInvalidModelModelThrowsBadRequest() {
        var activeBrandModel = new ActiveBrandModel()
                .setId("")
                .setLastUser("")
                .setEnabled(null);

        var ex = assertThrows(BadRequestException.class, () -> service.activateBrand(activeBrandModel));

        assertEquals(BadRequestException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(0)).save(any(Brand.class));
        verify(brandRepository, times(0)).findById(any(String.class));
    }

    @Test
    void activateBrandFindByIdFailsThrowsRetryableException() {
        var activeBrandModel = new ActiveBrandModel()
                .setId("b00001")
                .setLastUser("user")
                .setEnabled(true);

        when(brandRepository.findById("b00001")).thenThrow(new OptimisticLockException("Optimistic lock exception"));

        var ex = assertThrows(RetryableException.class, () -> service.activateBrand(activeBrandModel));

        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(0)).save(any(Brand.class));
        verify(brandRepository, times(4)).findById(any(String.class));
    }

    @Test
    void activateBrandFindByIdFailsThrowsNonRetryableException() {
        var activeBrandModel = new ActiveBrandModel()
                .setId("b00001")
                .setLastUser("user")
                .setEnabled(true);

        when(brandRepository.findById("b00001")).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.activateBrand(activeBrandModel));

        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(0)).save(any(Brand.class));
        verify(brandRepository, times(1)).findById(any(String.class));
    }

    @Test
    void activateBrandFindByIdFailsThrowsNotFoundException() {
        var activeBrandModel = new ActiveBrandModel()
                .setId("b00001")
                .setLastUser("user")
                .setEnabled(true);

        when(brandRepository.findById("b00001")).thenReturn(empty());

        var ex = assertThrows(NonRetryableException.class, () -> service.activateBrand(activeBrandModel));

        assertEquals(NotFoundException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(0)).save(any(Brand.class));
        verify(brandRepository, times(1)).findById(any(String.class));
    }

    @Test
    void activateBrandSaveFailsThrowsRetryableException() {
        var activeBrandModel = new ActiveBrandModel()
                .setId("b00001")
                .setLastUser("user")
                .setEnabled(true);

        final var mockBrand = mockBrandList(1).get(0);

        when(brandRepository.findById("b00001")).thenReturn(of(mockBrand));
        when(brandRepository.save(any(Brand.class))).thenThrow(new OptimisticLockException("Optimistic lock exception"));

        var ex = assertThrows(RetryableException.class, () -> service.activateBrand(activeBrandModel));

        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(4)).save(any(Brand.class));
        verify(brandRepository, times(4)).findById(any(String.class));
    }

    @Test
    void activateBrandSaveFailsThrowsNonRetryableException() {
        var activeBrandModel = new ActiveBrandModel()
                .setId("b00001")
                .setLastUser("user")
                .setEnabled(true);

        final var mockBrand = mockBrandList(1).get(0);

        when(brandRepository.findById("b00001")).thenReturn(of(mockBrand));

        when(brandRepository.save(any(Brand.class))).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.activateBrand(activeBrandModel));

        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(1)).save(any(Brand.class));
        verify(brandRepository, times(1)).findById(any(String.class));
    }

    @Test
    void activateBrandSetSuccessfulRequest() {
        var activeBrandModel1 = new ActiveBrandModel()
                .setId("b00000")
                .setLastUser("user")
                .setEnabled(true);

        var activeBrandModel2 = new ActiveBrandModel()
                .setId("b00001")
                .setLastUser("user")
                .setEnabled(false);

        final var brandList = mockBrandList(2);
        final var mockBrand1 = brandList.get(0);
        final var mockBrand2 = brandList.get(1);

        final var resultBrandList = mockBrandList(2);
        final var resultMock1 = resultBrandList.get(0);
        resultMock1.setLastUser("user");
        resultMock1.setEnabled(true);

        final var resultMock2 = resultBrandList.get(1);
        resultMock1.setLastUser("user");
        resultMock1.setEnabled(true);

        when(brandRepository.existsAllByBrandIdIn(anySet())).thenReturn(true);
        when(brandRepository.findAllById(anySet())).thenReturn(List.of(mockBrand1, mockBrand2));
        when(brandRepository.saveAll(anySet())).thenReturn(List.of(resultMock1, resultMock2));

        var result = service.activateBrandSet(Set.of(activeBrandModel1, activeBrandModel2));

        assertIterableEquals(toActiveModel(Set.of(resultMock1, resultMock2)), result);
        verify(brandRepository, times(1)).existsAllByBrandIdIn(anySet());
        verify(brandRepository, times(1)).findAllById(anySet());
        verify(brandRepository, times(1)).saveAll(anySet());
    }

    @Test
    void activateBrandSetWithEmptySetReturnsEmptySet() {
        var result = service.activateBrandSet(emptySet());

        assertIterableEquals(emptySet(), result);
        verify(brandRepository, times(0)).existsAllByBrandIdIn(anySet());
        verify(brandRepository, times(0)).findAllById(anySet());
        verify(brandRepository, times(0)).saveAll(anySet());
    }

    @Test
    void activateBrandSetNullModelThrowsNullPointerException() {
        var ex = assertThrows(NullPointerException.class, () -> service.activateBrandSet(null));
        verify(brandRepository, times(0)).existsAllByBrandIdIn(anySet());
        verify(brandRepository, times(0)).findAllById(anySet());
        verify(brandRepository, times(0)).saveAll(anySet());
    }

    @Test
    void activateBrandSetThrowsBadRequestException() {
        var activeBrandModel = new ActiveBrandModel()
                .setId("")
                .setLastUser("")
                .setEnabled(null);

        var ex = assertThrows(BadRequestException.class, () -> service.activateBrandSet(Set.of(activeBrandModel)));

        assertEquals(BadRequestException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(0)).existsAllByBrandIdIn(anySet());
        verify(brandRepository, times(0)).findAllById(anySet());
        verify(brandRepository, times(0)).saveAll(anySet());
    }

    @Test
    void activateBrandSetExistByIdFailsThrowsRetryableException() {
        var activeBrandModel = new ActiveBrandModel()
                .setId("b00001")
                .setLastUser("user")
                .setEnabled(true);

        when(brandRepository.existsAllByBrandIdIn(Set.of("b00001"))).thenThrow(new OptimisticLockException("Optimistic lock exception"));

        var ex = assertThrows(RetryableException.class, () -> service.activateBrandSet(Set.of(activeBrandModel)));

        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(4)).existsAllByBrandIdIn(anySet());
        verify(brandRepository, times(0)).findAllById(anySet());
        verify(brandRepository, times(0)).saveAll(anySet());
    }

    @Test
    void activateBrandExistsByIdFailsThrowsNonRetryableException() {
        var activeBrandModel = new ActiveBrandModel()
                .setId("b00001")
                .setLastUser("user")
                .setEnabled(true);

        when(brandRepository.existsAllByBrandIdIn(Set.of("b00001"))).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.activateBrandSet(Set.of(activeBrandModel)));

        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(1)).existsAllByBrandIdIn(anySet());
        verify(brandRepository, times(0)).findAllById(anySet());
        verify(brandRepository, times(0)).saveAll(anySet());
    }

    @Test
    void activateBrandExistsByIdFailsThrowsNotFoundException() {
        var activeBrandModel = new ActiveBrandModel()
                .setId("b00001")
                .setLastUser("user")
                .setEnabled(true);

        when(brandRepository.existsAllByBrandIdIn(Set.of("b00001"))).thenReturn(false);

        var ex = assertThrows(NonRetryableException.class, () -> service.activateBrandSet(Set.of(activeBrandModel)));

        assertEquals(NotFoundException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(1)).existsAllByBrandIdIn(anySet());
        verify(brandRepository, times(0)).findAllById(anySet());
        verify(brandRepository, times(0)).saveAll(anySet());
    }

    @Test
    void activateBrandSetFindAllByIdFailsThrowsRetryableException() {
        var activeBrandModel = new ActiveBrandModel()
                .setId("b00001")
                .setLastUser("user")
                .setEnabled(true);

        when(brandRepository.existsAllByBrandIdIn(anySet())).thenReturn(true);
        when(brandRepository.findAllById(anySet())).thenThrow(new OptimisticLockException("Optimistic lock exception"));

        var ex = assertThrows(RetryableException.class, () -> service.activateBrandSet(Set.of(activeBrandModel)));

        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(4)).existsAllByBrandIdIn(anySet());
        verify(brandRepository, times(4)).findAllById(anySet());
        verify(brandRepository, times(0)).saveAll(anySet());
    }

    @Test
    void activateBrandFindAllByIdFailsThrowsNonRetryableException() {
        var activeBrandModel = new ActiveBrandModel()
                .setId("b00001")
                .setLastUser("user")
                .setEnabled(true);

        when(brandRepository.existsAllByBrandIdIn(anySet())).thenReturn(true);
        when(brandRepository.findAllById(anySet())).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.activateBrandSet(Set.of(activeBrandModel)));

        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(1)).existsAllByBrandIdIn(anySet());
        verify(brandRepository, times(1)).findAllById(anySet());
        verify(brandRepository, times(0)).saveAll(anySet());
    }

    @Test
    void activateBrandFindAllByIdFailsThrowsNotFoundException() {
        var activeBrandModel = new ActiveBrandModel()
                .setId("b00001")
                .setLastUser("user")
                .setEnabled(true);

        when(brandRepository.existsAllByBrandIdIn(Set.of("b00001"))).thenReturn(true);
        when(brandRepository.findAllById(anySet())).thenReturn(emptyList());

        var ex = assertThrows(NonRetryableException.class, () -> service.activateBrandSet(Set.of(activeBrandModel)));

        assertEquals(NotFoundException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(1)).existsAllByBrandIdIn(anySet());
        verify(brandRepository, times(1)).findAllById(anySet());
        verify(brandRepository, times(0)).saveAll(anySet());
    }

    @Test
    void activateBrandSaveAllFailsThrowsRetryableException() {
        var activeBrandModel = new ActiveBrandModel()
                .setId("b00000")
                .setLastUser("user")
                .setEnabled(true);

        final var mockBrand = mockBrandList(1).get(0);

        when(brandRepository.existsAllByBrandIdIn(anySet())).thenReturn(true);
        when(brandRepository.findAllById(anySet())).thenReturn(List.of(mockBrand));
        when(brandRepository.saveAll(anySet())).thenThrow(new OptimisticLockException("Optimistic lock exception"));

        var ex = assertThrows(RetryableException.class, () -> service.activateBrandSet(Set.of(activeBrandModel)));

        assertEquals(OptimisticLockException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(4)).existsAllByBrandIdIn(anySet());
        verify(brandRepository, times(4)).findAllById(anySet());
        verify(brandRepository, times(4)).saveAll(anySet());
    }

    @Test
    void activateBrandSaveAllFailsThrowsNonRetryableException() {
        var activeBrandModel = new ActiveBrandModel()
                .setId("b00000")
                .setLastUser("user")
                .setEnabled(true);

        final var mockBrand = mockBrandList(1).get(0);

        when(brandRepository.existsAllByBrandIdIn(anySet())).thenReturn(true);
        when(brandRepository.findAllById(anySet())).thenReturn(List.of(mockBrand));
        when(brandRepository.saveAll(anySet())).thenThrow(new RuntimeException("Regular exception test."));

        var ex = assertThrows(NonRetryableException.class, () -> service.activateBrandSet(Set.of(activeBrandModel)));

        assertEquals(RuntimeException.class, getRootCause(ex).getClass());
        verify(brandRepository, times(1)).existsAllByBrandIdIn(anySet());
        verify(brandRepository, times(1)).findAllById(anySet());
        verify(brandRepository, times(1)).saveAll(anySet());
    }

    private List<Brand> mockBrandList(int length) {
        return IntStream.rangeClosed(0, length - 1)
                .mapToObj(this::mockBrand)
                .collect(Collectors.toList());
    }

    private Brand mockBrand(int idx) {
        return new Brand()
                .setBrandId("b0000" + idx)
                .setName("brand")
                .setLastUser("user")
                .setDescription("brand description")
                .setEnabled(true);
    }
}
