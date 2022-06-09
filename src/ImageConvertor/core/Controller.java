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
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ImageConvertor.views.desktop.ParsedImagePreview;
import ImageConvertor.views.desktop.View;
import ImageConvertor.views.desktop.ViewProccessStatus;

public class Controller {

	private BufferedImage bufferedImage;
	private short chunkSize;
	private short imageWidth, imageHeight;
	private float stroke;
	private boolean isLoaded;
	private boolean isProcessed;
	private ParsedImagePreview parsedImage;
	private ImageIcon imageIcon;
	private String fileName;
	private String figure;
	private boolean useRandom;
	private int layers;
	private int chunks;
	private WorkerManager pathConstructor;
	private List<List<Points>> forDrawContainer = new ArrayList<>();
	private List<List<Points>> allLayersContainer;
	public List<List<Point>> finalList = new ArrayList<>();
	public ArrayBlockingQueue<String> messageExchanger = new ArrayBlockingQueue<String>(View.N_THREADS);

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

	public void setPathConstructor(WorkerManager pathConstructor) {
		this.pathConstructor = pathConstructor;
	}

	public List<List<Points>> getForDrawContainer() {
		return forDrawContainer;
	}

	public WorkerManager getPathConstructor() {
		return pathConstructor;
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

	public boolean isProcessed() {
		return isProcessed;
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
		CompletionService<List<Points>> service = new ExecutorCompletionService<>(View.EXECUTOR_SERVICE);

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
		for (Iterator iterator = results.iterator(); iterator.hasNext();) {
			List<Points> list = (List<Points>) iterator.next();
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
		Thread worker = new Thread(() -> {
			isProcessed=false;
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
		parsedImage.removeListeners();
		parsedImage = null;
	}

	public boolean isRandom() {
		return useRandom;
	}

	public void setRandom(boolean useRandom) {
		this.useRandom = useRandom;
	}

}
