package gs.mclo.forge;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.CommandSource;


import static net.minecraft.command.Commands.literal;

public class CommandMclogs{
    static LiteralArgumentBuilder<CommandSource> register() {
        return literal("mclogs")
                .requires(source -> source.hasPermission(2))
                .executes((context) -> MclogsForgeLoader.share(context.getSource(), "latest.log"));
    }
}
