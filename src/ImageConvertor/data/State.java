package ImageConvertor.data;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import ImageConvertor.views.desktop.ParsedImagePreview;

public class State {
	private BufferedImage bufferedImage;
	private short chunkSize;
	private short imageWidth, imageHeight;
	private float stroke;
	private ParsedImagePreview parsedImage;
	private ImageIcon imageIcon;
	private String fileName;
	private String figure;
	private List<List<Points>> allLayersContainer;
	private List<List<Points>> forDrawContainer = new ArrayList<>();
	private List<List<Point>> chosedLayers = new ArrayList<>();
	private List<List<Point>> pathPointsList;
	private boolean isLoaded;
	private boolean useRandom;
	private int layers;
	private int chunks;

	private static final State INSTANCE = new State();

	private State() {
	}

	public static State getInstance() {
		return INSTANCE;
	}

	public boolean isUseRandom() {
		return useRandom;
	}

	public void setUseRandom(boolean useRandom) {
		this.useRandom = useRandom;
	}

	public int getLayers() {
		return layers;
	}

	public void setLayers(int layers) {
		this.layers = layers;
	}

	public int getChunks() {
		return chunks;
	}

	public void setChunks(int chunks) {
		this.chunks = chunks;
	}

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	public void setBufferedImage(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
	}

	public short getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(short chunkSize) {
		this.chunkSize = chunkSize;
	}

	public short getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(short imageWidth) {
		this.imageWidth = imageWidth;
	}

	public short getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(short imageHeight) {
		this.imageHeight = imageHeight;
	}

	public float getStroke() {
		return stroke;
	}

	public void setStroke(float stroke) {
		this.stroke = stroke;
	}

	public ParsedImagePreview getParsedImage() {
		return parsedImage;
	}

	public void setParsedImage(ParsedImagePreview parsedImage) {
		this.parsedImage = parsedImage;
	}

	public ImageIcon getImageIcon() {
		return imageIcon;
	}

	public void setImageIcon(ImageIcon imageIcon) {
		this.imageIcon = imageIcon;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFigure() {
		return figure;
	}

	public void setFigure(String figure) {
		this.figure = figure;
	}

	public List<List<Points>> getForDrawContainer() {
		return forDrawContainer;
	}

	public void setForDrawContainer(List<List<Points>> forDrawContainer) {
		this.forDrawContainer = forDrawContainer;
	}

	public List<List<Points>> getAllLayersContainer() {
		return allLayersContainer;
	}

	public void setAllLayersContainer(List<List<Points>> allLayersContainer) {
		this.allLayersContainer = allLayersContainer;
	}

	public List<List<Point>> getChosedLayers() {
		return chosedLayers;
	}

	public void setChosedLayers(List<List<Point>> finalList) {
		this.chosedLayers = finalList;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	public List<List<Point>> getPathsPointList() {
		return pathPointsList;
	}

	public void setPathsPointList(List<List<Point>> finalList) {
		pathPointsList = finalList;
	}

}
