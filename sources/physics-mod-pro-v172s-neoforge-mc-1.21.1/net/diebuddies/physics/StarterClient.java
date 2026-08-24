package net.diebuddies.physics;

import de.fabmax.physxjni.Platform;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.diebuddies.bridge.FabricAPI;
import net.diebuddies.bridge.FabricAPIServer;
import net.diebuddies.bridge.KeyBindingsRegistry;
import net.diebuddies.bridge.ModLoaderFunctions;
import net.diebuddies.bridge.WeatherParticlesRegistry;
import net.diebuddies.config.ConfigBlocks;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigCloth;
import net.diebuddies.config.ConfigMobs;
import net.diebuddies.opengl.Texture;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.sound.ContactSimulationCallback;
import net.diebuddies.physics.verlet.Cloth;
import net.diebuddies.render.MainRenderer;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryStack;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import physx.PxTopLevelFunctions;
import physx.common.PxCudaContextManager;
import physx.common.PxCudaContextManagerDesc;
import physx.common.PxCudaTopLevelFunctions;
import physx.common.PxDefaultAllocator;
import physx.common.PxErrorCallback;
import physx.common.PxErrorCallbackImpl;
import physx.common.PxErrorCodeEnum;
import physx.common.PxFoundation;
import physx.common.PxTolerancesScale;
import physx.cooking.PxConvexMeshCookingTypeEnum;
import physx.cooking.PxCookingParams;
import physx.physics.PxMaterial;
import physx.physics.PxPhysics;

public class StarterClient {
   public static final MemoryStack memoryStack = MemoryStack.create(5242880);
   public static final Logger logger = LogManager.getLogger("Physics Mod");
   public static boolean optifabric;
   public static boolean iris;
   public static boolean sodium;
   public static boolean replay;
   public static boolean immersivePortals;
   public static boolean valkyrienSkies;
   public static boolean sable;
   public static boolean soundPhysicsRemastered;
   public static boolean disableLightingCache;
   public static volatile boolean newUpdateAvailable;
   public static final boolean DEBUG_RENDER = false;
   public static final boolean PRO_VERSION = true;
   public static boolean cudaAvailable = false;
   public static PxPhysics physics;
   public static PxTolerancesScale tolerances;
   public static PxCookingParams cookingParams;
   public static PxMaterial defaultMaterial;
   public static PxFoundation foundation;
   public static PxCudaContextManager cudaManager;
   public static int physxVersion;
   public static volatile String updateMessage = "";
   public static volatile String customMessage = "";

   public static void onInitializeClient(IEventBus modEventBus) {
      PhysicsMod.createClothDirectory();
      if (ModLoaderFunctions.isModLoaded("optifabric") || ModLoaderFunctions.isModLoaded("optifine")) {
         optifabric = true;
      }

      iris = ModLoaderFunctions.isModLoaded("iris") || ModLoaderFunctions.isModLoaded("oculus");
      sodium = ModLoaderFunctions.isModLoaded("sodium") || iris || ModLoaderFunctions.isModLoaded("rubidium") || ModLoaderFunctions.isModLoaded("embeddium");
      replay = ModLoaderFunctions.isModLoaded("replaymod");
      immersivePortals = ModLoaderFunctions.isModLoaded("immersive_portals");
      soundPhysicsRemastered = ModLoaderFunctions.isModLoaded("sound_physics_remastered");
      valkyrienSkies = ModLoaderFunctions.isModLoaded("valkyrienskies");
      sable = ModLoaderFunctions.isModLoaded("sable");
      if (soundPhysicsRemastered) {
         ContactSimulationCallback.RESET_SOUNDS_PER_TICK_EVERY_X_TICKS = 20;
      }

      ConfigClient.init();
      ConfigMobs.init();
      ConfigBlocks.init();
      ConfigCloth.init();
      WeatherParticlesRegistry.register(modEventBus);
      KeyBindingsRegistry.register(modEventBus);
      physxVersion = PxTopLevelFunctions.getPHYSICS_VERSION();
      final PxDefaultAllocator allocator = new PxDefaultAllocator();
      final PxErrorCallback errorCb = new PxErrorCallbackImpl() {
         @Override
         public void reportError(PxErrorCodeEnum code, String message, String file, int line) {
            StarterClient.logger.error(code + ": " + message);
            Thread.dumpStack();
         }
      };
      foundation = PxTopLevelFunctions.CreateFoundation(physxVersion, allocator, errorCb);
      tolerances = new PxTolerancesScale();
      physics = PxTopLevelFunctions.CreatePhysics(physxVersion, foundation, tolerances);
      cudaAvailable = isCudaAvailable();
      cudaAvailable = false;
      createPhysicsCooking(ConfigClient.cudaLiquids && cudaAvailable);
      defaultMaterial = physics.createMaterial(1.0F, 1.0F, 0.0F);
      FabricAPI.CLIENT_STOPPING.register(new FabricAPI.ClientStopping() {
         @Override
         public void onClientStopping(Minecraft client) {
            ObjectIterator var2 = PhysicsMod.getInstances().values().iterator();

            while (var2.hasNext()) {
               PhysicsMod mod = (PhysicsMod)var2.next();
               mod.physicsWorld.destroy();
            }

            PhysicsMod.getInstances().clear();

            for (Cloth cloth : PhysicsMod.cloth.values()) {
               cloth.destroy();
            }

            if (PhysicsMod.defaultCape != null) {
               PhysicsMod.defaultCape.destroy();
            }

            StarterClient.defaultMaterial.release();
            StarterClient.physics.release();
            if (StarterClient.cudaManager != null) {
               StarterClient.cudaManager.release();
            }

            StarterClient.cookingParams.destroy();
            StarterClient.tolerances.destroy();
            StarterClient.foundation.release();
            errorCb.destroy();
            allocator.destroy();
            Texture.destroyAll();
            net.diebuddies.opengl.Mesh.destroyStoredVAOs();
            VAO.destroyHeaders();
            MainRenderer.destroy();
         }
      });
      ServerPhysicsMod server = new ServerPhysicsMod();
      FabricAPIServer.START_WORLD_TICK.register(server);
      FabricAPIServer.AFTER.register(server);
   }

   public static void createPhysicsCooking(boolean cudaEnabled) {
      if (cookingParams != null) {
         cookingParams.destroy();
      }

      cookingParams = new PxCookingParams(tolerances);
      cookingParams.setConvexMeshCookingType(PxConvexMeshCookingTypeEnum.eQUICKHULL);
      cookingParams.setSuppressTriangleMeshRemapTable(true);
      if (cudaManager != null) {
         cudaManager.release();
      }

      if (cudaEnabled) {
         cookingParams.setBuildGPUData(true);
         cudaManager = createCudaManager();
      } else {
         cudaManager = null;
      }
   }

   private static boolean isCudaAvailable() {
      return Platform.getPlatform() != Platform.MACOS && Platform.getPlatform() != Platform.MACOS_ARM64 && Platform.getPlatform() != Platform.LINUX
         ? PxCudaTopLevelFunctions.GetSuggestedCudaDeviceOrdinal(foundation) >= 0
         : false;
   }

   private static PxCudaContextManager createCudaManager() {
      if (!isCudaAvailable()) {
         System.err.println("CUDA is not available or disabled on this platform");
         return null;
      } else {
         MemoryStack mem = MemoryStack.stackPush();

         PxCudaContextManager var6;
         label49: {
            try {
               PxCudaContextManagerDesc desc = PxCudaContextManagerDesc.createAt(mem, MemoryStack::nmalloc);
               PxCudaContextManager cudaMgr = PxCudaTopLevelFunctions.CreateCudaContextManager(foundation, desc);
               if (cudaMgr != null && cudaMgr.contextIsValid()) {
                  var6 = cudaMgr;
                  break label49;
               }

               System.err.println("Failed creating CUDA context, no CUDA capable GPU?");
               var6 = null;
            } catch (Throwable var5) {
               if (mem != null) {
                  try {
                     mem.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (mem != null) {
               mem.close();
            }

            return var6;
         }

         if (mem != null) {
            mem.close();
         }

         return var6;
      }
   }

   private static Document convertStringToXMLDocument(String xmlString) {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = null;

      try {
         builder = factory.newDocumentBuilder();
         return builder.parse(new InputSource(new StringReader(xmlString)));
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }

   private static String getText(String urlString) throws Exception {
      URL url = new URL(urlString);
      URLConnection con = url.openConnection();
      con.setReadTimeout(10000);
      con.setConnectTimeout(10000);
      String text = "";

      String line;
      try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
         while ((line = in.readLine()) != null) {
            text = text + line + "\n";
         }
      }

      return text;
   }
}
