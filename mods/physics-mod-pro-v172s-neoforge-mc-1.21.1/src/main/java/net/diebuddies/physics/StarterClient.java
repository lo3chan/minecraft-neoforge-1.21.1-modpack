/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.DetectedVersion
 *  net.minecraft.client.Minecraft
 *  net.neoforged.bus.api.IEventBus
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.system.MemoryStack
 */
package net.diebuddies.physics;

import de.fabmax.physxjni.Platform;
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
import net.diebuddies.opengl.Mesh;
import net.diebuddies.opengl.Texture;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.ServerPhysicsMod;
import net.diebuddies.physics.Version;
import net.diebuddies.physics.sound.ContactSimulationCallback;
import net.diebuddies.physics.verlet.Cloth;
import net.diebuddies.render.MainRenderer;
import net.minecraft.DetectedVersion;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryStack;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import physx.PxTopLevelFunctions;
import physx.common.PxCudaContextManager;
import physx.common.PxCudaContextManagerDesc;
import physx.common.PxCudaTopLevelFunctions;
import physx.common.PxDefaultAllocator;
import physx.common.PxErrorCallbackImpl;
import physx.common.PxErrorCodeEnum;
import physx.common.PxFoundation;
import physx.common.PxTolerancesScale;
import physx.cooking.PxConvexMeshCookingTypeEnum;
import physx.cooking.PxCookingParams;
import physx.physics.PxMaterial;
import physx.physics.PxPhysics;

public class StarterClient {
    public static final MemoryStack memoryStack = MemoryStack.create((int)0x500000);
    public static final Logger logger = LogManager.getLogger((String)"Physics Mod");
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
    public static boolean cudaAvailable;
    public static PxPhysics physics;
    public static PxTolerancesScale tolerances;
    public static PxCookingParams cookingParams;
    public static PxMaterial defaultMaterial;
    public static PxFoundation foundation;
    public static PxCudaContextManager cudaManager;
    public static int physxVersion;
    public static volatile String updateMessage;
    public static volatile String customMessage;

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
        final PxErrorCallbackImpl errorCb = new PxErrorCallbackImpl(){

            @Override
            public void reportError(PxErrorCodeEnum code, String message, String file, int line) {
                logger.error(String.valueOf((Object)code) + ": " + message);
                Thread.dumpStack();
            }
        };
        foundation = PxTopLevelFunctions.CreateFoundation(physxVersion, allocator, errorCb);
        tolerances = new PxTolerancesScale();
        physics = PxTopLevelFunctions.CreatePhysics(physxVersion, foundation, tolerances);
        cudaAvailable = StarterClient.isCudaAvailable();
        cudaAvailable = false;
        StarterClient.createPhysicsCooking(ConfigClient.cudaLiquids && cudaAvailable);
        defaultMaterial = physics.createMaterial(1.0f, 1.0f, 0.0f);
        FabricAPI.CLIENT_STOPPING.register(new FabricAPI.ClientStopping(){

            @Override
            public void onClientStopping(Minecraft client) {
                for (PhysicsMod mod : PhysicsMod.getInstances().values()) {
                    mod.physicsWorld.destroy();
                }
                PhysicsMod.getInstances().clear();
                for (Cloth cloth : PhysicsMod.cloth.values()) {
                    cloth.destroy();
                }
                if (PhysicsMod.defaultCape != null) {
                    PhysicsMod.defaultCape.destroy();
                }
                defaultMaterial.release();
                physics.release();
                if (cudaManager != null) {
                    cudaManager.release();
                }
                cookingParams.destroy();
                tolerances.destroy();
                foundation.release();
                errorCb.destroy();
                allocator.destroy();
                Texture.destroyAll();
                Mesh.destroyStoredVAOs();
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
            cudaManager = StarterClient.createCudaManager();
        } else {
            cudaManager = null;
        }
    }

    private static boolean isCudaAvailable() {
        if (Platform.getPlatform() == Platform.MACOS || Platform.getPlatform() == Platform.MACOS_ARM64 || Platform.getPlatform() == Platform.LINUX) {
            return false;
        }
        return PxCudaTopLevelFunctions.GetSuggestedCudaDeviceOrdinal(foundation) >= 0;
    }

    private static PxCudaContextManager createCudaManager() {
        if (!StarterClient.isCudaAvailable()) {
            System.err.println("CUDA is not available or disabled on this platform");
            return null;
        }
        try (MemoryStack mem = MemoryStack.stackPush();){
            PxCudaContextManagerDesc desc = PxCudaContextManagerDesc.createAt(mem, MemoryStack::nmalloc);
            PxCudaContextManager cudaMgr = PxCudaTopLevelFunctions.CreateCudaContextManager(foundation, desc);
            if (cudaMgr == null || !cudaMgr.contextIsValid()) {
                System.err.println("Failed creating CUDA context, no CUDA capable GPU?");
                PxCudaContextManager pxCudaContextManager = null;
                return pxCudaContextManager;
            }
            PxCudaContextManager pxCudaContextManager = cudaMgr;
            return pxCudaContextManager;
        }
    }

    private static Document convertStringToXMLDocument(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getText(String urlString) throws Exception {
        URL url = new URL(urlString);
        URLConnection con = url.openConnection();
        con.setReadTimeout(10000);
        con.setConnectTimeout(10000);
        Object text = "";
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));){
            String line;
            while ((line = in.readLine()) != null) {
                text = (String)text + line + "\n";
            }
        }
        return text;
    }

    private static /* synthetic */ void lambda$onInitializeClient$0(boolean updateNotifications) {
        Version currentPhysicsModVersion = ModLoaderFunctions.getModID();
        String url = "https://minecraftphysicsmod.com/versions?format=rss";
        try {
            Version currentMCVersion = new Version(DetectedVersion.BUILT_IN.getName());
            String text = StarterClient.getText(url);
            Document doc = StarterClient.convertStringToXMLDocument(text);
            NodeList entries = doc.getElementsByTagName("item");
            for (int i = 0; i < entries.getLength(); ++i) {
                Node node = entries.item(i);
                try {
                    Element element = (Element)node;
                    String title = element.getElementsByTagName("title").item(0).getTextContent();
                    if (title.startsWith("Custom_Message:")) {
                        customMessage = title.replaceFirst("Custom_Message:", "");
                        continue;
                    }
                    String[] split = title.split(":");
                    String type = split[0];
                    Version mcVersion = new Version(split[1]);
                    Version physicsVersion = new Version(split[2]);
                    if (!type.equalsIgnoreCase(ModLoaderFunctions.getModloader()) || !mcVersion.equals(currentMCVersion) || currentPhysicsModVersion.equals(physicsVersion) || !updateNotifications) continue;
                    updateMessage = "{\"text\":\"NEW PHYSICS VERSION AVAILABLE! CLICK HERE!\",\"bold\":true,\"underlined\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://minecraftphysicsmod.com/\"}}";
                    newUpdateAvailable = true;
                    continue;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        cudaAvailable = false;
        updateMessage = "";
        customMessage = "";
    }
}

