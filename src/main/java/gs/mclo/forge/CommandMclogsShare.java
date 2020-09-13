package gs.mclo.forge;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandMclogsShare {
    public static void execute(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            TextComponentString error = new TextComponentString(MclogsCommandHandler.usage);
            error.setStyle(new Style().setColor(TextFormatting.RED));
            sender.sendMessage(error);
            return;
        }
        MclogsForgeLoader.share(sender, args[1]);
    }
}
