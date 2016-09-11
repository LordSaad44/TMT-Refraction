package com.teamwizardry.refraction.client.render;

import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import com.teamwizardry.refraction.api.IPrecisionTile;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

/**
 * Created by TheCodeWarrior
 */
public class ScrewdriverOverlay {
	public static final ScrewdriverOverlay INSTANCE = new ScrewdriverOverlay();
	private BlockPos highlighting;

	private ScrewdriverOverlay() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void overlay(RenderGameOverlayEvent.Post event) {
		ItemStack stack = getItemInHand(ModItems.SCREW_DRIVER);
		if (stack == null)
			return;

		if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
			ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

			double SQRT2 = Math.sqrt(0.5);

			double angle = ModItems.SCREW_DRIVER.getRotationMultiplier(stack);
			int textIndex = ModItems.SCREW_DRIVER.getRotationIndex(stack);
			double anglePer = 5.0;
			String text = I18n.format("gui.screw_driver.angle." + textIndex);


			int circleRadius = 75;
			int posX = res.getScaledWidth();
			int posY = res.getScaledHeight();

			if (angle < 5) {
				double radiusAdd = 500;
				posX += SQRT2 * radiusAdd;
				posY += SQRT2 * radiusAdd;
				circleRadius += radiusAdd;
			}

			GlStateManager.pushMatrix();
			GlStateManager.disableTexture2D();
			GlStateManager.color(0, 0, 0);
			GlStateManager.translate(posX, posY, 0);

			Vec2d vec = new Vec2d(0, -circleRadius);
			vec = rot(vec, -angle / 2 - 45);

			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer vb = tessellator.getBuffer();

			vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
			vb.pos(0, 0, 0).endVertex();

			double ang = angle;

			do {
				Vec2d v = rot(vec, ang);
				vb.pos(v.getX(), v.getY(), 0).endVertex();
				ang -= anglePer;
			} while (ang > 0);

			vb.pos(vec.getX(), vec.getY(), 0).endVertex();

			tessellator.draw();
			GlStateManager.enableTexture2D();

			int width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
			Minecraft.getMinecraft().fontRendererObj.drawString(text, -width - (int) (circleRadius * SQRT2), -9 - (int) (circleRadius * SQRT2), 0x000000, false);

			GlStateManager.color(1, 1, 1);
			GlStateManager.popMatrix();
		}

		if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
			// HIGHLIGHT ANGLE
			if (highlighting != null) {
				TileEntity precisiontile = Minecraft.getMinecraft().theWorld.getTileEntity(highlighting);
				if (precisiontile != null && precisiontile instanceof IPrecisionTile) {
					IPrecisionTile tile = (IPrecisionTile) precisiontile;
					GlStateManager.pushMatrix();
					GlStateManager.enableTexture2D();
					GlStateManager.color(1, 1, 1);
					String s = tile.getRotX() + ", " + tile.getRotY();
					Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(s, event.getResolution().getScaledWidth() / 2 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(s) / 2, event.getResolution().getScaledHeight() / 2 + 30, 0xFFFFFF);
					GlStateManager.pushMatrix();
				}
			}
		}
	}

	@SubscribeEvent
	public void highlight(DrawBlockHighlightEvent event) {
		BlockPos hit = event.getTarget().getBlockPos();
		/*IBlockState target = event.getPlayer().getEntityWorld().getBlockState(hit);
		if (target.getBlock() instanceof IPrecision) {
			highlighting = hit;
		} else highlighting = null;
	*/}

	private Vec2d rot(Vec2d vec, double deg) {
		double theta = Math.toRadians(deg);

		double cs = Math.cos(theta);
		double sn = Math.sin(theta);

		return new Vec2d(vec.getX() * cs - vec.getY() * sn, vec.getX() * sn + vec.getY() * cs);
	}

	private ItemStack getItemInHand(Item item) {
		ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItemMainhand();
		if (stack == null)
			stack = Minecraft.getMinecraft().thePlayer.getHeldItemOffhand();

		if (stack == null)
			return null;
		if (stack.getItem() != item)
			return null;

		return stack;
	}
}
