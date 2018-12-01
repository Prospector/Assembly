package teamreborn.assembly.mixin;

import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import teamreborn.assembly.powernet.PowernetConnector;

@Mixin(FurnaceBlockEntity.class)
public abstract class FurnaceBlockEntityMixin implements PowernetConnector {

    @Override
    public BlockPos getConnectorPos() {
        return getEntity().getPos();
    }

    @Override
    public World getConnectorWorld() {
        return getEntity().getWorld();
    }

    public FurnaceBlockEntity getEntity() {
        return (FurnaceBlockEntity) (Object) this;
    }
}
