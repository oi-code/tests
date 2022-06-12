package ImageConvertor.core;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import ImageConvertor.views.desktop.AlgorithmSettingsView;
import ImageConvertor.views.desktop.ParsedImagePreview;
import ImageConvertor.views.desktop.ViewProccessStatus;

public class Controller {

	private BufferedImage bufferedImage;
	private short chunkSize;
	private short imageWidth, imageHeight;
	private float stroke;
	private boolean isLoaded;
	private boolean isProcessed;
	private boolean isCanceled;
	private ParsedImagePreview parsedImage;
	private ImageIcon imageIcon;
	private String fileName;
	private String figure;
	private boolean useRandom;
	private int layers;
	private int chunks;
	private WorkerManager workerManager;
	public static final int N_THREADS = Runtime.getRuntime().availableProcessors();
	private List<List<Points>> forDrawContainer = new /* CopyOnWrite */ArrayList<>();
	private List<List<Points>> allLayersContainer;
	public List<List<Point>> finalList = new ArrayList<>();
	private ExecutorService execService; // = // Executors.newCachedThreadPool();
	// Executors.newFixedThreadPool(View.N_THREADS);
	{
		getExecutorService();
	}
	public ArrayBlockingQueue<String> messageExchanger = new ArrayBlockingQueue<String>(N_THREADS);
	CompletionService<List<Points>> service;

	int totalConnectedPointsLimit = 2500;
	int limitConnectedPoints = 80;
	float rangeRate = 20f;
	float weightRate = 1f;
	float pathLengthDivider = 9;
	int maxRange = 3;

	public void setTotalConnectedPointsLimit(int totalConnectedPointsLimit) {
		this.totalConnectedPointsLimit = totalConnectedPointsLimit;
	}

	public void setLimitConnectedPoints(int limitConnectedPoints) {
		this.limitConnectedPoints = limitConnectedPoints;
	}

	public void setRangeRate(float rangeRate) {
		this.rangeRate = rangeRate;
	}

	public void setWeightRate(float weightRate) {
		this.weightRate = weightRate;
	}

	public void setPathLengthDivider(float pathLengthDivider) {
		this.pathLengthDivider = pathLengthDivider;
	};

	public void setMaxRange(int maxRange) {
		this.maxRange = maxRange;
	}

	public int getChunks() {
		return chunks;
	}

	public void setChunks(int chunks) {
		this.chunks = chunks;
	}

	public ParsedImagePreview getParsedImage() {
		return parsedImage;
	}

	public void setParsedImage(ParsedImagePreview parsedImage) {
		this.parsedImage = parsedImage;
	}

	public List<List<Points>> getAllLayersContainer() {
		return allLayersContainer;
	}

	public List<List<Points>> getForDrawContainer() {
		return forDrawContainer;
	}

	// error something here, dont work if workermanager launched in external thread t
	public void createPath() {
		isProcessed = false;
		isCanceled = false;
		new AlgorithmSettingsView(this);
		if (isCanceled) {
			return;
		}
		Thread t = new Thread(() -> {
			messageExchanger.clear();
			Thread view = new Thread(new ViewProccessStatus(this));
			view.setDaemon(true);
			view.start();
			workerManager = new WorkerManager(this, totalConnectedPointsLimit, limitConnectedPoints, rangeRate,
					weightRate, pathLengthDivider, maxRange);
			// workerManager = new WorkerManager(this);
			workerManager.getPath();
			isProcessed = true;
		});
		t.setName("Controller support thread");
		t.setDaemon(true);
		t.start();
	}

	public void createSVG() {
		workerManager.createSVG();
	}

	public Controller() {
	}

	public int getLayers() {
		return layers;
	}

	public void setLayers(int layers) {
		this.layers = layers;
	}

	public void setFigure(String figure) {
		this.figure = figure;
	}

	public String getFigure() {
		return figure;
	}

	public String getFileName() {
		String resFileName = "";
		resFileName = fileName + "_";
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.ENGLISH);
		resFileName += sdf.format(new Date()).toString() + resFileName;
		return resFileName;
	}

	public void setChunkSize(short chunkSize) {
		this.chunkSize = chunkSize;
	}

	public void setStroke(float stroke) {
		this.stroke = stroke;
	}

	public void loadImage() {
		ImageLoader imageLoader = new ImageLoader();
		bufferedImage = imageLoader.loadImage();
		if (bufferedImage == null)
			return;
		fileName = imageLoader.getPath().getFileName().toString().substring(0,
				imageLoader.getPath().getFileName().toString().lastIndexOf("."));
		imageWidth = (short) bufferedImage.getWidth();
		imageHeight = (short) bufferedImage.getHeight();
		imageIcon = new ImageIcon(bufferedImage);
		isLoaded = true;
	}

	public ImageIcon getImageIcon() {
		return imageIcon;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public List<List<Points>> getPointsList() {
		messageExchanger.clear();
		Runtime.getRuntime().gc();
		float lumStep = 1f / layers;
		float start = 0f;
		List<Float> steps = new ArrayList<>();
		steps.add(start);
		while (start < 1f) {
			start = start + lumStep;
			steps.add(start);
		}
		List<List<Points>> results = new ArrayList<List<Points>>();
		service = new ExecutorCompletionService<>(execService);

		for (int i = 0; i < steps.size() - 1; i++) {
			SingleThreadParseImage temParseImage = new SingleThreadParseImage(this);
			temParseImage.setMinLum(steps.get(i));
			temParseImage.setMaxLum(steps.get(i + 1));
			service.submit(new Callable<List<Points>>() {
				@Override
				public List<Points> call() throws Exception {
					List<Points> result = temParseImage.getPointsList();
					return result;
				}
			});
		}
		int currentTask = 0;
		for (int i = 0; i < steps.size() - 1; i++) {
			try {
				Future<List<Points>> temp = service.take();
				if (temp.isDone()) {
					List<Points> res = temp.get();
					results.add(res);
					messageExchanger.offer(String.format("current layer %d", currentTask++));
				} else {
					continue;
				}
			} catch (InterruptedException | ExecutionException e) {
				JOptionPane.showMessageDialog(null,
						"Fatal Error:\n" + e.getCause().getMessage() + ".\nPress \"OK\" to exit.", "Exception occured",
						JOptionPane.ERROR_MESSAGE);
				// System.exit(0);
			}
		}
		// exec.shutdownNow();
		for (Iterator<List<Points>> iterator = results.iterator(); iterator.hasNext();) {
			List<Points> list = iterator.next();
			if (list.size() < 1) {
				iterator.remove();
			}
		}
		Collections.sort(results, (o1, o2) -> {
			return Float.compare(o2.get(0).layer, o1.get(0).layer);
		});

		allLayersContainer = results;
		return results;
	}

	public short getChunkSize() {
		return chunkSize;
	}

	public float getStroke() {
		return stroke;
	}

	public short getImageWidth() {
		return imageWidth;
	}

	public short getImageHeight() {
		return imageHeight;
	}

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	public void showImage() {
		isProcessed = false;
		Thread worker = new Thread(() -> {
			messageExchanger.clear();
			Thread processView = new Thread(new ViewProccessStatus(this));
			processView.setDaemon(true);
			processView.start();
			getPointsList();
			parsedImage = new ParsedImagePreview(this);
			parsedImage.showImage();
			isProcessed = true;
		});
		worker.setDaemon(true);
		worker.start();
	}

	public void saveImage() {
		parsedImage.saveImage();
	}

	public void setNullParser() {
		if (parsedImage == null) {
			return;
		}
		parsedImage.removeListeners();
		parsedImage = null;
	}

	public boolean isRandom() {
		return useRandom;
	}

	public void setRandom(boolean useRandom) {
		this.useRandom = useRandom;
	}

	public void cancelTask() {
		if (!execService.isShutdown()) {
			execService.shutdownNow();
			getExecutorService();
		}
		if (workerManager != null) {
			workerManager.cancelTask();
			workerManager = null;
		}

		isProcessed = true;
	}

	private void getExecutorService() {
		execService = Executors.newFixedThreadPool(N_THREADS);
	}

	public void createGCode() {
		new GCodeCreator(this);
	}

	public boolean isProcessed() {
		return isProcessed;
	}

	public void setCanceled(boolean isCanceled) {
		this.isCanceled = isCanceled;
	}

}
