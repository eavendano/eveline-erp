package net.erp.eveline.common.predicate;

import java.util.regex.Pattern;

public class WarehousePredicates {
    public static final String WAREHOUSE_MODEL_INVALID_MESSAGE = "The warehouseModel is invalid. Please verify the fields are correct.";
    public static final String WAREHOUSE_ID_INVALID_MESSAGE = "The warehouseId provided might be null or is not a valid input.";
    public static final String WAREHOUSE_ID_INVALID_AT_INSERT_MESSAGE = "The warehouseId must be null if you're creating a warehouse.";
    public static final String WAREHOUSE_DESCRIPTION_INVALID_MESSAGE = "The warehouse description does not match the expresion.";
    public static final String WAREHOUSE_LAST_USER_INVALID_MESSAGE = "The lastUser field might be null or is not a valid input.";
    //TODO ADD LOCATION

    private static final Pattern warehouseIdPattern = Pattern.compile("w[0-9]{5}");
    private static final Pattern warehouseDescriptionPattern = Pattern.compile("[\\wáéíóúÁÉÍÓÚüÜñÑ$₡€@%|\\s()\\[\\]{}¡!¿?\";,&/.:'<>_+-]*");
    private static final Pattern warehouseLastUserPattern = Pattern.compile("[\\w.]+");

}
