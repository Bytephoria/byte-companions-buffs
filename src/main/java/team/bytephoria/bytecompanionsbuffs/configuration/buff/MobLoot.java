package team.bytephoria.bytecompanionsbuffs.configuration.buff;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import team.bytephoria.bytecompanionsbuffs.util.ApplyTo;

@ConfigSerializable
public final class MobLoot extends Buff {

    @Setting("apply-to")
    private ApplyTo applyTo = ApplyTo.ALL;

    public ApplyTo applyTo() {
        return this.applyTo;
    }
}