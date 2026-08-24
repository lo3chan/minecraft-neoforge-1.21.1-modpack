package dev.latvian.mods.kubejs.block;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.Nullable;

public class KubeJSBlockProperties extends Properties {
   public final BlockBuilder blockBuilder;

   public KubeJSBlockProperties(BlockBuilder blockBuilder, @Nullable Block copyPropertiesFrom) {
      this.blockBuilder = blockBuilder;
      if (copyPropertiesFrom != null) {
         try {
            Properties from = copyPropertiesFrom.properties();

            for (Field field : Properties.class.getDeclaredFields()) {
               if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                  field.setAccessible(true);
                  field.set(this, field.get(from));
               }
            }
         } catch (Exception var8) {
            var8.printStackTrace();
         }
      }
   }
}
