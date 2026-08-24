package net.diebuddies.physics.verlet.constraints;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.verlet.VerletLine;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.VerletStick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.joml.Matrix4fStack;
import org.joml.Vector3d;

public class FishingHookConstraint implements VerletConstraint {
   private Vector3d playerPosAsync = new Vector3d();
   private Vector3d hookPosAsync = new Vector3d();
   private Vector3d playerPos = new Vector3d();
   private Vector3d hookPos = new Vector3d();
   private FishingHook fishingHook;
   private Player player;
   private EntityRenderDispatcher entityRenderDispatcher;

   public FishingHookConstraint(
      VerletSimulation simulation, FishingHook fishingHook, Player player, EntityRenderDispatcher entityRenderDispatcher, float tickDelta
   ) {
      this.fishingHook = fishingHook;
      this.player = player;
      this.entityRenderDispatcher = entityRenderDispatcher;
      this.calculatePlayerAndHookPos(tickDelta, this.playerPos, this.hookPos);
      int pointCount = 48;
      double totalLength = ConfigClient.fishingLineLength;

      for (int i = 0; i < pointCount; i++) {
         float perc = (float)(i - 1) / pointCount;
         Vector3d position = new Vector3d(
            Math.lerp(this.playerPos.x, this.hookPos.x, perc),
            Math.lerp(this.playerPos.y, this.hookPos.y, perc),
            Math.lerp(this.playerPos.z, this.hookPos.z, perc)
         );
         VerletPoint point = new VerletPoint(position);
         point.uv.set(0.01F, 0.99F);
         point.rgba.set(0.0F, 0.0F, 0.0F, 1.0F);
         point.locked = i == 0 || i == pointCount - 1;
         simulation.addPoint(point);
      }

      for (int i = 0; i < pointCount - 1; i++) {
         simulation.addStick(new VerletStick(simulation.getPoints().get(i), simulation.getPoints().get(i + 1), totalLength / pointCount));
         simulation.addLine(new VerletLine(simulation.getPoints().get(i), simulation.getPoints().get(i + 1)));
      }
   }

   private void calculatePlayerAndHookPos(float tickDelta, Vector3d playerPos, Vector3d hookPos) {
      int arm = this.player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
      ItemStack itemStack = this.player.getMainHandItem();
      if (!itemStack.is(Items.FISHING_ROD)) {
         arm = -arm;
      }

      float attackRotation = this.player.getAttackAnim(tickDelta);
      float attackRotationSin = Mth.sin(Mth.sqrt(attackRotation) * 3.1415927F);
      float bodyRotation = Mth.lerp(tickDelta, this.player.yBodyRotO, this.player.yBodyRot) * 0.017453292F;
      double bodyRotationSin = Mth.sin(bodyRotation);
      double bodyRotationCos = Mth.cos(bodyRotation);
      double armShort = arm * 0.35;
      double playerX;
      double playerY;
      double playerZ;
      float playerEyeHeight;
      if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson())
         && this.player == Minecraft.getInstance().player) {
         double hookX = 960.0 / ((Integer)Minecraft.getInstance().options.fov().get()).floatValue();
         Vec3 firstPersonOffset = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane(arm * 0.525F, -0.1F);
         firstPersonOffset = firstPersonOffset.scale(hookX);
         firstPersonOffset = firstPersonOffset.yRot(attackRotationSin * 0.5F);
         firstPersonOffset = firstPersonOffset.xRot(-attackRotationSin * 0.7F);
         playerX = Mth.lerp(tickDelta, this.player.xo, this.player.getX()) + firstPersonOffset.x;
         playerY = Mth.lerp(tickDelta, this.player.yo, this.player.getY()) + firstPersonOffset.y;
         playerZ = Mth.lerp(tickDelta, this.player.zo, this.player.getZ()) + firstPersonOffset.z;
         playerEyeHeight = this.player.getEyeHeight();
      } else {
         playerX = Mth.lerp(tickDelta, this.player.xo, this.player.getX()) - bodyRotationCos * armShort - bodyRotationSin * 0.8;
         playerY = this.player.yo + this.player.getEyeHeight() + (this.player.getY() - this.player.yo) * tickDelta - 0.45;
         playerZ = Mth.lerp(tickDelta, this.player.zo, this.player.getZ()) - bodyRotationSin * armShort + bodyRotationCos * 0.8;
         playerEyeHeight = this.player.isCrouching() ? -0.1875F : 0.0F;
      }

      double hookX = Mth.lerp(tickDelta, this.fishingHook.xo, this.fishingHook.getX());
      double hookY = Mth.lerp(tickDelta, this.fishingHook.yo, this.fishingHook.getY()) + 0.25;
      double hookZ = Mth.lerp(tickDelta, this.fishingHook.zo, this.fishingHook.getZ());
      if (ConfigClient.areOceanPhysicsEnabled()) {
         OceanWorld oceanWorld = PhysicsMod.getInstance(this.player.level()).getPhysicsWorld().getOceanWorld();
         playerY += oceanWorld.computeYOffset(this.player.level(), this.player, 1.0F);
         hookY += oceanWorld.computeYOffset(this.fishingHook.level(), this.fishingHook, 1.0F);
      }

      playerPos.set(playerX, playerY + playerEyeHeight, playerZ);
      hookPos.set(hookX, hookY, hookZ);
   }

   @Override
   public boolean initAsyncData(PhysicsWorld world, VerletSimulation simulation) {
      this.calculatePlayerAndHookPos(1.0F, this.playerPosAsync, this.hookPosAsync);
      return false;
   }

   @Override
   public void updateBefore(double delta, VerletSimulation simulation) {
      VerletPoint armPoint = simulation.getPoints().get(0);
      VerletPoint hookPoint = simulation.getPoints().get(simulation.getPoints().size() - 1);
      armPoint.position.set(this.playerPosAsync).sub(simulation.getOffset());
      hookPoint.position.set(this.hookPosAsync).sub(simulation.getOffset());
   }

   @Override
   public void subStep(double percent, VerletSimulation simulation) {
   }

   @Override
   public void updateAfter(double delta, VerletSimulation simulation) {
   }

   @Override
   public void renderBefore(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
      this.calculatePlayerAndHookPos((float)delta, this.playerPos, this.hookPos);
      VerletPoint armPoint = simulation.getPoints().get(0);
      VerletPoint hookPoint = simulation.getPoints().get(simulation.getPoints().size() - 1);
      armPoint.bufferPosition.set(this.playerPos).sub(simulation.getOffset());
      armPoint.bufferPrevPosition.set(armPoint.bufferPosition);
      hookPoint.bufferPosition.set(this.hookPos).sub(simulation.getOffset());
      hookPoint.bufferPrevPosition.set(hookPoint.bufferPosition);
   }

   @Override
   public void renderAfter(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
   }

   @Override
   public void render(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
   }
}
