package com.aetherteam.aether.client;

import com.aetherteam.aether.mixin.mixins.client.accessor.I18nAccessor;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;

public class TriviaGenerator {
   private final RandomSource random = RandomSource.create();
   private final List<Component> trivia = new ArrayList<>();
   private int index;

   public void generateTriviaList() {
      for (String string : I18nAccessor.aether$getLanguage().getLanguageData().keySet()) {
         if (string.startsWith("aether.pro_tips.line.")) {
            this.getTrivia().add(Component.translatable(string));
         }
      }
   }

   public void randomizeTriviaIndex() {
      if (!this.getTrivia().isEmpty()) {
         this.index = this.random.nextInt(this.getTrivia().size());
      }
   }

   @Nullable
   public Component getTriviaLine() {
      if (this.getTriviaComponent() != null) {
         Component triviaComponent = this.getTriviaComponent();
         MutableComponent prefixComponent = Component.translatable("gui.aether.pro_tip").withStyle(triviaComponent.getStyle());
         return prefixComponent.append(Component.literal(" ").append(triviaComponent));
      } else {
         return null;
      }
   }

   @Nullable
   private Component getTriviaComponent() {
      return !this.getTrivia().isEmpty() ? this.getTrivia().get(this.index) : null;
   }

   public List<Component> getTrivia() {
      return this.trivia;
   }
}
