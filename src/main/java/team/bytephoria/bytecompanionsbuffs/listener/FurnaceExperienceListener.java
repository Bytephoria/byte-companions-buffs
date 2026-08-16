package team.bytephoria.bytecompanionsbuffs.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanionsbuffs.PaperPlugin;
import team.bytephoria.bytecompanionsbuffs.util.BuffKey;

public final class FurnaceExperienceListener implements Listener {

    private final PaperPlugin paperPlugin;
    public FurnaceExperienceListener(final @NotNull PaperPlugin paperPlugin) {
        this.paperPlugin = paperPlugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onFurnaceExtractEvent(final @NotNull FurnaceExtractEvent extractEvent) {
        final Player player = extractEvent.getPlayer();
        final int base = extractEvent.getExpToDrop();
        if (base <= 0) {
            return;
        }

        this.paperPlugin.buffService().resolve(player, BuffKey.EXPERIENCE_FURNACE)
                .ifPresent(buff -> {
                    extractEvent.setExpToDrop(buff.operation().apply(base, buff.value()));
                    this.paperPlugin.notifyPlayer(player, buff);
                });
    }

}
