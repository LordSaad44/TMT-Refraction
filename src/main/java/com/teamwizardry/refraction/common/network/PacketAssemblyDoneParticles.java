package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorFade;
import com.teamwizardry.refraction.api.Constants;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Demoniaque.
 */
public class PacketAssemblyDoneParticles extends PacketBase {

	public BlockPos pos;

	public PacketAssemblyDoneParticles() {
	}

	public PacketAssemblyDoneParticles(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void fromBytes(@NotNull ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(@NotNull ByteBuf buf) {
		buf.writeLong(pos.toLong());
	}

	@Override
	public void handle(@NotNull MessageContext messageContext) {
		if (messageContext.side.isServer()) return;

		World world = Minecraft.getMinecraft().player.world;

		ParticleBuilder builder = new ParticleBuilder(1);
		builder.setAlphaFunction(new InterpFloatInOut(0.1f, 0.3f));
		builder.setColorFunction(new InterpColorFade(Color.GREEN, 1, 255, 1));
		builder.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
		ParticleSpawner.spawn(builder, world, new StaticInterp<>(new Vec3d(pos.getX() + 0.5, pos.getY() + 1.25, pos.getZ() + 0.5)), ThreadLocalRandom.current().nextInt(200, 300), 0, (aFloat, particleBuilder) -> {
			double radius = 0.1;
			double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
			double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
			double r = (u > 1) ? 2 - u : u;
			double x1 = r * Math.cos(t), z1 = r * Math.sin(t);
			builder.setPositionOffset(new Vec3d(x1, ThreadLocalRandom.current().nextDouble(-0.1, 0.1), z1));
			builder.setScale(ThreadLocalRandom.current().nextFloat());
			builder.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
					ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
					ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
			builder.setLifetime(ThreadLocalRandom.current().nextInt(20, 80));
		});
	}
}
