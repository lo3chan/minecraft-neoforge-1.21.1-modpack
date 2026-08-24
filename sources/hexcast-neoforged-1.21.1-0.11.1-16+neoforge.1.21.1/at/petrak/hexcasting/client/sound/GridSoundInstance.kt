package at.petrak.hexcasting.client.sound

import at.petrak.hexcasting.client.gui.GuiSpellcasting
import at.petrak.hexcasting.common.lib.HexSounds
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

public class GridSoundInstance(player: Player) : AbstractTickableSoundInstance(
      HexSounds.CASTING_AMBIANCE, SoundSource.PLAYERS, SoundInstance.createUnseededRandom()
   ) {
   public final val player: Player

   public final var mousePosX: Double
      internal set

   public final var mousePosY: Double
      internal set

   init {
      this.player = player;
      this.mousePosX = 0.5;
      this.mousePosY = 0.5;
      val lookVec: Vec3 = this.player.getLookAngle();
      val playerPos: Vec3 = this.player.getEyePosition();
      this.x = playerPos.x + lookVec.x;
      this.y = playerPos.y + lookVec.y;
      this.z = playerPos.z + lookVec.z;
      this.attenuation = Attenuation.LINEAR;
      this.looping = true;
      this.delay = 0;
      this.relative = false;
   }

   public open fun tick() {
      if (Minecraft.getInstance().screen !is GuiSpellcasting) {
         this.stop();
      } else {
         val newPos: Vec3 = this.player
            .getEyePosition()
            .add(this.calculateVectorFromPitchAndYaw(this.player.getXRot(), this.player.getYRot()))
            .add(this.calculateVectorFromPitchAndYaw(this.player.getXRot() + (float)90, this.player.getYRot()).scale(1.0 * (this.mousePosX - 0.5)))
            .add(this.calculateVectorFromPitchAndYaw(this.player.getXRot(), this.player.getYRot() + (float)90).scale(1.0 * (this.mousePosY - 0.5)));
         this.x = newPos.x;
         this.y = newPos.y;
         this.z = newPos.z;
      }
   }

   private fun calculateVectorFromPitchAndYaw(pitch: Float, yaw: Float): Vec3 {
      val radiansPitch: Float = pitch * 0.017453292F;
      val xComponent: Double = Mth.cos(-yaw * 0.017453292F);
      val zComponent: Double = Mth.sin(-yaw * 0.017453292F);
      val azimuthHorizontal: Double = Mth.cos(radiansPitch);
      return new Vec3(zComponent * azimuthHorizontal, -Mth.sin(radiansPitch), xComponent * azimuthHorizontal);
   }

   public companion object {
      public const val PAN_SCALE: Double
   }
}
