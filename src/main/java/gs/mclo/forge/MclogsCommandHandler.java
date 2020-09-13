package gs.mclo.forge;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class MclogsCommandHandler extends CommandBase {
    public static final String usage = "mclogs <list|share> <filename.log>";

    @Override
    public String getName() {
        return "mclogs";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return usage;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0)
            CommandMclogs.execute(sender);
        else if (args[0].equals("list"))
            CommandMclogsList.execute(server, sender, args);
        else if (args[0].equals("share"))
            CommandMclogsShare.execute(sender, args);
        else {
            TextComponentString error = new TextComponentString(getUsage(sender));
            error.setStyle(new Style().setColor(TextFormatting.RED));
            sender.sendMessage(error);
        }
    }
}
