package team.bytephoria.bytecompanionsbuffs.listener;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanions.api.access.ByteCompanions;
import team.bytephoria.bytecompanionsbuffs.util.BuffKey;
import team.bytephoria.bytecompanionsbuffs.PaperPlugin;
import team.bytephoria.bytecompanionsbuffs.configuration.Companions;
import team.bytephoria.bytecompanionsbuffs.configuration.buff.Buff;
import team.bytephoria.bytecompanionsbuffs.manager.CooldownManager;

public final class BlockDropListener implements Listener {

    private final PaperPlugin paperPlugin;
    private final CooldownManager cooldownManager;

    public BlockDropListener(
            final @NotNull PaperPlugin paperPlugin,
            final @NotNull CooldownManager cooldownManager
    ) {
        this.paperPlugin = paperPlugin;
        this.cooldownManager = cooldownManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockDropItemEvent(final @NotNull BlockDropItemEvent dropEvent) {
        final Player player = dropEvent.getPlayer();

        ByteCompanions.getAPI().getCompanion(player.getUniqueId()).ifPresent(companion -> {
            final ItemStack heldItem = player.getEquipment().getItem(player.getActiveItemHand());
            if (heldItem.containsEnchantment(Enchantment.SILK_TOUCH)) {
                return;
            }

            final Companions companions = this.paperPlugin.companions(companion.type().id());
            if (companions == null) {
                return;
            }

            final Buff buff = companions.vanilla().blockDrops();
            if (!buff.enabled()) {
                return;
            }

            if (!this.cooldownManager.tryUse(player, BuffKey.BLOCK_DROPS, buff.cooldown())) {
                return;
            }

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
