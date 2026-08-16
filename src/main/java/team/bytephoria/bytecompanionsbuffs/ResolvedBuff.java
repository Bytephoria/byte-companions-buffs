package team.bytephoria.bytecompanionsbuffs;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.bytecompanionsbuffs.configuration.buff.Buff;

public record ResolvedBuff(
        @NotNull String companionTypeId,
        int priority,
        @NotNull Buff buff
) {
}
