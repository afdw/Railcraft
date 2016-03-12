/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forge;

import mods.railcraft.api.core.WorldCoordinate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import static mods.railcraft.common.util.misc.MiscTools.*;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class WorldPlugin {

    public static IBlockState getBlockState(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos);
    }

    public static Block getBlock(IBlockAccess world, BlockPos pos) {
        return getBlockState(world, pos).getBlock();
    }

    public static TileEntity getBlockTile(IBlockAccess world, BlockPos pos) {
        return world.getTileEntity(pos);
    }

    @Deprecated
    public static Block getBlockOnSide(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getBlockState(world, pos.offset(side)).getBlock();
    }

    public static boolean isBlockAt(World world, BlockPos pos, Block block, int meta) {
        if (getBlock(world, pos) != block)
            return false;
        return meta == -1 || getBlockMetadata(world, pos) == meta;
    }

    public static boolean isBlockLoaded(World world, BlockPos pos) {
        return world.isBlockLoaded(pos);
    }

    public static boolean isAreaLoaded(World world, BlockPos pos1, BlockPos pos2) {
        return world.isAreaLoaded(pos1, pos2);
    }

    public static boolean isBlockAir(World world, BlockPos pos, Block block) {
        return block.isAir(world, pos);
    }

    public static boolean isBlockAir(World world, BlockPos pos) {
        return world.isAirBlock(pos);
    }

    @Deprecated
    public static int getBlockMetadata(IBlockAccess world, BlockPos pos) {
        return world.getBlockMetadata(pos);
    }

    @Deprecated
    public static int getBlockMetadataOnSide(IBlockAccess world, int i, int j, int k, EnumFacing side) {
        return world.getBlockMetadata(getXOnSide(i, side), getYOnSide(j, side), getZOnSide(k, side));
    }

    public static TileEntity getTileEntityOnSide(World world, BlockPos pos, EnumFacing side) {
        pos = pos.offset(side);
        if (isBlockLoaded(world, pos) && getBlock(world, pos) != Blocks.air)
            return getBlockTile(world, pos);
        return null;
    }

    public static TileEntity getTileEntityOnSide(IBlockAccess world, BlockPos pos, EnumFacing side) {
        pos = pos.offset(side);
        return world.getTileEntity(pos);
    }

    public static boolean setBlockState(World world, BlockPos pos, IBlockState blockState) {
        return world.setBlockState(pos, blockState);
    }

    public static boolean setBlock(World world, BlockPos pos, Block block) {
        return world.setBlock(pos, block);
    }

    public static boolean setBlock(World world, BlockPos pos, Block block, int meta) {
        return world.setBlock(pos, block, meta, 3);
    }

    public static boolean setBlock(World world, BlockPos pos, Block block, int meta, int update) {
        return world.setBlock(pos, block, meta, update);
    }

    public static boolean setBlockToAir(World world, BlockPos pos) {
        return world.setBlockToAir(pos);
    }

    public static void notifyBlocksOfNeighborChange(World world, BlockPos pos, Block block) {
        if (world != null && block != null)
            world.notifyNeighborsOfStateChange(pos, block);
    }

    public static void notifyBlocksOfNeighborChangeOnSide(World world, BlockPos pos, Block block, EnumFacing side) {
        pos = pos.offset(side);
        world.notifyNeighborsOfStateChange(pos, block);
    }

    public static void addBlockEvent(World world, BlockPos pos, Block block, int key, int value) {
        if (world != null && block != null)
            world.addBlockEvent(pos, block, key, value);
    }

    public static WorldCoordinate findBlock(World world, BlockPos pos, int distance, Block block, int meta) {
        for (int yy = y - distance; yy < y + distance; yy++) {
            for (int xx = x - distance; xx < x + distance; xx++) {
                for (int zz = z - distance; zz < z + distance; zz++) {
                    if (block == getBlock(world, xx, yy, zz) && meta == getBlockMetadata(world, xx, yy, zz))
                        return new WorldCoordinate(world.provider.dimensionId, xx, yy, zz);
                }
            }
        }
        return null;
    }

}
