package team.bytephoria.bytecompanionsbuffs.listener;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanionsbuffs.PaperPlugin;
import team.bytephoria.bytecompanionsbuffs.util.BuffKey;

public final class FishingListener implements Listener {

    private final PaperPlugin paperPlugin;
    public FishingListener(final @NotNull PaperPlugin paperPlugin) {
        this.paperPlugin = paperPlugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerFishEvent(final @NotNull PlayerFishEvent fishEvent) {
        if (fishEvent.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        final Player player = fishEvent.getPlayer();
        this.applyExperienceBuff(player, fishEvent);
        this.applyLootBuff(player, fishEvent);
    }

    private void applyExperienceBuff(final @NotNull Player player, final @NotNull PlayerFishEvent fishEvent) {
        final int base = fishEvent.getExpToDrop();
        if (base <= 0) {
            return;
        }

        this.paperPlugin.buffService().resolve(player, BuffKey.EXPERIENCE_FISHING).ifPresent(buff -> {
            fishEvent.setExpToDrop(buff.operation().apply(base, buff.value()));
            this.paperPlugin.notifyPlayer(player, buff);
        });
    }

    private void applyLootBuff(final @NotNull Player player, final @NotNull PlayerFishEvent fishEvent) {
        if (!(fishEvent.getCaught() instanceof Item caughtItem)) {
            return;
        }

        this.paperPlugin.buffService().resolve(player, BuffKey.FISHING_LOOT).ifPresent(buff -> {
            final ItemStack stack = caughtItem.getItemStack();
            stack.setAmount(Math.max(1, buff.operation().apply(stack.getAmount(), buff.value())));
            this.paperPlugin.notifyPlayer(player, buff);
        });
    }
}