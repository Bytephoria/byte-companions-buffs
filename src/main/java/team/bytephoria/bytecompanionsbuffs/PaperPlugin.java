package team.bytephoria.bytecompanionsbuffs;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import team.bytephoria.bytecompanionsbuffs.cache.PlayerBuffCache;
import team.bytephoria.bytecompanionsbuffs.command.BuffCommand;
import team.bytephoria.bytecompanionsbuffs.configuration.Companions;
import team.bytephoria.bytecompanionsbuffs.configuration.Configuration;
import team.bytephoria.bytecompanionsbuffs.configuration.buff.Buff;
import team.bytephoria.bytecompanionsbuffs.configuration.feedback.FeedBack;
import team.bytephoria.bytecompanionsbuffs.listener.BuffCacheListener;
import team.bytephoria.bytecompanionsbuffs.manager.CooldownManager;
import team.bytephoria.bytecompanionsbuffs.resolver.CompanionBuffResolver;
import team.bytephoria.bytecompanionsbuffs.service.BuffService;
import team.bytephoria.bytecompanionsbuffs.util.ComponentUtil;

import java.io.File;

public final class PaperPlugin extends JavaPlugin {

    private Configuration configuration;

    private PlayerBuffCache playerBuffCache;
    private CooldownManager cooldownManager;

    private BuffService buffService;
    private CompanionBuffResolver companionBuffResolver;

    private ListenerRegistry listenerRegistry;

    @Override
    public void onEnable() {
        this.configuration = this.loadConfiguration();
        this.cooldownManager = new CooldownManager();

        this.playerBuffCache = new PlayerBuffCache();
        this.buffService = new BuffService(this.playerBuffCache, this.cooldownManager);
        this.companionBuffResolver = new CompanionBuffResolver(this);

        this.listenerRegistry = new ListenerRegistry(this, this.configuration);
        this.getServer().getPluginManager().registerEvents(
                new BuffCacheListener(this.playerBuffCache, this.companionBuffResolver), this
        );

        this.listenerRegistry.registerAll();

        this.getServer().getCommandMap().register("bytecompanionsbuffs", new BuffCommand(this));
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        this.getServer().getCommandMap().getKnownCommands().remove("bytecompanionsbuffs");
        this.getServer().getCommandMap().getKnownCommands().remove("bcb");

        this.listenerRegistry = null;
        this.companionBuffResolver = null;
        this.buffService = null;

        if (this.playerBuffCache != null) {
            this.playerBuffCache.clear();
        }

        this.playerBuffCache = null;

        this.cooldownManager = null;
        this.configuration = null;
    }

    public void reload() {
        this.onDisable();
        this.onEnable();
    }

    public void notifyPlayer(final @NotNull Player player, final @NotNull Buff buff) {
        final FeedBack feedBack = buff.feedBack();

        if (feedBack.actionBar().enabled()) {
            final String message = feedBack.actionBar().message()
                    .replace("{value}", Double.toString(buff.value()))
                    .replace("{operation}", buff.operation().name());

            player.sendActionBar(ComponentUtil.asComponent(message));
        }

        if (feedBack.sound().enabled()) {
            player.playSound(feedBack.sound().adventureSound());
        }
    }

    public PlayerBuffCache playerBuffCache() {
        return this.playerBuffCache;
    }

    public BuffService buffService() {
        return this.buffService;
    }

    public CooldownManager cooldownManager() {
        return this.cooldownManager;
    }

    public Companions companions(final @NotNull String companionId) {
        return this.configuration().companions().get(companionId);
    }

    public Configuration configuration() {
        return this.configuration;
    }

    private Configuration loadConfiguration() {
        final File file = new File(this.getDataFolder(), "config.yml");
        if (!file.exists()) {
            this.saveResource("config.yml", false);
        }

        final YamlConfigurationLoader yamlConfigurationLoader = this.createConfigurationLoader(file);
        try {
            final ConfigurationNode configurationNode = yamlConfigurationLoader.load();
            return configurationNode.get(Configuration.class);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    @Contract("_ -> new")
    private @NonNull YamlConfigurationLoader createConfigurationLoader(final @NotNull File file) {
        return YamlConfigurationLoader.builder()
                .defaultOptions(ConfigurationOptions.defaults())
                .nodeStyle(NodeStyle.BLOCK)
                .file(file)
                .indent(2)
                .build();
    }
}
