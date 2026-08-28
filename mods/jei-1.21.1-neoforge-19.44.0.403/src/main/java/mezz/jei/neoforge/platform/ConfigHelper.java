/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.neoforged.fml.ModList
 *  net.neoforged.fml.loading.FMLPaths
 *  net.neoforged.neoforge.client.gui.IConfigScreenFactory
 *  net.neoforged.neoforgespi.language.IModInfo
 */
package mezz.jei.neoforge.platform;

import java.nio.file.Path;
import java.util.Optional;
import mezz.jei.common.platform.IPlatformConfigHelper;
import mezz.jei.common.util.ErrorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforgespi.language.IModInfo;

public class ConfigHelper
implements IPlatformConfigHelper {
    @Override
    public Path getModConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Optional<Screen> getConfigScreen() {
        Minecraft minecraft = Minecraft.getInstance();
        ErrorUtil.checkNotNull(minecraft.screen, "minecraft.screen");
        return ModList.get().getModContainerById("jei").flatMap(m -> {
            IModInfo modInfo = m.getModInfo();
            return IConfigScreenFactory.getForMod((IModInfo)modInfo).map(f -> f.createScreen(m, minecraft.screen));
        });
    }
}

