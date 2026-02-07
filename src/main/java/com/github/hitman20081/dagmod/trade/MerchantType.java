package com.github.hitman20081.dagmod.trade;

/**
 * Enum representing all merchant types that support rotating trades.
 */
public enum MerchantType {
    ARMORER("armorer"),
    MYSTERY_MERCHANT("mystery_merchant"),
    ENCHANTSMITH("enchantsmith"),
    VOODOO_ILLUSIONER("voodoo_illusioner"),
    TROPHY_DEALER("trophy_dealer"),
    MINER("miner"),
    HUNTER("hunter"),
    LUMBERJACK("lumberjack");

    private final String id;

    MerchantType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static MerchantType fromId(String id) {
        for (MerchantType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}
