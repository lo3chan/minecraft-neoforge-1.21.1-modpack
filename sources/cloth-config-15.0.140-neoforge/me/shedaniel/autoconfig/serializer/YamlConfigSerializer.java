package me.shedaniel.autoconfig.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.Yaml;

public class YamlConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {
   private Config definition;
   private Class<T> configClass;
   private Yaml yaml;

   public YamlConfigSerializer(Config definition, Class<T> configClass, Yaml yaml) {
      this.definition = definition;
      this.configClass = configClass;
      this.yaml = yaml;
   }

   public YamlConfigSerializer(Config definition, Class<T> configClass) {
      this(definition, configClass, new Yaml());
   }

   private Path getConfigPath() {
      return Utils.getConfigFolder().resolve(this.definition.name() + ".yaml");
   }

   @Override
   public void serialize(T config) throws ConfigSerializer.SerializationException {
      Path configPath = this.getConfigPath();

      try {
         Files.createDirectories(configPath.getParent());
         Files.write(configPath, this.yaml.dump(config).getBytes(StandardCharsets.UTF_8));
      } catch (IOException var4) {
         throw new ConfigSerializer.SerializationException(var4);
      }
   }

   @Override
   public T deserialize() throws ConfigSerializer.SerializationException {
      Path configPath = this.getConfigPath();
      if (Files.exists(configPath)) {
         try {
            ConfigData var3;
            try (InputStream stream = Files.newInputStream(configPath)) {
               var3 = this.yaml.load(stream);
            }

            return (T)var3;
         } catch (IOException var7) {
            throw new ConfigSerializer.SerializationException(var7);
         }
      } else {
         return this.createDefault();
      }
   }

   @Override
   public T createDefault() {
      return Utils.constructUnsafely(this.configClass);
   }
}
