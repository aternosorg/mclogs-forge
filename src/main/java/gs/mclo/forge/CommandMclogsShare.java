package gs.mclo.forge;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import static net.minecraft.command.Commands.literal;

public class CommandMclogsShare {
    static ArgumentBuilder<CommandSource, ?> register() {
        return literal("share")
            .requires(source -> source.hasPermissionLevel(2))
            .then(Commands.argument("filename", StringArgumentType.greedyString())
            .executes(context ->  MclogsForgeLoader.share(context.getSource(), context.getArgument("filename",String.class))));
    }
}
