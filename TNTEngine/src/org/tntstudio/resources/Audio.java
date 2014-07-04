
package org.tntstudio.resources;

import java.util.Iterator;

import org.tntstudio.core.Input.InputCreator;
import org.tntstudio.core.Top;
import org.tntstudio.interfaces.LifecycleListener;

import com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;

/** @author trungnt13 */
public final class Audio implements LifecycleListener, LoadedCallback, OnCompletionListener {
	static Audio CurrentAudio = null;

	final ObjectMap<String, Sound> mSoundPool = new ObjectMap<String, Sound>();
	final ObjectMap<String, Music> mMusicPool = new ObjectMap<String, Music>();

	final Array<Music> mPlayingMusicList = new Array<Music>(3);

	private boolean isEnable = true;

	final AudioCreator Creator = new AudioCreator();

	private float musicVolume = 1f;
	private float soundVolume = 1f;

	public Audio () {
		CurrentAudio = this;
	}

	public class AudioCreator {
		private AudioCreator () {
		}

		public InputCreator initAudio (boolean isEnable, float musicVolumn, float soundVolumn) {
			setEnable(isEnable);
			setMusicVolume(musicVolumn);
			setSoundVolume(soundVolumn);
			return Top.tinp.Creator;
		}
	}

	// ///////////////////////////////////////////////////////////////
	// load and unload sound and music
	// ///////////////////////////////////////////////////////////////

// public void loadMusic (String file) {
// Assets.CurrentAssets.load(file, Music.class, mMusicParam);
// }
//
// public void loadSound (String file) {
// Assets.CurrentAssets.load(file, Sound.class, mSoundParam);
// }
//
// public void unloadMusic (String file) {
// mMusicPool.remove(file);
// Assets.CurrentAssets.unload(file);
// }
//
// public void unloadSound (String file) {
// mSoundPool.remove(file);
// Assets.CurrentAssets.unload(file);
// }

	// ///////////////////////////////////////////////////////////////
	// music and sound manager
	// ///////////////////////////////////////////////////////////////
	public void setEnable (boolean isEnable) {
		this.isEnable = isEnable;
	}

	public Sound getSound (String name) {
		return mSoundPool.get(name);
	}

	public Music getMusic (String name) {
		return mMusicPool.get(name);
	}

	public Music pauseMusic (String name) {
		Music music = mMusicPool.get(name);
		if (music != null) music.pause();
		return music;
	}

	public Sound pauseSound (String name) {
		Sound s = mSoundPool.get(name);
		if (s != null) s.pause();
		return s;
	}

	public Sound pauseSound (String name, long iD) {
		Sound s = mSoundPool.get(name);
		if (s != null) s.pause(iD);
		return s;
	}

	// ///////////////////////////////////////////////////////////////
	// music mehods
	// ///////////////////////////////////////////////////////////////
	public void setMusicVolume (float volume) {
		musicVolume = volume;

		Iterator<Music> musics = mMusicPool.values();
		while (musics.hasNext()) {
			musics.next().setVolume(volume);
		}
	}

	public void setMusicVolume (String musicName, float volume) {
		Music m = mMusicPool.get(musicName);
		if (m != null) m.setVolume(volume);
	}

	public void playMusic (String audioName) {
		if (!isEnable) {
			return;
		}

		Music music = mMusicPool.get(audioName);
		if (music == null) return;
		music.setVolume(musicVolume);
		music.play();

		mPlayingMusicList.add(music);
	}

	public void playMusic (String audioName, float volume, boolean looping) {
		Music music = mMusicPool.get(audioName);
		if (!isEnable) {
			return;
		}

		if (music == null) return;
		music.setVolume(volume);
		music.setLooping(looping);
		music.play();

		mPlayingMusicList.add(music);
	}

	public void playMusic (String audioName, boolean looping) {
		if (!isEnable) {
			return;
		}
		Music music = mMusicPool.get(audioName);
		if (music == null) return;

		music.setVolume(musicVolume);
		music.setLooping(looping);
		music.play();

		mPlayingMusicList.add(music);
	}

	public void playMusic (String audioName, float volume) {
		if (!isEnable) {
			return;
		}
		Music music = mMusicPool.get(audioName);
		if (music == null) return;

		music.setVolume(1);
		music.play();

		mPlayingMusicList.add(music);
	}

	public void stopMusic (String musicName) {
		Music m = mMusicPool.get(musicName);
		if (m != null) m.stop();
	}

	public boolean isPlaying (String audioName) {
		Music m = mMusicPool.get(audioName);
		if (m == null) return false;
		return m.isPlaying();
	}

	public void setLooping (String audioName, boolean isLooping) {
		Music m = mMusicPool.get(audioName);
		if (m != null) m.setLooping(isLooping);
	}

	public boolean isLooping (String audioName) {
		Music m = mMusicPool.get(audioName);
		if (m == null) return false;
		return m.isLooping();
	}

	// ///////////////////////////////////////////////////////////////
	// sound methods
	// ///////////////////////////////////////////////////////////////

	public void setSoundVolume (float volume) {
		soundVolume = volume;
	}

	public void setSoundVolume (String soundName, long ID, float volume) {
		Sound s = mSoundPool.get(soundName);
		if (s != null) s.setVolume(ID, volume);
	}

	public long playLoopingSound (String audioName) {
		if (!isEnable) {
			return 0;
		}
		Sound sound = mSoundPool.get(audioName);
		if (sound == null) return 0;
		return sound.loop(soundVolume);
	}

	public long playLoopingSound (String audioName, float volume) {
		if (!isEnable) {
			return 0;
		}
		Sound sound = mSoundPool.get(audioName);
		if (sound == null) return 0;
		return sound.loop(volume);
	}

	public long playSound (String soundName) {
		if (!isEnable) {
			return 0;
		}
		Sound s = mSoundPool.get(soundName);
		if (s == null) return 0;
		return s.play(soundVolume);
	}

	public long playSound (String soundName, float volume) {
		Sound s = mSoundPool.get(soundName);
		if (!isEnable) {
			return 0;
		}
		if (s == null) return 0;
		return s.play(volume);
	}

	public long playSound (String soundName, float pitch, float pan) {
		Sound tmp = mSoundPool.get(soundName);
		if (!isEnable) {
			return 0;
		}
		if (tmp == null) return 0;
		long id = tmp.play();
		tmp.setPitch(id, pitch);
		tmp.setPan(id, pan, soundVolume);
		return id;
	}

	public long playSound (String soundName, float volume, float pitch, float pan) {
		Sound tmp = mSoundPool.get(soundName);
		if (!isEnable) {
			return 0;
		}

		if (tmp == null) return 0;
		long id = tmp.play();
		tmp.setPitch(id, pitch);
		tmp.setPan(id, pan, volume);
		return id;
	}

	public long playLoopingSound (String soundName, float volume, float pitch, float pan) {
		if (!isEnable) {
			return 0;
		}

		Sound sound = mSoundPool.get(soundName);
		if (sound != null)
			return sound.loop(volume, pitch, pan);
		else
			return 0;
	}

	public void stopSound (String audioName) {
		Sound s = mSoundPool.get(audioName);
		if (s != null) s.stop();
	}

	public void stopSound (String soundName, long ID) {
		Sound s = mSoundPool.get(soundName);
		if (s != null) s.stop(ID);
	}

	public void setPan (String soundName, long ID, float volume, float pan) {
		Sound s = mSoundPool.get(soundName);
		if (s != null) s.setPan(ID, pan, volume);
	}

	public void setPitch (String soundName, long ID, float pitch) {
		Sound s = mSoundPool.get(soundName);
		if (s != null) s.setPitch(ID, pitch);
	}

	public void setLooping (String soundName, long ID, boolean isLooping) {
		Sound s = mSoundPool.get(soundName);
		if (s != null) s.setLooping(ID, isLooping);
	}

	// ///////////////////////////////////////////////////////////////
	// Override
	// ///////////////////////////////////////////////////////////////

	public void dispose () {
		final Values<Sound> sound = mSoundPool.values();
		for (Sound s : sound) {
			s.stop();
		}
		mPlayingMusicList.clear();

		for (Music m : mPlayingMusicList) {
			m.stop();
		}

		mMusicPool.clear();
		mSoundPool.clear();
		
		//this is important, clear old static data
		CurrentAudio = null;
	}

	@Override
	public void resume () {
		final Values<Sound> sound = mSoundPool.values();
		for (Sound s : sound) {
			s.resume();
		}

		for (Music m : mPlayingMusicList) {
			m.play();
		}
	}

	@Override
	public void pause () {
		final Values<Sound> sound = mSoundPool.values();
		for (Sound s : sound) {
			s.pause();
		}

		for (Music m : mPlayingMusicList) {
			m.pause();
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void finishedLoading (AssetManager assetManager, String fileName, Class type) {
		if (type.equals(Sound.class)) {
			mSoundPool.put(fileName, assetManager.get(fileName, Sound.class));
		} else if (type.equals(Music.class)) {
			Music m = assetManager.get(fileName, Music.class);
			m.setOnCompletionListener(this);
			mMusicPool.put(fileName, m);
		}
	}

	@Override
	public void onCompletion (Music music) {
		mPlayingMusicList.removeValue(music, true);
	}

}
