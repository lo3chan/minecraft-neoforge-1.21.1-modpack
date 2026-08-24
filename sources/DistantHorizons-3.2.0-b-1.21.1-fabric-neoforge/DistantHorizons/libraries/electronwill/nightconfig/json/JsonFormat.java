package DistantHorizons.libraries.electronwill.nightconfig.json;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.file.FormatDetector;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ConfigParser;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ConfigWriter;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.WriterSupplier;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.function.Supplier;

public abstract class JsonFormat<W extends ConfigWriter> implements ConfigFormat<Config> {
   private static final JsonFormat<FancyJsonWriter> FANCY = new JsonFormat<FancyJsonWriter>() {
      public FancyJsonWriter createWriter() {
         return new FancyJsonWriter();
      }

      @Override
      public ConfigParser<Config> createParser() {
         return new JsonParser(this);
      }
   };
   private static final JsonFormat<MinimalJsonWriter> MINIMAL = new JsonFormat<MinimalJsonWriter>() {
      public MinimalJsonWriter createWriter() {
         return new MinimalJsonWriter();
      }

      @Override
      public ConfigParser<Config> createParser() {
         return new JsonParser(this);
      }
   };

   public static JsonFormat<FancyJsonWriter> fancyInstance() {
      return FANCY;
   }

   public static JsonFormat<MinimalJsonWriter> minimalInstance() {
      return MINIMAL;
   }

   public static JsonFormat<FancyJsonWriter> emptyTolerantInstance() {
      return new JsonFormat<FancyJsonWriter>() {
         public FancyJsonWriter createWriter() {
            return new FancyJsonWriter();
         }

         @Override
         public ConfigParser<Config> createParser() {
            return new JsonParser(this).setEmptyDataAccepted(true);
         }
      };
   }

   public static JsonFormat<MinimalJsonWriter> minimalEmptyTolerantInstance() {
      return new JsonFormat<MinimalJsonWriter>() {
         public MinimalJsonWriter createWriter() {
            return new MinimalJsonWriter();
         }

         @Override
         public ConfigParser<Config> createParser() {
            return new JsonParser(this).setEmptyDataAccepted(true);
         }
      };
   }

   public static Config newConfig() {
      return FANCY.createConfig();
   }

   public static Config newConfig(Supplier<Map<String, Object>> s) {
      return FANCY.createConfig(s);
   }

   public static Config newConcurrentConfig() {
      return FANCY.createConcurrentConfig();
   }

   private JsonFormat() {
   }

   @Override
   public abstract W createWriter();

   @Override
   public abstract ConfigParser<Config> createParser();

   @Override
   public Config createConfig(Supplier<Map<String, Object>> mapCreator) {
      return Config.of(mapCreator, this);
   }

   @Override
   public boolean supportsComments() {
      return false;
   }

   @Override
   public void initEmptyFile(WriterSupplier ws) throws IOException {
      try (Writer writer = ws.get()) {
         writer.write("{}");
      }
   }

   @Override
   public void initEmptyFile(Writer writer) throws IOException {
      writer.write("{}");
   }

   static {
      FormatDetector.registerExtension("json", FANCY);
   }
}
