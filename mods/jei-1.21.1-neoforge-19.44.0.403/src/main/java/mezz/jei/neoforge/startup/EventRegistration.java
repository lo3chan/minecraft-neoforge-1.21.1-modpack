/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.neoforge.client.event.ClientTickEvent$Post
 *  net.neoforged.neoforge.client.event.ContainerScreenEvent$Render$Background
 *  net.neoforged.neoforge.client.event.ContainerScreenEvent$Render$Foreground
 *  net.neoforged.neoforge.client.event.ScreenEvent$CharacterTyped$Post
 *  net.neoforged.neoforge.client.event.ScreenEvent$CharacterTyped$Pre
 *  net.neoforged.neoforge.client.event.ScreenEvent$Init$Post
 *  net.neoforged.neoforge.client.event.ScreenEvent$KeyPressed
 *  net.neoforged.neoforge.client.event.ScreenEvent$KeyPressed$Post
 *  net.neoforged.neoforge.client.event.ScreenEvent$KeyPressed$Pre
 *  net.neoforged.neoforge.client.event.ScreenEvent$MouseButtonPressed
 *  net.neoforged.neoforge.client.event.ScreenEvent$MouseButtonPressed$Pre
 *  net.neoforged.neoforge.client.event.ScreenEvent$MouseButtonReleased
 *  net.neoforged.neoforge.client.event.ScreenEvent$MouseButtonReleased$Pre
 *  net.neoforged.neoforge.client.event.ScreenEvent$MouseDragged$Pre
 *  net.neoforged.neoforge.client.event.ScreenEvent$MouseScrolled$Pre
 *  net.neoforged.neoforge.client.event.ScreenEvent$Opening
 *  net.neoforged.neoforge.client.event.ScreenEvent$Render$Post
 *  net.neoforged.neoforge.client.event.ScreenEvent$RenderInventoryMobEffects
 */
package mezz.jei.neoforge.startup;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.gui.events.GuiEventHandler;
import mezz.jei.gui.input.ClientInputHandler;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.startup.JeiEventHandlers;
import mezz.jei.neoforge.events.RuntimeEventSubscriptions;
import mezz.jei.neoforge.input.ForgeUserInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

public class EventRegistration {
    public static void registerEvents(RuntimeEventSubscriptions subscriptions, JeiEventHandlers eventHandlers) {
        ClientInputHandler clientInputHandler = eventHandlers.clientInputHandler();
        EventRegistration.registerClientInputHandler(subscriptions, clientInputHandler);
        GuiEventHandler guiEventHandler = eventHandlers.guiEventHandler();
        EventRegistration.registerGuiHandler(subscriptions, guiEventHandler);
    }

    private static void registerClientInputHandler(RuntimeEventSubscriptions subscriptions, ClientInputHandler handler) {
        subscriptions.register(ScreenEvent.Init.Post.class, event -> handler.onInitGui());
        subscriptions.register(ScreenEvent.KeyPressed.Pre.class, event -> {
            UserInput input;
            Screen screen = event.getScreen();
            if (handler.onKeyboardKeyPressedPre(screen, input = ForgeUserInput.fromEvent((ScreenEvent.KeyPressed)event))) {
                event.setCanceled(true);
            }
        });
        subscriptions.register(ScreenEvent.KeyPressed.Post.class, event -> {
            UserInput input;
            Screen screen = event.getScreen();
            if (handler.onKeyboardKeyPressedPost(screen, input = ForgeUserInput.fromEvent((ScreenEvent.KeyPressed)event))) {
                event.setCanceled(true);
            }
        });
        subscriptions.register(ScreenEvent.CharacterTyped.Pre.class, event -> {
            int modifiers;
            char codePoint;
            Screen screen = event.getScreen();
            if (handler.onKeyboardCharTypedPre(screen, codePoint = event.getCodePoint(), modifiers = event.getModifiers())) {
                event.setCanceled(true);
            }
        });
        subscriptions.register(ScreenEvent.CharacterTyped.Post.class, event -> {
            Screen screen = event.getScreen();
            char codePoint = event.getCodePoint();
            int modifiers = event.getModifiers();
            handler.onKeyboardCharTypedPost(screen, codePoint, modifiers);
        });
        subscriptions.register(ScreenEvent.MouseButtonPressed.Pre.class, event -> ForgeUserInput.fromEvent((ScreenEvent.MouseButtonPressed)event).ifPresent(input -> {
            Screen screen = event.getScreen();
            if (handler.onGuiMouseClicked(screen, (UserInput)input)) {
                event.setCanceled(true);
            }
        }));
        subscriptions.register(ScreenEvent.MouseButtonReleased.Pre.class, event -> ForgeUserInput.fromEvent((ScreenEvent.MouseButtonReleased)event).ifPresent(input -> {
            Screen screen = event.getScreen();
            if (handler.onGuiMouseReleased(screen, (UserInput)input)) {
                event.setCanceled(true);
            }
        }));
        subscriptions.register(ScreenEvent.MouseScrolled.Pre.class, event -> {
            double scrollDeltaY;
            double scrollDeltaX;
            double mouseY;
            double mouseX = event.getMouseX();
            if (handler.onGuiMouseScroll(mouseX, mouseY = event.getMouseY(), scrollDeltaX = event.getScrollDeltaX(), scrollDeltaY = event.getScrollDeltaY())) {
                event.setCanceled(true);
            }
        });
        subscriptions.register(ScreenEvent.MouseDragged.Pre.class, event -> {
            Screen screen = event.getScreen();
            if (handler.onGuiMouseDragged(screen, event.getMouseX(), event.getMouseY(), event.getMouseButton(), event.getDragX(), event.getDragY())) {
                event.setCanceled(true);
            }
        });
    }

    public static void registerGuiHandler(RuntimeEventSubscriptions subscriptions, GuiEventHandler guiEventHandler) {
        subscriptions.register(ClientTickEvent.Post.class, event -> {
            if (Minecraft.getInstance().screen != null) {
                guiEventHandler.onClientTick();
            }
        });
        subscriptions.register(ScreenEvent.Init.Post.class, event -> {
            Screen screen = event.getScreen();
            guiEventHandler.onGuiInit(screen);
        });
        subscriptions.register(ScreenEvent.Opening.class, event -> {
            Screen screen = event.getScreen();
            guiEventHandler.onGuiOpen(screen);
        });
        subscriptions.register(EventPriority.LOWEST, ContainerScreenEvent.Render.Foreground.class, event -> {
            AbstractContainerScreen containerScreen = event.getContainerScreen();
            GuiGraphics guiGraphics = event.getGuiGraphics();
            int mouseX = event.getMouseX();
            int mouseY = event.getMouseY();
            EventRegistration.runWithIdentityPose(guiGraphics, () -> guiEventHandler.drawForContainerScreen(containerScreen, guiGraphics, mouseX, mouseY));
        });
        subscriptions.register(EventPriority.HIGHEST, ContainerScreenEvent.Render.Background.class, event -> {
            AbstractContainerScreen containerScreen = event.getContainerScreen();
            GuiGraphics guiGraphics = event.getGuiGraphics();
            int mouseX = event.getMouseX();
            int mouseY = event.getMouseY();
            EventRegistration.runWithIdentityPose(guiGraphics, () -> guiEventHandler.drawForScreen((Screen)containerScreen, guiGraphics, mouseX, mouseY));
        });
        subscriptions.register(EventPriority.HIGHEST, ScreenEvent.Render.Post.class, event -> {
            Screen screen = event.getScreen();
            if (screen instanceof AbstractContainerScreen) {
                return;
            }
            GuiGraphics guiGraphics = event.getGuiGraphics();
            int mouseX = event.getMouseX();
            int mouseY = event.getMouseY();
            EventRegistration.runWithIdentityPose(guiGraphics, () -> guiEventHandler.drawForScreen(screen, guiGraphics, mouseX, mouseY));
        });
        subscriptions.register(ScreenEvent.RenderInventoryMobEffects.class, event -> {
            if (guiEventHandler.renderCompactPotionIndicators()) {
                event.setCompact(true);
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void runWithIdentityPose(GuiGraphics graphics, Runnable runnable) {
        PoseStack pose = graphics.pose();
        float z = pose.last().pose().m32();
        pose.pushPose();
        pose.setIdentity();
        pose.translate(0.0f, 0.0f, z);
        try {
            runnable.run();
        }
        finally {
            pose.popPose();
        }
    }
}

