package ImageConvertor.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

import ImageConvertor.data.Points;

public class ImageParserWorker {

	private Controller controller;
	private CompletionService<List<Points>> service;

	public ImageParserWorker(Controller c) {
		controller = c;
		service = controller.getService();
	}

	public List<List<Points>> doTask() {
		controller.messageExchanger.clear();
		controller.offerMessage(controller.getLocaleText("processing"));
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
			try {
				Future<List<Points>> temp = service.take();
				if (temp.isDone()) {
					List<Points> res = temp.get();
					results.add(res);
					controller.messageExchanger
							.offer(String.format(controller.getLocaleText("current_layer") + ": %d", currentTask++));
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
		// STATE.setAllLayersContainer(results);
		return results;
	}
}
