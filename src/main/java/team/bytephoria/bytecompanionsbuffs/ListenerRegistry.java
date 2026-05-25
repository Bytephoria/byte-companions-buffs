package team.bytephoria.bytecompanionsbuffs;

import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanionsbuffs.configuration.Companions;
import team.bytephoria.bytecompanionsbuffs.configuration.Configuration;
import team.bytephoria.bytecompanionsbuffs.configuration.Vanilla;
import team.bytephoria.bytecompanionsbuffs.listener.*;
import team.bytephoria.bytecompanionsbuffs.manager.CooldownManager;

import java.util.Collection;
import java.util.function.Function;

public final class ListenerRegistry {

    private final PaperPlugin paperPlugin;
    private final Configuration configuration;
    private final CooldownManager cooldownManager;

    public ListenerRegistry(
            final @NotNull PaperPlugin paperPlugin,
            final @NotNull Configuration configuration,
            final @NotNull CooldownManager cooldownManager
    ) {
        this.paperPlugin = paperPlugin;
        this.configuration = configuration;
        this.cooldownManager = cooldownManager;
    }

    public void registerAll() {
        final PluginManager pluginManager = this.paperPlugin.getServer().getPluginManager();
        final Collection<Companions> companions = this.configuration.companions().values();

        // Always registered — handles cooldown cleanup on disconnect.
        pluginManager.registerEvents(new PlayerQuitListener(this.cooldownManager), this.paperPlugin);

        if (this.anyEnabled(companions, vanilla -> vanilla.experience().enabled())) {
            pluginManager.registerEvents(new ExperienceListener(this.paperPlugin, this.cooldownManager), this.paperPlugin);
        }

        if (this.anyEnabled(companions, vanilla -> vanilla.experienceMob().enabled())) {
            pluginManager.registerEvents(new MobExperienceListener(this.paperPlugin, this.cooldownManager), this.paperPlugin);
        }

        if (this.anyEnabled(companions, vanilla -> vanilla.mobLoot().enabled())) {
            pluginManager.registerEvents(new MobLootListener(this.paperPlugin, this.cooldownManager), this.paperPlugin);
        }

        if (this.anyEnabled(companions, vanilla -> vanilla.experienceBlock().enabled())) {
            pluginManager.registerEvents(new BlockExperienceListener(this.paperPlugin, this.cooldownManager), this.paperPlugin);
        }

        if (this.anyEnabled(companions, vanilla -> vanilla.blockDrops().enabled())) {
            pluginManager.registerEvents(new BlockDropListener(this.paperPlugin, this.cooldownManager), this.paperPlugin);
        }

        if (this.anyEnabled(companions, vanilla -> vanilla.experienceFurnace().enabled())) {
            pluginManager.registerEvents(new FurnaceExperienceListener(this.paperPlugin, this.cooldownManager), this.paperPlugin);
        }

        if (this.anyEnabled(companions, vanilla -> vanilla.experienceFishing().enabled()) || this.anyEnabled(companions, v -> v.fishingLoot().enabled())) {
            pluginManager.registerEvents(new FishingListener(this.paperPlugin, this.cooldownManager), this.paperPlugin);
        }
    }

    private boolean anyEnabled(
            final @NotNull Collection<Companions> companions,
            final @NotNull Function<Vanilla, Boolean> predicate
    ) {
        return companions.stream().anyMatch(companion -> predicate.apply(companion.vanilla()));
    }
}