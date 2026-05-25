package team.bytephoria.bytecompanionsbuffs.listener;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanions.api.access.ByteCompanions;
import team.bytephoria.bytecompanionsbuffs.util.BuffKey;
import team.bytephoria.bytecompanionsbuffs.PaperPlugin;
import team.bytephoria.bytecompanionsbuffs.configuration.Companions;
import team.bytephoria.bytecompanionsbuffs.configuration.Vanilla;
import team.bytephoria.bytecompanionsbuffs.configuration.buff.Buff;
import team.bytephoria.bytecompanionsbuffs.manager.CooldownManager;

public final class ExperienceListener implements Listener {

    private final PaperPlugin paperPlugin;
    private final CooldownManager cooldownManager;

    public ExperienceListener(
            final @NotNull PaperPlugin paperPlugin,
            final @NotNull CooldownManager cooldownManager
    ) {
        this.paperPlugin = paperPlugin;
        this.cooldownManager = cooldownManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickupExperienceEvent(final @NotNull PlayerPickupExperienceEvent experienceEvent) {
        final Player player = experienceEvent.getPlayer();

        ByteCompanions.getAPI().getCompanion(player.getUniqueId()).ifPresent(companion -> {
            final Companions companions = this.paperPlugin.companions(companion.type().id());
            if (companions == null) {
                return;
            }

            final Vanilla vanilla = companions.vanilla();
            final Buff buff = vanilla.experience();

            if (!buff.enabled()) {
                return;
            }

            final boolean anySpecificEnabled =
                    vanilla.experienceMob().enabled() ||
                            vanilla.experienceBlock().enabled() ||
                            vanilla.experienceFurnace().enabled() ||
                            vanilla.experienceFishing().enabled();

            if (anySpecificEnabled) {
                return;
            }

            if (!this.cooldownManager.tryUse(player, BuffKey.EXPERIENCE, buff.cooldown())) {
                return;
            }

            final int base = experienceEvent.getExperienceOrb().getExperience();
            experienceEvent.getExperienceOrb().setExperience(buff.operation().apply(base, buff.value()));
            this.paperPlugin.notifyPlayer(player, buff);
        });
    }

}
