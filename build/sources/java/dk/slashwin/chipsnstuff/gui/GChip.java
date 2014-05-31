package dk.slashwin.chipsnstuff.gui;

import dk.slashwin.chipsnstuff.ChipsnStuff;
import dk.slashwin.chipsnstuff.ColorUtil;
import dk.slashwin.chipsnstuff.PadMode;
import dk.slashwin.chipsnstuff.PowerPad;
import dk.slashwin.chipsnstuff.circuit.*;
import dk.slashwin.chipsnstuff.network.*;
import dk.slashwin.chipsnstuff.tileentity.TEChip;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;

public class GChip extends GuiScreen
{
	public static final int GUI_ID = 1;
	private static final ResourceLocation texture = new ResourceLocation("chipsnstuff:textures/gui/chip.png");
	private final int GridBorder = 8;

	private int Width = 249;
	private int Height = 177;

	private Wafer wafer;
	private int lastX = -1;
	private int lastY = -1;

	private int selectedTool = 1;
	private TEChip te;

	public GChip(TEChip te, Wafer wafer)
	{
		this.te = te;
		this.wafer = wafer;
		Width = (wafer.xSize * 6) + 16;
		Height = (wafer.ySize * 6) + 28;
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void initGui()
	{
		int xGui = (width - Width) / 2;
		int yGui = (height - Height) / 2;

		buttonList.add(new GuiImageButton(PComponent.ID, xGui + 8, yGui + 160, texture, 0, 178, Color.red));
		buttonList.add(new GuiImageButton(NComponent.ID, xGui + 24, yGui + 160, texture, 0, 178, Color.yellow));

		buttonList.add(new GuiImageButton(100, xGui + 40, yGui + 160, texture, 0, 178, Color.lightGray));
		buttonList.add(new GuiImageButton(101, xGui + 56, yGui + 160, texture, 6, 196, Color.white));
		buttonList.add(new GuiImageButton(102, xGui + 72, yGui + 160, texture, 6, 196, Color.yellow));
		buttonList.add(new GuiImageButton(200, xGui + Width - 22, yGui + 160, texture, 0, 196, Color.white));
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button.enabled)
		{
			selectedTool = button.id;
		}
		super.actionPerformed(button);
	}

	@Override
	protected void mouseClicked(int x, int y, int button)
	{
		int xGui = (width - Width) / 2;
		int yGui = (height - Height) / 2;

		int xGrid = (int) Math.ceil((x - (xGui + GridBorder)) / 6.0) - 1;
		int yGrid = (int) Math.ceil((y - (yGui + GridBorder)) / 6.0) - 1;

		if(xGrid >= 0 && xGrid < wafer.xSize && yGrid >= 0 && yGrid < wafer.ySize)
		{
			if(selectedTool < 100)
				placeComponentPacket((byte) selectedTool, xGrid, yGrid, 0, Side.NONE);
			if(selectedTool == 100)
			{
				if (button == 0)
				{
					placeComponentPacket(MetalComponent.ID, xGrid, yGrid, 1, Side.NONE);
				} else
				{
					removeComponentPacket(xGrid, yGrid, 1);
				}
			}
			if(selectedTool == 101)
				setViaPacket(xGrid, yGrid, !wafer.hasVia(xGrid, yGrid));
			if(selectedTool == 102)
				changeModePacket(xGrid, yGrid);
			if(selectedTool == 200)
				removeComponentPacket(xGrid, yGrid, 0);
		}

		super.mouseClicked(x, y, button);
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int which)
	{
		if(which == 1 || which == 0)
		{
			lastX = -1;
			lastY = -1;
		}
		super.mouseMovedOrUp(x, y, which);
	}

	@Override
	protected void mouseClickMove(int x, int y, int lastClicked, long time)
	{
		int xGui = (width - Width) / 2;
		int yGui = (height - Height) / 2;
		int xGrid = (int) Math.ceil((x - (xGui + GridBorder)) / 6.0) - 1;
		int yGrid = (int) Math.ceil((y - (yGui + GridBorder)) / 6.0) - 1;
		if(xGrid >= 0 && xGrid < wafer.xSize && yGrid >= 0 && yGrid < wafer.ySize)
		{
			if(lastX == -1)
			{
				lastX = xGrid;
				lastY = yGrid;
			}
			if(lastX != xGrid || lastY != yGrid)
			{
				Side mouseSide = getMouseSide(xGrid, yGrid);
				if(selectedTool < 100)
				{
					placeComponentPacket((byte) selectedTool, xGrid, yGrid, 0, mouseSide);
				}

				if(selectedTool == 100)
				{
					if(lastClicked == 0)
						placeComponentPacket(MetalComponent.ID, xGrid, yGrid, 1, mouseSide);
					else
						removeComponentPacket(xGrid, yGrid, 1);
				}

				if(selectedTool == 200)
					removeComponentPacket(xGrid, yGrid, 0);
				lastX = xGrid;
				lastY = yGrid;
			}
		}
		super.mouseClickMove(x, y, lastClicked, time);
	}

	private void changeModePacket(int xGrid, int yGrid)
	{
		java.util.List<PowerPad> powerPads = wafer.getPowerPads();
		for (int i = 0; i < powerPads.size(); i++)
		{
			PowerPad pad = powerPads.get(i);
			if (pad.contains(xGrid, yGrid))
			{
				pad.mode = pad.mode.next();
				PacketHandler.INSTANCE.sendToServer(new PadModeChangePacket(te.getWorldObj().provider.dimensionId, te.xCoord, te.yCoord, te.zCoord, i, pad.mode));
			}
		}
	}

	private void placeComponentPacket(byte id, int xGrid, int yGrid, int layer, Side mouseSide)
	{
		PacketHandler.INSTANCE.sendToServer(new WaferPlacePacket(te.getWorldObj().provider.dimensionId, te.xCoord, te.yCoord, te.zCoord, xGrid, yGrid, layer, mouseSide.ordinal(), id));
		wafer.placeComponent(id, xGrid, yGrid, layer, mouseSide);
	}

	private void removeComponentPacket(int xGrid, int yGrid, int layer)
	{
		PacketHandler.INSTANCE.sendToServer(new WaferRemovePacket(te.getWorldObj().provider.dimensionId, te.xCoord, te.yCoord, te.zCoord, xGrid, yGrid, layer));
		wafer.removeComponent(xGrid, yGrid, layer);
	}

	private void setViaPacket(int xGrid, int yGrid, boolean state)
	{
		PacketHandler.INSTANCE.sendToServer(new WaferPlaceViaPacket(te.getWorldObj().provider.dimensionId, te.xCoord, te.yCoord, te.zCoord, xGrid, yGrid, state));
		wafer.setVia(xGrid, yGrid, state);
	}

	private Side getMouseSide(int xGrid, int yGrid)
	{
		Side side = Side.NONE;
		if(xGrid > lastX)
			side = Side.RIGHT;
		else if(xGrid < lastX)
			side = Side.LEFT;
		else if (yGrid > lastY)
			side = Side.DOWN;
		else if (yGrid < lastY)
			side = Side.UP;
		return side;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3)
	{
		GL11.glColor3f(1f, 1f, 1f);
		drawDefaultBackground();
		mc.getTextureManager().bindTexture(texture);
		int xGui = (width - Width) / 2;
		int yGui = (height - Height) / 2;
		drawBackground(xGui, yGui, Width, Height);
		drawPads(xGui, yGui);

		GL11.glEnable(GL11.GL_BLEND);
		drawSilicon(xGui, yGui, 0);
		drawSilicon(xGui, yGui, 1);
		GL11.glDisable(GL11.GL_BLEND);
		drawVia(xGui, yGui);
		super.drawScreen(mouseX, mouseY, par3);
	}

	private void drawVia(int xGui, int yGui)
	{
		GL11.glColor4f(1f, 1f, 1f, 1f);
		for(int xGrid = 0; xGrid < wafer.xSize; xGrid++)
		{
			for(int yGrid = 0; yGrid < wafer.ySize; yGrid++)
			{
				if(wafer.hasVia(xGrid, yGrid))
				{
					drawTexturedModalRect((xGui + GridBorder) + (xGrid * 6), (yGui + GridBorder) + (yGrid * 6), 6, 196, 6, 6);
				}
			}
		}
	}

	private void drawSilicon(int xGui, int yGui, int layer)
	{
		for(int xGrid = 0; xGrid < wafer.xSize; xGrid++)
		{
			for(int yGrid = 0; yGrid < wafer.ySize; yGrid++)
			{
				byte id = wafer.getID(xGrid, yGrid, layer);
				if(id == 0)
					continue;
				WaferComponent component = ComponentRegistry.getComponent(id);
				if(component instanceof SiliconComponent)
				{
					SiliconComponent siliconComponent = (SiliconComponent) component;

					Color componentColor = component.getPowerStatus(wafer.getMetadata(xGrid, yGrid, layer)) ? siliconComponent.getPoweredColor() : siliconComponent.getColor();
					GL11.glColor4d(componentColor.getRed() / 255f, componentColor.getGreen() / 255f, componentColor.getBlue() / 255f, componentColor.getAlpha() / 255f);

					EnumSet<Side> connectedSides = siliconComponent.getConnectedSides(wafer.getMetadata(xGrid, yGrid, layer));
					int xTex = 0;
					for(Side side : connectedSides)
						xTex |= side.flag;
					drawTexturedModalRect((xGui + GridBorder) + (xGrid * 6), (yGui + GridBorder) + (yGrid * 6), xTex*6, 178, 6, 6);
				}
				else if(component instanceof GateComponent)
				{
					GL11.glColor4f(1f, 0, 0, 1f);
					int xTex = ((GateComponent) component).getHorizontal(wafer.getMetadata(xGrid, yGrid, layer)) ? 0 : 6;
					drawTexturedModalRect((xGui + GridBorder) + (xGrid * 6), (yGui + GridBorder) + (yGrid * 6), xTex, 184, 6, 6);
				}
			}
		}
	}

	private void drawPads(int xGui, int yGui)
	{
		for(PowerPad pad : wafer.getPowerPads())
		{
			int color = 0x550000FF;
			if(pad.mode == PadMode.Output)
				color = 0x55FF0000;
			drawRect(xGui + GridBorder + pad.x * 6, yGui + GridBorder + pad.y * 6, xGui + GridBorder + pad.x * 6 + pad.width * 6, yGui + GridBorder + pad.y * 6 + pad.height * 6, color);
		}
	}

	private void drawBackground(int xGui, int yGui, int width, int height)
	{
		//Draw top
		drawRectangle(xGui, yGui, 8, 8, 0, 0, 8, 8);
		drawRectangle(xGui + 8, yGui, width - 16, 8, 8, 0, 6, 8);
		drawRectangle(xGui + width - 8, yGui, 8, 8, 14, 0, 8, 8);

		//Draw Left
		drawRectangle(xGui, yGui + 8, 8, height - 28, 0, 8, 8, 6);

		//Draw Right
		drawRectangle(xGui + width - 8, yGui + 8, 8, height - 28, 14, 8, 8, 6);

		//Draw Bottom
		drawRectangle(xGui, yGui + height - 20, 8, 20, 0, 14, 8, 20);
		drawRectangle(xGui + 8, yGui + height - 20, width - 16, 20, 8, 14, 6, 20);
		drawRectangle(xGui + width - 8, yGui + height - 20, 8, 20, 14, 14, 8, 20);

		GL11.glColor3f(.85f, .85f, .85f);
		for(int i = 0; i < wafer.xSize * wafer.ySize; i+=2)
		{
			int xGrid = i % wafer.xSize;
			int yGrid = i / wafer.xSize;
			drawTexturedModalRect(xGui + GridBorder + xGrid * 6, yGui + GridBorder + yGrid * 6, 0, 34, 6, 6);
		}

		GL11.glColor3f(.8f, .8f, .8f);
		for(int i = 1; i < wafer.xSize * wafer.ySize; i+=2)
		{
			int xGrid = i % wafer.xSize;
			int yGrid = i / wafer.xSize;
			drawTexturedModalRect(xGui + GridBorder + xGrid * 6, yGui + GridBorder + yGrid * 6, 0, 34, 6, 6);
		}
	}

	public static void drawRectangle(int x, int y, int width, int height, int u, int v, int textureWidth, int textureHeight)
	{
		drawQuad(x, y, 0, width, height, (float) u / 256, (float) v / 256, (float) (u + textureWidth) / 256, (float) (v + textureHeight) / 256);
	}

	public static void drawQuad(int x, int y, int z, int width, int height, float u, float v, float uMax, float vMax)
	{
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + height, z, u, vMax);
		tessellator.addVertexWithUV(x + width, y + height, z, uMax, vMax);
		tessellator.addVertexWithUV(x + width, y, z, uMax, v);
		tessellator.addVertexWithUV(x, y, z, u, v);
		tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);
	}
}
