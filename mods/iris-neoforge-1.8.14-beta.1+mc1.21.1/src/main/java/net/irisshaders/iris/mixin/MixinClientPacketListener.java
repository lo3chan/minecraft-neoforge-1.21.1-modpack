/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.HoverEvent
 *  net.minecraft.network.chat.HoverEvent$Action
 *  net.minecraft.network.protocol.game.ClientboundLoginPacket
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.shader.ShaderCompileException;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientPacketListener.class})
public class MixinClientPacketListener {
    @Inject(method={"handleLogin"}, at={@At(value="TAIL")})
    private void iris$showUpdateMessage(ClientboundLoginPacket a, CallbackInfo ci) {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        Iris.getUpdateChecker().getUpdateMessage().ifPresent(msg -> Minecraft.getInstance().player.displayClientMessage(msg, false));
        Iris.getStoredError().ifPresent(e -> Minecraft.getInstance().player.displayClientMessage((Component)Component.translatable((String)(e instanceof ShaderCompileException ? "iris.load.failure.shader" : "iris.load.failure.generic")).append((Component)Component.literal((String)"Copy Info").withStyle(arg -> arg.withUnderlined(Boolean.valueOf(true)).withColor(ChatFormatting.BLUE).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, e.getMessage())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (Object)Component.translatable((String)"chat.copy.click"))))), false));
        if (Iris.loadedIncompatiblePack()) {
            Minecraft.getInstance().gui.setTimes(10, 70, 140);
            Iris.logger.warn("Incompatible pack for DH!");
            Minecraft.getInstance().player.displayClientMessage((Component)Component.literal((String)"This pack doesn't have DH support.").withStyle(new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.RED}), false);
            Minecraft.getInstance().player.displayClientMessage((Component)Component.literal((String)"Distant Horizons (DH) chunks won't show up. This isn't a bug, get another shader.").withStyle(ChatFormatting.RED), false);
        }
    }
}

