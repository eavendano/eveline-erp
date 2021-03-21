package net.erp.eveline.common.predicate;

import net.erp.eveline.model.ActiveProductModel;
import net.erp.eveline.model.ProductModel;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;
import static net.erp.eveline.common.predicate.CommonPredicates.*;
import static net.erp.eveline.common.predicate.ProviderPredicates.isProviderSetValid;

public class ProductPredicates {
    public static final String PRODUCT_MODEL_INVALID_MESSAGE = "The productModel is invalid. Please verify the fields are correct.";
    public static final String PRODUCT_ID_INVALID_MESSAGE = "The productId provided might be null or is not a valid input.";
    public static final String PRODUCT_ID_INVALID_AT_INSERT_MESSAGE = "The productId must be null if you're creating a product.";
    public static final String PRODUCT_TITLE_INVALID_MESSAGE = "The product title might be null or does not match the expresion.";
    public static final String PRODUCT_UPC_INVALID_MESSAGE = "The product upc might be null or does not match the expresion.";
    public static final String PRODUCT_DESCRIPTION_INVALID_MESSAGE = "The product description does not match the expresion.";
    public static final String PRODUCT_LAST_USER_INVALID_MESSAGE = "The lastUser field might be null or is not a valid input.";
    public static final String PRODUCT_ENABLED_INVALID_MESSAGE = "Enabled field must not be null.";
    public static final String PRODUCT_PROVIDER_SET_INVALID_MESSAGE = "ProviderSet must have a valid set of ID and must not be null.";

    private static final Pattern productIdPattern = Pattern.compile("s[0-9]{5}");
    private static final Pattern productUpcPattern = Pattern.compile("[0-9]{12}");
    private static final Pattern productTitlePattern = Pattern.compile("[\\w\\s-]+");

    public static Predicate<ProductModel> isProductModelValidForInsert(final List<String> errorList) {
        return productModel -> {
            boolean idValid = isProductIdValidAtInsert().test(productModel.getId());
            if (!idValid) {
                errorList.add(PRODUCT_ID_INVALID_AT_INSERT_MESSAGE);
            }
            return evaluateModel(errorList, productModel, idValid);
        };
    }

    public static Predicate<ProductModel> isProductModelValidForUpdate(final List<String> errorList) {
        return productModel -> {
            boolean idValid = isProductIdValid().test(productModel.getId());
            if (!idValid) {
                errorList.add(PRODUCT_ID_INVALID_MESSAGE);
            }
            return evaluateModel(errorList, productModel, idValid);
        };
    }

    public static Predicate<String> isProductIdValidAtInsert() {
        return productId -> ofNullable(productId).isEmpty();
    }

    public static Predicate<ActiveProductModel> isActiveProductModelValid(final List<String> errorList) {
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
        return productId -> ofNullable(productId).isPresent()
                && productId.length() == 6
                && productIdPattern.matcher(productId.trim()).matches();
    }

    public static Predicate<String> isProductTitleValid() {
        return title -> ofNullable(title).isPresent()
                && title.length() >= 2
                && title.length() <= 100
                && productTitlePattern.matcher(title.trim()).matches();
    }

    public static Predicate<Boolean> isEnabledValid() {
        return enabled -> ofNullable(enabled).isPresent();
    }

    public static Predicate<String> isProductUpcValid() {
        return productUpc -> ofNullable(productUpc).isPresent()
                && productUpc.length() == 12
                && productUpcPattern.matcher(productUpc.trim()).matches();
    }


    private static boolean evaluateModel(final List<String> errorList, final ProductModel productModel, boolean idValid) {
        boolean titleValid = isProductTitleValid().test(productModel.getTitle().trim());
        boolean descriptionValid = isDescriptionValid().test(productModel.getDescription());
        boolean lastUserValid = isLastUserValid().test(productModel.getLastUser());
        boolean upcValid = isProductUpcValid().test(productModel.getUpc());
        boolean isProviderSetValid = isProviderSetValid().test(productModel.getProviderSet());

        if (!titleValid) {
            errorList.add(PRODUCT_TITLE_INVALID_MESSAGE);
        }
        if (!descriptionValid) {
            errorList.add(PRODUCT_DESCRIPTION_INVALID_MESSAGE);
        }
        if (!upcValid) {
            errorList.add(PRODUCT_UPC_INVALID_MESSAGE);
        }
        if (!lastUserValid) {
            errorList.add(PRODUCT_LAST_USER_INVALID_MESSAGE);
        }
        if (!isProviderSetValid) {
            errorList.add(PRODUCT_PROVIDER_SET_INVALID_MESSAGE);
        }

        return idValid
                && titleValid
                && descriptionValid
                && upcValid
                && lastUserValid
                && isProviderSetValid;
    }

}
