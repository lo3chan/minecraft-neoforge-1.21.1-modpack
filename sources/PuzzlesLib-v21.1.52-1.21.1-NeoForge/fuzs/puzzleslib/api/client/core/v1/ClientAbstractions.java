package fuzs.puzzleslib.api.client.core.v1;

import fuzs.puzzleslib.api.client.gui.v2.ScreenHelper;
import fuzs.puzzleslib.api.client.renderer.v1.RenderTypeHelper;
import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

@Deprecated
public interface ClientAbstractions {
   ClientAbstractions INSTANCE = new ClientAbstractions() {};

   default boolean hasChannel(ClientPacketListener clientPacketListener, Type<?> type) {
      return ClientProxyImpl.get().hasChannel(clientPacketListener, type);
   }

   default boolean isKeyActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode) {
      return ClientProxyImpl.get().isKeyActiveAndMatches(keyMapping, keyCode, scanCode);
   }

   default ClientTooltipComponent createImageComponent(TooltipComponent imageComponent) {
      return ClientProxyImpl.get().createImageComponent(imageComponent);
   }

   @Deprecated(
      forRemoval = true
   )
   default BakedModel getBakedModel(ResourceLocation resourceLocation) {
      return this.getBakedModel(Minecraft.getInstance().getModelManager(), resourceLocation);
   }

   default BakedModel getBakedModel(ModelManager modelManager, ResourceLocation resourceLocation) {
      return ClientProxyImpl.get().getBakedModel(modelManager, resourceLocation);
   }

   default RenderType getRenderType(Block block) {
      return RenderTypeHelper.getRenderType(block);
   }

   default RenderType getRenderType(Fluid fluid) {
      return RenderTypeHelper.getRenderType(fluid);
   }

   default void registerRenderType(Block block, RenderType renderType) {
      RenderTypeHelper.registerRenderType(block, renderType);
   }

   default void registerRenderType(Fluid fluid, RenderType renderType) {
      RenderTypeHelper.registerRenderType(fluid, renderType);
   }

   default float getPartialTick() {
      return ScreenHelper.getPartialTick();
   }

   default boolean onRenderTooltip(
      GuiGraphics guiGraphics, Font font, int mouseX, int mouseY, List<ClientTooltipComponent> components, ClientTooltipPositioner positioner
   ) {
      return ClientProxyImpl.get().onRenderTooltip(guiGraphics, font, mouseX, mouseY, components, positioner);
   }

   default int getGuiLeftHeight(Gui gui) {
      return ClientProxyImpl.get().getGuiLeftHeight(gui);
   }

   default int getGuiRightHeight(Gui gui) {
      return ClientProxyImpl.get().getGuiRightHeight(gui);
   }

   default void addGuiLeftHeight(Gui gui, int leftHeight) {
      ClientProxyImpl.get().addGuiLeftHeight(gui, leftHeight);
   }

   default void addGuiRightHeight(Gui gui, int rightHeight) {
      ClientProxyImpl.get().addGuiRightHeight(gui, rightHeight);
   }
}
