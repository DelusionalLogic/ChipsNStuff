package dk.slashwin.chipsnstuff.circuit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ComponentRegistry
{
	public static HashMap<Byte, WaferComponent> componentMap = new HashMap<Byte, WaferComponent>();

	public static void registerComponent(byte id, WaferComponent component)
	{
		componentMap.put(id, component);
	}

	public static WaferComponent getComponent(byte id)
	{
		return componentMap.get(id);
	}
}
