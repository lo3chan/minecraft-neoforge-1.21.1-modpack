package io.wispforest.owo.ui.component;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import io.wispforest.owo.mixin.ui.access.BlockEntityAccessor;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.parsing.UIModelParsingException;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.commands.arguments.blocks.BlockStateParser.BlockResult;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.w3c.dom.Element;

public class BlockComponent extends BaseComponent {
   private final Minecraft client = Minecraft.getInstance();
   private final BlockState state;
   @Nullable
   private final BlockEntity entity;

   protected BlockComponent(BlockState state, @Nullable BlockEntity entity) {
      this.state = state;
      this.entity = entity;
   }

   @Override
   public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      context.pose().pushPose();
      context.pose().translate(this.x + this.width / 2.0F, this.y + this.height / 2.0F, 100.0F);
      context.pose().scale(40 * this.width / 64.0F, -40 * this.height / 64.0F, 40.0F);
      context.pose().mulPose(Axis.XP.rotationDegrees(30.0F));
      context.pose().mulPose(Axis.YP.rotationDegrees(225.0F));
      context.pose().translate(-0.5, -0.5, -0.5);
      RenderSystem.runAsFancy(() -> {
         BufferSource vertexConsumers = this.client.renderBuffers().bufferSource();
         if (this.state.getRenderShape() != RenderShape.ENTITYBLOCK_ANIMATED) {
            this.client.getBlockRenderer().renderSingleBlock(this.state, context.pose(), vertexConsumers, 15728880, OverlayTexture.NO_OVERLAY);
         }

         if (this.entity != null) {
            BlockEntityRenderer<BlockEntity> медведь = this.client.getBlockEntityRenderDispatcher().getRenderer(this.entity);
            if (медведь != null) {
               медведь.render(this.entity, partialTicks, context.pose(), vertexConsumers, 15728880, OverlayTexture.NO_OVERLAY);
            }
         }

         RenderSystem.setShaderLights(new Vector3f(-1.5F, -0.5F, 0.0F), new Vector3f(0.0F, -1.0F, 0.0F));
         vertexConsumers.endBatch();
         Lighting.setupFor3DItems();
      });
      context.pose().popPose();
   }

   protected static void prepareBlockEntity(BlockState state, BlockEntity blockEntity, @Nullable CompoundTag nbt) {
      if (blockEntity != null) {
         ClientLevel world = Minecraft.getInstance().level;
         ((BlockEntityAccessor)blockEntity).owo$setCachedState(state);
         blockEntity.setLevel(world);
         if (nbt != null) {
            CompoundTag nbtCopy = nbt.copy();
            nbtCopy.putInt("x", 0);
            nbtCopy.putInt("y", 0);
            nbtCopy.putInt("z", 0);
            blockEntity.loadWithComponents(nbtCopy, world.registryAccess());
         }
      }
   }

   public static BlockComponent parse(Element element) {
      UIParsing.expectAttributes(element, "state");

      try {
         BlockResult result = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), element.getAttribute("state"), true);
         return Components.block(result.blockState(), result.nbt());
      } catch (CommandSyntaxException var2) {
         throw new UIModelParsingException("Invalid block state", var2);
      }
   }
}
