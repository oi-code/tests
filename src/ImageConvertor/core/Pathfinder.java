package ImageConvertor.core;

import java.util.List;

import ImageConvertor.data.Chunk;

public interface Pathfinder {
	public List<List<Chunk>> getSequencesOfClouds();

	public void cancelTask();
}
