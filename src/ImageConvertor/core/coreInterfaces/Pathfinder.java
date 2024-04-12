package ImageConvertor.core.coreInterfaces;

import java.util.List;

import ImageConvertor.data.Chunk;

public interface Pathfinder {
	public List<Chunk> getPath();

	public void cancelTask();
}
