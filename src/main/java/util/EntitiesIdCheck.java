package util;

import model.Entity;

public class EntitiesIdCheck {

    private static final String positiveValue = "Id shall be a positive value";
    private static final String addMethodCall = "Id shall not be a positive value. " +
            "Possible you need to call \"update()\" method instead of \"add()\"";
    private static final String objectShallNotBeNull = "Object shall not be null";

    public static void throwIfIdIsSmallerThanOne(Long id) {
        throwIfObjectIsNull(id);
        if (id < 1L) {
            throw new IllegalArgumentException(positiveValue);
        }

    }

    public static void throwIfIdIsSmallerThanOne(Entity entity) {
        throwIfObjectIsNull(entity);
        Object temp = entity.getId();
        if (temp instanceof Long) {
            if ((Long) temp < 1L) {
                throw new IllegalArgumentException(positiveValue);
            }
        }
    }

    public static void throwIfIdIsPositive(Entity entity) {
        throwIfObjectIsNull(entity);
        Object temp = entity.getId();
        if (temp instanceof Long) {
            if ((Long) temp >= 1L) {
                throw new IllegalArgumentException(addMethodCall);
            }
        }

    }

    public static void throwIfObjectIsNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException(objectShallNotBeNull);
        }
    }
}
