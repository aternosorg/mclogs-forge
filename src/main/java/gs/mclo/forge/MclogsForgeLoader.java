package gs.mclo.forge;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import gs.mclo.java.APIResponse;
import gs.mclo.java.MclogsAPI;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
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
     * @return log files
     * @throws IOException io exception
     */
    public static String[] getLogs() throws IOException {
        return MclogsAPI.listLogs(logsdir);
    }

    @SubscribeEvent
    public void serverStarting(ServerStartingEvent event) {
        MclogsAPI.mcversion = event.getServer().getServerVersion();
        MclogsAPI.userAgent = "Mclogs-forge";
        MclogsAPI.version = ModList.get().getModContainerById(MclogsForgeLoader.modid).get().getModInfo().getVersion().toString();

        try {
            logsdir = event.getServer().getFile("logs").getCanonicalPath() + "/";
        } catch (IOException e) {
            logger.error("couldn't read logs directory");
            logger.error(e);
            return;
        }
        CommandDispatcher<CommandSourceStack> dispatcher = event.getServer().getCommands().getDispatcher();
        dispatcher.register(CommandMclogs.register());
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("mclogs")
            .then(CommandMclogsList.register())
            .then(CommandMclogsShare.register())
        );
    }

    public static int share(CommandSourceStack source, String filename){
        logger.info("Sharing " + filename);
        try {
            Path logs = Paths.get(logsdir);
            Path log = logs.resolve(filename);
            if (!log.getParent().equals(logs)) {
                throw new FileNotFoundException();
            }
            APIResponse response = MclogsAPI.share(log);

            if (response.success) {
                Style s = Style.EMPTY.withColor(ChatFormatting.GREEN);
                TextComponent feedback = new TextComponent("Your log has been uploaded: ");
                feedback.setStyle(s);
                TextComponent link = new TextComponent(response.url);
                link.setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,response.url)));
                source.sendSuccess(feedback.append(link), true);
                return 1;
            } else {
                logger.error("An error occurred when uploading your log: " + response.error);
                TextComponent error = new TextComponent("An error occurred. Check your log for more details");
                source.sendFailure(error);
                return -1;
            }
        }
        catch (FileNotFoundException|IllegalArgumentException e) {
            TextComponent error = new TextComponent("The log file "+filename+" doesn't exist. Use '/mclogs list' to list all logs.");
            source.sendFailure(error);
            return -1;
        }
        catch (IOException e) {
            logger.error("An error occurred when reading your log.");
            logger.error(e);
            TextComponent error = new TextComponent("An error occurred. Check your log for more details.");
            source.sendFailure(error);
            return -1;
        }
    }
}
