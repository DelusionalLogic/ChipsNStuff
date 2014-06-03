package dk.slashwin.chipsnstuff;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import dk.slashwin.chipsnstuff.circuit.*;
import dk.slashwin.chipsnstuff.tileentity.TEChip;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class CommonProxy
{
	public void registerBlocks()
	{
		GameRegistry.registerBlock(ThePlaceWithTheBlocks.bChip, "chip");

		GameRegistry.registerTileEntity(TEChip.class, "chipTE");

		ComponentRegistry.registerComponent(PComponent.ID, new PComponent());
		ComponentRegistry.registerComponent(NComponent.ID, new NComponent());
		ComponentRegistry.registerComponent(MetalComponent.ID, new MetalComponent());
		ComponentRegistry.registerComponent(PNPComponent.ID, new PNPComponent());
		ComponentRegistry.registerComponent(NPNComponent.ID, new NPNComponent());
	}

	public void registerGUI()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(ChipsnStuff.instance, new GuiHandler());
	}

	public void registerRecipe()
	{
		ItemStack blackStack = new ItemStack(Blocks.stained_hardened_clay, 1, 15);
		GameRegistry.addRecipe(new ItemStack(ThePlaceWithTheBlocks.bChip, 1),
				"rbr",
				"sss",
				's', Blocks.stone,
				'b', blackStack,
				'r', Items.redstone);
	}

	public void registerTESR()
	{
	}
}
