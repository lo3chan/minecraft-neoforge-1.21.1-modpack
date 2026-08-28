/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.ModList
 *  net.neoforged.fml.loading.FMLLoader
 *  net.neoforged.neoforgespi.language.IModInfo
 *  org.apache.commons.lang3.StringUtils
 */
package mezz.jei.neoforge.platform;

import java.util.HashMap;
import java.util.Map;
import mezz.jei.common.platform.IPlatformModHelper;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.commons.lang3.StringUtils;

public class ModHelper
implements IPlatformModHelper {
    private final Map<String, String> cache = new HashMap<String, String>();

    @Override
    public String getModNameForModId(String modId) {
        return this.cache.computeIfAbsent(modId, this::computeModNameForModId);
    }

    private String computeModNameForModId(String modId) {
        return ModList.get().getModContainerById(modId).map(ModContainer::getModInfo).map(IModInfo::getDisplayName).orElseGet(() -> StringUtils.capitalize((String)modId));
    }

    @Override
    public boolean isInDev() {
        return !FMLLoader.isProduction();
    }
}

