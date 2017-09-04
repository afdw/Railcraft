/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.charge;

import mods.railcraft.common.blocks.charge.ChargeManager;
import mods.railcraft.common.blocks.charge.IChargeBlock;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileChargeFeederAdmin extends TileCharge {
    @Nullable
    public InfiniteBattery chargeBattery;

    private static class InfiniteBattery extends IChargeBlock.ChargeBattery {
        private boolean enabled;

        @Override
        public double getMaxDraw() {
            return enabled ? Double.MAX_VALUE : 0.0;
        }

        @Override
        public boolean isInfinite() {
            return enabled;
        }

        @Override
        public double getCharge() {
            return enabled ? getCapacity() : 0.0;
        }

        @Override
        public double removeCharge(double request) {
            if (enabled)
                return request;
            return 0.0;
        }

    }

    @Override
    public IEnumMachine<?> getMachineType() {
        return FeederVariant.ADMIN;
    }

    @Override
    public InfiniteBattery getChargeBattery() {
        if (chargeBattery == null) {
            chargeBattery = (InfiniteBattery) ChargeManager.getNetwork(worldObj).getTileBattery(pos, InfiniteBattery::new);
        }
        return chargeBattery;
    }

    @Override
    public List<ItemStack> getDrops(int fortune) {
        return Collections.emptyList();
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(state, placer, stack);
        getChargeBattery().enabled = state.getValue(BlockChargeFeeder.REDSTONE);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
        getChargeBattery().enabled = state.getValue(BlockChargeFeeder.REDSTONE);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("enabled", getChargeBattery().enabled);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        getChargeBattery().enabled = nbt.getBoolean("enabled");
    }
}
