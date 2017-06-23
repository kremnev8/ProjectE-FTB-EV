package moze_intel.projecte.gameObjs.container.slots.transmutation;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.items.MercurialEye;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class SlotInput extends Slot {
    private TransmutationInventory inv;
    
    public SlotInput(TransmutationInventory inv, int par2, int par3, int par4)
    {
        super(inv, par2, par3, par4);
        this.inv = inv;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return !this.getHasStack() && EMCHelper.doesItemHaveEmc(stack);
    }
    
    @Override
    public void putStack(ItemStack stack)
    {
        if (stack == null)
        {
            return;
        }
        
        super.putStack(stack);
        
        if (stack.getItem() instanceof IItemEmc)
        {
            inv.updateKleinStars();
            
            IItemEmc itemEmc = ((IItemEmc) stack.getItem());
            double remainingEmc = itemEmc.getMaximumEmc(stack) - (int) Math.ceil(itemEmc.getStoredEmc(stack));
            
            if (remainingEmc == 0)
            {
                if (inv.getMaxEmc() >= inv.getEmc() + itemEmc.getMaximumEmc(stack))
                {
                    inv.addEmc(itemEmc.getMaximumEmc(stack));
                    itemEmc.extractEmc(stack, itemEmc.getMaximumEmc(stack));
                }
            }
            else
            {
                
                if (inv.getEmc() >= remainingEmc)
                {
                    itemEmc.addEmc(stack, remainingEmc);
                    inv.removeEmc(remainingEmc);
                }
                else
                {
                    itemEmc.addEmc(stack, inv.getEmc());
                    inv.setEmc(0);
                }
            }
        }
        
        if (stack.getItem() instanceof MercurialEye)
        {
            MercurialEye eye = (MercurialEye)stack.getItem();
            ItemStack kStar =  eye.getInventory(stack)[0];
            int stored = (int) kStar.stackTagCompound.getDouble("StoredEMC");
            int diff = MathHelper.clamp_int((int) inv.getEmc(), 0, Constants.MAX_KLEIN_EMC[kStar.getItemDamage()] - stored);
            eye.addKleinEMC(stack, diff);
            inv.removeEmc(diff);
        }
        
        if (stack.getItem() != ObjHandler.tome)
        {
            inv.handleKnowledge(stack.copy());
        }
        else
        {
            inv.updateOutputs();
        }
    }
    
    @Override
    public int getSlotStackLimit()
    {
        return 1;
    }
}
