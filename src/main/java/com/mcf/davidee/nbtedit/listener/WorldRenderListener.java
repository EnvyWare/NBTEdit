package com.mcf.davidee.nbtedit.listener;

import com.mcf.davidee.nbtedit.NBTEdit;
import com.mcf.davidee.nbtedit.forge.ClientProxy;
import com.mcf.davidee.nbtedit.gui.GuiEditNBTTree;
import com.mcf.davidee.nbtedit.packets.EntityRequestPacket;
import com.mcf.davidee.nbtedit.packets.PacketHandler;
import com.mcf.davidee.nbtedit.packets.TileRequestPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(modid = NBTEdit.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldRenderListener {

    @SubscribeEvent
    public static void renderWorldLast(RenderWorldLastEvent event) {
        Screen curScreen = Minecraft.getInstance().screen;

        if (!(curScreen instanceof GuiEditNBTTree)) {
            return;
        }

        GuiEditNBTTree screen = (GuiEditNBTTree)curScreen;
        Entity e = screen.getEntity();

        if (e != null && e.isAlive())
            drawBoundingBox(event.getContext(), event.getPartialTicks(), VoxelShapes.create(e.getBoundingBox()));
        else if (screen.isTileEntity()) {
            int x = screen.getBlockX();
            int y = screen.y;
            int z = screen.z;
            World world = Minecraft.getInstance().level;
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = world.getBlockState(pos);
            Block block = world.getBlockState(pos).getBlock();
            if (block != null) {
                drawBoundingBox(event.getContext(), event.getPartialTicks(), block.getVisualShape(state, world, pos, ISelectionContext.of(Minecraft.getInstance().player)));
            }
        }
    }

    @SubscribeEvent
    public static void onKey(InputEvent.KeyInputEvent event) {
        if (ClientProxy.NBTEditKey.isDown()) {
            RayTraceResult pos = Minecraft.getInstance().hitResult;
            System.out.println(pos);
            if (pos != null) {
                if (pos.getType() == RayTraceResult.Type.ENTITY && ((EntityRayTraceResult)pos).getEntity() != null) {
                    PacketHandler.sendToServer(new EntityRequestPacket(((EntityRayTraceResult)pos).getEntity().getId()));
                } else if (pos.getType() == RayTraceResult.Type.BLOCK && ((BlockRayTraceResult)pos).getBlockPos() != null) {
                    PacketHandler.sendToServer(new TileRequestPacket(((BlockRayTraceResult)pos).getBlockPos()));
                } else {
                    Minecraft.getInstance().player.sendMessage(new StringTextComponent("Error - No tile or entity selected").withStyle(TextFormatting.RED), Util.NIL_UUID);
                }
            }
        }
    }

    private static void drawBoundingBox(WorldRenderer r, float f, VoxelShape shape) {
        if (shape == null)
            return;

        AxisAlignedBB aabb = shape.bounds();
        Entity player = Minecraft.getInstance().getCameraEntity();

        double var8 = player.xo + (player.position().x() - player.xo) * f;
        double var10 = player.yo + (player.position().y() - player.yo) * f;
        double var12 = player.zo + (player.position().z() - player.zo) * f;

        aabb = aabb.inflate(-var8, -var10, -var12);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 0.0F, 0.0F, .5F);
        GL11.glLineWidth(3.5F);
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuilder();

        worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.vertex(aabb.minX, aabb.minY, aabb.minZ).color(1, 1, 1, 1);
        worldRenderer.vertex(aabb.maxX, aabb.minY, aabb.minZ).color(1, 1, 1, 1);
        worldRenderer.vertex(aabb.maxX, aabb.minY, aabb.maxZ).color(1, 1, 1, 1);
        worldRenderer.vertex(aabb.minX, aabb.minY, aabb.maxZ).color(1, 1, 1, 1);
        worldRenderer.vertex(aabb.minX, aabb.minY, aabb.minZ).color(1, 1, 1, 1);
        tessellator.end();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.vertex(aabb.minX, aabb.maxY, aabb.minZ).color(1, 1, 1, 1);
        worldRenderer.vertex(aabb.maxX, aabb.maxY, aabb.minZ).color(1, 1, 1, 1);
        worldRenderer.vertex(aabb.maxX, aabb.maxY, aabb.maxZ).color(1, 1, 1, 1);
        worldRenderer.vertex(aabb.minX, aabb.maxY, aabb.maxZ).color(1, 1, 1, 1);
        worldRenderer.vertex(aabb.minX, aabb.maxY, aabb.minZ).color(1, 1, 1, 1);
        tessellator.end();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.vertex(aabb.minX, aabb.minY, aabb.minZ).color(1, 1, 1, 1);
        worldRenderer.vertex(aabb.minX, aabb.maxY, aabb.minZ).color(1, 1, 1, 1);
        worldRenderer.vertex(aabb.maxX, aabb.minY, aabb.minZ).color(1, 1, 1, 1);
        worldRenderer.vertex(aabb.maxX, aabb.maxY, aabb.minZ).color(1, 1, 1, 1);
        worldRenderer.vertex(aabb.maxX, aabb.minY, aabb.maxZ).color(1, 1, 1, 1);
        worldRenderer.vertex(aabb.maxX, aabb.maxY, aabb.maxZ).color(1, 1, 1, 1);
        worldRenderer.vertex(aabb.minX, aabb.minY, aabb.maxZ).color(1, 1, 1, 1);
        worldRenderer.vertex(aabb.minX, aabb.maxY, aabb.maxZ).color(1, 1, 1, 1);
        tessellator.end();

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();

    }
}
