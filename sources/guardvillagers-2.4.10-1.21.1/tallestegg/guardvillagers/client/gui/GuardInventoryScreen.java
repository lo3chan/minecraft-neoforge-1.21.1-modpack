package tallestegg.guardvillagers.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Gui.HeartType;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import tallestegg.guardvillagers.common.entities.Guard;
import tallestegg.guardvillagers.common.entities.GuardContainer;
import tallestegg.guardvillagers.configuration.GuardConfig;
import tallestegg.guardvillagers.networking.GuardFollowPacket;
import tallestegg.guardvillagers.networking.GuardSetPatrolPosPacket;

public class GuardInventoryScreen extends AbstractContainerScreen<GuardContainer> {
   private static final ResourceLocation GUARD_GUI_TEXTURES = ResourceLocation.fromNamespaceAndPath("guardvillagers", "textures/container/inventory.png");
   private static final WidgetSprites GUARD_FOLLOWING_ICONS = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("guardvillagers", "following/following"),
      ResourceLocation.fromNamespaceAndPath("guardvillagers", "following/following_highlighted")
   );
   private static final WidgetSprites GUARD_NOT_FOLLOWING_ICONS = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("guardvillagers", "following/not_following"),
      ResourceLocation.fromNamespaceAndPath("guardvillagers", "following/not_following_highlighted")
   );
   private static final WidgetSprites GUARD_PATROLLING_ICONS = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("guardvillagers", "patrolling/patrolling1"),
      ResourceLocation.fromNamespaceAndPath("guardvillagers", "patrolling/patrolling2")
   );
   private static final WidgetSprites GUARD_NOT_PATROLLING_ICONS = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("guardvillagers", "patrolling/notpatrolling1"),
      ResourceLocation.fromNamespaceAndPath("guardvillagers", "patrolling/notpatrolling2")
   );
   private static final ResourceLocation ARMOR_EMPTY_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_empty");
   private static final ResourceLocation ARMOR_HALF_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_half");
   private static final ResourceLocation ARMOR_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_full");
   private final Guard guard;
   private Player player;
   private float mousePosX;
   private float mousePosY;
   private boolean buttonPressed;

   public GuardInventoryScreen(GuardContainer container, Inventory playerInventory, Guard guard) {
      super(container, playerInventory, guard.getDisplayName());
      this.guard = guard;
      this.titleLabelX = 80;
      this.inventoryLabelX = 100;
      this.player = playerInventory.player;
   }

   public void init() {
      super.init();
      if ((Boolean)GuardConfig.COMMON.followHero.get() && this.player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)
         || !(Boolean)GuardConfig.COMMON.followHero.get()) {
         this.addRenderableWidget(
            new GuardInventoryScreen.GuardGuiButton(
               this.leftPos + 100,
               this.height / 2 - 40,
               20,
               18,
               GUARD_FOLLOWING_ICONS,
               GUARD_NOT_FOLLOWING_ICONS,
               true,
               p_214086_1_ -> PacketDistributor.sendToServer(new GuardFollowPacket(this.guard.getId()), new CustomPacketPayload[0])
            )
         );
      }

      if ((Boolean)GuardConfig.COMMON.setGuardPatrolHotv.get() && this.player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)
         || !(Boolean)GuardConfig.COMMON.setGuardPatrolHotv.get()) {
         this.addRenderableWidget(
            new GuardInventoryScreen.GuardGuiButton(
               this.leftPos + 120, this.height / 2 - 40, 20, 18, GUARD_PATROLLING_ICONS, GUARD_NOT_PATROLLING_ICONS, false, p_214086_1_ -> {
                  this.buttonPressed = !this.buttonPressed;
                  PacketDistributor.sendToServer(new GuardSetPatrolPosPacket(this.guard.getId(), this.buttonPressed), new CustomPacketPayload[0]);
               }
            )
         );
      }
   }

   protected void renderBg(GuiGraphics graphics, float partialTicks, int x, int y) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, GUARD_GUI_TEXTURES);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      graphics.blit(GUARD_GUI_TEXTURES, i, j, 0, 0, this.imageWidth, this.imageHeight);
      InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, i + 26, j + 8, i + 75, j + 78, 30, 0.0625F, this.mousePosX, this.mousePosY, this.guard);
   }

   protected void renderLabels(GuiGraphics graphics, int x, int y) {
      super.renderLabels(graphics, x, y);
      int health = Mth.ceil(this.guard.getHealth());
      int armor = this.guard.getArmorValue();
      Component guardHealthText = Component.translatable("guardinventory.health", new Object[]{health});
      Component guardArmorText = Component.translatable("guardinventory.armor", new Object[]{armor});
      int yValueWithOrWithoutArmor = armor <= 0 ? 20 : 30;
      if (!(Boolean)GuardConfig.CLIENT.guardInventoryNumbers.get() || this.guard.getMaxHealth() > 20.0F) {
         graphics.drawString(this.font, guardHealthText, 80, 30, 4210752, false);
      } else if (this.guard.getMaxHealth() <= 20.0F) {
         for (int i = 0; i < this.guard.getMaxHealth() * 0.5; i++) {
            int heartXValue = i * 8 + 80;
            this.renderHeart(graphics, HeartType.CONTAINER, heartXValue, yValueWithOrWithoutArmor, false);
         }

         for (int i = 0; i < health / 2; i++) {
            int heartXValue = i * 8 + 80;
            if (health % 2 != 0 && health / 2 == i + 1) {
               this.renderHeart(graphics, HeartType.NORMAL, heartXValue, yValueWithOrWithoutArmor, true);
            } else {
               this.renderHeart(graphics, HeartType.NORMAL, heartXValue, yValueWithOrWithoutArmor, false);
            }
         }
      }

      if (!(Boolean)GuardConfig.CLIENT.guardInventoryNumbers.get()) {
         graphics.drawString(this.font, guardArmorText, 80, 20, 4210752, false);
      } else if (armor > 0) {
         RenderSystem.enableBlend();

         for (int k = 0; k < 10; k++) {
            int l = k * 8 + 80;
            if (k * 2 + 1 < armor) {
               graphics.blitSprite(ARMOR_FULL_SPRITE, l, 20, 9, 9);
            }

            if (k * 2 + 1 == armor) {
               graphics.blitSprite(ARMOR_HALF_SPRITE, l, 20, 9, 9);
            }

            if (k * 2 + 1 > armor) {
               graphics.blitSprite(ARMOR_EMPTY_SPRITE, l, 20, 9, 9);
            }
         }

         RenderSystem.disableBlend();
      }
   }

   private void renderHeart(GuiGraphics guiGraphics, HeartType heartType, int x, int y, boolean halfHeart) {
      RenderSystem.enableBlend();
      guiGraphics.blitSprite(heartType.getSprite(false, halfHeart, false), x, y, 9, 9);
      RenderSystem.disableBlend();
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(graphics, mouseX, mouseX, partialTicks);
      this.mousePosX = mouseX;
      this.mousePosY = mouseY;
      super.render(graphics, mouseX, mouseY, partialTicks);
      this.renderTooltip(graphics, mouseX, mouseY);
   }

   class GuardGuiButton extends ImageButton {
      private WidgetSprites texture;
      private WidgetSprites newTexture;
      private boolean isFollowButton;

      public GuardGuiButton(
         int xIn, int yIn, int widthIn, int heightIn, WidgetSprites resourceLocationIn, WidgetSprites newTexture, boolean isFollowButton, OnPress onPressIn
      ) {
         super(xIn, yIn, widthIn, heightIn, resourceLocationIn, onPressIn);
         this.texture = resourceLocationIn;
         this.newTexture = newTexture;
         this.isFollowButton = isFollowButton;
      }

      public boolean requirementsForTexture() {
         boolean following = GuardInventoryScreen.this.guard.isFollowing();
         boolean patrol = GuardInventoryScreen.this.guard.isPatrolling();
         return this.isFollowButton ? following : patrol;
      }

      public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
         WidgetSprites icon = this.requirementsForTexture() ? this.texture : this.newTexture;
         ResourceLocation resourcelocation = icon.get(this.isActive(), this.isHoveredOrFocused());
         graphics.blitSprite(resourcelocation, this.getX(), this.getY(), this.width, this.height);
      }
   }
}
