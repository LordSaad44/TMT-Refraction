package com.teamwizardry.refraction.api;

import com.teamwizardry.refraction.api.beam.Beam;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author WireSegal
 *         Created at 10:51 PM on 10/31/16.
 *         <p>
 *         Attach this to blocks that should attach to adjacent Fiber Optic Cables.
 */
public interface IOpticConnectable {
	@Nonnull
	List<EnumFacing> getAvailableFacings(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos, @Nonnull EnumFacing facing);

	@Nonnull
	default List<EnumFacing> getAllFacings(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos, @Nonnull EnumFacing facing) {
		return getAvailableFacings(state, source, pos, facing);
	}

	void handleFiberBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam);
}
