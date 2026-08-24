package me.shedaniel.clothconfig2.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import me.shedaniel.clothconfig2.ClothConfigInitializer;
import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.api.ConfigScreen;
import me.shedaniel.clothconfig2.api.Modifier;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import me.shedaniel.clothconfig2.api.TickableWidget;
import me.shedaniel.clothconfig2.api.Tooltip;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import me.shedaniel.math.Rectangle;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix4f;

public abstract class AbstractConfigScreen extends Screen implements ConfigScreen {
   protected static final ResourceLocation CONFIG_TEX = ResourceLocation.fromNamespaceAndPath("cloth-config2", "textures/gui/cloth_config.png");
   private final ResourceLocation backgroundLocation;
   protected boolean confirmSave;
   protected final Screen parent;
   private boolean alwaysShowTabs = false;
   private boolean transparentBackground = false;
   @Nullable
   private Component defaultFallbackCategory = null;
   public int selectedCategoryIndex = 0;
   private boolean editable = true;
   private KeyCodeEntry focusedBinding;
   private ModifierKeyCode startedKeyCode = null;
   private final List<Tooltip> tooltips = Lists.newArrayList();
   @Nullable
   private Runnable savingRunnable = null;
   @Nullable
   protected Consumer<Screen> afterInitConsumer = null;

   protected AbstractConfigScreen(Screen parent, Component title, ResourceLocation backgroundLocation) {
      super(title);
      this.parent = parent;
      this.backgroundLocation = backgroundLocation;
   }

   public List<GuiEventListener> childrenL() {
      return super.children();
   }

   @Override
   public void setSavingRunnable(@Nullable Runnable savingRunnable) {
      this.savingRunnable = savingRunnable;
   }

   @Override
   public void setAfterInitConsumer(@Nullable Consumer<Screen> afterInitConsumer) {
      this.afterInitConsumer = afterInitConsumer;
   }

   @Override
   public ResourceLocation getBackgroundLocation() {
      return this.backgroundLocation;
   }

   @Override
   public boolean isRequiresRestart() {
      for (List<AbstractConfigEntry<?>> entries : this.getCategorizedEntries().values()) {
         for (AbstractConfigEntry<?> entry : entries) {
            if (entry.getConfigError().isEmpty() && entry.isEdited() && entry.isRequiresRestart()) {
               return true;
            }
         }
      }

      return false;
   }

   public abstract Map<Component, List<AbstractConfigEntry<?>>> getCategorizedEntries();

   @Override
   public boolean isEdited() {
      for (List<AbstractConfigEntry<?>> entries : this.getCategorizedEntries().values()) {
         for (AbstractConfigEntry<?> entry : entries) {
            if (entry.isEdited()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean isShowingTabs() {
      return this.isAlwaysShowTabs() || this.getCategorizedEntries().size() > 1;
   }

   public boolean isAlwaysShowTabs() {
      return this.alwaysShowTabs;
   }

   @Internal
   public void setAlwaysShowTabs(boolean alwaysShowTabs) {
      this.alwaysShowTabs = alwaysShowTabs;
   }

   public boolean isTransparentBackground() {
      return this.transparentBackground;
   }

   @Internal
   public void setTransparentBackground(boolean transparentBackground) {
      this.transparentBackground = transparentBackground;
   }

   public Component getFallbackCategory() {
      return this.defaultFallbackCategory != null ? this.defaultFallbackCategory : this.getCategorizedEntries().keySet().iterator().next();
   }

   @Internal
   public void setFallbackCategory(@Nullable Component defaultFallbackCategory) {
      this.defaultFallbackCategory = defaultFallbackCategory;
      List<Component> categories = Lists.newArrayList(this.getCategorizedEntries().keySet());

      for (int i = 0; i < categories.size(); i++) {
         Component category = categories.get(i);
         if (category.equals(this.getFallbackCategory())) {
            this.selectedCategoryIndex = i;
            break;
         }
      }
   }

   @Override
   public void saveAll(boolean openOtherScreens) {
      for (List<AbstractConfigEntry<?>> entries : Lists.newArrayList(this.getCategorizedEntries().values())) {
         for (AbstractConfigEntry<?> entry : entries) {
            entry.save();
         }
      }

      this.save();
      if (openOtherScreens) {
         if (this.isRequiresRestart()) {
            this.minecraft.setScreen(new ClothRequiresRestartScreen(this.parent));
         } else {
            this.minecraft.setScreen(this.parent);
         }
      }
   }

   public void save() {
      Optional.ofNullable(this.savingRunnable).ifPresent(Runnable::run);
   }

   public boolean isEditable() {
      return this.editable;
   }

   @Internal
   public void setEditable(boolean editable) {
      this.editable = editable;
   }

   @Internal
   public void setConfirmSave(boolean confirmSave) {
      this.confirmSave = confirmSave;
   }

   public KeyCodeEntry getFocusedBinding() {
      return this.focusedBinding;
   }

   @Internal
   public void setFocusedBinding(KeyCodeEntry focusedBinding) {
      this.focusedBinding = focusedBinding;
      if (focusedBinding != null) {
         this.startedKeyCode = this.focusedBinding.getValue();
         this.startedKeyCode.setKeyCodeAndModifier(InputConstants.UNKNOWN, Modifier.none());
      } else {
         this.startedKeyCode = null;
      }
   }

   public boolean mouseReleased(double double_1, double double_2, int int_1) {
      if (this.focusedBinding != null && this.startedKeyCode != null && !this.startedKeyCode.isUnknown() && this.focusedBinding.isAllowMouse()) {
         this.focusedBinding.setValue(this.startedKeyCode);
         this.setFocusedBinding(null);
         return true;
      } else {
         return super.mouseReleased(double_1, double_2, int_1);
      }
   }

   public boolean keyReleased(int int_1, int int_2, int int_3) {
      if (this.focusedBinding != null && this.startedKeyCode != null && this.focusedBinding.isAllowKey()) {
         this.focusedBinding.setValue(this.startedKeyCode);
         this.setFocusedBinding(null);
         return true;
      } else {
         return super.keyReleased(int_1, int_2, int_3);
      }
   }

   public boolean mouseClicked(double double_1, double double_2, int int_1) {
      if (this.focusedBinding != null && this.startedKeyCode != null && this.focusedBinding.isAllowMouse()) {
         if (this.startedKeyCode.isUnknown()) {
            this.startedKeyCode.setKeyCode(Type.MOUSE.getOrCreate(int_1));
         } else if (this.focusedBinding.isAllowModifiers() && this.startedKeyCode.getType() == Type.KEYSYM) {
            int code = this.startedKeyCode.getKeyCode().getValue();
            if (Minecraft.ON_OSX ? code == 343 || code == 347 : code == 341 || code == 345) {
               Modifier modifier = this.startedKeyCode.getModifier();
               this.startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), true, modifier.hasShift()));
               this.startedKeyCode.setKeyCode(Type.MOUSE.getOrCreate(int_1));
               return true;
            }

            if (code == 344 || code == 340) {
               Modifier modifier = this.startedKeyCode.getModifier();
               this.startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), modifier.hasControl(), true));
               this.startedKeyCode.setKeyCode(Type.MOUSE.getOrCreate(int_1));
               return true;
            }

            if (code == 342 || code == 346) {
               Modifier modifier = this.startedKeyCode.getModifier();
               this.startedKeyCode.setModifier(Modifier.of(true, modifier.hasControl(), modifier.hasShift()));
               this.startedKeyCode.setKeyCode(Type.MOUSE.getOrCreate(int_1));
               return true;
            }
         }

         return true;
      } else {
         return this.focusedBinding != null ? true : super.mouseClicked(double_1, double_2, int_1);
      }
   }

   public boolean keyPressed(int int_1, int int_2, int int_3) {
      if (this.focusedBinding != null && (this.focusedBinding.isAllowKey() || int_1 == 256)) {
         if (int_1 != 256) {
            if (this.startedKeyCode.isUnknown()) {
               this.startedKeyCode.setKeyCode(InputConstants.getKey(int_1, int_2));
            } else if (this.focusedBinding.isAllowModifiers()) {
               if (this.startedKeyCode.getType() == Type.KEYSYM) {
                  int code = this.startedKeyCode.getKeyCode().getValue();
                  if (Minecraft.ON_OSX ? code == 343 || code == 347 : code == 341 || code == 345) {
                     Modifier modifier = this.startedKeyCode.getModifier();
                     this.startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), true, modifier.hasShift()));
                     this.startedKeyCode.setKeyCode(InputConstants.getKey(int_1, int_2));
                     return true;
                  }

                  if (code == 344 || code == 340) {
                     Modifier modifier = this.startedKeyCode.getModifier();
                     this.startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), modifier.hasControl(), true));
                     this.startedKeyCode.setKeyCode(InputConstants.getKey(int_1, int_2));
                     return true;
                  }

                  if (code == 342 || code == 346) {
                     Modifier modifier = this.startedKeyCode.getModifier();
                     this.startedKeyCode.setModifier(Modifier.of(true, modifier.hasControl(), modifier.hasShift()));
                     this.startedKeyCode.setKeyCode(InputConstants.getKey(int_1, int_2));
                     return true;
                  }
               }

               if (Minecraft.ON_OSX ? int_1 == 343 || int_1 == 347 : int_1 == 341 || int_1 == 345) {
                  Modifier modifier = this.startedKeyCode.getModifier();
                  this.startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), true, modifier.hasShift()));
                  return true;
               }

               if (int_1 == 344 || int_1 == 340) {
                  Modifier modifier = this.startedKeyCode.getModifier();
                  this.startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), modifier.hasControl(), true));
                  return true;
               }

               if (int_1 == 342 || int_1 == 346) {
                  Modifier modifier = this.startedKeyCode.getModifier();
                  this.startedKeyCode.setModifier(Modifier.of(true, modifier.hasControl(), modifier.hasShift()));
                  return true;
               }
            }
         } else {
            this.focusedBinding.setValue(ModifierKeyCode.unknown());
            this.setFocusedBinding(null);
         }

         return true;
      } else if (this.focusedBinding != null && int_1 != 256) {
         return true;
      } else {
         return int_1 == 256 && this.shouldCloseOnEsc() ? this.quit() : super.keyPressed(int_1, int_2, int_3);
      }
   }

   protected final boolean quit() {
      if (this.confirmSave && this.isEdited()) {
         this.minecraft
            .setScreen(
               new ConfirmScreen(
                  new AbstractConfigScreen.QuitSaveConsumer(),
                  Component.translatable("text.cloth-config.quit_config"),
                  Component.translatable("text.cloth-config.quit_config_sure"),
                  Component.translatable("text.cloth-config.quit_discard"),
                  Component.translatable("gui.cancel")
               )
            );
      } else {
         this.minecraft.setScreen(this.parent);
      }

      return true;
   }

   public void tick() {
      super.tick();
      boolean edited = this.isEdited();
      Optional.ofNullable(this.getQuitButton())
         .ifPresent(button -> button.setMessage(edited ? Component.translatable("text.cloth-config.cancel_discard") : Component.translatable("gui.cancel")));

      for (GuiEventListener child : this.children()) {
         if (child instanceof TickableWidget widget) {
            widget.tick();
         }
      }
   }

   @Nullable
   protected AbstractWidget getQuitButton() {
      return null;
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      super.render(graphics, mouseX, mouseY, delta);

      for (Tooltip tooltip : this.tooltips) {
         graphics.renderTooltip(Minecraft.getInstance().font, tooltip.getText(), tooltip.getX(), tooltip.getY());
      }

      this.tooltips.clear();
   }

   @Override
   public void addTooltip(Tooltip tooltip) {
      this.tooltips.add(tooltip);
   }

   protected void overlayBackground(GuiGraphics graphics, Rectangle rect, int red, int green, int blue, int startAlpha, int endAlpha) {
      this.overlayBackground(graphics.pose(), rect, red, green, blue, startAlpha, endAlpha);
   }

   protected void overlayBackground(PoseStack matrices, Rectangle rect, int red, int green, int blue, int startAlpha, int endAlpha) {
      this.overlayBackground(matrices.last().pose(), rect, red, green, blue, startAlpha, endAlpha);
   }

   protected void overlayBackground(Matrix4f matrix, Rectangle rect, int red, int green, int blue, int startAlpha, int endAlpha) {
      if (!this.isTransparentBackground()) {
         Tesselator tesselator = Tesselator.getInstance();
         BufferBuilder buffer = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
         RenderSystem.setShaderTexture(0, this.getBackgroundLocation());
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         buffer.addVertex(matrix, rect.getMinX(), rect.getMaxY(), 0.0F)
            .setUv(rect.getMinX() / 32.0F, rect.getMaxY() / 32.0F)
            .setColor(red, green, blue, endAlpha);
         buffer.addVertex(matrix, rect.getMaxX(), rect.getMaxY(), 0.0F)
            .setUv(rect.getMaxX() / 32.0F, rect.getMaxY() / 32.0F)
            .setColor(red, green, blue, endAlpha);
         buffer.addVertex(matrix, rect.getMaxX(), rect.getMinY(), 0.0F)
            .setUv(rect.getMaxX() / 32.0F, rect.getMinY() / 32.0F)
            .setColor(red, green, blue, startAlpha);
         buffer.addVertex(matrix, rect.getMinX(), rect.getMinY(), 0.0F)
            .setUv(rect.getMinX() / 32.0F, rect.getMinY() / 32.0F)
            .setColor(red, green, blue, startAlpha);
      }
   }

   public boolean handleComponentClicked(@Nullable Style style) {
      if (style == null) {
         return false;
      } else {
         ClickEvent clickEvent = style.getClickEvent();
         if (clickEvent != null && clickEvent.getAction() == Action.OPEN_URL) {
            try {
               URI uri = new URI(clickEvent.getValue());
               String string = uri.getScheme();
               if (string == null) {
                  throw new URISyntaxException(clickEvent.getValue(), "Missing protocol");
               }

               if (!string.equalsIgnoreCase("http") && !string.equalsIgnoreCase("https")) {
                  throw new URISyntaxException(clickEvent.getValue(), "Unsupported protocol: " + string.toLowerCase(Locale.ROOT));
               }

               Minecraft.getInstance().setScreen(new ConfirmLinkScreen(openInBrowser -> {
                  if (openInBrowser) {
                     Util.getPlatform().openUri(uri);
                  }

                  Minecraft.getInstance().setScreen(this);
               }, clickEvent.getValue(), true));
            } catch (URISyntaxException var5) {
               ClothConfigInitializer.LOGGER.error("Can't open url for {}", clickEvent, var5);
            }

            return true;
         } else {
            return super.handleComponentClicked(style);
         }
      }
   }

   private class QuitSaveConsumer implements BooleanConsumer {
      public void accept(boolean t) {
         if (!t) {
            AbstractConfigScreen.this.minecraft.setScreen(AbstractConfigScreen.this);
         } else {
            AbstractConfigScreen.this.minecraft.setScreen(AbstractConfigScreen.this.parent);
         }
      }
   }
}
