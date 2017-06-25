package moze_intel.projecte.gameObjs.container.inventory;

import com.google.common.collect.Lists;

import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.KleinStarsSyncPKT;
import moze_intel.projecte.network.packets.PlayerInvSyncPKT;
import moze_intel.projecte.network.packets.SearchUpdatePKT;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.Comparators;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.ItemSearchHelper;
import moze_intel.projecte.utils.NBTWhitelist;
import moze_intel.projecte.utils.PELogger;

import com.google.common.collect.Queues;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class TomeInventory implements IInventory
{
	public EntityPlayer player = null;

	private ItemStack[] inventory = new ItemStack[104];
	
	public int currentPage = 0;
	
	public TomeInventory(EntityPlayer player)
	{
		this.player = player;
	}
	

	
	public void checkForUpdates()
	{
	
	}

	public void updateOutputs() {
		List<ItemStack> cache = Transmutation.getCacheTomeKnowledge();
		List<ItemStack> cacheCopy = new ArrayList<>();
		for (int i = 0; i < cache.size(); i++)
		{
			cacheCopy.add(cache.get(i).copy());
		}
		Collections.sort(cacheCopy, Comparators.ITEMSTACK_EMC_DESCENDING);
		for (int i = currentPage*104; i < cacheCopy.size(); i++)
		{
			if (i-currentPage*104 == inventory.length)
			{
				break;
			}
			inventory[i-currentPage*104] = cacheCopy.get(i);
		}
		if (cacheCopy.size()-currentPage*104 < 104)
		{
			int wr = cacheCopy.size()-currentPage*104;
			if (wr > 0)
			{
				for (int i = wr+1; i < 104; i++)
				{
					inventory[i] = null;
				}
				
			}
		}
		markDirty();
	}
	
	@Override
	public int getSizeInventory() 
	{
		return 104;
	}

	@Override
	public ItemStack getStackInSlot(int slot) 
	{
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int qty) 
	{
		ItemStack stack = inventory[slot];
		if (stack != null)
		{
			if (stack.stackSize <= qty)
			{
				inventory[slot] = null;
			}
			else
			{
				stack = stack.splitStack(qty);
				if (stack.stackSize == 0)
				{
					inventory[slot] = null;
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) 
	{
		if (inventory[slot] != null)
		{
			ItemStack stack = inventory[slot];
			inventory[slot] = null;
			return stack;
		}
		
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) 
	{
		inventory[slot] = stack;
		
		if (stack != null && stack.stackSize > this.getInventoryStackLimit())
		{
			stack.stackSize = this.getInventoryStackLimit();
		}
		
		this.markDirty();
	}

	@Override
	public String getInventoryName() 
	{
		return "item.pe_tome_selector.name";
	}

	@Override
	public boolean hasCustomInventoryName() 
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit() 
	{
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) 
	{
		return true;
	}

	@Override
	public void openInventory() 
	{
		updateOutputs();
	}

	@Override
	public void closeInventory()
	{
		
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) 
	{
		return false;
	}

	@Override
	public void markDirty() {}
}
