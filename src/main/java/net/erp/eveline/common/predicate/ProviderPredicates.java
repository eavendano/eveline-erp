package net.erp.eveline.common.predicate;

import net.erp.eveline.model.ActiveProviderModel;
import net.erp.eveline.model.ProviderModel;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;

public class ProviderPredicates {

    public static final String PROVIDER_MODEL_INVALID_MESSAGE = "The providerModel is invalid. Please verify the fields are correct.";
    public static final String PROVIDER_ID_INVALID_MESSAGE = "The providerId provided might be null or is not a valid input.";
    public static final String PROVIDER_ID_INVALID_AT_INSERT_MESSAGE = "The providerId must be null if you're creating a provider.";
    public static final String PROVIDER_NAME_INVALID_MESSAGE = "The provider name might be null or does not match the expresion.";
    public static final String PROVIDER_DESCRIPTION_INVALID_MESSAGE = "The provider description does not match the expresion.";
    public static final String PROVIDER_EMAIL_INVALID_MESSAGE = "The provider email might be null or does not match the expresion.";
    public static final String PROVIDER_PHONE_INVALID_MESSAGE = "The provider phone does not match the expresion.";
    public static final String PROVIDER_NULL_PHONE_INVALID_MESSAGE = "The provider phone might be null or does not match the expresion.";
    public static final String PROVIDER_LAST_USER_INVALID_MESSAGE = "The lastUser field might be null or is not a valid input.";
    public static final String PROVIDER_ENABLED_INVALID_MESSAGE = "Enabled field must not be null";

    private static final Pattern providerIdPattern = Pattern.compile("p[0-9]{5}");
    private static final Pattern namePattern = Pattern.compile("[\\w\\s&.-]+");
    private static final Pattern descriptionPattern = Pattern.compile("[\\wáéíóúÁÉÍÓÚüÜñÑ$₡€@%|\\s()\\[\\]{}¡!¿?\";,&/.:'<>_+-]+");
    private static final Pattern emailPattern = Pattern.compile("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
    private static final Pattern phonePattern = Pattern.compile("\\d{8,25}");
    private static final Pattern lastUserPattern = Pattern.compile("[\\w.]+");

    public static Predicate<ProviderModel> isProviderModelValid() {
        return providerModel -> isProviderIdValid().test(providerModel.getId());
    }

    public static Predicate<ProviderModel> isProviderModelValidForInsert(final List<String> errorList) {
        return providerModel -> {
            boolean idValid = isProviderIdValidAtInsert().test(providerModel.getId());
            return evaluateModel(errorList, providerModel, idValid);
        };
    }

    public static Predicate<ProviderModel> isProviderModelValidForUpdate(final List<String> errorList) {
        return providerModel -> {
            boolean idValid = isProviderIdValid().test(providerModel.getId());
            return evaluateModel(errorList, providerModel, idValid);
        };
    }

    private static boolean evaluateModel(List<String> errorList, ProviderModel providerModel, boolean idValid) {
        boolean nameValid = isProviderNameValid().test(providerModel.getName().trim());
        boolean descriptionValid = isProviderDescriptionValid().test(providerModel.getDescription().trim());
        boolean emailValid = isProviderEmailValid().test(providerModel.getEmail().trim());
        boolean telephone1Valid = isProviderPhoneValid().test(providerModel.getTelephone1());
        boolean telephone2Valid = isProviderNullPhoneValid().test(providerModel.getTelephone2());
        boolean telephone3Valid = isProviderNullPhoneValid().test(providerModel.getTelephone3());
        boolean lastUserValid = isLastUserValid().test(providerModel.getLastUser());

        if (!idValid) {
            errorList.add(PROVIDER_ID_INVALID_AT_INSERT_MESSAGE);
        }
        if (!nameValid) {
            errorList.add(PROVIDER_NAME_INVALID_MESSAGE);
        }
        if (!descriptionValid) {
            errorList.add(PROVIDER_DESCRIPTION_INVALID_MESSAGE);
        }
        if (!emailValid) {
            errorList.add(PROVIDER_EMAIL_INVALID_MESSAGE);
        }
        if (!telephone1Valid) {
            errorList.add(PROVIDER_PHONE_INVALID_MESSAGE);
        }
        if (!telephone2Valid) {
            errorList.add(PROVIDER_NULL_PHONE_INVALID_MESSAGE);
        }
        if (!telephone3Valid) {
            errorList.add(PROVIDER_NULL_PHONE_INVALID_MESSAGE);
        }
        if (!lastUserValid) {
            errorList.add(PROVIDER_LAST_USER_INVALID_MESSAGE);
        }

        return idValid && nameValid && descriptionValid && emailValid && telephone1Valid && telephone2Valid && telephone3Valid && lastUserValid;
    }

    public static Predicate<ActiveProviderModel> isActiveProviderModelValid(final List<String> errorList) {
        return activeProviderModel -> {
            boolean idValid = isProviderIdValid().test(activeProviderModel.getId());
            boolean lastUserValid = isLastUserValid().test(activeProviderModel.getLastUser());
            boolean enabledValid = isEnabledValid().test(activeProviderModel.isEnabled());

            if (!idValid) {
                errorList.add(PROVIDER_ID_INVALID_MESSAGE);
            }
            if (!lastUserValid) {
                errorList.add(PROVIDER_LAST_USER_INVALID_MESSAGE);
            }
            if (!enabledValid) {
                errorList.add(PROVIDER_ENABLED_INVALID_MESSAGE);
            }

            return idValid && lastUserValid && enabledValid;
        };
    }

    public static Predicate<String> isProviderIdValid() {
        return providerId -> ofNullable(providerId).isPresent()
                && providerId.length() == 6
                && providerIdPattern.matcher(providerId.trim()).matches();
    }

    public static Predicate<String> isProviderIdValidAtInsert() {
        return providerId -> ofNullable(providerId).isEmpty();
    }

    public static Predicate<String> isProviderNameValid() {
        return name -> ofNullable(name).isPresent()
                && name.length() >= 2
                && name.length() <= 100
                && namePattern.matcher(name.trim()).matches();
    }

    public static Predicate<String> isProviderDescriptionValid() {
        return description -> ofNullable(description).isEmpty()
                || descriptionPattern.matcher(description.trim()).matches();
    }

    public static Predicate<String> isProviderEmailValid() {
        return email -> ofNullable(email).isPresent()
                && email.length() >= 3
                && email.length() <= 100
                && emailPattern.matcher(email.trim()).matches();
    }

    public static Predicate<String> isProviderPhoneValid() {
        return telephone -> ofNullable(telephone).isPresent()
                && phonePattern.matcher(telephone).matches();
    }

    public static Predicate<String> isProviderNullPhoneValid() {
        return telephone -> ofNullable(telephone).isEmpty()
                || phonePattern.matcher(telephone).matches();
    }

    public static Predicate<String> isLastUserValid() {
        return lastUser -> ofNullable(lastUser).isPresent()
                && lastUser.length() >= 3
                && lastUser.length() <= 100
                && lastUserPattern.matcher(lastUser).matches();
    }

    public static Predicate<Boolean> isEnabledValid() {
        return enabled -> ofNullable(enabled).isPresent();
    }
}
