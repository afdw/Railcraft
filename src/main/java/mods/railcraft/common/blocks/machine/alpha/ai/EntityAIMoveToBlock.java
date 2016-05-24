/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha.ai;

import mods.railcraft.common.plugins.forge.WorldPlugin;

import com.google.common.base.Predicates;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Vec3;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EntityAIMoveToBlock extends EntityAIBase {

    private final EntityCreature entity;
    /**
     * This is the Maximum distance that the AI will look for the Entity
     */
    private final int maxDist;
    private final float weight;
    private final IBlockState searchedState;
    /**
     * The closest entity which is being watched by this one.
     */
    protected BlockPos watchedBlock;

    public EntityAIMoveToBlock(EntityCreature entity, IBlockState searchedState, int maxDist) {
        this(entity, searchedState, maxDist, 0.001F);
    }

    public EntityAIMoveToBlock(EntityCreature entity, IBlockState searchedState, int maxDist, float weight) {
        this.entity = entity;
        this.searchedState = searchedState;
        this.maxDist = maxDist;
        this.weight = weight;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     *
     * @return
     */
    @Override
    public boolean shouldExecute() {
        if (entity.getRNG().nextFloat() >= this.weight)
            return false;

        if (!entity.worldObj.isDaytime())
            return false;

        if (watchedBlock == null || !isBlockValid())
            watchedBlock = WorldPlugin.findBlock(entity.worldObj, entity.getPosition(), maxDist, Predicates.equalTo(searchedState));

        return watchedBlock != null;
    }

    private boolean isBlockValid() {
        if (searchedState != WorldPlugin.getBlockState(entity.worldObj, watchedBlock))
            return false;
        return entity.getDistanceSq(watchedBlock) <= maxDist * maxDist;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     *
     * @return
     */
    @Override
    public boolean continueExecuting() {
        return !entity.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        if (entity.getDistanceSq(watchedBlock.getX() + 0.5D, watchedBlock.getY() + 0.5D, watchedBlock.getZ() + 0.5D) > 256.0D) {
            Vec3 vec1 = new Vec3(watchedBlock.getX() + 0.5, watchedBlock.getY() + 0.5, watchedBlock.getZ() + 0.5);
            Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(entity, 14, 3, vec1);
            if (vec3 != null)
                move(vec3.xCoord, vec3.yCoord, vec3.zCoord);
        } else
            move(watchedBlock.getX() + 0.5D, watchedBlock.getY() + 0.5D, watchedBlock.getZ() + 0.5D);
    }

    private void move(double x, double y, double z) {
        entity.getNavigator().tryMoveToXYZ(x, y, z, 0.6D);
//        System.out.println("Moving to Block");
//        EffectManager.instance.teleportEffect(entity, watchedBlock.x + 0.5D, watchedBlock.y + 0.5D, watchedBlock.z + 0.5D);
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        this.watchedBlock = null;
    }
}
