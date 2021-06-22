package gs.mclo.forge;

import com.mojang.brigadier.builder.ArgumentBuilder;
import gs.mclo.java.MclogsAPI;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;

import static net.minecraft.command.Commands.literal;

public class CommandMclogsList {
    static ArgumentBuilder<CommandSource, ?> register() {
        return literal("list")
            .requires(source -> source.hasPermissionLevel(2))
            .executes((context) -> {
                CommandSource source = context.getSource();

                try {
                    String[] logs = MclogsAPI.listLogs(source.getServer().getDataDirectory().getAbsolutePath());

                    if (logs.length == 0) {
                        source.sendFeedback(new StringTextComponent("No logs available!"), false);
                        return 0;
                    }

                    StringTextComponent feedback = new StringTextComponent("Available Logs:");
                    for (String log : logs) {
                        Style s = new Style();
                        s = s.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/mclogs share " + log));
                        StringTextComponent tempText = new StringTextComponent("\n" + log);
                        tempText.setStyle(s);
                        feedback.appendSibling(tempText);
                    }
                    source.sendFeedback(feedback, false);
                    return logs.length;
                }
                catch (Exception e) {
                    MclogsForgeLoader.logger.error("An error occurred when listing your logs.");
                    MclogsForgeLoader.logger.error(e);
                    StringTextComponent error = new StringTextComponent("An error occurred. Check your log for more details.");
                    source.sendErrorMessage(error);
                    return -1;
                }
            });
    }
}
