package team.bytephoria.bytecompanionsbuffs.listener;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanions.api.access.ByteCompanions;
import team.bytephoria.bytecompanionsbuffs.util.BuffKey;
import team.bytephoria.bytecompanionsbuffs.PaperPlugin;
import team.bytephoria.bytecompanionsbuffs.configuration.Companions;
import team.bytephoria.bytecompanionsbuffs.configuration.Vanilla;
import team.bytephoria.bytecompanionsbuffs.configuration.buff.Buff;
import team.bytephoria.bytecompanionsbuffs.manager.CooldownManager;

public final class FishingListener implements Listener {

    private final PaperPlugin paperPlugin;
    private final CooldownManager cooldownManager;

    public FishingListener(
            final @NotNull PaperPlugin paperPlugin,
            final @NotNull CooldownManager cooldownManager
    ) {
        this.paperPlugin = paperPlugin;
        this.cooldownManager = cooldownManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerFishEvent(final @NotNull PlayerFishEvent fishEvent) {
        if (fishEvent.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        final Player player = fishEvent.getPlayer();

        ByteCompanions.getAPI().getCompanion(player.getUniqueId()).ifPresent(companion -> {
            final Companions companions = this.paperPlugin.companions(companion.type().id());
            if (companions == null) {
                return;
            }

            final Vanilla vanilla = companions.vanilla();
            final Buff expBuff = vanilla.experienceFishing();

            if (expBuff.enabled()) {
                final int base = fishEvent.getExpToDrop();
                if (base > 0 && this.cooldownManager.tryUse(player, BuffKey.EXPERIENCE_FISHING, expBuff.cooldown())) {
                    fishEvent.setExpToDrop(expBuff.operation().apply(base, expBuff.value()));
                    this.paperPlugin.notifyPlayer(player, expBuff);
                }
            }

            final Buff lootBuff = vanilla.fishingLoot();
            if (lootBuff.enabled() && fishEvent.getCaught() instanceof Item caughtItem) {
                if (this.cooldownManager.tryUse(player, BuffKey.FISHING_LOOT, lootBuff.cooldown())) {
                    final ItemStack stack = caughtItem.getItemStack();
                    final int newAmount = Math.max(1, lootBuff.operation().apply(stack.getAmount(), lootBuff.value()));
                    stack.setAmount(newAmount);
                    this.paperPlugin.notifyPlayer(player, lootBuff);
                }
            }
        });
    }

}
