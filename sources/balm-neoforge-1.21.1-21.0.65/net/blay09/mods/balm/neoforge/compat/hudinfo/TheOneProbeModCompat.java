package net.blay09.mods.balm.neoforge.compat.hudinfo;

import java.util.List;
import java.util.function.Function;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.compat.hudinfo.BlockInfoContext;
import net.blay09.mods.balm.api.compat.hudinfo.BlockInfoProvider;
import net.blay09.mods.balm.api.compat.hudinfo.HudInfoOutput;
import net.blay09.mods.balm.common.compat.hudinfo.CommonBalmModSupportHudInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.fml.InterModComms;
import org.jetbrains.annotations.Nullable;

public class TheOneProbeModCompat {
   public static void register() {
      InterModComms.sendTo("theoneprobe", "getTheOneProbe", TheOneProbeModCompat.BalmTheOneProbeInitializer::new);
   }

   private static class BalmProbeInfoProvider implements IProbeInfoProvider {
      private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("balm", "top");

      public ResourceLocation getID() {
         return ID;
      }

      public void addProbeInfo(ProbeMode probeMode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData hitData) {
         CommonBalmModSupportHudInfo modSupport = (CommonBalmModSupportHudInfo)Balm.getModSupport().hudInfo();
         List<BlockInfoProvider> blockInfoProviders = modSupport.getBlockInfoProviders(state.getBlock());
         if (!blockInfoProviders.isEmpty()) {
            TheOneProbeModCompat.TheOneProbeHudInfoInfoOutput output = new TheOneProbeModCompat.TheOneProbeHudInfoInfoOutput(info);
            BlockInfoContext context = new BlockInfoContext(
               level,
               hitData.getPos(),
               state,
               level.getBlockEntity(hitData.getPos()),
               new BlockHitResult(hitData.getHitVec(), hitData.getSideHit(), hitData.getPos(), false),
               player
            );

            for (BlockInfoProvider blockInfoProvider : blockInfoProviders) {
               blockInfoProvider.apply(context, output);
            }
         }
      }
   }

   public static class BalmTheOneProbeInitializer implements Function<ITheOneProbe, Void> {
      @Nullable
      public Void apply(@Nullable ITheOneProbe top) {
         if (top != null) {
            top.registerProvider(new TheOneProbeModCompat.BalmProbeInfoProvider());
         }

         return null;
      }
   }

   private record TheOneProbeHudInfoInfoOutput(IProbeInfo info) implements HudInfoOutput {
      @Override
      public void text(Component component) {
         this.info.text(component);
      }

      @Override
      public void progress(float progress) {
         this.info.progress((int)(progress * 100.0F), 100);
      }

      @Override
      public void progress(int progress, int maxProgress) {
         this.info.progress(progress, maxProgress);
      }
   }
}
