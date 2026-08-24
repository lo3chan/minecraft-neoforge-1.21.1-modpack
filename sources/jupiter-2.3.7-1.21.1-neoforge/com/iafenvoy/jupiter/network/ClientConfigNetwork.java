package com.iafenvoy.jupiter.network;

import com.iafenvoy.jupiter.config.container.AbstractConfigContainer;
import com.iafenvoy.jupiter.network.payload.ConfigErrorPayload;
import com.iafenvoy.jupiter.network.payload.ConfigRequestPayload;
import com.iafenvoy.jupiter.network.payload.ConfigSyncPayload;
import com.iafenvoy.jupiter.util.Comment;
import com.iafenvoy.jupiter.util.TextUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class ClientConfigNetwork {
   private static final Map<ResourceLocation, Consumer<CompoundTag>> CALLBACKS = new HashMap<>();

   public static void syncConfig(AbstractConfigContainer container) {
      syncConfig(container.getConfigId(), container::deserializeNbt);
   }

   @Comment("will pass null to nbt if not allowed")
   public static void syncConfig(ResourceLocation id, Consumer<CompoundTag> callback) {
      CALLBACKS.put(id, callback);
      ClientNetworkHelper.INSTANCE.sendToServer(new ConfigRequestPayload(id));
   }

   public static void init() {
      ClientNetworkHelper.INSTANCE
         .registerReceiver(ConfigSyncPayload.TYPE, (client, payload) -> onConfigSync(payload.id(), payload.allow(), payload.compound()));
      ClientNetworkHelper.INSTANCE.registerReceiver(ConfigErrorPayload.TYPE, (minecraft, buf) -> onConfigError(minecraft));
   }

   private static Runnable onConfigSync(ResourceLocation id, boolean allow, CompoundTag data) {
      Consumer<CompoundTag> callback = CALLBACKS.get(id);
      if (callback == null) {
         return null;
      } else {
         return allow ? () -> callback.accept(data) : () -> callback.accept(null);
      }
   }

   private static Runnable onConfigError(Minecraft minecraft) {
      return () -> minecraft.getToasts()
         .addToast(
            new SystemToast(
               SystemToastId.WORLD_ACCESS_FAILURE,
               TextUtil.translatable("jupiter.toast.upload_config_error_title"),
               TextUtil.translatable("jupiter.toast.upload_config_error_content")
            )
         );
   }
}
