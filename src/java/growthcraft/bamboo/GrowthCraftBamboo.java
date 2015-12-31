package growthcraft.bamboo;

import growthcraft.api.core.log.GrcLogger;
import growthcraft.api.core.log.ILogger;
import growthcraft.bamboo.common.block.BlockBamboo;
import growthcraft.bamboo.common.block.BlockBambooDoor;
import growthcraft.bamboo.common.block.BlockBambooFence;
import growthcraft.bamboo.common.block.BlockBambooFenceGate;
import growthcraft.bamboo.common.block.BlockBambooLeaves;
import growthcraft.bamboo.common.block.BlockBambooScaffold;
import growthcraft.bamboo.common.block.BlockBambooShoot;
import growthcraft.bamboo.common.block.BlockBambooSlab;
import growthcraft.bamboo.common.block.BlockBambooStairs;
import growthcraft.bamboo.common.block.BlockBambooStalk;
import growthcraft.bamboo.common.block.BlockBambooWall;
import growthcraft.bamboo.common.CommonProxy;
import growthcraft.bamboo.common.entity.EntityBambooRaft;
import growthcraft.bamboo.common.item.ItemBamboo;
import growthcraft.bamboo.common.item.ItemBambooCoal;
import growthcraft.bamboo.common.item.ItemBambooDoor;
import growthcraft.bamboo.common.item.ItemBambooRaft;
import growthcraft.bamboo.common.item.ItemBambooShoot;
import growthcraft.bamboo.common.item.ItemBambooSlab;
import growthcraft.bamboo.common.village.ComponentVillageBambooYard;
import growthcraft.bamboo.common.village.VillageHandlerBamboo;
import growthcraft.bamboo.common.world.BiomeGenBamboo;
import growthcraft.bamboo.common.world.WorldGeneratorBamboo;
import growthcraft.bamboo.event.BonemealEventBamboo;
import growthcraft.bamboo.handler.BambooFuelHandler;
import growthcraft.core.common.definition.BlockDefinition;
import growthcraft.core.common.definition.BlockTypeDefinition;
import growthcraft.core.common.definition.ItemDefinition;
import growthcraft.api.core.module.ModuleContainer;
import growthcraft.core.integration.NEI;
import growthcraft.core.util.MapGenHelper;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.block.BlockSlab;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(
	modid = GrowthCraftBamboo.MOD_ID,
	name = GrowthCraftBamboo.MOD_NAME,
	version = GrowthCraftBamboo.MOD_VERSION,
	dependencies = "required-after:Growthcraft"
)
public class GrowthCraftBamboo
{
	public static final String MOD_ID = "Growthcraft|Bamboo";
	public static final String MOD_NAME = "Growthcraft Bamboo";
	public static final String MOD_VERSION = "@VERSION@";

	@Instance(MOD_ID)
	public static GrowthCraftBamboo instance;

	public static BlockDefinition bambooBlock;
	public static BlockTypeDefinition<BlockBambooShoot> bambooShoot;
	public static BlockTypeDefinition<BlockBambooStalk> bambooStalk;
	public static BlockDefinition bambooLeaves;
	public static BlockDefinition bambooFence;
	public static BlockDefinition bambooWall;
	public static BlockDefinition bambooStairs;
	public static BlockTypeDefinition<BlockSlab> bambooSingleSlab;
	public static BlockTypeDefinition<BlockSlab> bambooDoubleSlab;
	public static BlockDefinition bambooDoor;
	public static BlockDefinition bambooFenceGate;
	public static BlockDefinition bambooScaffold;
	public static ItemDefinition bamboo;
	public static ItemDefinition bambooDoorItem;
	public static ItemDefinition bambooRaft;
	public static ItemDefinition bambooCoal;
	public static ItemDefinition bambooShootFood;

	public static BiomeGenBase bambooBiome;

	private ILogger logger = new GrcLogger(MOD_ID);
	private GrcBambooConfig config = new GrcBambooConfig();
	private ModuleContainer modules = new ModuleContainer();

	public static GrcBambooConfig getConfig()
	{
		return instance.config;
	}

	@EventHandler
	public void preload(FMLPreInitializationEvent event)
	{
		config.setLogger(logger);
		config.load(event.getModConfigurationDirectory(), "growthcraft/bamboo.conf");

		if (config.enableThaumcraftIntegration) modules.add(new growthcraft.bamboo.integration.ThaumcraftModule());

		if (config.debugEnabled) modules.setLogger(logger);

		//====================
		// INIT
		//====================
		bambooBlock      = new BlockDefinition(new BlockBamboo());
		bambooShoot      = new BlockTypeDefinition<BlockBambooShoot>(new BlockBambooShoot());
		bambooStalk      = new BlockTypeDefinition<BlockBambooStalk>(new BlockBambooStalk());
		bambooLeaves     = new BlockDefinition(new BlockBambooLeaves());
		bambooFence      = new BlockDefinition(new BlockBambooFence());
		bambooWall       = new BlockDefinition(new BlockBambooWall());
		bambooStairs     = new BlockDefinition(new BlockBambooStairs());
		bambooSingleSlab = new BlockTypeDefinition<BlockSlab>(new BlockBambooSlab(false));
		bambooDoubleSlab = new BlockTypeDefinition<BlockSlab>(new BlockBambooSlab(true));
		bambooDoor       = new BlockDefinition(new BlockBambooDoor());
		bambooFenceGate  = new BlockDefinition(new BlockBambooFenceGate());
		bambooScaffold   = new BlockDefinition(new BlockBambooScaffold());

		bamboo = new ItemDefinition(new ItemBamboo());
		bambooDoorItem = new ItemDefinition(new ItemBambooDoor());
		bambooRaft = new ItemDefinition(new ItemBambooRaft());
		bambooCoal = new ItemDefinition(new ItemBambooCoal());
		bambooShootFood = new ItemDefinition(new ItemBambooShoot());

		if (config.generateBambooBiome)
		{
			bambooBiome = (new BiomeGenBamboo(config.bambooBiomeID))
				.setColor(353825)
				.setBiomeName("BambooForest")
				.func_76733_a(5159473)
				.setTemperatureRainfall(0.7F, 0.8F);
		}

		modules.preInit();
		register();
	}

	private void register()
	{
		//====================
		// REGISTRIES
		//====================
		GameRegistry.registerBlock(bambooBlock.getBlock(), "grc.bambooBlock");
		GameRegistry.registerBlock(bambooShoot.getBlock(), "grc.bambooShoot");
		GameRegistry.registerBlock(bambooStalk.getBlock(), "grc.bambooStalk");
		GameRegistry.registerBlock(bambooLeaves.getBlock(), "grc.bambooLeaves");
		GameRegistry.registerBlock(bambooFence.getBlock(), "grc.bambooFence");
		GameRegistry.registerBlock(bambooWall.getBlock(), "grc.bambooWall");
		GameRegistry.registerBlock(bambooStairs.getBlock(), "grc.bambooStairs");
		GameRegistry.registerBlock(bambooSingleSlab.getBlock(), ItemBambooSlab.class, "grc.bambooSingleSlab");
		GameRegistry.registerBlock(bambooDoubleSlab.getBlock(), ItemBambooSlab.class, "grc.bambooDoubleSlab");
		GameRegistry.registerBlock(bambooDoor.getBlock(), "grc.bambooDoor");
		GameRegistry.registerBlock(bambooFenceGate.getBlock(), "grc.bambooFenceGate");
		GameRegistry.registerBlock(bambooScaffold.getBlock(), "grc.bambooScaffold");

		GameRegistry.registerItem(bamboo.getItem(), "grc.bamboo");
		GameRegistry.registerItem(bambooDoorItem.getItem(), "grc.bambooDoorItem");
		GameRegistry.registerItem(bambooRaft.getItem(), "grc.bambooRaft");
		GameRegistry.registerItem(bambooCoal.getItem(), "grc.bambooCoal");
		GameRegistry.registerItem(bambooShootFood.getItem(), "grc.bambooShootFood");

		if (config.generateBambooBiome)
		{
			//GameRegistry.addBiome(bambooBiome);
			BiomeManager.addSpawnBiome(bambooBiome);
			BiomeDictionary.registerBiomeType(bambooBiome, Type.FOREST);
		}

		GameRegistry.registerWorldGenerator(new WorldGeneratorBamboo(), 0);

		EntityRegistry.registerModEntity(EntityBambooRaft.class, "bambooRaft", 1, this, 80, 3, true);

		//====================
		// ADDITIONAL PROPS.
		//====================
		Blocks.fire.setFireInfo(bambooBlock.getBlock(), 5, 20);
		Blocks.fire.setFireInfo(bambooStalk.getBlock(), 5, 4);
		Blocks.fire.setFireInfo(bambooLeaves.getBlock(), 30, 60);
		Blocks.fire.setFireInfo(bambooFence.getBlock(), 5, 20);
		Blocks.fire.setFireInfo(bambooWall.getBlock(), 5, 20);
		Blocks.fire.setFireInfo(bambooStairs.getBlock(), 5, 20);
		Blocks.fire.setFireInfo(bambooSingleSlab.getBlock(), 5, 20);
		Blocks.fire.setFireInfo(bambooDoubleSlab.getBlock(), 5, 20);
		Blocks.fire.setFireInfo(bambooScaffold.getBlock(), 5, 20);

		//====================
		// CRAFTING
		//====================
		GameRegistry.addShapedRecipe(bambooWall.asStack(6), "###", "###", '#', bambooBlock.getBlock());
		GameRegistry.addShapedRecipe(bambooStairs.asStack(4), "#  ", "## ", "###", '#', bambooBlock.getBlock());
		GameRegistry.addShapedRecipe(bambooSingleSlab.asStack(6), "###", '#', bambooBlock.getBlock());
		GameRegistry.addShapedRecipe(bambooDoorItem.asStack(), "##", "##", "##", '#', bambooBlock.getBlock());
		GameRegistry.addShapedRecipe(bambooRaft.asStack(), "A A", "AAA", 'A', bambooBlock.getBlock());
		GameRegistry.addShapedRecipe(bambooBlock.asStack(), "A", "A", 'A', bambooSingleSlab.getBlock());
		GameRegistry.addShapedRecipe(bambooBlock.asStack(), "AA", "AA", 'A', bamboo.getItem());
		GameRegistry.addShapedRecipe(bambooFence.asStack(3), "AAA", "AAA", 'A', bamboo.getItem());
		GameRegistry.addShapedRecipe(bambooFenceGate.asStack(), "ABA", "ABA", 'A', bamboo.getItem(), 'B', bambooBlock.getBlock());
		GameRegistry.addShapedRecipe(bambooScaffold.asStack(16), "BBB", " A ", "A A", 'A', bamboo.getItem(), 'B', bambooBlock.getBlock());
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.torch, 2), new Object[] {"A", "B", 'A', bambooCoal.getItem(), 'B', "stickWood"}));

		MapGenHelper.registerVillageStructure(ComponentVillageBambooYard.class, "grc.bambooyard");

		registerOres();

		//====================
		// SMELTING
		//====================
		GameRegistry.registerFuelHandler(new BambooFuelHandler());
		GameRegistry.addSmelting(bamboo.getItem(), bambooCoal.asStack(), 0.075f);

		NEI.hideItem(bambooDoor.asStack());

		modules.register();
	}

	public void registerOres()
	{
		/*
		 * ORE DICTIONARY
		 */

		// General ore dictionary names
		OreDictionary.registerOre("stickWood", bamboo.getItem());
		OreDictionary.registerOre("woodStick", bamboo.getItem());
		OreDictionary.registerOre("plankWood", bambooBlock.getBlock());
		OreDictionary.registerOre("woodPlank", bambooBlock.getBlock());
		OreDictionary.registerOre("slabWood", bambooSingleSlab.getBlock());
		OreDictionary.registerOre("woodSlab", bambooSingleSlab.getBlock());
		OreDictionary.registerOre("stairWood", bambooStairs.getBlock());
		OreDictionary.registerOre("woodStair", bambooStairs.getBlock());
		OreDictionary.registerOre("leavesTree", bambooLeaves.getBlock());
		OreDictionary.registerOre("treeLeaves", bambooLeaves.getBlock());


		// Bamboo specific
		OreDictionary.registerOre("cropBamboo", bamboo.getItem());
		OreDictionary.registerOre("materialBamboo", bamboo.getItem());
		OreDictionary.registerOre("bamboo", bamboo.getItem());
		OreDictionary.registerOre("plankBamboo", bambooBlock.getBlock());
		OreDictionary.registerOre("slabBamboo", bambooSingleSlab.getBlock());
		OreDictionary.registerOre("stairBamboo", bambooStairs.getBlock());
		OreDictionary.registerOre("treeBambooLeaves", bambooLeaves.getBlock());

		OreDictionary.registerOre("foodBambooshoot", bambooShoot.getBlock());
		OreDictionary.registerOre("foodBambooshoot", bambooShootFood.getItem());

		/*
		 * For Pam's HarvestCraft
		 *   Uses the same OreDict. names as HarvestCraft
		 */
		OreDictionary.registerOre("cropBambooshoot", bambooShoot.getBlock());
		OreDictionary.registerOre("listAllveggie", bambooShoot.getBlock());
		OreDictionary.registerOre("cropBambooshoot", bambooShootFood.getItem());
		OreDictionary.registerOre("listAllveggie", bambooShootFood.getItem());
	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		CommonProxy.instance.initRenders();
		final VillageHandlerBamboo handler = new VillageHandlerBamboo();
		VillagerRegistry.instance().registerVillageCreationHandler(handler);

		modules.init();
	}

	@EventHandler
	public void postload(FMLPostInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new BonemealEventBamboo());

		modules.postInit();
	}
}
