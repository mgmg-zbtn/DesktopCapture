package gui;

import java.awt.Cursor;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Viewer extends JFrame implements KeyListener {

	private static Viewer viewer;
	
	Panel panel;
	GraphicsDevice device;
	
	public static Viewer getInstance() {
		if (viewer == null) viewer = new Viewer();
		return viewer;
	}
	
	private Viewer() {
		
		addKeyListener(this);
		panel = new Panel();
		getContentPane().add(panel);
		
		// hidden of window frame
		setUndecorated(true);
	}
	
	public void init(BufferedImage image) {
		// hidden of mouse pointer
		Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(
				new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
				new Point(), "");
		setCursor(cursor);

		// full screen
		device = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();
		device.setFullScreenWindow(this);
		
		panel.init(image);
		setVisible(true);
	}
	
	public void close() {
		device.setFullScreenWindow(null);
		setVisible(false);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			close();
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
}
