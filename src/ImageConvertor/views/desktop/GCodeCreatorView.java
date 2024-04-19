package ImageConvertor.views.desktop;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ImageConvertor.core.Controller;

@SuppressWarnings("serial")
public class GCodeCreatorView extends JDialog {

	private List<Component> components = new ArrayList<>();
	private List<String> settings;
	private int size;
	private Controller controller;
	int width = 500;
	int height = 200;

	public GCodeCreatorView(Controller c) {
		controller = c;
		getPenUpCommand();
		getPenDownCommand();
		getDelayCommand();
		getScaleCommand();
		getButtons();
		setTitle(controller.getLocaleText("create_gcode"));
		setModal(true);
		setResizable(false);
		// setLocationRelativeTo(null);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int) (d.getWidth() / 2 - width / 2), (int) (d.getHeight() / 2 - height / 2));
		setLayout(new GridLayout(components.size(), 1));
		components.stream().forEach(e -> add(e));
		setSize(width, height);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private void getPenUpCommand() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel(controller.getLocaleText("pen_up"));
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);

		size = text.getFont().getSize();

		JTextField input = new JTextField();
		input.setAlignmentX(CENTER_ALIGNMENT);
		input.setAlignmentY(CENTER_ALIGNMENT);
		input.setText("M5");
		input.setFont(text.getFont().deriveFont(size));

		container.add(text);
		container.add(input);

		components.add(container);
	}

	private void getPenDownCommand() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel(controller.getLocaleText("pen_down"));
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		JTextField input = new JTextField();
		input.setText("M3 S40");
		input.setFont(text.getFont().deriveFont(size));

		container.add(text);
		container.add(input);
		components.add(container);
	}

	private void getDelayCommand() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel(controller.getLocaleText("delay"));
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		JTextField input = new JTextField();
		input.setText("G4 P0.2");
		input.setFont(text.getFont().deriveFont(size));

		container.add(text);
		container.add(input);
		components.add(container);
	}

	private void getScaleCommand() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel(controller.getLocaleText("scale"));
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		JTextField input = new JTextField();
		input.setText("0.955");
		input.setFont(text.getFont().deriveFont(size));

		container.add(text);
		container.add(input);
		components.add(container);
	}

	private void getButtons() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));
		
		JButton okButton = new JButton(controller.getLocaleText("ok"));		
		okButton.addActionListener((e) -> {
			List<String>temp=new ArrayList<>();
			for (Component c : components) {
				if (c instanceof JPanel panel) {
					for (Component jp : panel.getComponents()) {
						if (jp instanceof JTextField text) {
							temp.add(text.getText());
						}
					}
				}
			}
			settings=temp;
			//System.out.println(settings);
			//dispose();
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		});

		JButton cancelButton = new JButton(controller.getLocaleText("cancel"));
		cancelButton.addActionListener(e -> {
			//dispose();
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		});

		container.add(okButton);
		container.add(cancelButton);

		components.add(container);
	}

	public List<String> getSettings() {
		return settings;
	}

}
