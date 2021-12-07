package gs.mclo.forge;

import gs.mclo.java.APIResponse;
import gs.mclo.java.MclogsAPI;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mod(modid = MclogsForgeLoader.modid, name = MclogsForgeLoader.name, version = MclogsForgeLoader.version)
public class MclogsForgeLoader{
    public static final String modid = "mclogs";
    public static final String name = "Mclogs";
    public static final String version = "1.0.6";
    public static final Logger logger = LogManager.getLogger();
    public static String logsdir;

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        MclogsAPI.mcversion = event.getServer().getMinecraftVersion();
        MclogsAPI.userAgent = "Mclogs-forge";
        MclogsAPI.version = version;

        try {
            logsdir = event.getServer().getFile("logs").getCanonicalPath() + "/";
        } catch (IOException e) {
            logger.error("couldn't read logs directory");
            logger.error(e);
            return;
        }
        event.registerServerCommand(new MclogsCommandHandler());
    }

    public static int share(ICommandSender source, String filename){
        logger.info("Sharing " + filename);
        try {
            Path logs = Paths.get(logsdir);
            Path log = logs.resolve(filename);
            if (!log.getParent().equals(logs)) {
                throw new FileNotFoundException();
            }
            APIResponse response = MclogsAPI.share(log);

            if (response.success) {
                Style s = new Style().setColor(TextFormatting.GREEN);
                TextComponentString feedback = new TextComponentString("Your log has been uploaded: ");
                feedback.setStyle(s);
                TextComponentString link = new TextComponentString(response.url);
                link.setStyle(new Style().setColor(TextFormatting.BLUE).setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,response.url)));
                source.sendMessage(feedback.appendSibling(link));
                return 1;
            } else {
                logger.error("An error occurred when uploading your log: " + response.error);
                logger.error(response.error);
                TextComponentString error = new TextComponentString("An error occurred. Check your log for more details");
                error.setStyle(new Style().setColor(TextFormatting.RED));
                source.sendMessage(error);
                return -1;
            }
        }
        catch (FileNotFoundException e) {
            TextComponentString error = new TextComponentString("The log file "+filename+" doesn't exist. Use '/mclogs list' to list all logs.");
            error.setStyle(new Style().setColor(TextFormatting.RED));
            source.sendMessage(error);
            return -1;
        }
        catch (IOException e) {
            logger.error("An error occurred when reading your log.");
            logger.error(e);
            TextComponentString error = new TextComponentString("An error occurred. Check your log for more details.");
            error.setStyle(new Style().setColor(TextFormatting.RED));
            source.sendMessage(error);
            return -1;
        }
    }
}
