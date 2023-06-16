package gs.mclo.forge;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
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
                    int total = 0;
                    MutableComponent message = Component.literal("");

                    message.append(Component.literal("Available logs:")
                        .setStyle(Style.EMPTY
                            .withColor(ChatFormatting.GREEN)
                            .withBold(true)
                        ));
                    for (String log : MclogsForgeLoader.getLogs(context)) {
                        Component tempText = Component.literal("\n" + log)
                                .setStyle(Style.EMPTY
                                .withClickEvent(
                                        new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/mclogs share " + log))
                                );
                        message.append(tempText);
                        total++;
                    }

                    message.append(Component.literal("\nAvailable crash reports:")
                            .setStyle(Style.EMPTY
                                    .withColor(ChatFormatting.GREEN)
                                    .withBold(true)
                            ));
                    for (String report : MclogsForgeLoader.getCrashReports(context)) {
                        Component tempText = Component.literal("\n" + report)
                                .setStyle(Style.EMPTY
                                        .withClickEvent(
                                                new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/mclogs share " + report))
                                );
                        message.append(tempText);
                        total++;
                    }

                    source.sendSuccess(() -> message, false);
                    return total;
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
