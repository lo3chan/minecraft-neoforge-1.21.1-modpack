/*
 * Decompiled with CFR 0.152.
 */
package traben.entity_texture_features.config;

import java.util.HashSet;
import java.util.Set;
import traben.entity_texture_features.config.ETFConfigWarning;

public abstract class ETFConfigWarnings {
    private static final Set<ETFConfigWarning> REGISTERED_WARNINGS = new HashSet<ETFConfigWarning>();

    public static void registerConfigWarning(ETFConfigWarning ... warnings) {
        for (ETFConfigWarning warn : warnings) {
            if (warn == null) continue;
            REGISTERED_WARNINGS.add(warn);
        }
    }

    public static Set<ETFConfigWarning> getRegisteredWarnings() {
        return REGISTERED_WARNINGS;
    }
}

