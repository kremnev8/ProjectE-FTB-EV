package moze_intel.projecte.integration;

import java.util.ArrayList;
import java.util.List;

import org.omg.PortableInterceptor.TRANSPORT_RETRY;

import cpw.mods.fml.common.Loader;
import li.cil.oc.common.tileentity.AccessPoint;
import li.cil.oc.common.tileentity.Adapter;
import li.cil.oc.common.tileentity.Capacitor;
import li.cil.oc.common.tileentity.Case;
import li.cil.oc.common.tileentity.DiskDrive;
import li.cil.oc.common.tileentity.Geolyzer;
import li.cil.oc.common.tileentity.Hologram;
import li.cil.oc.common.tileentity.Microcontroller;
import li.cil.oc.common.tileentity.MotionSensor;
import li.cil.oc.common.tileentity.NetSplitter;
import li.cil.oc.common.tileentity.Print;
import li.cil.oc.common.tileentity.Printer;
import li.cil.oc.common.tileentity.Raid;
import li.cil.oc.common.tileentity.Relay;
import li.cil.oc.common.tileentity.Robot;
import li.cil.oc.common.tileentity.Screen;
import li.cil.oc.common.tileentity.ServerRack;
import li.cil.oc.common.tileentity.Switch;
import li.cil.oc.common.tileentity.Transposer;
import moze_intel.projecte.gameObjs.items.TimeWatch;
import moze_intel.projecte.integration.MineTweaker.TweakInit;
import moze_intel.projecte.integration.NEI.NEIInit;
import moze_intel.projecte.utils.PELogger;

public class Integration
{
	// Single class to initiate different mod compatibilities. Idea came from Avaritia by SpitefulFox

	public static boolean mtweak = false;
	public static boolean NEI = false;
	public static boolean OC = false;

	public static void modChecks()
	{
		mtweak = Loader.isModLoaded("MineTweaker3");
		NEI = Loader.isModLoaded("NotEnoughItems");
		OC = Loader.isModLoaded("OpenComputers");
	}

	public static void init()
	{
		modChecks();
		
		if (OC)
		{
			try
			{
				List<Class> tiles = new ArrayList<>();
				
				tiles.add(Case.class);
				tiles.add(AccessPoint.class);
				tiles.add(Adapter.class);
				tiles.add(Capacitor.class);
				tiles.add(DiskDrive.class);
				tiles.add(Geolyzer.class);
				tiles.add(Hologram.class);
				tiles.add(Microcontroller.class);
				tiles.add(MotionSensor.class);
				tiles.add(NetSplitter.class);
				tiles.add(Printer.class);
				tiles.add(Raid.class);
				tiles.add(Relay.class);
				tiles.add(Robot.class);
				tiles.add(Screen.class);
				tiles.add(ServerRack.class);
				tiles.add(Switch.class);
				tiles.add(Transposer.class);
				
				for (int i = 0; i < tiles.size(); i++)
				{
					TimeWatch.blacklist(tiles.get(i));
				}
				
			}catch (Throwable e)
			{
				e.printStackTrace();
			}
		}

		if (mtweak)
		{
			try
			{
				TweakInit.init();
			} catch (Throwable e)
			{
				e.printStackTrace();
			}
		}

		if (NEI)
		{

			try
			{
				NEIInit.init();
			} catch (NoClassDefFoundError e)
			{
				PELogger.logWarn("NEI integration not loaded due to server side being detected");
			} catch (Throwable e)
			{
				e.printStackTrace();
			}
		}
	}
}
