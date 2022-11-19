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
            .requires(source -> source.hasPermission(2))
            .executes((context) -> {
                CommandSource source = context.getSource();

                try {
                    int total = 0;
                    StringTextComponent message = new StringTextComponent("");

                    message.append(new StringTextComponent("Available logs:")
                        .setStyle(Style.EMPTY
                            .withColor(TextFormatting.GREEN)
                            .withBold(true)
                        ));
                    for (String log : MclogsForgeLoader.getLogs(context)) {
                        ITextComponent tempText = new StringTextComponent("\n" + log)
                                .setStyle(Style.EMPTY
                                .withClickEvent(
                                        new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/mclogs share " + log))
                                );
                        message.append(tempText);
                        total++;
                    }

                    message.append(new StringTextComponent("\nAvailable crash reports:")
                            .setStyle(Style.EMPTY
                                    .withColor(TextFormatting.GREEN)
                                    .withBold(true)
                            ));
                    for (String report : MclogsForgeLoader.getCrashReports(context)) {
                        ITextComponent tempText = new StringTextComponent("\n" + report)
                                .setStyle(Style.EMPTY
                                        .withClickEvent(
                                                new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/mclogs share " + report))
                                );
                        message.append(tempText);
                        total++;
                    }

                    source.sendSuccess(message, false);
                    return total;
                }
                catch (Exception e) {
                    MclogsForgeLoader.logger.error("An error occurred when listing your logs.");
                    MclogsForgeLoader.logger.error(e);
                    StringTextComponent error = new StringTextComponent("An error occurred. Check your log for more details.");
                    source.sendFailure(error);
                    return -1;
                }
            });
    }
}
