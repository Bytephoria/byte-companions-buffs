package team.bytephoria.bytecompanionsbuffs.listener;

import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanions.api.access.ByteCompanions;
import team.bytephoria.bytecompanionsbuffs.util.BuffKey;
import team.bytephoria.bytecompanionsbuffs.PaperPlugin;
import team.bytephoria.bytecompanionsbuffs.configuration.Companions;
import team.bytephoria.bytecompanionsbuffs.configuration.buff.MobLoot;
import team.bytephoria.bytecompanionsbuffs.manager.CooldownManager;

import java.util.List;

public final class MobLootListener implements Listener {

    private final PaperPlugin paperPlugin;
    private final CooldownManager cooldownManager;

    public MobLootListener(
            final @NotNull PaperPlugin paperPlugin,
            final @NotNull CooldownManager cooldownManager
    ) {
        this.paperPlugin = paperPlugin;
        this.cooldownManager = cooldownManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeathEvent(final @NotNull EntityDeathEvent deathEvent) {
        final DamageSource damageSource = deathEvent.getDamageSource();
        if (!(damageSource.getCausingEntity() instanceof Player player)) {
            return;
        }

        ByteCompanions.getAPI().getCompanion(player.getUniqueId()).ifPresent(companion -> {
            final Companions companions = this.paperPlugin.companions(companion.type().id());
            if (companions == null) {
                return;
            }

            final MobLoot buffer = companions.vanilla().mobLoot();
            if (!buffer.enabled()) {
                return;
            }

            final Entity entity = deathEvent.getEntity();
            final boolean isHostile = entity instanceof Monster;

            final boolean matches = switch (buffer.applyTo()) {
                case ALL -> true;
                case HOSTILE -> isHostile;
                case PASSIVE -> !isHostile;
            };

            if (!matches || !this.cooldownManager.tryUse(player, BuffKey.MOB_LOOT, buffer.cooldown())) {
                return;
            }

            final List<ItemStack> drops = deathEvent.getDrops();
            drops.replaceAll(item -> {
                final int newAmount = Math.max(1, buffer.operation().apply(item.getAmount(), buffer.value()));
                item.setAmount(newAmount);
                return item;
            });

            this.paperPlugin.notifyPlayer(player, buffer);
        });
    }

}
