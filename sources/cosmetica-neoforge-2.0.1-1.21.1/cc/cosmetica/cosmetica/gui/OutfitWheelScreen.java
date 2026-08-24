package cc.cosmetica.cosmetica.gui;

import cc.cosmetica.core.api.CachedImage;
import cc.cosmetica.core.api.CosmeticaAPI;
import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.core.api.texture.CosmeticaTexture.AutoAnimate;
import cc.cosmetica.core.api.texture.CosmeticaTexture.Builder;
import cc.cosmetica.core.builtin.manager.SelfCosmeticManager;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.cosmetica.Cosmetica;
import cc.cosmetica.cosmetica.Keybinds;
import cc.cosmetica.cosmetica.gui.widget.ThumbnailCache;
import cc.cosmetica.cosmetica.mixin.keybinds.KeyMappingAccessor;
import cc.cosmetica.cosmetica.settings.CosmeticaSettings;
import cc.cosmetica.cosmetica.util.CosmeticaLogCategory;
import cc.cosmetica.cosmetica.util.Division;
import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.PolyBuilder;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.PolyBuilder.Mode;
import cc.cosmetica.kupe.impl.PoseCanvas;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import gg.cloaks.javaclient.api.OutfitsApi;
import gg.cloaks.javaclient.api.UsersApi;
import gg.cloaks.javaclient.model.Outfit;
import gg.cloaks.javaclient.model.OutfitAccessory;
import gg.cloaks.javaclient.model.PlayerResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public class OutfitWheelScreen extends Screen {
   private double page = 0.0;
   private double scaleFactor = 0.05;
   private long lastScaleTime = System.currentTimeMillis();
   private List<OutfitWheelScreen.OutfitOption> options;
   private static final ResourceLocation NO_OUTFIT_LOCATION = new ResourceKey("minecraft", "textures/item/barrier.png").toResourceLocation();
   private boolean calculatedStartPage = false;
   private static final Division<Integer> SECTORS = new Division<Integer>()
      .addSection(4.1887902047863905, 0)
      .addSection(5.235987755982989, 1)
      .addSection(6.283185307179586, 2)
      .addSection(0.0, 2)
      .addSection(1.0471975511965976, 3)
      .addSection(2.0943951023931953, 4)
      .addSection(3.141592653589793, 5);

   public OutfitWheelScreen() {
      super(Text.translatable("screens.cosmetica.wheel", new String[0]).toMinecraftComponent());
      this.options = Arrays.asList();
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      Canvas canvas = new PoseCanvas(graphics, this.minecraft, null, partialTick);
      double centreX = this.width / 2.0;
      double centreY = this.height / 2.0;
      double outerEdgeSize = this.getOuterEdgeRadius();
      double innerButtonSize = outerEdgeSize * 0.2;
      double innerEdgeSize = outerEdgeSize * 0.25;
      int titleHeight = this.getTitleHeight();
      Component title = this.getPageLabel();
      graphics.drawCenteredString(this.font, title, this.width / 2, titleHeight, 16777215);
      int[] pageChangeButton = new int[3];
      this.getPageButtonDimensions(pageChangeButton, titleHeight, title);
      int left = pageChangeButton[0];
      int right = pageChangeButton[1];
      int pcWidth = pageChangeButton[2];
      boolean previousPage = this.getLastPage() > 0;
      boolean hoveredY = mouseY >= titleHeight && mouseY <= titleHeight + 9 + 1;
      boolean hoveredPrevPage = hoveredY && mouseX >= left - pcWidth / 2 && mouseX <= left + pcWidth / 2 + 1;
      boolean hoveredNextPage = hoveredY && mouseX >= right - pcWidth / 2 && mouseX <= right + pcWidth / 2 + 1;
      graphics.drawCenteredString(
         this.font, Text.literal("<").toMinecraftComponent(), left, titleHeight, previousPage ? (hoveredPrevPage ? 8947848 : 16777215) : 8947848
      );
      graphics.drawCenteredString(
         this.font, Text.literal(">").toMinecraftComponent(), right, titleHeight, previousPage ? (hoveredNextPage ? 8947848 : 16777215) : 8947848
      );
      RenderSystem.enableBlend();
      RenderSystem.disableDepthTest();
      RenderSystem.defaultBlendFunc();
      int selectedButton = this.getSelectedButton(mouseX, mouseY, innerButtonSize, innerEdgeSize, outerEdgeSize);
      this.drawCircles(canvas, centreX, centreY, outerEdgeSize, innerButtonSize, innerEdgeSize, selectedButton);
      RenderSystem.disableBlend();
      this.drawThumbs(canvas, centreX, centreY, 0.5 * (outerEdgeSize + innerEdgeSize), (float)(0.5 * (outerEdgeSize - innerEdgeSize)));
      if (this.scaleFactor < 1.0) {
         long currentTime = System.currentTimeMillis();
         double diffTime = (currentTime - this.lastScaleTime) / 50.0;
         this.lastScaleTime = currentTime;
         double baseChangeRate = 0.3;
         double scaleChange = 0.3 - 0.003 * Math.exp(2.0 * this.scaleFactor);
         this.scaleFactor += scaleChange * diffTime;
         if (this.scaleFactor > 1.0) {
            this.scaleFactor = 1.0;
         }
      }
   }

   private void drawThumbs(Canvas canvas, double centreX, double centreY, double distance, float scale) {
      int nSectors = SECTORS.count();
      double theta = 6.283185307179586 / nSectors;
      int currentOutfitIndex = ((Optional)Cosmetica.SELECTED_OUTFIT_ID.peek()).map(this::indexOf).orElse(-1);

      for (int i = 0; i < nSectors; i++) {
         int index = i + this.getPage() * nSectors;
         if (index < this.options.size()) {
            double angle = theta * (i - 1.5);
            float x = (float)(centreX + distance * Math.cos(angle));
            float y = (float)(centreY + distance * Math.sin(angle));
            float x0 = x - scale / 2.0F;
            float y0 = y - scale / 2.0F;
            float x1 = x + scale / 2.0F;
            float y1 = y + scale / 2.0F;
            OutfitWheelScreen.OutfitOption outfit = this.options.get(index);
            canvas.setTexture(new ResourceKey(outfit.thumbnail.location));
            canvas.setTransparency(currentOutfitIndex != index && outfit.usable ? 0.8F : 0.5F);
            PolyBuilder builder = canvas.drawQuads(Mode.POSITION_TEXTURE);
            builder.vertex(x0, y1).uv(0.0F, 1.0F).endVertex();
            builder.vertex(x1, y1).uv(1.0F, 1.0F).endVertex();
            builder.vertex(x1, y0).uv(1.0F, 0.0F).endVertex();
            builder.vertex(x0, y0).uv(0.0F, 0.0F).endVertex();
            builder.build();
         }
      }

      if (!this.options.isEmpty()) {
         float x0 = (float)centreX - scale / 3.0F;
         float y0 = (float)centreY - scale / 3.0F;
         float x1 = (float)centreX + scale / 3.0F;
         float y1 = (float)centreY + scale / 3.0F;
         canvas.setTexture(new ResourceKey(NO_OUTFIT_LOCATION));
         canvas.setTransparency(0.8F);
         PolyBuilder builder = canvas.drawQuads(Mode.POSITION_TEXTURE);
         builder.vertex(x0, y1).uv(0.0F, 1.0F).endVertex();
         builder.vertex(x1, y1).uv(1.0F, 1.0F).endVertex();
         builder.vertex(x1, y0).uv(1.0F, 0.0F).endVertex();
         builder.vertex(x0, y0).uv(0.0F, 0.0F).endVertex();
         builder.build();
      }

      canvas.disableTransparency();
   }

   private void drawCircles(
      Canvas canvas, double centreX, double centreY, double outerEdgeSize, double innerButtonSize, double innerEdgeSize, int highlightedSector
   ) {
      PolyBuilder builder = canvas.drawTriangles(Mode.POSITION_COLOUR);
      int nOutfitSectors = SECTORS.count();
      int nRenderSectors = 64;
      double theta = 0.09817477042468103;
      float shade = highlightedSector == 8 ? 1.0F : 0.4F;

      for (int i = 0; i < 64; i++) {
         double angle = 0.09817477042468103 * i;
         this.drawRenderSector(builder, 0.09817477042468103, angle, centreX, centreY, innerButtonSize, shade);
      }

      int currentOutfitSector = this.indexOf(((Optional)Cosmetica.SELECTED_OUTFIT_ID.peek()).orElse("")) - this.getPage() * nOutfitSectors;

      for (int i = 0; i < 64; i++) {
         double angle = 0.09817477042468103 * i;
         int sector = SECTORS.get(angle);
         shade = sector == currentOutfitSector ? 0.0F : (sector == highlightedSector ? 1.0F : ((sector & 1) == 0 ? 0.3F : 0.4F));
         this.drawRenderArc(builder, 0.09817477042468103, angle, centreX, centreY, innerEdgeSize, outerEdgeSize, shade);
      }

      builder.build();
   }

   private void drawRenderSector(PolyBuilder builder, double theta, double angle, double centreX, double centreY, double radius, float shade) {
      builder.vertex(centreX, centreY).colour(shade, shade, shade, 0.5F).endVertex();
      builder.vertex(centreX + radius * Math.cos(angle + theta), centreY + radius * Math.sin(angle + theta)).colour(shade, shade, shade, 0.5F).endVertex();
      builder.vertex(centreX + radius * Math.cos(angle), centreY + radius * Math.sin(angle)).colour(shade, shade, shade, 0.5F).endVertex();
   }

   private void drawRenderArc(
      PolyBuilder builder, double theta, double angle, double centreX, double centreY, double innerRadius, double outerRadius, float shade
   ) {
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      double cosNext = Math.cos(angle + theta);
      double sinNext = Math.sin(angle + theta);
      builder.vertex(centreX + innerRadius * cos, centreY + innerRadius * sin).colour(shade, shade, shade, 0.5F).endVertex();
      builder.vertex(centreX + outerRadius * cosNext, centreY + outerRadius * sinNext).colour(shade, shade, shade, 0.5F).endVertex();
      builder.vertex(centreX + outerRadius * cos, centreY + outerRadius * sin).colour(shade, shade, shade, 0.5F).endVertex();
      builder.vertex(centreX + innerRadius * cosNext, centreY + innerRadius * sinNext).colour(shade, shade, shade, 0.5F).endVertex();
      builder.vertex(centreX + outerRadius * cosNext, centreY + outerRadius * sinNext).colour(shade, shade, shade, 0.5F).endVertex();
      builder.vertex(centreX + innerRadius * cos, centreY + innerRadius * sin).colour(shade, shade, shade, 0.5F).endVertex();
   }

   private int indexOf(String outfit) {
      for (int i = 0; i < this.options.size(); i++) {
         if (this.options.get(i).id.equals(outfit)) {
            return i;
         }
      }

      return -1;
   }

   public void tick() {
      this.options = CosmeticaAPI.isAuthenticated() ? (List)Cosmetica.OWN_OUTFITS.peek() : Arrays.asList();
      if (!this.calculatedStartPage) {
         this.calculatedStartPage = true;
         int page = 0;
         Cosmetics cosmetics = (Cosmetics)Cosmetica.OWN_COSMETICS.peek();
         if (cosmetics != null && cosmetics.getOutfitId().isPresent()) {
            int index = this.indexOf((String)cosmetics.getOutfitId().get());
            if (index != -1) {
               page = index / 6;
            }
         }

         this.page = page;
      }

      if (!CosmeticaSettings.TOGGLE_OUTFIT_WHEEL.get() && !isDown(Keybinds.SELECT_OUTFIT)) {
         this.onClose();
      }
   }

   public boolean keyPressed(int key, int scan, int mod) {
      Key k = InputConstants.getKey(key, scan);
      KeyMapping.set(k, true);
      KeyMapping.click(k);
      return true;
   }

   public boolean keyReleased(int key, int scan, int mod) {
      Key k = InputConstants.getKey(key, scan);
      KeyMapping.set(k, false);
      return true;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button != 0) {
         if (button == 1) {
            this.page = (int)this.page + 1;
            if (this.page > this.getLastPage()) {
               this.page = 0.0;
            }

            GuiUtils.playClick();
            return true;
         } else {
            return false;
         }
      } else {
         assert this.minecraft != null;

         double outerEdgeSize = this.getOuterEdgeRadius();
         double innerButtonSize = outerEdgeSize * 0.2;
         double innerEdgeSize = outerEdgeSize * 0.25;
         int titleHeight = this.getTitleHeight();
         boolean hoveredY = mouseY >= titleHeight && mouseY <= titleHeight + 9 + 1;
         if (hoveredY) {
            int[] measurements = new int[3];
            this.getPageButtonDimensions(measurements, titleHeight, this.getPageLabel());
            int left = measurements[0];
            int right = measurements[1];
            int pcWidth = measurements[2];
            boolean hoveredPrevPage = mouseX >= left - pcWidth / 2 && mouseX <= left + pcWidth / 2 + 1;
            boolean hoveredNextPage = mouseX >= right - pcWidth / 2 && mouseX <= right + pcWidth / 2 + 1;
            if (hoveredNextPage) {
               this.page = (int)this.page + 1;
               if (this.page > this.getLastPage()) {
                  this.page = 0.0;
               }

               GuiUtils.playClick();
               return true;
            }

            if (hoveredPrevPage) {
               this.page = (int)this.page - 1;
               if (this.page < 0.0) {
                  this.page = this.getLastPage();
               }

               GuiUtils.playClick();
               return true;
            }
         }

         int selectedButton = this.getSelectedButton((float)mouseX, (float)mouseY, innerButtonSize, innerEdgeSize, outerEdgeSize);
         if (selectedButton > -1) {
            if (selectedButton < 8) {
               int index = selectedButton + this.getPage() * SECTORS.count();
               if (index < this.options.size()) {
                  OutfitWheelScreen.OutfitOption outfit = this.options.get(index);
                  if (!outfit.id.equals(((Optional)Cosmetica.SELECTED_OUTFIT_ID.peek()).orElse(""))) {
                     GuiUtils.playClick();
                     outfit.equipAsync();
                  }
               }
            } else if (selectedButton == 8 && !this.options.isEmpty()) {
               GuiUtils.playClick();
               clearOutfit();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double delta) {
      int prevPage = this.getPage();
      this.page = (this.page - delta) % (this.getLastPage() + 1);
      if (this.page < 0.0) {
         this.page = this.getLastPage() + 1 + this.page;
      }

      if (prevPage != this.getPage()) {
         GuiUtils.playClick();
      }

      return true;
   }

   private int getLastPage() {
      return (this.options.size() - 1) / SECTORS.count();
   }

   private int getPage() {
      return (int)this.page;
   }

   private double getOuterEdgeRadius() {
      return this.scaleFactor * (Minecraft.getInstance().options.guiScale().get() == 4 ? this.height / 2.5 : this.height / 3.0);
   }

   private int getTitleHeight() {
      return this.height / 2 - (int)(Minecraft.getInstance().options.guiScale().get() == 4 ? this.height / 2.5 : this.height / 3.0) - 12;
   }

   private Component getPageLabel() {
      return Text.translatable("label.wheel.page", new String[]{String.valueOf(this.getPage() + 1), String.valueOf(this.getLastPage() + 1)})
         .toMinecraftComponent();
   }

   private void getPageButtonDimensions(int[] result, int titleHeight, Component title) {
      int titleWidth = this.font.width(title);
      int pcWidth = this.font.width(Text.literal("<").toMinecraftComponent());
      int leftPageChange = this.width / 2 - titleWidth / 2 - 12;
      int rightPageChange = this.width / 2 + titleWidth / 2 + 12;
      result[0] = leftPageChange;
      result[1] = rightPageChange;
      result[2] = pcWidth;
   }

   private int getSelectedButton(float mouseX, float mouseY, double innerButtonSize, double innerEdgeSize, double outerEdgeSize) {
      float[] mousePosPolar = rect2polar(mouseX - (float)(this.width / 2.0), mouseY - (float)(this.height / 2.0));
      if (mousePosPolar[0] < innerButtonSize) {
         return 8;
      } else {
         return mousePosPolar[0] >= innerEdgeSize && mousePosPolar[0] < outerEdgeSize ? SECTORS.get(mousePosPolar[1]) : -1;
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean shouldCloseOnEsc() {
      return CosmeticaSettings.TOGGLE_OUTFIT_WHEEL.get();
   }

   static void clearOutfit() {
      if (((Optional)Cosmetica.SELECTED_OUTFIT_ID.peek()).isPresent()) {
         CosmeticaAPI.outfits().requestAsync(OutfitsApi::unequip).thenAcceptAsync(user -> {
            SelfCosmeticManager.clear();
            Logging.getInstance().debug(CosmeticaLogCategory.GUI, "Unequipped Outfit successfully", new Object[0]);
         }, Minecraft.getInstance()).exceptionally(e -> {
            new RuntimeException("Outfits Controller Unequip", e).printStackTrace();
            return null;
         });
      }
   }

   private static boolean isDown(KeyMapping mapping) {
      if (mapping.isUnbound()) {
         return false;
      } else {
         Key key = ((KeyMappingAccessor)mapping).cosmetica$getKey();
         long window = Minecraft.getInstance().getWindow().getWindow();
         int value = key.getValue();
         if (key.getType() == Type.KEYSYM) {
            return GLFW.glfwGetKey(window, value) != 0;
         } else {
            return key.getType() == Type.MOUSE ? GLFW.glfwGetMouseButton(window, value) != 0 : false;
         }
      }
   }

   private static float[] rect2polar(float x, float y) {
      float r = (float)Math.sqrt(x * x + y * y);
      float theta = (float)Math.atan2(y, x);
      if (theta < 0.0F) {
         theta = (float)(theta + 6.283185307179586);
      }

      return new float[]{r, theta};
   }

   public static class OutfitOption {
      final String id;
      final String name;
      final CachedImage thumbnail;
      final boolean usable;
      final List<OutfitAccessory> accessories;
      final String capeId;
      final String elytraId;
      public static final CachedImage NO_OUTFIT_THUMBNAIL = new CachedImage(Cosmetica.FALLBACK_OUTFIT_TEXTURE, 0);

      public OutfitOption(Outfit outfit) {
         this.id = outfit.getId();
         this.name = outfit.getName();
         this.thumbnail = outfit.getThumbnail() == null
            ? NO_OUTFIT_THUMBNAIL
            : ThumbnailCache.getOrCreateImage(
               new Builder(outfit.getThumbnail() + "?width=276", Cosmetica.LOADING_TEXTURE)
                  .frames(8, 1)
                  .ignoreTilesheet(true)
                  .failToLoadTexture(Cosmetica.FALLBACK_OUTFIT_TEXTURE)
                  .autoAnimate(AutoAnimate.NEVER),
               false
            );
         this.usable = outfit.isUsable();
         this.accessories = outfit.getAccessories();
         this.capeId = outfit.getCloak() == null ? "" : outfit.getCloak().getId();
         this.elytraId = outfit.getElytra() == null ? "" : outfit.getElytra().getId();
      }

      void equipAsync() {
         Cosmetica.SELECTED_OUTFIT_ID.set(Optional.of(this.id));
         CosmeticaAPI.outfits()
            .requestAsync(api -> api.equip(this.id))
            .thenAcceptAsync(
               user -> {
                  if (!this.capeId.isEmpty() && !this.elytraId.isEmpty()) {
                     SelfCosmeticManager.update(new PlayerResponse().user(user).isUser(true));
                  } else {
                     CosmeticaAPI.users()
                        .requestAsync(UsersApi::getSelf)
                        .thenAcceptAsync(user_ -> SelfCosmeticManager.update(new PlayerResponse().user(user_).isUser(true)), Minecraft.getInstance())
                        .exceptionally(ex -> null);
                  }

                  Logging.getInstance().debug(CosmeticaLogCategory.GUI, "Equip Success!", new Object[0]);
               },
               Minecraft.getInstance()
            )
            .exceptionally(except -> {
               new RuntimeException("Outfits Controller Equip", except).printStackTrace();
               return null;
            });
      }
   }
}
