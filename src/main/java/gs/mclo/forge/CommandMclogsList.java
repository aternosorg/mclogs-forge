package gs.mclo.forge;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import gs.mclo.java.MclogsAPI;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class CommandMclogsList {
    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("list")
            .requires(source -> source.hasPermission(2))
            .executes((context) -> {
                CommandSourceStack source = context.getSource();

                try {
                    String[] logs = MclogsAPI.listLogs(source.getServer().getServerDirectory().getAbsolutePath());

                    if (logs.length == 0) {
                        source.sendSuccess(Component.literal("No logs available!"), false);
                        return 0;
                    }

                    MutableComponent feedback = Component.literal("Available Logs:");
                    for (String log : logs) {
                        Style s = Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/mclogs share " + log));
                        MutableComponent tempText = Component.literal("\n" + log);
                        tempText.setStyle(s);
                        feedback.append(tempText);
                    }
                    source.sendSuccess(feedback, false);
                    return logs.length;
                }
                catch (Exception e) {
                    MclogsForgeLoader.logger.error("An error occurred when listing your logs.");
                    MclogsForgeLoader.logger.error(e);
                    source.sendFailure(Component.literal("An error occurred. Check your log for more details."));
                    return -1;
                }
            });
    }
}
