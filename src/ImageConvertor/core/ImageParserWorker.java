package ImageConvertor.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

import ImageConvertor.data.Points;

public class ImageParserWorker {

	private Controller controller;
	private ExecutorService exec = Executors.newFixedThreadPool(Controller.N_THREADS * 2);
	private CompletionService<List<Points>> service = new ExecutorCompletionService<List<Points>>(exec);
	private ArrayBlockingQueue<String> queue;

	public ImageParserWorker(Controller c, ArrayBlockingQueue<String> queue) {
		controller = c;
		this.queue = queue;
	}

	@SuppressWarnings("unused")
	public List<List<Points>> doTask() {
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
		List<List<Points>> results = new ArrayList<List<Points>>();
		for (int i = 0; i < steps.size() - 1; i++) {

			SingleThreadParseImage temParseImage = new SingleThreadParseImage(controller);
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
			if (controller.isCanceled()) {
				exec.shutdownNow();
				break;
			}
			try {
				Future<List<Points>> temp = service.take();
				if (temp.isDone()) {
					List<Points> res = temp.get();
					results.add(res);
					queue.offer(String.format(controller.getLocaleText("current_layer") + ": %d", currentTask++));
				} else {
					if (controller.isCanceled()) {
						exec.shutdownNow();
						JOptionPane.showMessageDialog(null, controller.getLocaleText("cancel_task"),
								controller.getLocaleText("canceled"), JOptionPane.INFORMATION_MESSAGE);
						return null;
					}
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
			if (list == null) {
				continue;
			}
			if (list.size() < 1) {
				iterator.remove();
			}
		}
		if (results == null) {
			return null;
		}
		Collections.sort(results, (o1, o2) -> {
			return Float.compare(o2.get(0).layer, o1.get(0).layer);
		});
		// STATE.setAllLayersContainer(results);
		return results;
	}
}
