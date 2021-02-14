package net.erp.eveline.common.predicate;

import net.erp.eveline.model.ActivateProductModel;
import net.erp.eveline.model.ProductModel;
import net.erp.eveline.model.ProviderModel;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;
import static net.erp.eveline.common.predicate.ProviderPredicates.isProviderIdValid;

public class ProductPredicates {
    public static final String PRODUCT_MODEL_INVALID_MESSAGE = "The productModel is invalid. Please verify the fields are correct.";
    public static final String PRODUCT_ID_INVALID_MESSAGE = "The productId provided might be null or is not a valid input.";
    public static final String PRODUCT_ID_INVALID_AT_INSERT_MESSAGE = "The productId must be null if you're creating a product.";
    public static final String PRODUCT_TITLE_INVALID_MESSAGE = "The product title might be null or does not match the expresion.";
    public static final String PRODUCT_UPC_INVALID_MESSAGE = "The product upc might be null or does not match the expresion.";
    public static final String PRODUCT_DESCRIPTION_INVALID_MESSAGE = "The product description does not match the expresion.";
    public static final String PRODUCT_LAST_USER_INVALID_MESSAGE = "The lastUser field might be null or is not a valid input.";
    public static final String PRODUCT_ENABLED_INVALID_MESSAGE = "Enabled field must not be null";
    public static final String PRODUCT_PROVIDER_MODEL_INVALID_MESSAGE = "ProviderModel must have a valid ID and must not be null ";

    private static final Pattern productIdPattern = Pattern.compile("u[0-9]{9}");
    private static final Pattern productUpcPattern = Pattern.compile("[0-9]{12}");
    private static final Pattern productTitlePattern = Pattern.compile("[\\w\\s-]+");
    private static final Pattern productDescriptionPattern = Pattern.compile("[\\wáéíóúÁÉÍÓÚüÜñÑ$₡€@%|\\s()\\[\\]{}¡!¿?\";,&/.:'<>_+-]+");
    private static final Pattern productLastUserPattern = Pattern.compile("[\\w.]+");

    public static Predicate<ProductModel> isProductModelValidForInsert(final List<String> errorList) {
        return productModel -> {
            boolean idValid = isProductIdValidAtInsert().test(productModel.getId());
            return evaluateModel(errorList, productModel, idValid);
        };
    }

    public static Predicate<ProductModel> isProductModelValidForUpdate(final List<String> errorList) {
        return productModel -> {
            boolean idValid = isProductIdValid().test(productModel.getId());
            return evaluateModel(errorList, productModel, idValid);
        };
    }
    public static Predicate<String> isProductIdValidAtInsert() {
        return providerId -> ofNullable(providerId).isEmpty();
    }

    public static Predicate<ActivateProductModel> isActiveProductModelValid(final List<String> errorList) {
        return activateProductModel -> {
            boolean idValid = isProductIdValid().test(activateProductModel.getId());
            boolean lastUserValid = isLastUserValid().test(activateProductModel.getLastUser());
            boolean enabledValid = isEnabledValid().test(activateProductModel.isEnabled());

            if (!idValid) {
                errorList.add(PRODUCT_ID_INVALID_MESSAGE);
            }
            if (!lastUserValid) {
                errorList.add(PRODUCT_LAST_USER_INVALID_MESSAGE);
            }
            if (!enabledValid) {
                errorList.add(PRODUCT_ENABLED_INVALID_MESSAGE);
            }

            return idValid && lastUserValid && enabledValid;
        };
    }

    public static Predicate<String> isProductIdValid() {
        return providerId -> ofNullable(providerId).isPresent()
                && providerId.length() == 6
                && productIdPattern.matcher(providerId.trim()).matches();
    }

    public static Predicate<String> isProviderTitleValid() {
        return name -> ofNullable(name).isPresent()
                && name.length() >= 2
                && name.length() <= 100
                && productTitlePattern.matcher(name.trim()).matches();
    }

    public static Predicate<String> isProductDescriptionValid() {
        return description -> ofNullable(description).isEmpty()
                || productDescriptionPattern.matcher(description.trim()).matches();
    }

    public static Predicate<String> isLastUserValid() {
        return lastUser -> ofNullable(lastUser).isPresent()
                && lastUser.length() >= 3
                && lastUser.length() <= 100
                && productLastUserPattern.matcher(lastUser).matches();
    }

    public static Predicate<Boolean> isEnabledValid() {
        return enabled -> ofNullable(enabled).isPresent();
    }

    public static Predicate<String> isProductUpcValid() {
        return productUpc -> ofNullable(productUpc).isPresent()
                && productUpc.length() == 6
                && productUpcPattern.matcher(productUpc.trim()).matches();
    }

    public static Predicate<ProviderModel> isProviderValid(){
        return providerModel -> ofNullable(providerModel).isPresent()
                && isProviderIdValid().test(providerModel.getId());
    }

    private static boolean evaluateModel(List<String> errorList, ProductModel productModel, boolean idValid) {
        boolean titleValid = isProviderTitleValid().test(productModel.getTitle().trim());
        boolean descriptionValid = isProductDescriptionValid().test(productModel.getDescription().trim());
        boolean lastUserValid = isLastUserValid().test(productModel.getLastUser());
        boolean upcValid = isProductUpcValid().test(productModel.getUpc());
        boolean isProviderValid = isProviderValid().test(productModel.getProviderModel());

        if (!idValid) {
            errorList.add(PRODUCT_ID_INVALID_MESSAGE);
        }
        if (!titleValid) {
            errorList.add(PRODUCT_TITLE_INVALID_MESSAGE);
        }
        if (!descriptionValid) {
            errorList.add(PRODUCT_DESCRIPTION_INVALID_MESSAGE);
        }
        if(!upcValid){
            errorList.add(PRODUCT_UPC_INVALID_MESSAGE);
        }
        if (!lastUserValid) {
            errorList.add(PRODUCT_LAST_USER_INVALID_MESSAGE);
        }
        if(!isProviderValid){
            errorList.add(PRODUCT_PROVIDER_MODEL_INVALID_MESSAGE);
        }

        return idValid
                && titleValid
                && descriptionValid
                && lastUserValid;
    }

}
