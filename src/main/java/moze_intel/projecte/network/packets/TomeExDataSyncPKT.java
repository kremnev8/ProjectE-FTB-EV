package moze_intel.projecte.network.packets;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import moze_intel.projecte.gameObjs.container.TomeContainer;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.utils.PELogger;


public class TomeExDataSyncPKT implements IMessage
{
	public TomeExDataSyncPKT() {}

	private List<Integer> selected = new ArrayList<>();
	
	public TomeExDataSyncPKT(List<Integer> selected)
	{
		this.selected = selected;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int size = buf.readInt();
		selected.clear();
		for (int i = 0; i < size; i++)
		{
			selected.add(buf.readInt());
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(selected.size());
		for (int i = 0; i < selected.size(); i++)
		{
			int ix = selected.get(i);
			buf.writeInt(ix);
		}
	}

	public static class Handler implements IMessageHandler<TomeExDataSyncPKT, IMessage>
	{
		@Override
		public IMessage onMessage(final TomeExDataSyncPKT pkt, final MessageContext ctx)
		{
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
			{
				if (ctx.getServerHandler().playerEntity != null)
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					if (player.getHeldItem() != null)
					{
						ItemStack tome = player.getHeldItem();
						if (tome.stackTagCompound == null) tome.stackTagCompound = new NBTTagCompound();
						int array[] = new int[pkt.selected.size()];
						for (int i = 0; i < pkt.selected.size(); i++)
						{
							if (pkt.selected.get(i) != null && pkt.selected.get(i) > 0)
							{
								array[i] = pkt.selected.get(i);
							}
						}
						tome.stackTagCompound.setIntArray("Selected", array);
						
					}
				}
			}

			return null;
		}
	}
}
