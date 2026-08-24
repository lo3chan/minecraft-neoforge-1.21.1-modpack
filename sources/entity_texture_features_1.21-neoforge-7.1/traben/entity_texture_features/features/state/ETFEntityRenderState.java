package traben.entity_texture_features.features.state;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;

public interface ETFEntityRenderState {
   UUID uuid();

   boolean canRenderBright();

   boolean isBlockEntity();

   EntityType<?> entityType();

   Level world();

   BlockPos blockPos();

   int optifineId();

   int optifineVehicleId();

   int blockY();

   CompoundTag nbt();

   boolean hasCustomName();

   Component customName();

   Team scoreboardTeam();

   Iterable<ItemStack> itemsEquipped();

   Iterable<ItemStack> handItems();

   Iterable<ItemStack> armorItems();

   Vec3 velocity();

   @Deprecated
   Pose pose();

   String entityKey();

   @Deprecated
   ETFEntity entity();

   float distanceTo(Entity var1);

   static void setEtfRenderStateConstructor(String reason, ETFEntityRenderState.ETFRenderStateInit init) {
      ETFUtils2.logMessage("Modifying ETF Render State constructor because: " + reason);
      ETF.etfRenderStateConstructor = init;
   }

   static ETFEntityRenderState forEntity(ETFEntity entity) {
      return ETF.etfRenderStateConstructor.make(entity);
   }

   public interface ETFRenderStateInit {
      ETFEntityRenderState make(ETFEntity var1);
   }
}
