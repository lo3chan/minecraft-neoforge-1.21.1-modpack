package net.blay09.mods.balm.neoforge.compat.hudinfo;

import java.util.List;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.compat.hudinfo.BlockInfoContext;
import net.blay09.mods.balm.api.compat.hudinfo.BlockInfoProvider;
import net.blay09.mods.balm.api.compat.hudinfo.HudInfoOutput;
import net.blay09.mods.balm.common.compat.hudinfo.CommonBalmModSupportHudInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.impl.ui.ProgressElement;
import snownee.jade.impl.ui.SimpleProgressStyle;

@WailaPlugin("balm")
public class NeoForgeJadeModCompat implements IWailaPlugin {
   public void registerClient(IWailaClientRegistration registration) {
      registration.registerBlockComponent(new NeoForgeJadeModCompat.BalmBlockComponentProvider(), Block.class);
   }

   private static class BalmBlockComponentProvider implements IBlockComponentProvider {
      private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("balm", "jade");

      public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
         Block block = accessor.getBlock();
         CommonBalmModSupportHudInfo modSupport = (CommonBalmModSupportHudInfo)Balm.getModSupport().hudInfo();
         List<BlockInfoProvider> blockInfoProviders = modSupport.getBlockInfoProviders(block);
         if (!blockInfoProviders.isEmpty()) {
            NeoForgeJadeModCompat.JadeHudInfoOutput output = new NeoForgeJadeModCompat.JadeHudInfoOutput(tooltip);
            BlockInfoContext context = new BlockInfoContext(
               accessor.getLevel(),
               accessor.getPosition(),
               accessor.getBlockState(),
               accessor.getBlockEntity(),
               (BlockHitResult)accessor.getHitResult(),
               accessor.getPlayer()
            );

            for (BlockInfoProvider blockInfoProvider : blockInfoProviders) {
               blockInfoProvider.apply(context, output);
            }
         }
      }

      public ResourceLocation getUid() {
         return ID;
      }
   }

   private record JadeHudInfoOutput(ITooltip tooltip) implements HudInfoOutput {
      @Override
      public void text(Component component) {
         this.tooltip.add(component);
      }

      @Override
      public void progress(float progress) {
         this.tooltip.add(new ProgressElement(progress, Component.empty(), new SimpleProgressStyle(), BoxStyle.getNestedBox(), false));
      }
   }
}
