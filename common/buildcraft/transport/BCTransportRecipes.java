package buildcraft.transport;

import com.google.common.collect.ImmutableSet;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import buildcraft.api.BCItems;
import buildcraft.api.recipes.AssemblyRecipe;

import buildcraft.core.BCCoreBlocks;
import buildcraft.core.BCCoreItems;
import buildcraft.lib.misc.ColourUtil;
import buildcraft.lib.recipe.AssemblyRecipeRegistry;
import buildcraft.lib.recipe.NBTAwareShapedOreRecipe;
import buildcraft.lib.recipe.RecipeBuilderShaped;
import buildcraft.transport.gate.EnumGateLogic;
import buildcraft.transport.gate.EnumGateMaterial;
import buildcraft.transport.gate.EnumGateModifier;
import buildcraft.transport.gate.GateVariant;
import buildcraft.transport.item.ItemPipeHolder;

public class BCTransportRecipes {
    public static void init() {
        if (BCTransportItems.waterproof != null) {
            GameRegistry.addShapelessRecipe(new ItemStack(BCTransportItems.waterproof), new ItemStack(Items.DYE, 1, 2));
        }

        if (BCTransportBlocks.filteredBuffer != null) {
            ItemStack out = new ItemStack(BCTransportBlocks.filteredBuffer);
            RecipeBuilderShaped builder = new RecipeBuilderShaped(out);
            builder.add("wdw"); // TODO: diamond pipe in center of this line
            builder.add("wcw");
            builder.add("wpw");
            builder.map('w', "plankWood");
            builder.map('p', Blocks.PISTON);
            builder.map('c', Blocks.CHEST);

            if (BCItems.TRANSPORT_PIPE_DIAMOND_ITEM == null) {
                builder.map('d', Items.DIAMOND);
            } else {
                builder.map('d', BCItems.TRANSPORT_PIPE_DIAMOND_ITEM);
            }

            GameRegistry.addRecipe(builder.build());
        }

        if (BCTransportItems.pipeStructure != null) {
            ItemStack result = new ItemStack(BCTransportItems.pipeStructure, 8);
            RecipeBuilderShaped builder = new RecipeBuilderShaped(result);
            builder.add("cgc");
            builder.map('c', "cobblestone");
            builder.map('g', Blocks.GRAVEL);
            GameRegistry.addRecipe(builder.build());
        }

        addPipeRecipe(BCTransportItems.pipeItemWood, "plankWood");
        addPipeRecipe(BCTransportItems.pipeItemCobble, "cobblestone");
        addPipeRecipe(BCTransportItems.pipeItemStone, "stone");
        addPipeRecipe(BCTransportItems.pipeItemQuartz, "blockQuartz");
        addPipeRecipe(BCTransportItems.pipeItemIron, "ingotIron");
        addPipeRecipe(BCTransportItems.pipeItemGold, "ingotGold");
        addPipeRecipe(BCTransportItems.pipeItemClay, Blocks.CLAY);
        addPipeRecipe(BCTransportItems.pipeItemSandstone, new ItemStack(Blocks.SANDSTONE, 1, OreDictionary.WILDCARD_VALUE));
        addPipeRecipe(BCTransportItems.pipeItemVoid, new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()), "dustRedstone");
        addPipeRecipe(BCTransportItems.pipeItemObsidian, Blocks.OBSIDIAN);
        addPipeRecipe(BCTransportItems.pipeItemDiamond, Items.DIAMOND);
        addPipeRecipe(BCTransportItems.pipeItemLapis, Blocks.LAPIS_BLOCK);
        addPipeRecipe(BCTransportItems.pipeItemDaizuli, Blocks.LAPIS_BLOCK, Items.DIAMOND);
        addPipeRecipe(BCTransportItems.pipeItemDiaWood, "plankWood", Items.DIAMOND);

        Item waterproof = BCTransportItems.waterproof;
        if (waterproof == null) {
            waterproof = Items.SLIME_BALL;
        }
        addPipeUpgradeRecipe(BCTransportItems.pipeItemWood, BCTransportItems.pipeFluidWood, waterproof);
        addPipeUpgradeRecipe(BCTransportItems.pipeItemCobble, BCTransportItems.pipeFluidCobble, waterproof);
        addPipeUpgradeRecipe(BCTransportItems.pipeItemStone, BCTransportItems.pipeFluidStone, waterproof);
        addPipeUpgradeRecipe(BCTransportItems.pipeItemQuartz, BCTransportItems.pipeFluidQuartz, waterproof);
        addPipeUpgradeRecipe(BCTransportItems.pipeItemIron, BCTransportItems.pipeFluidIron, waterproof);
        addPipeUpgradeRecipe(BCTransportItems.pipeItemGold, BCTransportItems.pipeFluidGold, waterproof);
        addPipeUpgradeRecipe(BCTransportItems.pipeItemClay, BCTransportItems.pipeFluidClay, waterproof);
        addPipeUpgradeRecipe(BCTransportItems.pipeItemSandstone, BCTransportItems.pipeFluidSandstone, waterproof);
        addPipeUpgradeRecipe(BCTransportItems.pipeItemVoid, BCTransportItems.pipeFluidVoid, waterproof);
        addPipeUpgradeRecipe(BCTransportItems.pipeItemObsidian, BCTransportItems.pipeFluidObsidian, waterproof);
        // addPipeUpgradeRecipe(BCTransportItems.pipeItemDiamond, BCTransportItems.pipeFluidDiamond, waterproof);
        addPipeUpgradeRecipe(BCTransportItems.pipeItemDiaWood, BCTransportItems.pipeFluidDiaWood, waterproof);

        if (BCTransportItems.plugBlocker != null) {
            ItemStack result = new ItemStack(BCTransportItems.plugBlocker, 4);
            RecipeBuilderShaped builder = new RecipeBuilderShaped(result);
            builder.add("s");
            builder.map('s', BCTransportItems.pipeStructure);
            GameRegistry.addRecipe(builder.build());
        }

        if (BCTransportItems.plugPulsar != null) {
            ItemStack result = new ItemStack(BCTransportItems.plugPulsar);
            RecipeBuilderShaped builder = new RecipeBuilderShaped(result);
            builder.add("rer");
            builder.add("gpg");
            builder.map('e', BCCoreBlocks.engine);
            builder.map('p', BCTransportItems.plugBlocker);
            builder.map('g', "gearIron");
            builder.map('r', "dustRedstone");
            GameRegistry.addRecipe(builder.build());
        }

        if (BCTransportItems.plugGate != null) {
            // You can craft some of the basic gate types in a normal crafting table
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" m ");
            builder.add("mrm");
            builder.add(" b ");
            builder.map('r', "dustRedstone");
            builder.map('b', BCTransportItems.plugBlocker);

            // Base craftable types

            builder.map('m', Items.BRICK);
            makeGateRecipe(builder, EnumGateMaterial.CLAY_BRICK, EnumGateModifier.NO_MODIFIER);

            builder.map('m', "ingotIron");
            makeGateRecipe(builder, EnumGateMaterial.IRON, EnumGateModifier.NO_MODIFIER);

            builder.map('m', Items.NETHERBRICK);
            makeGateRecipe(builder, EnumGateMaterial.NETHER_BRICK, EnumGateModifier.NO_MODIFIER);

            // Iron modifier addition
            GateVariant variant = new GateVariant(EnumGateLogic.AND, EnumGateMaterial.IRON, EnumGateModifier.NO_MODIFIER);
            ItemStack ironGateBase = BCTransportItems.plugGate.getStack(variant);
            builder = new RecipeBuilderShaped();
            builder.add(" m ");
            builder.add("mgm");
            builder.add(" m ");
            builder.map('g', ironGateBase);

            builder.map('m', new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()));
            makeGateRecipe(builder, EnumGateMaterial.IRON, EnumGateModifier.LAPIS);

            builder.map('m', Items.QUARTZ);
            makeGateRecipe(builder, EnumGateMaterial.IRON, EnumGateModifier.QUARTZ);

            builder.map('m', BCCoreItems.diamondShard);
            makeGateRecipe(builder, EnumGateMaterial.IRON, EnumGateModifier.DIAMOND);

            // And Gate <-> Or Gate (shapeless)
            for (EnumGateMaterial material : EnumGateMaterial.VALUES) {
                if (material == EnumGateMaterial.CLAY_BRICK) {
                    continue;
                }
                for (EnumGateModifier modifier : EnumGateModifier.VALUES) {
                    GateVariant varAnd = new GateVariant(EnumGateLogic.AND, material, modifier);
                    ItemStack resultAnd = BCTransportItems.plugGate.getStack(varAnd);

                    GateVariant varOr = new GateVariant(EnumGateLogic.OR, material, modifier);
                    ItemStack resultOr = BCTransportItems.plugGate.getStack(varOr);

                    GameRegistry.addRecipe(new NBTAwareShapedOreRecipe(resultAnd, "i", 'i', resultOr));
                    GameRegistry.addRecipe(new NBTAwareShapedOreRecipe(resultOr, "i", 'i', resultAnd));
                }
            }
            for(EnumDyeColor color : EnumDyeColor.values()) {
                AssemblyRecipeRegistry.INSTANCE.addRecipe(new AssemblyRecipe(10000000000L, ImmutableSet.of(new ItemStack(Items.REDSTONE), new ItemStack(Items.DYE, 1, color.getMetadata())), new ItemStack(BCTransportItems.wire, 8, color.getMetadata())));
            }
        }
    }

    private static void makeGateRecipe(RecipeBuilderShaped builder, EnumGateMaterial material, EnumGateModifier modifier) {
        GateVariant variant = new GateVariant(EnumGateLogic.AND, material, modifier);
        ItemStack result = BCTransportItems.plugGate.getStack(variant);
        GameRegistry.addRecipe(builder.buildNbtAware(result));
    }

    private static void addPipeRecipe(ItemPipeHolder pipe, Object material) {
        addPipeRecipe(pipe, material, material);
    }

    private static void addPipeRecipe(ItemPipeHolder pipe, Object left, Object right) {
        if (pipe == null) {
            return;
        }

        // TODO: Use RecipePipeColour instead!
        RecipeBuilderShaped pipeBuilderSingle = new RecipeBuilderShaped();
        pipeBuilderSingle.add("lgr");

        pipeBuilderSingle.map('l', left);
        pipeBuilderSingle.map('r', right);
        pipeBuilderSingle.map('g', "blockGlassColorless");
        GameRegistry.addRecipe(pipeBuilderSingle.build(new ItemStack(pipe, 8, 0)));

        for (EnumDyeColor colour : EnumDyeColor.values()) {
            pipeBuilderSingle.map('g', "blockGlass" + ColourUtil.getName(colour));
            GameRegistry.addRecipe(pipeBuilderSingle.build(new ItemStack(pipe, 8, colour.getMetadata() + 1)));
        }
    }

    private static void addPipeUpgradeRecipe(ItemPipeHolder from, ItemPipeHolder to, Object additional) {
        if (from == null || to == null) {
            return;
        }
        if (additional == null) {
            throw new NullPointerException("additional");
        }
        
        // TODO: Use RecipePipeColour instead!

        GameRegistry.addShapelessRecipe(new ItemStack(from), new ItemStack(to));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(to), new ItemStack(from), additional));

        for (EnumDyeColor colour : ColourUtil.COLOURS) {
            ItemStack f = new ItemStack(from, 1, colour.getMetadata() + 1);
            ItemStack t = new ItemStack(to, 1, colour.getMetadata() + 1);
            GameRegistry.addShapelessRecipe(f, t);
            GameRegistry.addRecipe(new ShapelessOreRecipe(t, f, additional));
        }
    }
}
