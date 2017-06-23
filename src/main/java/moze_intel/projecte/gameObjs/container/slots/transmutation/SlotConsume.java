package moze_intel.projecte.gameObjs.container.slots.transmutation;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotConsume extends Slot
{
	private TransmutationInventory inv;
	
	public SlotConsume(TransmutationInventory inv, int par2, int par3, int par4)
	{
		super(inv, par2, par3, par4);
		this.inv = inv;
	}
	
	@Override
	public void putStack(ItemStack stack)
	{
		if (stack == null)
		{
			return;
		}
		
		ItemStack cache = stack.copy();
		
		double toAdd = 0;
		
		while ((!inv.hasMaxedEmc()&&EMCHelper.getEmcValue(stack) + toAdd + inv.getEmc() <= inv.getMaxEmc()) && stack.stackSize > 0)
		{
			toAdd += EMCHelper.getEmcValue(stack);
			stack.stackSize--;
		}
		inv.player.inventory.addItemStackToInventory(stack);
		
		inv.addEmc(toAdd);
		if (stack.stackSize > 0)
		
		
		this.onSlotChanged();
		inv.handleKnowledge(cache);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return !inv.hasMaxedEmc() && EMCHelper.getEmcValue(stack) + inv.getEmc() <= inv.getMaxEmc() && (EMCHelper.doesItemHaveEmc(stack) || stack.getItem() == ObjHandler.tome);
	}
}
