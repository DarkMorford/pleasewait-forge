package net.darkmorford.pleasewait;

import net.darkmorford.pleasewait.command.CommandMain;
import net.darkmorford.pleasewait.proxy.CommonProxy;
import net.darkmorford.pleasewait.task.TaskGetUserId;
import net.darkmorford.pleasewait.task.TaskRefresh;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod(
        modid = PleaseWait.MODID,
        name = PleaseWait.MODNAME,
        version = PleaseWait.VERSION,
        useMetadata = true,
        guiFactory = "net.darkmorford.pleasewait.gui.GuiConfigFactory",
        acceptableRemoteVersions = "*"
)
public class PleaseWait
{
    public static final String MODID = "pleasewait";
    public static final String MODNAME = "PleaseWait";
    public static final String VERSION = "1.10.2-0.0.1.5";

    @SidedProxy(clientSide = "net.darkmorford.pleasewait.proxy.ClientProxy", serverSide = "net.darkmorford.pleasewait.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Instance
    public static PleaseWait instance;

    public static Logger logger;

    // Custom creative tabs
    public static final CreativeTabs tabTwitchForge = new CreativeTabs("pleasewait") {
        @Override
        public Item getTabIconItem() {
            return Items.DIAMOND_SHOVEL;
        }
    };

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        // Sample things to do in this event handler:
        // Register blocks and items, register tileEntities, register entities, assign oreDict names

        logger = event.getModLog();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // Sample things to do in this event handler:
        // Register world generators, register recipes, register event handlers, send IMC messages

        proxy.init(event);

        PacketHandler.registerMessages();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        // Sample things to do in this event handler:
        // Mod compatibility, etc.

        proxy.postInit(event);
    }

    @EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event)
    {
        logger.log(Level.INFO, "Fetching Twitch channel ID");
        Thread t = new Thread(new TaskGetUserId());
        t.start();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        // Sample things to do in this event handler:
        // Register commands

        event.registerServerCommand(new CommandMain());

        // Schedule the refresh task to run every few minutes
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new TaskRefresh(), 1, Config.updateInterval, TimeUnit.MINUTES);
    }
}
