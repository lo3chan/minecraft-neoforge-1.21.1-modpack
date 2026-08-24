package com.github.alexthe666.alexsmobs.entity.util;

import com.github.alexthe666.alexsmobs.citadel.Citadel;
import com.github.alexthe666.alexsmobs.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.alexsmobs.citadel.server.message.PropertiesMessage;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class VineLassoUtil {
   private static final String LASSO_PACKET = "LassoSentPacketAlexsMobs";
   private static final String LASSO_REMOVED = "LassoRemovedAlexsMobs";
   private static final String LASSOED_TO_TAG = "LassoOwnerAlexsMobs";
   private static final String LASSOED_TO_ENTITY_ID_TAG = "LassoOwnerIDAlexsMobs";

   public static void lassoTo(@Nullable LivingEntity lassoer, LivingEntity lassoed) {
      CompoundTag lassoedTag = CitadelEntityData.getOrCreateCitadelTag(lassoed);
      if (lassoer == null) {
         AMCompat.putUUID(lassoedTag, "LassoOwnerAlexsMobs", UUID.randomUUID());
         lassoedTag.putInt("LassoOwnerIDAlexsMobs", -1);
         lassoedTag.putBoolean("LassoRemovedAlexsMobs", true);
      } else if (!lassoedTag.contains("LassoOwnerIDAlexsMobs") || AMCompat.getInt(lassoedTag, "LassoOwnerIDAlexsMobs") == -1) {
         AMCompat.putUUID(lassoedTag, "LassoOwnerAlexsMobs", lassoer.getUUID());
         lassoedTag.putInt("LassoOwnerIDAlexsMobs", lassoer.getId());
         lassoedTag.putBoolean("LassoRemovedAlexsMobs", false);
      }

      lassoedTag.putBoolean("LassoSentPacketAlexsMobs", true);
      CitadelEntityData.setCitadelTag(lassoed, lassoedTag);
      if (!lassoed.level().isClientSide()) {
         Citadel.sendMSGToAll(new PropertiesMessage("CitadelPatreonConfig", lassoedTag, lassoed.getId()));
      }
   }

   public static boolean hasLassoData(LivingEntity lasso) {
      CompoundTag lassoedTag = CitadelEntityData.getOrCreateCitadelTag(lasso);
      return lassoedTag.contains("LassoOwnerIDAlexsMobs")
         && !AMCompat.getBoolean(lassoedTag, "LassoRemovedAlexsMobs")
         && AMCompat.getInt(lassoedTag, "LassoOwnerIDAlexsMobs") != -1;
   }

   public static Entity getLassoedTo(LivingEntity lassoed) {
      CompoundTag lassoedTag = CitadelEntityData.getOrCreateCitadelTag(lassoed);
      if (AMCompat.getBoolean(lassoedTag, "LassoRemovedAlexsMobs")) {
         return null;
      } else {
         if (hasLassoData(lassoed)) {
            if (lassoed.level().isClientSide() && lassoedTag.contains("LassoOwnerIDAlexsMobs")) {
               int i = AMCompat.getInt(lassoedTag, "LassoOwnerIDAlexsMobs");
               if (i != -1) {
                  Entity found = lassoed.level().getEntity(i);
                  if (found != null) {
                     return found;
                  }

                  UUID uuid = AMCompat.getUUID(lassoedTag, "LassoOwnerAlexsMobs");
                  if (uuid != null) {
                     return lassoed.level().getPlayerByUUID(uuid);
                  }
               }
            } else if (lassoed.level() instanceof ServerLevel) {
               UUID uuid = AMCompat.getUUID(lassoedTag, "LassoOwnerAlexsMobs");
               if (uuid != null) {
                  Entity foundx = ((ServerLevel)lassoed.level()).getEntity(uuid);
                  if (foundx != null) {
                     lassoedTag.putInt("LassoOwnerIDAlexsMobs", foundx.getId());
                     return foundx;
                  }
               }
            }
         }

         return null;
      }
   }

   public static void tickLasso(LivingEntity lassoed) {
      CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(lassoed);
      if (!lassoed.level().isClientSide() && (tag.contains("LassoSentPacketAlexsMobs") || AMCompat.getBoolean(tag, "LassoRemovedAlexsMobs"))) {
         tag.putBoolean("LassoSentPacketAlexsMobs", false);
         CitadelEntityData.setCitadelTag(lassoed, tag);
         Citadel.sendMSGToAll(new PropertiesMessage("CitadelPatreonConfig", tag, lassoed.getId()));
      }

      Entity lassoedOwner = getLassoedTo(lassoed);
      if (lassoedOwner != null) {
         double distance = lassoed.distanceTo(lassoedOwner);
         if (lassoed instanceof Mob mob) {
            if (distance > 3.0) {
               mob.getNavigation().moveTo(lassoedOwner, 1.0);
            } else {
               mob.getNavigation().stop();
            }
         }

         if (distance > 10.0) {
            double d0 = (lassoedOwner.getX() - lassoed.getX()) / distance;
            double d1 = (lassoedOwner.getY() - lassoed.getY()) / distance;
            double d2 = (lassoedOwner.getZ() - lassoed.getZ()) / distance;
            double yd = Math.copySign(d1 * d1 * 0.4, d1);
            if (lassoed instanceof Player) {
               yd = 0.0;
            }

            lassoed.setDeltaMovement(lassoed.getDeltaMovement().add(Math.copySign(d0 * d0 * 0.4, d0), yd, Math.copySign(d2 * d2 * 0.4, d2)));
         }
      }
   }
}
