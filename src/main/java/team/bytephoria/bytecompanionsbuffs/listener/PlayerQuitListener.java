package team.bytephoria.bytecompanionsbuffs.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanionsbuffs.manager.CooldownManager;

public final class PlayerQuitListener implements Listener {

    private final CooldownManager cooldownManager;
    public PlayerQuitListener(final @NotNull CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }

    @EventHandler
    public void onPlayerQuitEvent(final @NotNull PlayerQuitEvent quitEvent) {
        this.cooldownManager.remove(quitEvent.getPlayer().getEntityId());
    }

}
