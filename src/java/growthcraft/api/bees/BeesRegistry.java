package growthcraft.api.bees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import growthcraft.api.core.util.ItemKey;
import growthcraft.api.core.util.BlockKey;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BeesRegistry
{
	private static final BeesRegistry INSTANCE = new BeesRegistry();

	private final List<ItemKey> beesList = new ArrayList<ItemKey>();
	private final Map<ItemKey, ItemStack> emptyToFullHoneyComb = new HashMap<ItemKey, ItemStack>();
	private final Map<ItemKey, ItemStack> fullToEmptyHoneyComb = new HashMap<ItemKey, ItemStack>();
	private final List<BlockKey> flowersList = new ArrayList<BlockKey>();

	public static final BeesRegistry instance()
	{
		return INSTANCE;
	}

	private ItemKey stackToKey(ItemStack itemstack)
	{
		return new ItemKey(itemstack);
	}

	///////////////////////////////////////////////////////////////////////
	// BEES ///////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	/**
	 * addBee()
	 * Adds a custom bee the mod.
	 * NOTE: This is not meta-sensitive.
	 *
	 * @param bee - The Item/Block to be registered.
	 */
	public void addBee(Item bee, int meta)
	{
		beesList.add(new ItemKey(bee, meta));
	}

	public void addBee(Block bee, int meta)
	{
		addBee(Item.getItemFromBlock(bee), meta);
	}

	public void addBee(ItemStack stack)
	{
		beesList.add(stackToKey(stack));
	}

	public void addBee(Item bee)
	{
		addBee(bee, ItemKey.WILDCARD_VALUE);
	}

	public void addBee(Block bee)
	{
		addBee(Item.getItemFromBlock(bee), ItemKey.WILDCARD_VALUE);
	}

	protected void addHoneyCombMapping(ItemStack empty, ItemStack full)
	{
		emptyToFullHoneyComb.put(stackToKey(empty), full);
		fullToEmptyHoneyComb.put(stackToKey(full), empty);
	}

	public void addHoneyComb(ItemStack empty, ItemStack full)
	{
		addHoneyCombMapping(empty, full);
	}

	public ItemStack getFilledHoneyComb(ItemStack itemstack)
	{
		return emptyToFullHoneyComb.get(stackToKey(itemstack));
	}

	public ItemStack getEmptyHoneyComb(ItemStack itemstack)
	{
		return fullToEmptyHoneyComb.get(stackToKey(itemstack));
	}

	protected boolean isItemFilledHoneyComb(ItemKey key)
	{
		return fullToEmptyHoneyComb.containsKey(key);
	}

	public boolean isItemFilledHoneyComb(ItemStack itemstack)
	{
		return isItemFilledHoneyComb(stackToKey(itemstack));
	}

	protected boolean isItemEmptyHoneyComb(ItemKey key)
	{
		return emptyToFullHoneyComb.containsKey(key);
	}

	public boolean isItemEmptyHoneyComb(ItemStack itemstack)
	{
		return isItemEmptyHoneyComb(stackToKey(itemstack));
	}

	public boolean isItemHoneyComb(ItemStack itemstack)
	{
		final ItemKey key = stackToKey(itemstack);
		return isItemFilledHoneyComb(key) || isItemEmptyHoneyComb(key);
	}

	/**
	 * addFlower()
	 * Adds a custom flower the mod.
	 * NOTE: This is meta-sensitive.
	 *
	 * @param flower - Block to be registered.
	 * @param meta   - Metadata of the block to be registered.
	 */
	public void addFlower(Block flower, int meta)
	{
		flowersList.add(new BlockKey(flower, meta));
	}

	/**
	 * Flower wildcard
	 *
	 * @param flower - Block to be registered.
	 */
	public void addFlower(Block flower)
	{
		addFlower(flower, ItemKey.WILDCARD_VALUE);
	}

	/**
	 * @param itemstack - an itemstack to check
	 * @return Does the provided itemstack contain any known bees?
	 */
	public boolean isItemBee(ItemStack itemstack)
	{
		if (itemstack == null) return false;
		return beesList.contains(new ItemKey(itemstack)) ||
			beesList.contains(new ItemKey(itemstack.getItem(), ItemKey.WILDCARD_VALUE));
	}

	public boolean isBlockFlower(Block block, int meta)
	{
		if (block == null) return false;
		return flowersList.contains(new BlockKey(block, meta)) ||
			flowersList.contains(new BlockKey(block, ItemKey.WILDCARD_VALUE));
	}
}
