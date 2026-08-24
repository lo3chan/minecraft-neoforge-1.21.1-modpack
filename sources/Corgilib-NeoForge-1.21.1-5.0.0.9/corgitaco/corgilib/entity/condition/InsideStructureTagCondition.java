package corgitaco.corgilib.entity.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.corgilib.entity.IsInsideStructureTracker;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class InsideStructureTagCondition implements Condition {
   public static final Codec<InsideStructureTagCondition> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            TagKey.codec(Registries.STRUCTURE)
               .listOf()
               .fieldOf("structure_tag_is")
               .forGetter(insideStructureTagCondition -> insideStructureTagCondition.structureTags),
            Codec.BOOL.optionalFieldOf("in_piece", false).forGetter(insideStructureTagCondition -> insideStructureTagCondition.intersectsPiece)
         )
         .apply(builder, InsideStructureTagCondition::new)
   );
   private final List<TagKey<Structure>> structureTags;
   private final boolean intersectsPiece;

   public InsideStructureTagCondition(List<TagKey<Structure>> structureTags, boolean mustIntersectPiece) {
      if (structureTags.isEmpty()) {
         throw new IllegalArgumentException("No structures were specified.");
      } else {
         this.structureTags = structureTags;
         this.intersectsPiece = mustIntersectPiece;
      }
   }

   @Override
   public boolean passes(ConditionContext conditionContext) {
      Level world = conditionContext.world();
      LivingEntity entity = conditionContext.entity();
      if (world.isClientSide) {
         return this.clientPasses((IsInsideStructureTracker.Access)entity);
      } else {
         Registry<Structure> configuredStructureFeatures = world.registryAccess().registryOrThrow(Registries.STRUCTURE);

         for (TagKey<Structure> structureTag : this.structureTags) {
            Named<Structure> tag = configuredStructureFeatures.getOrCreateTag(structureTag);

            for (Holder<Structure> structure : tag.stream().toList()) {
               BlockPos entityPosition = entity.blockPosition();
               Optional<? extends StructureStart> possibleStructureStart = ((ServerLevel)world)
                  .structureManager()
                  .startsForStructure(SectionPos.of(entityPosition), (Structure)structure.value())
                  .stream()
                  .findFirst();
               if (possibleStructureStart.isEmpty()) {
                  return false;
               }

               StructureStart structureStart = possibleStructureStart.get();
               if (this.intersectsPiece) {
                  for (StructurePiece piece : structureStart.getPieces()) {
                     if (piece.getBoundingBox().isInside(entityPosition)) {
                        ((IsInsideStructureTracker.Access)entity)
                           .getIsInsideStructureTracker()
                           .setInside(world, entity, new IsInsideStructureTracker.IsInside(true, true));
                        return true;
                     }

                     ((IsInsideStructureTracker.Access)entity)
                        .getIsInsideStructureTracker()
                        .setInside(world, entity, new IsInsideStructureTracker.IsInside(structureStart.getBoundingBox().isInside(entityPosition), false));
                  }
               } else {
                  if (structureStart.getBoundingBox().isInside(entityPosition)) {
                     ((IsInsideStructureTracker.Access)entity)
                        .getIsInsideStructureTracker()
                        .setInside(world, entity, new IsInsideStructureTracker.IsInside(true, false));
                     return true;
                  }

                  ((IsInsideStructureTracker.Access)entity)
                     .getIsInsideStructureTracker()
                     .setInside(world, entity, new IsInsideStructureTracker.IsInside(false, true));
               }
            }
         }

         return false;
      }
   }

   private boolean clientPasses(IsInsideStructureTracker.Access entity) {
      IsInsideStructureTracker.IsInside tracker = entity.getIsInsideStructureTracker().getTracker();
      return tracker.isInsideStructurePiece() && this.intersectsPiece || tracker.isInsideStructure();
   }

   @Override
   public Codec<? extends Condition> codec() {
      return CODEC;
   }
}
