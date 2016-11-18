package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpColorFade;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.AssemblyRecipe;
import com.teamwizardry.refraction.api.CapsUtils;
import com.teamwizardry.refraction.api.EventAssemblyTableCraft;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.init.ModItems;
import com.teamwizardry.refraction.init.recipies.AssemblyRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
@TileRegister("assembly_table")
public class TileAssemblyTable extends TileMod {

	@Save
	public boolean isCrafting = false;
	@Save
	public ItemStackHandler output = new ItemStackHandler(1) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (isCrafting) return null;
			else return super.extractItem(slot, amount, simulate);
		}
	};
	@Save
	public ItemStackHandler inventory = new ItemStackHandler(54) {
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (output.getStackInSlot(0) != null && output.getStackInSlot(0).stackSize > 0) return null;
			if (isCrafting) return null;
			return super.extractItem(slot, amount, simulate);
		}
	};
	@Save
	private int craftingTime = 0;
	@Save
	private boolean isGrenadeRecipe = false;

	public TileAssemblyTable() {
	}

	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (facing == EnumFacing.DOWN ? (T) output : (T) inventory) : super.getCapability(capability, facing);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	public void handle(Beam... inputs) {
		if (!worldObj.isBlockPowered(getPos()) && worldObj.isBlockIndirectlyGettingPowered(getPos()) == 0) return;
		if (inputs.length <= 0) return;
		int red = 0, green = 0, blue = 0, alpha = 0;

		int count = 0;
		for (Beam beam : inputs) {
			if (beam.enableEffect) {
				count++;
				red += beam.color.getRed() * (beam.color.getAlpha() / 255f);
				green += beam.color.getGreen() * (beam.color.getAlpha() / 255f);
				blue += beam.color.getBlue() * (beam.color.getAlpha() / 255f);
				alpha += beam.color.getAlpha();
			}
		}

		red = Math.min(red / count, 255);
		green = Math.min(green / count, 255);
		blue = Math.min(blue / count, 255);

		float[] hsbvals2 = Color.RGBtoHSB(red, green, blue, null);
		Color color = new Color(Color.HSBtoRGB(hsbvals2[0], hsbvals2[1], 1));
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha / 2, 255));

		if (isCrafting) {
			if (craftingTime < 100) {
				craftingTime++;
				ParticleBuilder builder = new ParticleBuilder(5);
				builder.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
				builder.setColorFunction(new InterpColorFade(Color.RED, 1, 255, 1));
				builder.setRender(new ResourceLocation(Refraction.MOD_ID, "particles/glow"));
				ParticleSpawner.spawn(builder, worldObj, new StaticInterp<>(new Vec3d(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5)), ThreadLocalRandom.current().nextInt(20, 40), 0, (aFloat, particleBuilder) -> {
					double radius = 0.3;
					double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
					double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
					double r = (u > 1) ? 2 - u : u;
					double x = r * Math.cos(t), z = r * Math.sin(t);
					builder.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(-0.3, 0.3), z));
					builder.setScale(ThreadLocalRandom.current().nextFloat());
					builder.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01) / 10,
							ThreadLocalRandom.current().nextDouble(0.001, 0.01) / 10,
							ThreadLocalRandom.current().nextDouble(-0.01, 0.01) / 10));
					builder.setLifetime(ThreadLocalRandom.current().nextInt(30, 50));
				});
			} else {
				isCrafting = false;
				ParticleBuilder builder = new ParticleBuilder(1);
				builder.setAlphaFunction(new InterpFadeInOut(0.1f, 0.3f));
				builder.setColorFunction(new InterpColorFade(Color.GREEN, 1, 255, 1));
				builder.setRender(new ResourceLocation(Refraction.MOD_ID, "particles/glow"));
				ParticleSpawner.spawn(builder, worldObj, new StaticInterp<>(new Vec3d(getPos().getX() + 0.5, getPos().getY() + 1.25, getPos().getZ() + 0.5)), ThreadLocalRandom.current().nextInt(200, 300), 0, (aFloat, particleBuilder) -> {
					double radius = 0.1;
					double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
					double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
					double r = (u > 1) ? 2 - u : u;
					double x = r * Math.cos(t), z = r * Math.sin(t);
					builder.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(-0.1, 0.1), z));
					builder.setScale(ThreadLocalRandom.current().nextFloat());
					builder.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
							ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
							ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
					builder.setLifetime(ThreadLocalRandom.current().nextInt(20, 80));
				});
				if (isGrenadeRecipe) {
					ItemStack stack = new ItemStack(ModItems.GRENADE);
					NBTTagCompound compound = new NBTTagCompound();
					compound.setInteger("color", new Color(red, green, blue).getRGB());
					compound.setInteger("color_alpha", Math.min(alpha, 255));
					stack.setTagCompound(compound);
					//MARKER: GRENADE CRAFTING
					EventAssemblyTableCraft eventAssemblyTableCraft = new EventAssemblyTableCraft(stack);
					MinecraftForge.EVENT_BUS.post(eventAssemblyTableCraft);
					output.setStackInSlot(0, eventAssemblyTableCraft.getOutput()); //
				}
				markDirty();
			}
			return;
		}

		if (getOccupiedSlotCount() <= 0) return;

		if (CapsUtils.getListOfItems(inventory).size() == 1
				&& CapsUtils.getListOfItems(inventory).get(0).getItem() == ModItems.GRENADE) {
			isCrafting = true;
			craftingTime = 0;
			isGrenadeRecipe = true;
			CapsUtils.clearInventory(inventory);
			markDirty();
		}

		for (AssemblyRecipe recipe : AssemblyRecipes.recipes) {

			if (recipe.getItems().size() != getOccupiedSlotCount()) continue;
			if (color.getRed() > recipe.getMaxRed()) continue;
			if (color.getRed() < recipe.getMinRed()) continue;
			if (color.getGreen() > recipe.getMaxGreen()) continue;
			if (color.getGreen() < recipe.getMinGreen()) continue;
			if (color.getBlue() > recipe.getMaxBlue()) continue;
			if (color.getBlue() < recipe.getMinBlue()) continue;
			if (color.getAlpha() > recipe.getMaxStrength()) continue;
			if (color.getAlpha() < recipe.getMinStrength()) continue;

			if (Utils.matchItemStackLists(recipe.getItems(), Utils.getListOfObjects(getListOfItems()))) {
				//MARKER: REGULAR CRAFTING
				EventAssemblyTableCraft eventAssemblyTableCraft = new EventAssemblyTableCraft(recipe.getResult().copy());
				MinecraftForge.EVENT_BUS.post(eventAssemblyTableCraft);
				output.setStackInSlot(0, eventAssemblyTableCraft.getOutput());
				isCrafting = true;
				craftingTime = 0;
				for (int i = 0; i < inventory.getSlots(); i++) inventory.setStackInSlot(i, null);
				markDirty();
			}
		}
	}

	public int getOccupiedSlotCount() {
		int x = 0;
		for (int i = 0; i < inventory.getSlots(); i++) if (inventory.getStackInSlot(i) != null) x++;
		return x;
	}

	public int getLastOccupiedSlot() {
		for (int i = inventory.getSlots() - 1; i > 0; i--) if (inventory.getStackInSlot(i) != null) return i;
		return 0;
	}

	public List<ItemStack> getListOfItems() {
		List<ItemStack> stacks = new ArrayList<>();
		for (int i = 0; i < inventory.getSlots(); i++)
			if (inventory.getStackInSlot(i) != null) stacks.add(inventory.getStackInSlot(i));
		return stacks;
	}
}
