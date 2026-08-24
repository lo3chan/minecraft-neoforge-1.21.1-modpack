package com.github.alexthe666.alexsmobs.entity.util;

import com.github.alexthe666.alexsmobs.citadel.Citadel;
import com.github.alexthe666.alexsmobs.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.alexsmobs.citadel.server.message.PropertiesMessage;
import com.github.alexthe666.alexsmobs.entity.EntityTendonSegment;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;

public class TendonWhipUtil {
   private static final String LAST_TENDON_UUID = "LastTendonUUIDAlexsMobs";
   private static final String LAST_TENDON_ID = "LastTendonIDAlexsMobs";

   private static void sync(LivingEntity enchanted, CompoundTag tag) {
      CitadelEntityData.setCitadelTag(enchanted, tag);
      if (!enchanted.level().isClientSide()) {
         Citadel.sendMSGToAll(new PropertiesMessage("CitadelTagUpdate", tag, enchanted.getId()));
      } else {
         Citadel.sendMSGToServer(new PropertiesMessage("CitadelTagUpdate", tag, enchanted.getId()));
      }
   }

   public static void setLastTendon(LivingEntity entity, EntityTendonSegment tendon) {
      CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(entity);
      if (tendon == null) {
         tag.remove("LastTendonUUIDAlexsMobs");
         tag.putInt("LastTendonIDAlexsMobs", -1);
      } else {
         AMCompat.putUUID(tag, "LastTendonUUIDAlexsMobs", tendon.getUUID());
         tag.putInt("LastTendonIDAlexsMobs", tendon.getId());
      }

      sync(entity, tag);
   }

   private static UUID getLastTendonUUID(LivingEntity entity) {
      CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(entity);
      return tag.contains("LastTendonUUIDAlexsMobs") ? AMCompat.getUUID(tag, "LastTendonUUIDAlexsMobs") : null;
   }

   public static int getLastTendonId(LivingEntity entity) {
      CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(entity);
      return tag.contains("LastTendonIDAlexsMobs") ? AMCompat.getInt(tag, "LastTendonIDAlexsMobs") : -1;
   }

   public static void retractFarTendons(Level level, LivingEntity player) {
      EntityTendonSegment last = getLastTendon(player);
      if (last != null) {
         last.remove(RemovalReason.DISCARDED);
         setLastTendon(player, null);
      }
   }

   public static boolean canLaunchTendons(Level level, LivingEntity player) {
      EntityTendonSegment last = getLastTendon(player);
      return last == null ? true : last.isRemoved() || last.distanceTo(player) > 30.0F;
   }

   public static EntityTendonSegment getLastTendon(LivingEntity player) {
      UUID uuid = getLastTendonUUID(player);
      int id = getLastTendonId(player);
      if (!player.level().isClientSide()) {
         if (uuid != null) {
            Entity e = player.level().getEntity(id);
            return e instanceof EntityTendonSegment ? (EntityTendonSegment)e : null;
         }
      } else if (id != -1) {
         Entity e = player.level().getEntity(id);
         return e instanceof EntityTendonSegment ? (EntityTendonSegment)e : null;
      }

      return null;
   }
}
