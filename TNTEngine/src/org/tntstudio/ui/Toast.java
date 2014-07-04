
package org.tntstudio.ui;

import org.tntstudio.Const.ToastTime;
import org.tntstudio.TNTRuntimeException;
import org.tntstudio.core.Top;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class Toast extends Group {

	private static float bottom = 100;


	private float type;
	private TextButton toast;
	private Action beginAction, endAction;

	private static TextButtonStyle style;

	public static void initToast (NinePatch patch, BitmapFont font) {
		/*-------- Create style of Toast --------*/
		NinePatchDrawable nine = new NinePatchDrawable(patch);
		style = new TextButtonStyle();
		style.up = nine;
		style.font = font;
	}

	private void validate () {
		this.clear();
		this.addActor(toast);
		this.setPosition(Top.gameWidth() / 2 - toast.getWidth() / 2, bottom);
		this.setOrigin(toast.getWidth() / 2, toast.getHeight() / 2);
	}

	public Toast makeToast (String text, float type, Action beginAction, Action endAction) {
		if (style == null) new TNTRuntimeException("Must contructor initToast() to make a Toast.");
		this.beginAction = beginAction;
		this.endAction = endAction;
		toast = new TextButton(text, style);
		toast.align(Align.center);
		validate();
		this.setTouchable(Touchable.disabled);
		this.setTransform(true);
		checkType(type);
		return this;
	}

	private void checkType (float mType) {
		/*-------- check type of Toast --------*/
		type = mType;
		if (mType != 0) {
			if (beginAction != null && endAction != null)
				this.addAction(Actions.sequence(beginAction, Actions.delay(mType), endAction));
			else {
				this.addAction(Actions.sequence(Actions.delay(mType), endAction));
			}
		} else {
			this.addAction(beginAction);
		}
		/*-------- end Check --------*/

	}

	@Override
	public void act (float delta) {
		super.act(delta);
		if (type == ToastTime.SCREEN_TOUCH) {
			if (Top.tinp.isTouched()) {
				this.addAction(endAction);
			}
		}
	}

	public static class ToastAction {
		public static Action popOut () {
			return Actions.sequence(Actions.scaleTo(0, 0), Actions.scaleTo(1f, 1f, 0.5f, Interpolation.bounceOut));
		}
		
		public static Action popIn(){
			return Actions.scaleTo(0f, 0f, 0.5f, Interpolation.bounceIn);
		}

		public static Action rollOut () {
			return Actions.sequence(Actions.scaleTo(0.5f, 0.5f), Actions.alpha(0f),
				Actions.parallel(Actions.scaleTo(1f, 1f, 0.5f, Interpolation.bounceOut), Actions.alpha(1f, 0.3f)));
		}
	}
}
