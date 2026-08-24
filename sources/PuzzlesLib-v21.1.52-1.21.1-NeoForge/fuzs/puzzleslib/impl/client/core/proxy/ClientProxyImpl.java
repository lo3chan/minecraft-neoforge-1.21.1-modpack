package fuzs.puzzleslib.impl.client.core.proxy;

import fuzs.puzzleslib.api.chat.v1.ComponentHelper;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.gui.v2.components.tooltip.ClientComponentSplitter;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.impl.client.init.ItemDisplayOverridesImpl;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Fluid;

public interface ClientProxyImpl extends ProxyImpl {
   static ClientProxyImpl get() {
      return (ClientProxyImpl)ProxyImpl.INSTANCE;
   }

   ModConstructorImpl<ClientModConstructor> getClientModConstructorImpl();

   ItemDisplayOverridesImpl<?> getItemModelDisplayOverrides();

   KeyMappingHelper getKeyMappingActivationHelper();

   boolean isKeyActiveAndMatches(KeyMapping var1, int var2, int var3);

   ClientTooltipComponent createImageComponent(TooltipComponent var1);

   boolean onRenderTooltip(GuiGraphics var1, Font var2, int var3, int var4, List<ClientTooltipComponent> var5, ClientTooltipPositioner var6);

   BakedQuad copyBakedQuad(BakedQuad var1);

   boolean isEffectVisibleInInventory(MobEffectInstance var1);

   boolean isEffectVisibleInGui(MobEffectInstance var1);

   void registerWoodType(WoodType var1);

   BakedModel getBakedModel(ModelManager var1, ResourceLocation var2);

   RenderType getRenderType(Block var1);

   void registerRenderType(Block var1, RenderType var2);

   void registerRenderType(Fluid var1, RenderType var2);

   int getGuiLeftHeight(Gui var1);

   int getGuiRightHeight(Gui var1);

   void addGuiLeftHeight(Gui var1, int var2);

   void addGuiRightHeight(Gui var1, int var2);

   @Override
   default BlockableEventLoop<? super TickTask> getBlockableEventLoop(Level level) {
      return (BlockableEventLoop<? super TickTask>)(level.isClientSide() ? Minecraft.getInstance() : ProxyImpl.super.getBlockableEventLoop(level));
   }

   @Override
   default RegistryAccess getRegistryAccess() {
      return Minecraft.getInstance().getConnection() != null ? Minecraft.getInstance().getConnection().registryAccess() : null;
   }

   @Override
   default Player getClientPlayer() {
      return Minecraft.getInstance().player;
   }

   @Override
   default Level getClientLevel() {
      return Minecraft.getInstance().level;
   }

   @Override
   default ClientPacketListener getClientPacketListener() {
      ClientPacketListener connection = Minecraft.getInstance().getConnection();
      Objects.requireNonNull(connection, "client packet listener is null");
      return connection;
   }

   @Override
   default boolean hasControlDown() {
      return Screen.hasControlDown();
   }

   @Override
   default boolean hasShiftDown() {
      return Screen.hasShiftDown();
   }

   @Override
   default boolean hasAltDown() {
      return Screen.hasAltDown();
   }

   @Override
   default List<Component> splitTooltipLines(Component component) {
      return ClientComponentSplitter.splitTooltipLines(component).map(ComponentHelper::toComponent).toList();
   }
}
