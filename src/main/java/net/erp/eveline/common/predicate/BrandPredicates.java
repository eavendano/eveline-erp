package net.erp.eveline.common.predicate;
import net.erp.eveline.model.ActiveBrandModel;
import net.erp.eveline.model.BrandModel;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;
import static net.erp.eveline.common.predicate.CommonPredicates.*;
import static net.erp.eveline.common.predicate.CommonPredicates.isLastUserValid;

public class BrandPredicates {
    public static final String BRAND_MODEL_INVALID_MESSAGE = "The brandModel is invalid. Please verify the fields are correct.";
    public static final String BRAND_ID_INVALID_MESSAGE = "The brandId provided might be null or is not a valid input.";
    public static final String BRAND_ID_INVALID_AT_INSERT_MESSAGE = "The brandId must be null if you're creating a brand.";
    public static final String BRAND_NAME_INVALID_MESSAGE = "The brand name might be null or does not match the expresion.";
    public static final String BRAND_DESCRIPTION_INVALID_MESSAGE = "The brand description does not match the expresion.";
    public static final String BRAND_LAST_USER_INVALID_MESSAGE = "The lastUser field might be null or is not a valid input.";
    public static final String BRAND_ENABLED_INVALID_MESSAGE = "Enabled field must not be null";

    private static final Pattern brandIdPattern = Pattern.compile("b[0-9]{5}");
    private static final Pattern namePattern = Pattern.compile("[\\w\\s&-]+");

    public static Predicate<String> isBrandIdValid() {
        return brandId -> ofNullable(brandId).isPresent()
                && brandId.length() == 6
                && brandIdPattern.matcher(brandId.trim()).matches();
    }

    public static Predicate<String> isBrandNameValid() {
        return name -> ofNullable(name).isPresent()
                && name.length() >= 3
                && name.length() <= 100
                && namePattern.matcher(name.trim()).matches();
    }

    public static Predicate<Boolean> isEnabledValid() {
        return enabled -> ofNullable(enabled).isPresent();
    }

    public static Predicate<String> isBrandIdValidAtInsert() {
        return brandId -> ofNullable(brandId).isEmpty();
    }

    public static Predicate<BrandModel> isBrandModelValid() {
        return brandModel -> isBrandIdValid().test(brandModel.getId());
    }


    public static Predicate<BrandModel> isBrandModelValidForInsert(final List<String> errorList) {
        return brandModel -> {
            boolean idValid = isBrandIdValidAtInsert().test(brandModel.getId());
            if (!idValid) {
                errorList.add(BRAND_ID_INVALID_AT_INSERT_MESSAGE);
            }
            return evaluateModel(errorList, brandModel, idValid);
        };
    }


    public static Predicate<BrandModel> isBrandModelValidForUpdate(final List<String> errorList) {
        return brandModel -> {
            boolean idValid = isBrandIdValid().test(brandModel.getId());
            if (!idValid) {
                errorList.add(BRAND_ID_INVALID_MESSAGE);
            }
            return evaluateModel(errorList, brandModel, idValid);
        };
    }

    private static boolean evaluateModel(List<String> errorList, BrandModel brandModel, boolean idValid) {
        boolean nameValid = isBrandNameValid().test(brandModel.getName());
        boolean descriptionValid = isDescriptionValid().test(brandModel.getDescription());
        boolean lastUserValid = isLastUserValid().test(brandModel.getLastUser());

        if (!nameValid) {
            errorList.add(BRAND_NAME_INVALID_MESSAGE);
        }
        if (!descriptionValid) {
            errorList.add(BRAND_DESCRIPTION_INVALID_MESSAGE);
        }
        if (!lastUserValid) {
            errorList.add(BRAND_LAST_USER_INVALID_MESSAGE);
        }

        return idValid
                && nameValid
                && descriptionValid
                && lastUserValid;
    }

    public static Predicate<ActiveBrandModel> isActiveBrandModelValid(final List<String> errorList) {
        return activeBrandModel -> {
            boolean idValid = isBrandIdValid().test(activeBrandModel.getId());
            boolean lastUserValid = isLastUserValid().test(activeBrandModel.getLastUser());
            boolean enabledValid = isEnabledValid().test(activeBrandModel.isEnabled());

            if (!idValid) {
                errorList.add(BRAND_ID_INVALID_MESSAGE);
            }
            if (!lastUserValid) {
                errorList.add(BRAND_LAST_USER_INVALID_MESSAGE);
            }
            if (!enabledValid) {
                errorList.add(BRAND_ENABLED_INVALID_MESSAGE);
            }

            return idValid && lastUserValid && enabledValid;
        };
    }

    public static Predicate<Set<ActiveBrandModel>> isActiveBrandSetValid(final List<String> errorList) {
        return activeBrandModelSet -> ofNullable(activeBrandModelSet).isPresent()
                && activeBrandModelSet.stream()
                .allMatch(activeBrandModel -> isActiveBrandModelValid(errorList).test(activeBrandModel));
    }


}
