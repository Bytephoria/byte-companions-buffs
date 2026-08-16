package team.bytephoria.bytecompanionsbuffs.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanions.api.access.ByteCompanions;
import team.bytephoria.bytecompanions.api.bukkit.event.CompanionEquipEvent;
import team.bytephoria.bytecompanions.api.bukkit.event.CompanionPlayerLoadEvent;
import team.bytephoria.bytecompanions.api.bukkit.event.CompanionPlayerUnloadEvent;
import team.bytephoria.bytecompanions.api.bukkit.event.CompanionUnequipEvent;
import team.bytephoria.bytecompanions.api.companion.Companion;
import team.bytephoria.bytecompanionsbuffs.resolver.CompanionBuffResolver;
import team.bytephoria.bytecompanionsbuffs.cache.PlayerBuffCache;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class BuffCacheListener implements Listener {

    private final PlayerBuffCache buffCache;
    private final CompanionBuffResolver resolver;

    public BuffCacheListener(
            final @NotNull PlayerBuffCache buffCache,
            final @NotNull CompanionBuffResolver resolver
    ) {
        this.buffCache = buffCache;
        this.resolver = resolver;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCompanionEquipEvent(final @NotNull CompanionEquipEvent equipEvent) {
        final Player player = equipEvent.player();

        final Set<String> equipped = new HashSet<>(this.equippedTypeIdsOf(player));
        equipped.add(equipEvent.companionType().id());

        this.refresh(player, equipped);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCompanionUnequipEvent(final @NotNull CompanionUnequipEvent unequipEvent) {
        final Player player = unequipEvent.player();

        final Companion companion = unequipEvent.companion();
        final Set<String> equipped = new HashSet<>(this.equippedTypeIdsOf(player));
        equipped.remove(companion.type().id());

        this.refresh(player, equipped);
    }

    @EventHandler
    public void onCompanionPlayerLoadEvent(final @NotNull CompanionPlayerLoadEvent loadEvent) {
        final Player player = loadEvent.player();
        this.refresh(player, this.equippedTypeIdsOf(player));
    }

    @EventHandler
    public void onCompanionPlayerUnloadEvent(final @NotNull CompanionPlayerUnloadEvent unloadEvent) {
        this.buffCache.clear(unloadEvent.player());
    }

    private void refresh(final @NotNull Player player, final @NotNull Set<String> equippedTypeIds) {
        this.buffCache.refresh(player, this.resolver.resolve(equippedTypeIds));
    }

    private @NotNull Set<String> equippedTypeIdsOf(final @NotNull Player player) {
        return ByteCompanions.getAPI().getCompanionPlayer(player)
                .map(companionPlayer -> companionPlayer.companions().keySet())
                .orElse(Collections.emptySet());
    }

}