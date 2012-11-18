/**
 * Copyright (c) SpaceToad, 2011-2012
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.builders;

import java.util.List;

import buildcraft.BuildCraftBuilders;
import buildcraft.core.ItemBuildCraft;
import buildcraft.core.blueprints.BptBase;
import buildcraft.core.proxy.CoreProxy;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public abstract class ItemBptBase extends ItemBuildCraft {

	public ItemBptBase(int i) {
		super(i);

		maxStackSize = 1;
		iconIndex = 5 * 16 + 0;
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	@SuppressWarnings({ "all" })
	// @Override (client only)
	public abstract int getIconFromDamage(int i);

	@SuppressWarnings({ "all" })
	// @Override (client only)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean advanced) {
		if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("BptName")) {
			list.add(itemstack.getTagCompound().getString("BptName"));
		}
	}

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if(CoreProxy.proxy.isSimulating(world)) {
			BptBase bpt = BuildCraftBuilders.getBptRootIndex().getBluePrint(itemStack.getItemDamage());
			if(bpt != null) {
				return BuildCraftBuilders
					.getBptItemStack(itemStack.itemID, itemStack.getItemDamage(), bpt.getName());
			}
		}
		return itemStack;
    }

	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {}

}
