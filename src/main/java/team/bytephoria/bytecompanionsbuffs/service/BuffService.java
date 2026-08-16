package team.bytephoria.bytecompanionsbuffs.service;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanionsbuffs.ResolvedBuff;
import team.bytephoria.bytecompanionsbuffs.cache.PlayerBuffCache;
import team.bytephoria.bytecompanionsbuffs.configuration.buff.Buff;
import team.bytephoria.bytecompanionsbuffs.manager.CooldownManager;
import team.bytephoria.bytecompanionsbuffs.util.BuffKey;

import java.util.Optional;

public final class BuffService {

    private final PlayerBuffCache playerBuffCache;
    private final CooldownManager cooldownManager;

    public BuffService(
            final @NotNull PlayerBuffCache playerBuffCache,
            final @NotNull CooldownManager cooldownManager
    ) {
        this.playerBuffCache = playerBuffCache;
        this.cooldownManager = cooldownManager;
    }

    public @NotNull Optional<Buff> peek(final @NotNull Player player, final @NotNull BuffKey buffKey) {
        return this.playerBuffCache.get(player, buffKey).map(ResolvedBuff::buff);
    }

    public boolean isActive(final @NotNull Player player, final @NotNull BuffKey buffKey) {
        return this.playerBuffCache.get(player, buffKey).isPresent();
    }

    public @NotNull Optional<Buff> resolve(
            final @NotNull Player player,
            final @NotNull BuffKey buffType
    ) {
        final ResolvedBuff resolvedBuff = this.playerBuffCache
                .get(player, buffType)
                .orElse(null);

        if (resolvedBuff == null) {
            return Optional.empty();
        }

        if (!this.cooldownManager.tryUse(player, buffType, resolvedBuff.buff().cooldown())) {
            return Optional.empty();
        }

        return Optional.of(resolvedBuff.buff());
    }

}
