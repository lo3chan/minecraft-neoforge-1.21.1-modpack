package com.aetherteam.aether.client.gui.screen.perks;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.client.gui.component.skins.ChangeSkinButton;
import com.aetherteam.aether.client.gui.component.skins.PatreonButton;
import com.aetherteam.aether.client.gui.component.skins.RefreshButton;
import com.aetherteam.aether.data.resources.registries.AetherMoaTypes;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.entity.passive.Moa;
import com.aetherteam.aether.network.packet.serverbound.ServerMoaSkinPacket;
import com.aetherteam.aether.perk.CustomizationsOptions;
import com.aetherteam.aether.perk.data.ClientMoaSkinPerkData;
import com.aetherteam.aether.perk.types.MoaData;
import com.aetherteam.aether.perk.types.MoaSkins;
import com.aetherteam.nitrogen.api.users.User;
import com.aetherteam.nitrogen.api.users.UserData.Client;
import com.aetherteam.nitrogen.network.packet.serverbound.TriggerUpdateInfoPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MoaSkinsScreen extends Screen {
   public static final ResourceLocation MOA_SKINS_GUI = ResourceLocation.fromNamespaceAndPath("aether", "textures/gui/perks/skins/skins.png");
   public static final ResourceLocation LOCK_SPRITE = ResourceLocation.fromNamespaceAndPath("aether", "skins/lock");
   public static final ResourceLocation SLOT_SELECTED_SPRITE = ResourceLocation.fromNamespaceAndPath("aether", "skins/slot_selected");
   public static final WidgetSprites SLOT_WIDGET = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("aether", "skins/slot"),
      ResourceLocation.fromNamespaceAndPath("aether", "skins/slot_disabled"),
      ResourceLocation.fromNamespaceAndPath("aether", "skins/slot_highlighted")
   );
   public static final WidgetSprites SCROLL_WIDGET = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("aether", "skins/scroll"),
      ResourceLocation.fromNamespaceAndPath("aether", "skins/scroll_disabled"),
      ResourceLocation.fromNamespaceAndPath("aether", "skins/scroll")
   );
   public static final WidgetSprites PERMANENT_WIDGET = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("aether", "skins/permanent"), ResourceLocation.fromNamespaceAndPath("aether", "skins/permanent_highlighted")
   );
   public static final WidgetSprites TEMPORARY_WIDGET = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("aether", "skins/temporary"), ResourceLocation.fromNamespaceAndPath("aether", "skins/temporary_highlighted")
   );
   private static final String PATREON_LINK = "https://www.patreon.com/TheAetherTeam";
   private static final String HELP_LINK = "https://github.com/The-Aether-Team/.github/wiki/Patreon-Guide";
   private final Screen lastScreen;
   private final int imageWidth = 176;
   private final int imageHeight = 184;
   private int leftPos;
   private int topPos;
   private final CustomizationsOptions customizations = CustomizationsOptions.INSTANCE;
   private List<MoaSkins.MoaSkin> moaSkins;
   private List<Float> snapPoints;
   private MoaSkins.MoaSkin selectedSkin;
   private ChangeSkinButton applyButton;
   private ChangeSkinButton removeButton;
   private boolean scrolling;
   private float scrollX;
   private float moaRotation = 0.0F;
   private Moa previewMoa;
   private boolean userConnectionExists = false;

   public MoaSkinsScreen(Screen lastScreen) {
      super(Component.translatable("gui.aether.moa_skins.title"));
      this.lastScreen = lastScreen;
   }

   public void init() {
      this.leftPos = (this.width - 176) / 2;
      this.topPos = (this.height - 184) / 2;
      User user = Client.getClientUser();
      this.userConnectionExists = user != null;
      this.moaSkins = List.copyOf(MoaSkins.getMoaSkins().values());
      this.snapPoints = new ArrayList<>();
      int remainingSlots = this.moaSkins.size() - this.maxSlots();

      for (int i = 0; i <= remainingSlots; i++) {
         this.snapPoints.add(this.scrollbarGutterWidth() / remainingSlots * i);
      }

      if (this.getMinecraft().player != null) {
         UUID uuid = this.getMinecraft().player.getUUID();
         Map<UUID, MoaData> userSkinsData = ClientMoaSkinPerkData.INSTANCE.getClientPerkData();
         if (this.getSelectedSkin() == null) {
            this.selectedSkin = userSkinsData.containsKey(uuid) ? userSkinsData.get(uuid).moaSkin() : (MoaSkins.MoaSkin)this.moaSkins.getFirst();
         }

         this.applyButton = (ChangeSkinButton)this.addRenderableWidget(
            new ChangeSkinButton(
               ChangeSkinButton.ButtonType.APPLY,
               Button.builder(
                     Component.translatable("gui.aether.moa_skins.button.apply"),
                     pressed -> {
                        PacketDistributor.sendToServer(
                           new ServerMoaSkinPacket.Apply(
                              this.getMinecraft().player.getUUID(),
                              new MoaData(
                                 ((AetherPlayerAttachment)this.getMinecraft().player.getData(AetherDataAttachments.AETHER_PLAYER)).getLastRiddenMoa(),
                                 this.getSelectedSkin()
                              )
                           ),
                           new CustomPacketPayload[0]
                        );
                        this.customizations.setMoaSkin(this.getSelectedSkin().getId());
                        this.customizations.save();
                        this.customizations.load();
                     }
                  )
                  .bounds(this.leftPos + 176 - 20, this.topPos + 13, 7, 7)
            )
         );
         this.removeButton = (ChangeSkinButton)this.addRenderableWidget(
            new ChangeSkinButton(ChangeSkinButton.ButtonType.REMOVE, Button.builder(Component.translatable("gui.aether.moa_skins.button.remove"), pressed -> {
               PacketDistributor.sendToServer(new ServerMoaSkinPacket.Remove(this.getMinecraft().player.getUUID()), new CustomPacketPayload[0]);
               this.customizations.setMoaSkin("");
               this.customizations.save();
               this.customizations.load();
            }).bounds(this.leftPos + 176 - 20, this.topPos + 22, 7, 7))
         );
         this.addRenderableWidget(new RefreshButton(Button.builder(Component.literal(""), pressed -> {
            if (RefreshButton.reboundTimer == 0) {
               PacketDistributor.sendToServer(new TriggerUpdateInfoPacket(this.getMinecraft().player.getId()), new CustomPacketPayload[0]);
               RefreshButton.reboundTimer = 1200;
            }
         }).bounds(this.leftPos + 7, this.topPos + 184 - 25, 18, 18).tooltip(Tooltip.create(Component.translatable("gui.aether.moa_skins.button.refresh")))));
         this.addRenderableWidget(
            new PatreonButton(
               Button.builder(
                     Component.translatable("gui.aether.moa_skins.button.donate"), pressed -> this.getMinecraft().setScreen(new ConfirmLinkScreen(callback -> {
                        if (callback) {
                           Util.getPlatform().openUri("https://www.patreon.com/TheAetherTeam");
                        }

                        this.getMinecraft().setScreen(this);
                     }, "https://www.patreon.com/TheAetherTeam", true))
                  )
                  .bounds(this.leftPos + 176 / 2 - 27, this.topPos + 184 - 25, 54, 18)
            )
         );
         this.addRenderableWidget(
            new PatreonButton(
               Button.builder(Component.literal("?"), pressed -> this.getMinecraft().setScreen(new ConfirmLinkScreen(callback -> {
                     if (callback) {
                        Util.getPlatform().openUri("https://github.com/The-Aether-Team/.github/wiki/Patreon-Guide");
                     }

                     this.getMinecraft().setScreen(this);
                  }, "https://github.com/The-Aether-Team/.github/wiki/Patreon-Guide", true)))
                  .bounds(this.leftPos + 176 / 2 + 63, this.topPos + 184 - 25, 18, 18)
                  .tooltip(Tooltip.create(Component.translatable("gui.aether.moa_skins.button.help"))),
               true
            )
         );
      }
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      super.render(guiGraphics, mouseX, mouseY, partialTicks);
      this.checkUserConnectionStatus();
   }

   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      this.renderTransparentBackground(guiGraphics);
      this.renderWindow(guiGraphics);
      this.renderSlots(guiGraphics, mouseX, mouseY);
      this.renderInterface(guiGraphics, mouseX, mouseY, partialTicks);
   }

   private void renderWindow(GuiGraphics guiGraphics) {
      User user = Client.getClientUser();
      Font font = this.getMinecraft().font;
      guiGraphics.blit(MOA_SKINS_GUI, this.leftPos, this.topPos, 0, 0, 176, 184);
      Component component = user == null
         ? Component.translatable("gui.aether.moa_skins.text.donate")
         : Component.translatable("gui.aether.moa_skins.text.reward");
      int y = this.topPos + 184 - 69 + font.wordWrapHeight(component, 176 - 20);

      for (FormattedCharSequence sequence : font.split(component, 176 - 20)) {
         guiGraphics.drawCenteredString(font, sequence, this.leftPos + 176 / 2, y, 16777215);
         y += 12;
      }
   }

   private void renderSlots(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      if (this.getMinecraft().player != null) {
         UUID uuid = this.getMinecraft().player.getUUID();
         Map<UUID, MoaData> userSkinsData = ClientMoaSkinPerkData.INSTANCE.getClientPerkData();
         User user = Client.getClientUser();
         List<MoaSkins.MoaSkin> visibleSkins = this.moaSkins.size() > this.maxSlots()
            ? this.moaSkins.subList(this.getSlotOffset(), this.getSlotOffset() + this.maxSlots())
            : this.moaSkins;
         int slotIndex = 0;

         for (MoaSkins.MoaSkin skin : visibleSkins) {
            int x = this.leftPos + 7 + slotIndex * 18;
            int y = this.topPos + 184 / 2 + 9;
            if (user == null || !skin.getUserPredicate().test(user) || skin == this.getSelectedSkin() || this.getSlotIndex(mouseX, mouseY) == slotIndex) {
               ResourceLocation location = SLOT_WIDGET.get(
                  user != null && skin.getUserPredicate().test(user), skin == this.getSelectedSkin() || this.getSlotIndex(mouseX, mouseY) == slotIndex
               );
               guiGraphics.blitSprite(location, x, y, 18, 18);
            }

            if (userSkinsData.containsKey(uuid) && userSkinsData.get(uuid).moaSkin() == skin) {
               guiGraphics.blitSprite(SLOT_SELECTED_SPRITE, x, y, 18, 18);
            }

            guiGraphics.blitSprite(skin.getIconLocation(), x + 1, y + 1, 16, 16);
            slotIndex++;
         }
      }

      this.renderScrollbar(guiGraphics);
      this.renderSlotTooltips(guiGraphics, mouseX, mouseY);
   }

   private void renderScrollbar(GuiGraphics guiGraphics) {
      int scrollbarTop = this.topPos + 184 / 2 + 29;
      int scrollbarLeft = this.leftPos + 8;
      ResourceLocation location = SCROLL_WIDGET.get(this.moaSkins.size() > this.maxSlots(), true);
      guiGraphics.blitSprite(location, (int)(scrollbarLeft + this.scrollX), scrollbarTop, 13, 6);
   }

   private void renderSlotTooltips(GuiGraphics guiGraphics, double mouseX, double mouseY) {
      MoaSkins.MoaSkin skin = this.getSkinFromSlot(mouseX, mouseY);
      if (skin != null) {
         Component name = skin.getDisplayName();
         guiGraphics.renderTooltip(this.getMinecraft().font, name, (int)mouseX, (int)mouseY);
      }
   }

   private void renderInterface(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      User user = Client.getClientUser();
      if (user != null && this.getSelectedSkin().getUserPredicate().test(user)) {
         this.applyButton.active = true;
         this.removeButton.active = true;
         if (this.getSelectedSkin().getInfo().lifetime()
            || user.getCurrentTier() == null
            || user.getCurrentTierLevel() < this.getSelectedSkin().getInfo().tier().getLevel()) {
            boolean mouseOver = this.isMouseOverIcon(mouseX, mouseY, 8);
            this.renderLifetimeIcon(guiGraphics, mouseOver);
            if (mouseOver) {
               this.renderTooltip(
                  Component.translatable("gui.aether.moa_skins.tooltip.title.access.lifetime"),
                  Component.translatable("gui.aether.moa_skins.tooltip.lifetime"),
                  guiGraphics,
                  mouseX,
                  mouseY
               );
            }
         } else if (user.getCurrentTier() != null) {
            boolean mouseOver = this.isMouseOverIcon(mouseX, mouseY, 7);
            this.renderPledgingIcon(guiGraphics, mouseOver);
            if (mouseOver) {
               this.renderTooltip(
                  Component.translatable("gui.aether.moa_skins.tooltip.title.access.pledging"),
                  Component.translatable("gui.aether.moa_skins.tooltip.pledging", new Object[]{user.getCurrentTier().getDisplayName()}),
                  guiGraphics,
                  mouseX,
                  mouseY
               );
            }
         }
      } else {
         this.applyButton.active = false;
         this.removeButton.active = false;
         guiGraphics.blitSprite(LOCK_SPRITE, this.leftPos + 13, this.topPos + 13, 10, 14);
         if (this.getSelectedSkin().getInfo().lifetime()) {
            boolean mouseOver = this.isMouseOverIcon(mouseX, mouseY, 8);
            this.renderLifetimeIcon(guiGraphics, mouseOver);
            if (mouseOver) {
               this.renderTooltip(
                  Component.translatable("gui.aether.moa_skins.tooltip.title.access.lifetime"),
                  Component.translatable("gui.aether.moa_skins.tooltip.access.lifetime", new Object[]{this.getSelectedSkin().getInfo().tier().getDisplayName()}),
                  guiGraphics,
                  mouseX,
                  mouseY
               );
            }
         } else {
            boolean mouseOver = this.isMouseOverIcon(mouseX, mouseY, 7);
            this.renderPledgingIcon(guiGraphics, mouseOver);
            if (mouseOver) {
               this.renderTooltip(
                  Component.translatable("gui.aether.moa_skins.tooltip.title.access.pledging"),
                  Component.translatable("gui.aether.moa_skins.tooltip.access.pledging", new Object[]{this.getSelectedSkin().getInfo().tier().getDisplayName()}),
                  guiGraphics,
                  mouseX,
                  mouseY
               );
            }
         }
      }

      this.renderMoa(guiGraphics, partialTicks);
      guiGraphics.drawCenteredString(this.getMinecraft().font, this.getSelectedSkin().getDisplayName(), this.leftPos + 176 / 2, this.topPos + 12, 16777215);
      guiGraphics.drawCenteredString(this.getMinecraft().font, this.getTitle(), this.leftPos + 176 / 2, this.topPos - 15, 16777215);
   }

   private void renderLifetimeIcon(GuiGraphics guiGraphics, boolean mouseOver) {
      ResourceLocation location = PERMANENT_WIDGET.get(true, mouseOver);
      guiGraphics.blitSprite(location, this.leftPos + 13, this.topPos + 184 / 2 - 9, 8, 7);
   }

   private void renderPledgingIcon(GuiGraphics guiGraphics, boolean mouseOver) {
      ResourceLocation location = TEMPORARY_WIDGET.get(true, mouseOver);
      guiGraphics.blitSprite(location, this.leftPos + 13, this.topPos + 184 / 2 - 9, 7, 7);
   }

   private boolean isMouseOverIcon(int mouseX, int mouseY, int width) {
      int leftPos = this.leftPos + 13;
      int topPos = this.topPos + 184 / 2 - 9;
      double mouseXDiff = mouseX - leftPos;
      double mouseYDiff = mouseY - topPos;
      return mouseYDiff <= 7.0 && mouseYDiff >= 0.0 && mouseXDiff <= width && mouseXDiff >= 0.0;
   }

   private void renderTooltip(MutableComponent title, Component description, GuiGraphics guiGraphics, int mouseX, int mouseY) {
      List<FormattedText> formattedTextList = new ArrayList<>();
      formattedTextList.add(title.withStyle(ChatFormatting.GOLD));
      formattedTextList.addAll(this.getMinecraft().font.getSplitter().splitLines(description, this.width / 3, Style.EMPTY));
      guiGraphics.renderComponentTooltip(this.getMinecraft().font, formattedTextList, mouseX, mouseY, ItemStack.EMPTY);
   }

   private void renderMoa(GuiGraphics guiGraphics, float partialTicks) {
      if (this.getMinecraft().level != null) {
         if (this.getPreviewMoa() == null) {
            Moa moa = (Moa)((EntityType)AetherEntityTypes.MOA.get()).create(this.getMinecraft().level);
            if (moa != null) {
               moa.generateMoaUUID();
               moa.setMoaTypeByKey(AetherMoaTypes.BLUE);
               moa.setSaddled(true);
               this.previewMoa = moa;
            }
         } else {
            this.moaRotation = Mth.wrapDegrees(Mth.lerp(partialTicks, this.moaRotation, this.moaRotation + 1.0F));
            int startX = this.leftPos + 176 / 2;
            int startY = this.topPos + 184 / 2;
            renderRotatingEntity(guiGraphics, startX - 300, startY - 135, startX + 300, startY + 65, 27, 0.055F, this.moaRotation, -20.0F, this.getPreviewMoa());
         }
      }
   }

   public static void renderRotatingEntity(
      GuiGraphics guiGraphics,
      int startX,
      int startY,
      int endX,
      int endY,
      int scale,
      float yOffset,
      float angleXComponent,
      float angleYComponent,
      LivingEntity livingEntity
   ) {
      float posX = (startX + endX) / 2.0F;
      float posY = (startY + endY) / 2.0F;
      guiGraphics.enableScissor(startX, startY, endX, endY);
      Quaternionf xQuaternion = new Quaternionf().rotateZ(3.1415927F);
      Quaternionf zQuaternion = new Quaternionf().rotateX(angleYComponent * 0.017453292F);
      xQuaternion.mul(zQuaternion);
      float yBodyRot = livingEntity.yBodyRot;
      float yRot = livingEntity.getYRot();
      float xRot = livingEntity.getXRot();
      livingEntity.setYBodyRot(180.0F + angleXComponent);
      livingEntity.setYRot(180.0F + angleXComponent);
      livingEntity.setXRot(-angleYComponent);
      livingEntity.setYHeadRot(livingEntity.getYRot());
      livingEntity.yHeadRotO = livingEntity.getYRot();
      Vector3f vector3f = new Vector3f(0.0F, livingEntity.getBbHeight() / 2.0F + yOffset, 0.0F);
      InventoryScreen.renderEntityInInventory(guiGraphics, posX, posY, scale, vector3f, xQuaternion, zQuaternion, livingEntity);
      livingEntity.setYBodyRot(yBodyRot);
      livingEntity.setYRot(yRot);
      livingEntity.setXRot(xRot);
      guiGraphics.disableScissor();
   }

   private void checkUserConnectionStatus() {
      User user = Client.getClientUser();
      if (this.getMinecraft().player != null) {
         if (user == null && this.userConnectionExists) {
            PacketDistributor.sendToServer(new ServerMoaSkinPacket.Remove(this.getMinecraft().player.getUUID()), new CustomPacketPayload[0]);
            this.userConnectionExists = false;
         } else if (user != null && !this.userConnectionExists && MoaSkins.getMoaSkins().get(this.customizations.getMoaSkin()) != null) {
            PacketDistributor.sendToServer(
               new ServerMoaSkinPacket.Apply(
                  this.getMinecraft().player.getUUID(),
                  new MoaData(
                     ((AetherPlayerAttachment)this.getMinecraft().player.getData(AetherDataAttachments.AETHER_PLAYER)).getLastRiddenMoa(),
                     MoaSkins.getMoaSkins().get(this.customizations.getMoaSkin())
                  )
               ),
               new CustomPacketPayload[0]
            );
            this.userConnectionExists = true;
         }
      }
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
      if (button == 0) {
         float scrollbarGutterLeft = this.leftPos + 7.0F;
         float scrollbarGutterTop = this.topPos + 184.0F / 2.0F + 29.0F;
         double mouseXDiff = mouseX - scrollbarGutterLeft;
         double mouseYDiff = mouseY - scrollbarGutterTop;
         if ((mouseYDiff <= 6.0 && mouseYDiff >= 0.0 || this.scrolling) && mouseXDiff <= 160.0 && mouseXDiff >= 0.0) {
            this.scrolling = true;
            this.scrollX = Math.max(0.0F, Math.min((float)mouseXDiff - this.scrollbarWidth() / 2.0F, this.scrollbarGutterWidth()));
            return true;
         }
      }

      return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      int i = 0;
      int index = this.getSlotOffset();
      if (index != -1) {
         i = index;
      }

      if (scrollY < 0.0) {
         i = Math.min(i + 1, this.snapPoints.size() - 1);
      } else if (scrollY > 0.0) {
         i = Math.max(i - 1, 0);
      }

      this.scrollX = this.snapPoints.get(i);
      return true;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      MoaSkins.MoaSkin skin = this.getSkinFromSlot(mouseX, mouseY);
      if (skin != null) {
         this.selectedSkin = skin;
         return true;
      } else {
         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   @Nullable
   private MoaSkins.MoaSkin getSkinFromSlot(double mouseX, double mouseY) {
      int slot = this.getSlotIndex(mouseX, mouseY);
      if (slot != -1) {
         int trueSlot = slot + this.getSlotOffset();
         return this.moaSkins.get(trueSlot);
      } else {
         return null;
      }
   }

   private int getSlotIndex(double mouseX, double mouseY) {
      int slotLeft = this.leftPos + 7;
      int slotTop = this.topPos + 184 / 2 + 9;
      double mouseXDiff = mouseX - slotLeft;
      double mouseYDiff = mouseY - slotTop;
      return mouseYDiff <= 18.0 && mouseYDiff >= 0.0 && mouseXDiff <= 160.0 && mouseXDiff >= 0.0 ? (int)(mouseXDiff / 18.0) : -1;
   }

   private int getSlotOffset() {
      int offset = 0;
      int index = this.snapPoints.indexOf(this.scrollX);
      if (index != -1) {
         offset = index;
      } else {
         for (int i = 0; i < this.snapPoints.size() - 1; i++) {
            float currentPoint = this.snapPoints.get(i);
            float nextPoint = this.snapPoints.get(i + 1);
            float midway = currentPoint + (nextPoint - currentPoint) / 2.0F;
            if (this.scrollX > midway && this.scrollX < nextPoint) {
               offset = i + 1;
            } else if (this.scrollX <= midway && this.scrollX > currentPoint) {
               offset = i;
            }
         }
      }

      return offset;
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      this.scrolling = false;
      return super.mouseReleased(mouseX, mouseY, button);
   }

   private float scrollbarWidth() {
      return 13.0F;
   }

   private float scrollbarGutterWidth() {
      return 160.0F - this.scrollbarWidth();
   }

   private int maxSlots() {
      return 9;
   }

   public void onClose() {
      this.getMinecraft().setScreen(this.lastScreen);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public MoaSkins.MoaSkin getSelectedSkin() {
      return this.selectedSkin;
   }

   public Moa getPreviewMoa() {
      return this.previewMoa;
   }
}
