
package com.ict.entities;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.ict.entities.G4CrossBow.Arrow;
import com.ict.entities.G4Enemies.Enemy;

public class CollisionChecker {
	public static final void checkCollision (ArrayList<Enemy> enemies, ArrayList<Arrow> arrows) {
		for (Arrow a : arrows) {
			for (Enemy e : enemies) {
				if (Vector2.dst(a.getX() + a.getWidth() / 2, a.getY() + a.getHeight() / 2, e.getCenterX(), e.getCenterY()) < G4Enemies.ENEMY_WIDTH / 2) {
					a.hit(e);
					e.hit(a);
				}
			}
		}
	}
}
