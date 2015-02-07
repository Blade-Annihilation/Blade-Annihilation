package com.bladeannihilation.main;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JFrame;

public class Game {

   //public static final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
	
	public static final int initialHeight = 540;
	public static final int initialWidth = 720;
	public static final Config c = new Config();
	private static JFrame frame;
	
	public static GamePanel gp;
	
	public static void main(String[] args) {
		Languages.initialize(c.language);
		frame = new JFrame(Languages.TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Image icon = Resources.getImage("icon.png");
		frame.setIconImage(icon);
		junkForOSX(frame, icon);
		gp = new GamePanel(initialWidth, initialHeight);
		frame.setContentPane(gp);
		frame.pack();
		frame.setMinimumSize(new Dimension(initialWidth, initialHeight));
		frame.setLocationRelativeTo(null);
		frame.addComponentListener(new ComponentListener() {
		    public void componentResized(ComponentEvent e) {
		    	System.out.println("Resize occured");
		    	GamePanel.sizeChangeUpdate = true;
		        gp.setDimensions(((JFrame)e.getSource()).getContentPane().getSize());       
		    }

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		frame.setVisible(true);
		//device.setFullScreenWindow(frame);
		gp.beginRun();
	}
	
	public static void setTitle(String title) {
		frame.setTitle(title);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void junkForOSX(Window window, Image icon) {
	    try {
	        Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
	        Class params[] = new Class[]{Window.class, Boolean.TYPE};
	        Method method = util.getMethod("setWindowCanFullScreen", params);
	        method.invoke(util, window, true);
	    } catch (ClassNotFoundException e1) {
	    } catch (Exception e) {
	        System.out.println("OS X Fullscreen FAIL");
	    }
	    try {
	    	Class util = Class.forName("com.apple.eawt.Application");
	        Method getApplication = util.getMethod("getApplication", new Class[0]);
	        Object application = getApplication.invoke(util);
	        Class params[] = new Class[1];
	        params[0] = Image.class;
	        Method setDockIconImage = util.getMethod("setDockIconImage", params);
	        setDockIconImage.invoke(application, icon);
	    } catch (IllegalAccessException e) {
			System.out.println("Error setting dock icon for OSX #1");
			//e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.out.println("Error setting dock icon for OSX #2");
			//e.printStackTrace();
		} catch (InvocationTargetException e) {
			System.out.println("Error setting dock icon for OSX #3");
			//e.printStackTrace();
		} catch (NoSuchMethodException e) {
			System.out.println("Error setting dock icon for OSX #4");
			//e.printStackTrace();
		} catch (SecurityException e) {
			System.out.println("Error setting dock icon for OSX #5");
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Error setting dock icon for OSX #6");
			//e.printStackTrace();
		}
	}
}
