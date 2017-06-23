package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class KleinStarsSyncPKT implements IMessage
{
	private InventoryPlayer inv;
	private NBTTagList list;

	public KleinStarsSyncPKT() {}

	@Override
	public void fromBytes(ByteBuf buf)
	{
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
	}

	public static class Handler implements IMessageHandler<KleinStarsSyncPKT, IMessage>
	{
		@SideOnly(Side.CLIENT)
		@Override
		public IMessage onMessage(final KleinStarsSyncPKT message, MessageContext ctx)
		{
			if (message != null)
			{
				TransmutationContainer cont = (TransmutationContainer)Minecraft.getMinecraft().thePlayer.openContainer;
				cont.transmutationInventory.updateOutputs(true);
			}
			return null;
		}
	}
}
