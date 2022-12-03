package school.hei.utils;

import java.util.function.Supplier;

public class Utils {
    public static String infallibleSupply(Supplier<String> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return null;
        }
    }
}
