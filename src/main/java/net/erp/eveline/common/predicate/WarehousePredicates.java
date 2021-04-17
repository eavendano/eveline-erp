package net.erp.eveline.common.predicate;

import net.erp.eveline.model.ActiveWarehouseModel;
import net.erp.eveline.model.WarehouseModel;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;
import static net.erp.eveline.common.predicate.CommonPredicates.*;

public class WarehousePredicates {
    public static final String WAREHOUSE_MODEL_INVALID_MESSAGE = "The warehouseModel is invalid. Please verify the fields are correct.";
    public static final String WAREHOUSE_ID_INVALID_MESSAGE = "The warehouseId provided might be null or is not a valid input.";
    public static final String WAREHOUSE_ID_INVALID_AT_INSERT_MESSAGE = "The warehouseId must be null if you're creating a warehouse.";
    public static final String WAREHOUSE_NAME_INVALID_MESSAGE = "The warehouse name might be null or does not match the expresion.";
    public static final String WAREHOUSE_DESCRIPTION_INVALID_MESSAGE = "The warehouse description does not match the expresion.";
    public static final String WAREHOUSE_NOTES_INVALID_MESSAGE = "The warehouse's notes does not match the expresion.";
    public static final String WAREHOUSE_ADDRESS_INVALID_MESSAGE = "The warehouse address does not match the expresion.";
    public static final String WAREHOUSE_NULL_ADDRESS_INVALID_MESSAGE = "The warehouse address might be null or does not match the expresion.";
    public static final String WAREHOUSE_PHONE_INVALID_MESSAGE = "The warehouse phone does not match the expresion.";
    public static final String WAREHOUSE_NULL_PHONE_INVALID_MESSAGE = "The warehouse phone might be null or does not match the expresion.";
    public static final String WAREHOUSE_LAST_USER_INVALID_MESSAGE = "The lastUser field might be null or is not a valid input.";
    public static final String WAREHOUSE_ENABLED_INVALID_MESSAGE = "Enabled field must not be null";
    public static final String WAREHOUSE_LONGITUDE_INVALID_MESSAGE = "Longitude field must not be null";
    public static final String WAREHOUSE_LATITUDE_INVALID_MESSAGE = "Latitude field must not be null";

    private static final Pattern warehouseIdPattern = Pattern.compile("w[0-9]{5}");
    private static final Pattern namePattern = Pattern.compile("[\\w\\s&-]+");

    public static Predicate<String> isWarehouseIdValid() {
        return warehouseId -> ofNullable(warehouseId).isPresent()
                && warehouseId.length() == 6
                && warehouseIdPattern.matcher(warehouseId.trim()).matches();
    }

    public static Predicate<String> isWarehouseNameValid() {
        return name -> ofNullable(name).isPresent()
                && name.length() >= 3
                && name.length() <= 100
                && namePattern.matcher(name.trim()).matches();
    }

    public static Predicate<String> isWarehouseAddressValid() {
        return address -> ofNullable(address).isPresent()
                && !address.isBlank()
                && texFieldPattern.matcher(address.trim()).matches();
    }

    public static Predicate<String> isWarehouseOptionalAddressValid() {
        return address -> ofNullable(address).isEmpty()
                || texFieldPattern.matcher(address.trim()).matches();
    }

    public static Predicate<String> isWarehouseOptionalNotesValid() {
        return notes -> ofNullable(notes).isEmpty()
                || texFieldPattern.matcher(notes).matches();
    }

    public static Predicate<Boolean> isEnabledValid() {
        return enabled -> ofNullable(enabled).isPresent();
    }

    public static Predicate<String> isWarehouseIdValidAtInsert() {
        return warehouseId -> ofNullable(warehouseId).isEmpty();
    }

    public static Predicate<Double> isAxisValid() {
        return axis -> ofNullable(axis).isEmpty();
    }

    public static Predicate<WarehouseModel> isWarehouseModelValid() {
        return warehouseModel -> isWarehouseIdValid().test(warehouseModel.getId());
    }


    public static Predicate<WarehouseModel> isWarehouseModelValidForInsert(final List<String> errorList) {
        return warehouseModel -> {
            boolean idValid = isWarehouseIdValidAtInsert().test(warehouseModel.getId());
            if (!idValid) {
                errorList.add(WAREHOUSE_ID_INVALID_AT_INSERT_MESSAGE);
            }
            return evaluateModel(errorList, warehouseModel, idValid);
        };
    }


    public static Predicate<WarehouseModel> isWarehouseModelValidForUpdate(final List<String> errorList) {
        return warehouseModel -> {
            boolean idValid = isWarehouseIdValid().test(warehouseModel.getId());
            if (!idValid) {
                errorList.add(WAREHOUSE_ID_INVALID_MESSAGE);
            }
            return evaluateModel(errorList, warehouseModel, idValid);
        };
    }

    private static boolean evaluateModel(List<String> errorList, WarehouseModel warehouseModel, boolean idValid) {
        boolean nameValid = isWarehouseNameValid().test(warehouseModel.getName().trim());
        boolean descriptionValid = isDescriptionValid().test(warehouseModel.getDescription());
        boolean address1Valid = isWarehouseAddressValid().test(warehouseModel.getAddress1());
        boolean address2Valid = isWarehouseOptionalAddressValid().test(warehouseModel.getAddress2());
        boolean telephone1Valid = isPhoneValid().test(warehouseModel.getTelephone1());
        boolean telephone2Valid = isOptionalPhoneValid().test(warehouseModel.getTelephone2());
        boolean longitudeValid = isAxisValid().test(warehouseModel.getLongitude());
        boolean latitudeValid = isAxisValid().test(warehouseModel.getLatitude());
        boolean notesValid = isWarehouseOptionalNotesValid().test(warehouseModel.getNotes());
        boolean lastUserValid = isLastUserValid().test(warehouseModel.getLastUser());

        if (!nameValid) {
            errorList.add(WAREHOUSE_NAME_INVALID_MESSAGE);
        }
        if (!descriptionValid) {
            errorList.add(WAREHOUSE_DESCRIPTION_INVALID_MESSAGE);
        }
        if (!address1Valid) {
            errorList.add(WAREHOUSE_NULL_ADDRESS_INVALID_MESSAGE);
        }
        if (!address2Valid) {
            errorList.add(WAREHOUSE_ADDRESS_INVALID_MESSAGE);
        }
        if (!telephone1Valid) {
            errorList.add(WAREHOUSE_NULL_PHONE_INVALID_MESSAGE);
        }
        if (!telephone2Valid) {
            errorList.add(WAREHOUSE_PHONE_INVALID_MESSAGE);
        }
        if (!notesValid) {
            errorList.add(WAREHOUSE_NOTES_INVALID_MESSAGE);
        }
        if (!lastUserValid) {
            errorList.add(WAREHOUSE_LAST_USER_INVALID_MESSAGE);
        }

        if (!longitudeValid) {
            errorList.add(WAREHOUSE_LONGITUDE_INVALID_MESSAGE);
        }

        if (!latitudeValid) {
            errorList.add(WAREHOUSE_LATITUDE_INVALID_MESSAGE);
        }

        return idValid
                && nameValid
                && descriptionValid
                && address1Valid
                && address2Valid
                && telephone1Valid
                && telephone2Valid
                && notesValid
                && lastUserValid
                && longitudeValid
                && latitudeValid;
    }

    public static Predicate<ActiveWarehouseModel> isActiveWarehouseModelValid(final List<String> errorList) {
        return activeWarehouseModel -> {
            boolean idValid = isWarehouseIdValid().test(activeWarehouseModel.getId());
            boolean lastUserValid = isLastUserValid().test(activeWarehouseModel.getLastUser());
            boolean enabledValid = isEnabledValid().test(activeWarehouseModel.isEnabled());

            if (!idValid) {
                errorList.add(WAREHOUSE_ID_INVALID_MESSAGE);
            }
            if (!lastUserValid) {
                errorList.add(WAREHOUSE_LAST_USER_INVALID_MESSAGE);
            }
            if (!enabledValid) {
                errorList.add(WAREHOUSE_ENABLED_INVALID_MESSAGE);
            }

            return idValid && lastUserValid && enabledValid;
        };
    }

    public static Predicate<Set<ActiveWarehouseModel>> isActiveWarehouseSetValid(final List<String> errorList) {
        return activeWarehouseModelSet -> ofNullable(activeWarehouseModelSet).isPresent()
                && activeWarehouseModelSet.stream()
                .allMatch(activeWarehouseModel -> isActiveWarehouseModelValid(errorList).test(activeWarehouseModel));
    }


}
