package gs.mclo.forge;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import gs.mclo.api.Log;
import gs.mclo.api.MclogsClient;
import gs.mclo.api.response.UploadLogResponse;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Mod(MclogsForgeLoader.modid)
public class MclogsForgeLoader{
    public static final String modid = "mclogs";
    public static final Logger logger = LogManager.getLogger();

    private static final MclogsClient client = new MclogsClient("Mclogs-forge");

    public MclogsForgeLoader() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * @return log files
     * @throws IOException io exception
     */
    public static String[] getLogs(CommandContext<CommandSourceStack> context) throws IOException {
        return client.listLogsInDirectory(context.getSource().getServer().getServerDirectory().getAbsolutePath());
    }

    /**
     * @param context command context
     * @return crash reports
     * @throws IOException io exception
     */
    public static String[] getCrashReports(CommandContext<CommandSourceStack> context) throws IOException {
        return client.listCrashReportsInDirectory(context.getSource().getServer().getServerDirectory().getAbsolutePath());
    }
    @SubscribeEvent
    public void serverStarting(ServerStartingEvent event) {
        client.setMinecraftVersion(event.getServer().getServerVersion());
        Optional<? extends ModContainer> modcontainer = ModList.get().getModContainerById(MclogsForgeLoader.modid);
        modcontainer.ifPresent(modContainer -> client.setProjectVersion(modContainer.getModInfo().getVersion().toString()));
        CommandDispatcher<CommandSourceStack> dispatcher = event.getServer().getCommands().getDispatcher();
        dispatcher.register(CommandMclogs.register());
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("mclogs")
            .then(CommandMclogsList.register())
            .then(CommandMclogsShare.register())
        );
    }

    public static int share(CommandSourceStack source, String filename){
        logger.info("Sharing " + filename);
        source.sendSuccess(new TextComponent("Sharing " + filename), false);

        Path directory = source.getServer().getServerDirectory().toPath();
        Path logs = directory.resolve("logs");
        Path crashReports = directory.resolve("crash-reports");
        Path log = directory.resolve("logs").resolve(filename);

        if (!log.toFile().exists()) {
            log = directory.resolve("crash-reports").resolve(filename);
        }

        boolean isInAllowedDirectory = false;
        try {
            Path logPath = log.toRealPath();
            isInAllowedDirectory = (logs.toFile().exists() && logPath.startsWith(logs.toRealPath()))
                    || (crashReports.toFile().exists() && logPath.startsWith(crashReports.toRealPath()));
        }
        catch (IOException ignored) {}

        if (!log.toFile().exists() || !isInAllowedDirectory
                || !log.getFileName().toString().matches(Log.ALLOWED_FILE_NAME_PATTERN.pattern())) {
            source.sendFailure(new TextComponent("There is no log or crash report with the name '"
                    + filename + "'. Use '/mclogs list' to list all logs."));
            return -1;
        }

        try {
            UploadLogResponse response = client.uploadLog(log).get();
            response.setClient(client);

            if (response.isSuccess()) {
                Style s = Style.EMPTY.withColor(ChatFormatting.GREEN);
                TextComponent feedback = new TextComponent("Your log has been uploaded: ");
                feedback.setStyle(s);
                TextComponent link = new TextComponent(response.getUrl());
                link.setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,response.getUrl())));
                source.sendSuccess(feedback.append(link), true);
                return 1;
            } else {
                logger.error("An error occurred when uploading your log: " + response.getError());
                TextComponent error = new TextComponent("An error occurred. Check your log for more details");
                source.sendFailure(error);
                return -1;
            }
        }
        catch (IOException | ExecutionException | InterruptedException e) {
            logger.error("An error occurred when reading your log.");
            logger.error(e);
            TextComponent error = new TextComponent("An error occurred. Check your log for more details.");
            source.sendFailure(error);
            return -1;
        }
    }
}
