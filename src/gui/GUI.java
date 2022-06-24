package gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;


public class GUI extends JFrame implements CaptureNotify {
	
	Timer timer;
	JComboBox combo;
	Viewer viewer;
	JCheckBox checkFullScreenCap;
	Dimension screenSize;
	GridBagLayout gbl;
	
	File configFile;
	
	public GUI () {

		timer = new Timer();
		viewer = Viewer.getInstance();
		viewer.panel.cn = this;

		screenSize = new Dimension();
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		viewer.panel.screenSize = screenSize;
		
		gbl = new GridBagLayout();
		setLayout(gbl);

		//--------------------------------------------------------------
		
		combo = new JComboBox();
		combo.setEditable(true);

		// release fix
		configFile = new File(System.getProperty("user.dir") + "\\conf.txt");
//		configFile = new File("E:\\Eclipse\\tools\\DesktopCapture\\release\\conf.txt");

		if (configFile.exists()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));
				String line;
				while ((line = br.readLine()) != null) {
					File file = new File(line);
					if (file.exists()) {
						combo.addItem(line);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			try {
				configFile.createNewFile();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		
		//--------------------------------------------------------------
		
		JButton capture = new JButton("CAP");
		capture.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				minimize();
				timer.schedule(new CaptureTask(), 500);
			}
		});
		
		JButton addUrl = new JButton("ADD");
		addUrl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File file = new File(combo.getSelectedItem()+"");
				if (file.exists()) {
					combo.addItem(combo.getSelectedItem());
				}
			}
		});
		
		JButton delUrl = new JButton("DEL");
		delUrl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (1 <= combo.getItemCount()) {
					combo.removeItemAt(combo.getSelectedIndex());
				}
			}
		});

		JButton saveConfigButton = new JButton("SAVE");
		saveConfigButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String temp = configFile.getAbsolutePath();
				
				configFile.delete();
				BufferedWriter bw = null;

				try {
					configFile = new File(temp);
					configFile.createNewFile();
					bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile)));
					for (int i = 0; i < combo.getItemCount(); i++) {
						bw.write(combo.getItemAt(i) + "");
						bw.newLine();
					}
				} catch (IOException e4) {
					e4.printStackTrace();
				} finally {
					try {
						bw.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		checkFullScreenCap = new JCheckBox("full Sc");
		
		addGrid(capture, 0, 0, 1, 1);
		addGrid(addUrl, 1, 0, 1, 1);
		addGrid(delUrl, 2, 0, 1, 1);
		addGrid(saveConfigButton, 3, 0, 1, 1);
		addGrid(checkFullScreenCap, 4, 0, 1, 1);

		//--------------------------------------------------------------
		
		addGrid(combo, 0, 1, 5, 1);
		
		setSize(new Dimension(300, 100));
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}
	
	private void addGrid(JComponent c, int x, int y, int w, int h) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbl.setConstraints(c, gbc);
		add(c);
	}
	
	private void minimize() {
		setState(Frame.ICONIFIED);
	}
	
	@Override
	public void call(BufferedImage image, Point startPos, Point endPos) {
		
		Point subStartPos = new Point(Math.min(startPos.x, endPos.x), Math.min(startPos.y, endPos.y));
		Point subEndPos = new Point(Math.max(startPos.x, endPos.x), Math.max(startPos.y, endPos.y));

		int subWidth = subEndPos.x - subStartPos.x;
		int subHeight = subEndPos.y - subStartPos.y;
		
		if (1 < subWidth && 1 < subHeight) {
			BufferedImage subImage = image.getSubimage(subStartPos.x, subStartPos.y, subWidth, subHeight);
			saveCapture(subImage);
		}
		viewer.close();
		setState(Frame.NORMAL);
	}
	
	private void saveCapture(BufferedImage image) {
		try {
			File saveFolder = new File(combo.getSelectedItem() + "\\" + System.nanoTime() + ".jpg");
			ImageIO.write(image, "jpg", saveFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class CaptureTask extends TimerTask {

		@Override
		public void run() {
			BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			
			try {
				Robot robot = new Robot();
				image = robot.createScreenCapture(new Rectangle(screenSize));
			} catch(Exception e3) {
				e3.printStackTrace();
			}
			
			if (checkFullScreenCap.isSelected()) {
				saveCapture(image);
				setState(Frame.NORMAL);
			} else {
				viewer.init(image);
			}
		}
	}
}
