package net.astralya.hexalia.worldgen.gen.decorator;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import java.lang.reflect.Constructor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public final class ModTreeDecorators {
   public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS = DeferredRegister.create("hexalia", Registries.TREE_DECORATOR_TYPE);
   public static final RegistrySupplier<TreeDecoratorType<CocoonTreeDecorator>> COCOON_TREE = TREE_DECORATORS.register(
      "cocoon_tree", () -> create(CocoonTreeDecorator.CODEC)
   );
   public static final RegistrySupplier<TreeDecoratorType<CatkinTreeDecorator>> CATKIN = TREE_DECORATORS.register(
      "catkin", () -> create(CatkinTreeDecorator.CODEC)
   );

   private ModTreeDecorators() {
   }

   public static void init() {
      TREE_DECORATORS.register();
   }

   private static <P extends TreeDecorator> TreeDecoratorType<P> create(MapCodec<P> codec) {
      try {
         Constructor<TreeDecoratorType<P>> constructor = TreeDecoratorType.class.getDeclaredConstructor(MapCodec.class);
         constructor.setAccessible(true);
         return constructor.newInstance(codec);
      } catch (ReflectiveOperationException var2) {
         throw new IllegalStateException("Unable to create tree decorator type", var2);
      }
   }
}
