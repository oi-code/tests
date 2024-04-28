package ImageConvertor.views.desktop;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;

import ImageConvertor.core.Controller;
import ImageConvertor.data.Chunk;

@SuppressWarnings("serial")
public class PathsImagePreview_v2 extends AbstractImagePreview {

	public PathsImagePreview_v2(Controller controller) {
		super(controller);
		controller.setPathPreview(this);
	}

	@Override
	protected void setDrawingContainers() {
		this.allLayersContainer = controller.getPathsPointList();
		//this.chosedLayersContainer = controller.getFinalList();
	}

	@Override
	protected void updateImage() {		
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = newImage.createGraphics();
		Color c = new Color(0f, 0f, 0f, 0f);
		g2.setColor(c);
		g2.fillRect(0, 0, width, height);
		g2.setColor(Color.GRAY);
		g2.setStroke(new BasicStroke(1));
		int layer = -1;
		//controller.getFinalList().clear();
		for (List<Chunk> list : allLayersContainer) {
			layer++;
			if (!boxes.get(layer).isSelected())
				continue;
			if (list.size() == 0)
				continue;
			//controller.getFinalList().add(list);
			Chunk prev = list.get(0);
			for (Chunk cur : list) {
				if (controller.isRandom()) {
					int prevX = prev.startPoint.x / count;
					int prevY = prev.startPoint.y / count;
					int curX = cur.startPoint.x / count;
					int curY = cur.startPoint.y / count;
					g2.drawLine(prevX, prevY, curX, curY);
					// g2.drawLine(curX, curY, cur.startPoint.x/count, cur.startPoint.y/count);
					/*
					 * prevX = prev.startPoint.x / count;
					 * prevY = prev.startPoint.y / count;
					 * curX = cur.endPoint.x / count;
					 * curY = cur.endPoint.y / count;
					 * g2.drawLine(prevX, prevY, curX, curY);
					 */
				} else {
					/*
					 * int prevX = prev.chunkPosition.x * chunkSize / count;
					 * int prevY = prev.chunkPosition.y * chunkSize / count;
					 * int curX = cur.chunkPosition.x * chunkSize / count;
					 * int curY = cur.chunkPosition.y * chunkSize / count;
					 */
					int prevX = prev.endPoint.x / count;
					int prevY = prev.endPoint.y / count;
					int curX = cur.endPoint.x / count;
					int curY = cur.endPoint.y / count;
					g2.drawLine(prevX, prevY, curX, curY);
				}
				// int prevX = prev.endPoint.x / count;
				// int prevY = prev.endPoint.y / count;
				// int curX = cur.startPoint.x / count;
				// int curY = cur.startPoint.y / count;
				// int prevX = prev.chunkPosition.x * chunkSize / count;
				// int prevY = prev.chunkPosition.y * chunkSize / count;
				// int curX = cur.chunkPosition.x * chunkSize / count;
				// int curY = cur.chunkPosition.y * chunkSize / count;
				// if (prev.chunkPosition.distance(cur.chunkPosition) < controller.getChunkSize()/4)
				// g2.drawLine(prevX, prevY, curX, curY);

				prev = cur;
			}
		}
		g2.dispose();
		currentImage = newImage;
		imageContainer.setIcon(new ImageIcon(newImage));
	}

}
