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
import team.bytephoria.bytecompanionsbuffs.PaperPlugin;
import team.bytephoria.bytecompanionsbuffs.configuration.buff.MobLoot;
import team.bytephoria.bytecompanionsbuffs.manager.CooldownManager;
import team.bytephoria.bytecompanionsbuffs.util.BuffKey;

import java.util.List;

public final class MobLootListener implements Listener {

    private final PaperPlugin paperPlugin;
    public MobLootListener(final @NotNull PaperPlugin paperPlugin) {
        this.paperPlugin = paperPlugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeathEvent(final @NotNull EntityDeathEvent deathEvent) {
        final DamageSource damageSource = deathEvent.getDamageSource();
        if (!(damageSource.getCausingEntity() instanceof Player player)) {
            return;
        }

        final MobLoot peeked = (MobLoot) this.paperPlugin.buffService()
                .peek(player, BuffKey.MOB_LOOT)
                .orElse(null);

        if (peeked == null || !this.matchesApplyTo(peeked, deathEvent.getEntity())) {
            return;
        }

        final CooldownManager cooldownManager = this.paperPlugin.cooldownManager();
        if (!cooldownManager.tryUse(player, BuffKey.MOB_LOOT, peeked.cooldown())) {
            return;
        }

        final List<ItemStack> drops = deathEvent.getDrops();
        drops.replaceAll(item -> {
            final int newAmount = Math.max(1, peeked.operation().apply(item.getAmount(), peeked.value()));
            item.setAmount(newAmount);
            return item;
        });

        this.paperPlugin.notifyPlayer(player, peeked);
    }

    private boolean matchesApplyTo(final @NotNull MobLoot buff, final @NotNull Entity entity) {
        final boolean isHostile = entity instanceof Monster;
        return switch (buff.applyTo()) {
            case ALL -> true;
            case HOSTILE -> isHostile;
            case PASSIVE -> !isHostile;
        };
    }
}