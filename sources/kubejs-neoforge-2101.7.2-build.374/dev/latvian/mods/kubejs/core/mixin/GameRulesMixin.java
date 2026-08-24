package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.GameRulesKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.GameRuleTypeVisitor;
import net.minecraft.world.level.GameRules.Key;
import net.minecraft.world.level.GameRules.Type;
import net.minecraft.world.level.GameRules.Value;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@RemapPrefixForJS("kjs$")
@Mixin({GameRules.class})
public abstract class GameRulesMixin implements GameRulesKJS {
   @Unique
   private Map<String, Key<?>> kjs$keyCache;

   @Shadow
   public abstract <T extends Value<T>> T getRule(Key<T> key);

   @Unique
   @Nullable
   private Key<?> kjs$getKey(String rule) {
      if (this.kjs$keyCache == null) {
         this.kjs$keyCache = new HashMap<>();
         GameRules.visitGameRuleTypes(new GameRuleTypeVisitor() {
            public <T extends Value<T>> void visit(Key<T> key, Type<T> type) {
               GameRulesMixin.this.kjs$keyCache.put(key.toString(), key);
            }
         });
      }

      return this.kjs$keyCache.get(rule);
   }

   @Nullable
   @Override
   public Value<?> kjs$get(String rule) {
      Key<? extends Value<?>> key = (Key<? extends Value<?>>)this.kjs$getKey(rule);
      return key == null ? null : this.getRule((Key<Value<?>>)key);
   }

   @Override
   public void kjs$set(String rule, String value) {
      Key<? extends Value<?>> key = (Key<? extends Value<?>>)this.kjs$getKey(rule);
      Value<?> r = key == null ? null : this.getRule((Key<Value<?>>)key);
      if (r != null) {
         r.deserialize(value);
         r.onChanged(ServerLifecycleHooks.getCurrentServer());
      }
   }
}
