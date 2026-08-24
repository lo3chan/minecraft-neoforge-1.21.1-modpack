package me.lucko.spark.lib.adventure.title;

import me.lucko.spark.lib.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface TitlePart<T> {
   TitlePart<Component> TITLE = new TitlePart<Component>() {
      @Override
      public String toString() {
         return "TitlePart.TITLE";
      }
   };
   TitlePart<Component> SUBTITLE = new TitlePart<Component>() {
      @Override
      public String toString() {
         return "TitlePart.SUBTITLE";
      }
   };
   TitlePart<Title.Times> TIMES = new TitlePart<Title.Times>() {
      @Override
      public String toString() {
         return "TitlePart.TIMES";
      }
   };
}
