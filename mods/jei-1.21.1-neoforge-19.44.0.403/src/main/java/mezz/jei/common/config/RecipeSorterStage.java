/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import mezz.jei.common.config.IClientConfig;

public enum RecipeSorterStage {
    BOOKMARKED,
    CRAFTABLE;

    public static final List<RecipeSorterStage> defaultStages;

    public boolean isEnabled(IClientConfig clientConfig) {
        return clientConfig.recipeSorterStages().getValue().contains((Object)this);
    }

    public void setEnabled(IClientConfig clientConfig, boolean enabled) {
        List<RecipeSorterStage> recipeSorterStages = clientConfig.recipeSorterStages().getValue();
        boolean currentlyEnabled = recipeSorterStages.contains((Object)this);
        if (enabled == currentlyEnabled) {
            return;
        }
        recipeSorterStages = new ArrayList<RecipeSorterStage>(recipeSorterStages);
        if (enabled) {
            recipeSorterStages.add(this);
        } else {
            recipeSorterStages.remove((Object)this);
        }
        clientConfig.recipeSorterStages().set(recipeSorterStages);
    }

    public static Set<RecipeSorterStage> getEnabled(IClientConfig clientConfig) {
        return Set.copyOf((Collection)clientConfig.recipeSorterStages().getValue());
    }

    static {
        defaultStages = List.of(BOOKMARKED, CRAFTABLE);
    }
}

