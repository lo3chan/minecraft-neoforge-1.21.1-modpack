package traben.entity_model_features.mod_compat;

import com.zigythebird.playeranim.accessors.IAnimatedPlayer;
import com.zigythebird.playeranim.animation.PlayerAnimManager;
import traben.entity_model_features.utils.EMFEntity;

public class PALCompat {
   public static boolean shouldPauseEntityAnim(EMFEntity entity) {
      if (!(entity instanceof IAnimatedPlayer player)) {
         return false;
      } else {
         PlayerAnimManager manager = player.playerAnimLib$getAnimManager();
         return manager != null && manager.isActive();
      }
   }
}
