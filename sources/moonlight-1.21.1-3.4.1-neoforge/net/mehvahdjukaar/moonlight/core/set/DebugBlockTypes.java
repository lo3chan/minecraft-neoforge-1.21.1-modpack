package net.mehvahdjukaar.moonlight.core.set;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.BlockTypeRegistry;
import net.mehvahdjukaar.moonlight.core.CommonConfigs;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class DebugBlockTypes {
   private static final Path debugDir = Paths.get("debug", "dynamic_registry_dump");

   public static void writeToFile() {
      try {
         Files.createDirectories(debugDir);

         for (BlockTypeRegistry<?> reg : BlockSetAPI.getRegistries()) {
            String registryName = reg.typeName().replace(":", "_");
            Path filePath = debugDir.resolve(registryName + ".txt");
            StringBuilder builder = new StringBuilder();
            builder.append("─────────────────────────────── LIST ────────────────────────────────").append(System.lineSeparator());

            for (BlockType entry : reg.getValues()) {
               builder.append(entry.getId().toString()).append(System.lineSeparator());
            }

            if (CommonConfigs.EXTRA_CHILDREN_DEBUG.get()) {
               builder.append(System.lineSeparator())
                  .append("─────────────────────────────── LIST OF CHILDREN ────────────────────────────────")
                  .append(System.lineSeparator());
               Set<String> allChildKeys = new TreeSet<>();

               for (BlockType entry : reg.getValues()) {
                  allChildKeys.addAll(entry.getChildren().stream().map(Entry::getKey).toList());
               }

               for (BlockType entry : reg.getValues()) {
                  builder.append("[").append(entry.getId().toString()).append("]").append(System.lineSeparator());
                  if (allChildKeys.isEmpty()) {
                     builder.append("  (no children)").append(System.lineSeparator());
                  } else {
                     for (String key : allChildKeys) {
                        Object value = entry.getChild(key);
                        builder.append("  - ").append(key).append(" = ").append(value != null ? formatValue(value) : "MISSING").append(System.lineSeparator());
                     }
                  }

                  builder.append(System.lineSeparator());
               }
            }

            Files.writeString(filePath, builder.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
         }
      } catch (IOException var11) {
         Moonlight.LOGGER.error("Failed to write registry debug dump: {}", var11.toString());
      }
   }

   private static Object formatValue(Object child) {
      return child instanceof Item i ? "Item{" + BuiltInRegistries.ITEM.getKey(i) + "}" : child.toString();
   }
}
