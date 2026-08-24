package dev.latvian.mods.kubejs.entity;

import com.google.common.base.Predicates;
import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute.Sentiment;
import net.neoforged.neoforge.common.BooleanAttribute;

@ReturnsSelf
public class AttributeBuilder extends BuilderBase<Attribute> {
   public final List<Predicate<EntityType<?>>> predicateList = new ArrayList<>();
   public Either<AttributeBuilder.Range, Boolean> defaultValue;
   public boolean syncable = true;
   public Sentiment sentiment;

   public AttributeBuilder(ResourceLocation id) {
      super(id);
   }

   public AttributeBuilder bool(boolean defaultValue) {
      this.defaultValue = Either.right(defaultValue);
      return this;
   }

   public AttributeBuilder range(double defaultValue, double min, double max) {
      this.defaultValue = Either.left(new AttributeBuilder.Range(defaultValue, min, max));
      return this;
   }

   public AttributeBuilder syncable(boolean watch) {
      this.syncable = watch;
      return this;
   }

   public AttributeBuilder sentiment(Sentiment sentiment) {
      this.sentiment = sentiment;
      return this;
   }

   public AttributeBuilder negativeSentiment() {
      return this.sentiment(Sentiment.NEGATIVE);
   }

   public AttributeBuilder neutralSentiment() {
      return this.sentiment(Sentiment.NEUTRAL);
   }

   public AttributeBuilder attachTo(Predicate<EntityType<?>> entityType) {
      this.predicateList.add(entityType);
      return this;
   }

   public AttributeBuilder attachToPlayers() {
      this.predicateList.add(entityType -> entityType == EntityType.PLAYER);
      return this;
   }

   public AttributeBuilder attachToMonsters() {
      this.predicateList.add(entityType -> entityType.getCategory() == MobCategory.MONSTER);
      return this;
   }

   public AttributeBuilder attachToCategory(MobCategory category) {
      this.predicateList.add(entityType -> entityType.getCategory() == category);
      return this;
   }

   @HideFromJS
   public List<Predicate<EntityType<?>>> getPredicateList() {
      return Collections.unmodifiableList(this.predicateList);
   }

   public Attribute createObject() {
      if (this.defaultValue == null) {
         throw new IllegalArgumentException("Not possible to create a Boolean or Ranged Attribute. Use bool() or range() methods.");
      } else {
         return (Attribute)Either.unwrap(
            this.defaultValue
               .mapBoth(
                  l -> new RangedAttribute(this.getBuilderTranslationKey(), l.defaultValue, l.min, l.max),
                  r -> new BooleanAttribute(this.getBuilderTranslationKey(), r)
               )
         );
      }
   }

   public Attribute transformObject(Attribute attribute) {
      if (this.syncable) {
         attribute.setSyncable(true);
      }

      if (this.sentiment != null) {
         attribute.setSentiment(this.sentiment);
      }

      if (this.predicateList.isEmpty()) {
         this.predicateList.add(Predicates.alwaysTrue());
      }

      return attribute;
   }

   public record Range(double defaultValue, double min, double max) {
   }
}
