package net.mehvahdjukaar.moonlight.api.integration.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;
import pepjebs.mapatlases.client.MapAtlasesClient;
import pepjebs.mapatlases.item.MapAtlasItem;

public class MapAtlasCompatImpl {
   public static boolean isAtlas(Item item) {
      return item instanceof MapAtlasItem;
   }

   @Nullable
   public static MapItemSavedData getSavedDataFromAtlas(ItemStack atlas, Level level, Player player) {
      return null;
   }

   @Nullable
   public static Integer getMapIdFromAtlas(ItemStack atlas, Level level, Object data) {
      return null;
   }

   public static void scaleDecoration(PoseStack poseStack) {
      MapAtlasesClient.modifyDecorationTransform(poseStack);
   }

   public static void scaleDecorationText(PoseStack poseStack, float textWidth, float textScale) {
      MapAtlasesClient.modifyTextDecorationTransform(poseStack, textWidth, textScale);
   }
}
