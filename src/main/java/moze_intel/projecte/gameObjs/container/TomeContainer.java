package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.inventory.TomeInventory;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.container.slots.DisplaySlot;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotConsume;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotInput;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotLock;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotOutput;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotUnlearn;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SearchUpdatePKT;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class TomeContainer extends Container
{
	public TomeInventory tomeInventory;

	public TomeContainer(EntityPlayer player, TomeInventory inventory)
	{
		this.tomeInventory = inventory;
		
		int baseX = 12;
		int baseY = 21;
		
		for (int y = 0; y < 8; y++)
		{
			for (int x = 0; x < 13; x++)
			{
				addSlotToContainer(new DisplaySlot(tomeInventory, (13*y)+x, x*18+baseX, y*18+baseY));
			}
		}
		
		tomeInventory.openInventory();
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) 
	{
		return true;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
	{
		/*Slot slot = this.getSlot(slotIndex);
		
		if (slot == null || !slot.getHasStack()) 
		{
			return null;
		}
		
		ItemStack stack = slot.getStack();
		ItemStack newStack = stack.copy();
		
		if (slotIndex <= 7) //Input Slots
		{
			return null;
		}
		else if (slotIndex >= 10 && slotIndex <= 25) // Output Slots
		{	
			int emc = EMCHelper.getEmcValue(newStack);
			
			int stackSize = 0;
			
			while (tomeInventory.getEmc() >= emc && stackSize < newStack.getMaxStackSize() && ItemHelper.hasSpace(player.inventory.mainInventory, newStack))
			{
				tomeInventory.removeEmc(emc);
				ItemHelper.pushStackInInv(player.inventory, ItemHelper.getNormalizedStack(newStack));
				stackSize++;
			}
			
			tomeInventory.updateOutputs();
		}
		else if (slotIndex >= 26) //Unlearn Slot and Player Inventory
		{
			int emc = EMCHelper.getEmcValue(stack);
			
			if (emc == 0 && stack.getItem() != ObjHandler.tome)
			{
				return null;
			}
			
			while(!tomeInventory.hasMaxedEmc() && stack.stackSize > 0)
			{
				tomeInventory.addEmc(emc);
				--stack.stackSize;
			}
			
			tomeInventory.handleKnowledge(newStack);

			if (stack.stackSize == 0)
			{
				slot.putStack(null);
			}
		}
		*/
		return null;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		tomeInventory.closeInventory();
	}


	@Override
	public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player)
	{
		return super.slotClick(slot, button, flag, player);
	}
	
	@Override
	public boolean canDragIntoSlot(Slot slot) 
	{
		return false;
	}
}
