package team.bytephoria.bytecompanionsbuffs.configuration.buff;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import team.bytephoria.bytecompanionsbuffs.util.Operation;
import team.bytephoria.bytecompanionsbuffs.configuration.feedback.FeedBack;

@ConfigSerializable
public class Buff {

    @Setting("enabled")
    private boolean enabled = true;

    @Setting("operation")
    private Operation operation = Operation.SUM;

    @Setting("value")
    private double value = 0.0;

    @Setting("cooldown")
    private long cooldown = 0L;

    @Setting("feedback")
    private FeedBack feedBack = new FeedBack();

    public boolean enabled() {
        return this.enabled;
    }

    public Operation operation() {
        return this.operation;
    }

    public double value() {
        return this.value;
    }

    public long cooldown() {
        return this.cooldown;
    }

    public FeedBack feedBack() {
        return this.feedBack;
    }
}