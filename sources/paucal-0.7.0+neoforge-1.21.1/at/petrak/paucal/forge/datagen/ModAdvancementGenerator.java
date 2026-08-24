package at.petrak.paucal.forge.datagen;

import at.petrak.paucal.api.PaucalAPI;
import at.petrak.paucal.xplat.common.advancement.BeContributorTrigger;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.core.HolderLookup.Provider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.AdvancementProvider.AdvancementGenerator;

public class ModAdvancementGenerator implements AdvancementGenerator {
   public void generate(Provider arg, Consumer<AdvancementHolder> consumer, ExistingFileHelper existingFileHelper) {
      Builder.recipeAdvancement()
         .addCriterion("on_login", new BeContributorTrigger.Instance(Optional.empty(), Ints.atLeast(1), Optional.empty()).criterion())
         .rewards(net.minecraft.advancements.AdvancementRewards.Builder.function(PaucalAPI.modLoc("welcome")))
         .save(consumer, "paucal:be_patron");
   }
}
