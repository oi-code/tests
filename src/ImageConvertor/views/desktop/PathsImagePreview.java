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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ImageConvertor.core.Controller;

@SuppressWarnings(value = "serial")
public class PathsImagePreview extends JPanel
		implements MouseMotionListener, MouseListener, MouseWheelListener, WindowListener {

	Controller controller;
	List<List<Point>> forDrawContainer;
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

	public PathsImagePreview(Controller controllerr) {
		controller = controllerr;
		// forDraw = controller.getPointsList();
		this.forDrawContainer = controller.getPathsPointList();
		s = controller.getChunkSize();
		figure = controller.getFigure();
		width = controller.getImageWidth();
		height = controller.getImageHeight();
		j = new JFrame();
		j.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		j.setTitle("Preview image");
		j.setSize(controller.getImageWidth(), controller.getImageHeight() + 55);
		j.setLocationRelativeTo(null);

		boxes = new ArrayList<JCheckBox>();

		layers = new JFrame("Path chooser");
		layers.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		layers.setLocation(j.getLocation().x - 210, j.getLocation().y);
		layers.setLayout(new GridLayout(10, 3));
		layers.setSize(225, 300);
		for (int i = 0; i < forDrawContainer.size(); i++) {
			JCheckBox temp = new JCheckBox("Path " + (i + 1));
			temp.setFocusable(false);
			boxes.add(temp);
			layers.add(temp);
		}
		int select = boxes.size() / 4;
		boxes.get(select).setSelected(true);
		layers.setVisible(true);
	}
	
	private void sortPoints() {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (forDrawContainer == null) {
			return;
		}
		setOpaque(false);
		layerCount = -1;
		setSize(width, height);
		Graphics2D g2 = (Graphics2D) g;
		Color c = new Color(0f, 0f, 0f, 0f);
		g2.setColor(c);
		g2.fillRect(0, 0, width, height);
		g2.setColor(Color.GRAY);
		g2.setStroke(new BasicStroke(controller.getStroke()));
		controller.getFinalList().clear();
		for (List<Point> list : forDrawContainer) {
			// System.out.println(list.size());
			layerCount++;
			repaint();
			if (!boxes.get(layerCount).isSelected())
				continue;
			controller.getFinalList().add(list);
			Point prev = list.get(0);
			for (Point cur : list) {
				int prevX = prev.x / count;
				int prevY = prev.y / count;
				int curX = cur.x / count;
				int curY = cur.y / count;
				// if (prev.distance(cur) < 20)
				g2.drawLine(prevX, prevY, curX, curY);
				prev = cur;
			}
		}

	}

	public void showImage() {

		j.addMouseListener(this);
		j.addMouseMotionListener(this);
		j.addMouseWheelListener(this);
		j.addWindowListener(this);

		j.add(this);
		j.setVisible(true);
	}

	public void removeListeners() {
		forDrawContainer.clear();
		forDrawContainer = null;
		j.removeMouseListener(this);
		j.removeMouseMotionListener(this);
		j.removeMouseWheelListener(this);
		j.removeWindowListener(this);
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
