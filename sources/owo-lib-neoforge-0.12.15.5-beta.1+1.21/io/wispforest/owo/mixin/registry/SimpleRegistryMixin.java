package io.wispforest.owo.mixin.registry;

import com.mojang.serialization.Lifecycle;
import io.wispforest.owo.util.OwoFreezer;
import io.wispforest.owo.util.pond.OwoSimpleRegistryExtensions;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.BaseMappedRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({MappedRegistry.class})
public abstract class SimpleRegistryMixin<T> extends BaseMappedRegistry<T> implements WritableRegistry<T>, OwoSimpleRegistryExtensions<T> {
   @Shadow
   private Map<T, Reference<T>> unregisteredIntrusiveHolders;
   @Shadow
   @Final
   private Map<ResourceKey<T>, Reference<T>> byKey;
   @Shadow
   @Final
   private Map<ResourceLocation, Reference<T>> byLocation;
   @Shadow
   @Final
   private Map<T, Reference<T>> byValue;
   @Shadow
   @Final
   private ObjectList<Reference<T>> byId;
   @Shadow
   @Final
   private Reference2IntMap<T> toId;
   @Shadow
   @Final
   private Map<ResourceKey<T>, RegistrationInfo> registrationInfos;
   @Shadow
   private Lifecycle registryLifecycle;

   @Override
   public Reference<T> owo$set(int id, ResourceKey<T> arg, T object, RegistrationInfo arg2) {
      this.byValue.remove(object);
      OwoFreezer.checkRegister("Registry Set Calls");
      Objects.requireNonNull(arg);
      Objects.requireNonNull(object);
      Reference<T> reference;
      if (this.unregisteredIntrusiveHolders != null) {
         reference = this.unregisteredIntrusiveHolders.remove(object);
         if (reference == null) {
            throw new AssertionError("Missing intrusive holder for " + arg + ":" + object);
         }

         ((ReferenceAccessor)reference).owo$setRegistryKey(arg);
      } else {
         reference = this.byKey.computeIfAbsent(arg, k -> Reference.createStandAlone(this.holderOwner(), k));
         ((ReferenceAccessor)reference).owo$setValue(object);
      }

      this.byKey.put(arg, reference);
      this.byLocation.put(arg.location(), reference);
      this.byValue.put(object, reference);
      this.byId.set(id, reference);
      this.toId.put(object, id);
      this.registrationInfos.put(arg, arg2);
      this.registryLifecycle = this.registryLifecycle.add(arg2.lifecycle());
      this.addCallbacks.forEach(tAddCallback -> tAddCallback.onAdd(this, id, arg, object));
      return reference;
   }
}
