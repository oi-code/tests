package ImageConvertor.views.desktop;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.management.RuntimeErrorException;
import javax.swing.ImageIcon;

import ImageConvertor.core.Controller;
import ImageConvertor.data.Chunk;

@SuppressWarnings("serial")
public class ParsedImagePreview_v2 extends AbstractImagePreview {

	public ParsedImagePreview_v2(Controller controller) {
		super(controller);		
		controller.setImagePreview(this);
	}

	@Override
	protected void setDrawingSources() {
		this.allLayersContainer = new ArrayList<List<Chunk>>(controller.getAllLayers());
		this.chosedLayersContainer = controller.getChosedLayersForDraw();
	}

	@Override
	protected void updateImage() {
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = newImage.createGraphics();
		Color c = new Color(0f, 0f, 0f, 0f);
		g2.setColor(c);
		g2.fillRect(0, 0, width, height);
		g2.setColor(new Color(0, 191, 255));
		g2.setStroke(new BasicStroke(1));
		int layer = -1;
		chosedLayersContainer.clear();
		for (List<Chunk> out : allLayersContainer) {
			layer++;
			if (!boxes.get(layer).isSelected()) {
				continue;
			}
			if (out.size() == 0) {
				continue;
			}
			chosedLayersContainer.add(out);
			// Chunk outChunk = out.get(0);
			for (Chunk inner : out) {
				int x1 = inner.startPoint.x / count;
				int y1 = inner.startPoint.y / count;
				int x2 = inner.endPoint.x / count;
				int y2 = inner.endPoint.y / count;
				// int x3 = outChunk.startPoint.x / count;
				// int y3 = outChunk.startPoint.y / count;
				// g2.drawLine(x1, y1, x2, y2);
				// g2.drawLine(x1, y1, x3, y3);
				// outChunk = inner;
				if (controller.getLocaleText("line").equals(figure)) {
					if (controller.getProcessor().equals("Lumin") && chunkSize > 2) {
						g2.fillRect(x1, y1, 2, 2);
					} else {
						g2.drawLine(x1, y1, x2, y2);
					}
				} else if (controller.getLocaleText("circle").equals(figure)) {
					int rnd = (int) inner.getLength() + ThreadLocalRandom.current().nextInt(chunkSize);
					g2.drawArc(x1, y1, rnd, rnd, 0, 360);
				} else if (controller.getLocaleText("x").equals(figure)) {
					g2.drawLine(x1, y1, x2, y2);
					g2.drawLine(x1, y2, x2, y1);
				} else {
					throw new RuntimeException("error during drawing");
				}
			}
		}
		g2.dispose();
		currentImage = newImage;
		imageContainer.setIcon(new ImageIcon(newImage));
	}
}