package net.mehvahdjukaar.moonlight.api.item;

import java.lang.reflect.Field;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class ModBucketItem extends BucketItem {
   private static final Field CONTENT;
   private final Supplier<Fluid> supplier;

   public ModBucketItem(Supplier<Fluid> fluid, Properties properties) {
      super(PlatHelper.getPlatform().isForge() ? Fluids.EMPTY : fluid.get(), properties);
      this.supplier = fluid;
      if (PlatHelper.getPlatform().isForge()) {
         try {
            CONTENT.setAccessible(true);
            CONTENT.set(this, null);
         } catch (Exception var4) {
            throw new RuntimeException(var4);
         }
      }
   }

   public Fluid getFluid() {
      return this.supplier.get();
   }

   static {
      Field c = null;

      for (Field field : BucketItem.class.getDeclaredFields()) {
         if (field.getType() == Fluid.class) {
            c = field;
            break;
         }
      }

      CONTENT = c;
   }
}
