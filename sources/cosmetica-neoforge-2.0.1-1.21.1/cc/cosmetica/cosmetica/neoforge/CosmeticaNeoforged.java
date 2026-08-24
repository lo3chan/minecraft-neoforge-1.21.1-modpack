package cc.cosmetica.cosmetica.neoforge;

import cc.cosmetica.com.fasterxml.jackson.databind.MapperFeature;
import cc.cosmetica.com.fasterxml.jackson.databind.ObjectMapper;
import cc.cosmetica.com.fasterxml.jackson.databind.json.JsonMapper;
import cc.cosmetica.com.fasterxml.jackson.databind.json.JsonMapper.Builder;
import cc.cosmetica.cosmetica.CacheCosmeticManager;
import cc.cosmetica.cosmetica.Cosmetica;
import gg.cloaks.javaclient.model.CosmeticaUser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod("cosmetica")
public class CosmeticaNeoforged implements CacheCosmeticManager.UserIO {
   private final ObjectMapper mapper;

   public CosmeticaNeoforged(IEventBus bus) {
      bus.addListener(this::onClientSetup);
      this.mapper = ((Builder)JsonMapper.builder().enable(new MapperFeature[]{MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS})).build();
      this.mapper.findAndRegisterModules();
   }

   private void onClientSetup(FMLClientSetupEvent event) {
      Cosmetica.init(this);
   }

   @Override
   public CosmeticaUser read(InputStream is) throws IOException {
      return (CosmeticaUser)this.mapper.readValue(is, CosmeticaUser.class);
   }

   @Override
   public void write(CosmeticaUser user, OutputStream os) throws IOException {
      this.mapper.writeValue(os, user);
   }
}
