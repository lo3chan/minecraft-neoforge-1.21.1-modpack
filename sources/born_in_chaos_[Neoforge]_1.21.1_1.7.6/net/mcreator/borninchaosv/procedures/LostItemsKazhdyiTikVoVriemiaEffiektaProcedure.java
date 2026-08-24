package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class LostItemsKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         if (world instanceof ServerLevel _level) {
            _level.getServer()
               .getCommands()
               .performPrefixedCommand(
                  new CommandSourceStack(
                        CommandSource.NULL,
                        new Vec3(entity.getX(), entity.getY(), entity.getZ()),
                        Vec2.ZERO,
                        _level,
                        4,
                        "",
                        Component.literal(""),
                        _level.getServer(),
                        null
                     )
                     .withSuppressedOutput(),
                  "execute as @e[type=minecraft:item,nbt={OnGround:1b},distance=..10] run data merge entity @s {Glowing:1}"
               );
         }

         if ((
                  entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.LOST_ITEMS)
                     ? _livEnt.getEffect(BornInChaosV1ModMobEffects.LOST_ITEMS).getDuration()
                     : 0
               )
               < 20
            && entity instanceof LivingEntity _entity) {
            _entity.removeEffect(BornInChaosV1ModMobEffects.LOST_ITEMS);
         }
      }
   }
}
