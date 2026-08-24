package snownee.jade.addon.vanilla;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerData;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public enum MobSpawnerCooldownProvider implements IBlockComponentProvider, StreamServerDataProvider<BlockAccessor, Integer> {
   INSTANCE;

   public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
      if (config.get(JadeIds.MC_MOB_SPAWNER)) {
         int cooldown = this.decodeFromData(accessor).orElse(0);
         tooltip.add(Component.translatable("jade.trial_spawner_cd", new Object[]{IThemeHelper.get().seconds(cooldown, accessor.tickRate())}));
      }
   }

   @Nullable
   public Integer streamData(BlockAccessor accessor) {
      TrialSpawnerBlockEntity spawner = (TrialSpawnerBlockEntity)accessor.getBlockEntity();
      TrialSpawnerData spawnerData = spawner.getTrialSpawner().getData();
      ServerLevel level = (ServerLevel)accessor.getLevel();
      return spawner.getTrialSpawner().canSpawnInLevel(level) && level.getGameTime() < spawnerData.cooldownEndsAt
         ? (int)(spawnerData.cooldownEndsAt - level.getGameTime())
         : null;
   }

   @Override
   public StreamCodec<RegistryFriendlyByteBuf, Integer> streamCodec() {
      return ByteBufCodecs.VAR_INT.cast();
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.MC_MOB_SPAWNER_COOLDOWN;
   }

   @Override
   public boolean isRequired() {
      return true;
   }
}
