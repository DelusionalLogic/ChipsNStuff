package dk.slashwin.chipsnstuff.circuit;

public interface IConnectable
{
	boolean connectedToSide(Wafer wafer, int x, int y, int layer, Side side);
}
