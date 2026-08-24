package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Collection;
import java.util.Random;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.recipes.KeychainRecipe;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.KeychainItem;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;

public class KeychainApplyRecipeCategory implements IRecipeCategory<KeychainRecipe> {
   public static final ResourceLocation UID = HexereiUtil.getResource("keychain_apply");
   public static final ResourceLocation TEXTURE = HexereiUtil.getResource("textures/gui/add_to_candle_gui_jei.png");
   private IDrawable background;
   private final IDrawable icon;
   public ItemStack itemShown;
   private boolean findNewItem;

   public void getTooltip(ITooltipBuilder tooltip, KeychainRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
      if (this.isHovering(mouseX, mouseY, 33.0, 19.0, 16.0, 16.0)) {
         tooltip.add(Component.translatable("Any Item"));
         tooltip.add(
            Component.translatable(
                  "item shown: - %s",
                  new Object[]{Component.translatable(this.itemShown.getHoverName().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(13391138)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
      }

      super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
   }

   public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height) {
      return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
   }

   public KeychainApplyRecipeCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(TEXTURE, 0, 0, 144, 86);
      this.icon = new ExtraKeychainIcon(() -> new ItemStack((ItemLike)ModItems.CANDLE.get()));
      Collection<Item> col = BuiltInRegistries.ITEM.stream().toList();
      Random rand = new Random();
      if (col.toArray()[(int)(col.size() * rand.nextFloat())] instanceof Item item) {
         this.itemShown = new ItemStack(item);
      }
   }

   public int getWidth() {
      return this.background.getWidth();
   }

   public int getHeight() {
      return this.background.getHeight();
   }

   public RecipeType<KeychainRecipe> getRecipeType() {
      return new RecipeType(HexereiUtil.getResource("keychain_apply"), KeychainRecipe.class);
   }

   public Component getTitle() {
      return Component.translatable("Keychain Attach");
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, KeychainRecipe recipe, IFocusGroup focuses) {
      builder.moveRecipeTransferButton(160, 90);
      builder.setShapeless();
      builder.addSlot(RecipeIngredientRole.INPUT, 15, 19).addItemStack(new ItemStack((ItemLike)ModItems.BROOM_KEYCHAIN.get()));
   }

   public void draw(KeychainRecipe recipe, IRecipeSlotsView view, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.background.draw(guiGraphics);
      float newItem = ClientEvents.getClientTicks() % 200.0F / 200.0F;
      if (newItem <= 0.05F && this.findNewItem || this.itemShown == null) {
         this.findNewItem = false;
         if (Minecraft.getInstance().level != null) {
            Collection<Item> col = BuiltInRegistries.ITEM.stream().toList();
            Random rand = new Random();
            if (col.toArray()[(int)(col.size() * rand.nextFloat())] instanceof Item item) {
               this.itemShown = new ItemStack(item);
            }
         }
      }

      if (newItem > 0.05F) {
         this.findNewItem = true;
      }

      Minecraft minecraft = Minecraft.getInstance();
      ItemRenderer renderer = minecraft.getItemRenderer();
      BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
      Component outputName = this.getTitle();
      RenderSystem.enableDepthTest();
      if (this.isHovering(mouseX, mouseY, 33.0, 19.0, 16.0, 16.0)) {
         guiGraphics.fill(33, 19, 49, 35, 1728053247);
      }

      if (!renderer.getModel(this.itemShown, null, null, 0).usesBlockLight()) {
         Lighting.setupForFlatItems();
      }

      new ItemStack((ItemLike)ModItems.BROOM_KEYCHAIN.get());
      ItemStack keychain = new ItemStack((ItemLike)ModItems.BROOM_KEYCHAIN.get());
      ItemStack other = this.itemShown;
      if (keychain.getItem() instanceof KeychainItem && !other.isEmpty()) {
         CompoundTag tag = ((CustomData)keychain.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         ListTag listtag = new ListTag();
         if (!other.isEmpty()) {
            listtag.add(other.save(Hexerei.DynamicRegistries.get()));
         }

         tag.put("Items", listtag);
         keychain.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      }

      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(41.0F, 27.0F, 0.0F);
      guiGraphics.pose().mulPose(new Matrix4f().scale(1.0F, -1.0F, 1.0F));
      guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
      guiGraphics.pose().scale(16.0F, 16.0F, 16.0F);
      guiGraphics.pose().last().normal().rotate(Axis.YP.rotationDegrees(-45.0F));
      this.renderItem(this.itemShown, minecraft.level, guiGraphics.pose(), buffer, 15728880);
      guiGraphics.pose().popPose();
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(117.0F, 45.0F, 0.0F);
      guiGraphics.pose().mulPose(new Matrix4f().scale(1.0F, -1.0F, 1.0F));
      guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
      guiGraphics.pose().scale(16.0F, 16.0F, 16.0F);
      guiGraphics.pose().last().normal().rotate(Axis.YP.rotationDegrees(-45.0F));
      this.renderItem(keychain, minecraft.level, guiGraphics.pose(), buffer, 15728880);
      guiGraphics.pose().popPose();
      buffer.endBatch();
      RenderSystem.enableDepthTest();
      Lighting.setupFor3DItems();
      int width = minecraft.font.width(outputName);
      float lineHeight = 9.0F / 2.0F;
      if (width > 131) {
         float percent = width / 131.0F;
         guiGraphics.pose().pushPose();
         guiGraphics.pose().scale(1.0F / percent, 1.0F / percent, 1.0F / percent);
         minecraft.font
            .drawInBatch(
               outputName,
               7.0F * percent,
               (5.0F + lineHeight) * percent - 4.5F,
               -12566464,
               false,
               guiGraphics.pose().last().pose(),
               guiGraphics.bufferSource(),
               DisplayMode.NORMAL,
               0,
               15728880
            );
         guiGraphics.pose().popPose();
      } else {
         minecraft.font
            .drawInBatch(
               outputName,
               7.0F,
               5.0F + lineHeight - 4.5F,
               -12566464,
               false,
               guiGraphics.pose().last().pose(),
               guiGraphics.bufferSource(),
               DisplayMode.NORMAL,
               0,
               15728880
            );
      }
   }

   private void renderItem(ItemStack stack, Level level, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
      Minecraft.getInstance()
         .getItemRenderer()
         .renderStatic(stack, ItemDisplayContext.GUI, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, level, 1);
   }

   @OnlyIn(Dist.CLIENT)
   private void renderBlock(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
      this.renderSingleBlock(state, matrixStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);
   }

   @OnlyIn(Dist.CLIENT)
   public void renderSingleBlock(
      BlockState p_110913_, PoseStack poseStack, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color
   ) {
      RenderShape rendershape = p_110913_.getRenderShape();
      if (rendershape != RenderShape.INVISIBLE) {
         switch (rendershape) {
            case MODEL:
               BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
               BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
               float f = (color >> 16 & 0xFF) / 255.0F;
               float f1 = (color >> 8 & 0xFF) / 255.0F;
               float f2 = (color & 0xFF) / 255.0F;
               dispatcher.getModelRenderer()
                  .renderModel(
                     poseStack.last(),
                     p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)),
                     p_110913_,
                     bakedmodel,
                     f,
                     f1,
                     f2,
                     p_110916_,
                     p_110917_,
                     modelData,
                     null
                  );
               break;
            case ENTITYBLOCK_ANIMATED:
               ItemStack stack = new ItemStack(p_110913_.getBlock());
               poseStack.translate(0.2, -0.1, -0.1);
               IClientItemExtensions.of(stack.getItem())
                  .getCustomRenderer()
                  .renderByItem(stack, ItemDisplayContext.NONE, poseStack, p_110915_, p_110916_, p_110917_);
         }
      }
   }
}
