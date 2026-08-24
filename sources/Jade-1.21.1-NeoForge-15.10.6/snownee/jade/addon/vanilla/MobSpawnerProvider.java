package snownee.jade.addon.vanilla;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerData;
import org.jetbrains.annotations.Nullable;
import snownee.jade.addon.core.ObjectNameProvider;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IToggleableProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public abstract class MobSpawnerProvider implements IToggleableProvider {
   public static MobSpawnerProvider.ForBlock getBlock() {
      return MobSpawnerProvider.ForBlock.INSTANCE;
   }

   public static MobSpawnerProvider.ForEntity getEntity() {
      return MobSpawnerProvider.ForEntity.INSTANCE;
   }

   public static void appendTooltip(ITooltip tooltip, @Nullable Entity displayEntity, MutableComponent name) {
      if (displayEntity != null) {
         name = Component.translatable("jade.spawner", new Object[]{name, displayEntity.getDisplayName()});
         tooltip.replace(JadeIds.CORE_OBJECT_NAME, IThemeHelper.get().title(name));
      }
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.MC_MOB_SPAWNER;
   }

   @Override
   public int getDefaultPriority() {
      return ObjectNameProvider.getEntity().getDefaultPriority() + 10;
   }

   public static class ForBlock extends MobSpawnerProvider implements IBlockComponentProvider {
      private static final MobSpawnerProvider.ForBlock INSTANCE = new MobSpawnerProvider.ForBlock();

      public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
         MutableComponent name = accessor.getBlock().getName();
         Level level = accessor.getLevel();
         BlockPos pos = accessor.getPosition();
         if (accessor.getBlockEntity() instanceof SpawnerBlockEntity spawner) {
            appendTooltip(tooltip, spawner.getSpawner().getOrCreateDisplayEntity(level, pos), name);
         } else if (accessor.getBlockEntity() instanceof TrialSpawnerBlockEntity spawner) {
            TrialSpawnerData data = spawner.getTrialSpawner().getData();
            appendTooltip(tooltip, data.getOrCreateDisplayEntity(spawner.getTrialSpawner(), level, spawner.getState()), name);
         }
      }
   }

   public static class ForEntity extends MobSpawnerProvider implements IEntityComponentProvider {
      private static final MobSpawnerProvider.ForEntity INSTANCE = new MobSpawnerProvider.ForEntity();

      public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
         MinecartSpawner spawner = (MinecartSpawner)accessor.getEntity();
         MutableComponent name = ObjectNameProvider.getEntityName(spawner, false).copy();
         appendTooltip(tooltip, spawner.getSpawner().getOrCreateDisplayEntity(accessor.getLevel(), accessor.getEntity().blockPosition()), name);
      }
   }
}
