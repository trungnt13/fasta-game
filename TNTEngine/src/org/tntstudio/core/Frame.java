
package org.tntstudio.core;

import org.tntstudio.TNTRuntimeException;
import org.tntstudio.ui.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.SnapshotArray;

/** Manage all UI in game
 * @author trungnt13 */
public class Frame extends Stage {
	private final ObjectMap<String, Panel> mPanelMap = new ObjectMap<String, Panel>();
	/** up Layer */
	private final Group mHighLevelGroup = new Group();
	/** down Layer */
	private final Group mLowLevelGroup = new Group();

	public Frame (float width, float height, boolean isKeepAspectRatio) {
		super(width, height, isKeepAspectRatio);
		addActor(mLowLevelGroup);
		addActor(mHighLevelGroup);

		/*-------- Init Toast Resources --------*/

		Toast
			.initToast(new NinePatch(new Texture(Gdx.files.classpath("resources/ui/ninepatch.png")), 3, 3, 3, 3), new BitmapFont());
	}

	Panel createNewPanel (String nameOfScreen) {
		return createNewPanel(nameOfScreen, null);
	}

	Panel createNewPanel (BaseScreen screen) {
		return createNewPanel(screen.Name(), null);
	}

	/** {@link Panel} is only created base on given Screen <li>
	 * First find the desire screen, not exist throw Exception. <li>
	 * Get living panel from cached map, if panel doesn't exist, create new one <li>
	 * Add panel to Frame */
	Panel createNewPanel (String nameOfScreen, Drawable background) {
		Screen screen = null;
		if ((screen = Top.tgame.getInitedScreen(nameOfScreen)) == null)
			throw new TNTRuntimeException("Unknown screen with given name:" + nameOfScreen);

		Panel p = mPanelMap.get(nameOfScreen);
		if (p == null) {
			p = new Panel(screen, background);
			p.setName(nameOfScreen);
			mPanelMap.put(nameOfScreen, p);
			addLowLevelUI(p);
		}
		return p;
	}

	/** call {@link Panel#dispose()} */
	public void destroyPanel (String screenName) {
		Screen screen = Top.tgame.getInitedScreen(screenName);
		if (screen != null) {
			final Panel p = screen.getPanel();
			p.dispose();
		}
	}

	/*-------- low level ui manage --------*/

	Group getLowLevelUIs () {
		return mLowLevelGroup;
	}

	void addLowLevelUI (Actor actor) {
		mLowLevelGroup.addActor(actor);
	}

	void removeLowLevelUI (Actor actor) {
		mLowLevelGroup.removeActor(actor);
		if (actor instanceof Panel) mPanelMap.remove(actor.getName());
	}

	/*-------- high level ui --------*/

	Group getHighLevelUIs () {
		return mHighLevelGroup;
	}

	void addHighLevelUI (Actor actor) {
		mHighLevelGroup.addActor(actor);
	}

	void removeHighLevelUI (Actor actor) {
		mHighLevelGroup.removeActor(actor);
	}

	void drawPanel (Panel panel) {
		final SnapshotArray<Actor> children = mLowLevelGroup.getChildren();
		for (Actor a : children)
			a.setVisible(false);
		panel.setVisible(true);

		draw();
	}

	// ///////////////////////////////////////////////////////////////
	// Toast
	// ///////////////////////////////////////////////////////////////

	private final Pool<Toast> poolToast = new Pool<Toast>() {
		@Override
		protected Toast newObject () {
			return new Toast();
		}
	};

	public void makeToast (String text, float type, Action beginAction, Action endAction) {
		Toast toast = poolToast.obtain();
		mHighLevelGroup.addActor(toast.makeToast(text, type, beginAction, Actions.sequence(endAction, Actions.removeActor())));
		poolToast.free(toast);
	}

	public void makeToast (String text, float type) {
		Toast toast = poolToast.obtain();
		mHighLevelGroup.addActor(poolToast.obtain().makeToast(text, type, null, Actions.removeActor()));
		poolToast.free(toast);
	}

}
