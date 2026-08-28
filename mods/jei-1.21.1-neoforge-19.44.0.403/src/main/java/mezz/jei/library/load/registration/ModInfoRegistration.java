/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSetMultimap
 */
package mezz.jei.library.load.registration;

import com.google.common.collect.ImmutableSetMultimap;
import java.util.Collection;
import mezz.jei.api.registration.IModInfoRegistration;
import mezz.jei.common.collect.SetMultiMap;

public class ModInfoRegistration
implements IModInfoRegistration {
    private final SetMultiMap<String, String> modAliases = new SetMultiMap();

    @Override
    public void addModAliases(String modId, Collection<String> aliases) {
        this.modAliases.putAll(modId, aliases);
    }

    public ImmutableSetMultimap<String, String> getModAliases() {
        return this.modAliases.toImmutable();
    }
}

