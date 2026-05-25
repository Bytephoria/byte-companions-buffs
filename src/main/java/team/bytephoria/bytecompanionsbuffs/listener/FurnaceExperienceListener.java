package team.bytephoria.bytecompanionsbuffs.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanions.api.access.ByteCompanions;
import team.bytephoria.bytecompanionsbuffs.util.BuffKey;
import team.bytephoria.bytecompanionsbuffs.PaperPlugin;
import team.bytephoria.bytecompanionsbuffs.configuration.Companions;
import team.bytephoria.bytecompanionsbuffs.configuration.buff.Buff;
import team.bytephoria.bytecompanionsbuffs.manager.CooldownManager;

public final class FurnaceExperienceListener implements Listener {

    private final PaperPlugin paperPlugin;
    private final CooldownManager cooldownManager;

    public FurnaceExperienceListener(
            final @NotNull PaperPlugin paperPlugin,
            final @NotNull CooldownManager cooldownManager
    ) {
        this.paperPlugin = paperPlugin;
        this.cooldownManager = cooldownManager;
    }


    @EventHandler(ignoreCancelled = true)
    public void onFurnaceExtractEvent(final @NotNull FurnaceExtractEvent extractEvent) {
        final Player player = extractEvent.getPlayer();

        ByteCompanions.getAPI().getCompanion(player.getUniqueId()).ifPresent(companion -> {
            final Companions companions = this.paperPlugin.companions(companion.type().id());
            if (companions == null) {
                return;
            }

            final Buff buff = companions.vanilla().experienceFurnace();
            if (!buff.enabled()) {
                return;
            }

            final int base = extractEvent.getExpToDrop();
            if (base <= 0) {
                return;
            }

            if (!this.cooldownManager.tryUse(player, BuffKey.EXPERIENCE_FURNACE, buff.cooldown())) {
                return;
            }

            extractEvent.setExpToDrop(buff.operation().apply(base, buff.value()));
            this.paperPlugin.notifyPlayer(player, buff);
        });
    }

}
