package dk.slashwin.chipsnstuff.renderer;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;

public class TESRChip extends TESRGate
{
	IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation("chipsnstuff:models/chip.obj"));
	ResourceLocation texture = new ResourceLocation("chipsnstuff:textures/models/chip.png");

	@Override
	protected void render(double x, double y, double z, EnumMap<ForgeDirection, Boolean> powerMap)
	{
		super.render(x, y, z, powerMap);
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		model.renderAll();
		GL11.glPopMatrix();
	}
}
