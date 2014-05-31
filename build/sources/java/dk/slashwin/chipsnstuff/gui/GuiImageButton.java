package dk.slashwin.chipsnstuff.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiImageButton extends GuiButton
{
	private final ResourceLocation texture;
	private final int tx;
	private final int ty;
	private final int tWidth;
	private final int tHeight;
	private final Color color;

	public GuiImageButton(int id, int x, int y, ResourceLocation texture, int tx, int ty, Color color)
	{
		super(id, x, y, 14, 14, "");
		this.texture = texture;
		this.tx = tx;
		this.ty = ty;
		this.tWidth = 6;
		this.tHeight = 6;
		this.color = color;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		super.drawButton(mc, mouseX, mouseY);
		mc.getTextureManager().bindTexture(texture);
		GL11.glColor4d(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
		drawTexturedModalRect(xPosition + 4, yPosition + 4, tx, ty, tWidth, tHeight);
	}
}
