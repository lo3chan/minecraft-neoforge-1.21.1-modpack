/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.multiplayer.PlayerInfo
 *  net.minecraft.client.renderer.texture.HttpTexture
 *  net.minecraft.client.resources.PlayerSkin$Model
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 */
package traben.entity_texture_features.config.screens.skin;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.screens.skin.ETFScreenOldCompat;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.utils.ETFUtils2;

public class ETFConfigScreenSkinToolOutcome
extends ETFScreenOldCompat {
    private final boolean didSucceed;
    private final NativeImage skin;

    protected ETFConfigScreenSkinToolOutcome(Screen parent, boolean success, NativeImage skin) {
        super("config.entity_texture_features.player_skin_editor.print_skin.result", parent, false);
        this.didSucceed = success;
        this.skin = skin;
    }

    public static boolean uploadSkin(boolean skinType) {
        try {
            if ("127.0.0.1".equals(InetAddress.getLocalHost().getHostAddress())) {
                return false;
            }
            String auth = Minecraft.getInstance().getUser().getAccessToken();
            Path skinPath = Path.of(ETF.getConfigDirectory().toFile().getParent(), "ETF_player_skin_printout.png");
            String boundary = UUID.randomUUID().toString();
            byte[] fileBytes = Files.readAllBytes(skinPath);
            String bodyStart = "--" + boundary + "\r\nContent-Disposition: form-data; name=\"variant\"\r\n\r\n" + (skinType ? "classic" : "slim") + "\r\n--" + boundary + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"skin.png\"\r\nContent-Type: image/png\r\n\r\n";
            String bodyEnd = "\r\n--" + boundary + "--\r\n";
            byte[] requestBody = ETFConfigScreenSkinToolOutcome.concat(bodyStart.getBytes(), fileBytes, bodyEnd.getBytes());
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.minecraftservices.com/minecraft/profile/skins")).header("Authorization", "Bearer " + auth).header("Content-Type", "multipart/form-data; boundary=" + boundary).POST(HttpRequest.BodyPublishers.ofByteArray(requestBody)).build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            client.close();
            return response.statusCode() == 200;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static byte[] concat(byte[] ... parts) {
        int len = 0;
        for (byte[] p : parts) {
            len += p.length;
        }
        byte[] out = new byte[len];
        int pos = 0;
        for (byte[] p : parts) {
            System.arraycopy(p, 0, out, pos, p.length);
            pos += p.length;
        }
        return out;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget((GuiEventListener)this.getETFButton((int)((double)this.width * 0.55), (int)((double)this.height * 0.9), (int)((double)this.width * 0.2), 20, CommonComponents.GUI_DONE, button -> Objects.requireNonNull(this.minecraft).setScreen(this.parent)));
        if (this.didSucceed) {
            this.addRenderableWidget((GuiEventListener)this.getETFButton((int)((double)this.width * 0.15), (int)((double)this.height * 0.6), (int)((double)this.width * 0.7), 20, ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.print_skin.open"), button -> {
                try {
                    assert (ETF.getConfigDirectory() != null);
                    Path outputDirectory = Path.of(ETF.getConfigDirectory().toFile().getParent(), new String[0]);
                    Util.getPlatform().openFile(outputDirectory.toFile());
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }));
            this.addRenderableWidget((GuiEventListener)this.getETFButton((int)((double)this.width * 0.15), (int)((double)this.height * 0.4), (int)((double)this.width * 0.7), 20, ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.upload_skin"), button -> {
                boolean changeSuccess;
                PlayerInfo playerListEntry;
                if (Minecraft.getInstance().player == null) {
                    return;
                }
                boolean skinType = true;
                if (Minecraft.getInstance().getConnection() != null && (playerListEntry = Minecraft.getInstance().getConnection().getPlayerInfo(Minecraft.getInstance().player.getUUID())) != null) {
                    skinType = Minecraft.getInstance().getSkinManager().getInsecureSkin(playerListEntry.getProfile()).model() == PlayerSkin.Model.WIDE;
                }
                button.setMessage(ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.upload_skin_v2." + ((changeSuccess = ETFConfigScreenSkinToolOutcome.uploadSkin(skinType)) ? "success" : "fail")));
                if (changeSuccess) {
                    HttpTexture skinfile = (HttpTexture)Minecraft.getInstance().getSkinManager().skinTextures.textureManager.getTexture(Minecraft.getInstance().player.getSkin().texture(), null);
                    try {
                        assert (skinfile.file != null);
                        this.skin.writeToFile(skinfile.file);
                    }
                    catch (IOException e) {
                        ETFUtils2.logError(ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.upload_skin.success_local_fail").getString(), true);
                    }
                    if (Minecraft.getInstance().player != null) {
                        ETFManager.getInstance().PLAYER_TEXTURE_MAP.removeEntryOnly(Minecraft.getInstance().player.getUUID());
                    }
                } else {
                    ETFUtils2.logError("Failed to change in-game skin correctly, you might need to restart to see all the uploaded changes in-game", true);
                }
                button.active = false;
            }));
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        String[] strings = ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.print_skin.result." + (this.didSucceed ? "success" : "fail")).getString().split("\n");
        ArrayList<Component> lines = new ArrayList<Component>();
        for (String str : strings) {
            lines.add(Component.nullToEmpty((String)str.strip()));
        }
        int i = 0;
        for (Component txt : lines) {
            context.drawCenteredString(this.font, txt.getVisualOrderText(), (int)((double)this.width * 0.5), (int)((double)this.height * 0.3) + i, 0xFFFFFF);
            i += txt.getString().isBlank() ? 5 : 10;
        }
    }
}

