/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.Screen
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.ModLoadingContext
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.loading.FMLEnvironment
 *  net.neoforged.neoforge.client.gui.IConfigScreenFactory
 */
package traben.entity_texture_features;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import traben.entity_texture_features.ETF;

@Mod(value="entity_texture_features")
public class ETFInit {
    public ETFInit() {
        if (FMLEnvironment.dist.isClient()) {
            try {
                ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> this::createScreen);
            }
            catch (NoClassDefFoundError e) {
                System.out.println("[Entity Texture Features]: Mod config broken, download latest neoforge version");
            }
            ETF.start();
        }
    }

    Screen createScreen(ModContainer arg, Screen arg2) {
        return ETF.getConfigScreen(arg2);
    }
}

