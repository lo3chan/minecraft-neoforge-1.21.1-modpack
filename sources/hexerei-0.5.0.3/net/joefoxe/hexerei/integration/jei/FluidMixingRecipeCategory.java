package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.MixingCauldron;
import net.joefoxe.hexerei.data.recipes.FluidMixingRecipe;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.tileentity.renderer.MixingCauldronRenderer;
import net.joefoxe.hexerei.util.HexereiTags;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.Tags.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class FluidMixingRecipeCategory implements IRecipeCategory<FluidMixingRecipe> {
   public static final ResourceLocation UID = HexereiUtil.getResource("fluid_mixing");
   public static final ResourceLocation POTION_UID = HexereiUtil.getResource("potion_mixing");
   public static final ResourceLocation TEXTURE = HexereiUtil.getResource("textures/gui/fluid_mixing_gui_jei.png");
   public static final ResourceLocation TEXTURE_BLANK = HexereiUtil.getResource("textures/block/blank.png");
   private IDrawable background;
   private final IDrawable icon;
   private final IDrawable liquid;
   private final IDrawable cauldron;
   private final IDrawable output1;
   private final IDrawable output2;
   private Block heatSource;
   private boolean findNewHeatSource;
   private String type;

   public void getTooltip(ITooltipBuilder tooltip, FluidMixingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
      if (recipe.getHeatCondition() != FluidMixingRecipe.HeatCondition.NONE && this.isHovering(mouseX, mouseY, 79.0, 59.0, 24.0, 18.0)) {
         tooltip.add(Component.translatable("tooltip.hexerei.heat_source"));
         if (Screen.hasShiftDown()) {
            tooltip.add(
               Component.translatable(
                     "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                  )
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            tooltip.add(Component.translatable("tooltip.hexerei.recipe_heated_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltip.add(Component.translatable("tooltip.hexerei.recipe_heated_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltip.add(Component.translatable("tooltip.hexerei.recipe_heated_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltip.add(Component.translatable("tooltip.hexerei.recipe_heated_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltip.add(
               Component.translatable(
                     "Heat source shown: - %s",
                     new Object[]{Component.translatable(this.heatSource.getDescriptionId()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(13391138)))}
                  )
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
         } else {
            tooltip.add(
               Component.translatable(
                     "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                  )
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            tooltip.add(Component.translatable("tooltip.hexerei.recipe_heated").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         }
      }

      super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
   }

   public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height) {
      return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
   }

   public int getWidth() {
      return this.background.getWidth();
   }

   public int getHeight() {
      return this.background.getHeight();
   }

   public FluidMixingRecipeCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(TEXTURE, 0, 0, 186, 109);
      this.icon = new ExtraCauldronIcon(() -> new ItemStack(Items.WATER_BUCKET));
      this.liquid = helper.createDrawable(TEXTURE, 208, 12, 16, 32);
      this.cauldron = helper.createDrawable(TEXTURE, 238, 50, 12, 10);
      this.output1 = helper.createDrawable(TEXTURE, 209, 64, 47, 82);
      this.output2 = helper.createDrawable(TEXTURE, 209, 146, 47, 82);
      this.heatSource = getTagStack(HexereiTags.Blocks.HEAT_SOURCES);
      this.type = "Fluid";
   }

   public FluidMixingRecipeCategory(IGuiHelper helper, String type) {
      this.background = helper.createDrawable(TEXTURE, 0, 0, 186, 109);
      this.type = type;
      if (Objects.equals(this.type, "Potion")) {
         this.icon = new ExtraCauldronIcon(() -> new ItemStack(Items.POTION), type);
      } else {
         this.icon = new ExtraCauldronIcon(() -> new ItemStack(Items.WATER_BUCKET));
      }

      this.liquid = helper.createDrawable(TEXTURE, 208, 12, 16, 32);
      this.cauldron = helper.createDrawable(TEXTURE, 238, 50, 12, 10);
      this.output1 = helper.createDrawable(TEXTURE, 209, 64, 47, 82);
      this.output2 = helper.createDrawable(TEXTURE, 209, 146, 47, 82);
      this.heatSource = getTagStack(HexereiTags.Blocks.HEAT_SOURCES);
   }

   public static Block getTagStack(TagKey<Block> key) {
      if (BuiltInRegistries.BLOCK.getTag(key).isPresent()) {
         Optional<Holder<Block>> optional = ((Named)BuiltInRegistries.BLOCK.getTag(key).get()).getRandomElement(RandomSource.create());
         return (Block)optional.orElse(Holder.direct(Blocks.AIR)).value();
      } else {
         return Blocks.AIR;
      }
   }

   public RecipeType<FluidMixingRecipe> getRecipeType() {
      return new RecipeType(HexereiUtil.getResource(this.type.toLowerCase(Locale.ROOT) + "_mixing"), FluidMixingRecipe.class);
   }

   public Component getTitle() {
      return Component.translatable("gui.jei.category." + (this.type + "_mixing").toLowerCase(Locale.ROOT));
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, FluidMixingRecipe recipe, IFocusGroup focuses) {
      builder.moveRecipeTransferButton(160, 90);
      builder.setShapeless();
      FluidStack input = recipe.getLiquid();
      FluidStack output = recipe.getLiquidOutput();
      input.setAmount(2000);
      output.setAmount(2000);
      builder.addSlot(RecipeIngredientRole.OUTPUT, 152, 51)
         .setFluidRenderer(2000L, true, 12, 10)
         .setBackground(this.cauldron, 0, 0)
         .setOverlay(this.cauldron, 0, 0)
         .addFluidStack(recipe.getLiquidOutput().getFluid(), 2000L, recipe.getLiquidOutput().getComponentsPatch())
         .addRichTooltipCallback(
            (recipeSlotView, tooltip) -> {
               List<Component> tooltips = new ArrayList<>();
               ((PotionContents)recipe.getLiquidOutput().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY))
                  .addPotionTooltip(tooltips::add, 1.0F, 20.0F);
               tooltip.addAll(tooltips);
            }
         );
      builder.addSlot(RecipeIngredientRole.INPUT, 20, 57)
         .setFluidRenderer(2000L, false, 16, 32)
         .setBackground(this.liquid, 0, 0)
         .setOverlay(this.liquid, 0, 0)
         .addFluidStack(recipe.getLiquid().getFluid(), 2000L, recipe.getLiquid().getComponentsPatch())
         .addRichTooltipCallback((recipeSlotView, tooltip) -> {
            List<Component> tooltips = new ArrayList<>();
            ((PotionContents)recipe.getLiquid().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY))
               .addPotionTooltip(tooltips::add, 1.0F, 20.0F);
            tooltip.addAll(tooltips);
         });
      int size = recipe.getIngredients().size();
      if (size > 0) {
         builder.addSlot(RecipeIngredientRole.INPUT, 83, 18).addIngredients((Ingredient)recipe.getIngredients().get(0));
      }

      if (size > 1) {
         builder.addSlot(RecipeIngredientRole.INPUT, 105, 27).addIngredients((Ingredient)recipe.getIngredients().get(1));
      }

      if (size > 2) {
         builder.addSlot(RecipeIngredientRole.INPUT, 114, 49).addIngredients((Ingredient)recipe.getIngredients().get(2));
      }

      if (size > 3) {
         builder.addSlot(RecipeIngredientRole.INPUT, 105, 71).addIngredients((Ingredient)recipe.getIngredients().get(3));
      }

      if (size > 4) {
         builder.addSlot(RecipeIngredientRole.INPUT, 83, 80).addIngredients((Ingredient)recipe.getIngredients().get(4));
      }

      if (size > 5) {
         builder.addSlot(RecipeIngredientRole.INPUT, 61, 71).addIngredients((Ingredient)recipe.getIngredients().get(5));
      }

      if (size > 6) {
         builder.addSlot(RecipeIngredientRole.INPUT, 52, 49).addIngredients((Ingredient)recipe.getIngredients().get(6));
      }

      if (size > 7) {
         builder.addSlot(RecipeIngredientRole.INPUT, 61, 27).addIngredients((Ingredient)recipe.getIngredients().get(7));
      }
   }

   public void draw(FluidMixingRecipe recipe, IRecipeSlotsView view, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.background.draw(guiGraphics);
      if (recipe.getHeatCondition() != FluidMixingRecipe.HeatCondition.HEATED && recipe.getHeatCondition() != FluidMixingRecipe.HeatCondition.SUPERHEATED) {
         FluidStack input = recipe.getLiquid();
         FluidStack output = recipe.getLiquidOutput();
         input.setAmount(2000);
         output.setAmount(2000);
         float newHeatSource = ClientEvents.getClientTicks() % 200.0F / 200.0F;
         float craftPercent = ClientEvents.getClientTicks() % 100.0F / 100.0F;
         boolean showOutput = ClientEvents.getClientTicks() % 200.0F > 100.0F;
         if (newHeatSource <= 0.05F && this.findNewHeatSource || this.heatSource == null) {
            this.findNewHeatSource = false;
            if (Minecraft.getInstance().level != null) {
               this.heatSource = getTagStack(HexereiTags.Blocks.HEAT_SOURCES);
            }
         }

         if (newHeatSource > 0.05F) {
            this.findNewHeatSource = true;
         }

         boolean compare = FluidStack.isSameFluidSameComponents(recipe.getLiquid(), recipe.getLiquidOutput());
         Minecraft minecraft = Minecraft.getInstance();
         Component outputName = recipe.getLiquidOutput().getHoverName();
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

         BlockState blockState = (BlockState)((MixingCauldron)ModBlocks.MIXING_CAULDRON.get()).defaultBlockState().setValue(MixingCauldron.GUI_RENDER, true);
         ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
         BlockRenderDispatcher rendererer = Minecraft.getInstance().getBlockRenderer();
         rendererer.getBlockModel(blockState);
         BakedModel bakedModel = rendererer.getBlockModel(blockState);
         RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         guiGraphics.pose().pushPose();
         guiGraphics.pose().translate(70.0F, 73.0F, 100.0F);
         guiGraphics.pose().translate(8.0F, -8.0F, 0.0F);
         guiGraphics.pose().scale(20.0F, 20.0F, 20.0F);
         guiGraphics.pose().mulPose(new Matrix4f().scale(1.0F, -1.0F, 1.0F));
         Vec3 rotationOffset = new Vec3(0.0, 0.0, 0.0);
         float zRot = 0.0F;
         float xRot = 20.0F;
         float yRot = 30.0F;
         guiGraphics.pose().translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
         guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(zRot));
         guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(xRot));
         guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(yRot));
         guiGraphics.pose().translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);
         BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
         boolean flatLighting = !bakedModel.usesBlockLight();
         if (flatLighting) {
            Lighting.setupForFlatItems();
         }

         this.renderBlock(guiGraphics.pose(), buffer, 15728880, blockState, -12566464);
         float fillPercentage = 1.0F;
         if (!showOutput) {
            if (input.getFluid().is(Fluids.GASEOUS)) {
               MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, input, fillPercentage, 1.0F, OverlayTexture.NO_OVERLAY);
            } else {
               MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, input, 1.0F, fillPercentage, OverlayTexture.NO_OVERLAY);
            }

            float height = 0.25F + 0.6875F * fillPercentage;

            for (int i = 0; i < recipe.getIngredients().size(); i++) {
               ItemStack[] items = ((Ingredient)recipe.getIngredients().get(i)).getItems();
               if (items.length > 0 && !items[(int)ClientEvents.getClientTicksWithoutPartial() / 40 % items.length].isEmpty()) {
                  guiGraphics.pose().pushPose();
                  guiGraphics.pose().translate(0.5, height + 0.00390625F, 0.5);
                  double itemRotationOffset = 0.8 * i + craftPercent * (20.0F * craftPercent);
                  guiGraphics.pose()
                     .translate(
                        0.0 + Math.sin(itemRotationOffset) / (3.5F + craftPercent * craftPercent * 10.0F),
                        Math.sin(3.141592653589793 * ClientEvents.getClientTicks() / 30.0 + i * 20) / 10.0 * 0.2,
                        0.0 + Math.cos(itemRotationOffset) / (3.5F + craftPercent * craftPercent * 10.0F)
                     );
                  guiGraphics.pose()
                     .mulPose(Axis.YP.rotationDegrees((float)(45 * i - 1.0F + 2.0 * Math.sin((ClientEvents.getClientTicks() + i * 20) / 40.0F))));
                  guiGraphics.pose().mulPose(Axis.XP.rotationDegrees((float)(82.5 + 5.0 * Math.cos((ClientEvents.getClientTicks() + i * 22) / 40.0F))));
                  guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees((float)(-2.5 + 5.0 * Math.cos((ClientEvents.getClientTicks() + i * 24) / 40.0F))));
                  guiGraphics.pose().scale(1.0F - craftPercent * 0.5F, 1.0F - craftPercent * 0.5F, 1.0F - craftPercent * 0.5F);
                  guiGraphics.pose().scale(0.4F, 0.4F, 0.4F);
                  this.renderItem(
                     items[(int)ClientEvents.getClientTicksWithoutPartial() / 40 % items.length], minecraft.level, guiGraphics.pose(), buffer, 15728880
                  );
                  guiGraphics.pose().popPose();
               }
            }
         } else if (input.getFluid().is(Fluids.GASEOUS)) {
            MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, output, fillPercentage, 1.0F, OverlayTexture.NO_OVERLAY);
         } else {
            MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, output, 1.0F, fillPercentage, OverlayTexture.NO_OVERLAY);
         }

         buffer.endBatch();
         RenderSystem.enableDepthTest();
         if (flatLighting) {
            Lighting.setupFor3DItems();
         }

         guiGraphics.pose().popPose();
         if (output.isEmpty()
            || recipe.getLiquid().getFluid().isSame(recipe.getLiquidOutput().getFluid())
               && (!recipe.getLiquid().getFluid().isSame(recipe.getLiquidOutput().getFluid()) || compare)) {
            this.output1.draw(guiGraphics, 138, 16);
         } else {
            this.output2.draw(guiGraphics, 138, 16);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(0.6F, 0.6F, 0.6F);
            minecraft.font
               .drawInBatch(
                  Component.translatable("gui.jei.category.mixing_cauldron.convert_fluid"),
                  231.574F,
                  63.308F,
                  -12566464,
                  false,
                  guiGraphics.pose().last().pose(),
                  guiGraphics.bufferSource(),
                  DisplayMode.NORMAL,
                  0,
                  15728880
               );
            guiGraphics.pose().popPose();
         }
      } else {
         FluidStack inputx = recipe.getLiquid();
         FluidStack outputx = recipe.getLiquidOutput();
         inputx.setAmount(2000);
         outputx.setAmount(2000);
         float newHeatSourcex = ClientEvents.getClientTicks() % 200.0F / 200.0F;
         float craftPercentx = ClientEvents.getClientTicks() % 100.0F / 100.0F;
         boolean showOutputx = ClientEvents.getClientTicks() % 200.0F > 100.0F;
         if (newHeatSourcex <= 0.05F && this.findNewHeatSource || this.heatSource == null) {
            this.findNewHeatSource = false;
            if (Minecraft.getInstance().level != null) {
               this.heatSource = getTagStack(HexereiTags.Blocks.HEAT_SOURCES);
            }
         }

         if (newHeatSourcex > 0.05F) {
            this.findNewHeatSource = true;
         }

         boolean comparex = FluidStack.isSameFluidSameComponents(recipe.getLiquid(), recipe.getLiquidOutput());
         Minecraft minecraftx = Minecraft.getInstance();
         Component outputNamex = recipe.getLiquidOutput().getHoverName();
         int widthx = minecraftx.font.width(outputNamex);
         float lineHeightx = 9.0F / 2.0F;
         if (widthx > 131) {
            float percent = widthx / 131.0F;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(1.0F / percent, 1.0F / percent, 1.0F / percent);
            minecraftx.font
               .drawInBatch(
                  outputNamex,
                  7.0F * percent,
                  (5.0F + lineHeightx) * percent - 4.5F,
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
            minecraftx.font
               .drawInBatch(
                  outputNamex,
                  7.0F,
                  5.0F + lineHeightx - 4.5F,
                  -12566464,
                  false,
                  guiGraphics.pose().last().pose(),
                  guiGraphics.bufferSource(),
                  DisplayMode.NORMAL,
                  0,
                  15728880
               );
         }

         BlockState blockStatex = (BlockState)((MixingCauldron)ModBlocks.MIXING_CAULDRON.get()).defaultBlockState().setValue(MixingCauldron.GUI_RENDER, true);
         ItemRenderer rendererx = Minecraft.getInstance().getItemRenderer();
         RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         guiGraphics.pose().pushPose();
         guiGraphics.pose().translate(75.0F, 67.0F, 100.0F);
         guiGraphics.pose().translate(8.0F, -8.0F, 0.0F);
         guiGraphics.pose().scale(16.0F, 16.0F, 16.0F);
         guiGraphics.pose().mulPose(new Matrix4f().scale(1.0F, -1.0F, 1.0F));
         Vec3 rotationOffsetx = new Vec3(0.5, 0.0, 0.5);
         float zRotx = 0.0F;
         float xRotx = 20.0F;
         float yRotx = 30.0F;
         guiGraphics.pose().translate(rotationOffsetx.x, rotationOffsetx.y, rotationOffsetx.z);
         guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(zRotx));
         guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(xRotx));
         guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(yRotx));
         guiGraphics.pose().translate(-rotationOffsetx.x, -rotationOffsetx.y, -rotationOffsetx.z);
         BufferSource bufferx = Minecraft.getInstance().renderBuffers().bufferSource();
         Lighting.setupFor3DItems();
         guiGraphics.pose().last().normal().rotate(Axis.YP.rotationDegrees(-45.0F));
         this.renderBlock(guiGraphics.pose(), bufferx, 15728880, blockStatex, -1);
         guiGraphics.pose().pushPose();
         guiGraphics.pose().translate(0.0F, -1.0F, 0.0F);
         BlockState state = this.heatSource.defaultBlockState();
         if (state.getBlock() instanceof LiquidBlock liquidBlock) {
            state = (BlockState)liquidBlock.fluid.defaultFluidState().createLegacyBlock().setValue(LiquidBlock.LEVEL, 7);
            MixingCauldronRenderer.renderFluidBlockGUI(guiGraphics.pose(), bufferx, new FluidStack(liquidBlock.fluid, 2000), 1.0F, OverlayTexture.NO_OVERLAY);
         }

         this.renderBlock(guiGraphics.pose(), bufferx, 15728880, state, -1);
         guiGraphics.pose().popPose();
         float fillPercentagex = 1.0F;
         if (!showOutputx) {
            if (inputx.getFluid().is(Fluids.GASEOUS)) {
               MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), bufferx, inputx, fillPercentagex, 1.0F, OverlayTexture.NO_OVERLAY);
            } else {
               MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), bufferx, inputx, 1.0F, fillPercentagex, OverlayTexture.NO_OVERLAY);
            }

            float height = 0.25F + 0.6875F * fillPercentagex;

            for (int ix = 0; ix < recipe.getIngredients().size(); ix++) {
               ItemStack[] items = ((Ingredient)recipe.getIngredients().get(ix)).getItems();
               if (items.length > 0 && !items[(int)ClientEvents.getClientTicksWithoutPartial() / 40 % items.length].isEmpty()) {
                  guiGraphics.pose().pushPose();
                  guiGraphics.pose().translate(0.5, height + 0.00390625F, 0.5);
                  double itemRotationOffset = 0.8 * ix + craftPercentx * (20.0F * craftPercentx);
                  guiGraphics.pose()
                     .translate(
                        0.0 + Math.sin(itemRotationOffset) / (3.5F + craftPercentx * craftPercentx * 10.0F),
                        Math.sin(3.141592653589793 * ClientEvents.getClientTicks() / 30.0 + ix * 20) / 10.0 * 0.2,
                        0.0 + Math.cos(itemRotationOffset) / (3.5F + craftPercentx * craftPercentx * 10.0F)
                     );
                  guiGraphics.pose()
                     .mulPose(Axis.YP.rotationDegrees((float)(45 * ix - 1.0F + 2.0 * Math.sin((ClientEvents.getClientTicks() + ix * 20) / 40.0F))));
                  guiGraphics.pose().mulPose(Axis.XP.rotationDegrees((float)(82.5 + 5.0 * Math.cos((ClientEvents.getClientTicks() + ix * 22) / 40.0F))));
                  guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees((float)(-2.5 + 5.0 * Math.cos((ClientEvents.getClientTicks() + ix * 24) / 40.0F))));
                  guiGraphics.pose().scale(1.0F - craftPercentx * 0.5F, 1.0F - craftPercentx * 0.5F, 1.0F - craftPercentx * 0.5F);
                  guiGraphics.pose().scale(0.4F, 0.4F, 0.4F);
                  this.renderItem(
                     items[(int)ClientEvents.getClientTicksWithoutPartial() / 40 % items.length], minecraftx.level, guiGraphics.pose(), bufferx, 15728880
                  );
                  guiGraphics.pose().popPose();
               }
            }
         } else if (inputx.getFluid().is(Fluids.GASEOUS)) {
            MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), bufferx, outputx, fillPercentagex, 1.0F, OverlayTexture.NO_OVERLAY);
         } else {
            MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), bufferx, outputx, 1.0F, fillPercentagex, OverlayTexture.NO_OVERLAY);
         }

         guiGraphics.pose().popPose();
         Lighting.setupFor3DItems();
         bufferx.endBatch();
         RenderSystem.enableDepthTest();
         if (outputx.isEmpty()
            || recipe.getLiquid().getFluid().isSame(recipe.getLiquidOutput().getFluid())
               && (!recipe.getLiquid().getFluid().isSame(recipe.getLiquidOutput().getFluid()) || comparex)) {
            this.output1.draw(guiGraphics, 138, 16);
         } else {
            this.output2.draw(guiGraphics, 138, 16);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(0.6F, 0.6F, 0.6F);
            minecraftx.font
               .drawInBatch(
                  Component.translatable("gui.jei.category.mixing_cauldron.convert_fluid"),
                  231.574F,
                  63.308F,
                  -12566464,
                  false,
                  guiGraphics.pose().last().pose(),
                  guiGraphics.bufferSource(),
                  DisplayMode.NORMAL,
                  0,
                  15728880
               );
            guiGraphics.pose().popPose();
         }
      }
   }

   private void renderItem(ItemStack stack, Level level, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
      Minecraft.getInstance()
         .getItemRenderer()
         .renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, level, 1);
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
