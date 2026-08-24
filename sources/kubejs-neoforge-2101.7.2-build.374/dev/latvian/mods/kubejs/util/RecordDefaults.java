package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.rhino.type.RecordTypeInfo;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteMaps;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2ShortMap;
import it.unimi.dsi.fastutil.objects.Object2ShortMaps;
import java.util.Optional;
import net.minecraft.advancements.critereon.GameTypePredicate;
import net.minecraft.advancements.critereon.EntityPredicate.LocationWrapper;
import net.minecraft.advancements.critereon.MinMaxBounds.Doubles;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.core.component.DataComponentPredicate;

public class RecordDefaults {
   private static <T> void add(Class<T> type, T def) {
      RecordTypeInfo.setGlobalDefaultValue(type, def);
   }

   public static void init() {
      add(Object2BooleanMap.class, Object2BooleanMaps.emptyMap());
      add(Object2ByteMap.class, Object2ByteMaps.emptyMap());
      add(Object2ShortMap.class, Object2ShortMaps.emptyMap());
      add(Object2IntMap.class, Object2IntMaps.emptyMap());
      add(Object2LongMap.class, Object2LongMaps.emptyMap());
      add(Object2FloatMap.class, Object2FloatMaps.emptyMap());
      add(Object2DoubleMap.class, Object2DoubleMaps.emptyMap());
      add(Ints.class, Ints.ANY);
      add(Doubles.class, Doubles.ANY);
      add(DataComponentPredicate.class, DataComponentPredicate.EMPTY);
      add(LocationWrapper.class, new LocationWrapper(Optional.empty(), Optional.empty(), Optional.empty()));
      add(GameTypePredicate.class, GameTypePredicate.ANY);
      add(Tristate.class, Tristate.DEFAULT);
   }
}
