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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
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
	JFrame mainPanel;
	JScrollPane jsp;
	Point p;
	JFrame layersBox;
	List<JCheckBox> boxes;
	ThreadLocalRandom tlr = ThreadLocalRandom.current();
	int width;
	int height;
	int count = 1;
	int layerCount = -1;

	public PathsImagePreview(Controller controllerr) {
		controller = controllerr;
		this.forDrawContainer = controller.getPathsPointList();
		s = controller.getChunkSize();
		figure = controller.getFigure();
		width = controller.getImageWidth();
		height = controller.getImageHeight();
		mainPanel = new JFrame();
		mainPanel.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainPanel.setTitle(controller.getLocaleText("path_prev"));
		mainPanel.setSize(controller.getImageWidth(), controller.getImageHeight() + 55);
		mainPanel.setLocationRelativeTo(null);
		getLayersBoxFrame();
	}

	private void sortPoints() {

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (forDrawContainer == null) {
			layersBox.setVisible(false);
			mainPanel.setVisible(false);
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

		mainPanel.addMouseListener(this);
		mainPanel.addMouseMotionListener(this);
		mainPanel.addMouseWheelListener(this);
		mainPanel.addWindowListener(this);

		mainPanel.add(this);

		if (controller.isCanceled()) {
			mainPanel.setVisible(false);
			layersBox.setVisible(false);
		}
	}

	public void removeListeners() {
		forDrawContainer.clear();
		forDrawContainer = null;
		mainPanel.removeMouseListener(this);
		mainPanel.removeMouseMotionListener(this);
		mainPanel.removeMouseWheelListener(this);
		mainPanel.removeWindowListener(this);
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
			height = (controller.getImageHeight() + 55) / count;
		} else if (e.getWheelRotation() < 0) {
			count = 1;
			width = controller.getImageWidth();
			height = controller.getImageHeight() + 55;
		}
		mainPanel.setSize(width, height);
		repaint();
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		layersBox.dispose();
		layersBox = null;
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
	

	private JButton getButtonForLayers() {
		JButton button = new JButton(controller.getLocaleText("select_all"));
		button.setPreferredSize(new Dimension(layersBox.getWidth(), (int)(layersBox.getHeight()*0.1f)));
		button.setFocusable(false);
		button.addActionListener(e -> {
			if (button.getText().equals(controller.getLocaleText("select_all"))) {
				button.setText(controller.getLocaleText("unselect_all"));
				for (JCheckBox box : boxes) {
					box.setSelected(true);
				}
			} else {
				button.setText(controller.getLocaleText("select_all"));
				for (JCheckBox box : boxes) {
					box.setSelected(false);
				}
			}
		});
		return button;
	}

	private JFrame getLayersBoxFrame() {

		layersBox = new JFrame(controller.getLocaleText("layer_csr"));
		layersBox.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		layersBox.setLocation(mainPanel.getLocation().x - 240, mainPanel.getLocation().y);
		layersBox.setLayout(new BorderLayout());
		layersBox.setSize(250, 300);// (forDraw.size()+1)*40);
		layersBox.setLocation(mainPanel.getLocation().x - 240, mainPanel.getLocation().y + 333);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(10, 3));
		boxes = new ArrayList<JCheckBox>();
		for (int i = 0; i < forDrawContainer.size(); i++) {
			JCheckBox temp = new JCheckBox(controller.getLocaleText("layer") + ": " + (i + 1));
			temp.setFocusable(false);
			boxes.add(temp);
			topPanel.add(temp);
		}
		int select = boxes.size() / 4;
		boxes.get(select).setSelected(true);
		if (!controller.isCanceled()) {
			layersBox.setVisible(true);
			mainPanel.setVisible(true);
		}

		JPanel buttonContainer = new JPanel();
		buttonContainer.add(getButtonForLayers());

		layersBox.add(topPanel, BorderLayout.CENTER);
		layersBox.add(buttonContainer, BorderLayout.SOUTH);

		return layersBox;
	}
}
