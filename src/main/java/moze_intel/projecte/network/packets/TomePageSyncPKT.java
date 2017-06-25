package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import moze_intel.projecte.gameObjs.container.TomeContainer;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.utils.PELogger;


public class TomePageSyncPKT implements IMessage
{
	public TomePageSyncPKT() {}

	private int pageNum;
	
	public TomePageSyncPKT(int pageNum)
	{
		this.pageNum = pageNum;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pageNum = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(pageNum);
	}

	public static class Handler implements IMessageHandler<TomePageSyncPKT, IMessage>
	{
		@Override
		public IMessage onMessage(final TomePageSyncPKT pkt, final MessageContext ctx)
		{
			if (ctx.getServerHandler().playerEntity.openContainer instanceof TomeContainer)
			{
				TomeContainer container = ((TomeContainer) ctx.getServerHandler().playerEntity.openContainer);
				container.tomeInventory.currentPage = pkt.pageNum;
				container.tomeInventory.updateOutputs();
			}

			return null;
		}
	}
}
