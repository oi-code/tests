package ImageConvertor.views.desktop;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ImageConvertor.core.Controller;
import ImageConvertor.core.Direction;
import ImageConvertor.core.Points;

@SuppressWarnings(value = "serial")
public class ParsedImagePreview extends JPanel
		implements MouseMotionListener, MouseListener, MouseWheelListener, WindowListener {

	Controller controller;
	List<List<Points>> allLayersContainer;
	Short s;
	String figure;
	JFrame j;
	JScrollPane jsp;
	Point p;
	JFrame layers;
	List<JCheckBox> boxes;
	ThreadLocalRandom tlr = ThreadLocalRandom.current();
	int width;
	int height;
	int count = 1;
	int layerCount = -1;

	public ParsedImagePreview(Controller controllerr) {
		controller = controllerr;
		// forDraw = controller.getPointsList();
		allLayersContainer = controllerr.getAllLayersContainer();
		s = controller.getChunkSize();
		figure = controller.getFigure();
		width = controller.getImageWidth();
		height = controller.getImageHeight();
		j = new JFrame();
		j.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		j.setTitle("Preview image");
		/*
		 * if (width > height) {
		 * j.setSize(720, 480);
		 * } else {
		 * j.setSize(480, 720);
		 * }
		 */
		j.setSize(controller.getImageWidth(), controller.getImageHeight() + 55);
		j.setLocationRelativeTo(null);

		boxes = new ArrayList<JCheckBox>();

		layers = new JFrame("Layer chooser");
		layers.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		layers.setLocation(j.getLocation().x - 210, j.getLocation().y);
		// layers.setLocationRelativeTo(null);
		// layers.setLayout(new GridLayout(forDraw.size(), 1));
		layers.setLayout(new GridLayout(10, 3));
		layers.setSize(225, 300);// (forDraw.size()+1)*40);
		for (int i = 0; i < allLayersContainer.size(); i++) {
			JCheckBox temp = new JCheckBox("Layer " + (i + 1));
			temp.setFocusable(false);
			boxes.add(temp);
			layers.add(temp);
		}
		int select = boxes.size() / 4;
		boxes.get(select).setSelected(true);
		layers.setVisible(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setOpaque(false);
		layerCount = -1;
		setSize(width, height);
		Graphics2D g2 = (Graphics2D) g;
		Color c = new Color(0f, 0f, 0f, 0f);
		g2.setColor(c);
		g2.fillRect(0, 0, width, height);
		g2.setColor(Color.GRAY);
		g2.setStroke(new BasicStroke(controller.getStroke()));
		if (allLayersContainer == null) {
			return;
		}
		controller.getForDrawContainer().clear();
		for (List<Points> currentDrawingList : allLayersContainer) {
			layerCount++;
			repaint();
			if (!boxes.get(layerCount).isSelected()) {
				continue;
			}
			controller.getForDrawContainer().add(currentDrawingList);

			for (Points innerCurrentPoints : currentDrawingList) {

				if (innerCurrentPoints.direction == Direction.STUB || innerCurrentPoints.getLength() < s / 2) {
					continue;
				}

				int x1 = innerCurrentPoints.startPoint.x / count;
				int y1 = innerCurrentPoints.startPoint.y / count;
				int x2 = innerCurrentPoints.endPoint.x / count;
				int y2 = innerCurrentPoints.endPoint.y / count;

				switch (figure) {
				case "Circle": {
					int rnd = (int) innerCurrentPoints.getLength() + ThreadLocalRandom.current().nextInt(s);
					g2.drawArc(x1, y1, rnd, rnd, 0, 360);
					break;
				}
				case "Line": {
					g2.drawLine(x1, y1, x2, y2);

					break;
				}
				case "X": {
					g2.drawLine(x1, y1, x2, y2);
					g2.drawLine(x1, y2, x2, y1);
					break;
				}
				default:
					break;
				}
			}

		}
	}

	public void showImage() {
		/*
		 * BufferedImage img = new BufferedImage(controller.getImageWidth(),
		 * controller.getImageHeight(),
		 * BufferedImage.TYPE_INT_ARGB);
		 * Graphics2D g2 = img.createGraphics();
		 * setSize(controller.getImageWidth(), controller.getImageHeight());
		 * printAll(g2);
		 * g2.dispose();
		 * new LayeredPaneExample(img);
		 */

		j.addMouseListener(this);
		j.addMouseMotionListener(this);
		j.addMouseWheelListener(this);
		j.addWindowListener(this);

		j.add(this);
		j.setVisible(true);
	}

	public void removeListeners() {
		allLayersContainer.clear();
		allLayersContainer = null;
		j.removeMouseListener(this);
		j.removeMouseMotionListener(this);
		j.removeMouseWheelListener(this);
		j.removeWindowListener(this);
	}

	public void saveImage() {
		width = controller.getImageWidth();
		height = controller.getImageHeight();
		count = 1;
		BufferedImage img = new BufferedImage(controller.getImageWidth(), controller.getImageHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = img.createGraphics();
		setSize(controller.getImageWidth(), controller.getImageHeight());
		printAll(g2);
		g2.dispose();
		try {
			;
			Path s = Paths.get(View.DESKTOP_PATH.toString() + "\\" + controller.getFileName() + ".png");

			Files.deleteIfExists(s);
			Files.createFile(s);

			ImageIO.write(img, "png", s.toFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "PNG image saved!", "Done", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int thisX = getLocation().x;
		int thisY = getLocation().y;

		int xMoved = (thisX + e.getX()) - (thisX + p.x);
		int yMoved = (thisY + e.getY()) - (thisY + p.y);

		int X = thisX + xMoved / 10;
		int Y = thisY + yMoved / 10;

		setLocation(X, Y);
		// repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		p = e.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() > 0) {
			count++;
			width = controller.getImageWidth() / count;
			height = controller.getImageHeight() / count;
		} else if (e.getWheelRotation() < 0) {
			count = 1;
			width = controller.getImageWidth();
			height = controller.getImageHeight();
		}
		j.setSize(width, height);
		repaint();
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		layers.disable();
		layers.dispose();
		layers = null;
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}
}

class LayeredPaneExample extends JFrame {

	BufferedImage im;

	public LayeredPaneExample(BufferedImage im) {
		super("Preview Image");
		this.im = im;
		setSize(600, 600);
		JLayeredPane pane = getLayeredPane();
		JLayeredPane formaPanel = new JLayeredPane();
		formaPanel.setBounds(0, 0, 600, 600);
		formaPanel.setLayout(new BoxLayout(formaPanel, BoxLayout.Y_AXIS));
		formaPanel.setLayout(null);
		BufferedImage img;
		JLabel label;
		JScrollPane scroll;
		try {
			img = im;
			ImageIcon icon = new ImageIcon(img);
			label = new JLabel(icon);

			scroll = new JScrollPane(label, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			formaPanel.add(scroll);
			// without THIS DON'T WORK!!!!
			scroll.setBounds(1, 30, 585, 562); // !!!!!!!!!!!!!!!!!!<-------------------------

			pane.add(formaPanel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}
}
