package io.wispforest.owo.mixin.itemgroup;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import io.wispforest.owo.itemgroup.gui.ItemGroupButtonWidget;
import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.util.CursorAdapter;
import io.wispforest.owo.util.pond.OwoCreativeInventoryScreenExtensions;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen.ItemPickerMenu;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Row;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({CreativeModeInventoryScreen.class})
public abstract class CreativeInventoryScreenMixin extends EffectRenderingInventoryScreen<ItemPickerMenu> implements OwoCreativeInventoryScreenExtensions {
   @Shadow
   private static CreativeModeTab selectedTab;
   @Unique
   private final List<ItemGroupButtonWidget> owoButtons = new ArrayList<>();
   @Unique
   private FeatureFlagSet enabledFeatures = null;
   @Unique
   private final CursorAdapter cursorAdapter = CursorAdapter.ofClientWindow();

   @Shadow
   protected abstract void init();

   @Shadow
   protected abstract boolean hasPermissions(Player var1);

   @Shadow
   protected abstract boolean canScroll();

   @Inject(
      method = {"<init>(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/flag/FeatureFlagSet;Z)V"},
      at = {@At("TAIL")}
   )
   private void captureFeatures(LocalPlayer player, FeatureFlagSet enabledFeatures, boolean operatorTabEnabled, CallbackInfo ci) {
      this.enabledFeatures = enabledFeatures;
   }

   @ModifyArg(
      method = {"renderBg(Lnet/minecraft/client/gui/GuiGraphics;FII)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V",
         ordinal = 0
      )
   )
   private ResourceLocation injectCustomGroupTexture(ResourceLocation original) {
      return selectedTab instanceof OwoItemGroup owoGroup && owoGroup.owo$getBackgroundTexture() != null ? owoGroup.owo$getBackgroundTexture() : original;
   }

   @ModifyArgs(
      method = {"renderBg(Lnet/minecraft/client/gui/GuiGraphics;FII)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"
      )
   )
   private void injectCustomScrollbarTexture(Args args) {
      if (selectedTab instanceof OwoItemGroup owoGroup && owoGroup.getScrollerTextures() != null) {
         args.set(0, this.canScroll() ? owoGroup.getScrollerTextures().enabled() : owoGroup.getScrollerTextures().disabled());
      }
   }

   @ModifyArg(
      method = {"renderTabButton(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/item/CreativeModeTab;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"
      )
   )
   private ResourceLocation injectCustomTabTexture(ResourceLocation texture, @Local(argsOnly = true) CreativeModeTab group) {
      if (group instanceof OwoItemGroup contextGroup && contextGroup.getTabTextures() != null) {
         OwoItemGroup.TabTextures textures = contextGroup.getTabTextures();
         return contextGroup.row() == Row.TOP
            ? (
               selectedTab == contextGroup
                  ? (contextGroup.column() == 0 ? textures.topSelectedFirstColumn() : textures.topSelected())
                  : textures.topUnselected()
            )
            : (
               selectedTab == contextGroup
                  ? (contextGroup.column() == 0 ? textures.bottomSelectedFirstColumn() : textures.bottomSelected())
                  : textures.bottomUnselected()
            );
      } else {
         return texture;
      }
   }

   @Inject(
      method = {"renderTabButton(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/item/CreativeModeTab;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/CreativeModeTab;getIconItem()Lnet/minecraft/world/item/ItemStack;"
      )},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private void renderOwoIcon(GuiGraphics context, CreativeModeTab group, CallbackInfo ci, boolean bl, boolean bl2, int i, int j, int k) {
      if (group instanceof OwoItemGroup owoGroup) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         owoGroup.icon().render(context, j, k, 0, 0, 0.0F);
         RenderSystem.disableBlend();
      }
   }

   @ModifyArg(
      method = {"renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I"
      )
   )
   private Component injectTabNameAsTitle(Component original) {
      if (selectedTab instanceof OwoItemGroup owoGroup && owoGroup.hasDynamicTitle() && owoGroup.selectedTabs().size() == 1) {
         ItemGroupTab singleActiveTab = owoGroup.getTab(owoGroup.selectedTabs().iterator().nextInt());
         return (Component)(singleActiveTab.primary()
            ? singleActiveTab.name()
            : Component.translatable("text.owo.itemGroup.tab_template", new Object[]{owoGroup.getDisplayName(), singleActiveTab.name()}));
      } else {
         return original;
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"selectTab(Lnet/minecraft/world/item/CreativeModeTab;)V"}
   )
   private void setSelectedTab(CreativeModeTab group, CallbackInfo ci) {
      this.owoButtons.forEach(x$0 -> this.removeWidget(x$0));
      this.owoButtons.clear();
      if (group instanceof OwoItemGroup owoGroup) {
         int tabRootY = this.topPos;
         int tabStackHeight = owoGroup.getTabStackHeight();
         tabRootY -= 13 * (tabStackHeight - 4);
         if (owoGroup.shouldDisplaySingleTab() || owoGroup.tabs.size() > 1) {
            for (int tabIdx = 0; tabIdx < owoGroup.tabs.size(); tabIdx++) {
               ItemGroupTab tab = owoGroup.tabs.get(tabIdx);
               int xOffset = this.leftPos - 27 - tabIdx / tabStackHeight * 26;
               int yOffset = tabRootY + 10 + tabIdx % tabStackHeight * 30;
               ItemGroupButtonWidget tabButton = new ItemGroupButtonWidget(xOffset, yOffset, 32, tab, this.owo$createSelectAction(owoGroup, tabIdx));
               if (owoGroup.isTabSelected(tabIdx)) {
                  tabButton.isSelected = true;
               }

               this.owoButtons.add(tabButton);
               this.addRenderableWidget(tabButton);
            }
         }

         int buttonStackHeight = owoGroup.getButtonStackHeight();
         tabRootY = this.topPos - 13 * (buttonStackHeight - 4);
         List<ItemGroupButton> buttons = owoGroup.getButtons();

         for (int i = 0; i < buttons.size(); i++) {
            ItemGroupButton buttonDefinition = buttons.get(i);
            int xOffset = this.leftPos + 198 + i / buttonStackHeight * 26;
            int yOffset = tabRootY + 10 + i % buttonStackHeight * 30;
            ItemGroupButtonWidget tabButton = new ItemGroupButtonWidget(xOffset, yOffset, 0, buttonDefinition, __ -> buttonDefinition.action().run());
            this.owoButtons.add(tabButton);
            this.addRenderableWidget(tabButton);
         }
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"}
   )
   private void render(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      boolean anyButtonHovered = false;

      for (ItemGroupButtonWidget button : this.owoButtons) {
         if (button.trulyHovered()) {
            context.renderComponentTooltip(
               this.font,
               button.isTab() && ((OwoItemGroup)selectedTab).canSelectMultipleTabs()
                  ? List.of(button.getMessage(), Component.translatable("text.owo.itemGroup.select_hint"))
                  : List.of(button.getMessage()),
               mouseX,
               mouseY
            );
            anyButtonHovered = true;
         }
      }

      this.cursorAdapter.applyStyle(anyButtonHovered ? CursorStyle.HAND : CursorStyle.NONE);
   }

   @Inject(
      method = {"removed()V"},
      at = {@At("HEAD")}
   )
   private void disposeCursorAdapter(CallbackInfo ci) {
      this.cursorAdapter.dispose();
   }

   @Override
   public int owo$getRootX() {
      return this.leftPos;
   }

   @Override
   public int owo$getRootY() {
      return this.topPos;
   }

   @Unique
   private Consumer<ItemGroupButtonWidget> owo$createSelectAction(OwoItemGroup group, int tabIdx) {
      return button -> {
         ItemDisplayParameters context = new ItemDisplayParameters(
            this.enabledFeatures, this.hasPermissions(((ItemPickerMenu)this.menu).player()), ((ItemPickerMenu)this.menu).player().level().registryAccess()
         );
         if (Screen.hasShiftDown()) {
            group.toggleTab(tabIdx, context);
         } else {
            group.selectSingleTab(tabIdx, context);
         }

         this.rebuildWidgets();
         button.isSelected = true;
      };
   }

   public CreativeInventoryScreenMixin(ItemPickerMenu screenHandler, Inventory playerInventory, Component text) {
      super(screenHandler, playerInventory, text);
   }
}
