/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks;

import com.mojang.authlib.GameProfile;
import mods.railcraft.api.core.INetworkedObject;
import mods.railcraft.api.core.IOwnable;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.AdjacentTileCache;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.PacketBuilder;
import mods.railcraft.common.util.network.PacketTileEntity;
import mods.railcraft.common.util.network.RailcraftPacket;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import javax.annotation.Nonnull;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class RailcraftTileEntity extends TileEntity implements INetworkedObject, IOwnable, ITickable {

    protected final AdjacentTileCache tileCache = new AdjacentTileCache(this);
    protected int clock = MiscTools.RANDOM.nextInt();
    private GameProfile owner = new GameProfile(null, "[Railcraft]");
    private boolean sendClientUpdate;
    private UUID uuid;
    private String customName = "";

    public IBlockState getActualState(IBlockState state) {
        return state;
    }

    public static boolean isUsableByPlayerHelper(TileEntity tile, EntityPlayer player) {
        return !tile.isInvalid() && tile.getWorld().getTileEntity(tile.getPos()) == tile && player.getDistanceSq(tile.getPos()) <= 64;
    }

    public UUID getUUID() {
        if (uuid == null)
            uuid = UUID.randomUUID();
        return uuid;
    }

    public AdjacentTileCache getTileCache() {
        return tileCache;
    }

    public boolean canUpdate() {
        return true;
    }

    @Override
    public void update() {
        if (!canUpdate())
            worldObj.tickabNOPEleTileEntities.remove(this); // Concurrency error

        clock++;

        if (sendClientUpdate) {
            sendClientUpdate = false;
            PacketBuilder.instance().sendTileEntityPacket(this);
        }
    }

    @Override
    public FMLProxyPacket getDescriptionPacket() {
//        System.out.println("Sending Tile Packet");
        RailcraftPacket packet = new PacketTileEntity(this);
        return packet.getPacket();
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
//        data.writeUTF(owner);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
//        owner = data.readUTF();
    }

    public void markBlockForUpdate() {
//        System.out.println("updating");
        if (worldObj != null)
            worldObj.markBlockForUpdate(getPos());
    }

    public void notifyBlocksOfNeighborChange() {
        if (worldObj != null)
            WorldPlugin.notifyBlocksOfNeighborChange(worldObj, getPos(), getBlockType());
    }

    public void sendUpdateToClient() {
        if (canUpdate())
            sendClientUpdate = true;
        else
            PacketBuilder.instance().sendTileEntityPacket(this);
    }

    public void onBlockPlacedBy(IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (placer instanceof EntityPlayer)
            owner = ((EntityPlayer) placer).getGameProfile();
    }

    public void onNeighborBlockChange(IBlockState state, Block neighborBlock) {
        tileCache.onNeighborChange();
    }

    @Override
    public void invalidate() {
        tileCache.purge();
        super.invalidate();
    }

    @Override
    public void validate() {
        tileCache.purge();
        super.validate();
    }

    public final int getDimension() {
        if (worldObj == null)
            return 0;
        return worldObj.provider.getDimension();
    }

    @Override
    public final GameProfile getOwner() {
        return owner;
    }

    public boolean isOwner(GameProfile player) {
        return PlayerPlugin.isSamePlayer(owner, player);
    }

    public abstract String getLocalizationTag();

    public List<String> getDebugOutput() {
        List<String> debug = new ArrayList<String>();
        debug.add("Railcraft Tile Entity Data Dump");
        debug.add("Object: " + this);
        debug.add(String.format("Coordinates: d=%d, %s", worldObj.provider.getDimensionId(), getPos()));
        debug.add("Owner: " + (owner == null ? "null" : owner.getName()));
        debug.addAll(tileCache.getDebugOutput());
        return debug;
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if (owner.getName() != null)
            data.setString("owner", owner.getName());
        if (owner.getId() != null)
            data.setString("ownerId", owner.getId().toString());

        MiscTools.writeUUID(data, "uuid", uuid);
        if (!customName.isEmpty())
            data.setString("customName", customName);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        owner = PlayerPlugin.readOwnerFromNBT(data);
        uuid = MiscTools.readUUID(data, "uuid");
        customName = data.getString("customName");
    }

    public final int getX() {
        return getPos().getX();
    }

    public final int getY() {
        return getPos().getY();
    }

    public final int getZ() {
        return getPos().getZ();
    }

    @Nonnull
    @Override
    public final World getWorld() {
        return worldObj;
    }

    public abstract short getId();

    @Override
    public boolean hasCustomName() {
        return !customName.isEmpty();
    }

    public void setCustomName(String name) {
        if (name != null)
            customName = name;
    }

    @Nonnull
    @Override
    public String getName() {
        return hasCustomName() ? customName : getLocalizationTag();
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return hasCustomName() ? new TextComponentString(customName) : new TextComponentTranslation(getLocalizationTag());
    }

}
