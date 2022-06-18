package ImageConvertor.core;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
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
	private PathWorkerManager workerManager;
	private CompletionService<List<Points>> service;
	private ExecutorService exec; // = // Executors.newCachedThreadPool();
	// Executors.newFixedThreadPool(View.N_THREADS);
	public ArrayBlockingQueue<String> messageExchanger;
	public static final int N_THREADS = Runtime.getRuntime().availableProcessors();
	private static final State STATE = State.getInstance();

	public Controller() {
		getExecutorService();
		getLocale();
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

	private void getLocale() {
		Properties prop = new Properties();
		String defLoc = "ru_RU";// Locale.getDefault().toString();
		try {
			InputStream is = new FileInputStream("locale\\" + defLoc + ".properties");
			InputStreamReader isr = new InputStreamReader(is, Charset.forName("UTF-8"));
			prop.load(isr);
			STATE.setLocale(prop);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getLocaleText(String key) {
		return STATE.getLocale().getProperty(key);
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
		/*
		 * Thread worker = new Thread(() -> {
		 * ImageParser parser = new ImageParser(this);
		 * STATE.setAllLayersContainer(parser.doTask());
		 * });
		 * worker.setDaemon(true);
		 * worker.start();
		 */
	}

	public void createPath(List<Float> settings) {
		isProcessed = false;
		isCanceled = false;
		if (settings == null || settings.size() < 5) {
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
		Runnable t = () -> {
			messageExchanger.clear();
			/*
			 * Thread view = new Thread(new ViewProccessStatus(this));
			 * view.setDaemon(true);
			 * view.start();
			 */
			exec.submit(new ViewProccessStatus(this));
			workerManager = new PathWorkerManager(this, totalConnectedPointsLimit, limitConnectedPoints, rangeRate,
					weightRate, pathLengthDivider, maxRange, pathDivider, iterations, vaporizeRate);
			workerManager.getPath();
			isProcessed = true;
			// view.interrupt();
			new PathsImagePreview(this).showImage();
		};
		exec.submit(t);
		/*
		 * t.setName("Controller support thread pathCreate");
		 * t.setDaemon(true);
		 * t.start();
		 */
	}

	public void showImage() {
		isProcessed = false;
		Runnable worker = () -> {
			messageExchanger.clear();
			/*
			 * Thread view = new Thread(new ViewProccessStatus(this));
			 * view.setDaemon(true);
			 * view.start();
			 */
			exec.submit(new ViewProccessStatus(this));
			// getPointsList();
			ImageParserWorker parser = new ImageParserWorker(this);
			STATE.setAllLayersContainer(parser.doTask());
			/*
			 * STATE.setParsedImage(new ParsedImagePreview(this));
			 * STATE.getParsedImage().showImage();
			 */
			isProcessed = true;
			// view.interrupt();
			new ParsedImagePreview(this).showImage();
		};
		/*
		 * worker.setName("Controller support thread parseImage");
		 * worker.setDaemon(true);
		 * worker.start();
		 */
		exec.submit(worker);
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
		if (!exec.isShutdown()) {
			exec.shutdownNow();
			getExecutorService();
		}
		if (workerManager != null) {
			workerManager.cancelTask();
			workerManager = null;
		}

		isProcessed = true;
	}

	private void getExecutorService() {
		exec = Executors.newFixedThreadPool(N_THREADS*2);
	}

	public CompletionService<List<Points>> getService() {
		if (service == null) {
			service = new ExecutorCompletionService<>(exec);
		}
		return service;
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
