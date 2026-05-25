package team.bytephoria.bytecompanionsbuffs.configuration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public final class Companions {

    @Setting("vanilla")
    private Vanilla vanilla = new Vanilla();

    public Vanilla vanilla() {
        return this.vanilla;
    }

}
