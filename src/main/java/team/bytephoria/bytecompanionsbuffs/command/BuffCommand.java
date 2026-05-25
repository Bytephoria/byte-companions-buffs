package team.bytephoria.bytecompanionsbuffs.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanionsbuffs.PaperPlugin;

import java.util.List;
import java.util.Locale;

public final class BuffCommand extends BukkitCommand {

    private final PaperPlugin paperPlugin;
    public BuffCommand(final @NotNull PaperPlugin paperPlugin) {
        super("byte-companions-effects", "Main command of plugin.", "/<command>", List.of("bcb"));
        this.paperPlugin = paperPlugin;
    }

    @Override
    public boolean execute(
            final @NotNull CommandSender commandSender,
            final @NotNull String commandLabel,
            final @NotNull String @NotNull [] arguments
    ) {
        if (arguments.length == 0) {
            commandSender.sendMessage(
                    Component.text("This plugin was made by")
                            .appendSpace()
                            .append(
                                    Component.text("@Bytephoria", TextColor.fromHexString("#dbfdcf"))
                                            .clickEvent(ClickEvent.openUrl("https://bytephoria.team"))
                            )
                            .appendSpace()
                            .append(Component.text("(Click here to see the github repository)", NamedTextColor.GRAY)
                                    .clickEvent(ClickEvent.openUrl("https://github.com/bytephoria")))
            );

            return true;
        }

        final String zeroArgument = arguments[0].toLowerCase(Locale.ROOT);
        if (zeroArgument.equals("reload")) {
            if (!commandSender.hasPermission("bytecompanionsbuffs.command.reload")) {
                commandSender.sendMessage(Component.text("You don't have permission to execute this command.", NamedTextColor.RED));
                return true;
            }

            this.paperPlugin.reload();
            commandSender.sendMessage(Component.text("The configuration was reloaded successfully.", NamedTextColor.GREEN));
        }

        return true;
    }
}