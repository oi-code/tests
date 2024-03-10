package ImageConvertor.views.desktop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.JTextComponent;

import ImageConvertor.core.Controller;
import ImageConvertor.data.State;

@SuppressWarnings("serial")
public class ViewProccessStatus extends JDialog implements Runnable {

	Controller controller;
	JTextComponent text;
	JScrollPane scrollPane;
	float size = 0f;
	private Queue<String> messageExchanger;

	public ViewProccessStatus(Controller controller, Queue<String> messageExchanger) {
		// super(View.getInstance(), true);
		this.controller = controller;
		this.messageExchanger = messageExchanger;
		setLayout(new BorderLayout());
		setTitle(controller.getLocaleText("processing"));
		setAlwaysOnTop(true);
		Thread.currentThread().setName("process_window_thread_helper");
		add(getButtonAndLoadingImageLabel(), BorderLayout.PAGE_END);
		add(getTextLabel());
		setSize(500, 200);
		setLocationRelativeTo(null);
		setFocusable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	@Override
	public void run() {
		setVisible(true);
		controller.setProcessWindowShowed(true);
		try {
			while (!Thread.currentThread().isInterrupted() && !controller.isProcessed() && !controller.isCanceled()) {
				String nextText = "";
				nextText = messageExchanger.poll();
				if (nextText == null) {
					continue;
				}
				text.setText(text.getText() + "\n" + nextText);
				if (text.getText().length() > 5000) {
					text.setText(text.getText().substring(text.getText().length() - 1000, text.getText().length()));
				}
				text.setCaretPosition(text.getText().length());
			}
		} finally {
			controller.setProcessWindowShowed(false);
			// dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			dispose();
		}
		return;
	}

	JComponent getImageLabel() {
		InputStream is = State.class.getResourceAsStream("images/ezgif-4-66d70871c9.gif");
		ImageIcon icon = null;
		try {
			icon = new ImageIcon(is.readAllBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		// ImageIcon icon = new ImageIcon("images/ezgif-4-66d70871c9.gif");
		icon.setImage(icon.getImage().getScaledInstance(140, 40, Image.SCALE_DEFAULT));
		JLabel label = new JLabel(icon);
		return label;
	}

	JComponent getButtonAndLoadingImageLabel() {
		JPanel label = new JPanel();
		label.setLayout(new GridLayout(1, 2));
		label.add(getImageLabel());
		label.add(getCancelButton());
		return label;
	}

	JComponent getTextLabel() {
		JTextArea text = new JTextArea();
		text.setFont(text.getFont().deriveFont(size));
		text.setSize(300, 50);
		text.setFocusable(false);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		// scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// scroll.setAutoscrolls(true);
		scroll.setViewportView(text);
		this.text = text;
		return scroll;
	}

	JComponent getCancelButton() {
		/*
		 * ImageIcon icon = new ImageIcon("images/ezgif-4-66d70871c9.gif");
		 * icon.setImage(icon.getImage().getScaledInstance(140, 40, Image.SCALE_DEFAULT));
		 * JButton button = new JButton("cancel task", icon);
		 */
		JButton button = new JButton();
		size = button.getFont().getSize();
		button.setText(controller.getLocaleText("cancel_task"));
		button.setFocusable(false);
		button.setSelected(false);
		button.setEnabled(true);
		Color color = button.getBackground();
		button.addActionListener((e) -> {
			button.setEnabled(false);
			button.setBackground(color);
			controller.cancelTask();
		});
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (button.isEnabled())
					button.setBackground(Color.red);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (button.isEnabled())
					button.setBackground(color);
			}
		});
		return button;
	}

}
