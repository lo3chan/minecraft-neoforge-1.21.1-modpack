package net.bettercombat.api.fx;

public record ParticlePlacement(
   String particle_type, float x_addition, float y_addition, float z_addition, float local_yaw, float pitch_addition, float roll_set
) {
   public static final ParticlePlacement DEFAULT = new ParticlePlacement("none", 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
}
