package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.util.ID;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;

public class ScriptFileInfo {
   private static final Pattern FILE_FIXER = Pattern.compile("[^\\w./]");
   public final ScriptPackInfo pack;
   public final Path path;
   public final String file;
   public final ResourceLocation id;
   public final String locationPath;
   public final String location;

   public ScriptFileInfo(ScriptPackInfo p, Path ph, String f) {
      this.pack = p;
      this.path = ph;
      this.file = f;
      this.id = ResourceLocation.fromNamespaceAndPath(
         this.pack.namespace, FILE_FIXER.matcher(this.pack.pathStart + this.file).replaceAll("_").toLowerCase(Locale.ROOT)
      );
      this.locationPath = this.pack.pathStart + this.file;
      this.location = ID.string(this.pack.namespace + ":" + this.locationPath);
   }
}
