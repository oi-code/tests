package mvcTest;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;


import org.springframework.context.annotation.Configuration;

@Configuration
public class OtherConfigurations {

    // @PostConstruct
    public void initialization() {
	class OutputStreamChanger extends OutputStream {
	    List<String> lines = new ArrayList<String>();
	    StringBuilder sb = new StringBuilder();

	    @Override
	    public void write(int b) throws IOException {
		if (b == '\n') {
		    lines.add(sb.toString());
		    sb = new StringBuilder();
		} else {
		    sb.append((char) b);
		}
	    }

	    public List<String> getLines() {
		List<String> copy = new ArrayList<String>(lines);
		lines.removeAll(copy);
		return copy;
	    }
	}

	PrintStream defaultOut = System.out;
	OutputStreamChanger osc = new OutputStreamChanger();
	PrintStream newOut = new PrintStream(osc);
	System.setOut(/* new PrintStream(osc) */newOut);
	System.setErr(newOut);

	Thread t = new Thread(() -> {
	    JFrame jframe = new JFrame("Console output");
	    jframe.setSize(1500, 500);
	    jframe.setLocationRelativeTo(null);
	    JTextArea text = new JTextArea();
	    text.setLineWrap(true);
	    text.setWrapStyleWord(true);
	    JScrollPane scroll = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
		    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
	    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    // scroll.setAutoscrolls(true);
	    scroll.setViewportView(text);

	    JButton b = new JButton("exit");
	    b.addActionListener(e -> {
		System.exit(0);
	    });
	    b.setSize(100, 20);
	    JPanel panel = new JPanel(new BorderLayout());
	    panel.add(b, BorderLayout.PAGE_START);
	    panel.add(scroll, BorderLayout.PAGE_END);

	    jframe.add(panel);

	    while (!Thread.interrupted()) {
		try {
		    for (String s : osc.getLines()) {
			// defaultOut.println("PEPEGA: " + s);
			text.setText(text.getText() + s + "\n" + "-" + "\n");
			text.setCaretPosition(text.getDocument().getLength() - 1);
		    }
		    if (!jframe.isVisible()) {
			jframe.setVisible(true);
		    }
		    defaultOut.flush();
		} catch (Exception e) {
		    continue;
		}
	    }
	});
	t.setDaemon(true);
	t.start();
    }
}
