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
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ImageConvertor.core.Controller;
import ImageConvertor.data.Chunk;

@SuppressWarnings(value = "serial")
public class ParsedImagePreview extends JPanel
		implements MouseMotionListener, MouseListener, MouseWheelListener, WindowListener {

	Controller controller;
	List<List<Chunk>> allLayersContainer;
	Short s;
	String figure;
	JFrame mainPanel;
	JScrollPane jsp;
	Point p;
	JFrame layersBox;
	List<JCheckBox> boxes;
	ThreadLocalRandom tlr = ThreadLocalRandom.current();
	JButton but;
	int width;
	int height;
	int count = 1;
	int layerCount = -1;
	AtomicBoolean isImageChanged = new AtomicBoolean(false);

	public ParsedImagePreview(Controller controllerr) {

		controller = controllerr;
		allLayersContainer = controllerr.getAllLayers();
		s = controller.getChunkSize();
		figure = controller.getFigure();
		width = controller.getImageWidth();
		height = controller.getImageHeight();

		mainPanel = new JFrame();
		mainPanel.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainPanel.setTitle(controller.getLocaleText("img_prev"));
		mainPanel.setSize(controller.getImageWidth(), controller.getImageHeight() + 55);
		mainPanel.setLocationRelativeTo(null);

		getLayersBoxFrame();

		/*
		 * JLabel j = new JLabel();
		 * ImageIcon icon = createImage();
		 * j.setIcon(icon);
		 * add(j);
		 * 
		 * Thread helper = new Thread(() -> {
		 * while (mainPanel.isVisible()) {
		 * if (isImageChanged.get()) {
		 * j.setIcon(createImage());
		 * isImageChanged.set(false);
		 * j.repaint();
		 * System.out.println("image changed");
		 * }
		 * try {
		 * TimeUnit.MILLISECONDS.sleep(300);
		 * } catch (InterruptedException e) {
		 * e.printStackTrace();
		 * }
		 * }
		 * });
		 * helper.setDaemon(true);
		 * helper.start();
		 */
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (controller.isCanceled() || allLayersContainer == null) {
			layersBox.setVisible(false);
			mainPanel.setVisible(false);
			return;
		}

		if (controller.isProcessWindowShowed()) {
			for (JCheckBox box : boxes) {
				box.setEnabled(false);
			}
			but.setEnabled(false);
		} else {
			for (JCheckBox box : boxes) {
				box.setEnabled(true);
			}
			but.setEnabled(true);
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
		if (controller.isCanceled() || allLayersContainer == null) {
			layersBox.setVisible(false);
			mainPanel.setVisible(false);
			return;
		}
		controller.getChosedLayersForDraw().clear();
		for (List<Chunk> currentDrawingList : allLayersContainer) {

			layerCount++;
			repaint();
			if (!boxes.get(layerCount).isSelected()) {
				continue;
			}
			controller.getChosedLayersForDraw().add(currentDrawingList);
			for (Chunk innerCurrentPoints : currentDrawingList) {

				// if (innerCurrentPoints.direction == Direction.STUB || innerCurrentPoints.getLength() < s /
				// 2){
				// continue;
				// }

				int x1 = innerCurrentPoints.startPoint.x / count;
				int y1 = innerCurrentPoints.startPoint.y / count;
				int x2 = innerCurrentPoints.endPoint.x / count;
				int y2 = innerCurrentPoints.endPoint.y / count;
				if (controller.getLocaleText("line").equals(figure)) {
					if (controller.getProcessor().equals("Lumin") && s > 2) {
						g2.fillRect(x1, y1, 2, 2);
					} else {
						g2.drawLine(x1, y1, x2, y2);
					}
				} else if (controller.getLocaleText("circle").equals(figure)) {
					int rnd = (int) innerCurrentPoints.getLength() + ThreadLocalRandom.current().nextInt(s);
					g2.drawArc(x1, y1, rnd, rnd, 0, 360);
				} else if (controller.getLocaleText("x").equals(figure)) {
					g2.drawLine(x1, y1, x2, y2);
					g2.drawLine(x1, y2, x2, y1);
				} else {
				}
			}
		}

	}

	public void showImage() {

		/*
		 * BufferedImage img = new BufferedImage(controller.getImageWidth(), controller.getImageHeight(),
		 * BufferedImage.TYPE_INT_ARGB);
		 * Graphics2D g2 = img.createGraphics();
		 * setSize(controller.getImageWidth(), controller.getImageHeight());
		 * printAll(g2);
		 * g2.dispose();
		 * new LayeredPaneExample(img);
		 */

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
		allLayersContainer.clear();
		allLayersContainer = null;
		mainPanel.removeMouseListener(this);
		mainPanel.removeMouseMotionListener(this);
		mainPanel.removeMouseWheelListener(this);
		mainPanel.removeWindowListener(this);
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
		JOptionPane.showMessageDialog(null, controller.getLocaleText("image_saved"),
				controller.getLocaleText("saved_word_done"), JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		try {
			int thisX = getLocation().x;
			int thisY = getLocation().y;

			int xMoved = (thisX + e.getX()) - (thisX + p.x);
			int yMoved = (thisY + e.getY()) - (thisY + p.y);

			int X = thisX + xMoved / 10;
			int Y = thisY + yMoved / 10;

			setLocation(X, Y);
		} catch (NullPointerException exc) {
		}
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
		button.setPreferredSize(new Dimension(layersBox.getWidth(), (int) (layersBox.getHeight() * 0.1f)));
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
			isImageChanged.set(true);
		});
		but = button;
		return button;
	}

	private JFrame getLayersBoxFrame() {

		layersBox = new JFrame(controller.getLocaleText("layer_csr"));
		layersBox.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		layersBox.setLocation(mainPanel.getLocation().x - 240, mainPanel.getLocation().y);
		layersBox.setLayout(new BorderLayout());
		layersBox.setSize(250, 300);// (forDraw.size()+1)*40);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(10, 3));
		boxes = new ArrayList<JCheckBox>();
		for (int i = 0; i < allLayersContainer.size(); i++) {
			JCheckBox temp = new JCheckBox(controller.getLocaleText("layer") + ": " + (i + 1));
			temp.setFocusable(false);
			boxes.add(temp);
			topPanel.add(temp);
			temp.addActionListener(event -> {
				isImageChanged.set(true);
			});
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

/*
 * class LayeredPaneExample extends JFrame {
 * 
 * BufferedImage im;
 * 
 * public LayeredPaneExample(BufferedImage im) {
 * super("Preview Image");
 * this.im = im;
 * setSize(600, 600);
 * JLayeredPane pane = getLayeredPane();
 * JLayeredPane formaPanel = new JLayeredPane();
 * formaPanel.setBounds(0, 0, 600, 600);
 * formaPanel.setLayout(new BoxLayout(formaPanel, BoxLayout.Y_AXIS));
 * formaPanel.setLayout(null);
 * BufferedImage img;
 * JLabel label;
 * JScrollPane scroll;
 * try {
 * img = im;
 * ImageIcon icon = new ImageIcon(img);
 * label = new JLabel(icon);
 * 
 * scroll = new JScrollPane(label, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
 * JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
 * formaPanel.add(scroll);
 * // without THIS DON'T WORK!!!!
 * scroll.setBounds(1, 30, 585, 562); // !!!!!!!!!!!!!!!!!!<-------------------------
 * 
 * pane.add(formaPanel);
 * } catch (Exception e) {
 * e.printStackTrace();
 * }
 * setLocationRelativeTo(null);
 * setResizable(false);
 * setVisible(true);
 * }
 * }
 */