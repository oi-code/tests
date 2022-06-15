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
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import ImageConvertor.data.ImageLoader;
import ImageConvertor.data.State;
import ImageConvertor.data.Points;
import ImageConvertor.views.desktop.ParsedImagePreview;
import ImageConvertor.views.desktop.PathsImagePreview;
import ImageConvertor.views.desktop.ViewProccessStatus;

public class Controller {

	private boolean isProcessed;
	private boolean isCanceled;
	private WorkerManager workerManager;
	private ExecutorService execService; // = // Executors.newCachedThreadPool();
	// Executors.newFixedThreadPool(View.N_THREADS);
	private ArrayBlockingQueue<String> messageExchanger;
	private CompletionService<List<Points>> service;
	public static final int N_THREADS = Runtime.getRuntime().availableProcessors();
	private static final State STATE = State.getInstance();

	public Controller() {
		getExecutorService();
		messageExchanger = new ArrayBlockingQueue<String>(N_THREADS);
		Thread helper = new Thread(() -> {
			if (messageExchanger.size() > 50) {
				messageExchanger.clear();
			}
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		helper.setName("controller message exchanger observer");
		helper.setDaemon(true);
		helper.start();
	}

	public int getChunks() {
		return STATE.getChunks();
	}

	public void setChunks(int chunks) {
		STATE.setChunks(chunks);
	}

	public ParsedImagePreview getParsedImage() {
		return STATE.getParsedImage();
	}

	public void setParsedImage(ParsedImagePreview parsedImage) {
		STATE.setParsedImage(parsedImage);
	}

	public List<List<Points>> getAllLayersContainer() {
		return STATE.getAllLayersContainer();
	}

	public List<List<Points>> getForDrawContainer() {
		return STATE.getForDrawContainer();
	}

	public void createPath(List<Float> settings) {
		isProcessed = false;
		isCanceled = false;
		if (settings.size() < 5) {
			isCanceled = true;
			isProcessed = true;
			return;
		}
		int totalConnectedPointsLimit = settings.get(0).intValue();
		int limitConnectedPoints = settings.get(1).intValue();
		float rangeRate = settings.get(2).floatValue();
		float weightRate = settings.get(3).floatValue();
		float pathLengthDivider = settings.get(4).floatValue();
		int maxRange = settings.get(5).intValue();
		float pathDivider = settings.get(6).floatValue();
		int iterations = settings.get(7).intValue();
		float vaporizeRate = settings.get(8).floatValue();
		if (isCanceled) {
			return;
		}
		Thread t = new Thread(() -> {
			messageExchanger.clear();
			Thread view = new Thread(new ViewProccessStatus(this));
			view.setDaemon(true);
			view.start();
			workerManager = new WorkerManager(this, totalConnectedPointsLimit, limitConnectedPoints, rangeRate,
					weightRate, pathLengthDivider, maxRange, pathDivider, iterations, vaporizeRate);
			workerManager.getPath();
			isProcessed = true;

			new PathsImagePreview(this).showImage();
		});
		t.setName("Controller support thread");
		t.setDaemon(true);
		t.start();
	}

	public void createSVG() {
		workerManager.createSVG();
	}

	public int getLayers() {
		return STATE.getLayers();
	}

	public void setLayers(int layers) {
		STATE.setLayers(layers);
	}

	public void setFigure(String figure) {
		STATE.setFigure(figure);
	}

	public String getFigure() {
		return STATE.getFigure();
	}

	public String getFileName() {
		String resFileName = "";
		resFileName = STATE.getFileName() + "_";
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.ENGLISH);
		resFileName += sdf.format(new Date()).toString() + resFileName;
		return resFileName;
	}

	public void setChunkSize(short chunkSize) {
		STATE.setChunkSize(chunkSize);
	}

	public void setStroke(float stroke) {
		STATE.setStroke(stroke);
	}

	public void loadImage() {
		ImageLoader imageLoader = new ImageLoader();

		BufferedImage img = imageLoader.loadImage();
		if (img == null) {
			return;
		}
		STATE.setBufferedImage(img);
		STATE.setFileName(imageLoader.getPath().getFileName().toString().substring(0,
				imageLoader.getPath().getFileName().toString().lastIndexOf(".")));
		STATE.setImageWidth((short) img.getWidth());
		STATE.setImageHeight((short) img.getHeight());
		STATE.setImageIcon(new ImageIcon(img));
		STATE.setLoaded(true);
	}

	public ImageIcon getImageIcon() {
		return STATE.getImageIcon();
	}

	public boolean isLoaded() {
		return STATE.isLoaded();
	}

	private void getPointsList() {
		messageExchanger.clear();
		Runtime.getRuntime().gc();
		float lumStep = 1f / STATE.getLayers();
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
			}
		}
		for (Iterator<List<Points>> iterator = results.iterator(); iterator.hasNext();) {
			List<Points> list = iterator.next();
			if (list.size() < 1) {
				iterator.remove();
			}
		}
		Collections.sort(results, (o1, o2) -> {
			return Float.compare(o2.get(0).layer, o1.get(0).layer);
		});
		STATE.setAllLayersContainer(results);
	}

	public short getChunkSize() {
		return STATE.getChunkSize();
	}

	public float getStroke() {
		return STATE.getStroke();
	}

	public short getImageWidth() {
		return STATE.getImageWidth();
	}

	public short getImageHeight() {
		return STATE.getImageHeight();
	}

	public BufferedImage getBufferedImage() {
		return STATE.getBufferedImage();
	}

	public void showImage() {
		isProcessed = false;
		Thread worker = new Thread(() -> {
			messageExchanger.clear();
			Thread processView = new Thread(new ViewProccessStatus(this));
			processView.setDaemon(true);
			processView.start();
			getPointsList();
			STATE.setParsedImage(new ParsedImagePreview(this));
			STATE.getParsedImage().showImage();
			isProcessed = true;
		});
		worker.setDaemon(true);
		worker.start();
	}

	public void saveImage() {
		STATE.getParsedImage().saveImage();
	}

	public void setNullParser() {
		if (STATE.getParsedImage() == null) {
			return;
		}
		STATE.getParsedImage().removeListeners();
		STATE.setParsedImage(null);

	}

	public boolean isRandom() {
		return STATE.isUseRandom();
	}

	public void setRandom(boolean useRandom) {
		STATE.setUseRandom(useRandom);
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

	public void setFinalList(List<List<Point>> finalList) {
		STATE.setChosedLayers(finalList);
	}

	public List<List<Point>> getFinalList() {
		return STATE.getChosedLayers();
	}

	public String pollMessage() {
		return messageExchanger.poll();
	}

	public void offerMessage(String string) {
		messageExchanger.offer(string);
	}

	public List<List<Point>> getPathsPointList() {
		return STATE.getPathsPointList();
	}

	public void setPathsPointList(List<List<Point>> finalList) {
		STATE.setPathsPointList(finalList);
	}

}
