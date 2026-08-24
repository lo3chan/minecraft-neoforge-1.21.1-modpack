package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.AetherTags;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;

public class LevelClientHooks {
   private static final HashMap<Integer, List<BlockPos>> positionsForTypes = new HashMap<>();

   public static void renderDungeonBlockOverlays(Stage stage, PoseStack poseStack, Camera camera, Frustum frustum, Minecraft minecraft) {
      if (stage == Stage.AFTER_PARTICLES && minecraft.level != null) {
         LocalPlayer player = minecraft.player;
         ClientLevel level = minecraft.level;
         RenderBuffers renderBuffers = minecraft.renderBuffers();
         int range = 32;
         if (player != null && player.isCreative()) {
            BlockPos playerPos = player.blockPosition();
            ItemStack stack = player.getMainHandItem();
            int type = idForItem(stack);
            if (type != -1) {
               updatePositions(playerPos, level, stack, range, type, false);
            }

            for (int i = 0; i < positionsForTypes.size(); i++) {
               renderOverlays(level, poseStack, renderBuffers, camera, frustum, i);
               updatePositions(playerPos, level, stack, range, i, true);
            }
         }
      }
   }

   private static void updatePositions(BlockPos playerPos, ClientLevel level, ItemStack stack, int range, int type, boolean depopulate) {
      positionsForTypes.putIfAbsent(0, new ArrayList<>());
      positionsForTypes.putIfAbsent(1, new ArrayList<>());
      positionsForTypes.putIfAbsent(2, new ArrayList<>());
      positionsForTypes.putIfAbsent(3, new ArrayList<>());

      for (int c = 0; c < 667; c++) {
         int x = playerPos.getX() + level.getRandom().nextInt(range) - level.getRandom().nextInt(range);
         int y = playerPos.getY() + level.getRandom().nextInt(range) - level.getRandom().nextInt(range);
         int z = playerPos.getZ() + level.getRandom().nextInt(range) - level.getRandom().nextInt(range);
         if (!depopulate) {
            BlockPos pos = new BlockPos(x, y, z);
            if (stack.is(level.getBlockState(pos).getBlock().asItem()) && !positionsForTypes.get(type).contains(pos)) {
               positionsForTypes.get(type).add(pos);
            }
         } else {
            List<BlockPos> positions = positionsForTypes.get(type);
            if (!positions.isEmpty() && level.getRandom().nextInt(100) == 0) {
               BlockPos pos = positions.get(level.getRandom().nextInt(positions.size()));
               if (!stack.is(level.getBlockState(pos).getBlock().asItem())) {
                  positions.remove(pos);
                  positionsForTypes.put(type, positions);
               }
            }
         }
      }
   }

   private static void renderOverlays(ClientLevel level, PoseStack poseStack, RenderBuffers renderBuffers, Camera camera, Frustum frustum, int type) {
      for (BlockPos blockPos : positionsForTypes.get(type)) {
         if (frustum.isVisible(new AABB(blockPos)) && level.getBlockState(blockPos).getRenderShape() != RenderShape.INVISIBLE) {
            drawSurfaces(
               renderBuffers.bufferSource(),
               poseStack.last(),
               blockPos,
               camera,
               (float)(blockPos.getX() - camera.getPosition().x()) - 0.001F,
               (float)(blockPos.getZ() - camera.getPosition().z()) - 0.001F,
               (float)(blockPos.getX() - camera.getPosition().x()) + 1.001F,
               (float)(blockPos.getZ() - camera.getPosition().z()) + 1.001F,
               (float)(blockPos.getY() - camera.getPosition().y()) - 0.001F,
               (float)(blockPos.getY() - camera.getPosition().y()) + 1.001F,
               type
            );
         }
      }

      renderBuffers.bufferSource().endBatch();
   }

   private static void drawSurfaces(
      MultiBufferSource buffer,
      Pose pose,
      BlockPos blockPos,
      Camera camera,
      float startX,
      float startZ,
      float endX,
      float endZ,
      float botY,
      float topY,
      int type
   ) {
      VertexConsumer builder = buffer.getBuffer(RenderType.cutout());
      TextureAtlasSprite sprite = spriteForId(type);
      if (sprite != null) {
         float minU = sprite.getU1();
         float maxU = sprite.getU0();
         float minV = sprite.getV1();
         float maxV = sprite.getV0();
         if (camera.getPosition().y() < blockPos.getY() + botY) {
            buildVertex(builder, pose, startX, botY, startZ, minU, minV, 0.0F, -1.0F, 0.0F);
            buildVertex(builder, pose, endX, botY, startZ, maxU, minV, 0.0F, -1.0F, 0.0F);
            buildVertex(builder, pose, endX, botY, endZ, maxU, maxV, 0.0F, -1.0F, 0.0F);
            buildVertex(builder, pose, startX, botY, endZ, minU, maxV, 0.0F, -1.0F, 0.0F);
         }

         if (camera.getPosition().y() > blockPos.getY() + topY) {
            buildVertex(builder, pose, endX, topY, startZ, minU, minV, 0.0F, 1.0F, 0.0F);
            buildVertex(builder, pose, startX, topY, startZ, maxU, minV, 0.0F, 1.0F, 0.0F);
            buildVertex(builder, pose, startX, topY, endZ, maxU, maxV, 0.0F, 1.0F, 0.0F);
            buildVertex(builder, pose, endX, topY, endZ, minU, maxV, 0.0F, 1.0F, 0.0F);
         }

         if (camera.getPosition().z() < blockPos.getZ() + startZ) {
            buildVertex(builder, pose, startX, botY, startZ, minU, minV, 0.0F, 0.0F, -1.0F);
            buildVertex(builder, pose, startX, topY, startZ, minU, maxV, 0.0F, 0.0F, -1.0F);
            buildVertex(builder, pose, endX, topY, startZ, maxU, maxV, 0.0F, 0.0F, -1.0F);
            buildVertex(builder, pose, endX, botY, startZ, maxU, minV, 0.0F, 0.0F, -1.0F);
         }

         if (camera.getPosition().z() > blockPos.getZ() + endZ) {
            buildVertex(builder, pose, endX, botY, endZ, minU, minV, 0.0F, 0.0F, 1.0F);
            buildVertex(builder, pose, endX, topY, endZ, minU, maxV, 0.0F, 0.0F, 1.0F);
            buildVertex(builder, pose, startX, topY, endZ, maxU, maxV, 0.0F, 0.0F, 1.0F);
            buildVertex(builder, pose, startX, botY, endZ, maxU, minV, 0.0F, 0.0F, 1.0F);
         }

         if (camera.getPosition().x() < blockPos.getX() + startX) {
            buildVertex(builder, pose, startX, botY, endZ, minU, minV, -1.0F, 0.0F, 0.0F);
            buildVertex(builder, pose, startX, topY, endZ, minU, maxV, -1.0F, 0.0F, 0.0F);
            buildVertex(builder, pose, startX, topY, startZ, maxU, maxV, -1.0F, 0.0F, 0.0F);
            buildVertex(builder, pose, startX, botY, startZ, maxU, minV, -1.0F, 0.0F, 0.0F);
         }

         if (camera.getPosition().x() > blockPos.getX() + endX) {
            buildVertex(builder, pose, endX, botY, startZ, minU, minV, 1.0F, 0.0F, 0.0F);
            buildVertex(builder, pose, endX, topY, startZ, minU, maxV, 1.0F, 0.0F, 0.0F);
            buildVertex(builder, pose, endX, topY, endZ, maxU, maxV, 1.0F, 0.0F, 0.0F);
            buildVertex(builder, pose, endX, botY, endZ, maxU, minV, 1.0F, 0.0F, 0.0F);
         }
      }
   }

   private static void buildVertex(VertexConsumer builder, Pose pose, float x, float y, float z, float u, float v, float normalX, float normalY, float normalZ) {
      builder.addVertex(pose, x, y, z)
         .setColor(255, 255, 255, 170)
         .setUv(u, v)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(240)
         .setNormal(pose, normalX, normalY, normalZ);
   }

   @Nullable
   private static TextureAtlasSprite spriteForId(int id) {
      switch (id) {
         case 0:
            return (TextureAtlasSprite)Minecraft.getInstance()
               .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
               .apply(ResourceLocation.fromNamespaceAndPath("aether", "block/dungeon/lock"));
         case 1:
            return (TextureAtlasSprite)Minecraft.getInstance()
               .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
               .apply(ResourceLocation.fromNamespaceAndPath("aether", "block/dungeon/exclamation"));
         case 2:
            return (TextureAtlasSprite)Minecraft.getInstance()
               .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
               .apply(ResourceLocation.fromNamespaceAndPath("aether", "block/dungeon/door"));
         case 3:
            return (TextureAtlasSprite)Minecraft.getInstance()
               .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
               .apply(ResourceLocation.fromNamespaceAndPath("aether", "block/dungeon/treasure"));
         default:
            return null;
      }
   }

   private static int idForItem(ItemStack stack) {
      if (stack.is(AetherTags.Items.LOCKED_DUNGEON_BLOCKS)) {
         return 0;
      } else if (stack.is(AetherTags.Items.TRAPPED_DUNGEON_BLOCKS)) {
         return 1;
      } else if (stack.is(AetherTags.Items.BOSS_DOORWAY_DUNGEON_BLOCKS)) {
         return 2;
      } else {
         return stack.is(AetherTags.Items.TREASURE_DOORWAY_DUNGEON_BLOCKS) ? 3 : -1;
      }
   }
}
