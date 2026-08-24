package com.finndog.moogs_structures.utils;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.Error;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;

public final class VersionResolver {
   private static final String CURRENT_VERSION_STRING = "1.21.1";
   private static final VersionResolver.VersionNumber CURRENT_VERSION = VersionResolver.VersionNumber.parseInternal("1.21.1");

   private VersionResolver() {
   }

   public static VersionResolver.VersionNumber getCurrentVersion() {
      return CURRENT_VERSION;
   }

   public static String getCurrentVersionString() {
      return "1.21.1";
   }

   public static DataResult<List<VersionResolver.VersionEntry>> parseVersionMap(Map<String, ResourceLocation> raw) {
      List<VersionResolver.VersionEntry> entries = new ArrayList<>();

      for (Entry<String, ResourceLocation> entry : raw.entrySet()) {
         DataResult<VersionResolver.VersionEntry> parsedEntry = parseRange(entry.getKey())
            .map(range -> new VersionResolver.VersionEntry(entry.getKey(), range, entry.getValue()));
         Optional<VersionResolver.VersionEntry> result = parsedEntry.result();
         if (result.isEmpty()) {
            String errorMessage = parsedEntry.error().<String>map(Error::message).orElse("Unknown version range error");
            return DataResult.error(() -> errorMessage);
         }

         entries.add(result.get());
      }

      return DataResult.success(List.copyOf(entries));
   }

   public static DataResult<Map<String, ResourceLocation>> encodeVersionEntries(List<VersionResolver.VersionEntry> entries) {
      LinkedHashMap<String, ResourceLocation> map = new LinkedHashMap<>();

      for (VersionResolver.VersionEntry entry : entries) {
         map.put(entry.rawRange(), entry.location());
      }

      return DataResult.success(map);
   }

   public static Optional<VersionResolver.VersionEntry> resolve(List<VersionResolver.VersionEntry> entries, VersionResolver.VersionNumber version) {
      for (VersionResolver.VersionEntry entry : entries) {
         if (entry.range().contains(version)) {
            return Optional.of(entry);
         }
      }

      return Optional.empty();
   }

   public static DataResult<VersionResolver.VersionRange> parseRange(String raw) {
      String trimmed = raw.trim();
      if (trimmed.isEmpty()) {
         return DataResult.error(() -> "Version range cannot be empty");
      } else {
         String[] tokens = trimmed.split("-", -1);
         if (tokens.length == 1) {
            return parseVersionNumber(tokens[0]).map(number -> new VersionResolver.VersionRange(number, number));
         } else if (tokens.length == 2) {
            if (!tokens[0].isEmpty() && !tokens[1].isEmpty()) {
               DataResult<VersionResolver.VersionNumber> minResult = parseVersionNumber(tokens[0]);
               DataResult<VersionResolver.VersionNumber> maxResult = parseVersionNumber(tokens[1]);
               Optional<VersionResolver.VersionNumber> min = minResult.result();
               if (min.isEmpty()) {
                  String errorMessage = minResult.error().<String>map(Error::message).orElse("Failed to parse minimum version for range '" + raw + "'");
                  return DataResult.error(() -> errorMessage);
               } else {
                  Optional<VersionResolver.VersionNumber> max = maxResult.result();
                  if (max.isEmpty()) {
                     String errorMessage = maxResult.error().<String>map(Error::message).orElse("Failed to parse maximum version for range '" + raw + "'");
                     return DataResult.error(() -> errorMessage);
                  } else {
                     return min.get().compareTo(max.get()) > 0
                        ? DataResult.error(() -> "Version range '" + raw + "' has a minimum greater than its maximum")
                        : DataResult.success(new VersionResolver.VersionRange(min.get(), max.get()));
                  }
               }
            } else {
               return DataResult.error(() -> "Version range '" + raw + "' must specify both minimum and maximum versions");
            }
         } else {
            return DataResult.error(() -> "Version range '" + raw + "' has too many '-' separators");
         }
      }
   }

   private static DataResult<VersionResolver.VersionNumber> parseVersionNumber(String raw) {
      String trimmed = raw.trim();
      if (trimmed.isEmpty()) {
         return DataResult.error(() -> "Version value cannot be empty");
      } else {
         String[] parts = trimmed.split("\\.");
         List<Integer> numbers = new ArrayList<>(parts.length);

         for (String part : parts) {
            if (part.isEmpty()) {
               return DataResult.error(() -> "Version '" + raw + "' contains empty components");
            }

            try {
               numbers.add(Integer.parseInt(part));
            } catch (NumberFormatException var9) {
               return DataResult.error(() -> "Version '" + raw + "' contains non-numeric component '" + part + "'");
            }
         }

         return DataResult.success(new VersionResolver.VersionNumber(List.copyOf(numbers)));
      }
   }

   public record VersionEntry(String rawRange, VersionResolver.VersionRange range, ResourceLocation location) {
   }

   public record VersionNumber(List<Integer> parts) implements Comparable<VersionResolver.VersionNumber> {
      public VersionNumber(List<Integer> parts) {
         if (parts.isEmpty()) {
            throw new IllegalArgumentException("Version number must contain at least one component");
         } else {
            parts = List.copyOf(parts);
            this.parts = parts;
         }
      }

      private static VersionResolver.VersionNumber parseInternal(String value) {
         String[] tokens = value.split("\\.");
         List<Integer> numbers = new ArrayList<>(tokens.length);

         for (String token : tokens) {
            numbers.add(Integer.parseInt(token));
         }

         return new VersionResolver.VersionNumber(List.copyOf(numbers));
      }

      public int compareTo(VersionResolver.VersionNumber other) {
         int maxLength = Math.max(this.parts.size(), other.parts.size());

         for (int index = 0; index < maxLength; index++) {
            int left = index < this.parts.size() ? this.parts.get(index) : 0;
            int right = index < other.parts.size() ? other.parts.get(index) : 0;
            if (left != right) {
               return Integer.compare(left, right);
            }
         }

         return 0;
      }

      @Override
      public String toString() {
         StringJoiner joiner = new StringJoiner(".");

         for (Integer part : this.parts) {
            joiner.add(Integer.toString(part));
         }

         return joiner.toString();
      }
   }

   public record VersionRange(VersionResolver.VersionNumber minInclusive, VersionResolver.VersionNumber maxInclusive) {
      public VersionRange(VersionResolver.VersionNumber minInclusive, VersionResolver.VersionNumber maxInclusive) {
         Objects.requireNonNull(minInclusive, "minInclusive");
         Objects.requireNonNull(maxInclusive, "maxInclusive");
         this.minInclusive = minInclusive;
         this.maxInclusive = maxInclusive;
      }

      public boolean contains(VersionResolver.VersionNumber version) {
         return version.compareTo(this.minInclusive) >= 0 && version.compareTo(this.maxInclusive) <= 0;
      }

      @Override
      public String toString() {
         return this.minInclusive + "-" + this.maxInclusive;
      }
   }
}
