package dev.worldgen.lithostitched.mixin.common.template.mansion;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.worldgen.lithostitched.duck.RegistryHolder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces.FirstFloorRoomCollection;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces.MansionPiecePlacer;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces.SecondFloorRoomCollection;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces.ThirdFloorRoomCollection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({MansionPiecePlacer.class})
public class PiecePlacerMixin implements RegistryHolder {
   private RegistryAccess registries;

   @Override
   public RegistryAccess getRegistries() {
      return this.registries;
   }

   @Override
   public void setRegistries(RegistryAccess registries) {
      this.registries = registries;
   }

   @WrapOperation(
      method = {"createMansion"},
      at = {@At(
         value = "NEW",
         target = "net/minecraft/world/level/levelgen/structure/structures/WoodlandMansionPieces$FirstFloorRoomCollection"
      )}
   )
   private FirstFloorRoomCollection addFirstFloorRegistries(Operation<FirstFloorRoomCollection> operation) {
      FirstFloorRoomCollection collection = (FirstFloorRoomCollection)operation.call(new Object[0]);
      ((RegistryHolder)collection).setRegistries(this.registries);
      return collection;
   }

   @WrapOperation(
      method = {"createMansion"},
      at = {@At(
         value = "NEW",
         target = "net/minecraft/world/level/levelgen/structure/structures/WoodlandMansionPieces$SecondFloorRoomCollection"
      )}
   )
   private SecondFloorRoomCollection addSecondFloorRegistries(Operation<SecondFloorRoomCollection> operation) {
      SecondFloorRoomCollection collection = (SecondFloorRoomCollection)operation.call(new Object[0]);
      ((RegistryHolder)collection).setRegistries(this.registries);
      return collection;
   }

   @WrapOperation(
      method = {"createMansion"},
      at = {@At(
         value = "NEW",
         target = "net/minecraft/world/level/levelgen/structure/structures/WoodlandMansionPieces$ThirdFloorRoomCollection"
      )}
   )
   private ThirdFloorRoomCollection addThirdFloorRegistries(Operation<ThirdFloorRoomCollection> operation) {
      ThirdFloorRoomCollection collection = (ThirdFloorRoomCollection)operation.call(new Object[0]);
      ((RegistryHolder)collection).setRegistries(this.registries);
      return collection;
   }
}
