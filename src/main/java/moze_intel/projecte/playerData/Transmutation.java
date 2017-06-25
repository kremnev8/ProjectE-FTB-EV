package moze_intel.projecte.playerData;

import com.google.common.collect.Lists;

import moze_intel.projecte.api.event.PlayerKnowledgeChangeEvent;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.SimpleStack;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.KnowledgeSyncPKT;
import moze_intel.projecte.utils.Comparators;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class Transmutation {
	private static List<ItemStack> CACHED_TOME_KNOWLEDGE = Lists.newArrayList();

	public static void clearCache()
	{
		CACHED_TOME_KNOWLEDGE.clear();
	}

	public static List<ItemStack> getCacheTomeKnowledge()
	{
		return CACHED_TOME_KNOWLEDGE;
	}

	public static void cacheFullKnowledge()
	{
		for (SimpleStack stack : EMCMapper.emc.keySet())
		{
			if (!stack.isValid())
			{
				continue;
			}

			try
			{
				ItemStack s = stack.toItemStack();
				s.stackSize = 1;

				// Apparently items can still not have EMC if they are in the
				// EMC map.
				if (EMCHelper.doesItemHaveEmc(s) && EMCHelper.getEmcValue(s) > 0 && !ItemHelper.containsItemStack(CACHED_TOME_KNOWLEDGE, s))
				{
					CACHED_TOME_KNOWLEDGE.add(s);
				}
			} catch (Exception e)
			{
				PELogger.logInfo("Failed to cache knowledge for " + stack + ": " + e.toString());
			}
		}
	}

	public static List<ItemStack> getKnowledge(EntityPlayer player)
	{
		TransmutationProps data = TransmutationProps.getDataFor(player);

		return data.getKnowledge();
	}

	public static void addKnowledge(ItemStack stack, EntityPlayer player)
	{
		TransmutationProps data = TransmutationProps.getDataFor(player);
		if (!hasKnowledgeForStack(stack, player))
		{
			data.getKnowledge().add(stack);
			if (!player.worldObj.isRemote)
			{
				MinecraftForge.EVENT_BUS.post(new PlayerKnowledgeChangeEvent(player));
			}
		}
	}

	public static void removeKnowledge(ItemStack stack, EntityPlayer player)
	{
		TransmutationProps data = TransmutationProps.getDataFor(player);
		if (hasKnowledgeForStack(stack, player))
		{
			Iterator<ItemStack> iter = data.getKnowledge().iterator();

			while (iter.hasNext())
			{
				if (ItemStack.areItemStacksEqual(stack, iter.next()))
				{
					iter.remove();
					if (!player.worldObj.isRemote)
					{
						MinecraftForge.EVENT_BUS.post(new PlayerKnowledgeChangeEvent(player));
					}
					break;
				}
			}
		}
	}

	public static void setInputsAndLocks(ItemStack[] stacks, EntityPlayer player)
	{
		TransmutationProps data = TransmutationProps.getDataFor(player);
		data.setInputLocks(stacks);
	}

	public static ItemStack[] getInputsAndLock(EntityPlayer player)
	{
		ItemStack[] locks = TransmutationProps.getDataFor(player).getInputLocks();
		return Arrays.copyOf(locks, locks.length);
	}

	public static boolean hasKnowledgeForStack(ItemStack stack, EntityPlayer player)
	{
		TransmutationProps data = TransmutationProps.getDataFor(player);
		for (ItemStack s : data.getKnowledge())
		{
			if (ItemHelper.basicAreStacksEqual(s, stack))
			{
				return true;
			}
		}
		return false;
	}

	public static void setFullKnowledge(EntityPlayer player, ItemStack tome)
	{
		List<ItemStack> pknow = TransmutationProps.getDataFor(player).getKnowledge();
		ItemStack fullK[] = new ItemStack[CACHED_TOME_KNOWLEDGE.size()];
		Collections.sort(CACHED_TOME_KNOWLEDGE, Comparators.ITEMSTACK_EMC_DESCENDING);
		for (int i = 0; i < CACHED_TOME_KNOWLEDGE.size(); i++)
		{
			fullK[i] = CACHED_TOME_KNOWLEDGE.get(i).copy();
		}
		List<Integer> selected = new ArrayList();
		if (tome.stackTagCompound != null)
		{
			NBTTagCompound tag = tome.stackTagCompound;
			int array[] = tag.getIntArray("Selected");
			if (array != null && array.length > 0)
			{
				for (int i = 0; i < array.length; i++)
				{
					selected.add(array[i]);
				}
			}

			for (int i = 0; i < selected.size(); i++)
			{
				int ix = selected.get(i);
				if (ix >= 0 && ix < fullK.length)
				{
					fullK[ix] = null;
				}
			}
			for (int i = 0; i < fullK.length; i++)
			{
				ItemStack item = fullK[i];
				boolean knows = false;
				if (item != null)
				{
					for (int j = 0; j < pknow.size(); j++)
					{
						ItemStack know = pknow.get(j);
						if (ItemStack.areItemStacksEqual(item, know))
						{
							knows = true;
							break;
						}
					}
					if (!knows)
					{
						pknow.add(item);
					}
				}
			}
			

		}
		else
		{
			TransmutationProps.getDataFor(player).getKnowledge().clear();
			TransmutationProps.getDataFor(player).getKnowledge().addAll(CACHED_TOME_KNOWLEDGE);
		}

		// TransmutationProps.getDataFor(player).getKnowledge().clear();
		// TransmutationProps.getDataFor(player).getKnowledge().addAll(CACHED_TOME_KNOWLEDGE);
		if (!player.worldObj.isRemote)
		{
			MinecraftForge.EVENT_BUS.post(new PlayerKnowledgeChangeEvent(player));
		}
	}

	public static void clearKnowledge(EntityPlayer player)
	{
		TransmutationProps data = TransmutationProps.getDataFor(player);
		data.getKnowledge().clear();
		if (!player.worldObj.isRemote)
		{
			MinecraftForge.EVENT_BUS.post(new PlayerKnowledgeChangeEvent(player));
		}
	}

	public static double getEmc(EntityPlayer player)
	{
		return TransmutationProps.getDataFor(player).getTransmutationEmc();
	}

	public static void setEmc(EntityPlayer player, double emc)
	{
		TransmutationProps.getDataFor(player).setTransmutationEmc(emc);
	}

	public static void sync(EntityPlayer player)
	{
		if (player.worldObj.isRemote == false)
		{
			PacketHandler.sendTo(new KnowledgeSyncPKT(TransmutationProps.getDataFor(player).saveForPacket()), (EntityPlayerMP) player);
			PELogger.logDebug("** SENT TRANSMUTATION DATA **");
		}
	}
}
