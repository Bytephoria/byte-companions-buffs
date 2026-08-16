package team.bytephoria.bytecompanionsbuffs.listener;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanionsbuffs.PaperPlugin;
import team.bytephoria.bytecompanionsbuffs.util.BuffKey;

public final class BlockExperienceListener implements Listener {

    private final PaperPlugin paperPlugin;
    public BlockExperienceListener(final @NotNull PaperPlugin paperPlugin) {
        this.paperPlugin = paperPlugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreakEvent(final @NotNull BlockBreakEvent breakEvent) {
        final Player player = breakEvent.getPlayer();
        final int base = breakEvent.getExpToDrop();
        if (base <= 0) {
            return;
        }

        final ItemStack heldItem = player.getEquipment().getItem(player.getActiveItemHand());
        if (heldItem.containsEnchantment(Enchantment.SILK_TOUCH)) {
            return;
        }

        this.paperPlugin.buffService().resolve(player, BuffKey.EXPERIENCE_BLOCK)
                .ifPresent(buff -> {
                    final int experience = buff.operation().apply(base, buff.value());
                    breakEvent.setExpToDrop(experience);
                    this.paperPlugin.notifyPlayer(player, buff);
                });

    }

}
