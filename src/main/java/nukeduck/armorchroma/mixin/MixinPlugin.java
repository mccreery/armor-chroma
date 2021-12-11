package nukeduck.armorchroma.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("compat.", "nukeduck.armorchroma.mixin.".length())) {
            // Compatiblity mixins
            FabricLoader loader = FabricLoader.getInstance();
            if (matchesCompatPackage(mixinClassName, "extendedarmorbars.")) {
                return loader.isModLoaded("extended_armor_bars");
            } else if (matchesCompatPackage(mixinClassName, "semitranslucencyfix.")) {
                return loader.isModLoaded("semitranslucency");
            } else {
                // Shouldn't happen
                throw new AssertionError("Compatibility mixin '" + mixinClassName + "' doesn't have a corresponding check in shouldApplyMixin");
            }
        } else {
            // Regular mixins
            return true;
        }
    }

    private boolean matchesCompatPackage(String mixinClassName, String pkg) {
        return mixinClassName.startsWith(pkg, "nukeduck.armorchroma.mixin.compat.".length());
    }

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

}
