package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.core.ClientPacketListenerKJS;
import dev.latvian.mods.kubejs.net.KubeServerData;
import dev.latvian.mods.kubejs.recipe.viewer.server.RecipeViewerData;
import dev.latvian.mods.kubejs.recipe.viewer.server.RemoteRecipeViewerDataUpdatedEvent;
import dev.latvian.mods.kubejs.text.tooltip.ItemTooltipData;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

public class KubeSessionData {
   public ResourceLocation activePostShader = null;
   public RecipeViewerData recipeViewerData = null;
   public List<ItemTooltipData> itemTooltips = List.of();

   @Nullable
   public static KubeSessionData of(ClientPacketListener listener) {
      return listener == null ? null : ((ClientPacketListenerKJS)listener).kjs$sessionData();
   }

   @Nullable
   public static KubeSessionData of(Minecraft mc) {
      return mc == null ? null : of(mc.getConnection());
   }

   public void sync(KubeServerData data) {
      this.recipeViewerData = data.recipeViewerData().orElse(null);
      this.itemTooltips = List.copyOf(data.itemTooltipData());
      NeoForge.EVENT_BUS.post(new RemoteRecipeViewerDataUpdatedEvent(this.recipeViewerData));
   }
}
