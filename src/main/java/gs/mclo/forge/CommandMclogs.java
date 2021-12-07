package gs.mclo.forge;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;

public class CommandMclogs{
    static LiteralArgumentBuilder<CommandSourceStack> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("mclogs")
                .requires(source -> source.hasPermission(2))
                .executes((context) -> MclogsForgeLoader.share(context.getSource(), "latest.log"));
    }
}
