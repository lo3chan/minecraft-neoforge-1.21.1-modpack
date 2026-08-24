package at.petrak.hexcasting.api.casting.eval;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public interface CastingEnvironmentComponent {
   CastingEnvironmentComponent.Key<?> getKey();

   public interface ExtractMedia extends CastingEnvironmentComponent {
      long onExtractMedia(long var1);
   }

   public interface HasEditPermissionsAt extends CastingEnvironmentComponent {
      boolean onHasEditPermissionsAt(BlockPos var1, boolean var2);
   }

   public interface IsVecInRange extends CastingEnvironmentComponent {
      boolean onIsVecInRange(Vec3 var1, boolean var2);
   }

   public interface Key<C extends CastingEnvironmentComponent> {
   }

   public interface PostExecution extends CastingEnvironmentComponent {
      void onPostExecution(CastResult var1);
   }
}
