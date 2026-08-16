package team.bytephoria.bytecompanionsbuffs.listener;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanionsbuffs.PaperPlugin;
import team.bytephoria.bytecompanionsbuffs.util.BuffKey;

public final class BlockDropListener implements Listener {

    private final PaperPlugin paperPlugin;
    public BlockDropListener(final @NotNull PaperPlugin paperPlugin) {
        this.paperPlugin = paperPlugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockDropItemEvent(final @NotNull BlockDropItemEvent dropEvent) {
        final Player player = dropEvent.getPlayer();
        this.paperPlugin.buffService().resolve(player, BuffKey.BLOCK_DROPS)
                .ifPresent(buff -> {
                    for (final Item item : dropEvent.getItems()) {
                        final ItemStack stack = item.getItemStack();
                        final int newAmount = Math.max(1, buff.operation().apply(stack.getAmount(), buff.value()));
                        stack.setAmount(newAmount);
                        item.setItemStack(stack);
                    }

                    this.paperPlugin.notifyPlayer(player, buff);
                });

    }
}
