package moze_intel.projecte.gameObjs.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class Tome extends ItemPE implements IExtraFunction
{
	public Tome()
	{
		this.setUnlocalizedName("tome");
		this.setCreativeTab(ObjHandler.cTab);
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
	}

	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack itemstack)
	{
		return false; 
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("tome"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4)
	{
		list.add(StatCollector.translateToLocal("pe.tome.tooltip1"));
		list.add(StatCollector.translateToLocal("pe.tome.tooltip2"));
	}

	@Override
	public void doExtraFunction(ItemStack stack, EntityPlayer player)
	{
		if (!player.worldObj.isRemote)
		{
			player.openGui(PECore.instance, Constants.TOME_GUI, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
		}
	}
}





