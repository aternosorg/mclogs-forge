package gs.mclo.forge;

import gs.mclo.mclogs.APIResponse;
import gs.mclo.mclogs.MclogsAPI;
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

@Mod(modid = "mclogs", name = "Mclogs", version = "1.0.3")
public class MclogsForgeLoader {
    public static final Logger logger = LogManager.getLogger();
    public static String logsdir;

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
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
        try {
            APIResponse response = MclogsAPI.share(MclogsForgeLoader.logsdir + filename);

            if (response.success) {
                Style s = new Style().setColor(TextFormatting.GREEN);
                TextComponentString feedback = new TextComponentString("Your log has been uploaded: ");
                feedback.setStyle(s);
                TextComponentString link = new TextComponentString(response.url);
                link.setStyle(new Style().setColor(TextFormatting.BLUE).setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,response.url)));
                source.sendMessage(feedback.appendSibling(link));
                return 1;
            } else {
                logger.error("An error occurred when uploading your log");
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
