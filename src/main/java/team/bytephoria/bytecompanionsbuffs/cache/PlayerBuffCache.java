package team.bytephoria.bytecompanionsbuffs.cache;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanionsbuffs.ResolvedBuff;
import team.bytephoria.bytecompanionsbuffs.util.BuffKey;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class PlayerBuffCache {

    private final Int2ObjectOpenHashMap<EnumMap<BuffKey, ResolvedBuff>> activeBuffs = new Int2ObjectOpenHashMap<>();

    public void refresh(final @NotNull Player player, final @NotNull Map<BuffKey, ResolvedBuff> buffs) {
        this.activeBuffs.put(player.getEntityId(), new EnumMap<>(buffs));
    }

    public @NotNull Optional<ResolvedBuff> get(final @NotNull Player player, final @NotNull BuffKey buffType) {
        final EnumMap<BuffKey, ResolvedBuff> active = this.activeBuffs.get(player.getEntityId());
        if (active == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(active.get(buffType));
    }

    public void clear(final @NotNull Player player) {
        this.activeBuffs.remove(player.getEntityId());
    }

    public void clear() {
        this.activeBuffs.clear();
    }

}
