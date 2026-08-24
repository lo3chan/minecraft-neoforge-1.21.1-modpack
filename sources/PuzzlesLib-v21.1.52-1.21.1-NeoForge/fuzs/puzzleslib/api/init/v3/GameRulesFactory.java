package fuzs.puzzleslib.api.init.v3;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import java.util.function.BiConsumer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Category;
import net.minecraft.world.level.GameRules.IntegerValue;
import net.minecraft.world.level.GameRules.Key;
import net.minecraft.world.level.GameRules.Type;
import net.minecraft.world.level.GameRules.Value;

public interface GameRulesFactory {
   GameRulesFactory INSTANCE = ProxyImpl.get().getGameRulesFactory();

   default Key<BooleanValue> registerBooleanRule(String name, Category category, boolean defaultValue) {
      return this.register(name, category, this.createBooleanRule(defaultValue));
   }

   default Key<IntegerValue> registerIntRule(String name, Category category, int defaultValue) {
      return this.register(name, category, this.createIntRule(defaultValue));
   }

   <T extends Value<T>> Key<T> register(String var1, Category var2, Type<T> var3);

   default Type<BooleanValue> createBooleanRule(boolean defaultValue) {
      return this.createBooleanRule(defaultValue, (server, booleanValue) -> {});
   }

   Type<BooleanValue> createBooleanRule(boolean var1, BiConsumer<MinecraftServer, BooleanValue> var2);

   default Type<IntegerValue> createIntRule(int defaultValue) {
      return this.createIntRule(defaultValue, -2147483648);
   }

   default Type<IntegerValue> createIntRule(int defaultValue, int minimumValue) {
      return this.createIntRule(defaultValue, minimumValue, 2147483647);
   }

   default Type<IntegerValue> createIntRule(int defaultValue, int minimumValue, int maximumValue) {
      return this.createIntRule(defaultValue, minimumValue, maximumValue, (server, integerValue) -> {});
   }

   default Type<IntegerValue> createIntRule(int defaultValue, BiConsumer<MinecraftServer, IntegerValue> callback) {
      return this.createIntRule(defaultValue, -2147483648, callback);
   }

   default Type<IntegerValue> createIntRule(int defaultValue, int minimumValue, BiConsumer<MinecraftServer, IntegerValue> callback) {
      return this.createIntRule(defaultValue, minimumValue, 2147483647, callback);
   }

   Type<IntegerValue> createIntRule(int var1, int var2, int var3, BiConsumer<MinecraftServer, IntegerValue> var4);
}
