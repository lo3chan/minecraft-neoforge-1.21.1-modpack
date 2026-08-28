/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.neoforge.client.event.ClientTickEvent$Post
 *  net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
 *  net.neoforged.neoforge.client.settings.IKeyConflictContext
 *  net.neoforged.neoforge.client.settings.KeyConflictContext
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.common.util.Lazy
 */
package net.diebuddies.bridge;

import com.mojang.blaze3d.platform.InputConstants;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.settings.PhysicsSettingsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.Lazy;

public class KeyBindingsRegistry {
    public static final Lazy<KeyMapping> GUI_PHYSICS = Lazy.of(() -> new KeyMapping("physicsmod.keybinding.guiphysics", (IKeyConflictContext)KeyConflictContext.GUI, InputConstants.Type.KEYSYM, 295, "physicsmod.keybinding.category"));
    public static final Lazy<KeyMapping> PHYISCS_MENU = Lazy.of(() -> new KeyMapping("physicsmod.keybinding.physicsmenu", (IKeyConflictContext)KeyConflictContext.UNIVERSAL, InputConstants.Type.KEYSYM, 296, "physicsmod.keybinding.category"));
    public static final Lazy<KeyMapping> TOGGLE_PHYSICS = Lazy.of(() -> new KeyMapping("physicsmod.keybinding.togglephysics", (IKeyConflictContext)KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "physicsmod.keybinding.category"));
    public static final Lazy<KeyMapping> PHYSICS_DEBUG_OVERLAY = Lazy.of(() -> new KeyMapping("physicsmod.keybinding.debug", (IKeyConflictContext)KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "physicsmod.keybinding.category"));

    public static void register(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(KeyBindingsRegistry::onClientTick);
        modEventBus.addListener(KeyBindingsRegistry::registerBindings);
    }

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register((KeyMapping)GUI_PHYSICS.get());
        event.register((KeyMapping)PHYISCS_MENU.get());
        event.register((KeyMapping)TOGGLE_PHYSICS.get());
        event.register((KeyMapping)PHYSICS_DEBUG_OVERLAY.get());
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (((KeyMapping)GUI_PHYSICS.get()).consumeClick()) {
        }
        while (((KeyMapping)PHYISCS_MENU.get()).consumeClick()) {
            Minecraft.getInstance().setScreen((Screen)new PhysicsSettingsScreen(null));
        }
        while (((KeyMapping)TOGGLE_PHYSICS.get()).consumeClick()) {
            ConfigClient.toggleSettings();
        }
        while (((KeyMapping)PHYSICS_DEBUG_OVERLAY.get()).consumeClick()) {
            ConfigClient.renderPhysicsDebugOverlay = !ConfigClient.renderPhysicsDebugOverlay;
        }
    }
}

