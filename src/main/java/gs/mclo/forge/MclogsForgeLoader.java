package gs.mclo.forge;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import gs.mclo.mclogs.APIResponse;
import gs.mclo.mclogs.MclogsAPI;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod("mclogs")
public class MclogsForgeLoader {
    public static final Logger logger = LogManager.getLogger();
    public static String logsdir;

    public MclogsForgeLoader() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent event) {
        logsdir = event.getServer().getFile("logs/latest.log").getAbsolutePath();
        logsdir = logsdir.substring(0, logsdir.length() - 10);
        event.getCommandDispatcher().register(CommandMclogs.register());
        event.getCommandDispatcher().register(LiteralArgumentBuilder.<CommandSource>literal("mclogs")
            .then(CommandMclogsList.register())
            .then(CommandMclogsShare.register())
        );
    }

    public static int share(CommandSource source, String filename){
        try {
            APIResponse response = MclogsAPI.share(MclogsForgeLoader.logsdir + filename);

            if (response.success) {
                Style s = new Style();
                s = s.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,response.url));
                s = s.setColor(TextFormatting.GREEN);
                StringTextComponent feedback = new StringTextComponent("Click here for your log");
                feedback.setStyle(s);
                source.sendFeedback(feedback, true);
                return 1;
            } else {
                logger.error("An error occurred when uploading your log");
                logger.error(response.error);
                StringTextComponent error = new StringTextComponent("An error occurred. Check your log for more details");
                source.sendErrorMessage(error);
                return -1;
            }
        }
        catch (IOException e) {
            logger.error("An error occurred when reading your log");
            logger.error(e);
            StringTextComponent error = new StringTextComponent("An error occurred. Check your log for more details");
            source.sendErrorMessage(error);
            return -1;
        }
    }
}
