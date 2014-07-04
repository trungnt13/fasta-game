
package org.tntstudio.interfaces;


/** @author trungnt13 */
public interface AssetsListener {
	public void loadingCompleted ();

	public void loadingOnProcess (int currentLoaded, int maxAssets);
}
