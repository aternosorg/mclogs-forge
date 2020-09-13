package gs.mclo.forge;

import net.minecraft.command.ICommandSender;

public class CommandMclogs {
    public static void execute(ICommandSender sender) {
        MclogsForgeLoader.share(sender, "latest.log");
    }
}