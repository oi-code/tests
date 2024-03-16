package ImageConvertor.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ImageConvertor.data.Chunk;

public class ImageParserWorker {

	private Controller controller;
	private ExecutorService exec = Executors.newFixedThreadPool(Controller.N_THREADS * 2);
	private CompletionService<List<Chunk>> service = new ExecutorCompletionService<List<Chunk>>(exec);
	private Queue<String> queue;

	public ImageParserWorker(Controller c, ConcurrentLinkedQueue<String> queue) {
		controller = c;
		this.queue = queue;
	}

	public List<List<Chunk>> doTask() {
		queue.offer(controller.getLocaleText("processing"));
		Runtime.getRuntime().gc();
		float lumStep = 1f / controller.getLayers();
		float start = 0f;
		List<Float> steps = new ArrayList<>();
		steps.add(start);
		while (start < 1f) {
			start = start + lumStep;
			steps.add(start);
		}
		List<List<Chunk>> results = new ArrayList<List<Chunk>>();
		byte currentAccaptedTask = 0;
		for (int i = 0; i < steps.size() - 1; i++) {

			SingleThreadParseImage temParseImage = new SingleThreadParseImage(controller);
			temParseImage.setMinLum(steps.get(i));
			temParseImage.setMaxLum(steps.get(i + 1));
			temParseImage.setLayer(currentAccaptedTask);
			service.submit(new Callable<List<Chunk>>() {
				@Override
				public List<Chunk> call() throws Exception {
					List<Chunk> result = temParseImage.getChunks();
					return result;
				}
			});
			currentAccaptedTask++;
			queue.offer(
					String.format(controller.getLocaleText("current_Accepted_layer") + ": %d", currentAccaptedTask));

		}
		int currentDoneTask = 0;

		while (currentDoneTask < currentAccaptedTask) {
			if (controller.isCanceled()) {
				exec.shutdownNow();
				break;
			}
			Future<List<Chunk>> temp = service.poll();
			if (temp != null) {
				List<Chunk> res;
				try {
					res = temp.get();
					results.add(res);
					queue.offer(String.format(controller.getLocaleText("current_Done_Layer") + ": %d",
							Math.round(res.get(0).layer)));

					currentDoneTask++;
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}

		}
		/*
		 * for (int i = 0; i < steps.size() - 1; i++) {
		 * if (controller.isCanceled()) {
		 * exec.shutdownNow();
		 * break;
		 * }
		 * try {
		 * Future<List<Node>> temp = service.take();
		 * if (temp.isDone()) {
		 * List<Node> res = temp.get();
		 * results.add(res);
		 * queue.offer(String.format(controller.getLocaleText("current_Done_Layer") + ": %d",
		 * currentDoneTask++));
		 * } else {
		 * if (controller.isCanceled()) {
		 * exec.shutdownNow();
		 * JOptionPane.showMessageDialog(null, controller.getLocaleText("cancel_task"),
		 * controller.getLocaleText("canceled"), JOptionPane.INFORMATION_MESSAGE);
		 * return null;
		 * }
		 * continue;
		 * }
		 * } catch (InterruptedException | ExecutionException e) {
		 * JOptionPane.showMessageDialog(null,
		 * "Fatal Error:\n" + e.getCause().getMessage() + ".\nPress \"OK\" to exit.", "Exception occured",
		 * JOptionPane.ERROR_MESSAGE);
		 * }
		 * }
		 */
		for (Iterator<List<Chunk>> iterator = results.iterator(); iterator.hasNext();) {
			List<Chunk> list = iterator.next();
			if (list == null) {
				continue;
			}
			if (list.size() < 1) {
				iterator.remove();
			}
		}
		if (results != null) {
			Collections.sort(results, (o1, o2) -> {
				return Float.compare(o2.get(0).layer, o1.get(0).layer);
			});
		}
		// STATE.setAllLayersContainer(results);
		return results;
	}
}
