package ImageConvertor.core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;

import ImageConvertor.data.Chunk;
import ImageConvertor.views.desktop.AbstractImagePreview;

public class Bhuas extends AbstractImagePreview {

	public Bhuas(Controller controller) {
		super(controller);
		System.out.println(this.getClass().getSimpleName() + " created");
	}

	@Override
	protected void updateImage() {
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = newImage.createGraphics();
		Color c = new Color(0f, 0f, 0f, 0f);
		g2.setColor(c);
		g2.fillRect(0, 0, width, height);
		g2.setColor(Color.BLUE);
		g2.setStroke(new BasicStroke(1));
		int layer = -1;

		for (List<Chunk> out : allLayersContainer) {
			layer++;
			if (!boxes.get(layer).isSelected()) {
				continue;
			}
			for (Chunk inner : out) {
				int x1 = inner.startPoint.x / count;
				int y1 = inner.startPoint.y / count;
				int x2 = inner.endPoint.x / count;
				int y2 = inner.endPoint.y / count;
				g2.drawLine(x1, y1, x2, y2);
			}
		}
		g2.dispose();
		currentImage = newImage;
		imageContainer.setIcon(new ImageIcon(newImage));
	}
}