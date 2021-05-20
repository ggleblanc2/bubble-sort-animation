package com.ggl.testing;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class BubbleSortAnimation implements Runnable {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new BubbleSortAnimation());
	}
	
	private int[] array;
	
	private DrawingPanel drawingPanel;
	
	private JTextField integerArrayField;
	
	public BubbleSortAnimation() {
		this.array = new int[] { 8, 5, 26, 4, 9, 3, 2, 7, 1 };
	}

	@Override
	public void run() {
		JFrame frame = new JFrame("Bubble Sort Animation");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		drawingPanel = new DrawingPanel(this);
		frame.add(createEntryPanel(), BorderLayout.BEFORE_FIRST_LINE);
		frame.add(drawingPanel, BorderLayout.CENTER);
		frame.add(createButtonPanel(), BorderLayout.AFTER_LAST_LINE);
		
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}
	
	private JPanel createEntryPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JLabel label = new JLabel("Integers to sort:");
		panel.add(label);
		
		integerArrayField = new JTextField(30);
		panel.add(integerArrayField);
		
		JButton button = new JButton("Submit");
		button.addActionListener(new ArrayListener(this));
		panel.add(button);
		
		return panel;
	}
	
	private JPanel createButtonPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JButton button = new JButton("Sort Integers");
		button.addActionListener(new SortButtonListener(this));
		panel.add(button);
		
		return panel;
	}
	
	public int[] getArray() {
		return array;
	}

	public void setArray(int[] array) {
		this.array = array;
	}

	public JTextField getIntegerArrayField() {
		return integerArrayField;
	}
	
	public void repaint() {
		drawingPanel.repaint();
	}
	
	public void setColorArray(Color[] colorArray) {
		drawingPanel.setColorArray(colorArray);
	}

	public class DrawingPanel extends JPanel {
		
		private static final long serialVersionUID = 1L;
		
		private int blockWidth;
		private int margin;
		
		private BubbleSortAnimation frame;
		
		private Color[] colorArray;

		public DrawingPanel(BubbleSortAnimation frame) {
			this.frame = frame;
			this.blockWidth = 96;
			this.margin = 20;
			this.colorArray = new Color[array.length];
			for (int index = 0; index < array.length; index++) {
				colorArray[index] = Color.WHITE;
			}
			
			int width = array.length * blockWidth +  2 * margin;
			int height = blockWidth + 2 * margin;
			this.setBackground(Color.WHITE);
			this.setPreferredSize(new Dimension(width, height));
		}
		
		public void setColorArray(Color[] colorArray) {
			this.colorArray = colorArray;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			int[] array = frame.getArray();
			int x = margin;
			int y = margin;
			
			for (int index = 0; index < array.length; index++) {
				paintDigit(g, array[index], colorArray[index], x, y);
				x += blockWidth;
			}
		}
		
		private void paintDigit(Graphics g, int digit, Color color, int x, int y) {
			String text = Integer.toString(digit);
			
			g.setColor(color);
			g.fillRect(x, y, blockWidth, blockWidth);
			
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(new BasicStroke(5f));
			g2d.setColor(Color.BLACK);
			g2d.drawRect(x, y, blockWidth, blockWidth);
			
			Font font = getFont().deriveFont(Font.BOLD, 54f);
			g2d.setFont(font);
			FontMetrics fm = g2d.getFontMetrics();
			Rectangle2D r = fm.getStringBounds(text, g2d);
			int a = (blockWidth - (int) r.getWidth()) / 2 + x;
	        int b = (blockWidth - (int) r.getHeight()) / 2 + fm.getAscent() + y;
	        g2d.drawString(text, a, b);
		}
	}
	
	public class ArrayListener implements ActionListener {
		
		private BubbleSortAnimation frame;

		public ArrayListener(BubbleSortAnimation frame) {
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			String text = frame.getIntegerArrayField().getText().trim();
			String[] parts = text.split("\\s+");
			
			int[] array = new int[parts.length];
			for (int index = 0; index < parts.length; index++) {
				try {
					array[index] = Integer.valueOf(parts[index]);
				} catch (NumberFormatException e) {
					return;
				}
			}
			
			frame.getIntegerArrayField().setText("");
			frame.setColorArray(createColorArray(Color.WHITE));
			frame.setArray(array);
			frame.repaint();
		}
		
		private Color[] createColorArray(Color color) {
			Color[] colorArray = new Color[array.length];
			
			for (int index = 0; index < array.length; index++) {
				colorArray[index] = color;
			}
			
			return colorArray;
		}
		
	}
	
	public class SortButtonListener implements ActionListener {
		
		private BubbleSortAnimation frame;

		public SortButtonListener(BubbleSortAnimation frame) {
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			SortListener listener = new SortListener(frame);
			Timer timer = new Timer(2000, listener);
			listener.setTimer(timer);
			timer.setInitialDelay(0);
			timer.start();
		}
		
	}
	
	public class SortListener implements ActionListener {
		
		private int[] array;
		
		private int currentIndex;
		private int endIndex;
		private int stage;
		
		private BubbleSortAnimation frame;
		
		private Timer timer;

		public SortListener(BubbleSortAnimation frame) {
			this.frame = frame;
			this.array = frame.getArray();
			this.currentIndex = 0;
			this.endIndex = array.length - 2;
			this.stage = 1;
		}

		public void setTimer(Timer timer) {
			this.timer = timer;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			Color[] colorArray = createColorArray(Color.WHITE, Color.WHITE);
			
			if (endIndex < 0) {
				colorArray = createColorArray(Color.GREEN, Color.GREEN);
				frame.setColorArray(colorArray);
				frame.repaint();
				timer.stop();
				return;
			}
			
			switch (stage) {
			case 1:
				colorArray = createColorArray(Color.YELLOW, Color.WHITE);
				if (array[currentIndex] > array[currentIndex + 1]) {
					stage = 2;
				} else {
					currentIndex++;
				}
				break;
			case 2:
				colorArray = createColorArray(Color.RED, Color.WHITE);
				stage = 3;
				break;
			case 3:
				int temp = array[currentIndex];
				array[currentIndex] = array[currentIndex + 1];
				array[currentIndex + 1] = temp;
				
				frame.setArray(array);
				stage = 1;
				currentIndex++;
				break;
			}
			
			frame.setColorArray(colorArray);
			frame.repaint();
			
			if (currentIndex > endIndex) {
				currentIndex = 0;
				endIndex--;
			} 
		}
		
		private Color[] createColorArray(Color color, Color baseColor) {
			Color[] colorArray = new Color[array.length];
			
			for (int index = 0; index < array.length; index++) {
				colorArray[index] = baseColor;
			}
			
			colorArray[currentIndex] = color;
			colorArray[currentIndex + 1] = color;
			
			return colorArray;
		}
		
	}

}
