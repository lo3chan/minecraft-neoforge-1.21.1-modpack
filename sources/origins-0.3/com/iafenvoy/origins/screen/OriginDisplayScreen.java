package com.iafenvoy.origins.screen;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableSet.Builder;
import com.iafenvoy.origins.data.badge.Badge;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Impact;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.screen.badge.BadgeTooltipManager;
import com.iafenvoy.origins.util.codec.RegistryCodecs;
import com.iafenvoy.origins.util.math.TextAlignment;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class OriginDisplayScreen extends Screen {
   private static final ResourceLocation WINDOW_BACKGROUND = ResourceLocation.fromNamespaceAndPath("origins", "choose_origin/background");
   private static final ResourceLocation WINDOW_BORDER = ResourceLocation.fromNamespaceAndPath("origins", "choose_origin/border");
   private static final ResourceLocation WINDOW_NAME_PLATE = ResourceLocation.fromNamespaceAndPath("origins", "choose_origin/name_plate");
   private static final ResourceLocation WINDOW_SCROLL_BAR = ResourceLocation.fromNamespaceAndPath("origins", "choose_origin/scroll_bar");
   private static final ResourceLocation WINDOW_SCROLL_BAR_PRESSED = ResourceLocation.fromNamespaceAndPath("origins", "choose_origin/scroll_bar/pressed");
   private static final ResourceLocation WINDOW_SCROLL_BAR_SLOT = ResourceLocation.fromNamespaceAndPath("origins", "choose_origin/scroll_bar/slot");
   protected static final int WINDOW_WIDTH = 176;
   protected static final int WINDOW_HEIGHT = 182;
   private final LinkedList<OriginDisplayScreen.RenderedBadge> renderedBadges = new LinkedList<>();
   protected final boolean showDirtBackground;
   private Holder<Origin> origin;
   private Holder<Origin> prevOrigin;
   private Holder<Layer> layer;
   private Holder<Layer> prevLayer;
   private Component randomOriginText;
   private ScrollingTextWidget originNameWidget;
   private boolean refreshOriginNameWidget = false;
   private boolean isOriginRandom;
   private boolean dragScrolling = false;
   private double mouseDragStart = 0.0;
   private int currentMaxScroll = 0;
   private int scrollDragStart = 0;
   private int scrollPos = 0;
   protected int guiTop;
   protected int guiLeft;

   public OriginDisplayScreen(Component title, boolean showDirtBackground) {
      super(title);
      this.showDirtBackground = showDirtBackground;
   }

   public void showOrigin(Holder<Origin> origin, Holder<Layer> layer, boolean isRandom) {
      this.origin = origin;
      this.layer = layer;
      this.isOriginRandom = isRandom;
      this.scrollPos = 0;
   }

   public void setRandomOriginText(Component text) {
      this.randomOriginText = text;
   }

   protected void init() {
      super.init();
      this.guiLeft = (this.width - 176) / 2;
      this.guiTop = (this.height - 182) / 2;
      this.originNameWidget = new ScrollingTextWidget(this.guiLeft + 38, this.guiTop + 18, 90, 9, Component.empty(), true, this.font);
      this.refreshOriginNameWidget = true;
   }

   public void renderBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      if (this.showDirtBackground) {
         super.renderBackground(graphics, mouseX, mouseY, delta);
      } else {
         this.renderTransparentBackground(graphics);
      }
   }

   public void renderTransparentBackground(GuiGraphics graphics) {
      graphics.fillGradient(0, 0, this.width, this.height, -5, 1678774288, -2112876528);
   }

   public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      this.renderedBadges.clear();
      super.render(graphics, mouseX, mouseY, delta);
      this.renderOriginWindow(graphics, mouseX, mouseY, delta);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      this.dragScrolling = false;
      return super.mouseReleased(mouseX, mouseY, button);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      boolean mouseClicked = super.mouseClicked(mouseX, mouseY, button);
      if (this.cannotScroll()) {
         return mouseClicked;
      } else {
         this.dragScrolling = false;
         int scrollBarY = 36;
         int maxScrollBarOffset = 141;
         scrollBarY += (int)((maxScrollBarOffset - scrollBarY) * ((float)this.scrollPos / this.currentMaxScroll));
         if (!this.canDragScroll(mouseX, mouseY, scrollBarY)) {
            return mouseClicked;
         } else {
            this.dragScrolling = true;
            this.scrollDragStart = scrollBarY;
            this.mouseDragStart = mouseY;
            return true;
         }
      }
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      boolean mouseDragged = super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
      if (!this.dragScrolling) {
         return mouseDragged;
      } else {
         int delta = (int)(mouseY - this.mouseDragStart);
         int newScrollPos = Math.max(36, Math.min(141, this.scrollDragStart + delta));
         float part = (newScrollPos - 36) / 105.0F;
         this.scrollPos = (int)(part * this.currentMaxScroll);
         return mouseDragged;
      }
   }

   public boolean mouseScrolled(double x, double y, double horizontal, double vertical) {
      this.scrollPos = Mth.clamp(this.scrollPos - (int)vertical * 10, 0, this.currentMaxScroll);
      return super.mouseScrolled(x, y, horizontal, vertical);
   }

   public Holder<Origin> getCurrentOrigin() {
      return this.origin;
   }

   public Holder<Layer> getCurrentLayer() {
      return this.layer;
   }

   protected void renderScrollbar(GuiGraphics graphics, int mouseX, int mouseY) {
      if (!this.cannotScroll()) {
         graphics.blitSprite(WINDOW_SCROLL_BAR_SLOT, this.guiLeft + 155, this.guiTop + 35, 8, 134);
         int scrollbarY = 36;
         int maxScrollbarOffset = 141;
         scrollbarY += (int)((maxScrollbarOffset - scrollbarY) * ((float)this.scrollPos / this.currentMaxScroll));
         ResourceLocation scrollBarTexture = !this.dragScrolling && !this.canDragScroll(mouseX, mouseY, scrollbarY)
            ? WINDOW_SCROLL_BAR
            : WINDOW_SCROLL_BAR_PRESSED;
         graphics.blitSprite(scrollBarTexture, this.guiLeft + 156, this.guiTop + scrollbarY, 6, 27);
      }
   }

   protected boolean cannotScroll() {
      return this.origin == null || this.currentMaxScroll <= 0;
   }

   protected boolean canDragScroll(double mouseX, double mouseY, int scrollBarY) {
      return mouseX >= this.guiLeft + 156 && mouseX < this.guiLeft + 156 + 6 && mouseY >= this.guiTop + scrollBarY && mouseY < this.guiTop + scrollBarY + 27;
   }

   protected void renderBadgeTooltips(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      int widthLimit = this.width - mouseX - 24;
      if (this.isWithinWindowBoundaries(mouseX, mouseY)) {
         this.renderedBadges
            .stream()
            .filter(b -> b.isWithinBadgeBoundaries(mouseX, mouseY))
            .map(b -> b.getTooltipComponents(this.font, widthLimit, delta))
            .filter(x -> !x.isEmpty())
            .forEach(t -> graphics.renderTooltipInternal(this.font, t, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE));
      }
   }

   protected boolean isWithinWindowBoundaries(int mouseX, int mouseY) {
      return mouseX >= this.guiLeft && mouseX < this.guiLeft + 176 && mouseY >= this.guiTop && mouseY < this.guiTop + 182;
   }

   protected void renderOriginWindow(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      graphics.blitSprite(WINDOW_BACKGROUND, this.guiLeft, this.guiTop, -4, 176, 182);
      if (this.origin != null) {
         graphics.enableScissor(this.guiLeft, this.guiTop, this.guiLeft + 176, this.guiTop + 182);
         this.renderOriginContent(graphics);
         graphics.disableScissor();
      }

      graphics.blitSprite(WINDOW_BORDER, this.guiLeft, this.guiTop, 2, 176, 182);
      graphics.blitSprite(WINDOW_NAME_PLATE, this.guiLeft + 10, this.guiTop + 10, 2, 150, 26);
      if (this.origin != null) {
         graphics.pose().pushPose();
         graphics.pose().translate(0.0F, 0.0F, 5.0F);
         this.renderOriginName(graphics, mouseX, mouseY, delta);
         this.renderOriginImpact(graphics, mouseX, mouseY);
         graphics.pose().popPose();
         graphics.drawCenteredString(this.font, this.getTitle(), this.width / 2, this.guiTop - 15, 16777215);
         this.renderScrollbar(graphics, mouseX, mouseY);
         this.renderBadgeTooltips(graphics, mouseX, mouseY, delta);
      }
   }

   protected void renderOriginImpact(GuiGraphics graphics, int mouseX, int mouseY) {
      Impact impact = ((Origin)this.origin.value()).impact();
      graphics.blitSprite(impact.getSpriteId(), this.guiLeft + 128, this.guiTop + 19, 2, 28, 8);
      if (this.isWithinWindowBoundaries(mouseX, mouseY) && this.isWithinImpactBoundaries(mouseX, mouseY)) {
         MutableComponent impactHoverTooltip = Component.translatable("origins.gui.impact.impact").append(": ").append(impact.getTextComponent());
         graphics.renderTooltip(this.font, impactHoverTooltip, mouseX, mouseY);
      }
   }

   protected boolean isWithinImpactBoundaries(int mouseX, int mouseY) {
      int impactStartX = this.guiLeft + 128;
      int impactStartY = this.guiTop + 19;
      return mouseX >= impactStartX && mouseX < impactStartX + 28 && mouseY >= impactStartY && mouseY < impactStartY + 8;
   }

   protected void renderOriginName(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      if (this.refreshOriginNameWidget || this.origin != this.prevOrigin || this.layer != this.prevLayer) {
         this.originNameWidget = new ScrollingTextWidget(this.guiLeft + 38, this.guiTop + 18, 90, 9, Origin.getName(this.getCurrentOrigin()), true, this.font);
         this.originNameWidget.setAlignment(TextAlignment.LEFT);
         this.refreshOriginNameWidget = false;
         this.prevOrigin = this.origin;
         this.prevLayer = this.layer;
      }

      this.originNameWidget.render(graphics, mouseX, mouseY, delta);
      ItemStack iconStack = ((Origin)this.getCurrentOrigin().value()).icon().orElse(ItemStack.EMPTY);
      graphics.renderItem(iconStack, this.guiLeft + 15, this.guiTop + 15);
   }

   protected void renderOriginContent(GuiGraphics graphics) {
      assert Minecraft.getInstance().level != null;

      RegistryAccess access = Minecraft.getInstance().level.registryAccess();
      int textWidthLimit = 128;
      int x = this.guiLeft + 18;
      int y = this.guiTop + 45 - this.scrollPos;

      for (FormattedCharSequence descriptionLine : this.font.split(Origin.getDescription(this.getCurrentOrigin()), textWidthLimit)) {
         graphics.drawString(this.font, descriptionLine, x + 2, y, 13421772);
         y += 12;
      }

      y += 12;
      if (this.isOriginRandom) {
         for (FormattedCharSequence randomOriginLine : this.font.split(this.randomOriginText, textWidthLimit)) {
            y += 12;
            graphics.drawString(this.font, randomOriginLine, x + 2, y, 13421772);
         }

         y += 14;
      } else {
         for (Holder<Power> holder : RegistryCodecs.listAll(((Origin)this.origin.value()).powers(), access, PowerRegistries.POWER_KEY)) {
            Power power = (Power)holder.value();
            if (!power.isHidden()) {
               LinkedList<FormattedCharSequence> powerName = new LinkedList<>(
                  this.font.split(power.getName(access).withStyle(ChatFormatting.UNDERLINE), textWidthLimit)
               );
               int powerNameWidth = this.font.width(powerName.getLast());

               for (FormattedCharSequence powerNameLine : powerName) {
                  graphics.drawString(this.font, powerNameLine, x, y, 16777215);
                  y += 12;
               }

               y -= 12;
               int badgeStartX = x + powerNameWidth + 4;
               int badgeEndX = x + 135;
               int badgeOffsetX = 0;
               int badgeOffsetY = 0;
               Builder<Badge> badgeBuilder = ImmutableSet.builder();
               power.collectBadges(badgeBuilder);

               for (UnmodifiableIterator var16 = badgeBuilder.build().iterator(); var16.hasNext(); badgeOffsetX++) {
                  Badge badge = (Badge)var16.next();
                  int badgeX = badgeStartX + 10 * badgeOffsetX;
                  int badgeY = y - 1 + 10 * badgeOffsetY;
                  if (badgeX >= badgeEndX) {
                     badgeOffsetX = 0;
                     badgeOffsetY++;
                     badgeStartX = x;
                     badgeX = x;
                     badgeY = y - 1 + 10 * badgeOffsetY;
                  }

                  OriginDisplayScreen.RenderedBadge renderedBadge = new OriginDisplayScreen.RenderedBadge(power, badge, badgeX, badgeY);
                  this.renderedBadges.add(renderedBadge);
                  graphics.blit(badge.sprite(), renderedBadge.x, renderedBadge.y, 0.0F, 0.0F, 9, 9, 9, 9);
               }

               y += badgeOffsetY * 10;

               for (FormattedCharSequence powerDescriptionLine : this.font.split(power.getDescription(access), textWidthLimit)) {
                  y += 12;
                  graphics.drawString(this.font, powerDescriptionLine, x + 2, y, 13421772);
               }

               y += 20;
            }
         }
      }

      y += this.scrollPos;
      this.currentMaxScroll = Math.max(0, y - 14 - (this.guiTop + 158));
   }

   protected record RenderedBadge(Power power, Badge badge, int x, int y) {
      public List<ClientTooltipComponent> getTooltipComponents(Font textRenderer, int widthLimit, float delta) {
         return BadgeTooltipManager.getTooltipComponents(this.badge, this.power, textRenderer, widthLimit, delta);
      }

      protected boolean isWithinBadgeBoundaries(int mouseX, int mouseY) {
         return mouseX >= this.x && mouseX < this.x + 9 && mouseY >= this.y && mouseY < this.y + 9;
      }
   }
}
