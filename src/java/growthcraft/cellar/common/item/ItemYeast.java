package growthcraft.cellar.common.item;

import java.util.List;

import growthcraft.cellar.GrowthCraftCellar;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

public class ItemYeast extends Item
{
	protected IIcon[] icons;

	public ItemYeast()
	{
		super();
		setHasSubtypes(true);
		setMaxDamage(0);
		setTextureName("grccellar:yeast");
		setUnlocalizedName("grc.yeast");
		setCreativeTab(GrowthCraftCellar.tab);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return super.getUnlocalizedName(stack) + stack.getItemDamage();
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (EnumYeast ytype : EnumYeast.values())
		{
			list.add(ytype.asStack());
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister reg)
	{
		this.icons = new IIcon[EnumYeast.length];
		icons[EnumYeast.BAYANUS.ordinal()] = reg.registerIcon(getIconString() + "_bayanus");
		icons[EnumYeast.BREWERS.ordinal()] = reg.registerIcon(getIconString() + "_brewers");
		icons[EnumYeast.ETHEREAL.ordinal()] = reg.registerIcon(getIconString() + "_ethereal");
		icons[EnumYeast.LAGER.ordinal()] = reg.registerIcon(getIconString() + "_lager");
		icons[EnumYeast.ORIGIN.ordinal()] = reg.registerIcon(getIconString() + "_origin");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int meta)
	{
		return icons[MathHelper.clamp_int(meta, 0, icons.length - 1)];
	}
}
