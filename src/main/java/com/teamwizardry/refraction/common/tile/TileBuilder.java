package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.SaveMethodGetter;
import com.teamwizardry.librarianlib.common.util.saving.SaveMethodSetter;
import com.teamwizardry.refraction.api.MultipleBeamTile;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.client.gui.builder.GuiBuilder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.awt.*;

/**
 * Created by LordSaad.
 */
@TileRegister("builder")
public class TileBuilder extends MultipleBeamTile {

	public GuiBuilder.TileType[][][] grid;

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
	public void update() {
		super.update();

		if (outputBeam != null && Utils.doColorsMatchNoAlpha(Color.GREEN, outputBeam.color)) {

		}
	}
}
