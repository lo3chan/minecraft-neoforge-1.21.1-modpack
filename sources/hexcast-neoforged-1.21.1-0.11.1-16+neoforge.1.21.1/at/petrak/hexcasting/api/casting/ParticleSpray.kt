package at.petrak.hexcasting.api.casting

import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.msgs.MsgCastParticleS2C
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec3

public data class ParticleSpray(pos: Vec3, vel: Vec3, fuzziness: Double, spread: Double, count: Int = 20) {
   public final val pos: Vec3
   public final val vel: Vec3
   public final val fuzziness: Double
   public final val spread: Double
   public final val count: Int

   init {
      this.pos = pos;
      this.vel = vel;
      this.fuzziness = fuzziness;
      this.spread = spread;
      this.count = count;
   }

   public fun sprayParticles(world: ServerLevel, color: FrozenPigment) {
      IXplatAbstractions.INSTANCE.sendPacketNear(this.pos, 128.0, world, new MsgCastParticleS2C(this, color));
   }

   public operator fun component1(): Vec3 {
      return this.pos;
   }

   public operator fun component2(): Vec3 {
      return this.vel;
   }

   public operator fun component3(): Double {
      return this.fuzziness;
   }

   public operator fun component4(): Double {
      return this.spread;
   }

   public operator fun component5(): Int {
      return this.count;
   }

   public fun copy(pos: Vec3 = this.pos, vel: Vec3 = this.vel, fuzziness: Double = this.fuzziness, spread: Double = this.spread, count: Int = this.count): ParticleSpray {
      return new ParticleSpray(pos, vel, fuzziness, spread, count);
   }

   public override fun toString(): String {
      return "ParticleSpray(pos=${this.pos}, vel=${this.vel}, fuzziness=${this.fuzziness}, spread=${this.spread}, count=${this.count})";
   }

   public override fun hashCode(): Int {
      return (((this.pos.hashCode() * 31 + this.vel.hashCode()) * 31 + java.lang.Double.hashCode(this.fuzziness)) * 31 + java.lang.Double.hashCode(this.spread))
            * 31
         + Integer.hashCode(this.count);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is ParticleSpray) {
         return false;
      } else {
         val var2: ParticleSpray = other as ParticleSpray;
         if (!(this.pos == (other as ParticleSpray).pos)) {
            return false;
         } else if (!(this.vel == var2.vel)) {
            return false;
         } else if (java.lang.Double.compare(this.fuzziness, var2.fuzziness) != 0) {
            return false;
         } else if (java.lang.Double.compare(this.spread, var2.spread) != 0) {
            return false;
         } else {
            return this.count == var2.count;
         }
      }
   }

   public companion object {
      public fun burst(pos: Vec3, size: Double, count: Int = 20): ParticleSpray {
         return new ParticleSpray(pos, new Vec3(size, 0.0, 0.0), 0.0, 3.14, count);
      }

      public fun cloud(pos: Vec3, size: Double, count: Int = 20): ParticleSpray {
         return new ParticleSpray(pos, new Vec3(0.0, 0.001, 0.0), size, 0.0, count);
      }
   }
}
