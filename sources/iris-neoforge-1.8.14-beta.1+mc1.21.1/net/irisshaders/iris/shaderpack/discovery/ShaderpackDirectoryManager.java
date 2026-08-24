package net.irisshaders.iris.shaderpack.discovery;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.irisshaders.iris.Iris;

public class ShaderpackDirectoryManager {
   private final Path root;

   public ShaderpackDirectoryManager(Path root) {
      this.root = root;
   }

   private static String removeFormatting(String formatted) {
      char[] original = formatted.toCharArray();
      char[] cleaned = new char[original.length];
      int c = 0;

      for (int i = 0; i < original.length; i++) {
         if (original[i] == 167) {
            i++;
         } else {
            cleaned[c++] = original[i];
         }
      }

      return new String(cleaned, 0, c);
   }

   public void copyPackIntoDirectory(String name, Path source) throws IOException {
      Path target = Iris.getShaderpacksDirectory().resolve(name);
      Files.copy(source, target);
      if (Files.isDirectory(source)) {
         try (Stream<Path> stream = Files.walk(source)) {
            for (Path p : stream.filter(x$0 -> Files.isDirectory(x$0)).toList()) {
               Path folder = source.relativize(p);
               if (!Files.exists(folder)) {
                  Files.createDirectory(target.resolve(folder));
               }
            }
         }

         try (Stream<Path> stream = Files.walk(source)) {
            for (Path px : stream.filter(pxx -> !Files.isDirectory(pxx)).collect(Collectors.toSet())) {
               Path file = source.relativize(px);
               Files.copy(px, target.resolve(file));
            }
         }
      }
   }

   public List<String> enumerate() throws IOException {
      boolean debug = Iris.getIrisConfig().areDebugOptionsEnabled();
      Comparator<String> baseComparator = String.CASE_INSENSITIVE_ORDER.thenComparing(Comparator.naturalOrder());
      Comparator<Path> comparator = (a, b) -> {
         if (debug) {
            if (Files.isDirectory(a)) {
               if (!Files.isDirectory(b)) {
                  return -1;
               }
            } else if (Files.isDirectory(b) && !Files.isDirectory(a)) {
               return 1;
            }
         }

         return baseComparator.compare(removeFormatting(a.getFileName().toString()), removeFormatting(b.getFileName().toString()));
      };

      List var5;
      try (Stream<Path> list = Files.list(this.root)) {
         var5 = list.filter(Iris::isValidToShowPack).sorted(comparator).map(path -> path.getFileName().toString()).collect(Collectors.toList());
      }

      return var5;
   }

   public URI getDirectoryUri() {
      return this.root.toUri();
   }
}
