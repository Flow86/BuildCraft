package buildcraft.transport.pipe.behaviour;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandlerModifiable;

import buildcraft.api.core.EnumPipePart;
import buildcraft.api.core.IStackFilter;
import buildcraft.api.transport.neptune.IFlowFluid;
import buildcraft.api.transport.neptune.IFlowItems;
import buildcraft.api.transport.neptune.IItemPluggable;
import buildcraft.api.transport.neptune.IPipe;
import buildcraft.api.transport.neptune.IPipeHolder.PipeMessageReceiver;

import buildcraft.lib.inventory.filter.*;
import buildcraft.lib.misc.EntityUtil;
import buildcraft.lib.misc.StackUtil;
import buildcraft.lib.tile.item.ItemHandlerSimple;
import buildcraft.transport.BCTransportGuis;

public class PipeBehaviourWoodDiamond extends PipeBehaviourWood {

    public enum FilterMode {
        WHITE_LIST,
        BLACK_LIST,
        ROUND_ROBIN;

        public static FilterMode get(int index) {
            switch (index) {
                default:
                case 0:
                    return WHITE_LIST;
                case 1:
                    return BLACK_LIST;
                case 2:
                    return ROUND_ROBIN;
            }
        }
    }

    public final ItemHandlerSimple filters = new ItemHandlerSimple(9, this::onSlotChanged);
    public FilterMode filterMode = FilterMode.WHITE_LIST;
    public int currentFilter = 0;
    public boolean filterValid = false;

    public PipeBehaviourWoodDiamond(IPipe pipe) {
        super(pipe);
    }

    public PipeBehaviourWoodDiamond(IPipe pipe, NBTTagCompound nbt) {
        super(pipe, nbt);
        filters.deserializeNBT(nbt.getCompoundTag("filters"));
        filterMode = FilterMode.get(nbt.getByte("mode"));
        currentFilter = nbt.getByte("currentFilter") % filters.getSlots();
        filterValid = !filters.extract(StackFilter.ALL, 1, 1, true).isEmpty();
    }

    @Override
    public NBTTagCompound writeToNbt() {
        NBTTagCompound nbt = super.writeToNbt();
        nbt.setTag("filters", filters.serializeNBT());
        nbt.setByte("mode", (byte) filterMode.ordinal());
        nbt.setByte("currentFilter", (byte) currentFilter);
        return nbt;
    }

    @Override
    public void readPayload(PacketBuffer buffer, Side side, MessageContext ctx) {
        super.readPayload(buffer, side, ctx);
        if (side == Side.CLIENT) {
            filterMode = FilterMode.get(buffer.readUnsignedByte());
            currentFilter = buffer.readUnsignedByte() % filters.getSlots();
            filterValid = buffer.readBoolean();
        }
    }

    @Override
    public void writePayload(PacketBuffer buffer, Side side) {
        super.writePayload(buffer, side);
        if (side == Side.SERVER) {
            buffer.writeByte(filterMode.ordinal());
            buffer.writeByte(currentFilter);
            buffer.writeBoolean(filterValid);
        }
    }

    @Override
    public boolean onPipeActivate(EntityPlayer player, RayTraceResult trace, float hitX, float hitY, float hitZ, EnumPipePart part) {
        if (EntityUtil.getWrenchHand(player) != null) {
            return super.onPipeActivate(player, trace, hitX, hitY, hitZ, part);
        }
        ItemStack held = player.getHeldItemMainhand();
        if (held != null) {
            if (held.getItem() instanceof IItemPluggable) {
                return false;
            }
        }
        if (!player.world.isRemote) {
            BCTransportGuis.PIPE_DIAMOND_WOOD.openGui(player, pipe.getHolder().getPipePos());
        }
        return true;
    }

    private void onSlotChanged(IItemHandlerModifiable itemHandler, int slot, ItemStack before, ItemStack after) {
        if (!after.isEmpty()) {
            if (!filterValid) {
                currentFilter = slot;
                filterValid = true;
            }
        } else if (slot == currentFilter) {
            advanceFilter();
        }
    }

    private IStackFilter getStackFilter() {
        switch (filterMode) {
            default:
            case WHITE_LIST:
                return new DelegatingItemHandlerFilter(StackUtil::isMatchingItemOrList, filters);
            case BLACK_LIST:
                return new InvertedStackFilter(new DelegatingItemHandlerFilter(StackUtil::isMatchingItemOrList, filters));
            case ROUND_ROBIN:
                return (comparison) -> {
                    ItemStack filter = filters.getStackInSlot(currentFilter);
                    return StackUtil.isMatchingItemOrList(filter, comparison);
                };
        }
    }

    @Override
    protected int extractItems(IFlowItems flow, EnumFacing dir, int count) {
        if (filters.getStackInSlot(currentFilter).isEmpty()) {
            advanceFilter();
        }
        int extracted = flow.tryExtractItems(1, getCurrentDir(), getStackFilter());
        if (extracted > 0 & filterMode == FilterMode.ROUND_ROBIN) {
            advanceFilter();
        }
        return extracted;
    }

    @Override
    protected FluidStack extractFluid(IFlowFluid flow, EnumFacing dir, int millibuckets) {
        if (filters.getStackInSlot(currentFilter).isEmpty()) {
            advanceFilter();
        }

        switch (filterMode) {
            default:
            case WHITE_LIST:
                // Firstly try the advanced version - if that fails we will need to try the basic version
                FluidStack extracted = flow.tryExtractFluidAdv(millibuckets, dir, new ArrayFluidFilter(filters.stacks));

                if (extracted == null || extracted.amount <= 0) {
                    for (int i = 0; i < filters.getSlots(); i++) {
                        ItemStack stack = filters.getStackInSlot(i);
                        extracted = flow.tryExtractFluid(millibuckets, dir, FluidUtil.getFluidContained(stack));
                        if (extracted != null && extracted.amount > 0) {
                            return extracted;
                        }
                    }
                }
                return null;
            case BLACK_LIST:
                // We cannot fallback to the basic version - only use the advanced version
                return flow.tryExtractFluidAdv(millibuckets, dir, new InvertedFluidFilter(new ArrayFluidFilter(filters.stacks)));
            case ROUND_ROBIN:
                // We can't do this -- amounts might differ and its just ugly
                return null;
        }
    }

    private void advanceFilter() {
        int lastFilter = currentFilter;
        filterValid = false;
        while (true) {
            currentFilter++;
            if (currentFilter >= filters.getSlots()) {
                currentFilter = 0;
            }
            if (!filters.getStackInSlot(currentFilter).isEmpty()) {
                filterValid = true;
                break;
            }
            if (currentFilter == lastFilter) {
                break;
            }
        }
        if (lastFilter != currentFilter) {
            pipe.getHolder().scheduleNetworkGuiUpdate(PipeMessageReceiver.BEHAVIOUR);
        }
    }
}
