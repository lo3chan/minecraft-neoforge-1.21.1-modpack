/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package mezz.jei.library.plugins.vanilla.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.common.Internal;
import mezz.jei.common.platform.IPlatformRenderHelper;
import mezz.jei.common.platform.IPlatformScreenHelper;
import mezz.jei.common.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class InventoryEffectRendererGuiHandler<T extends AbstractContainerMenu>
implements IGuiContainerHandler<EffectRenderingInventoryScreen<T>> {
    @Override
    public List<Rect2i> getGuiExtraAreas(EffectRenderingInventoryScreen<T> containerScreen) {
        if (!Internal.getJeiFeatures().getInventoryEffectRendererGuiHandlerEnabled()) {
            return List.of();
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return Collections.emptyList();
        }
        Collection activePotionEffects = player.getActiveEffects();
        if (activePotionEffects.isEmpty()) {
            return Collections.emptyList();
        }
        IPlatformScreenHelper screenHelper = Services.PLATFORM.getScreenHelper();
        ArrayList<Rect2i> areas = new ArrayList<Rect2i>();
        int x = screenHelper.getGuiLeft((AbstractContainerScreen<?>)containerScreen) + screenHelper.getXSize((AbstractContainerScreen<?>)containerScreen) + 2;
        int y = screenHelper.getGuiTop((AbstractContainerScreen<?>)containerScreen);
        int width = 32;
        int height = 33;
        if (activePotionEffects.size() > 5) {
            height = 132 / (activePotionEffects.size() - 1);
        }
        IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
        for (MobEffectInstance potionEffect : activePotionEffects) {
            if (!renderHelper.shouldRender(potionEffect)) continue;
            areas.add(new Rect2i(x, y, width, height));
            y += height;
        }
        return areas;
    }
}

