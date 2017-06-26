package moze_intel.projecte.gameObjs.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.TomeContainer;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.container.inventory.TomeInventory;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.TomeExDataSyncPKT;
import moze_intel.projecte.network.packets.TomePageSyncPKT;
import moze_intel.projecte.playerData.Transmutation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GUITome extends GuiContainer {
	private static ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/tome.png");
	TomeInventory inv;

	int xLocation;
	int yLocation;

	int lastClcX = -1;// not used
	int lastClcY = -1;

	List<Integer> selected = new ArrayList<>();

	int numPages = 0;
	int currentPage = 0;

	public GUITome(EntityPlayer player, TomeInventory inventory)
	{
		super(new TomeContainer(player, inventory));
		this.inv = inventory;
		this.xSize = 256;
		this.ySize = 193;

		selected.clear();
		if (player.getHeldItem() != null && player.getHeldItem().getItem() == ObjHandler.tome)
		{
			ItemStack tome = player.getHeldItem();
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
			}
		}

		int fullKnowledgeSize = Transmutation.getCacheTomeKnowledge().size();
		numPages = fullKnowledgeSize / 104;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		this.xLocation = (this.width - this.xSize) / 2;
		this.yLocation = (this.height - this.ySize) / 2;

		this.buttonList.add(new GuiButton(1, this.xLocation + 11, this.yLocation + 171, 14, 14, "<"));
		this.buttonList.add(new GuiButton(2, this.xLocation + 231, this.yLocation + 171, 14, 14, ">"));
		this.buttonList.add(new GuiButton(3, this.xLocation + 92, this.yLocation + 168, 72, 20, StatCollector.translateToLocal("pe.tomegui.reset")));

		this.buttonList.add(new CustomGuiButton(4, this.xLocation + 172, this.yLocation + 168, false));
		this.buttonList.add(new CustomGuiButton(5, this.xLocation + 200, this.yLocation + 168, true));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
	{
		if (currentPage == 0)
		{
			((GuiButton) this.buttonList.get(0)).enabled = false;
		}
		else
		{
			((GuiButton) this.buttonList.get(0)).enabled = true;
		}

		if (currentPage == numPages)
		{
			((GuiButton) this.buttonList.get(1)).enabled = false;
		}
		else
		{
			((GuiButton) this.buttonList.get(1)).enabled = true;
		}

		GL11.glColor4f(1F, 1F, 1F, 1F);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2)
	{
		// GL11.glDisable(GL11.GL_ALPHA_TEST);
		int baseX = 11;
		int baseY = 20;
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		if (selected.size() > 0)
		{
			this.zLevel = 101.0F;
			GL11.glEnable(GL11.GL_BLEND);
			for (int i = 0; i < selected.size(); i++)
			{
				int num = selected.get(i);
				int page = Math.floorDiv(num, 104);
				if (page == currentPage)
				{
					int y = Math.floorDiv(num - 104 * page, 13);
					int x = num - 104 * page - 13 * y;
					this.drawTexturedModalRect(x * 18 + baseX, y * 18 + baseY, 2, 195, 18, 18);
				}
			}
			GL11.glDisable(GL11.GL_BLEND);
			this.zLevel = 0.0F;
		}

		fontRendererObj.drawString(StatCollector.translateToLocal("pe.tomegui.page")+ ":" + Integer.toString(currentPage + 1) + "/" + Integer.toString(numPages + 1), (int) (xSize / 4.5D) - (fontRendererObj.getStringWidth(StatCollector.translateToLocal("pe.tomegui.page") + ":   ") / 2) + 70, 8, 4210752, false);

	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		PacketHandler.sendToServer(new TomeExDataSyncPKT(selected));
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		int id = button.id;
		switch (id) {
		case 1:
			if (currentPage > 0) currentPage--;
			break;
		case 2:
			if (currentPage < numPages) currentPage++;
			break;
		case 3:
			selected.clear();
			break;
		case 4:
			for (int i = 0; i < 104; i++)
			{
				int ix = 104 * currentPage + i;
				if (inv.getSizeInventory() > i)
				{
					ItemStack item = inv.getStackInSlot(i);
					if (item == null) continue;
				}
				boolean found = false;
				for (int j = 0; j < selected.size(); j++)
				{
					if (selected.get(j) == ix)
					{
						found = true;
					}
				}
				if (!found) selected.add(ix);
			}
			break;
		case 5:
			List<Integer> selC = new ArrayList<>();
			for (int i = 0; i < selected.size(); i++)
			{
				int ix = selected.get(i);
				if (ix / 104 != currentPage)
				{
					selC.add(ix);
				}
			}
			selected.clear();
			for (int i = 0; i < selC.size(); i++)
			{
				selected.add(selC.get(i));
			}

			break;
		}
		PacketHandler.sendToServer(new TomePageSyncPKT(currentPage));
	}

	@Override
	protected void mouseClicked(int x, int y, int mouseButton)
	{
		int Cx = x - xLocation;
		int Cy = y - yLocation;
		if (Cx < 11 || Cx > this.xSize - 12) Cx = 0;
		if (Cy < 21 || Cy > this.ySize - 30) Cy = 0;
		if (Cx != 0 && Cy != 0)
		{
			Cx -= 11;
			Cy -= 21;
			Cx = Math.floorDiv(Cx, 18);
			Cy = Math.floorDiv(Cy, 18);
			if (inv.getSizeInventory() > (13 * Cy) + Cx)
			{
				ItemStack item = inv.getStackInSlot((13 * Cy) + Cx);
				if (item == null) return;
			}

			int index = 104 * currentPage + 13 * Cy + Cx;
			boolean found = false;
			int fI = 0;
			for (int i = 0; i < selected.size(); i++)
			{
				if (index == selected.get(i))
				{
					found = true;
					fI = i;
				}
			}
			if (!found && mouseButton == 0)
			{
				selected.add(index);
			}
			else if (found && mouseButton == 1)
			{
				selected.remove(fI);
			}
		}

		super.mouseClicked(x, y, mouseButton);
	}

}
