package pl.skidam.automodpack_core.utils;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemLoopException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pl.skidam.automodpack_core.GlobalVariables;

public class FileTreeScanner {
   private volatile Map<String, Path> wildcardMatches = Map.of();
   private final Object lock = new Object();
   private final List<PathMatcher> whiteListMatchers = new ArrayList<>();
   private final List<PathMatcher> blackListMatchers = new ArrayList<>();
   private final List<FileTreeScanner.PruningRule> pruningRules = new ArrayList<>();
   private final Set<Path> startDirectories;
   private final FileSystem fs;
   private final boolean isCaseInsensitive;

   public FileTreeScanner(Set<String> rules, Set<Path> startDirectories) {
      this(rules, startDirectories, FileSystems.getDefault());
   }

   public FileTreeScanner(Set<String> rules, Set<Path> startDirectories, FileSystem fs) {
      this.startDirectories = startDirectories != null ? startDirectories : Set.of();
      this.isCaseInsensitive = FileSystemCapabilities.isCaseInsensitive(fs);
      this.fs = fs;
      this.parseRules(rules);
   }

   public Map<String, Path> getMatchedPaths() {
      return this.wildcardMatches;
   }

   public boolean hasMatch(String pathStr) {
      return pathStr != null && this.wildcardMatches.containsKey(pathStr);
   }

   public void scan() {
      synchronized (this.lock) {
         if (!this.whiteListMatchers.isEmpty()) {
            Map<String, Path> localMatches = new HashMap<>(512);
            EnumSet<FileVisitOption> options = EnumSet.of(FileVisitOption.FOLLOW_LINKS);

            for (Path startDir : this.startDirectories) {
               if (startDir != null && Files.exists(startDir)) {
                  Path absStartDir = startDir.toAbsolutePath().normalize();

                  try {
                     Files.walkFileTree(startDir, options, 2147483647, new FileTreeScanner.Visitor(startDir, absStartDir, localMatches));
                  } catch (IOException var9) {
                     GlobalVariables.LOGGER.error("Error walking directory: {}", startDir, var9);
                  }
               }
            }

            this.wildcardMatches = Map.copyOf(localMatches);
         }
      }
   }

   private String formatOutputKey(Path relativePath) {
      String pathStr = relativePath.toString().replace(this.fs.getSeparator(), "/");
      return pathStr.startsWith("/") ? pathStr : "/" + pathStr;
   }

   private void parseRules(Set<String> rawRules) {
      if (rawRules != null) {
         for (String rule : rawRules) {
            if (rule != null && !rule.isBlank()) {
               boolean isBlacklist = rule.startsWith("!");
               String clean = isBlacklist ? rule.substring(1) : rule;
               if (clean.startsWith("/")) {
                  clean = clean.substring(1);
               }

               while (clean.contains("**/**")) {
                  clean = clean.replace("**/**", "**");
               }

               try {
                  PathMatcher matcher = this.fs.getPathMatcher("glob:" + clean);
                  if (isBlacklist) {
                     this.blackListMatchers.add(matcher);
                  } else {
                     this.whiteListMatchers.add(matcher);
                     if (clean.contains("/**/")) {
                        String collapsed = clean.replace("/**/", "/");
                        this.whiteListMatchers.add(this.fs.getPathMatcher("glob:" + collapsed));
                     }

                     this.pruningRules.add(FileTreeScanner.PruningRule.compile(clean, this.fs, this.isCaseInsensitive));
                     if (clean.contains("/**/")) {
                        String prefix = clean.substring(0, clean.indexOf("/**/"));
                        if (!prefix.isEmpty()) {
                           this.pruningRules.add(FileTreeScanner.PruningRule.compile(prefix, this.fs, this.isCaseInsensitive));
                        }
                     }
                  }
               } catch (Exception var8) {
                  GlobalVariables.LOGGER.error("Invalid glob: {}", rule, var8);
               }
            }
         }
      }
   }

   private static class GlobComponent {
      final boolean isDoubleWildcard;
      final boolean isExact;
      final char[] pattern;
      final String patternStr;
      final PathMatcher complexMatcher;
      final boolean isCaseInsensitive;
      final FileSystem fs;

      GlobComponent(String patternStr, FileSystem fs, boolean isCaseInsensitive) {
         this.fs = fs;
         this.patternStr = patternStr;
         this.isCaseInsensitive = isCaseInsensitive;
         this.isDoubleWildcard = patternStr.equals("**");
         this.pattern = patternStr.toCharArray();
         boolean hasSimpleMeta = false;
         boolean hasComplex = false;

         for (char c : this.pattern) {
            if (c == '*' || c == '?') {
               hasSimpleMeta = true;
            } else if (c == '[' || c == '{' || c == '\\') {
               hasSimpleMeta = true;
               hasComplex = true;
            }
         }

         this.isExact = !hasSimpleMeta;
         this.complexMatcher = hasComplex ? fs.getPathMatcher("glob:" + patternStr) : null;
      }

      boolean matches(String text) {
         if (this.isDoubleWildcard) {
            return true;
         } else if (this.complexMatcher != null) {
            return this.complexMatcher.matches(this.fs.getPath(text));
         } else if (this.isExact) {
            return this.isCaseInsensitive ? text.equalsIgnoreCase(this.patternStr) : text.equals(this.patternStr);
         } else {
            return fastGlobMatch(this.pattern, text, this.isCaseInsensitive);
         }
      }

      private static boolean fastGlobMatch(char[] p, String t, boolean caseInsensitive) {
         int tLen = t.length();
         int pLen = p.length;
         int tIdx = 0;
         int pIdx = 0;
         int starIdx = -1;
         int matchIdx = -1;

         while (tIdx < tLen) {
            char tc = t.charAt(tIdx);
            char pc = pIdx < pLen ? p[pIdx] : 0;
            boolean isSlashMismatch = pc == '/' && tc == '\\';
            boolean charsMatch = pc == tc || isSlashMismatch || caseInsensitive && Character.toLowerCase(pc) == Character.toLowerCase(tc);
            if (pIdx >= pLen || pc != '?' && !charsMatch) {
               if (pIdx < pLen && pc == '*') {
                  starIdx = pIdx;
                  matchIdx = tIdx;
                  pIdx++;
               } else {
                  if (starIdx == -1) {
                     return false;
                  }

                  pIdx = starIdx + 1;
                  tIdx = ++matchIdx;
               }
            } else {
               pIdx++;
               tIdx++;
            }
         }

         while (pIdx < pLen && p[pIdx] == '*') {
            pIdx++;
         }

         return pIdx == pLen;
      }
   }

   private record PruningRule(FileTreeScanner.GlobComponent[] components, int firstDoubleWildcardIdx) {
      static FileTreeScanner.PruningRule compile(String pattern, FileSystem fs, boolean isCaseInsensitive) {
         String[] parts = pattern.split("/");
         ArrayList<FileTreeScanner.GlobComponent> comps = new ArrayList<>(parts.length);
         int firstDouble = -1;

         for (String part : parts) {
            if (!part.isEmpty()) {
               FileTreeScanner.GlobComponent c = new FileTreeScanner.GlobComponent(part, fs, isCaseInsensitive);
               comps.add(c);
               if (firstDouble == -1 && c.isDoubleWildcard) {
                  firstDouble = comps.size() - 1;
               }
            }
         }

         return new FileTreeScanner.PruningRule(comps.toArray(new FileTreeScanner.GlobComponent[0]), firstDouble);
      }

      boolean allows(String currentName, int depth) {
         if (this.firstDoubleWildcardIdx == -1 && depth - 1 >= this.components.length) {
            return false;
         } else {
            int idx = depth - 1;
            if (this.firstDoubleWildcardIdx != -1 && idx >= this.firstDoubleWildcardIdx) {
               return true;
            } else {
               return idx >= 0 && idx < this.components.length ? this.components[idx].matches(currentName) : false;
            }
         }
      }
   }

   private class Visitor extends SimpleFileVisitor<Path> {
      private final Path startDir;
      private final Path absStartDir;
      private final int startDirCount;
      private final Map<String, Path> targetMap;

      Visitor(Path startDir, Path absStartDir, Map<String, Path> targetMap) {
         this.startDir = startDir;
         this.absStartDir = absStartDir;
         this.startDirCount = startDir.getNameCount();
         this.targetMap = targetMap;
      }

      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
         if (dir.equals(this.startDir)) {
            return FileVisitResult.CONTINUE;
         } else {
            try {
               if (attrs.isSymbolicLink()) {
                  Path absDir = dir.toAbsolutePath().normalize();
                  if (!absDir.startsWith(this.absStartDir)) {
                     return FileVisitResult.SKIP_SUBTREE;
                  }
               }
            } catch (Exception var5) {
               return FileVisitResult.SKIP_SUBTREE;
            }

            Path fileNamePath = dir.getFileName();
            if (fileNamePath != null) {
               int depth = dir.getNameCount() - this.startDirCount;
               if (!this.couldMatchStructure(fileNamePath.toString(), depth)) {
                  return FileVisitResult.SKIP_SUBTREE;
               }
            }

            return FileVisitResult.CONTINUE;
         }
      }

      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
         Path relative;
         try {
            relative = this.startDir.relativize(file);
         } catch (IllegalArgumentException var8) {
            return FileVisitResult.CONTINUE;
         }

         int whiteSize = FileTreeScanner.this.whiteListMatchers.size();

         for (int i = 0; i < whiteSize; i++) {
            if (FileTreeScanner.this.whiteListMatchers.get(i).matches(relative)) {
               int blackSize = FileTreeScanner.this.blackListMatchers.size();

               for (int j = 0; j < blackSize; j++) {
                  if (FileTreeScanner.this.blackListMatchers.get(j).matches(relative)) {
                     return FileVisitResult.CONTINUE;
                  }
               }

               this.targetMap.put(FileTreeScanner.this.formatOutputKey(relative), file);
               return FileVisitResult.CONTINUE;
            }
         }

         return FileVisitResult.CONTINUE;
      }

      public FileVisitResult visitFileFailed(Path file, IOException exc) {
         if (exc instanceof FileSystemLoopException) {
            GlobalVariables.LOGGER.warn("Cycle: {}", file);
         } else if (exc instanceof AccessDeniedException) {
            GlobalVariables.LOGGER.info("Access denied: {}", file);
         } else {
            GlobalVariables.LOGGER.error("Visit failed: {} ({})", file, exc);
         }

         return FileVisitResult.CONTINUE;
      }

      private boolean couldMatchStructure(String dirName, int depth) {
         if (FileTreeScanner.this.pruningRules.isEmpty()) {
            return false;
         } else {
            int size = FileTreeScanner.this.pruningRules.size();

            for (int i = 0; i < size; i++) {
               if (FileTreeScanner.this.pruningRules.get(i).allows(dirName, depth)) {
                  return true;
               }
            }

            return false;
         }
      }
   }
}
