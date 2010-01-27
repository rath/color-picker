package com.xrath.tools.colorpicker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class PickerFrame extends JFrame {

	private static final long serialVersionUID = 1695333312112668757L;
	
	private DataFlavor DF;
	
	public PickerFrame() {
		createComponents();
	}

	private void createComponents() {
		setLayout(new BorderLayout());
		
		JLabel label = new JLabel("Drop here!", SwingConstants.CENTER);
		label.setFont(new Font("Dialog", Font.BOLD, 20));
		add(label, "Center");
		
		
		try {
			DF = new DataFlavor("application/x-java-file-list; class=java.util.List");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		DropTargetListener l = new DropTargetAdapter() {
			@Override
			public void dragEnter(DropTargetDragEvent e) {
				boolean support = e.isDataFlavorSupported(DF);
				if( support ) {
					e.acceptDrag(DnDConstants.ACTION_COPY);
				} else {
					e.rejectDrag();
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			public void drop(DropTargetDropEvent e) {
				e.acceptDrop(DnDConstants.ACTION_COPY);
				
				Transferable t = e.getTransferable();
				List<File> fileList = null;
				try {
					fileList = (List<File>)t.getTransferData(DF);
				} catch (UnsupportedFlavorException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				for(File f : fileList) {
					try {
						openImage(f);
					} catch( Exception ex ) {
						ex.printStackTrace();
					}
				}
				
				e.dropComplete(true);
			}

		};
		
		DropTarget drop = new DropTarget(label, l);
		label.setDropTarget(drop);
	}

	protected void openImage(File f) throws Exception {
		BufferedImage img = null; 
		try {
			img = ImageIO.read(f);
		} catch( Exception e ) {
			return;
		}
		
		int width = img.getWidth();
		int height = img.getHeight();
		
		ImageFrame frame = new ImageFrame(img);
		frame.setTitle(f.getName() + " " + width + "x" + height);
		frame.setLocation(50, 50);
		frame.setSize(width+80, height+80);
		frame.setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		
		final PickerFrame f = new PickerFrame();
		f.setTitle("Hex Picker");
		f.setSize(180, 100);
		f.setLocation((size.width - 180)/2, (size.height - 100)/2);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setResizable(false);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				f.setVisible(true);				
			}
		});		
	}
}
