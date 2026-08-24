package alternate.current.wire;

import alternate.current.AlternateCurrentMod;
import alternate.current.interfaces.mixin.IServerLevel;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;

public interface Config {
   static Config forLevel(ServerLevel level, LevelStorageAccess storage) {
      return (Config)(level.dimension() == Level.OVERWORLD
         ? new Config.Primary(storage)
         : new Config.Derived(((IServerLevel)level.getServer().overworld()).alternate_current$getWireHandler().getConfig()));
   }

   boolean getEnabled();

   void setEnabled(boolean var1);

   UpdateOrder getUpdateOrder();

   void setUpdateOrder(UpdateOrder var1);

   void load();

   void save(boolean var1);

   public static class Derived implements Config {
      private final Config delegate;

      public Derived(Config delegate) {
         this.delegate = delegate;
      }

      @Override
      public boolean getEnabled() {
         return this.delegate.getEnabled();
      }

      @Override
      public void setEnabled(boolean enabled) {
         this.delegate.setEnabled(enabled);
      }

      @Override
      public UpdateOrder getUpdateOrder() {
         return this.delegate.getUpdateOrder();
      }

      @Override
      public void setUpdateOrder(UpdateOrder updateOrder) {
         this.delegate.setUpdateOrder(updateOrder);
      }

      @Override
      public void load() {
      }

      @Override
      public void save(boolean silent) {
      }
   }

   public static class Primary implements Config {
      private final Path path;
      private boolean enabled = true;
      private UpdateOrder updateOrder = UpdateOrder.HORIZONTAL_FIRST_OUTWARD;
      private boolean modified;

      public Primary(LevelStorageAccess storage) {
         this.path = storage.getLevelPath(LevelResource.ROOT).resolve("alternate-current.conf");
      }

      @Override
      public boolean getEnabled() {
         return this.enabled;
      }

      @Override
      public void setEnabled(boolean enabled) {
         this.enabled = enabled;
         AlternateCurrentMod.on = enabled;
         this.modified = true;
      }

      @Override
      public UpdateOrder getUpdateOrder() {
         return this.updateOrder;
      }

      @Override
      public void setUpdateOrder(UpdateOrder updateOrder) {
         this.updateOrder = Objects.requireNonNull(updateOrder);
         this.modified = true;
      }

      @Override
      public void load() {
         if (Files.exists(this.path)) {
            try (BufferedReader br = Files.newBufferedReader(this.path)) {
               String line;
               while ((line = br.readLine()) != null) {
                  if (!line.startsWith("#")) {
                     String[] parts = line.split("[=]");
                     if (parts.length == 2) {
                        String key = parts[0];
                        String value = parts[1];

                        try {
                           switch (key) {
                              case "enabled":
                                 this.setEnabled(Boolean.parseBoolean(value));
                                 break;
                              case "update-order":
                                 this.setUpdateOrder(UpdateOrder.byId(value));
                                 break;
                              default:
                                 AlternateCurrentMod.LOGGER.info("skipping unknown option '" + key + "' in Alternate Current config");
                           }
                        } catch (Exception var9) {
                           AlternateCurrentMod.LOGGER.info("skipping bad value '" + value + "' for option '" + key + "' in Alternate Current config!", var9);
                        }
                     }
                  }
               }

               this.modified = false;
            } catch (IOException var11) {
               AlternateCurrentMod.LOGGER.info("unable to load Alternate Current config!", var11);
               this.modified = true;
            }
         } else {
            this.modified = true;
         }
      }

      @Override
      public void save(boolean silent) {
         if (this.modified) {
            if (!silent) {
               AlternateCurrentMod.LOGGER.info("saving Alternate Current config");
            }

            try (BufferedWriter bw = Files.newBufferedWriter(this.path)) {
               bw.write("enabled");
               bw.write(61);
               bw.write(Boolean.toString(this.enabled));
               bw.newLine();
               bw.write("update-order");
               bw.write(61);
               bw.write(this.updateOrder.id());
               bw.newLine();
            } catch (IOException var12) {
               AlternateCurrentMod.LOGGER.info("unable to save Alternate Current config!", var12);
            } finally {
               this.modified = false;
            }
         }
      }
   }
}
