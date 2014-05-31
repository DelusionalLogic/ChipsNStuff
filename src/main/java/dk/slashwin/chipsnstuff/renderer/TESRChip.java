package dk.slashwin.chipsnstuff.renderer;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class TESRChip extends TESRGate
{
	IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation("chipsnstuff:models/chip.obj"));
	ResourceLocation texture = new ResourceLocation("chipsnstuff:textures/models/chip.png");

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f)
	{
		super.renderTileEntityAt(te, x, y, z, f);
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		model.renderAll();
		GL11.glPopMatrix();
	}
}
