package vazkii.patchouli.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BookOpenTrigger extends SimpleCriterionTrigger<BookOpenTrigger.TriggerInstance> {
   public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("patchouli", "open_book");
   public static final BookOpenTrigger INSTANCE = new BookOpenTrigger();

   @NotNull
   public Codec<BookOpenTrigger.TriggerInstance> codec() {
      return BookOpenTrigger.TriggerInstance.CODEC;
   }

   public void trigger(@NotNull ServerPlayer player, @NotNull ResourceLocation book) {
      this.trigger(player, instance -> instance.matches(book, null, 0));
   }

   public void trigger(@NotNull ServerPlayer player, @NotNull ResourceLocation book, @Nullable ResourceLocation entry, int page) {
      this.trigger(player, instance -> instance.matches(book, entry, page));
   }

   public record TriggerInstance(Optional<ContextAwarePredicate> player, ResourceLocation book, Optional<ResourceLocation> entry, Ints page)
      implements SimpleInstance {
      public static Codec<BookOpenTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(BookOpenTrigger.TriggerInstance::player),
               ResourceLocation.CODEC.fieldOf("book").forGetter(BookOpenTrigger.TriggerInstance::book),
               ResourceLocation.CODEC.optionalFieldOf("entry").forGetter(BookOpenTrigger.TriggerInstance::entry),
               Ints.CODEC.optionalFieldOf("page", Ints.ANY).forGetter(BookOpenTrigger.TriggerInstance::page)
            )
            .apply(instance, BookOpenTrigger.TriggerInstance::new)
      );

      public boolean matches(@NotNull ResourceLocation book, @Nullable ResourceLocation entry, int page) {
         return this.book.equals(book) && (this.entry.isEmpty() || this.entry.get().equals(entry)) && this.page.matches(page);
      }
   }
}
