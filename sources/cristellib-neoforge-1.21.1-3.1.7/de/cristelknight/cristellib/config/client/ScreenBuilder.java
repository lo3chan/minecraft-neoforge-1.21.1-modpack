package de.cristelknight.cristellib.config.client;

import de.cristelknight.cristellib.Constants;
import de.cristelknight.cristellib.ModLoadingUtil;
import de.cristelknight.cristellib.PlatformHelper;
import de.cristelknight.cristellib.config.client.extension.ConfigScreenExtension;
import de.cristelknight.cristellib.config.client.extension.ExtensionRegistry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ScreenBuilder {
   private final String modId;
   private final List<ConfigScreenExtension> extensions = new ArrayList<>();

   public ScreenBuilder(String modId) {
      this.modId = modId;
   }

   public Screen create(Screen parent) {
      ConfigBuilder builder = ConfigBuilder.create()
         .setParentScreen(parent)
         .setSavingRunnable(this::onConfigSave)
         .setTitle(
            Component.translatable("§7" + PlatformHelper.getModDisplayName(this.modId) + " Configuration (via %s§7)", new Object[]{Constants.MOD_COMPONENT})
         );
      this.addToBuilder(builder);
      return this.extensions.isEmpty() ? null : builder.build();
   }

   public void addToBuilder(ConfigBuilder builder) {
      for (Entry<ExtensionRegistry.ExtensionFactory<?>, ExtensionRegistry.LoadPredicate> entry : ExtensionRegistry.getExtensions().entrySet()) {
         if (entry.getValue().test(this.modId) && !this.modId.equals("minecraft")) {
            ConfigScreenExtension extension = entry.getKey().create(this.modId);
            this.extensions.add(extension);
         }
      }

      this.extensions.sort(Comparator.comparingInt(ConfigScreenExtension::priority).reversed());
      this.extensions.forEach(e -> e.addToBuilder(builder, builder.entryBuilder()));
   }

   private void onConfigSave() {
      this.extensions.forEach(ConfigScreenExtension::onSave);
   }

   public static Optional<Component[]> tooltip(String name, Map<String, String> comments) {
      String comment = comments.get(name);
      return comment != null && !comment.isEmpty() ? Optional.of(new MutableComponent[]{Component.literal(comment)}) : Optional.empty();
   }

   public static boolean shouldCreateScreen(String modId) {
      return !modId.equals("cristellib") && !modId.equals("minecraft")
         ? ExtensionRegistry.getExtensions().values().stream().anyMatch(loadPredicate -> loadPredicate.test(modId))
         : false;
   }

   public static Set<String> allModsWithScreen() {
      return ModLoadingUtil.getModIds().stream().filter(ScreenBuilder::shouldCreateScreen).collect(Collectors.toSet());
   }
}
