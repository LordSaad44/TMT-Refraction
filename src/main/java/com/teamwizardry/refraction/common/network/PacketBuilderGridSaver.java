package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.librarianlib.common.util.saving.SaveMethodGetter;
import com.teamwizardry.librarianlib.common.util.saving.SaveMethodSetter;
import com.teamwizardry.refraction.client.gui.builder.GuiBuilder;
import com.teamwizardry.refraction.common.tile.TileBuilder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by LordSaad.
 */
public class PacketBuilderGridSaver extends PacketBase {

	@Save
	private BlockPos pos;
	private GuiBuilder.TileType[][][] grid;

	public PacketBuilderGridSaver() {
	}

	public PacketBuilderGridSaver(BlockPos pos, GuiBuilder.TileType[][][] grid) {
		this.pos = pos;
		this.grid = grid;
	}

	@SaveMethodGetter(saveName = "grid")
	public NBTTagList getter() {
		if (grid != null) {
			NBTTagList list = new NBTTagList();
			for (int i = 0; i < grid.length; i++)
				for (int j = 0; j < grid.length; j++)
					for (int k = 0; k < grid.length; k++) {
						GuiBuilder.TileType box = grid[i][j][k];
						NBTTagCompound compound = new NBTTagCompound();
						compound.setString("type", box.toString());
						compound.setInteger("layer", i);
						compound.setInteger("x", j);
						compound.setInteger("y", k);
						list.appendTag(compound);
					}
		}
		return null;
	}

	@SaveMethodSetter(saveName = "grid")
	public void setter(NBTTagList list) {
		if (list != null) {
			for (int q = 0; q < list.tagCount(); q++) {
				NBTTagCompound compound = list.getCompoundTagAt(q);
				GuiBuilder.TileType type = GuiBuilder.TileType.valueOf(compound.getString("type"));
				int layer = compound.getInteger("layer");
				int x = compound.getInteger("x");
				int y = compound.getInteger("y");
				grid[layer][x][y] = type;
			}
		}
	}

	@Override
	public void handle(MessageContext messageContext) {
		World world = messageContext.getServerHandler().playerEntity.world;
		TileBuilder builder = (TileBuilder) world.getTileEntity(pos);

		if (builder == null) return;

		builder.grid = grid;
		builder.markDirty();
	}
}
