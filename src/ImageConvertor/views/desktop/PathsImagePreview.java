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
import ImageConvertor.data.Chunk;

@SuppressWarnings(value = "serial")
public class PathsImagePreview extends JPanel
		implements MouseMotionListener, MouseListener, MouseWheelListener, WindowListener {

	Controller controller;
	List<List<Chunk>> forDrawContainer;
	Short s;
	String figure;
	JFrame mainPanel;
	JScrollPane jsp;
	Point p;
	JFrame pathsBox;
	List<JCheckBox> boxes;
	ThreadLocalRandom tlr = ThreadLocalRandom.current();
	int width;
	int height;
	int count = 1;
	int layerCount = -1;

	public PathsImagePreview(Controller controllerr) {
		controller = controllerr;
		/**
		 * to avoid {@link CuncurentModificationException} in {@link #paintComponent()}
		 */
		this.forDrawContainer = new ArrayList<>(controller.getPathsPointList());
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

	@SuppressWarnings("unused")
	private void sortPoints() {

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (forDrawContainer == null) {
			pathsBox.setVisible(false);
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
		for (List<Chunk> list : forDrawContainer) {
			layerCount++;
			repaint();
			if (!boxes.get(layerCount).isSelected())
				continue;
			controller.getFinalList().add(list);
			Chunk prev = list.get(0);
			for (Chunk cur : list) {
				int prevX = prev.endPoint.x / count;
				int prevY = prev.endPoint.y / count;
				int curX = cur.startPoint.x / count;
				int curY = cur.startPoint.y / count;
				// if (prev.chunkPosition.distance(cur.chunkPosition) < controller.getChunkSize()/4)
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
			pathsBox.setVisible(false);
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
		pathsBox.dispose();
		pathsBox = null;
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
		button.setPreferredSize(new Dimension(pathsBox.getWidth(), (int) (pathsBox.getHeight() * 0.1f)));
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

		if (forDrawContainer == null) {
			return null;
		}
		pathsBox = new JFrame(controller.getLocaleText("path_csr"));
		pathsBox.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pathsBox.setLocation(mainPanel.getLocation().x - 240, mainPanel.getLocation().y);
		pathsBox.setLayout(new BorderLayout());
		pathsBox.setSize(250, 300);// (forDraw.size()+1)*40);
		pathsBox.setLocation(mainPanel.getLocation().x - 240, mainPanel.getLocation().y + 333);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(10, 3));
		boxes = new ArrayList<JCheckBox>();
		for (int i = 0; i < forDrawContainer.size(); i++) {
			JCheckBox temp = new JCheckBox(controller.getLocaleText("path") + ": " + (i + 1));
			temp.setFocusable(false);
			boxes.add(temp);
			topPanel.add(temp);
		}
		int select = boxes.size() / 4;
		boxes.get(select).setSelected(true);
		if (!controller.isCanceled()) {
			pathsBox.setVisible(true);
			mainPanel.setVisible(true);
		}

		JPanel buttonContainer = new JPanel();
		buttonContainer.add(getButtonForLayers());

		pathsBox.add(topPanel, BorderLayout.CENTER);
		pathsBox.add(buttonContainer, BorderLayout.SOUTH);

		return pathsBox;
	}
}
