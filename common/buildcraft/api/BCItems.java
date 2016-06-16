package buildcraft.api;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

import buildcraft.api.core.BCDebugging;
import buildcraft.api.core.BCLog;

/** Stores all of BuildCraft's items, from all of its modules. If any of them have been disabled by the user (or it the
 * module is not installed) then they will be null. This is the equivalent of {@link Items} */
public class BCItems {
    private static final boolean DEBUG = BCDebugging.shouldDebugLog("api.items");

    // The ONLY item in BC LIB, and has other rules for when its enabled.
    public static final Item LIB_GUIDE;

    // BC Core
    public static final Item CORE_WRENCH;
    public static final Item CORE_LIST;
    public static final Item CORE_MAP_LOCATION;
    public static final Item CORE_PAINTBRUSH;
    public static final Item CORE_GEAR_WOOD;
    public static final Item CORE_GEAR_STONE;
    public static final Item CORE_GEAR_IRON;
    public static final Item CORE_GEAR_GOLD;
    public static final Item CORE_GEAR_DIAMOND;
    public static final Item CORE_MARKER_CONNECTOR;

    // BC Builders
    public static final Item BUILDERS_SINGLE_SCHEMATIC;
    public static final Item BUILDERS_BLUEPRINT;
    public static final Item BUILDERS_TEMPLATE;

    // BC Factory
    public static final Item FACTORY_PLASTIC_SHEET;

    // BC Robotics
    public static final Item ROBOTICS_REDSTONE_BOARD;
    public static final Item ROBOTICS_ROBOT;
    public static final Item ROBOTICS_ROBOT_GOGGLES;
    public static final Item ROBOTICS_PLUGGABLE_ROBOT_STATION;

    // BC Silicon
    public static final Item SILICON_REDSTONE_CLIPSET;

    // BC Transport
    public static final Item TRANSPORT_WATERPROOF;
    public static final Item TRANSPORT_GATE_COPIER;
    public static final Item TRANSPORT_PLUGGABLE_GATE;
    public static final Item TRANSPORT_PLUGGABLE_WIRE;
    public static final Item TRANSPORT_PLUGGABLE_PLUG;
    public static final Item TRANSPORT_PLUGGABLE_LENS;
    public static final Item TRANSPORT_PLUGGABLE_POWER_ADAPTOR;
    public static final Item TRANSPORT_PLUGGABLE_FACADE;

    static {
        if (!Loader.instance().hasReachedState(LoaderState.INITIALIZATION)) {
            throw new RuntimeException("Accessed BC items too early! You can only use them from init onwards!");
        }
        // core
        final String core = "core";
        CORE_WRENCH = getRegisteredItem(core, "wrench");
        CORE_LIST = getRegisteredItem(core, "list");
        CORE_MAP_LOCATION = getRegisteredItem(core, "map_location");
        CORE_PAINTBRUSH = getRegisteredItem(core, "paintbrush");
        CORE_GEAR_WOOD = getRegisteredItem(core, "gear_wood");
        CORE_GEAR_STONE = getRegisteredItem(core, "gear_stone");
        CORE_GEAR_IRON = getRegisteredItem(core, "gear_iron");
        CORE_GEAR_GOLD = getRegisteredItem(core, "gear_gold");
        CORE_GEAR_DIAMOND = getRegisteredItem(core, "gear_diamond");
        LIB_GUIDE = getRegisteredItem(core, "guide");
        CORE_MARKER_CONNECTOR = getRegisteredItem(core, "marker_connector");
        // builders
        final String builders = "builders";
        BUILDERS_SINGLE_SCHEMATIC = getRegisteredItem(builders, "single_schematic");
        BUILDERS_BLUEPRINT = getRegisteredItem(builders, "blueprint");
        BUILDERS_TEMPLATE = getRegisteredItem(builders, "template");
        // factory
        final String factory = "factory";
        FACTORY_PLASTIC_SHEET = getRegisteredItem(factory, "plastic_sheet");
        // robotics
        final String robotics = "robotics";
        ROBOTICS_REDSTONE_BOARD = getRegisteredItem(robotics, "redstone_board");
        ROBOTICS_ROBOT = getRegisteredItem(robotics, "robot");
        ROBOTICS_PLUGGABLE_ROBOT_STATION = getRegisteredItem(robotics, "robot_station");
        ROBOTICS_ROBOT_GOGGLES = getRegisteredItem(robotics, "robot_goggles");
        // silicon
        final String silicon = "silicon";
        SILICON_REDSTONE_CLIPSET = getRegisteredItem(silicon, "redstone_chipset");
        // transport
        final String transport = "transport";
        TRANSPORT_WATERPROOF = getRegisteredItem(transport, "waterproof");
        TRANSPORT_GATE_COPIER = getRegisteredItem(transport, "gate_copier");
        TRANSPORT_PLUGGABLE_GATE = getRegisteredItem(transport, "pluggable_gate");
        TRANSPORT_PLUGGABLE_WIRE = getRegisteredItem(transport, "pluggable_wire");
        TRANSPORT_PLUGGABLE_PLUG = getRegisteredItem(transport, "pluggable_plug");
        TRANSPORT_PLUGGABLE_LENS = getRegisteredItem(transport, "pluggable_lens");
        TRANSPORT_PLUGGABLE_FACADE = getRegisteredItem(transport, "pluggable_facade");
        TRANSPORT_PLUGGABLE_POWER_ADAPTOR = getRegisteredItem(transport, "pluggable_power_adapter");
    }

    private static Item getRegisteredItem(String module, String regName) {
        String modid = "buildcraft" + module;
        Item item = Item.REGISTRY.getObject(new ResourceLocation(modid, regName));
        if (item != null) {
            if (DEBUG) {
                BCLog.logger.info("[api.items] Found the item " + regName + " from the module " + module);
            }
            return item;
        }
        if (DEBUG) {
            if (Loader.isModLoaded(modid)) {
                BCLog.logger.info("[api.items] Did not find the item " + regName + " dispite the appropriate mod being loaded (" + modid + ")");
            } else {
                BCLog.logger.info("[api.items] Did not find the item " + regName + " probably because the mod is not loaded (" + modid + ")");
            }
        }

        return null;
    }
}
