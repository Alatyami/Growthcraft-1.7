package growthcraft.cellar.common.item;

import java.util.List;

import growthcraft.cellar.common.block.BlockFluidBooze;
import growthcraft.core.util.UnitFormatter;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class ItemBlockFluidBooze extends ItemBlock
{
	protected int color = 0xFFFFFF;
	protected Fluid booze;

	public ItemBlockFluidBooze(Block block)
	{
		super(block);
		if (block instanceof BlockFluidBooze)
		{
			final BlockFluidBooze boozeBlock = (BlockFluidBooze)block;
			this.color = boozeBlock.getColor();
			this.booze = boozeBlock.getFluid();
		}
	}

	public Fluid getBooze()
	{
		return this.booze;
	}

	public int getBoozeColor()
	{
		return this.color;
	}

	@SideOnly(Side.CLIENT)
	@SuppressWarnings({"rawtypes", "unchecked"})
	protected void writeModifierTooltip(ItemStack stack, EntityPlayer player, List list, boolean bool)
	{
		final String modifier = UnitFormatter.fluidModifier(getBooze());
		if (modifier != null) list.add(modifier);
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool)
	{
		writeModifierTooltip(stack, player, list, bool);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass)
	{
		return getBoozeColor();
	}
}
