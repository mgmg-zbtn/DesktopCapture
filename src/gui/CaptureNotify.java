package gui;

import java.awt.Point;
import java.awt.image.BufferedImage;

public interface CaptureNotify {
	
	public void call(BufferedImage image, Point startPos, Point endPos);

}
