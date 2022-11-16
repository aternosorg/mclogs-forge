package gs.mclo.forge;

import com.mojang.brigadier.builder.ArgumentBuilder;
import gs.mclo.java.MclogsAPI;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

import static net.minecraft.command.Commands.literal;

public class CommandMclogsList {
    static ArgumentBuilder<CommandSource, ?> register() {
        return literal("list")
            .requires(source -> source.hasPermissionLevel(2))
            .executes((context) -> {
                CommandSource source = context.getSource();

                try {
                    int total = 0;
                    StringTextComponent message = new StringTextComponent("");

                    message.appendSibling(new StringTextComponent("Available logs:")
                        .setStyle(new Style()
                            .setColor(TextFormatting.GREEN)
                            .setBold(true)
                        ));
                    for (String log : MclogsForgeLoader.getLogs(context)) {
                        ITextComponent tempText = new StringTextComponent("\n" + log)
                                .setStyle(new Style()
                                .setClickEvent(
                                        new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/mclogs share " + log))
                                );
                        message.appendSibling(tempText);
                        total++;
                    }

                    message.appendSibling(new StringTextComponent("\nAvailable crash reports:")
                            .setStyle(new Style()
                                    .setColor(TextFormatting.GREEN)
                                    .setBold(true)
                            ));
                    for (String report : MclogsForgeLoader.getCrashReports(context)) {
                        ITextComponent tempText = new StringTextComponent("\n" + report)
                                .setStyle(new Style()
                                        .setClickEvent(
                                                new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/mclogs share " + report))
                                );
                        message.appendSibling(tempText);
                        total++;
                    }

                    source.sendFeedback(message, false);
                    return total;
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
