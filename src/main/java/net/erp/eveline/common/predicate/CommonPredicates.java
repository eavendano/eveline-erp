package net.erp.eveline.common.predicate;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;

public class CommonPredicates {
    static final Pattern texFieldPattern = Pattern.compile("[\\wáéíóúÁÉÍÓÚüÜñÑ$₡€@%|\\s()\\[\\]{}¡!¿?\";,&/.:'<>_+-]*");
    static final Pattern phonePattern = Pattern.compile("\\d{8,25}");
    static final Pattern lastUserPattern = Pattern.compile("[\\w.]+");

    public static Predicate<String> isLastUserValid() {
        return lastUser -> ofNullable(lastUser).isPresent()
                && lastUser.length() >= 3
                && lastUser.length() <= 100
                && lastUserPattern.matcher(lastUser).matches();
    }

    public static Predicate<String> isDescriptionValid() {
        return description -> ofNullable(description).isEmpty()
                || texFieldPattern.matcher(description.trim()).matches();
    }

    public static Predicate<String> isPhoneValid() {
        return telephone -> ofNullable(telephone).isPresent()
                && phonePattern.matcher(telephone).matches();
    }

    public static Predicate<String> isOptionalPhoneValid() {
        return telephone -> ofNullable(telephone).isEmpty()
                || phonePattern.matcher(telephone).matches();
    }

}
