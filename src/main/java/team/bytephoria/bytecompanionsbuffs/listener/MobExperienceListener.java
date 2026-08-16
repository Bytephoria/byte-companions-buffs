package team.bytephoria.bytecompanionsbuffs.listener;

import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanionsbuffs.PaperPlugin;
import team.bytephoria.bytecompanionsbuffs.util.BuffKey;

public final class MobExperienceListener implements Listener {

    private final PaperPlugin paperPlugin;
    public MobExperienceListener(final @NotNull PaperPlugin paperPlugin) {
        this.paperPlugin = paperPlugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeathExpEvent(final @NotNull EntityDeathEvent deathEvent) {
        final DamageSource damageSource = deathEvent.getDamageSource();
        if (!(damageSource.getCausingEntity() instanceof Player player)) {
            return;
        }

        final int base = deathEvent.getDroppedExp();
        if (base <= 0) {
            return;
        }

        this.paperPlugin.buffService().resolve(player, BuffKey.EXPERIENCE_MOB)
                .ifPresent(buff -> {
                    deathEvent.setDroppedExp(buff.operation().apply(base, buff.value()));
                    this.paperPlugin.notifyPlayer(player, buff);
                });

    }

}
