package gs.mclo.forge;

import gs.mclo.java.MclogsAPI;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

public class CommandMclogsList {

    public static void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        try {
            String[] logs = MclogsAPI.listLogs(server.getDataDirectory().getAbsolutePath());

            if (logs.length == 0) {
                sender.sendMessage(new TextComponentString("No logs available!"));
                return;
            }

            TextComponentString feedback = new TextComponentString("Available Logs:");
            for (String log : logs) {
                Style s = new Style();
                s = s.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/mclogs share " + log));
                TextComponentString tempText = new TextComponentString("\n" + log);
                tempText.setStyle(s);
                feedback.appendSibling(tempText);
            }
            sender.sendMessage(feedback);
        }
        catch (Exception e) {
            MclogsForgeLoader.logger.error("An error occurred when listing your logs.");
            MclogsForgeLoader.logger.error(e);
            TextComponentString error = new TextComponentString("An error occurred. Check your log for more details.");
            error.setStyle(new Style().setColor(TextFormatting.RED));
            sender.sendMessage(error);
        }
    }
}
