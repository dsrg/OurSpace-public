package org.dsrg.ourSpace.wea.domLogic;

import static com.google.common.base.Preconditions.checkState;
import static org.dsrg.util.Nulls.nonNull;

import java.util.ArrayList;
import java.util.List;

public class GameMgr {

	public final int SCENE_WIDTH = 16;
	public final int SCENE_HEIGHT = SCENE_WIDTH;
	public final char PLAYER = 'P';
	public final char HOME = 'H';
	public final char BLANK = ' ';

	private final char scene[/* y */][/* x */] = new char[SCENE_HEIGHT][SCENE_WIDTH];

	/*
	 * Tile can contain: 'P' : player; 'B', 'R', 'T' : bush, rock, tree; 'H' :
	 * village house; '' : nothing.
	 */
	// Attributes below here are not considered in the implementation of
	// equals and hashCode.
	private String[] sceneBase = {
			// Initial game scene.
			/* _ *//* 0123456789ABCDEF */
			/* 00 */"TTTTTTTTTTTTTTTT",
			/* 01 */"T  H R         T",
			/* 02 */"T    R   H     T",
			/* 03 */"TRB BR         T",
			/* 04 */"T        T     T",
			/* 05 */"T       T      T",
			/* 06 */"T      T       T",
			/* 07 */"T   H T        T",
			/* 08 */"T     B R      T",
			/* 09 */"T   R          T",
			/* 10 */"T        BB  H T",
			/* 11 */"T              T",
			/* 12 */"T   BBBBBBBBB  T",
			/* 13 */"T        H     T",
			/* 14 */"T              T",
			/* 15 */"TTTTTTTTTTTTTTTT", };

	// Player coordinates
	private Coord playerCoord = new Coord(5, 5);

	public static class Coord {
		public int x, y;

		public Coord(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public GameMgr() {
		checkState(scene.length == sceneBase.length);
		for (int y = 0; y < scene.length; y++) {
			scene[y] = sceneBase[y].toCharArray();
		}
	}

	/**
	 * @return A list of strings representing the game scene. Placing the
	 *         strings one on top of the other will give a rectangular structure
	 *         where each character represents a cell in the scene.
	 */
	public List<String> getScene() {
		Coord c = playerCoord;
		List<String> result = new ArrayList<String>(scene.length);
		for (int y = 0; y < scene.length; y++) {
			char[] row;
			if (y == c.y) {
				row = scene[y].clone();
				row[c.x] = PLAYER;
			} else {
				row = scene[y];
			}
			result.add(String.valueOf(row));
		}
		return nonNull(result);
	}

	public Coord playerChangeX(int deltaX) {
		synchronized (this.playerCoord) {
			Coord newC = new Coord(playerCoord.x + deltaX, playerCoord.y);
			if(onSolid(newC)) {
				// don't move
			} else {
				this.playerCoord = newC;
			}
			adjustScene();
		}
		return this.playerCoord;
	}

	public Coord playerChangeY(int deltaY) {
		synchronized (this.playerCoord) {
			Coord newC = new Coord(playerCoord.x, playerCoord.y + deltaY);
			if(onSolid(newC)) {
				// don't move
			} else {
				this.playerCoord = newC;
			}
			adjustScene();
		}
		return this.playerCoord;
	}

	public boolean onSolid(Coord c) {
		char elt = at(c);
		return elt != BLANK && elt != HOME;
	}
	
	public char at(Coord c) {
		return this.scene[c.y][c.x];
	}
	
	/**
	 * Adjust scene, removing houses if the player hits them.
	 */
	protected void adjustScene() {
		synchronized (this.scene) {
			char[] row = this.scene[playerCoord.y];
			if (row[playerCoord.x] == HOME) {
				row[playerCoord.x] = BLANK;
			}
		}
	}
}
