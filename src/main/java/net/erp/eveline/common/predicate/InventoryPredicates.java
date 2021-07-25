package net.erp.eveline.common.predicate;

import net.erp.eveline.model.ActiveInventoryModel;
import net.erp.eveline.model.InventoryModel;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;
import static net.erp.eveline.common.predicate.CommonPredicates.isLastUserValid;

public class InventoryPredicates {
    public static final String INVENTORY_MODEL_INVALID_MESSAGE = "The inventoryModel is invalid. Please verify the fields are correct.";
    public static final String INVENTORY_ID_INVALID_MESSAGE = "The inventoryId provided might be null or is not a valid input.";
    public static final String INVENTORY_QUANTITY_INVALID_MESSAGE = "The quantity provided might be null or is not a valid input.";
    public static final String INVENTORY_LAST_USER_INVALID_MESSAGE = "The lastUser field might be null or is not a valid input.";
    public static final String INVENTORY_ENABLED_INVALID_MESSAGE = "Enabled field must not be null";
    private static final Pattern inventoryIdPattern = Pattern.compile("i[0-9]{5}");

    public static Predicate<String> isInventoryIdValid() {
        return inventoryId -> ofNullable(inventoryId).isPresent()
                && inventoryId.length() == 6
                && inventoryIdPattern.matcher(inventoryId.trim()).matches();
    }

    public static Predicate<Boolean> isEnabledValid() {
        return enabled -> ofNullable(enabled).isPresent();
    }

    public static Predicate<Integer> isQuantityValid() {
        return quantity -> ofNullable(quantity).isPresent()
                && quantity >= 0;
    }


    public static Predicate<String> isInventoryIdValidAtInsert() {
        return inventoryId -> ofNullable(inventoryId).isEmpty();
    }

    public static Predicate<InventoryModel> isInventoryModelValid() {
        return inventoryModel -> isInventoryIdValid().test(inventoryModel.getId());
    }

    public static Predicate<InventoryModel> isInventoryModelValidForInsert(final List<String> errorList) {
        return inventoryModel -> {
            boolean idValid = isInventoryIdValidAtInsert().test(inventoryModel.getId());
            if (!idValid) {
                errorList.add(INVENTORY_ID_INVALID_MESSAGE);
            }
            return evaluateModel(errorList, inventoryModel, idValid);
        };
    }

    public static Predicate<InventoryModel> isInventoryModelValidForUpdate(final List<String> errorList) {
        return inventoryModel -> {
            boolean idValid = isInventoryIdValid().test(inventoryModel.getId());
            if (!idValid) {
                errorList.add(INVENTORY_ID_INVALID_MESSAGE);
            }
            return evaluateModel(errorList, inventoryModel, idValid);
        };
    }


    private static boolean evaluateModel(List<String> errorList, InventoryModel inventoryModel, boolean idValid) {
        boolean productIdValid = ProductPredicates.isProductIdValid().test(inventoryModel.getProductModel().getId());
        boolean warehouseIdValid = WarehousePredicates.isWarehouseIdValid().test(inventoryModel.getWarehouseModel().getId());
        boolean quantityValid = isQuantityValid().test(inventoryModel.getQuantity());
        boolean lastUserValid = isLastUserValid().test(inventoryModel.getLastUser());


        if (!productIdValid) {
            errorList.add(ProductPredicates.PRODUCT_ID_INVALID_MESSAGE);
        }
        if (!warehouseIdValid) {
            errorList.add(WarehousePredicates.WAREHOUSE_ID_INVALID_MESSAGE);
        }
        if (!quantityValid) {
            errorList.add(INVENTORY_QUANTITY_INVALID_MESSAGE);
        }

        if (!lastUserValid) {
            errorList.add(INVENTORY_LAST_USER_INVALID_MESSAGE);
        }

        return idValid
                && warehouseIdValid
                && productIdValid
                && quantityValid
                && lastUserValid;
    }

    public static Predicate<ActiveInventoryModel> isActiveInventoryModelValid(final List<String> errorList) {
        return activeInventoryModel -> {
            boolean idValid = isInventoryIdValid().test(activeInventoryModel.getId());
            boolean lastUserValid = isLastUserValid().test(activeInventoryModel.getLastUser());
            boolean enabledValid = isEnabledValid().test(activeInventoryModel.isEnabled());

            if (!idValid) {
                errorList.add(INVENTORY_ID_INVALID_MESSAGE);
            }
            if (!lastUserValid) {
                errorList.add(INVENTORY_LAST_USER_INVALID_MESSAGE);
            }
            if (!enabledValid) {
                errorList.add(INVENTORY_ENABLED_INVALID_MESSAGE);
            }

            return idValid && lastUserValid && enabledValid;
        };
    }


    public static Predicate<Set<ActiveInventoryModel>> isActiveInventorySetValid(final List<String> errorList) {
        return activeInventoryModel -> ofNullable(activeInventoryModel).isPresent()
                && activeInventoryModel.stream()
                .allMatch(activeBrandModel -> isActiveInventoryModelValid(errorList).test(activeBrandModel));
    }


}
