package main;

import javax.swing.SwingUtilities;

import gui.GUI;

/**
 * 
 * 設定ファイルのこと考えてなかった
 * 
 * @since 2019/04/13
 */
public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new GUI();
			}
		});
	}
}
