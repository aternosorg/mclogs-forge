package gs.mclo.forge;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public class CommandMclogsList {
    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("list")
            .requires(source -> source.hasPermission(2))
            .executes((context) -> {
                CommandSourceStack source = context.getSource();

                try {
                    int total = 0;
                    TextComponent message = new TextComponent("");

                    message.append(new TextComponent("Available logs:")
                        .setStyle(Style.EMPTY
                            .withColor(ChatFormatting.GREEN)
                            .withBold(true)
                        ));
                    for (String log : MclogsForgeLoader.getLogs(context)) {
                        Component tempText = new TextComponent("\n" + log)
                                .setStyle(Style.EMPTY
                                .withClickEvent(
                                        new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/mclogs share " + log))
                                );
                        message.append(tempText);
                        total++;
                    }

                    message.append(new TextComponent("\nAvailable crash reports:")
                            .setStyle(Style.EMPTY
                                    .withColor(ChatFormatting.GREEN)
                                    .withBold(true)
                            ));
                    for (String report : MclogsForgeLoader.getCrashReports(context)) {
                        Component tempText = new TextComponent("\n" + report)
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
                    TextComponent error = new TextComponent("An error occurred. Check your log for more details.");
                    source.sendFailure(error);
                    return -1;
                }
            });
    }
}
