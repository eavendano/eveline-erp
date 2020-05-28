package net.erp.eveline.common.predicate;

import net.erp.eveline.model.ProviderModel;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ProviderPredicates {

    private static Pattern providerIdPattern = Pattern.compile("p[0-9]{5}");

    public static Predicate<ProviderModel> isProviderModelValid() {
        return providerModel -> isProviderIdValid().test(providerModel.getId());
    }

    public static Predicate<String> isProviderIdValid() {
        return providerId -> Optional.ofNullable(providerId).isPresent() && providerId.length() == 6 && providerIdPattern.matcher(providerId).matches();
    }

    public static String providerIdInvalidMessage() {
        return "The providerId provided might be null or is not a valid value.";
    }
}
