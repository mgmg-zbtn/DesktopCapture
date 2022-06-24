package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class Panel extends JPanel implements Runnable, MouseListener, MouseMotionListener {

	BufferedImage image;
	Point pos;
	Point startPos;
	Point endPos;
	boolean isStart;
	Dimension screenSize;
	
	CaptureNotify cn;

	Thread thread;
	
	public Panel() {
		
		pos = new Point();
		startPos = new Point();
		endPos = new Point();
		isStart = false;

		addMouseListener(this);
		addMouseMotionListener(this);

		setDoubleBuffered(true);
		thread = new Thread(this);
		thread.start();
	}
	
	public void init(BufferedImage image) {
		this.image = image;
		startPos = new Point();
		endPos = new Point();
		isStart = false;
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		
		while(true) {
			repaint();
			
			try {
				Thread.sleep(20);
			} catch(InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		
		g.drawImage(image, 0, 0, null);
		
		if (isStart) {
			g.setColor(Color.BLUE);
			
			g.drawLine(0, pos.y, screenSize.width, pos.y);	// X
			g.drawLine(pos.x, 0, pos.x, screenSize.height);	// Y

			g.setColor(Color.RED);
			g.drawLine(0, startPos.y, screenSize.width, startPos.y);	// X
			g.drawLine(startPos.x, 0, startPos.x, screenSize.height);	// Y

		} else {
			g.setColor(Color.RED);
			g.drawLine(0, pos.y, screenSize.width, pos.y);	// X
			g.drawLine(pos.x, 0, pos.x, screenSize.height);	// Y
		}
		
		
		int sx = 10;
		int sy = 10;
		if (screenSize.width * 0.5< pos.x) sx = -120;
		if (screenSize.height * 0.5 < pos.y) sy = -10;

		// 座標文字背景色
		g.setColor(new Color(255, 255, 255, 200));
		g.fillRect(pos.x + sx -10, pos.y + sy -10, 130, 15);
		
		// 座標文字
		g.setColor(Color.BLACK);
		String str = "X = " + pos.x + " : Y = " + pos.y;
		g.drawString(str , pos.x + sx, pos.y + sy);
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		pos.x = e.getX();
		pos.y = e.getY();
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		pos.x = e.getX();
		pos.y = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		startPos = new Point(e.getPoint());
		isStart = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		endPos = new Point(e.getPoint());
		cn.call(image, startPos, endPos);
		thread = null;
	}
}
