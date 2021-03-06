package com.teamwizardry.refraction.api.beam;

import com.google.common.collect.HashMultimap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.lang.ref.WeakReference;
import java.util.HashSet;

public class BlockTracker {
	public HashMultimap<BlockPos, Beam> locations;
	public WeakReference<World> world;

	public BlockTracker(World world) {
		this.world = new WeakReference<>(world);
		locations = HashMultimap.create();
	}

	public void addBeam(Beam beam) {
		HashSet<BlockPos> possible = new HashSet<>();
		Vec3d slope = beam.slope;
		Vec3d curPos = beam.initLoc;
		boolean finished = false;
		while (!finished) {
			Vec3d nextPos = curPos.add(slope);
			for (BlockPos pos : BlockPos.getAllInBox(new BlockPos(curPos), new BlockPos(nextPos)))
				possible.add(pos);
			if (curPos.distanceTo(beam.finalLoc) <= curPos.distanceTo(nextPos))
				finished = true;
			curPos = nextPos;
		}

		Vec3d invSlope = new Vec3d(1 / slope.x, 1 / slope.y, 1 / slope.z);

		for (BlockPos pos : possible) {
			if (collides(beam, pos, invSlope))
				locations.put(pos, beam);
		}

		locations.remove(new BlockPos(beam.initLoc), beam);
		locations.remove(beam.trace.getBlockPos(), beam);
	}

	private boolean collides(Beam beam, BlockPos pos, Vec3d invSlope) {
		boolean signX = invSlope.x < 0;
		boolean signY = invSlope.y < 0;
		boolean signZ = invSlope.z < 0;

		double txMin, txMax, tyMin, tyMax, tzMin, tzMax;
		AxisAlignedBB axis = new AxisAlignedBB(pos);

		txMin = ((signX ? axis.maxX : axis.minX) - beam.initLoc.x) * invSlope.x;
		txMax = ((signX ? axis.minX : axis.maxX) - beam.initLoc.x) * invSlope.x;
		tyMin = ((signY ? axis.maxY : axis.minY) - beam.initLoc.y) * invSlope.y;
		tyMax = ((signY ? axis.minY : axis.maxY) - beam.initLoc.y) * invSlope.y;
		tzMin = ((signZ ? axis.maxZ : axis.minZ) - beam.initLoc.z) * invSlope.z;
		tzMax = ((signZ ? axis.minZ : axis.maxZ) - beam.initLoc.z) * invSlope.z;

		if (txMin > txMax || tyMin > txMax)
			return false;
		if (tyMin > txMin)
			txMin = tyMin;
		if (tyMax < txMax)
			txMax = tyMax;
		if (txMin > tzMax || tzMin > txMax)
			return false;
		if (tzMin > txMin)
			txMin = tzMin;
		if (tzMax < txMax)
			txMax = tzMax;

		return true;
	}

	public void generateEffects() {
		if (world.get() != null) {
			for (BlockPos pos : locations.keySet()) {
				for (Beam beam : locations.get(pos)) {
					EffectTracker.addEffect(world.get(), new Vec3d(pos), beam.effect);
				}
			}
		}
		locations.clear();
	}
}
