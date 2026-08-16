package team.bytephoria.bytecompanionsbuffs.listener;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanionsbuffs.PaperPlugin;
import team.bytephoria.bytecompanionsbuffs.util.BuffKey;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class ExperienceListener implements Listener {

    private static final Map<ExperienceOrb.SpawnReason, BuffKey> REASON_TO_SPECIFIC_TYPE = new EnumMap<>(ExperienceOrb.SpawnReason.class) {{
        put(ExperienceOrb.SpawnReason.BLOCK_BREAK, BuffKey.EXPERIENCE_BLOCK);
        put(ExperienceOrb.SpawnReason.FURNACE, BuffKey.EXPERIENCE_FURNACE);
        put(ExperienceOrb.SpawnReason.FISHING, BuffKey.EXPERIENCE_FISHING);
        put(ExperienceOrb.SpawnReason.ENTITY_DEATH, BuffKey.EXPERIENCE_MOB);
    }};

    private static final Set<ExperienceOrb.SpawnReason> EXCLUDED_REASONS = EnumSet.of(
            ExperienceOrb.SpawnReason.PLAYER_DEATH,
            ExperienceOrb.SpawnReason.EXP_BOTTLE
    );

    private final PaperPlugin paperPlugin;
    public ExperienceListener(final @NotNull PaperPlugin paperPlugin) {
        this.paperPlugin = paperPlugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickupExperienceEvent(final @NotNull PlayerPickupExperienceEvent experienceEvent) {
        final Player player = experienceEvent.getPlayer();
        final ExperienceOrb experienceOrb = experienceEvent.getExperienceOrb();
        final ExperienceOrb.SpawnReason spawnReason = experienceOrb.getSpawnReason();

        if (EXCLUDED_REASONS.contains(spawnReason)) {
            return;
        }

        final BuffKey specificKey = REASON_TO_SPECIFIC_TYPE.get(spawnReason);
        if (specificKey != null && this.paperPlugin.buffService().isActive(player, specificKey)) {
            return;
        }

        this.paperPlugin.buffService().resolve(player, BuffKey.EXPERIENCE)
                .ifPresent(buff -> {
                    final int base = experienceOrb.getExperience();
                    experienceOrb.setExperience(buff.operation().apply(base, buff.value()));
                    this.paperPlugin.notifyPlayer(player, buff);
                });
    }

}
