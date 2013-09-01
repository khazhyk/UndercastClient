package undercast.client.achievements2;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.mod_Undercast;
import undercast.client.achievements2.animation.UndercastAchievementAccessor;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.*;
import net.minecraft.src.Minecraft;
import net.minecraft.src.AbstractClientPlayer;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.Tessellator;
import net.minecraft.src.TextureManager;
import net.minecraft.src.ResourceLocation;

public class UndercastAchievement implements TweenCallback {

    private static final ResourceLocation achievementBackground = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    private static final ResourceLocation ingameWidgets = new ResourceLocation("textures/gui/widgets.png");
    public String killerName;
    public String line1;
    public String line2;
    public int achievementTime = 0;
    public float posX;
    public float posY;
    public long lastRenderTime = -1;
    TweenManager manager;
    private int rank = 0;
    public float alpha = 0.0F;
    
    public UndercastAchievement(String name, String l1, String l2) {
        manager = new TweenManager();
        ScaledResolution scr = new ScaledResolution(Minecraft.getMinecraft().gameSettings, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        killerName = name;
        line1 = l1;
        line2 = l2;
        posX = scr.getScaledWidth() + 30;
        posY = 10;
        lastRenderTime = System.currentTimeMillis();
        Timeline.createSequence().beginParallel().push(Tween.to(this, UndercastAchievementAccessor.POSITION_X, 1.0f).target(scr.getScaledWidth() - 170).ease(Bounce.OUT)).push(Tween.to(this, UndercastAchievementAccessor.ALPHA, 0.7f).target(1.0f)).end().pushPause(4.0f).beginParallel().push(Tween.to(this, UndercastAchievementAccessor.POSITION_X, 1.0f).target(scr.getScaledWidth() + 80).ease(Bounce.OUT)).push(Tween.to(this, UndercastAchievementAccessor.ALPHA, 1.0f).target(0.0F)).end().setCallback(this).start(manager);
    }
    
    //Constructor for compatibility with old achievements
    public UndercastAchievement(String name, boolean killOrKilled) {
        manager = new TweenManager();
        ScaledResolution scr = new ScaledResolution(Minecraft.getMinecraft().gameSettings, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        killerName = name;
        line1 = killOrKilled ? "\u00A7a" + name : "\u00A74" + name;
        line2 = killOrKilled ? "\u00A7a" + "+1 Kill" : "\u00A74" + "+1 Death";
        posX = scr.getScaledWidth() + 30;
        posY = 10;
        lastRenderTime = System.currentTimeMillis();
        Timeline.createSequence().beginParallel().push(Tween.to(this, UndercastAchievementAccessor.POSITION_X, 1.0f).target(scr.getScaledWidth() - 170).ease(Bounce.OUT)).push(Tween.to(this, UndercastAchievementAccessor.ALPHA, 0.7f).target(1.0f)).end().pushPause(4.0f).beginParallel().push(Tween.to(this, UndercastAchievementAccessor.POSITION_X, 1.0f).target(scr.getScaledWidth() + 80).ease(Bounce.OUT)).push(Tween.to(this, UndercastAchievementAccessor.ALPHA, 1.0f).target(0.0F)).end().setCallback(this).start(manager);
    }

    public void update(long deltaTime) {
        float deltaTimeF = (float) deltaTime / 1000.0F;
        manager.update(deltaTimeF);
    }

    public void draw() {
        update(System.currentTimeMillis() - lastRenderTime);
        lastRenderTime = System.currentTimeMillis();
        GL11.glPushMatrix();
        // Drawing Background
        GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Minecraft.getMinecraft().func_110434_K().func_110577_a(achievementBackground);
        this.drawTexturedModalRect((int) posX, (int) posY, 96, 202, 160, 32);
        // Drawing text lines
        GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
        Minecraft.getMinecraft().fontRenderer.drawString(this.line1, (int) posX + 30, (int) posY + 7, 16777215);
        Minecraft.getMinecraft().fontRenderer.drawString(this.line2, (int) posX + 30, (int) posY + 18, 16777215);
        // Drawing skin border
        GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
        Minecraft.getMinecraft().func_110434_K().func_110577_a(ingameWidgets);
        this.drawTexturedModalRect((int) posX + 6, (int) posY + 6, 1, 1, 20, 20);
        // Drawing skin
        GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
        ResourceLocation resourcelocation = AbstractClientPlayer.func_110311_f(this.killerName);
        AbstractClientPlayer.func_110304_a(resourcelocation, this.killerName);
        GL11.glPushMatrix(); // New GL11 matrix to not affect other
                             // part of the gui
        TextureManager texturemanager = Minecraft.getMinecraft().func_110434_K();
        if (texturemanager != null) {
            texturemanager.func_110577_a(resourcelocation);
        }
        GL11.glScalef(1F / 2F, 1F / 4F, 1F);
        this.drawTexturedModalRect(((int) posX + 8) * 2, ((int) posY + 8) * 4, 32, 64, 32, 64);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public void drawTexturedModalRect(int par1, int par2, int par3, int par4, int par5, int par6) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) (par1 + 0), (double) (par2 + par6), 0.0D, (double) ((float) (par3 + 0) * f), (double) ((float) (par4 + par6) * f1));
        tessellator.addVertexWithUV((double) (par1 + par5), (double) (par2 + par6), 0.0D, (double) ((float) (par3 + par5) * f), (double) ((float) (par4 + par6) * f1));
        tessellator.addVertexWithUV((double) (par1 + par5), (double) (par2 + 0), 0.0D, (double) ((float) (par3 + par5) * f), (double) ((float) (par4 + 0) * f1));
        tessellator.addVertexWithUV((double) (par1 + 0), (double) (par2 + 0), 0.0D, (double) ((float) (par3 + 0) * f), (double) ((float) (par4 + 0) * f1));
        tessellator.draw();
    }

    @Override
    public void onEvent(int type, BaseTween<?> source) {
        if (type == COMPLETE) {
            mod_Undercast.guiAchievement.removeAchievement(this);
        }
    }

    public void setRank(int i) {
        posY = 10 + i * 36;
        rank = i;
    }

    public int getRank() {
        return rank;
    }
}
