package ImageConvertor.views.desktop;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.TexturePaint;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;

import ImageConvertor.core.Controller;
import ImageConvertor.data.State;

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
	Map<String, JButton> buttonContainer = new HashMap<>();

	private static final View INSTANCE = new View();

	private View() {
		super();
		stubImage = new StubImage();
		controller = new Controller();
		/*
		 * controller.setChunkSize((short) 3);
		 * controller.setFigure("line");
		 * controller.setLayers(10);
		 * controller.setStroke(1f);
		 * controller.setRandom(true);
		 */
		init();
		setSize(600, 335);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static View getInstance() {
		return INSTANCE;
	}

	/*
	 * private void clearMemory() {
	 * 
	 * controller.setNullParser();
	 * 
	 * rightContainer.removeAll();
	 * leftContainer.removeAll();
	 * mainContainer.removeAll();
	 * 
	 * remove(mainContainer);
	 * // removeAll();
	 * mainContainer = null;
	 * leftContainer = null;
	 * rightContainer = null;
	 * // controller = null;
	 * reloadController();
	 * Runtime.getRuntime().gc();
	 * init();
	 * }
	 */

	/*
	 * private void reloadController() {
	 * 
	 * short chunk = controller.getChunkSize();
	 * float strokefactor = controller.getStroke();
	 * int layers = controller.getLayers();
	 * String figure = controller.getFigure();
	 * boolean rnd = controller.isRandom();
	 * 
	 * controller = new Controller();
	 * 
	 * controller.setChunkSize(chunk);
	 * controller.setStroke(strokefactor);
	 * controller.setLayers(layers);
	 * controller.setFigure(figure);
	 * controller.setRandom(rnd);
	 * 
	 * }
	 */

	private void init() {
		setTitle(controller.getLocaleText("program_name"));
		mainContainer = new JPanel();
		mainContainer.setLayout(new GridLayout(1, 2));

		leftContainer = getLeftContainer();
		rightContainer = getRightContainer();

		mainContainer.add(leftContainer);
		mainContainer.add(rightContainer);

		add(mainContainer);

		startViewSupportThread();
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
		components.add(getImageProcessorContainer());
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
		jChunkSize.setText(controller.getLocaleText("chunk_size"));
		jChunkSize.setToolTipText(controller.getLocaleText("max") + ": 25");
		jChunkSize.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		jChunkSize.setFocusable(false);
		jChunkSize.setBorder(null);
		jChunkSize.setEditable(false);

		JSpinner chunkSpinner = new JSpinner(new SpinnerNumberModel(8, 1, 25, 1));
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
		JTextField jStrokeFactor = new JTextField(controller.getLocaleText("stroke_factor") + ":");
		jStrokeFactor.setToolTipText(controller.getLocaleText("max") + ": 5");
		jStrokeFactor.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		jStrokeFactor.setFocusable(false);
		jStrokeFactor.setBorder(null);
		jStrokeFactor.setEditable(false);
		JSpinner jStrokeFactorSpinner = new JSpinner(new SpinnerNumberModel(1, 0.0f, 5f, 0.5f));
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

		JTextField layersText = new JTextField(controller.getLocaleText("layers") + ":");
		layersText.setToolTipText(controller.getLocaleText("layers_count"));
		layersText.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		layersText.setFocusable(false);
		layersText.setEditable(false);
		layersText.setBorder(null);

		JSpinner layerSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 30, 1));
		layerSpinner.setToolTipText(controller.getLocaleText("min_lum"));
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
		JTextField textFigure = new JTextField(controller.getLocaleText("figure") + ":");
		textFigure.setToolTipText(controller.getLocaleText("figure_tip"));
		textFigure.setEditable(false);
		textFigure.setFocusable(false);
		textFigure.setBorder(null);
		textFigure.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		String[] arr = { controller.getLocaleText("line"), controller.getLocaleText("circle"),
				controller.getLocaleText("x") };
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

	private JComponent getImageProcessorContainer() {
		JPanel imageProcessor = new JPanel(new GridLayout(1, 2));
		JTextField imageProcessorText = new JTextField("Image Processor");
		imageProcessorText.setEditable(false);
		imageProcessorText.setFocusable(false);
		imageProcessorText.setBorder(null);
		imageProcessorText.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		String[] arr = { "Line", "Lumin" };
		/*
		 * default image processor will be chose after loading image
		 * {@link ImageConvertor.views.desktop#getImageLoaderButton()}
		 */
		JSpinner figSpinner = new JSpinner(new SpinnerListModel(arr));
		figSpinner.setBorder(null);
		figSpinner.setEnabled(false);
		JComponent figEditor = figSpinner.getEditor();
		JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) figEditor;
		spinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
		spinnerEditor.getTextField().setEditable(false);
		figSpinner.addChangeListener(e -> {
			controller.setProcessor(figSpinner.getValue().toString());
		});
		imageProcessor.add(imageProcessorText);
		imageProcessor.add(figSpinner);

		Thread t = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				if (State.getInstance().isLoaded()) {
					controller.setProcessor(figSpinner.getValue().toString());
					figSpinner.setEnabled(true);
					Thread.currentThread().interrupt();
				} else {
					try {
						TimeUnit.MILLISECONDS.sleep(200);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		t.setDaemon(true);
		t.start();

		return imageProcessor;

	}

	private JComponent getRndChoserContainer() {
		JPanel rndChooser = new JPanel(new GridLayout(1, 2));
		JTextField rndChooserText = new JTextField(controller.getLocaleText("rnd_for_dr"));
		rndChooserText.setToolTipText(controller.getLocaleText("rnd_for_dr"));
		rndChooserText.setEditable(false);
		rndChooserText.setFocusable(false);
		rndChooserText.setBorder(null);
		rndChooserText.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		JPanel rndChooserPan = new JPanel();
		JCheckBox randChoose = new JCheckBox(controller.getLocaleText("use"));
		randChoose.setFocusable(false);
		randChoose.setSelected(true);
		randChoose.setToolTipText(controller.getLocaleText("rnd_tip"));
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

		JButton imageChooser = new JButton(controller.getLocaleText("chose_image"));
		imageChooser.setFocusable(false);
		imageChooser.addActionListener(e -> {
			setTitle(controller.getLocaleText("program_name"));
			controller.loadImage();
			if (controller.getProcessor() != null) {
				controller.setProcessor(controller.getProcessor());
			}
			JPanel temp;
			rightContainer.removeAll();
			if (controller.isLoaded()) {
				temp = new PreView(controller);
			} else {
				temp = stubImage;
			}
			rightContainer.add(temp);
			CardLayout cl = (CardLayout) rightContainer.getLayout();
			cl.show(rightContainer, "");
			validate();

		});
		buttonContainer.put("load", imageChooser);
		return imageChooser;
	}

	private JComponent getProccessButton() {
		JButton processImage = new JButton(controller.getLocaleText("prc_img"));

		processImage.setFocusable(false);
		processImage.addActionListener(e -> {
			double time = System.currentTimeMillis();

			short chunk = controller.getChunkSize();
			float strokefactor = controller.getStroke();
			int layers = controller.getLayers();
			String figure = controller.getFigure();
			boolean rnd = controller.isRandom();
			if (controller.getProcessor() != null) {
				controller.setProcessor(controller.getProcessor());
			}
			controller.setChunkSize(chunk);
			controller.setStroke(strokefactor);
			controller.setLayers(layers);
			controller.setFigure(figure);
			controller.setRandom(rnd);
			controller.parseImage();

			workTime = (System.currentTimeMillis() - time) / 1000d;
			setTitle(String.format(controller.getLocaleText("img_prev_time"), workTime));

		});
		buttonContainer.put("proc", processImage);
		return processImage;
	}

	private JComponent getSaveButton() {
		JButton saveImage = new JButton(controller.getLocaleText("save_img"));
		saveImage.setFocusable(false);
		saveImage.addActionListener(e -> {
			controller.saveImage();
			setTitle(String.format(controller.getLocaleText("saved_word_done"), workTime));
		});
		buttonContainer.put("save", saveImage);
		return saveImage;
	}

	private JComponent getConstructPathButton() {
		JButton constructPath = new JButton(controller.getLocaleText("construct_path"));
		constructPath.setFocusable(false);
		constructPath.addActionListener(e -> {
			List<Float> settings = new AlgorithmSettingsView(controller).getSettings();
			controller.createPath(settings);
		});
		buttonContainer.put("createpath", constructPath);
		return constructPath;
	}

	private JComponent getCreateSvgButton() {
		JButton testButton = new JButton(controller.getLocaleText("create_svg"));
		testButton.setFocusable(false);
		testButton.addActionListener(e -> {
			controller.createSVG();
		});
		buttonContainer.put("createsvg", testButton);
		return testButton;
	}

	private JComponent createGCodeButton() {
		JButton gcode = new JButton(controller.getLocaleText("GCODE"));
		gcode.setFocusable(false);
		gcode.addActionListener(e -> {
			controller.createGCode();
		});
		buttonContainer.put("creategcode", gcode);
		return gcode;
	}

	public int getChungSize() {
		return size;
	}

	private void startViewSupportThread() {
		Thread support = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				if (controller.isProcessWindowShowed()) {
					buttonContainer.values().forEach(e -> e.setEnabled(false));
					continue;
				}
				if (!controller.isLoaded() && !controller.isCanceled()) {
					buttonContainer.entrySet().stream().filter(e -> !e.getKey().equals("load"))
							.forEach(e -> e.getValue().setEnabled(false));
				} else {
					buttonContainer.get("proc").setEnabled(true);
					buttonContainer.get("load").setEnabled(true);
					if (controller.isProcessed() && !controller.isCanceled()) {
						buttonContainer.get("save").setEnabled(true);
						buttonContainer.get("createpath").setEnabled(true);
						if (controller.isPatsCreated()) {
							buttonContainer.get("creategcode").setEnabled(true);
							buttonContainer.get("createsvg").setEnabled(true);
						}
					} else {
						buttonContainer.get("createsvg").setEnabled(false);
						buttonContainer.get("creategcode").setEnabled(false);
						buttonContainer.get("createpath").setEnabled(false);
						buttonContainer.get("save").setEnabled(false);
					}
				}
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		support.setDaemon(true);
		support.setName("main-view-support-thread");
		support.start();
	}

}
