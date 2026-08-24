package com.github.alexthe666.alexsmobs.citadel.client.gui;

import com.github.alexthe666.alexsmobs.citadel.Citadel;
import com.github.alexthe666.alexsmobs.citadel.client.gui.data.EntityLinkData;
import com.github.alexthe666.alexsmobs.citadel.client.gui.data.EntityRenderData;
import com.github.alexthe666.alexsmobs.citadel.client.gui.data.ImageData;
import com.github.alexthe666.alexsmobs.citadel.client.gui.data.ItemRenderData;
import com.github.alexthe666.alexsmobs.citadel.client.gui.data.LineData;
import com.github.alexthe666.alexsmobs.citadel.client.gui.data.LinkData;
import com.github.alexthe666.alexsmobs.citadel.client.gui.data.RecipeData;
import com.github.alexthe666.alexsmobs.citadel.client.gui.data.TabulaRenderData;
import com.github.alexthe666.alexsmobs.citadel.client.gui.data.Whitespace;
import com.github.alexthe666.alexsmobs.citadel.client.model.TabulaModel;
import com.github.alexthe666.alexsmobs.citadel.client.model.TabulaModelHandler;
import com.github.alexthe666.alexsmobs.client.render.AMRenderCompat;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.io.IOUtils;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class GuiBasicBook extends Screen {
   private static final ResourceLocation BOOK_PAGE_TEXTURE = AMCompat.rl("alexsmobs:textures/gui/book/book_pages.png");
   private static final ResourceLocation BOOK_BINDING_TEXTURE = AMCompat.rl("alexsmobs:textures/gui/book/book_binding.png");
   private static final ResourceLocation BOOK_WIDGET_TEXTURE = AMCompat.rl("alexsmobs:textures/gui/book/widgets.png");
   private static final ResourceLocation BOOK_BUTTONS_TEXTURE = AMCompat.rl("alexsmobs:textures/gui/book/link_buttons.png");
   protected final List<LineData> lines = new ArrayList<>();
   protected final List<LinkData> links = new ArrayList<>();
   protected final List<ItemRenderData> itemRenders = new ArrayList<>();
   protected final List<RecipeData> recipes = new ArrayList<>();
   protected final List<TabulaRenderData> tabulaRenders = new ArrayList<>();
   protected final List<EntityRenderData> entityRenders = new ArrayList<>();
   protected final List<EntityLinkData> entityLinks = new ArrayList<>();
   protected final List<ImageData> images = new ArrayList<>();
   protected final List<Whitespace> yIndexesToSkip = new ArrayList<>();
   private final Map<String, TabulaModel> renderedTabulaModels = new HashMap<>();
   private final Map<String, Entity> renderedEntites = new HashMap<>();
   private final Map<String, ResourceLocation> textureMap = new HashMap<>();
   protected ItemStack bookStack;
   protected int xSize = 390;
   protected int ySize = 320;
   private static final int COLUMN_WIDTH_LEFT = 190;
   private static final int COLUMN_WIDTH_RIGHT = 170;
   protected int currentPageCounter = 0;
   protected int maxPagesFromPrinting = 0;
   protected int linesFromJSON = 0;
   protected int linesFromPrinting = 0;
   protected ResourceLocation prevPageJSON;
   protected ResourceLocation currentPageJSON;
   protected ResourceLocation currentPageText = null;
   protected BookPageButton buttonNextPage;
   protected BookPageButton buttonPreviousPage;
   protected BookPage internalPage = null;
   protected String writtenTitle = "";
   protected int preservedPageIndex = 0;
   protected String entityTooltip;
   private int mouseX;
   private int mouseY;
   private boolean suppressBackground;

   public GuiBasicBook(ItemStack bookStack, Component title) {
      super(title);
      this.bookStack = bookStack;
      this.currentPageJSON = this.getRootPage();
   }

   public static void drawTabulaModelOnScreen(
      GuiGraphics guiGraphics,
      TabulaModel model,
      ResourceLocation tex,
      int posX,
      int posY,
      float scale,
      boolean follow,
      double xRot,
      double yRot,
      double zRot,
      float mouseX,
      float mouseY
   ) {
      float f = (float)Math.atan(mouseX / 40.0F);
      float f1 = (float)Math.atan(mouseY / 40.0F);
      PoseStack matrixstack = new PoseStack();
      matrixstack.translate(posX, posY, 120.0);
      matrixstack.scale(scale, scale, scale);
      Quaternionf quaternion = Axis.ZP.rotationDegrees(0.0F);
      Quaternionf quaternion1 = Axis.XP.rotationDegrees(f1 * 20.0F);
      if (follow) {
         quaternion.mul(quaternion1);
      }

      matrixstack.mulPose(quaternion);
      if (follow) {
         matrixstack.mulPose(Axis.YP.rotationDegrees(180.0F + f * 40.0F));
      }

      matrixstack.mulPose(Axis.XP.rotationDegrees((float)(-xRot)));
      matrixstack.mulPose(Axis.YP.rotationDegrees((float)yRot));
      matrixstack.mulPose(Axis.ZP.rotationDegrees((float)zRot));
      EntityRenderDispatcher entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
      quaternion1.conjugate();
      entityrenderermanager.overrideCameraOrientation(quaternion1);
      entityrenderermanager.setRenderShadow(false);
      BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
      AMRenderCompat.runAsFancy(() -> {
         VertexConsumer ivertexbuilder = irendertypebuffer$impl.getBuffer(RenderType.entityCutoutNoCull(tex));
         model.resetToDefaultPose();
         model.renderToBuffer(matrixstack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      });
      Lighting.setupFor3DItems();
   }

   public void drawEntityOnScreen(
      GuiGraphics guiGraphics,
      MultiBufferSource bufferSource,
      int posX,
      int posY,
      float zOff,
      float scale,
      boolean follow,
      double xRot,
      double yRot,
      double zRot,
      float mouseX,
      float mouseY,
      Entity entity
   ) {
      float customYaw = posX - mouseX;
      float customPitch = posY - mouseY;
      float f = (float)Math.atan(customYaw / 40.0F);
      float f1 = (float)Math.atan(customPitch / 40.0F);
      if (follow) {
         float setX = f1 * 20.0F;
         float setY = f * 20.0F;
         entity.setXRot(setX);
         entity.setYRot(setY);
         if (entity instanceof LivingEntity) {
            ((LivingEntity)entity).yBodyRot = setY;
            ((LivingEntity)entity).yBodyRotO = setY;
            ((LivingEntity)entity).yHeadRot = setY;
            ((LivingEntity)entity).yHeadRotO = setY;
         }
      } else {
         f = 0.0F;
         f1 = 0.0F;
      }

      Quaternionf quaternion = Axis.ZP.rotationDegrees(180.0F);
      Quaternionf quaternion1 = Axis.XP.rotationDegrees(f1 * 20.0F);
      quaternion.mul(quaternion1);
      quaternion.mul(Axis.XN.rotationDegrees((float)xRot));
      quaternion.mul(Axis.YP.rotationDegrees((float)yRot));
      quaternion.mul(Axis.ZP.rotationDegrees((float)zRot));
      guiGraphics.pose().pushPose();
      AMRenderCompat.translateGui(guiGraphics, posX, posY, zOff);
      guiGraphics.pose().mulPose(new Matrix4f().scaling(scale, scale, -scale));
      guiGraphics.pose().mulPose(quaternion);
      Vector3f light0 = new Vector3f(1.0F, -1.0F, -1.0F).normalize();
      Vector3f light1 = new Vector3f(-1.0F, 1.0F, 1.0F).normalize();
      RenderSystem.setShaderLights(light0, light1);
      EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      quaternion1.conjugate();
      entityrenderdispatcher.overrideCameraOrientation(quaternion1);
      entityrenderdispatcher.setRenderShadow(false);
      AMRenderCompat.runAsFancy(() -> entityrenderdispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, guiGraphics.pose(), bufferSource, 240));
      entityrenderdispatcher.setRenderShadow(true);
      guiGraphics.flush();
      guiGraphics.pose().popPose();
      Lighting.setupFor3DItems();
      entity.setYRot(0.0F);
      entity.setXRot(0.0F);
      if (entity instanceof LivingEntity) {
         ((LivingEntity)entity).yBodyRot = 0.0F;
         ((LivingEntity)entity).yHeadRotO = 0.0F;
         ((LivingEntity)entity).yHeadRot = 0.0F;
      }
   }

   protected void init() {
      super.init();
      this.playBookOpeningSound();
      this.addNextPreviousButtons();
      this.addLinkButtons();
   }

   private void addNextPreviousButtons() {
      int k = (this.width - this.xSize) / 2;
      int l = (this.height - this.ySize + 128) / 2;
      this.buttonPreviousPage = (BookPageButton)this.addRenderableWidget(
         new BookPageButton(this, k + 10, l + 180, false, p_214208_1_ -> this.onSwitchPage(false), true)
      );
      this.buttonNextPage = (BookPageButton)this.addRenderableWidget(
         new BookPageButton(this, k + 365, l + 180, true, p_214205_1_ -> this.onSwitchPage(true), true)
      );
   }

   private void addLinkButtons() {
      this.renderables.clear();
      this.clearWidgets();
      this.addNextPreviousButtons();
      int k = (this.width - this.xSize) / 2;
      int l = (this.height - this.ySize + 128) / 2;

      for (LinkData linkData : this.links) {
         if (linkData.getPage() == this.currentPageCounter) {
            int maxLength = Math.max(100, Minecraft.getInstance().font.width(linkData.getTitleText()) + 20);
            this.yIndexesToSkip.add(new Whitespace(linkData.getPage(), linkData.getX() - maxLength / 2, linkData.getY(), 100, 20));
            this.addRenderableWidget(
               new LinkButton(
                  this,
                  k + linkData.getX() - maxLength / 2,
                  l + linkData.getY(),
                  maxLength,
                  20,
                  Component.translatable(linkData.getTitleText()),
                  linkData.getDisplayItem(),
                  p_213021_1_ -> {
                     this.prevPageJSON = this.currentPageJSON;
                     this.currentPageJSON = AMCompat.rl(this.getTextFileDirectory() + linkData.getLinkedPage());
                     this.preservedPageIndex = this.currentPageCounter;
                     this.currentPageCounter = 0;
                     this.addNextPreviousButtons();
                  }
               )
            );
         }

         if (linkData.getPage() > this.maxPagesFromPrinting) {
            this.maxPagesFromPrinting = linkData.getPage();
         }
      }

      for (EntityLinkData linkData : this.entityLinks) {
         if (linkData.getPage() == this.currentPageCounter) {
            this.yIndexesToSkip.add(new Whitespace(linkData.getPage(), linkData.getX() - 12, linkData.getY(), 100, 20));
            this.addRenderableWidget(new EntityLinkButton(this, linkData, k, l, p_213021_1_ -> {
               this.prevPageJSON = this.currentPageJSON;
               this.currentPageJSON = AMCompat.rl(this.getTextFileDirectory() + linkData.getLinkedPage());
               this.preservedPageIndex = this.currentPageCounter;
               this.currentPageCounter = 0;
               this.addNextPreviousButtons();
            }));
         }

         if (linkData.getPage() > this.maxPagesFromPrinting) {
            this.maxPagesFromPrinting = linkData.getPage();
         }
      }
   }

   private void onSwitchPage(boolean next) {
      if (next) {
         if (this.currentPageCounter < this.maxPagesFromPrinting) {
            this.currentPageCounter++;
         }
      } else if (this.currentPageCounter > 0) {
         this.currentPageCounter--;
      } else if (this.internalPage != null && !this.internalPage.getParent().isEmpty()) {
         this.prevPageJSON = this.currentPageJSON;
         this.currentPageJSON = AMCompat.rl(this.getTextFileDirectory() + this.internalPage.getParent());
         this.currentPageCounter = this.preservedPageIndex;
         this.preservedPageIndex = 0;
      }

      this.refreshSpacing();
   }

   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      if (!this.suppressBackground) {
         super.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
      }
   }

   public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
      this.mouseX = x;
      this.mouseY = y;
      int bindingColor = this.getBindingColor();
      int bindingR = bindingColor >> 16 & 0xFF;
      int bindingG = bindingColor >> 8 & 0xFF;
      int bindingB = bindingColor & 0xFF;
      this.renderBackground(guiGraphics, x, y, partialTicks);
      int k = (this.width - this.xSize) / 2;
      int l = (this.height - this.ySize + 128) / 2;
      BookBlit.blitWithColor(
         guiGraphics, this.getBookBindingTexture(), k, l, 0.0F, 0.0F, this.xSize, this.ySize, this.xSize, this.ySize, bindingR, bindingG, bindingB, 255
      );
      BookBlit.blitWithColor(guiGraphics, this.getBookPageTexture(), k, l, 0.0F, 0.0F, this.xSize, this.ySize, this.xSize, this.ySize, 255, 255, 255, 255);
      if (this.internalPage == null || this.currentPageJSON != this.prevPageJSON || this.prevPageJSON == null) {
         this.internalPage = this.generatePage(this.currentPageJSON);
         if (this.internalPage != null) {
            this.refreshSpacing();
         }
      }

      if (this.internalPage != null) {
         this.writePageText(guiGraphics, x, y);
      }

      this.suppressBackground = true;
      super.render(guiGraphics, x, y, partialTicks);
      this.suppressBackground = false;
      this.prevPageJSON = this.currentPageJSON;
      if (this.internalPage != null) {
         guiGraphics.pose().pushPose();
         this.renderOtherWidgets(guiGraphics, x, y, this.internalPage);
         guiGraphics.pose().popPose();
      }

      if (this.entityTooltip != null) {
         guiGraphics.pose().pushPose();
         AMRenderCompat.translateGui(guiGraphics, 0.0, 0.0, 550.0);
         guiGraphics.renderTooltip(
            this.font, Minecraft.getInstance().font.split(Component.translatable(this.entityTooltip), Math.max(this.width / 2 - 43, 170)), x, y
         );
         this.entityTooltip = null;
         guiGraphics.pose().popPose();
      }
   }

   private void refreshSpacing() {
      if (this.internalPage != null) {
         String lang = Minecraft.getInstance().getLanguageManager().getSelected().toLowerCase();
         this.currentPageText = AMCompat.rl(this.getTextFileDirectory() + lang + "/" + this.internalPage.getTextFileToReadFrom());
         boolean invalid = false;

         try {
            InputStream is = Minecraft.getInstance().getResourceManager().open(this.currentPageText);
            is.close();
         } catch (Exception var4) {
            invalid = true;
            Citadel.LOGGER.warn("Could not find language file for translation, defaulting to english");
            this.currentPageText = AMCompat.rl(this.getTextFileDirectory() + "en_us/" + this.internalPage.getTextFileToReadFrom());
         }

         this.readInPageWidgets(this.internalPage);
         this.addWidgetSpacing();
         this.addLinkButtons();
         this.readInPageText(this.currentPageText);
      }
   }

   private Item getItemByRegistryName(String registryName) {
      return (Item)BuiltInRegistries.ITEM.get(AMCompat.rl(registryName));
   }

   private BookRecipe getRecipeByName(String registryName) {
      return BookRecipe.get(registryName);
   }

   private void addWidgetSpacing() {
      this.yIndexesToSkip.clear();

      for (ItemRenderData itemRenderData : this.itemRenders) {
         Item item = this.getItemByRegistryName(itemRenderData.getItem());
         if (item != null) {
            this.yIndexesToSkip
               .add(
                  new Whitespace(
                     itemRenderData.getPage(),
                     itemRenderData.getX(),
                     itemRenderData.getY(),
                     (int)(itemRenderData.getScale() * 17.0),
                     (int)(itemRenderData.getScale() * 15.0)
                  )
               );
         }
      }

      for (RecipeData recipeData : this.recipes) {
         BookRecipe recipe = this.getRecipeByName(recipeData.getRecipe());
         if (recipe != null) {
            this.yIndexesToSkip
               .add(
                  new Whitespace(
                     recipeData.getPage(),
                     recipeData.getX(),
                     recipeData.getY() - (int)(recipeData.getScale() * 15.0),
                     (int)(recipeData.getScale() * 35.0),
                     (int)(recipeData.getScale() * 60.0),
                     true
                  )
               );
         }
      }

      for (ImageData imageData : this.images) {
         if (imageData != null) {
            this.yIndexesToSkip
               .add(
                  new Whitespace(
                     imageData.getPage(),
                     imageData.getX(),
                     imageData.getY(),
                     (int)(imageData.getScale() * imageData.getWidth()),
                     (int)(imageData.getScale() * imageData.getHeight() * 0.800000011920929)
                  )
               );
         }
      }

      if (!this.writtenTitle.isEmpty()) {
         this.yIndexesToSkip.add(new Whitespace(0, 20, 5, 70, 15));
      }
   }

   private void renderOtherWidgets(GuiGraphics guiGraphics, int x, int y, BookPage page) {
      int color = this.getBindingColor();
      int r = (color & 0xFF0000) >> 16;
      int g = (color & 0xFF00) >> 8;
      int b = color & 0xFF;
      int k = (this.width - this.xSize) / 2;
      int l = (this.height - this.ySize + 128) / 2;

      for (ImageData imageData : this.images) {
         if (imageData.getPage() == this.currentPageCounter && imageData != null) {
            ResourceLocation tex = this.textureMap.get(imageData.getTexture());
            if (tex == null) {
               tex = AMCompat.rl(imageData.getTexture());
               this.textureMap.put(imageData.getTexture(), tex);
            }

            float scale = (float)imageData.getScale();
            guiGraphics.pose().pushPose();
            AMRenderCompat.translateGui(guiGraphics, k + imageData.getX(), l + imageData.getY(), 0.0);
            AMRenderCompat.scaleGui(guiGraphics, scale, scale, scale);
            AMRenderCompat.blit(guiGraphics, tex, 0, 0, imageData.getU(), imageData.getV(), imageData.getWidth(), imageData.getHeight());
            guiGraphics.pose().popPose();
         }
      }

      for (RecipeData recipeData : this.recipes) {
         if (recipeData.getPage() == this.currentPageCounter) {
            guiGraphics.pose().pushPose();
            AMRenderCompat.translateGui(guiGraphics, k + recipeData.getX(), l + recipeData.getY(), 0.0);
            float scale = (float)recipeData.getScale();
            AMRenderCompat.scaleGui(guiGraphics, scale, scale, scale);
            AMRenderCompat.blit(guiGraphics, this.getBookWidgetTexture(), 0, 0, 0, 88, 116, 53);
            guiGraphics.pose().popPose();
         }
      }

      for (TabulaRenderData tabulaRenderData : this.tabulaRenders) {
         if (tabulaRenderData.getPage() == this.currentPageCounter) {
            TabulaModel model = null;
            ResourceLocation texture = this.textureMap.computeIfAbsent(tabulaRenderData.getTexture(), AMCompat::rl);
            if (this.renderedTabulaModels.get(tabulaRenderData.getModel()) != null) {
               model = this.renderedTabulaModels.get(tabulaRenderData.getModel());
            } else {
               try {
                  model = new TabulaModel(
                     TabulaModelHandler.INSTANCE
                        .loadTabulaModel("/assets/" + tabulaRenderData.getModel().split(":")[0] + "/" + tabulaRenderData.getModel().split(":")[1])
                  );
               } catch (Exception var20) {
                  Citadel.LOGGER.warn("Could not load in tabula model for book at " + tabulaRenderData.getModel());
               }

               this.renderedTabulaModels.put(tabulaRenderData.getModel(), model);
            }

            if (model != null && texture != null) {
               float scale = (float)tabulaRenderData.getScale();
               drawTabulaModelOnScreen(
                  guiGraphics,
                  model,
                  texture,
                  k + tabulaRenderData.getX(),
                  l + tabulaRenderData.getY(),
                  30.0F * scale,
                  tabulaRenderData.isFollow_cursor(),
                  tabulaRenderData.getRot_x(),
                  tabulaRenderData.getRot_y(),
                  tabulaRenderData.getRot_z(),
                  this.mouseX,
                  this.mouseY
               );
            }
         }
      }

      for (EntityRenderData data : this.entityRenders) {
         if (data.getPage() == this.currentPageCounter) {
            Entity modelx = null;
            EntityType type = (EntityType)BuiltInRegistries.ENTITY_TYPE.get(AMCompat.rl(data.getEntity()));
            if (type != null) {
               modelx = this.renderedEntites.computeIfAbsent(data.getEntity(), key -> AMCompat.createForDisplay(type, Minecraft.getInstance().level));
            }

            if (modelx != null) {
               float scale = (float)data.getScale();
               modelx.tickCount = Minecraft.getInstance().player.tickCount;
               if (data.getEntityData() != null) {
                  try {
                     CompoundTag tag = TagParser.parseTag(data.getEntityData());
                     AMCompat.loadEntity(modelx, tag);
                  } catch (CommandSyntaxException var19) {
                     var19.printStackTrace();
                  }
               }

               this.drawEntityOnScreen(
                  guiGraphics,
                  guiGraphics.bufferSource(),
                  k + data.getX(),
                  l + data.getY(),
                  1050.0F,
                  30.0F * scale,
                  data.isFollow_cursor(),
                  data.getRot_x(),
                  data.getRot_y(),
                  data.getRot_z(),
                  this.mouseX,
                  this.mouseY,
                  modelx
               );
            }
         }
      }

      for (RecipeData recipeDatax : this.recipes) {
         if (recipeDatax.getPage() == this.currentPageCounter) {
            BookRecipe recipe = this.getRecipeByName(recipeDatax.getRecipe());
            if (recipe != null) {
               this.renderRecipe(guiGraphics, recipe, recipeDatax, k, l);
            }
         }
      }

      for (ItemRenderData itemRenderData : this.itemRenders) {
         if (itemRenderData.getPage() == this.currentPageCounter) {
            Item item = this.getItemByRegistryName(itemRenderData.getItem());
            if (item != null) {
               float scale = (float)itemRenderData.getScale();
               ItemStack stack = new ItemStack(item);
               if (itemRenderData.getItemTag() != null && !itemRenderData.getItemTag().isEmpty()) {
                  CompoundTag tag = null;

                  try {
                     tag = TagParser.parseTag(itemRenderData.getItemTag());
                  } catch (CommandSyntaxException var18) {
                     var18.printStackTrace();
                  }

                  AMCompat.setTag(stack, tag);
               }

               guiGraphics.pose().pushPose();
               AMRenderCompat.translateGui(guiGraphics, k, l, 0.0);
               AMRenderCompat.scaleGui(guiGraphics, scale, scale, scale);
               guiGraphics.renderItem(stack, itemRenderData.getX(), itemRenderData.getY());
               guiGraphics.pose().popPose();
            }
         }
      }
   }

   protected void renderRecipe(GuiGraphics guiGraphics, BookRecipe recipe, RecipeData recipeData, int k, int l) {
      int playerTicks = Minecraft.getInstance().player.tickCount;
      float scale = (float)recipeData.getScale();
      List<ItemStack[]> ingredients = recipe.getIngredients();

      for (int i = 0; i < ingredients.size(); i++) {
         ItemStack[] options = ingredients.get(i);
         if (options.length != 0) {
            ItemStack stack = options.length > 1 ? options[(int)(playerTicks / 20.0F % options.length)] : options[0];
            if (!stack.isEmpty()) {
               guiGraphics.pose().pushPose();
               AMRenderCompat.translateGui(guiGraphics, k, l, 32.0);
               AMRenderCompat.translateGui(guiGraphics, (int)(recipeData.getX() + i % 3 * 20 * scale), (int)(recipeData.getY() + i / 3 * 20 * scale), 0.0);
               AMRenderCompat.scaleGui(guiGraphics, scale, scale, scale);
               guiGraphics.renderItem(stack, 0, 0);
               guiGraphics.pose().popPose();
            }
         }
      }

      ItemStack result = recipe.getResult();
      if (!result.isEmpty()) {
         guiGraphics.pose().pushPose();
         AMRenderCompat.translateGui(guiGraphics, k, l, 32.0);
         float finScale = scale * 1.5F;
         AMRenderCompat.translateGui(guiGraphics, recipeData.getX() + 70.0F * finScale, recipeData.getY() + 10.0F * finScale, 0.0);
         AMRenderCompat.scaleGui(guiGraphics, finScale, finScale, finScale);
         AMRenderCompat.translateGui(guiGraphics, 0.0, 0.0, 100.0);
         guiGraphics.renderItem(result, 0, 0);
         guiGraphics.pose().popPose();
      }
   }

   protected void writePageText(GuiGraphics guiGraphics, int x, int y) {
      Font font = this.font;
      int k = (this.width - this.xSize) / 2;
      int l = (this.height - this.ySize + 128) / 2;

      for (LineData line : this.lines) {
         if (line.getPage() == this.currentPageCounter) {
            guiGraphics.drawString(font, line.getText(), k + 10 + line.getxIndex(), l + 10 + line.getyIndex() * 12, this.getTextColor(), false);
         }
      }

      if (this.currentPageCounter == 0 && !this.writtenTitle.isEmpty()) {
         String actualTitle = I18n.get(this.writtenTitle, new Object[0]);
         guiGraphics.pose().pushPose();
         float scale = 2.0F;
         if (font.width(actualTitle) > 80) {
            scale = 2.0F - Mth.clamp((font.width(actualTitle) - 80) * 0.011F, 0.0F, 1.95F);
         }

         AMRenderCompat.translateGui(guiGraphics, k + 10, l + 10, 0.0);
         AMRenderCompat.scaleGui(guiGraphics, scale, scale, scale);
         guiGraphics.drawString(font, actualTitle, 0, 0, this.getTitleColor(), false);
         guiGraphics.pose().popPose();
      }

      this.buttonNextPage.visible = this.currentPageCounter < this.maxPagesFromPrinting;
      this.buttonPreviousPage.visible = this.currentPageCounter > 0 || !this.currentPageJSON.equals(this.getRootPage());
   }

   public boolean isPauseScreen() {
      return false;
   }

   protected void playBookOpeningSound() {
      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
   }

   protected void playBookClosingSound() {
   }

   protected abstract int getBindingColor();

   protected int getWidgetColor() {
      return this.getBindingColor();
   }

   protected int getTextColor() {
      return -13619152;
   }

   protected int getTitleColor() {
      return -12635614;
   }

   public abstract ResourceLocation getRootPage();

   public abstract String getTextFileDirectory();

   protected ResourceLocation getBookPageTexture() {
      return BOOK_PAGE_TEXTURE;
   }

   protected ResourceLocation getBookBindingTexture() {
      return BOOK_BINDING_TEXTURE;
   }

   protected ResourceLocation getBookWidgetTexture() {
      return BOOK_WIDGET_TEXTURE;
   }

   protected void playPageFlipSound() {
   }

   @Nullable
   protected BookPage generatePage(ResourceLocation res) {
      Optional<Resource> resource = null;
      BookPage page = null;

      try {
         resource = Minecraft.getInstance().getResourceManager().getResource(res);

         try {
            resource = Minecraft.getInstance().getResourceManager().getResource(res);
            if (resource.isPresent()) {
               BufferedReader inputstream = resource.get().openAsReader();
               page = BookPage.deserialize(inputstream);
            }
         } catch (IOException var5) {
            var5.printStackTrace();
         }

         return page;
      } catch (Exception var6) {
         return null;
      }
   }

   protected void readInPageWidgets(BookPage page) {
      this.links.clear();
      this.itemRenders.clear();
      this.recipes.clear();
      this.tabulaRenders.clear();
      this.entityRenders.clear();
      this.images.clear();
      this.entityLinks.clear();
      this.links.addAll(page.getLinkedButtons());
      this.entityLinks.addAll(page.getLinkedEntities());
      this.itemRenders.addAll(page.getItemRenders());
      this.recipes.addAll(page.getRecipes());
      this.tabulaRenders.addAll(page.getTabulaRenders());
      this.entityRenders.addAll(page.getEntityRenders());
      this.images.addAll(page.getImages());
      this.writtenTitle = page.generateTitle();
   }

   protected void readInPageText(ResourceLocation res) {
      Resource resource = null;
      int xIndex = 0;
      int actualTextX = 0;
      int yIndex = 0;

      try {
         BufferedReader bufferedreader = Minecraft.getInstance().getResourceManager().openAsReader(res);

         try {
            List<String> readStrings = IOUtils.readLines(bufferedreader);
            this.linesFromJSON = readStrings.size();
            this.lines.clear();
            Font pageFont = Minecraft.getInstance().font;
            List<String> splitBySpaces = new ArrayList<>();

            for (String line : readStrings) {
               for (String word : line.split(" ")) {
                  splitBySpaces.addAll(hardSplitToWidth(pageFont, word, 160));
               }
            }

            String lineToPrint = "";
            this.linesFromPrinting = 0;
            int page = 0;

            for (int i = 0; i < splitBySpaces.size(); i++) {
               String word = splitBySpaces.get(i);
               int cutoffPoint = xIndex > 100 ? 30 : 35;
               int effectiveX = yIndex > 13 ? (xIndex > 0 ? 0 : 200) : xIndex;
               int columnWidth = effectiveX > 100 ? 170 : 190;
               boolean newline = word.equals("<NEWLINE>");

               for (Whitespace indexes : this.yIndexesToSkip) {
                  int indexPage = indexes.getPage();
                  if (indexPage == page) {
                     int buttonX = indexes.getX();
                     int buttonY = indexes.getY();
                     int width = indexes.getWidth();
                     int height = indexes.getHeight();
                     if (indexes.isDown()) {
                        if (yIndex >= buttonY / 12.0F && yIndex <= (buttonY + height) / 12.0F && (buttonX < 90 && xIndex < 90 || buttonX >= 90 && xIndex >= 90)
                           )
                         {
                           yIndex += 2;
                        }
                     } else if (yIndex >= (buttonY - height) / 12.0F
                        && yIndex <= (buttonY + height) / 12.0F
                        && (buttonX < 90 && xIndex < 90 || buttonX >= 90 && xIndex >= 90)) {
                        yIndex++;
                     }
                  }
               }

               boolean last = i == splitBySpaces.size() - 1;
               actualTextX += word.length() + 1;
               boolean tooWide = !lineToPrint.isEmpty() && pageFont.width(lineToPrint + " " + word) > columnWidth;
               if (lineToPrint.length() + word.length() + 1 < cutoffPoint && !tooWide && !newline) {
                  lineToPrint = lineToPrint + " " + word;
                  if (last) {
                     this.linesFromPrinting++;
                     this.lines.add(new LineData(xIndex, yIndex, lineToPrint, page));
                     yIndex++;
                     actualTextX = 0;
                     if (newline) {
                        yIndex++;
                     }
                  }
               } else {
                  this.linesFromPrinting++;
                  if (yIndex > 13) {
                     if (xIndex > 0) {
                        page++;
                        xIndex = 0;
                        yIndex = 0;
                     } else {
                        xIndex = 200;
                        yIndex = 0;
                     }
                  }

                  if (last && !tooWide) {
                     lineToPrint = lineToPrint + " " + word;
                  }

                  this.lines.add(new LineData(xIndex, yIndex, lineToPrint, page));
                  yIndex++;
                  actualTextX = 0;
                  if (newline) {
                     yIndex++;
                  }

                  lineToPrint = word.equals("<NEWLINE>") ? "" : word;
                  if (last && tooWide) {
                     this.linesFromPrinting++;
                     this.lines.add(new LineData(xIndex, yIndex, word, page));
                     yIndex++;
                  }
               }
            }

            this.maxPagesFromPrinting = page;
         } catch (Exception var25) {
            var25.printStackTrace();
         }
      } catch (Exception var26) {
         Citadel.LOGGER.warn("Could not load in page .txt from json from page, page: " + res);
      }
   }

   private static List<String> hardSplitToWidth(Font font, String word, int maxWidth) {
      if (!word.isEmpty() && font.width(word) > maxWidth) {
         List<String> out = new ArrayList<>();
         StringBuilder current = new StringBuilder();

         for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (current.length() > 0 && font.width(current.toString() + c) > maxWidth) {
               out.add(current.toString());
               current.setLength(0);
            }

            current.append(c);
         }

         if (current.length() > 0) {
            out.add(current.toString());
         }

         return out;
      } else {
         return Collections.singletonList(word);
      }
   }

   public void setEntityTooltip(String hoverText) {
      this.entityTooltip = hoverText;
   }

   public ResourceLocation getBookButtonsTexture() {
      return BOOK_BUTTONS_TEXTURE;
   }
}
