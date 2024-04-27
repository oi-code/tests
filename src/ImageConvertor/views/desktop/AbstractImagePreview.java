package ImageConvertor.views.desktop;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;
import javax.swing.Icon;
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

public abstract class AbstractImagePreview extends JFrame
		implements MouseMotionListener, MouseWheelListener, MouseListener, WindowListener {

	protected List<List<Chunk>> allLayersContainer;
	protected BufferedImage currentImage = null;
	protected Short chunkSize;
	protected String figure;
	protected JLabel imageContainer;
	protected JPanel imagePanel;
	protected JFrame layersBox;
	protected JScrollPane jsp;
	protected Point p;
	protected List<JCheckBox> boxes;
	protected ThreadLocalRandom tlr = ThreadLocalRandom.current();
	protected JButton but;
	protected final int defaultImageWidth;
	protected final int defaultImageHeight;
	protected int width;
	protected int height;
	protected int count = 1;
	protected int layerCount = -1;
	protected AtomicBoolean isImageChanged = new AtomicBoolean(false);

	public AbstractImagePreview(Controller controller) {
		/*
		 * avoid {@link java.util.ConcurrentModificationException}
		 */
		this.allLayersContainer = new ArrayList<List<Chunk>>(controller.getAllLayers());
		this.chunkSize = controller.getChunkSize();
		this.figure = controller.getFigure();
		this.width = controller.getImageWidth() - (controller.getImageWidth() % chunkSize);
		this.height = controller.getImageHeight() - (controller.getImageHeight() % chunkSize);
		this.defaultImageHeight = height;
		this.defaultImageWidth = width;

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle(controller.getLocaleText("img_prev"));
		setSize(controller.getImageWidth(), controller.getImageHeight() + 55);
		setLocationRelativeTo(null);

		imageContainer = new JLabel();
		imagePanel = new JPanel();

		imagePanel.add(imageContainer);
		getContentPane().add(imagePanel);
		getLayersBoxFrame(controller);
		setListeners();
		startHelperThread();
		updateImage();

		System.out.println("created");
		setVisible(true);
	}

	abstract protected void updateImage();

	private void resizeImage() {
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = newImage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(currentImage, 0, 0, width, height, null);
		g2.dispose();
		currentImage = newImage;
		imageContainer.setIcon(new ImageIcon(newImage));
	}

	public void saveImage(Controller controller) {
		count = 1;
		width = defaultImageWidth;
		height = defaultImageHeight;
		updateImage();
		Graphics2D g2 = currentImage.createGraphics();
		Image image = ((ImageIcon) imageContainer.getIcon()).getImage();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		//setVisible(false);
		//layersBox.setVisible(false);
		try {
			Path s = Paths.get(View.DESKTOP_PATH.toString() + "\\" + controller.getFileName() + ".png");
			Files.deleteIfExists(s);
			Files.createFile(s);
			ImageIO.write(currentImage, "png", s.toFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, controller.getLocaleText("image_saved"),
				controller.getLocaleText("saved_word_done"), JOptionPane.INFORMATION_MESSAGE);
	}

	private JFrame getLayersBoxFrame(Controller controller) {

		layersBox = new JFrame(controller.getLocaleText("layer_csr"));
		layersBox.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		layersBox.setLocation(getLocation().x - 235, getLocation().y + 75);
		layersBox.setLayout(new BorderLayout());
		layersBox.setSize(240, 300);

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
			setVisible(true);
		}

		JPanel buttonContainer = new JPanel();
		buttonContainer.add(getButtonForLayers(controller));

		layersBox.add(topPanel, BorderLayout.CENTER);
		layersBox.add(buttonContainer, BorderLayout.SOUTH);

		return layersBox;
	}

	private JButton getButtonForLayers(Controller controller) {
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

	private void setListeners() {
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addWindowListener(this);
	}

	private void startHelperThread() {
		Thread helper = new Thread(() -> {
			while (isVisible()) {
				if (isImageChanged.get()) {
					updateImage();
					isImageChanged.set(false);
					System.out.println("image changed");
				}
				try {
					TimeUnit.MILLISECONDS.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		helper.setDaemon(true);
		helper.start();
		System.out.println("daemon " + helper.isDaemon());

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
			width = defaultImageWidth / count;
			height = defaultImageHeight / count;
			resizeImage();
		} else {
			count = 1;
			width = defaultImageWidth;
			height = defaultImageHeight;
			updateImage();
		}
		setSize(width, height + 55);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		try {
			int thisX = imageContainer.getLocation().x;
			int thisY = imageContainer.getLocation().y;

			int xMoved = (thisX + e.getX()) - (thisX + p.x);
			int yMoved = (thisY + e.getY()) - (thisY + p.y);

			int X = thisX + xMoved / 10;
			int Y = thisY + yMoved / 10;

			imageContainer.setLocation(X, Y);
			imageContainer.repaint();
		} catch (NullPointerException exc) {
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
