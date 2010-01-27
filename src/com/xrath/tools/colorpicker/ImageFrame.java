package com.xrath.tools.colorpicker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImageFrame extends JFrame implements ClipboardOwner {
	private static final long serialVersionUID = -4223357606871458418L;
	private BufferedImage image;
	private ImageCanvas canvas;
	private JLabel status;
	private ColorViewer viewer;
	private String hex;
	
	private float scaleFactor = 1.0f;
	
	public ImageFrame( BufferedImage img ) {
		this.image = img;
		
		setLayout(new BorderLayout());
		
		viewer = new ColorViewer();
		viewer.setPreferredSize(new Dimension(40, 40));
		add(viewer, "East");
		
		canvas = new ImageCanvas();
		canvas.setBackground(Color.white);
		canvas.addMouseMotionListener(new MouseMotionListener(){
			@Override
			public void mouseDragged(MouseEvent e) {
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				showHex(e.getPoint());
				
			}});
		canvas.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				if( hex==null )
					return;
				StringSelection ss = new StringSelection(hex);
				Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
				clip.setContents(ss, ImageFrame.this);
				
				status.setText( hex + " is copied to clipboard.");
			}
		});
		canvas.addMouseWheelListener(new MouseWheelListener(){
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int rotate = e.getWheelRotation();
				if( rotate < 0 ) { // up
					scaleFactor += 0.15;
				} else 
				if( rotate > 0 ) { // down 
					if( scaleFactor > 0.2f ) 
						scaleFactor -= 0.15;
				}
				
				if( scaleFactor < 1.05f && scaleFactor > 0.95f ) 
					scaleFactor = 1.0f;
				
				canvas.repaint();
				
				status.setText("Scaled to " + (int)((100* scaleFactor)) + "%");
				
			}});
		add(canvas, "Center");
		
		status = new JLabel("Ready");
		status.setBorder(BorderFactory.createEtchedBorder());
		add(status, "South");
		
		addWindowListener(new WindowAdapter(){

			@Override
			public void windowClosed(WindowEvent e) {
				image.flush();
				
			}
		});
		
		addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				if( e.getModifiers()==InputEvent.META_MASK && e.getKeyCode()==87 )
					dispose();
			}
		});

	}
	
	protected void showHex(Point p) {
		p.x -= canvas.x;
		p.y -= canvas.y;
		
		p.x = (int)((float)p.x / scaleFactor); 
		p.y = (int)((float)p.y / scaleFactor); 
		
		if( p.x < 0 || p.x >= image.getWidth() || p.y < 0 || p.y >= image.getHeight() )
			return;
		
		int color = image.getRGB(p.x, p.y);
		int r = color>>16 & 0xff;
		int g = color>>8 & 0xff;
		int b = color>>0 & 0xff;
		
		viewer.setColor(new Color(color));
		viewer.repaint();
		
		this.hex = "#" + h(r) + h(g) + h(b);
		status.setText(p.x + "/" + p.y + " " + hex);
	}

	private String h(int r) {
		String s = Integer.toHexString(r);
		if( s.length()==1 )
			s = "0" + s;
		return s.toUpperCase();
	}

	class ImageCanvas extends JComponent {
		private static final long serialVersionUID = 1L;
		
		public int x;
		public int y;

		private int imageWidth;
		private int imageHeight;

		@Override
		protected void paintComponent( Graphics og ) {
			Graphics2D g = (Graphics2D)og;
			imageWidth = (int) (image.getWidth() * scaleFactor);
			imageHeight = (int) (image.getHeight() * scaleFactor);
			
			this.x = (getWidth() - imageWidth) / 2;
			this.y = (getHeight() - imageHeight) / 2;
			
			g.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED));
			g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF));
			g.drawImage(image, x, y, imageWidth, imageHeight, this);
		}
	}
	
	class ColorViewer extends JComponent {
		private static final long serialVersionUID = 1L;
		private Color color = Color.black;
		@Override
		protected void paintComponent( Graphics g ) {
			g.setColor(this.color);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		public void setColor(Color color) {
			this.color = color;
		}
		public Color getColor() {
			return color;
		}
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		
	}
}
