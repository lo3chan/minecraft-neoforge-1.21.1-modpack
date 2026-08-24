package com.seibel.distanthorizons.common.wrappers.block;

import com.seibel.distanthorizons.common.wrappers.McObjectConverter_fabric;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_5819;
import net.minecraft.class_777;
import org.jetbrains.annotations.Nullable;

public class QuadWrapper_fabric {
   private static final class_310 MC = class_310.method_1551();
   private static final class_5819 RANDOM = class_5819.method_43047();
   private static final ReentrantLock GETTER_LOCK = new ReentrantLock();

   @Nullable
   public static List<class_777> getUnculledQuads(class_2680 blockState) throws Exception {
      return getQuadsForDirection(blockState, null);
   }

   @Nullable
   public static List<class_777> getQuadsForDirection(class_2680 blockState, @Nullable EDhDirection dhDirection) throws Exception {
      GETTER_LOCK.lock();

      List var4;
      try {
         class_2350 direction = McObjectConverter_fabric.convert(dhDirection);
         List<class_777> quads = MC.method_1554().method_4743().method_3335(blockState).method_4707(blockState, direction, RANDOM);
         var4 = quads;
      } finally {
         GETTER_LOCK.unlock();
      }

      return var4;
   }
}
