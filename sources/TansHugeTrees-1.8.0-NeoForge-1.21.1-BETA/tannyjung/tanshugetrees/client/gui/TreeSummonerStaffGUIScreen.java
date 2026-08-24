package tannyjung.tanshugetrees.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import tannyjung.tanshugetrees.init.TanshugetreesModScreens;
import tannyjung.tanshugetrees.network.TreeSummonerStaffGUIButtonMessage;
import tannyjung.tanshugetrees.world.inventory.TreeSummonerStaffGUIMenu;

public class TreeSummonerStaffGUIScreen extends AbstractContainerScreen<TreeSummonerStaffGUIMenu> implements TanshugetreesModScreens.ScreenAccessor {
   private final Level world;
   private final int x;
   private final int y;
   private final int z;
   private final Player entity;
   private boolean menuStateUpdateActive = false;
   private EditBox path;
   private Button button_apply;
   private static final ResourceLocation texture = ResourceLocation.parse("tanshugetrees:textures/screens/tree_summoner_staff_gui.png");

   public TreeSummonerStaffGUIScreen(TreeSummonerStaffGUIMenu container, Inventory inventory, Component text) {
      super(container, inventory, text);
      this.world = container.world;
      this.x = container.x;
      this.y = container.y;
      this.z = container.z;
      this.entity = container.entity;
      this.imageWidth = 0;
      this.imageHeight = 0;
   }

   @Override
   public void updateMenuState(int elementType, String name, Object elementState) {
      this.menuStateUpdateActive = true;
      if (elementType == 0 && elementState instanceof String stringState && name.equals("path")) {
         this.path.setValue(stringState);
      }

      this.menuStateUpdateActive = false;
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      super.render(guiGraphics, mouseX, mouseY, partialTicks);
      this.path.render(guiGraphics, mouseX, mouseY, partialTicks);
      this.renderTooltip(guiGraphics, mouseX, mouseY);
   }

   protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      guiGraphics.blit(texture, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
      RenderSystem.disableBlend();
   }

   public boolean keyPressed(int key, int b, int c) {
      if (key == 256) {
         this.minecraft.player.closeContainer();
         return true;
      } else {
         return this.path.isFocused() ? this.path.keyPressed(key, b, c) : super.keyPressed(key, b, c);
      }
   }

   public void resize(Minecraft minecraft, int width, int height) {
      String pathValue = this.path.getValue();
      super.resize(minecraft, width, height);
      this.path.setValue(pathValue);
   }

   protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      guiGraphics.drawString(
         this.font, Component.translatable("gui.tanshugetrees.tree_summoner_staff_gui.label_example_tannyjungmainpackr"), -176, -24, -6710887, false
      );
      guiGraphics.drawString(
         this.font, Component.translatable("gui.tanshugetrees.tree_summoner_staff_gui.label_the_path_will_be_test_from_extra"), -208, 92, -6710887, false
      );
      guiGraphics.drawString(
         this.font, Component.translatable("gui.tanshugetrees.tree_summoner_staff_gui.label_path_of_preset_inside_the_tempor"), -208, 80, -6710887, false
      );
      guiGraphics.drawString(
         this.font, Component.translatable("gui.tanshugetrees.tree_summoner_staff_gui.label_is_extracted_then_it_will_go_ge"), -208, 104, -6710887, false
      );
   }

   public void init() {
      super.init();
      this.path = new EditBox(
         this.font, this.leftPos + -175, this.topPos + -7, 350, 18, Component.translatable("gui.tanshugetrees.tree_summoner_staff_gui.path")
      );
      this.path.setMaxLength(8192);
      this.path.setResponder(content -> {
         if (!this.menuStateUpdateActive) {
            ((TreeSummonerStaffGUIMenu)this.menu).sendMenuStateUpdate(this.entity, 0, "path", content, false);
         }
      });
      this.addWidget(this.path);
      this.button_apply = Button.builder(Component.translatable("gui.tanshugetrees.tree_summoner_staff_gui.button_apply"), e -> {
         int x = this.x;
         int y = this.y;
         PacketDistributor.sendToServer(new TreeSummonerStaffGUIButtonMessage(0, x, y, this.z), new CustomPacketPayload[0]);
         TreeSummonerStaffGUIButtonMessage.handleButtonAction(this.entity, 0, x, y, this.z);
      }).bounds(this.leftPos + 128, this.topPos + 16, 48, 20).build();
      this.addRenderableWidget(this.button_apply);
   }
}
