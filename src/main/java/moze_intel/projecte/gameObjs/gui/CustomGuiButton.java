package moze_intel.projecte.gameObjs.gui;

import org.lwjgl.opengl.GL11;

import moze_intel.projecte.PECore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class CustomGuiButton extends GuiButton {
	
	protected static final ResourceLocation butTex = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/tome.png");
	
	private boolean type;

	public CustomGuiButton(int id, int x, int y, boolean type)
	{
		super(id, x, y, 20, 20, "");
		this.type = type;
	}

	
	 public void drawButton(Minecraft mine, int x, int y)
	    {
	        if (this.visible)
	        {
	            FontRenderer fontrenderer = mine.fontRenderer;
	            mine.getTextureManager().bindTexture(butTex);
	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	            this.field_146123_n = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
	            //int k = this.getHoverState(this.field_146123_n);
	            GL11.glEnable(GL11.GL_BLEND);
	            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	            this.drawTexturedModalRect(this.xPosition, this.yPosition, 26 + (this.field_146123_n ? 24 : 0), 202 + (this.type ? 24 : 0), this.width, this.height);
	            //this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
	            this.mouseDragged(mine, x, y);
	            //int l = 14737632;

	           /* if (packedFGColour != 0)
	            {
	                l = packedFGColour;
	            }
	            else if (!this.enabled)
	            {
	                l = 10526880;
	            }
	            else if (this.field_146123_n)
	            {
	                l = 16777120;
	            }*/

	            //this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
	        }
	    }

}
