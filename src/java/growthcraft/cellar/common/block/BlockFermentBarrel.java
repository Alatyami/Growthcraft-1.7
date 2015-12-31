package growthcraft.cellar.common.block;

import java.util.Random;

import growthcraft.api.cellar.booze.BoozeTag;
import growthcraft.api.cellar.CellarRegistry;
import growthcraft.cellar.client.render.RenderFermentBarrel;
import growthcraft.cellar.common.tileentity.TileEntityFermentBarrel;
import growthcraft.cellar.GrowthCraftCellar;
import growthcraft.cellar.stats.CellarAchievement;
import growthcraft.cellar.util.CellarGuiType;
import growthcraft.core.util.BlockFlags;
import growthcraft.core.Utils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public class BlockFermentBarrel extends BlockCellarContainer implements ICellarFluidHandler
{
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public BlockFermentBarrel()
	{
		super(Material.wood);
		setTileEntityType(TileEntityFermentBarrel.class);
		setHardness(2.5F);
		setStepSound(soundTypeWood);
		setBlockName("grc.fermentBarrel");
		setCreativeTab(GrowthCraftCellar.tab);
		setGuiType(CellarGuiType.FERMENT_BARREL);
	}

	public boolean isRotatable(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}

	/************
	 * TRIGGERS
	 ************/
	private void setAchievements(EntityPlayer player, Fluid fluid)
	{
		if (fluid != null)
		{
			if (CellarRegistry.instance().booze().isFluidBooze(fluid))
			{
				if (CellarRegistry.instance().booze().hasTags(fluid, BoozeTag.FERMENTED))
				{
					CellarAchievement.FERMENT_BOOZE.unlock(player);
				}
			}
		}
	}

	@Override
	protected boolean playerDrainTank(World world, int x, int y, int z, IFluidHandler tank, ItemStack held, EntityPlayer player)
	{
		final FluidStack available = Utils.playerDrainTank(world, x, y, z, tank, held, player);
		if (available != null && available.amount > 0)
		{
			setAchievements(player, available.getFluid());
			return true;
		}
		return false;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		super.onBlockAdded(world, x, y, z);
		this.setDefaultDirection(world, x, y, z);
	}

	private void setDefaultDirection(World world, int x, int y, int z)
	{
		if (!world.isRemote)
		{
			final Block block = world.getBlock(x, y, z - 1);
			final Block block1 = world.getBlock(x, y, z + 1);
			final Block block2 = world.getBlock(x - 1, y, z);
			final Block block3 = world.getBlock(x + 1, y, z);
			byte meta = 3;

			if (block.func_149730_j() && !block1.func_149730_j())
			{
				meta = 3;
			}

			if (block1.func_149730_j() && !block.func_149730_j())
			{
				meta = 2;
			}

			if (block2.func_149730_j() && !block3.func_149730_j())
			{
				meta = 5;
			}

			if (block3.func_149730_j() && !block2.func_149730_j())
			{
				meta = 4;
			}

			world.setBlockMetadataWithNotify(x, y, z, meta, BlockFlags.UPDATE_CLIENT);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	{
		final int meta = BlockPistonBase.determineOrientation(world, x, y, z, entity);
		world.setBlockMetadataWithNotify(x, y, z, meta, BlockFlags.UPDATE_CLIENT);

		if (stack.hasDisplayName())
		{
			final TileEntityFermentBarrel te = getTileEntity(world, x, y, z);
			te.setGuiDisplayName(stack.getDisplayName());
		}
	}

	/************
	 * STUFF
	 ************/
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z)
	{
		return GrowthCraftCellar.fermentBarrel.getItem();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2)
	{
		return new TileEntityFermentBarrel();
	}

	/************
	 * DROPS
	 ************/
	@Override
	public Item getItemDropped(int meta, Random random, int par3)
	{
		return GrowthCraftCellar.fermentBarrel.getItem();
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 1;
	}

	/************
	 * TEXTURES
	 ************/
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg)
	{
		this.icons = new IIcon[4];

		icons[0] = reg.registerIcon("grccellar:fermentbarrel_0");
		icons[1] = reg.registerIcon("grccellar:fermentbarrel_1");
		icons[2] = reg.registerIcon("grccellar:fermentbarrel_2");
		icons[3] = reg.registerIcon("grccellar:fermentbarrel_3");
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIconByIndex(int index)
	{
		return icons[index];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if (meta == 0 || meta == 1)
		{
			return side == 0 || side == 1 ? icons[1] : icons[0];
		}
		else if (meta == 2 || meta == 3)
		{
			return side == 2 || side == 3 ? icons[1] : icons[0];
		}
		else if (meta == 4 || meta == 5)
		{
			return side == 4 || side == 5 ? icons[1] : icons[0];
		}
		return icons[0];
	}

	/************
	 * RENDERS
	 ************/
	@Override
	public int getRenderType()
	{
		return RenderFermentBarrel.RENDER_ID;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
	{
		return true;
	}

	/************
	 * COMPARATOR
	 ************/
	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int par5)
	{
		final TileEntityFermentBarrel te = getTileEntity(world, x, y, z);
		if (te != null)
		{
			return te.getFermentProgressScaled(15);
		}
		return 0;
	}
}
