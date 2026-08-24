package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.BabySpiderControlledEntity;
import net.mcreator.borninchaosv.entity.BabySpiderEntity;
import net.mcreator.borninchaosv.entity.MotherSpiderEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class CobwebCoverPriStolknovieniiSushchnostiSBlokomProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (!(entity instanceof Spider)
            && !(entity instanceof CaveSpider)
            && !(entity instanceof BabySpiderEntity)
            && !(entity instanceof MotherSpiderEntity)
            && !(entity instanceof BabySpiderControlledEntity)) {
            entity.makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(0.25, 0.05, 0.25));
         }
      }
   }
}
