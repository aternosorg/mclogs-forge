package gs.mclo.forge;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import gs.mclo.java.APIResponse;
import gs.mclo.java.MclogsAPI;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
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
                MutableComponent feedback = Component.literal("Your log has been uploaded: ").copy();
                feedback.setStyle(s);
                MutableComponent link = Component.literal(response.url);
                link.setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,response.url)));
                source.sendSuccess(feedback.append(link), true);
                return 1;
            } else {
                logger.error("An error occurred when uploading your log: " + response.error);
                source.sendFailure(Component.literal("An error occurred. Check your log for more details"));
                return -1;
            }
        }
        catch (FileNotFoundException|IllegalArgumentException e) {
            source.sendFailure(Component.literal("The log file "+filename+" doesn't exist. Use '/mclogs list' to list all logs."));
            return -1;
        }
        catch (IOException e) {
            logger.error("An error occurred when reading your log.");
            logger.error(e);;
            source.sendFailure(Component.literal("An error occurred. Check your log for more details."));
            return -1;
        }
    }
}
