package ImageConvertor.views.desktop;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;

import ImageConvertor.core.Controller;
import ImageConvertor.core.GCodeCreator;
import ImageConvertor.core.WorkerManager;

@SuppressWarnings(value = "serial")
public class View extends JFrame {

	public final static Path DESKTOP_PATH = Paths.get(System.getProperty("user.home") + "\\Desktop\\");
	int size;
	Controller controller;
	double workTime;
	StubImage stubImage;

	JPanel mainContainer;
	JPanel rightContainer;
	JPanel leftContainer;

	private static final View INSTANCE = new View();

	private View() {
		super();
		stubImage = new StubImage();
		controller = new Controller();
		controller.setChunkSize((short) 3);
		controller.setFigure("line");
		controller.setLayers(10);
		controller.setStroke(1f);
		controller.setRandom(true);
		init();

		setSize(600, 335);
		// setResizable(false);
		// setLayout(null);
		setTitle("ImageConvertor v11");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static View getInstance() {
		return INSTANCE;
	}

	private void clearMemory() {

		controller.setNullParser();

		rightContainer.removeAll();
		leftContainer.removeAll();
		mainContainer.removeAll();

		remove(mainContainer);
		// removeAll();
		mainContainer = null;
		leftContainer = null;
		rightContainer = null;
		// controller = null;
		reloadController();
		Runtime.getRuntime().gc();
		init();
	}

	private void reloadController() {

		short chunk = controller.getChunkSize();
		float strokefactor = controller.getStroke();
		int layers = controller.getLayers();
		String figure = controller.getFigure();
		boolean rnd = controller.isRandom();

		controller = new Controller();

		controller.setChunkSize(chunk);
		controller.setStroke(strokefactor);
		controller.setLayers(layers);
		controller.setFigure(figure);
		controller.setRandom(rnd);

	}

	private void init() {

		mainContainer = new JPanel();
		mainContainer.setLayout(new GridLayout(1, 2));

		leftContainer = getLeftContainer();
		rightContainer = getRightContainer();

		mainContainer.add(leftContainer);
		mainContainer.add(rightContainer);

		add(mainContainer);

		setVisible(true);

	}

	private JPanel getRightContainer() {

		rightContainer = new JPanel();
		rightContainer.setLayout(new CardLayout());
		rightContainer.setSize(300, 300);
		rightContainer.add(stubImage);

		return rightContainer;

	}

	private JPanel getLeftContainer() {

		List<JComponent> components = new ArrayList<JComponent>();

		components.add(getChunkContainer());
		components.add(getStrokeFactorContainer());
		components.add(getLayersContainer());
		components.add(getFigureChoserContainer());
		components.add(getRndChoserContainer());

		components.add(getImageLoaderButton());
		components.add(getProccessButton());
		components.add(getSaveButton());
		components.add(getConstructPathButton());
		components.add(getCreateSvgButton());
		components.add(createGCodeButton());

		leftContainer = new JPanel();
		leftContainer.setSize(300, 300);
		leftContainer.setLayout(new GridLayout(components.size(), 1));

		for (JComponent j : components) {
			leftContainer.add(j);
		}

		return leftContainer;
	}

	private JComponent getChunkContainer() {
		JPanel chunkSize = new JPanel();
		chunkSize.setLayout(new GridLayout(1, 2));

		JTextField jChunkSize = new JTextField();
		jChunkSize.setText("Chunk size:");
		jChunkSize.setToolTipText("Maximum: 25");
		jChunkSize.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		jChunkSize.setFocusable(false);
		jChunkSize.setBorder(null);
		jChunkSize.setEditable(false);

		JSpinner chunkSpinner = new JSpinner(new SpinnerNumberModel(8/* controller.getChunkSize() */, 1, 25, 1));
		JComponent figEditorCh = chunkSpinner.getEditor();
		JSpinner.DefaultEditor chunkSpinnerEditor = (JSpinner.DefaultEditor) figEditorCh;
		chunkSpinner.setBorder(null);
		chunkSpinnerEditor.getTextField().setEditable(false);
		chunkSpinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
		controller.setChunkSize(Short.valueOf(chunkSpinner.getValue().toString()));
		chunkSpinner.addChangeListener(e -> {
			controller.setChunkSize(Short.valueOf(chunkSpinner.getValue().toString()));
		});

		chunkSize.add(jChunkSize);
		chunkSize.add(chunkSpinner);

		return chunkSize;
	}

	private JComponent getStrokeFactorContainer() {
		JPanel strokeFactor = new JPanel();
		strokeFactor.setLayout(new GridLayout(1, 2));
		JTextField jStrokeFactor = new JTextField("Stroke factor:");
		jStrokeFactor.setToolTipText("Maximum: 5");
		jStrokeFactor.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		jStrokeFactor.setFocusable(false);
		jStrokeFactor.setBorder(null);
		jStrokeFactor.setEditable(false);
		JSpinner jStrokeFactorSpinner = new JSpinner(new SpinnerNumberModel(controller.getStroke(), 0.0f, 5f, 0.5f));
		jStrokeFactorSpinner.setBorder(null);
		JComponent jStrokeFactorSpinnerComp = jStrokeFactorSpinner.getEditor();
		JSpinner.DefaultEditor strokeSpinnerEditor = (JSpinner.DefaultEditor) jStrokeFactorSpinnerComp;
		strokeSpinnerEditor.getTextField().setEditable(false);
		strokeSpinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
		controller.setStroke(Float.valueOf(jStrokeFactorSpinner.getValue().toString()));
		jStrokeFactorSpinner.addChangeListener(e -> {
			controller.setStroke(Float.valueOf(jStrokeFactorSpinner.getValue().toString()));
		});

		strokeFactor.add(jStrokeFactor);
		strokeFactor.add(jStrokeFactorSpinner);
		return strokeFactor;
	}

	private JComponent getLayersContainer() {
		JPanel layers = new JPanel();
		layers.setLayout(new GridLayout(1, 2));

		JTextField layersText = new JTextField("Layers:");
		layersText.setToolTipText("Layers count");
		layersText.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		layersText.setFocusable(false);
		layersText.setEditable(false);
		layersText.setBorder(null);

		JSpinner layerSpinner = new JSpinner(new SpinnerNumberModel(controller.getLayers(), 1, 30, 1));
		layerSpinner.setToolTipText("Minimal luminal pixel");
		layerSpinner.setBorder(null);
		JComponent layerSpinnerEditor = layerSpinner.getEditor();
		JSpinner.DefaultEditor lumSpinnerEditor = (JSpinner.DefaultEditor) layerSpinnerEditor;
		lumSpinnerEditor.getTextField().setEditable(false);
		lumSpinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);

		controller.setLayers(Integer.valueOf(layerSpinner.getValue().toString()));
		layerSpinner.addChangeListener(e -> {
			controller.setLayers(Integer.valueOf(layerSpinner.getValue().toString()));
		});

		layers.add(layersText);
		layers.add(layerSpinner);

		return layers;
	}

	private JComponent getFigureChoserContainer() {
		JPanel chooseFigure = new JPanel(new GridLayout(1, 2));
		JTextField textFigure = new JTextField("Figure: ");
		textFigure.setToolTipText("The figure will be use for draw image");
		textFigure.setEditable(false);
		textFigure.setFocusable(false);
		textFigure.setBorder(null);
		textFigure.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		String[] arr = { "Line", "Circle", "X" };
		JSpinner figSpinner = new JSpinner(new SpinnerListModel(arr));
		figSpinner.setBorder(null);
		JComponent figEditor = figSpinner.getEditor();
		JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) figEditor;
		spinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
		spinnerEditor.getTextField().setEditable(false);
		controller.setFigure(figSpinner.getValue().toString());
		figSpinner.addChangeListener(e -> {
			controller.setFigure(figSpinner.getValue().toString());
		});

		chooseFigure.add(textFigure);
		chooseFigure.add(figSpinner);

		return chooseFigure;
	}

	private JComponent getRndChoserContainer() {
		JPanel rndChooser = new JPanel(new GridLayout(1, 2));
		JTextField rndChooserText = new JTextField("Random for draw:");
		rndChooserText.setToolTipText("Random for pixels");
		rndChooserText.setEditable(false);
		rndChooserText.setFocusable(false);
		rndChooserText.setBorder(null);
		rndChooserText.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		JPanel rndChooserPan = new JPanel();
		JCheckBox randChoose = new JCheckBox("Use");
		randChoose.setFocusable(false);
		randChoose.setSelected(true);
		randChoose.setToolTipText("Use random for draw image.");
		controller.setRandom(randChoose.isSelected());
		randChoose.addChangeListener(e -> {
			controller.setRandom(randChoose.isSelected());
		});

		rndChooserPan.add(randChoose);
		rndChooser.add(rndChooserText);
		rndChooser.add(rndChooserPan);
		return rndChooser;
	}

	private JComponent getImageLoaderButton() {

		JButton imageChooser = new JButton("Choose Image");
		imageChooser.setFocusable(false);
		imageChooser.addActionListener(e -> {

			if (controller.isProcessed()) {
				clearMemory();
			}

			setTitle("ImageConvertor");
			controller.loadImage();

			if (controller.isLoaded()) {
				rightContainer.removeAll();
				JPanel temp = new PreView(controller);
				rightContainer.add(temp);
				CardLayout cl = (CardLayout) rightContainer.getLayout();
				cl.show(rightContainer, "");
				validate();
			} else {
				rightContainer.add(stubImage);
				CardLayout cl = (CardLayout) rightContainer.getLayout();
				cl.show(rightContainer, "");
				validate();
			}

		});
		return imageChooser;
	}

	private JComponent getProccessButton() {
		JButton processImage = new JButton("Process Image");

		processImage.setFocusable(false);
		processImage.addActionListener(e -> {
			double time = System.currentTimeMillis();

			short chunk = controller.getChunkSize();
			float strokefactor = controller.getStroke();
			int layers = controller.getLayers();
			String figure = controller.getFigure();
			boolean rnd = controller.isRandom();

			controller.setChunkSize(chunk);
			controller.setStroke(strokefactor);
			controller.setLayers(layers);
			controller.setFigure(figure);
			controller.setRandom(rnd);
			controller.showImage();

			workTime = (System.currentTimeMillis() - time) / 1000d;
			setTitle("Previrw image. Work done in " + workTime + " seconds.");

		});
		return processImage;
	}

	private JComponent getSaveButton() {
		JButton saveImage = new JButton("Save Image");
		saveImage.setFocusable(false);
		saveImage.addActionListener(e -> {
			controller.saveImage();
			setTitle("Saved. Work done in " + workTime + " seconds.");
		});

		return saveImage;
	}

	private JComponent getConstructPathButton() {
		JButton constructPath = new JButton("Construct Path");
		constructPath.setFocusable(false);
		constructPath.addActionListener(e -> {			
			controller.createPath();
		});

		return constructPath;
	}

	private JComponent getCreateSvgButton() {
		JButton testButton = new JButton("Create SVG");
		testButton.setFocusable(false);
		testButton.addActionListener(e -> {
			controller.createSVG();
		});
		return testButton;
	}

	private JComponent createGCodeButton() {
		JButton gcode = new JButton("GCODE");
		gcode.setFocusable(false);
		gcode.addActionListener(e -> {
			controller.createGCode();			
		});
		return gcode;
	}

	public int getChungSize() {
		return size;
	}

}
