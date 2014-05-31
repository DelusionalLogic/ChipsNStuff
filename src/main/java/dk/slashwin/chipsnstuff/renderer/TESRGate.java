package dk.slashwin.chipsnstuff.renderer;

import dk.slashwin.chipsnstuff.ThePlaceWithTheBlocks;
import dk.slashwin.chipsnstuff.tileentity.TEChip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;

public class TESRGate extends TileEntitySpecialRenderer
{
	IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation("chipsnstuff:models/plate.obj"));
	ResourceLocation texture = new ResourceLocation("chipsnstuff:textures/models/plate.png");
	ResourceLocation redTex = new ResourceLocation("chipsnstuff:textures/models/redstone.png");

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f)
	{
		if(te instanceof TEChip)
		{
			EnumMap<ForgeDirection, Boolean> powerMap = new EnumMap<ForgeDirection, Boolean>(ForgeDirection.class);
			for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
				powerMap.put(dir, ((TEChip) te).isPowered(dir));
			render(x, y, z, powerMap);
		}
	}

	protected void render(double x, double y, double z, EnumMap<ForgeDirection, Boolean> powerMap)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		model.renderAll();

		Minecraft.getMinecraft().getTextureManager().bindTexture(redTex);
		GL11.glDisable(GL11.GL_LIGHTING);

		Tessellator tess = Tessellator.instance;
		tess.setBrightness(16);
		tess.setColorOpaque_F(1f, 1f, 1f);

		ForgeDirection[] dirs = new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.WEST};
		for(ForgeDirection dir : dirs)
		{
			tess.startDrawingQuads();
			drawRect(tess, -.0625, .126, -.5, .125, .375, powerMap.get(dir) ? 2 : 0, 0, 2, 6);
			tess.draw();
			GL11.glRotated(-90, 0, 1, 0);
		}
		GL11.glPopMatrix();
	}

	private void drawRect(Tessellator tess, double x, double y, double z, double width, double height, int xTex, int yTex, int widthTex, int heightTex)
	{
		double u1 = xTex / 8d;
		double u2 = (xTex + widthTex) / 8d;
		double v1 = yTex / 8d;
		double v2 = (yTex + heightTex) / 8d;
		tess.addVertexWithUV(x, y, z, u1, v1);
		tess.addVertexWithUV(x, y, z + height, u1, v2);
		tess.addVertexWithUV(x + width, y, z + height, u2, v2);
		tess.addVertexWithUV(x + width, y, z, u2, v1);
	}
}
