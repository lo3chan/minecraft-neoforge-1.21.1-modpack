package dev.tr7zw.waveycapes.versionless;

import dev.tr7zw.waveycapes.versionless.nms.MinecraftPlayer;
import dev.tr7zw.waveycapes.versionless.sim.BasicSimulation;
import dev.tr7zw.waveycapes.versionless.sim.StickSimulation;
import dev.tr7zw.waveycapes.versionless.sim.StickSimulation3d;
import dev.tr7zw.waveycapes.versionless.sim.StickSimulationDungeons;
import dev.tr7zw.waveycapes.versionless.util.Mth;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import java.util.UUID;

public interface CapeHolder {
   BasicSimulation getSimulation();

   Vector3 getLastPlayerAnimatorPosition();

   void setLastPlayerAnimatorPosition(Vector3 var1);

   void setSimulation(BasicSimulation var1);

   UUID getWCUUID();

   void setDirty();

   default void updateSimulation(int partCount) {
      BasicSimulation simulation = this.getSimulation();
      if (simulation == null || this.incorrectSimulation(simulation)) {
         simulation = this.createSimulation();
         this.setSimulation(simulation);
      }

      if (simulation != null) {
         if (simulation.init(partCount)) {
            this.setDirty();
         }
      }
   }

   default boolean incorrectSimulation(BasicSimulation sim) {
      CapeMovement style = ModBase.config.capeMovement;
      if (style == CapeMovement.BASIC_SIMULATION && sim.getClass() != StickSimulation.class) {
         return true;
      } else {
         return style == CapeMovement.BASIC_SIMULATION_3D && sim.getClass() != StickSimulation3d.class
            ? true
            : style == CapeMovement.DUNGEONS && sim.getClass() != StickSimulationDungeons.class;
      }
   }

   default BasicSimulation createSimulation() {
      CapeMovement style = ModBase.config.capeMovement;
      if (style == CapeMovement.BASIC_SIMULATION) {
         return new StickSimulation();
      } else if (style == CapeMovement.BASIC_SIMULATION_3D) {
         return new StickSimulation3d();
      } else {
         return style == CapeMovement.DUNGEONS ? new StickSimulationDungeons() : null;
      }
   }

   default void simulate(MinecraftPlayer abstractClientPlayer) {
      BasicSimulation simulation = this.getSimulation();
      if (simulation != null && !simulation.empty()) {
         double d = abstractClientPlayer.getXCloak() - abstractClientPlayer.getX();
         double m = abstractClientPlayer.getZCloak() - abstractClientPlayer.getZ();
         float n = abstractClientPlayer.getYBodyRotO() + abstractClientPlayer.getYBodyRot() - abstractClientPlayer.getYBodyRotO();
         double o = Mth.sin(n * 0.017453292F);
         double p = -Mth.cos(n * 0.017453292F);
         float heightMul = ModBase.config.heightMultiplier;
         float straveMul = ModBase.config.straveMultiplier;
         if (abstractClientPlayer.isUnderWater()) {
            heightMul *= 2.0F;
         }

         double fallHack = Mth.clamp((abstractClientPlayer.getYo() - abstractClientPlayer.getY()) * 10.0, 0.0, 1.0);
         if (abstractClientPlayer.isUnderWater()) {
            simulation.setGravity(ModBase.config.gravity / 10.0F);
         } else {
            simulation.setGravity(ModBase.config.gravity);
         }

         Vector3 gravity = new Vector3(0.0F, -1.0F, 0.0F);
         StickSimulation.Vector2 strave = new StickSimulation.Vector2(
            (float)(abstractClientPlayer.getX() - abstractClientPlayer.getXo()), (float)(abstractClientPlayer.getZ() - abstractClientPlayer.getZo())
         );
         strave.rotateDegrees(-abstractClientPlayer.getYRot());
         double changeX = d * o + m * p + fallHack + (abstractClientPlayer.isCrouching() && !simulation.isSneaking() ? 3 : 0);
         double changeY = (abstractClientPlayer.getY() - abstractClientPlayer.getYo()) * heightMul
            + (abstractClientPlayer.isCrouching() && !simulation.isSneaking() ? 1 : 0);
         double changeZ = -strave.x * straveMul;
         simulation.setSneaking(abstractClientPlayer.isCrouching());
         Vector3 change = new Vector3((float)changeX, (float)changeY, (float)changeZ);
         if (abstractClientPlayer.isVisuallySwimming()) {
            float rotation = abstractClientPlayer.getXRot();
            rotation += 90.0F;
            gravity.rotateDegrees(rotation);
            change.rotateDegrees(rotation);
         }

         simulation.setGravityDirection(gravity);
         change = ModBase.getINSTANCE().applyModAnimations(abstractClientPlayer, change);
         simulation.applyMovement(change);
         simulation.simulate();
      }
   }
}
