package team.bytephoria.bytecompanionsbuffs.listener;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanions.api.access.ByteCompanions;
import team.bytephoria.bytecompanionsbuffs.util.BuffKey;
import team.bytephoria.bytecompanionsbuffs.PaperPlugin;
import team.bytephoria.bytecompanionsbuffs.configuration.Companions;
import team.bytephoria.bytecompanionsbuffs.configuration.buff.Buff;
import team.bytephoria.bytecompanionsbuffs.manager.CooldownManager;

public final class BlockExperienceListener implements Listener {

    private final PaperPlugin paperPlugin;
    private final CooldownManager cooldownManager;

    public BlockExperienceListener(
            final @NotNull PaperPlugin paperPlugin,
            final @NotNull CooldownManager cooldownManager
    ) {
        this.paperPlugin = paperPlugin;
        this.cooldownManager = cooldownManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreakEvent(final @NotNull BlockBreakEvent breakEvent) {
        final Player player = breakEvent.getPlayer();

        ByteCompanions.getAPI().getCompanion(player.getUniqueId()).ifPresent(companion -> {
            final ItemStack heldItem = player.getEquipment().getItem(player.getActiveItemHand());
            if (heldItem.containsEnchantment(Enchantment.SILK_TOUCH)) {
                return;
            }

            final Companions companions = this.paperPlugin.companions(companion.type().id());
            if (companions == null) {
                return;
            }

            final Buff buff = companions.vanilla().experienceBlock();
            if (!buff.enabled()) {
                return;
            }

            final int base = breakEvent.getExpToDrop();
            if (base <= 0) {
                return;
            }

            if (!this.cooldownManager.tryUse(player, BuffKey.EXPERIENCE_BLOCK, buff.cooldown())) {
                return;
            }

            breakEvent.setExpToDrop(buff.operation().apply(base, buff.value()));
            this.paperPlugin.notifyPlayer(player, buff);
        });
    }


}
