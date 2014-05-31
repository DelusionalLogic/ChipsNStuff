package dk.slashwin.chipsnstuff;

import dk.slashwin.chipsnstuff.circuit.Wafer;

import java.util.LinkedList;

public class WaferProvider
{
	private static LinkedList<Wafer> WaferCache;

	public static Wafer getWafer(int id)
	{
		return WaferCache.get(id);
	}

	public static int newWafer(int xSize, int ySize, int layers)
	{
		int id = WaferCache.size();
		WaferCache.push(new Wafer(xSize, ySize, layers));
		return id;
	}

	public static int getID(Wafer wafer)
	{
		return WaferCache.indexOf(wafer);
	}
}
