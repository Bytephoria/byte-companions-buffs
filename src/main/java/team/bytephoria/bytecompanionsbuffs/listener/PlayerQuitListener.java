package team.bytephoria.bytecompanionsbuffs.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanionsbuffs.PaperPlugin;

public final class PlayerQuitListener implements Listener {

    private final PaperPlugin paperPlugin;
    public PlayerQuitListener(final @NotNull PaperPlugin paperPlugin) {
        this.paperPlugin = paperPlugin;
    }

    @EventHandler
    public void onPlayerQuitEvent(final @NotNull PlayerQuitEvent quitEvent) {
        this.paperPlugin.playerBuffCache().clear(quitEvent.getPlayer());
    }

}
