package ImageConvertor.core;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.ImageIcon;

import ImageConvertor.data.State;
import ImageConvertor.data.Chunk;
import ImageConvertor.views.desktop.GCodeCreatorView;
import ImageConvertor.views.desktop.ParsedImagePreview;
import ImageConvertor.views.desktop.PathsImagePreview;
import ImageConvertor.views.desktop.ViewProccessStatus;

public class Controller {

	private boolean isProcessed;
	private boolean isCanceled;
	private boolean isPathsCreated;
	private boolean isProcessWindowShowed;
	private Pathfinder workerManager;
	public static final int N_THREADS = Runtime.getRuntime().availableProcessors();
	private static final State STATE = State.getInstance();

	public Controller() {
		getLocale();
	}

	private void getLocale() {
		Properties prop = new Properties();
		String defLoc = Locale.getDefault().toString();
		try {
			// InputStream is = new FileInputStream("locale\\" + defLoc + ".properties");
			// InputStreamReader isr = new InputStreamReader(is, Charset.forName("UTF-8"));
			InputStream is = State.class.getResourceAsStream("l18n/" + defLoc + ".properties");
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

	public List<List<Chunk>> getAllLayers() {
		return STATE.getAllLayers();
	}

	public List<List<Chunk>> getChosedLayersForDraw() {
		return STATE.getChosedLayersForDraw();
	}

	public void createSVG() {
		
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
		ImageLoader imageLoader = new ImageLoader(this);

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

	public void createPath(List<Float> settings) {
		isProcessed = false;
		isCanceled = false;
		isPathsCreated = false;
		if (settings == null || settings.size() < 5) {
			isCanceled = true;
			isProcessed = false;
			isPathsCreated = false;
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
			Queue<String> queue = new ConcurrentLinkedQueue<>();
			Thread view = new Thread(new ViewProccessStatus(this, queue));
			view.setDaemon(true);
			view.start();
			// SwingUtilities.invokeLater(view);

			/*workerManager = new PathWorkerManager(this, totalConnectedPointsLimit, limitConnectedPoints, rangeRate,
					weightRate, pathLengthDivider, maxRange, pathDivider, iterations, vaporizeRate, queue);*/
			workerManager=new AntPathWorkerManager(this, totalConnectedPointsLimit, limitConnectedPoints, rangeRate,
					weightRate, pathLengthDivider, maxRange, pathDivider, iterations, vaporizeRate, queue);

			//workerManager.createClouds();
			workerManager.getSequencesOfPaths();
			if (isCanceled) {
				isProcessed = false;
			} else {
				isProcessed = true;
				isPathsCreated = true;
			}
			view.interrupt();
			new PathsImagePreview(this).showImage();
		});

		t.setName("Controller support thread pathCreate");
		t.setDaemon(true);
		t.start();

	}

	public void parseImage() {
		isProcessed = false;
		isCanceled = false;
		Thread worker = new Thread(() -> {
			ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
			Thread view = new Thread(new ViewProccessStatus(this, queue));
			view.setDaemon(true);
			view.start();
			ImageParserWorker parser = new ImageParserWorker(this, queue);
			STATE.setAllLayers(parser.doTask());
			if (isCanceled) {
				isProcessed = false;
			} else {
				isProcessed = true;
			}
			view.interrupt();
			STATE.setParsedImage(new ParsedImagePreview(this));
			STATE.getParsedImage().showImage();
		});

		worker.setName("Controller support thread parseImage");
		worker.setDaemon(true);
		worker.start();

	}

	public void createGCode() {
		GCodeCreatorView gview = new GCodeCreatorView(this);
		List<String> settings = gview.getSettings();
		if (settings == null) {
			return;
		}
		new GCodeGenerator(this, settings);
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
		isCanceled = true;
		if (workerManager != null) {
			workerManager.cancelTask();
			workerManager = null;
		}
	}

	public boolean isProcessed() {
		return isProcessed;
	}

	public void setCanceled(boolean isCanceled) {
		this.isCanceled = isCanceled;
	}

	public boolean isCanceled() {
		return isCanceled;
	}

	public boolean isPatsCreated() {
		return isPathsCreated;
	}

	/*
	 * public void setPatsCreated(boolean isPatsCreated) {
	 * this.isPatsCreated = isPatsCreated;
	 * }
	 */

	public void setFinalList(List<List<Chunk>> finalList) {
		STATE.setChosedLayers(finalList);
	}

	public List<List<Chunk>> getFinalList() {
		return STATE.getChosedLayers();
	}

	public List<List<Chunk>> getPathsPointList() {
		return STATE.getPathsPointList();
	}

	public void setPathsPointList(List<List<Chunk>> finalList) {
		STATE.setPathsPointList(finalList);
	}

	public boolean isProcessWindowShowed() {
		return isProcessWindowShowed;
	}

	public void setProcessWindowShowed(boolean isProcessWindowShowed) {
		this.isProcessWindowShowed = isProcessWindowShowed;
	}

}
