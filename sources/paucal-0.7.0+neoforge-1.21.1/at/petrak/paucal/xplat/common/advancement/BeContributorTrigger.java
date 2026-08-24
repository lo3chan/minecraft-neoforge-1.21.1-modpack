package at.petrak.paucal.xplat.common.advancement;

import at.petrak.paucal.api.contrib.Contributor;
import at.petrak.paucal.xplat.common.ContributorsManifest;
import at.petrak.paucal.xplat.common.ModRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.server.level.ServerPlayer;

public class BeContributorTrigger extends SimpleCriterionTrigger<BeContributorTrigger.Instance> {
   public static final Codec<BeContributorTrigger.Instance> CODEC = RecordCodecBuilder.create(
      i -> i.group(
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(BeContributorTrigger.Instance::player),
            Ints.CODEC.fieldOf("patron_level").forGetter(BeContributorTrigger.Instance::patronLevel),
            Codec.BOOL.optionalFieldOf("is_dev").forGetter(BeContributorTrigger.Instance::isDev)
         )
         .apply(i, BeContributorTrigger.Instance::new)
   );

   public void trigger(ServerPlayer player) {
      super.trigger(player, inst -> {
         UUID uuid = player.getUUID();
         Contributor profile = ContributorsManifest.getContributor(uuid);
         return profile == null ? false : inst.patronLevel.matches(profile.getLevel()) && (inst.isDev.isEmpty() || inst.isDev.get() == profile.isDev());
      });
   }

   public Codec<BeContributorTrigger.Instance> codec() {
      return CODEC;
   }

   public record Instance(Optional<ContextAwarePredicate> player, Ints patronLevel, Optional<Boolean> isDev) implements SimpleInstance {
      public Criterion<BeContributorTrigger.Instance> criterion() {
         return ((BeContributorTrigger)ModRegistries.BE_CONTRIBUTOR_TRIGGER.get()).createCriterion(this);
      }
   }
}
