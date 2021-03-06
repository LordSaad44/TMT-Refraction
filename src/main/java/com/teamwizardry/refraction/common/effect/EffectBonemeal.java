package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.beam.Effect;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Demoniaque
 */
public class EffectBonemeal extends Effect {

	@Nonnull
	protected Color getEffectColor() {
		return Color.GREEN;
	}

	@Override
	public boolean stillFail() {
        return ConfigValues.EXTRA_FAIL_CHANCE_GREEN > 1 && ThreadLocalRandom.current().nextInt(ConfigValues.EXTRA_FAIL_CHANCE_GREEN) == 0;
    }

	@Override
    public void runFinalBlock(World world, BlockPos pos, int potency) {
        if (world.getBlockState(pos).getBlock() instanceof IGrowable)
			ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, pos);
	}

	@Override
	public void runEntity(World world, Entity entity, int potency) {
		if (entity instanceof EntityPlayer && !Utils.entityWearsFullReflective((EntityLivingBase) entity)) {
			EntityPlayer player = (EntityPlayer) entity;
			player.getFoodStats().addStats(1, 0.5f);
		}
	}
}
