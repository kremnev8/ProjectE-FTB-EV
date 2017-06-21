package moze_intel.projecte.gameObjs.container.inventory;

import com.google.common.collect.Lists;

import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.network.PacketHandler;
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
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class TransmutationInventory implements IInventory
{
	private double emc;
	public static double maxPlayerEmc = 50000;
	public List<ItemStack> playerKleinStars = Lists.newArrayList();//.getDouble("StoredEMC");
	
	public EntityPlayer player = null;
	private static final int LOCK_INDEX = 8;
	private static final int[] MATTER_INDEXES = new int[] {12, 11, 13, 10, 14, 21, 15, 20, 16, 19, 17, 18};
	private static final int[] FUEL_INDEXES = new int[] {22, 23, 24, 25};
	private ItemStack[] inventory = new ItemStack[27];
	public int learnFlag = 0;
	public int unlearnFlag = 0;
	public String filter = "";
	public int searchpage = 0;
	public List<ItemStack> knowledge = Lists.newArrayList();
	
	public TransmutationInventory(EntityPlayer player)
	{
		this.player = player;
	}
	
	public void handleKnowledge(ItemStack stack)
	{
		if (stack.stackSize > 1)
		{
			stack.stackSize = 1;
		}
		
		if (!stack.getHasSubtypes() && stack.getMaxDamage() != 0 && stack.getItemDamage() != 0)
		{
			stack.setItemDamage(0);
		}
		
		if (!Transmutation.hasKnowledgeForStack(stack, player))
		{
			learnFlag = 300;
			
			if (stack.getItem() == ObjHandler.tome)
			{
				Transmutation.setFullKnowledge(player);
			}
			else
			{
				if (stack.hasTagCompound() && !NBTWhitelist.shouldDupeWithNBT(stack))
				{
					stack.stackTagCompound = null;
				}

				Transmutation.addKnowledge(stack, player);
			}
			
			if (!player.worldObj.isRemote)
			{
				Transmutation.sync(player);
			}
		}
		
		updateOutputs();
	}

	public void handleUnlearn(ItemStack stack)
	{
		if (stack.stackSize > 1)
		{
			stack.stackSize = 1;
		}

		if (!stack.getHasSubtypes() && stack.getMaxDamage() != 0 && stack.getItemDamage() != 0)
		{
			stack.setItemDamage(0);
		}
		
		if (Transmutation.hasKnowledgeForStack(stack, player))
		{
			unlearnFlag = 300;

			if (stack.hasTagCompound() && !NBTWhitelist.shouldDupeWithNBT(stack))
			{
				stack.stackTagCompound = null;
			}

			Transmutation.removeKnowledge(stack, player);
			
			if (!player.worldObj.isRemote)
			{
				Transmutation.sync(player);
			}
		}
		
		updateOutputs();
	}
	
	public void checkForUpdates()
	{
		int matterEmc = EMCHelper.getEmcValue(inventory[MATTER_INDEXES[0]]);
		int fuelEmc = EMCHelper.getEmcValue(inventory[FUEL_INDEXES[0]]);
		
		int maxEmc = matterEmc > fuelEmc ? matterEmc : fuelEmc;
		
		if (maxEmc > emc)
		{
			updateOutputs();
		}
	}

	public void updateOutputs() {
		updateOutputs(false);
	}
	@SuppressWarnings("unchecked")
	public void updateOutputs(boolean async)
	{
		if (!this.player.worldObj.isRemote) {
			return;
		}
		knowledge = Lists.newArrayList(Transmutation.getKnowledge(player));

		for (int i : MATTER_INDEXES)
		{
			inventory[i] = null;
		}
		
		for (int i : FUEL_INDEXES)
		{
			inventory[i] = null;
		}
		
		ItemStack lockCopy = null;

		Collections.sort(knowledge, Comparators.ITEMSTACK_EMC_DESCENDING);
		ItemSearchHelper searchHelper = ItemSearchHelper.create(filter);
		if (inventory[LOCK_INDEX] != null)
		{
			int reqEmc = EMCHelper.getEmcValue(inventory[LOCK_INDEX]);
			
			if (this.emc < reqEmc)
			{
				return;
			}

			lockCopy = ItemHelper.getNormalizedStack(inventory[LOCK_INDEX]);

			if (lockCopy.hasTagCompound() && !NBTWhitelist.shouldDupeWithNBT(lockCopy))
			{
				lockCopy.setTagCompound(new NBTTagCompound());
			}
			
			Iterator<ItemStack> iter = knowledge.iterator();
			int pagecounter = 0;
			
			while (iter.hasNext())
			{
				ItemStack stack = iter.next();
				
				if (EMCHelper.getEmcValue(stack) > reqEmc)
				{
					iter.remove();
					continue;
				}

				if (ItemHelper.basicAreStacksEqual(lockCopy, stack))
				{
					iter.remove();
					continue;
				}

				if (!searchHelper.doesItemMatchFilter(stack)) {
					iter.remove();
					continue;
				}

				if (pagecounter < (searchpage * 12))
				{
					pagecounter++;
					iter.remove();
					continue;
				}
			}
		}
		else
		{
			Iterator<ItemStack> iter = knowledge.iterator();
			int pagecounter = 0;
			
			while (iter.hasNext())
			{
				ItemStack stack = iter.next();
				
				if (emc < EMCHelper.getEmcValue(stack))
				{
					iter.remove();
					continue;
				}

				if (!searchHelper.doesItemMatchFilter(stack)) {
					iter.remove();
					continue;
				}

				if (pagecounter < (searchpage * 12))
				{
					pagecounter++;
					iter.remove();
					continue;
				}
			}
		}
		
		int matterCounter = 0;
		int fuelCounter = 0;

		if (lockCopy != null)
		{
			if (FuelMapper.isStackFuel(lockCopy))
			{
				inventory[FUEL_INDEXES[0]] = lockCopy;
				fuelCounter++;
			}
			else
			{
				inventory[MATTER_INDEXES[0]] = lockCopy;
				matterCounter++;
			}
		}
		
		for (ItemStack stack : knowledge)
		{
			if (FuelMapper.isStackFuel(stack))
			{
				if (fuelCounter < 4)
				{
					inventory[FUEL_INDEXES[fuelCounter]] = stack;
				
					fuelCounter++;
				}
			}
			else
			{
				if (matterCounter < 12)
				{
					inventory[MATTER_INDEXES[matterCounter]] = stack;
					
					matterCounter++;
 				}
			}
		}
	}

	public void writeIntoOutputSlot(int slot, ItemStack item)
	{

		if (EMCHelper.doesItemHaveEmc(item) && EMCHelper.getEmcValue(item) <= this.emc && Transmutation.hasKnowledgeForStack(item, player))
		{
			inventory[slot] = item;
		}
		else
		{
			inventory[slot] = null;
		}
	}

	public List<ItemStack> getOutputSlots() {
		return Arrays.asList(inventory).subList(10,26);
	}
	
	@Override
	public int getSizeInventory() 
	{
		return 26;
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
		return "item.pe_transmutation_tablet.name";
	}

	@Override
	public boolean hasCustomInventoryName() 
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit() 
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) 
	{
		return true;
	}
	
	public List<ItemStack> findKleinStars(EntityPlayer player)
	{
		List<ItemStack> stars = Lists.newArrayList();
		InventoryPlayer pinv = player.inventory;
		
		for (int i = 0; i < pinv.getSizeInventory(); i++) {
			ItemStack item = pinv.getStackInSlot(i);
			if (item != null && item.getItem() instanceof KleinStar && stars.size() < 20)
			{
				stars.add(item);
			}
		}
		return stars;
	}

	@Override
	public void openInventory() 
	{
		emc = Transmutation.getEmc(player);
		playerKleinStars = findKleinStars(player);
		
		ItemStack[] inputLocks = Transmutation.getInputsAndLock(player);
		System.arraycopy(inputLocks, 0, inventory, 0, 9);
		if (this.player.worldObj.isRemote)
		{
			updateOutputs(true);
		}
	}

	@Override
	public void closeInventory()
	{
		if (!player.worldObj.isRemote)
		{
			Transmutation.setEmc(player, emc);
			Transmutation.setInputsAndLocks(Arrays.copyOfRange(inventory, 0, 9), player);
			Transmutation.sync(player);
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) 
	{
		return false;
	}

	@Override
	public void markDirty() {}
	
	public double getEmc()
	{
		double kleinEmc = 0;
		
		for (int i = 0; i < playerKleinStars.size(); i++) {
			ItemStack star = playerKleinStars.get(i);
			NBTTagCompound nbt = star.getTagCompound();
			kleinEmc = kleinEmc + nbt.getDouble("StoredEMC");
		}
		return emc + kleinEmc;
	}
	
	public double getMaxEmc()
	{
		if (playerKleinStars.size() == 0)
		{
			return maxPlayerEmc;
		}else
		{
			double kleinMaxEmc = 0;
			
			for (int i = 0; i < playerKleinStars.size(); i++) {
				ItemStack star = playerKleinStars.get(i);
				NBTTagCompound nbt = star.getTagCompound();
				kleinMaxEmc = kleinMaxEmc + EMCHelper.getKleinStarMaxEmc(star);
			}
		    return kleinMaxEmc + maxPlayerEmc;
		}
	}
	
	public void setEmc(double value)
	{
		
		if (playerKleinStars.size() == 0)
		{
			emc = value;
		}else
		{
			double sett = value;
			
			for (int i = 0; i < playerKleinStars.size(); i++) {
				ItemStack star = playerKleinStars.get(i);
				NBTTagCompound nbt = star.getTagCompound();
				double starEmc = nbt.getDouble("StoredEMC");
				if (sett <= EMCHelper.getKleinStarMaxEmc(star))
				{
					nbt.setDouble("StoredEMC", sett);
					if (EMCHelper.getKleinStarMaxEmc(star) >= sett)
					{
						sett = 0;
					}else
					{
						sett = sett - EMCHelper.getKleinStarMaxEmc(star);
					}
					star.setTagCompound(nbt);
				}
			}
			if (sett <= maxPlayerEmc)
			{
				emc = sett;
			}
		}
	}
	
	public boolean addEmc(double value)
	{
		if (playerKleinStars.size() == 0)
		{
			if (emc + value <= maxPlayerEmc)
			{
				emc += value;
				return true;
			}
		}else
		{
			double putt = value;
			for (int i = 0; i < playerKleinStars.size(); i++) {
				ItemStack star = playerKleinStars.get(i);
				NBTTagCompound nbt = star.getTagCompound();
				double starEmc = nbt.getDouble("StoredEMC");
				if (starEmc + putt <= EMCHelper.getKleinStarMaxEmc(star))
				{
					nbt.setDouble("StoredEMC", starEmc + putt);
					star.setTagCompound(nbt);
					putt = 0;
				}else
				{
					nbt.setDouble("StoredEMC", EMCHelper.getKleinStarMaxEmc(star));
					star.setTagCompound(nbt);
					putt = putt - (EMCHelper.getKleinStarMaxEmc(star)-starEmc);
				}
			}
			if (putt == 0)
			{
				return true;
			}
			if (emc + putt <= maxPlayerEmc)
			{
				emc += putt;
				return true;
			}
		}
		return false;
	}
	
	public boolean removeEmc(double value) 
	{
		if (playerKleinStars.size() == 0)
		{
			if (emc - value >= 0)
			{
				emc -= value;
				return true;
			}
		}else
		{
			for (int i = 0; i < playerKleinStars.size(); i++) {
				ItemStack star = playerKleinStars.get(i);
				NBTTagCompound nbt = star.getTagCompound();
				double starEmc = nbt.getDouble("StoredEMC");
				if (starEmc - value >= 0)
				{
					nbt.setDouble("StoredEMC", starEmc - value);
					star.setTagCompound(nbt);
					return true;
				}
			}
			if (emc - value >= 0)
			{
				emc -= value;
				return true;
			}
		}
		return false;
	}

	public boolean hasMaxedEmc()
	{
		if (playerKleinStars.size() == 0)
		{
			return emc >= maxPlayerEmc;
		}else
		{
			double kleinEmc = 0;
			double kleinMaxEmc = 0;
			
			for (int i = 0; i < playerKleinStars.size(); i++) {
				ItemStack star = playerKleinStars.get(i);
				NBTTagCompound nbt = star.getTagCompound();
				kleinEmc = kleinEmc + nbt.getDouble("StoredEMC");
				kleinMaxEmc = kleinMaxEmc + EMCHelper.getKleinStarMaxEmc(star);
			}
			if (kleinEmc >= kleinMaxEmc && emc >= maxPlayerEmc)
			{
				return true;
			}
		}
		return false;
	}
}
