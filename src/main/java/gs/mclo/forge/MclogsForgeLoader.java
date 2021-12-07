package gs.mclo.forge;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import gs.mclo.java.APIResponse;
import gs.mclo.java.MclogsAPI;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mod(MclogsForgeLoader.modid)
public class MclogsForgeLoader{
    public static final String modid = "mclogs";
    public static final Logger logger = LogManager.getLogger();
    public static String logsdir;

    public MclogsForgeLoader() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * @param context command context
     * @return log files
     * @throws IOException io exception
     */
    public static String[] getLogs(CommandContext<CommandSource> context) throws IOException {
        return MclogsAPI.listLogs(logsdir);
    }

    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent event) {
        MclogsAPI.mcversion = event.getServer().getMinecraftVersion();
        MclogsAPI.userAgent = "Mclogs-forge";
        MclogsAPI.version = ModList.get().getModContainerById(MclogsForgeLoader.modid).get().getModInfo().getVersion().toString();

        try {
            logsdir = event.getServer().getFile("logs").getCanonicalPath() + "/";
        } catch (IOException e) {
            logger.error("couldn't read logs directory");
            logger.error(e);
            return;
        }
        CommandDispatcher<CommandSource> dispatcher = event.getServer().getCommandManager().getDispatcher();
        dispatcher.register(CommandMclogs.register());
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("mclogs")
            .then(CommandMclogsList.register())
            .then(CommandMclogsShare.register())
        );
    }

    public static int share(CommandSource source, String filename){
        logger.info("Sharing " + filename);
        try {
            Path logs = Paths.get(logsdir);
            Path log = logs.resolve(filename);
            if (!log.getParent().equals(logs)) {
                throw new FileNotFoundException();
            }
            APIResponse response = MclogsAPI.share(log);

            if (response.success) {
                Style s = Style.EMPTY.setFormatting(TextFormatting.GREEN);
                StringTextComponent feedback = new StringTextComponent("Your log has been uploaded: ");
                feedback.setStyle(s);
                StringTextComponent link = new StringTextComponent(response.url);
                link.setStyle(Style.EMPTY.setFormatting(TextFormatting.BLUE).setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,response.url)));
                source.sendFeedback(feedback.append(link), true);
                return 1;
            } else {
<<<<<<< HEAD
                logger.error("An error occurred when uploading your log");
                logger.error(response.error);
=======
                logger.error("An error occurred when uploading your log: " + response.error);
>>>>>>> master
                StringTextComponent error = new StringTextComponent("An error occurred. Check your log for more details");
                source.sendErrorMessage(error);
                return -1;
            }
        }
        catch (FileNotFoundException|IllegalArgumentException e) {
            StringTextComponent error = new StringTextComponent("The log file "+filename+" doesn't exist. Use '/mclogs list' to list all logs.");
            source.sendErrorMessage(error);
            return -1;
        }
        catch (IOException e) {
            logger.error("An error occurred when reading your log.");
            logger.error(e);
            StringTextComponent error = new StringTextComponent("An error occurred. Check your log for more details.");
            source.sendErrorMessage(error);
            return -1;
        }
    }
}
