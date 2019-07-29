package teamreborn.assembly;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import teamreborn.assembly.block.AssemblyBlocks;
import teamreborn.assembly.blockentity.AssemblyBlockEntities;
import teamreborn.assembly.fluid.AssemblyFluids;
import teamreborn.assembly.item.AssemblyItems;
import teamreborn.assembly.world.AssemblyWorldgen;
import teamreborn.assembly.world.feature.AssemblyFeatures;

public class Assembly implements ModInitializer {
    public static final String MOD_ID = "assembly";

    @Override
    public void onInitialize() {
        AssemblyFluids.register();
        AssemblyItems.register();
        AssemblyBlocks.register();
        AssemblyBlockEntities.register();
        AssemblyFeatures.register();
        AssemblyWorldgen.register();

        FabricItemGroupBuilder.create(new Identifier(MOD_ID, "assembly")).icon(() -> AssemblyItems.OIL_BUCKET.getStackForRender()).appendItems(stacks -> Registry.ITEM.forEach(item -> {
            if (Registry.ITEM.getId(item).getNamespace().equals(MOD_ID)) {
                item.appendStacks(item.getGroup(), (DefaultedList<ItemStack>) stacks);
            }
        })).build();
    }
}
