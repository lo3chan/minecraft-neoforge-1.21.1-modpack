package dev.latvian.mods.kubejs.core;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.client.KubeJSKeybinds;
import dev.latvian.mods.kubejs.item.ItemClickedKubeEvent;
import dev.latvian.mods.kubejs.net.FirstClickPayload;
import dev.latvian.mods.kubejs.plugin.builtin.event.ItemEvents;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.GLFWInputWrapper;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.function.Function;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

@RemapPrefixForJS("kjs$")
public interface MinecraftClientKJS extends MinecraftEnvironmentKJS {
   default Minecraft kjs$self() {
      return (Minecraft)this;
   }

   @Override
   default Component kjs$getName() {
      return Component.literal(this.kjs$self().name());
   }

   @Override
   default void kjs$tell(Component message) {
      this.kjs$self().player.kjs$tell(message);
   }

   @Override
   default void kjs$setStatusMessage(Component message) {
      this.kjs$self().player.kjs$setStatusMessage(message);
   }

   @Info(
      value = "Runs the specified console command client-side with the player's permission level.",
      params = {@Param(
         name = "command",
         value = "The console command. Slash at the beginning is optional."
      )}
   )
   @Override
   default void kjs$runCommand(String command) {
      this.kjs$self().player.kjs$runCommand(command);
   }

   @Info(
      value = "Runs the specified console command client-side with the player's permission level. The command won't output any logs in chat nor console.",
      params = {@Param(
         name = "command",
         value = "The console command. Slash at the beginning is optional."
      )}
   )
   @Override
   default void kjs$runCommandSilent(String command) {
      this.kjs$self().player.kjs$runCommandSilent(command);
   }

   @Override
   default void kjs$setActivePostShader(@Nullable ResourceLocation id) {
      this.kjs$self().player.kjs$setActivePostShader(id);
   }

   @Nullable
   default Screen kjs$getCurrentScreen() {
      return this.kjs$self().screen;
   }

   default void kjs$setCurrentScreen(Screen gui) {
      this.kjs$self().setScreen(gui);
   }

   default void kjs$setTitle(String t) {
      ClientProperties.get().windowTitle = t.trim();
      this.kjs$self().updateTitle();
   }

   default String kjs$getTitle() {
      throw new NoMixinException();
   }

   default String kjs$getCurrentWorldName() {
      ServerData server = this.kjs$self().getCurrentServer();
      return server == null ? "Singleplayer" : server.name;
   }

   default boolean kjs$isKeyDown(int key) {
      return key != -1 && InputConstants.isKeyDown(this.kjs$self().getWindow().getWindow(), key);
   }

   default boolean kjs$isKeyDown(String keyName) {
      return this.kjs$isKeyDown(GLFWInputWrapper.get(keyName));
   }

   default boolean kjs$isKeyBindDown(String id) {
      KubeJSKeybinds.KubeKey bind = KubeJSKeybinds.get(id);
      return bind != null && bind.down;
   }

   default int kjs$getKeyBindPressedTicks(String id) {
      KubeJSKeybinds.KubeKey bind = KubeJSKeybinds.get(id);
      return bind != null && bind.down ? bind.ticksPressed : -1;
   }

   default boolean kjs$isKeyMappingDown(KeyMapping key) {
      if (key != null && !key.isUnbound() && key.isConflictContextAndModifierActive()) {
         if (key.getKey().getType() == Type.KEYSYM) {
            return this.kjs$isKeyDown(key.getKey().getValue());
         }

         if (key.getKey().getType() == Type.MOUSE) {
            return GLFW.glfwGetMouseButton(this.kjs$self().getWindow().getWindow(), key.getKey().getValue()) == 1;
         }
      }

      return false;
   }

   default boolean kjs$isShiftDown() {
      return Screen.hasShiftDown();
   }

   default boolean kjs$isCtrlDown() {
      return Screen.hasControlDown();
   }

   default boolean kjs$isAltDown() {
      return Screen.hasAltDown();
   }

   @HideFromJS
   default void kjs$startAttack0() {
      if (ItemEvents.FIRST_LEFT_CLICKED.hasListeners()) {
         LocalPlayer player = this.kjs$self().player;
         ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
         ResourceKey<Item> key = stack.getItem().kjs$getKey();
         if (ItemEvents.FIRST_LEFT_CLICKED.hasListeners(key)) {
            ItemEvents.FIRST_LEFT_CLICKED.post(ScriptType.CLIENT, key, new ItemClickedKubeEvent(player, InteractionHand.MAIN_HAND, stack));
         }
      }

      PacketDistributor.sendToServer(new FirstClickPayload(0), new CustomPacketPayload[0]);
   }

   @HideFromJS
   default void kjs$startUseItem0() {
      if (ItemEvents.FIRST_RIGHT_CLICKED.hasListeners()) {
         LocalPlayer player = this.kjs$self().player;

         for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);
            ResourceKey<Item> key = stack.getItem().kjs$getKey();
            if (ItemEvents.FIRST_RIGHT_CLICKED.hasListeners(key)) {
               ItemEvents.FIRST_RIGHT_CLICKED.post(ScriptType.CLIENT, key, new ItemClickedKubeEvent(player, hand, stack));
            }
         }
      }

      PacketDistributor.sendToServer(new FirstClickPayload(1), new CustomPacketPayload[0]);
   }

   @HideFromJS
   default void kjs$afterResourcesLoaded(boolean reload) {
      if (reload) {
         ConsoleJS.CLIENT.stopCapturingErrors();
      }

      ConsoleJS.CLIENT.info("Client resource reload complete!");
   }

   default Function<ResourceLocation, TextureAtlasSprite> kjs$getBlockTextureAtlas() {
      return this.kjs$self().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);
   }

   default Function<ResourceLocation, TextureAtlasSprite> kjs$getParticleTextureAtlas() {
      return this.kjs$self().getTextureAtlas(TextureAtlas.LOCATION_PARTICLES);
   }
}
