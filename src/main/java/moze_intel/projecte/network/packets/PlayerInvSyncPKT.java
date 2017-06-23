package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class PlayerInvSyncPKT implements IMessage
{
	private InventoryPlayer inv;
	private NBTTagList list;

	public PlayerInvSyncPKT() {}

	public PlayerInvSyncPKT(EntityPlayer player)
	{
		this.inv = player.inventory;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		NBTTagCompound nbt = ByteBufUtils.readTag(buf);
		list = nbt.getTagList("Pinv", 10);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList nbtl = new NBTTagList();
		inv.writeToNBT(nbtl);
		nbt.setTag("Pinv", nbtl);
		ByteBufUtils.writeTag(buf, nbt);
	}

	public static class Handler implements IMessageHandler<PlayerInvSyncPKT, IMessage>
	{
		@SideOnly(Side.CLIENT)
		@Override
		public IMessage onMessage(final PlayerInvSyncPKT message, MessageContext ctx)
		{
			if (message != null)
			{
			Minecraft.getMinecraft().thePlayer.inventory.readFromNBT(message.list);
			}
			return null;
		}
	}
}
