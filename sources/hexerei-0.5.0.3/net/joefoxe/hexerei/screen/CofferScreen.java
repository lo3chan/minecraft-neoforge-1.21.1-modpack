package net.joefoxe.hexerei.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.Coffer;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.container.CofferContainer;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.CofferCycleWhitelistButtonToServer;
import net.joefoxe.hexerei.util.message.CofferInvCycleWhitelistButtonToServer;
import net.joefoxe.hexerei.util.message.CofferInvUpdateWhitelistToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraft.world.item.component.CustomData;

public class CofferScreen extends AbstractContainerScreen<CofferContainer> {
   private final ResourceLocation GUI = HexereiUtil.getResource("textures/gui/coffer_gui.png");
   private final ResourceLocation INVENTORY = HexereiUtil.getResource("textures/gui/inventory.png");
   public int mouseX;
   public int mouseY;

   public CofferScreen(CofferContainer screenContainer, Inventory inv, Component titleIn) {
      super(screenContainer, inv, titleIn);
      this.titleLabelY = -27;
      this.titleLabelX = 4;
      this.inventoryLabelY = 107;
      this.mouseX = 0;
      this.mouseY = 0;
   }

   protected void init() {
      super.init();
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      this.mouseX = mouseX;
      this.mouseY = mouseY;
      this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
      super.render(guiGraphics, mouseX, mouseY, partialTicks);
      this.renderTooltip(guiGraphics, mouseX, mouseY);
      this.renderButtonTooltip(guiGraphics, mouseX, mouseY);
   }

   public Component getTitle() {
      return super.getTitle();
   }

   public boolean getToggled() {
      return ((CofferContainer)this.menu).getToggled() == 1;
   }

   protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int i = this.leftPos;
      int j = this.topPos - 28;
      guiGraphics.blit(this.GUI, i, j - 3, 0, 0, 214, 157);
      guiGraphics.blit(this.GUI, i + 94, j - 30, 230, 0, 26, 26);
      if (((CofferContainer)this.menu).getToggled() == 1) {
         guiGraphics.blit(this.GUI, i + 188, j + 130, 230, 44, 18, 18);
         guiGraphics.blit(this.GUI, i + 211, j + 62, 2, 159, 62, 62);
         CofferTile.WhitelistMode mode = CofferTile.WhitelistMode.WHITELIST_INV;
         if (((CofferContainer)this.getMenu()).inWorld) {
            if (((CofferContainer)this.menu).tileEntity instanceof CofferTile coffer) {
               mode = coffer.mode;
            }
         } else {
            ItemStack handStack = ItemStack.EMPTY;
            if (Inventory.isHotbarSlot(((CofferContainer)this.menu).slotIndex)) {
               if (Minecraft.getInstance().player.getInventory().selected == ((CofferContainer)this.menu).slotIndex) {
                  handStack = Minecraft.getInstance().player.getMainHandItem();
               }
            } else {
               handStack = Minecraft.getInstance().player.getOffhandItem();
            }

            CompoundTag tag = ((CustomData)handStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
            if (tag.contains("WhitelistMode")) {
               mode = CofferTile.WhitelistMode.byId(tag.getInt("WhitelistMode"));
            }
         }

         guiGraphics.blit(this.GUI, i + 216, j + 126, 65 + 18 * mode.ordinal(), 159, 18, 18);

         for (int i2 = 0; i2 < 9; i2++) {
            int ix = i2 % 3;
            int iy = i2 / 3;
            if (this.hovering(216 + ix * 18, 66 + iy * 18, 18.0, 18.0)) {
               renderSlotHighlight(guiGraphics, i + 216 + ix * 18, j + 66 + iy * 18 + 1, 0);
            }
         }
      }

      guiGraphics.blit(this.INVENTORY, i + 3, j + 129, 0, 0, 176, 100);
      Minecraft minecraft = Minecraft.getInstance();
      RenderSystem.disableDepthTest();
      guiGraphics.renderItem(new ItemStack(((Coffer)ModBlocks.COFFER.get()).asItem()), this.leftPos + 99, this.topPos - 25 - 28);
      if (((CofferContainer)this.menu).getToggled() == 1) {
         for (int i2x = 0; i2x < 9; i2x++) {
            ItemStack stackInSlot = ItemStack.EMPTY;
            if (((CofferContainer)this.getMenu()).inWorld) {
               if (((CofferContainer)this.menu).tileEntity instanceof CofferTile coffer) {
                  stackInSlot = ((ItemStack)coffer.whitelist.get(i2x)).copy();
               }
            } else {
               ItemStack handStackx = ItemStack.EMPTY;
               if (Inventory.isHotbarSlot(((CofferContainer)this.menu).slotIndex)) {
                  if (Minecraft.getInstance().player.getInventory().selected == ((CofferContainer)this.menu).slotIndex) {
                     handStackx = Minecraft.getInstance().player.getMainHandItem();
                  }
               } else {
                  handStackx = Minecraft.getInstance().player.getOffhandItem();
               }

               CompoundTag tag = ((CustomData)handStackx.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
               if (tag.contains("WhitelistItems", 9)) {
                  ListTag itemsTag = tag.getList("WhitelistItems", 10);
                  if (!itemsTag.getCompound(i2x).getCompound("Item").isEmpty()) {
                     stackInSlot = ItemStack.parse(Hexerei.DynamicRegistries.get(), itemsTag.getCompound(i2x).getCompound("Item")).orElse(ItemStack.EMPTY);
                  }
               }
            }

            int ix = i2x % 3;
            int iy = i2x / 3;
            guiGraphics.renderItem(stackInSlot, i + 216 + ix * 18, j + 67 + iy * 18);
         }
      }

      if (minecraft.player != null) {
         InventoryScreen.renderEntityInInventoryFollowsMouse(
            guiGraphics, i + 107 - 26, j + 8 + 35, i + 107 + 26, j + 78 + 15, 22, 0.0625F, x, y, minecraft.player
         );
      }

      RenderSystem.enableDepthTest();
   }

   protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
      boolean insideWhitelist = this.hovering(211.0, 62.0, 62.0, 62.0);
      boolean insideWhitelistButton = this.hovering(216.0, 126.0, 18.0, 18.0);
      boolean insideCoffer = this.hovering(0.0, -3.0, 214.0, 157.0);
      return super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop, mouseButton) && !insideWhitelist && !insideWhitelistButton && !insideCoffer;
   }

   public boolean mouseClicked(double x, double y, int button) {
      boolean mouseClicked = super.mouseClicked(x, y, button);
      if (((CofferContainer)this.menu).getToggled() == 1) {
         if (this.hovering(216.0, 126.0, 18.0, 18.0)) {
            if (((CofferContainer)this.menu).inWorld) {
               if (((CofferContainer)this.menu).tileEntity instanceof CofferTile coffer) {
                  HexereiPacketHandler.sendToServer(new CofferCycleWhitelistButtonToServer(coffer));
               }
            } else {
               HexereiPacketHandler.sendToServer(new CofferInvCycleWhitelistButtonToServer(((CofferContainer)this.menu).slotIndex));
            }

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }

         for (int i2 = 0; i2 < 9; i2++) {
            int ix = i2 % 3;
            int iy = i2 / 3;
            if (this.hovering(216 + ix * 18, 66 + iy * 18, 18.0, 18.0)) {
               if (!((CofferContainer)this.menu).getCarried().isEmpty()) {
                  ItemStack stack = ((CofferContainer)this.menu).getCarried();
                  if (!((List)HexConfig.COFFER_BLACKLIST.get()).contains(HexereiUtil.getRegistryName(stack.getItem()).toString())
                     && !stack.is(ModItems.COFFER)
                     && !stack.is(ModItems.ENTANGLED_COFFER)) {
                     if (((CofferContainer)this.menu).inWorld) {
                        if (((CofferContainer)this.menu).tileEntity instanceof CofferTile cofferTile) {
                           cofferTile.setWhitelistSlot(i2, ((CofferContainer)this.menu).getCarried().copyWithCount(1));
                        }
                     } else {
                        HexereiPacketHandler.sendToServer(
                           new CofferInvUpdateWhitelistToServer(
                              ((CofferContainer)this.menu).slotIndex, i2, ((CofferContainer)this.menu).getCarried().copyWithCount(1)
                           )
                        );
                     }
                  }

                  Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
               } else {
                  if (((CofferContainer)this.menu).inWorld) {
                     if (((CofferContainer)this.menu).tileEntity instanceof CofferTile cofferTile) {
                        cofferTile.setWhitelistSlot(i2, ItemStack.EMPTY);
                     }
                  } else {
                     HexereiPacketHandler.sendToServer(new CofferInvUpdateWhitelistToServer(((CofferContainer)this.menu).slotIndex, i2, ItemStack.EMPTY));
                  }

                  Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
               }
            }
         }
      }

      if (x > this.leftPos + 188 && x < this.leftPos + 188 + 18 && y > this.topPos + 129 - 28 && y < this.topPos + 129 + 18 - 28) {
         ((CofferContainer)this.menu).setToggled(1 - ((CofferContainer)this.menu).getToggled());
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      }

      return mouseClicked;
   }

   public boolean hovering(double x, double y, double width, double height) {
      return this.mouseX >= this.leftPos + x
         && this.mouseX < this.leftPos + x + width
         && this.mouseY >= this.topPos - 28 + y
         && this.mouseY < this.topPos - 28 + y + height;
   }

   public void renderButtonTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      List<Component> components = new ArrayList<>();
      if (this.hovering(188.0, 130.0, 18.0, 18.0)) {
         components.add(Component.translatable("tooltip.hexerei.gather_to_here_button"));
         if (Screen.hasShiftDown()) {
            components.add(
               Component.translatable(
                     "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                  )
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         } else {
            components.add(
               Component.translatable(
                     "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                  )
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
         }

         guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
      }

      components = new ArrayList<>();
      if (this.hovering(216.0, 126.0, 18.0, 18.0)) {
         CofferTile.WhitelistMode mode = CofferTile.WhitelistMode.WHITELIST_INV;
         if (((CofferContainer)this.getMenu()).inWorld) {
            if (((CofferContainer)this.menu).tileEntity instanceof CofferTile coffer) {
               mode = coffer.mode;
            }
         } else {
            ItemStack handStack = ItemStack.EMPTY;
            if (Inventory.isHotbarSlot(((CofferContainer)this.menu).slotIndex)) {
               if (Minecraft.getInstance().player.getInventory().selected == ((CofferContainer)this.menu).slotIndex) {
                  handStack = Minecraft.getInstance().player.getMainHandItem();
               }
            } else {
               handStack = Minecraft.getInstance().player.getOffhandItem();
            }

            CompoundTag tag = ((CustomData)handStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
            if (tag.contains("WhitelistMode")) {
               mode = CofferTile.WhitelistMode.byId(tag.getInt("WhitelistMode"));
            }
         }

         components.add(Component.translatable("tooltip.hexerei.coffer_" + mode.getName()));
         components.add(Component.translatable("tooltip.hexerei.coffer_whitelist_match").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         components.add(Component.translatable("tooltip.hexerei.coffer_" + mode.getName() + "_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232))));
         guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
      }

      for (int i2 = 0; i2 < 9; i2++) {
         int ix = i2 % 3;
         int iy = i2 / 3;
         if (this.hovering(216 + ix * 18, 66 + iy * 18, 18.0, 18.0)) {
            ItemStack stackInSlot = ItemStack.EMPTY;
            if (((CofferContainer)this.getMenu()).inWorld) {
               if (((CofferContainer)this.menu).tileEntity instanceof CofferTile coffer) {
                  stackInSlot = ((ItemStack)coffer.whitelist.get(i2)).copy();
               }
            } else {
               ItemStack handStackx = ItemStack.EMPTY;
               if (Inventory.isHotbarSlot(((CofferContainer)this.menu).slotIndex)) {
                  if (Minecraft.getInstance().player.getInventory().selected == ((CofferContainer)this.menu).slotIndex) {
                     handStackx = Minecraft.getInstance().player.getMainHandItem();
                  }
               } else {
                  handStackx = Minecraft.getInstance().player.getOffhandItem();
               }

               CompoundTag tag = ((CustomData)handStackx.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
               if (tag.contains("WhitelistItems", 9)) {
                  ListTag itemsTag = tag.getList("WhitelistItems", 10);
                  if (!itemsTag.getCompound(i2).getCompound("Item").isEmpty()) {
                     stackInSlot = ItemStack.parse(Hexerei.DynamicRegistries.get(), itemsTag.getCompound(i2).getCompound("Item")).orElse(ItemStack.EMPTY);
                  }
               }
            }

            if (!stackInSlot.isEmpty()) {
               List<Component> tooltip = stackInSlot.getTooltipLines(
                  TooltipContext.EMPTY, Hexerei.proxy.getPlayer(), Minecraft.getInstance().options.advancedItemTooltips ? Default.ADVANCED : Default.NORMAL
               );
               guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltip, Optional.empty(), mouseX, mouseY);
            }
         }
      }
   }
}
