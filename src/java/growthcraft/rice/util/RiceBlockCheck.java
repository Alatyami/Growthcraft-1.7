package growthcraft.rice.util;

import growthcraft.rice.GrowthCraftRice;

import net.minecraft.block.Block;

public class RiceBlockCheck
{
	private RiceBlockCheck() {}

	/**
	 * Determines if block is a normal dirt paddy block
	 *
	 * @param block - the block to check
	 * @return true if the block is a Paddy, false otherwise
	 */
	public static boolean isPaddy(Block block)
	{
		return GrowthCraftRice.paddyField.equals(block);
	}
}
