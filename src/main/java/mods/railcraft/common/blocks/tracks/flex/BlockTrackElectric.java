/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.flex;

import mods.railcraft.common.blocks.charge.IChargeBlock;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedController;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 8/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockTrackElectric extends BlockTrackFlex implements IChargeBlock {
    private static ChargeDef chargeDef = new ChargeDef(ConnectType.TRACK, 0.01);

    public BlockTrackElectric() {
        this(new SpeedController());
    }

    protected BlockTrackElectric(SpeedController speedController) {
        super(speedController);
    }

    @Nullable
    @Override
    public ChargeDef getChargeDef(IBlockState state, IBlockAccess world, BlockPos pos) {
        return chargeDef;
    }
}