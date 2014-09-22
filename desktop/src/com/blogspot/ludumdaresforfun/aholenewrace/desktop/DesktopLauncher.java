package com.blogspot.ludumdaresforfun.aholenewrace.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.blogspot.ludumdaresforfun.aholenewrace.AHoleNewRace;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new AHoleNewRace(), config);
	}
}
