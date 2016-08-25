package br.odb.droidlib;

import java.io.Serializable;

import br.odb.knights.Actor;
import br.odb.knights.GameViewGLES2;
import br.odb.knights.Knight;

public class Tile implements Renderable, Serializable{
	private int kind;
	private boolean block;
	private Renderable occupant;
	private final GameViewGLES2.ETextures textureId;

	/**
	 * @return the block
	 */
	public boolean isBlock() {
		return block;
	}

	/**
	 * @param block the block to set
	 */
	public void setBlock(boolean block) {
		this.block = block;
	}

	/**
	 * @return the kind
	 */
	public int getKind() {
		return kind;
	}

	/**
	 * @param kind the kind to set
	 */
	public void setKind(int kind) {
		this.kind = kind;
	}

	public Tile(int kind, GameViewGLES2.ETextures texture) {
		if (kind < 0) {
			kind = 0;
		}

		textureId = texture;
		setKind(kind);
	}

	@Override
	public GameViewGLES2.ETextures getTextureIndex() {

		if (occupant != null) {

			if (occupant instanceof Knight && ((Knight) occupant).hasExited) {
				return textureId;
			}

			return occupant.getTextureIndex();
		} else {
			return textureId;
		}
	}

	public Renderable getOccupant() {
		return occupant;
	}

	public void setOccupant(Actor actor) {
		occupant = actor;
	}

	public GameViewGLES2.ETextures getMapTextureIndex() {
		return textureId;
	}
}
