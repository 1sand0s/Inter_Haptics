/* Copyright (C) 2015 ADITYA T 
 * ORG: Interactive Spaces
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.*/

package IS_UltraSonic;

import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.*;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.PrintStream;
import java.util.ArrayList;
import javax.swing.*;

public class Ultra_main extends JFrame implements WindowListener
{
	
	static JTabbedPane tabs;
	static Ultra_real real;
	static Ultra_virtual virtual;
	static JDesktopPane d1,d2;
	public static void main(String[] args) 
	{
	   new Ultra_main().configure();
	}
	Ultra_main()
	{
	   setSize(800,640);
	   setTitle("Inter_Haptics_GSOC");
           addWindowListener(this);
	}
	public void windowActivated(WindowEvent arg0) 
	{
		Ultra_virtual.stop.setState(false);
		/* If the window is activated, that is if the mouse has been clicked inside the container(Frame)
		 * Then uncheck 'stop' cehckbox and resume the rendering process*/
	}
	public void windowClosed(WindowEvent arg0) 
	{
					
	}
	public void windowClosing(WindowEvent arg0)
	{
		try
		{
			if((!Ultra_virtual.OS_Check) && Ultra_virtual.connect)
			{
				Ultra_virtual.check2=true;
				Ultra_virtual.disconnect_serial();
				System.exit(0);
			}
			else
			{
				System.exit(0);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
					
	}
	public void windowDeactivated(WindowEvent arg0) 
	{
		Ultra_virtual.stop.setState(true);
		/* If the mouse has been clicked outside the container , check the 'stop' checkbox 
		 * and stop the rendering process until the container is made active again*/
					
	}

	public void windowDeiconified(WindowEvent arg0) 
	{
		Ultra_virtual.stop.setState(false);
		/* If the container has been maximized ,then uncheck the 'stop' checkbox and resume
		 * the rendering process*/
					
	}

	public void windowIconified(WindowEvent arg0) 
	{
		Ultra_virtual.stop.setState(true);
		/* If the container(Frame) has been minimized or iconified ,check the 'stop' checkbox
		 * and stop the rendering process to prevent wastage of system resource*/
					
	}

	public void windowOpened(WindowEvent arg0) 
        {
					// TODO Auto-generated method stub
					
	}
        
        private void configure() 
	{
    	   tabs=new JTabbedPane();
    	   virtual=new Ultra_virtual();
    	   d1=new JDesktopPane();
    	   d2=new JDesktopPane();
    	   real=new Ultra_real(virtual);
    	   d1.add(virtual);
    	   d2.add(real);
    	   tabs.add("Virtual",d1);
    	   tabs.add("Real",d2);
	   add(tabs);
	   setVisible(true);
	}
}
/*                        ================= WORKING OF THE ALGORITHM ================
 
 
 						      / /O|                                        
                                           (Lx) /    /    |
                          (Emitters)    |/        /       |(Parallel Focal Distance)
                                        |      / R        |
                                        |   /Sine_phi	  |
              mid-transmitter (at accx) |/_ _ _ _ _ _ _ _ |	O=focal point
                                        |  	          |	R= Phase_length from mid transmitter(at accx) to focal point 
                                        |	          |	Sine_phi= Sine of the angle of inclination of 'R'
                                        |	          |	Lx= Phase_length from nth transmitter to focal point
                                        |	          |
						          |

 Therefore the nth transmitter will have to cover a distance Lx to reach 'O' in contrast to the transmitter at accx
 which requires a distance of R to be travelled to reach 'O'.Therefore since the speed of the ultrasonic wave emitted
 is the same for both the transmitters ,this causes a phase delay of abs(R-Lx)/speed, which may or may not result in
 constructive interference. For constructive interference to take place the ultrasonic waves from nth and mid transmitters
 must reach 'O' at the same time which implies the excitation of the nth transmitter in the above case must be set back
 by a time factor abs(R-Lx)/speed, which counteracts the loss in time for the wave from mid transmitter to cover the extra
 length R-Lx, however for transmitter below mid transmitter , Lx is greater than R hence these will have to excited in 
 advance hence as given below ,we will use a variable 'tog' to account for both these variations
========================================================================================================================== 
*/
class Ultra_virtual extends JInternalFrame implements MouseMotionListener,MouseListener,ActionListener,AdjustmentListener,ComponentListener,LayoutManager,ItemListener
{
	static JScrollBar frequency,Resolution,em_size; 
	/* frequency-> Controls the frequency of the emitted ultrasonic wave
	 * Resolution-> Controls the relative grid size allowing magnification of the canvas
	 * em_size-> Controls the diameter of the emitter */
	static Ultra_canvas can; 
	/* For drawing the ultrasonic waves, the canvas is differentiated from the frame to facilitate easy paint job */
	static int transmitters=-1; 
	// For Keeping count of number of transmitters
	static int dsX = -1, dsY;
	// For keeping track of x and y co-ordinates of Mouse click on the canvas
	static Method Setup,Draw,Write,Available,List;
	static Object PApplet,Serial;
	static Class papplet,serial;
	/* Setup-> To dynamically load processing 'setup' method = Configure all parameters
	 * Draw-> "                             " 'draw' method = repeated loop
	 * Write->"				" 'write' method = part of Serial class to write to port
	 * Available->"				" 'available' method = to check is serial data is available
	 * List->"				" 'list' method = to list all COM ports in usage
	 * PApplet-> To hold new instance of processing's PApplet class
	 * Serial->"					" Serial class*/
	static int selemitter; 
	/* 'selemitter' -> For checking if any transmitter has been selected to be dragged,deleted etc,occurs 
	 * when mouse hovers over the emitter */
	static String path_to_jar;
	/* To hold the path to processing jars*/
	static int add=0; 
	/* For switching between clear,add emitter,delete emitter blocks on mouse clicks in the canvas*/
	static int accx=0,accy=0; 
	// Points to the mid-value(x,y)of the linearly arranged transmitters
	static int tog; 
	//Required to toggle + or - depending on whether the phase_cal point is < or > accx respectively
	static JButton AddSource,DeleteSource; //For adding and deleting transmitters
	static JButton Clear,PhaseCalc;  //For clearing the canvas and calculating the phase delay
        static JButton Set_Baud_Rate,Connect_serial,Execute,Carrier_freq,Modulated_freq;
        /* Set_Baud_Rate-> setting baud rate for serial communication
         * Connect_serial->establish serial communication
         * Exceute -> Send data to serial port
         * Carrier_freq-> Set Carrier Frequency
         * Modulated_freq-> Set Modulated Frequency*/
	int ww,wh,wox,woy,gx,gy,gxy,wi,hi,si;// window width,window height,window offset x, window offset y, 
	static String med[]={"#800000","#ffffff","#000000","#808080","#0000ff","#000000","#000080","#00ff00"}; 
	// Store the color values in med[] and then instantiate the color array with these values
	static Color med2[]; // Color array soon to be instantiated with med[]
	static Image im; // 'im' -> Images created using the 'source' ,used for updating the canvas with the ultrasonic wave
	static JTextArea text; // For displaying the Phase calculations for each individual transmitter 
	static int[] pixels,surface,pixel,order; 
	/* 'pixels' and 'pixel' are used for grabbing the pixels or modifying the pixels of the images
	 * 'surface'-> not yet implemented, for future use
	 * 'order'-> Required for holding the indices of the transmitters in ascending order of phase_delay*/
	static float[] buf1,buf2,damp; 
	/* 'buf1,buf2' -> hold the perturbed values after disturbance 
	   'damp' -> holds the values to dampen the waves as they propogate away from the source */
	static boolean md,mr; 
	// To process the grid switching between left right and top bottom to obtain perfectly spherical rendering
	static ArrayList<Emitter_loc>loc; 
	/* loc->Holds the x,y on canvas and on screen values of each individual transmitter*/
	static ArrayList<Double>phase_length,phase_del; 
	/* 'phase_length' -> Lx(length from nth transmitter to focal point)
	 * 'phase_del'    -> Time delay to counteract (R-Lx) */
	static Checkbox view_phase_plane,viewreal; // Not yet Implemented
	static Checkbox stop;
	/* stop->Stop the simulation */
	static MemoryImageSource source; 
	/* 'source' -> Acts as source for Image 'im', we modify the pixels and then update the 
	 *  image with the modified pixels*/
	static double l;
	// Multiplier to keep perturbing the medium
	static PixelGrabber p; 
	/* object to grab the pixels of 'source' modify it,update it and create Image 'im' with it*/
	static boolean set=true; //Yet to be implemented
	static boolean check=false; 
	/* check-> To check if focal point has been selected , if yes then change the phase accordingly*/
	static boolean light=true; // To send the data,stil buggy, requires more efficient implementation
	static boolean connect=false; 
	/* connect-> To check whether serial communication has been established with arduino*/
	static boolean check2=false;
	/* check2-> To disable serial communication before closing the port */
	static boolean OS_Check=true;
	/* OS_Check-> To check whether the OS is Windows or Linux*/
	static int baudrate=9600; 
	/* baudrate-> Set default baud rate to 9600 */
	static String com[];
	/* com-> To hold the list of Ports connected to the PC*/
	/* static     //data to arduino ,code still buggy
	{
		System.loadLibrary("blink");
	}*/
	public static native void write(String h);
	/* native method to send data to C which in turn sends it to arduino*/
    Ultra_virtual()
    {
    	// Instantiate the variables
	   can=new Ultra_canvas(this);
	   AddSource=new JButton("Add Emitters");
	   PhaseCalc=new JButton("Phase Calculation");
	   Carrier_freq=new JButton("Set Carrier");
	   Modulated_freq=new JButton("Set Modulated");
	   text=new JTextArea(5,10);
	   stop=new Checkbox("Stop");
	   phase_length=new ArrayList<Double>();
	   phase_del=new ArrayList<Double>();
	   md=mr=true;
	   Clear=new JButton("Clear");
	   DeleteSource=new JButton("Delete Emitters");
	   Set_Baud_Rate=new JButton("Baudrate");
	   Connect_serial=new JButton("Connect to Serial");
	   Execute=new JButton("Send Data");
	   JLabel L=new JLabel("Frequency",JLabel.CENTER);
	   JLabel L1=new JLabel("Resolution",JLabel.CENTER);
	   JLabel L2=new JLabel("Emitter Size");
	   frequency=new JScrollBar(JScrollBar.HORIZONTAL,15,1,1,30);
	   Resolution=new JScrollBar(JScrollBar.HORIZONTAL,110,5,5,400);
	   em_size=new JScrollBar(JScrollBar.HORIZONTAL,7,4,7,20);
	   med2=new Color[8];
	   view_phase_plane=new Checkbox("Phase Plane Plot");
	   viewreal=new Checkbox("view Real mode");
	   loc=new ArrayList<Emitter_loc>();
	   can.addComponentListener(this);
	   can.addMouseMotionListener(this);
	   can.addMouseListener(this);
	   AddSource.addActionListener(this);
	   PhaseCalc.addActionListener(this);
	   AddSource.setActionCommand("ADD_SRC");
	   DeleteSource.setActionCommand("DEL_SRC");
	   Clear.setActionCommand("CLEAR");
	   PhaseCalc.setActionCommand("PHASE");
	   Connect_serial.setActionCommand("CONNECT");
	   Execute.setActionCommand("SEND");
	   Carrier_freq.setActionCommand("CAR_freq");
	   Modulated_freq.setActionCommand("MOD_freq");
	   Clear.addActionListener(this);
	   DeleteSource.addActionListener(this);
	   Set_Baud_Rate.addActionListener(this);
	   stop.addItemListener(this);
	   Set_Baud_Rate.setActionCommand("BAUD");
	   Connect_serial.addActionListener(this);
	   Execute.addActionListener(this);
	   Carrier_freq.addActionListener(this);
	   Modulated_freq.addActionListener(this);
	   frequency.addAdjustmentListener(this);
	   Resolution.addAdjustmentListener(this);
	   em_size.addAdjustmentListener(this);
	   setLayout(this);
	   add(can);
	   add(AddSource);
	   add(PhaseCalc);
	   add(Clear);
	   add(DeleteSource);
	   add(Set_Baud_Rate);
	   add(Connect_serial);
	   add(Execute);
	   add(Carrier_freq);
	   add(Modulated_freq);
	   add(stop);
	   add(view_phase_plane);
	   add(viewreal);
	   add(L);
	   add(frequency);
	   add(L1);
	   add(Resolution);
	   add(L2);
	   add(em_size);
	   add(text);
	   can.setBackground(Color.black);
	   can.setForeground(Color.lightGray);
	   for(int i=0;i<med.length;i++)
	   {
		   med2[i]=Color.decode(med[i]); // Convert the elements held by the string array to color and store
	   }
	   if(System.getProperty("os.name").startsWith("Windows"))
	   {
		/* If OS is windows , use processing to communicate with serial device 
		 * Therefore import all required classes and methods*/
		try
		{
			URL url1=getClass().getResource("Ultra_main.class");
			path_to_jar=url1.toString();
			path_to_jar=path_to_jar.replaceAll("/bin/IS_UltraSonic/Ultra_main.class","/jars");
			path_to_jar=path_to_jar.replaceAll("file:","");
			URL[] url={new URL("jar:file:" + path_to_jar+"!/")};
			URLClassLoader c=new URLClassLoader(url);
			papplet=Class.forName("processing.core.PApplet");
			serial=Class.forName("processing.serial.Serial");
			//Import methods
			Setup=papplet.getMethod("setup");
			Draw=papplet.getMethod("draw");
			Write=serial.getMethod("write",String.class);
			Available=serial.getMethod("available");
			List=serial.getMethod("list");
			OS_Check=false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
			
	   }
	   else
	   {
		/* If OS is any Linux derivatives,then use JNI and C to 
		 * interface the serial device, this improves speed
		 * and efficiency,since most IOT devices are based on
		 * linux and having low RAM and clock speeds are incapable
		 * of working with packages like processing
		 */
		System.loadLibrary("libcom");
	   }
	   setResolution(); // Calculate all the Frame parameters and update it 
	   settings(); // Use the parameters calculated in 'setResolution' and instantiate arrays
	   setSize(800,640); //Set the preferred size
	   defineRaster(); // Make 'pixels[]' the defining array(handle) for the pixels of 'source'
	   setVisible(true);
	}
	public enum BTN_SRC{ADD_SRC,DEL_SRC,CLEAR,PHASE,CONNECT,SEND,BAUD,CAR_freq,MOD_freq}
	public void settings() 
	{
		gxy = gx*gy; 
		buf1  = new float[gxy];
		buf2 = new float[gxy];
		damp = new float[gxy];
		surface = new int[gxy]; // not implemented yet
        	l=0; // set perturbed factor to 0
		int i, j;
		for (i = 0; i<gxy; i++)
		{
		    damp[i] = 1f; // Inintailly set the damp factor to 1
		}
		/* However since the damping exponentially increases as the wave propogates from the source
		   using the inital value of 1f ,we calculate how far each grid element is from the boundary element
		   and depending on that we update those elements of the damp array with lesser values
		   and since these damp array values will at a later point be multiplied with the perturbed values,
		   less than 1 damp value would cause a faster decay and hence simulate a real exponential decay of 
		   the wave*/
		for (i = 0; i<wox; i++)
		{
		    for (j = 0; j<gx; j++)
		    {
			    damp[i+j*gy] = damp[gx-1-i+gy*j] =damp[j+gy*i] = damp[j+(gy-1-i)*gy] =(float) (1-(wox-i) * .002);
		    }
		}
		/* Resolution is basically used for magnifying the canvas ,therefore if it is set too low
		   the rendering becomes too slow and hence don't permit it to drop below a certain value*/
		if(Resolution.getValue()<32)
		{
			Resolution.setValue(32);
			setResolution();
			settings();
		}
		clearWave(); // Clear the canvas
	}
	public void clearWave() 
	{
		for(int i=0;i<gxy;i++)
		{
			buf1[i]=buf2[i]=0;
			// Set perturbed values to 0 hence clearing the medium of all disturbances
		}
	}

	public void defineRaster()
	{
		 int w,h;
		 w=can.getWidth();
		 h=can.getHeight();
		 pixels = new int[w*h]; //Instantiate 'pixels' to be of size spanning the entire canvas
		 source = new MemoryImageSource(w,h, pixels, 0,w); // Make 'pixels' the handle to pixels of source
		 source.setAnimated(true);
		 source.setFullBufferUpdates(true);
		 im = can.createImage(source); //Make 'source' the defining element of Image 'im' 
	}

	public void mouseDragged(MouseEvent arg0) 
	{
		if(loc.size()!=0)
		{
			loc_edit(arg0);
			/* If mouse is dragged over an emitter than update the emitter element with the new 
	           	location and repaint the canvas*/
			can.repaint();
		}
	}

	public void mouseMoved(MouseEvent arg0) 
	{
		dsX= arg0.getX();
		dsY = arg0.getY();
		em_select(arg0);
	}

	public void adjustmentValueChanged(AdjustmentEvent arg0) 
	{
	  
	  if(arg0.getSource()==Resolution)
	  {
		  setResolution(); 
		  /* If resolution has been changed , then update the Frame parameters and reconfigure the
		     the array elements by calling settings*/
		  settings();
	  }
		
	}
	public void setResolution() 
	{
		ww = wh =Resolution.getValue();
	    	wox = woy =(ww/9)<20?20:(ww/9);
		gx = ww + wox*2;
		gy = wh + woy*2;
	}
    	public void setResolution(int x)
    	{
	    	Resolution.setValue(x);
	    	setResolution();
	    	settings();
    	}
	public void actionPerformed(ActionEvent arg0) 
	{
		BTN_SRC btn=BTN_SRC.valueOf(arg0.getActionCommand());
		switch(btn)
		{
			case CLEAR:
			clearWave(); // Clear the waves or the medium 
			check=false;
			/* 'check' -> Clear the focal point and hence clearing all the phase differences
			    between the transmitters*/
			set=true;
			light=true;
			canvas2.clear();
			if(connect)
			{
				check2=true;
				disconnect_serial();
			}
			can.repaint();// repaint the canvas
			break;
		
			case ADD_SRC:
			add=1;
			break;
		
			case DEL_SRC:
			add=2;
			break;
			
			case PHASE:
			add=3;
			break;
			/* 'add' -> we basically modify the add values ,since upon mouse click on the canvas
		    	 *  we need to identify which action preceeded it,whether it was 'AddSource' or 'DeleteSource' etc*/
			case BAUD:
			setRate();
			break;
			
			case CONNECT:
			connect_serial();
			break;
			
			case SEND:
			new Serial_Handler();
			break;
			
			case CAR_freq:
			Frequency F=new Carrier();
			F.select();
			break;
			
			case MOD_freq:
			Frequency F=new Modulated();
			F.select();
			break;
		
		}
	}
        private void setRate() 
	{
		baudrate=Integer.parseInt(JOptionPane.showInputDialog(this,"Enter Baud-Rate"));
	}
	public void phase_delay_cal(MouseEvent e) 
	{
		for(int i=0;i<loc.size();i++)
		{
			accx+=loc.get(i).x;
			accy+=loc.get(i).y;
		}
		accx=accx/loc.size();
		accy=accy/loc.size();
		/* accx,accy -> since all the transmitters are equally spaced , this gives the median or mid value
		   of the transmitter's x-coord,y-coord and therefore points to the mid transmitter*/
		int u=((((e.getX()*ww)-(can.getWidth()/2))/can.getWidth())+wox);
		int v=((((e.getY()*wh)-(can.getHeight()/2))/can.getHeight())+woy);
		/* Mouse click events basically give the absolute value of the click with respect to either the
		   container or the screen however, we previously defined a new co-ordinate system for the canvas
		   and since all the transmitter locations are based on this co-ordinate system , we need to convert 
		   the focal_point obtained through mouse click to the new co-ordinate system ,this is done in the 
		   above lines with u and v , (u,v) give the (x,y) co-ordinates of the focal point*/
		double R=Math.sqrt(Math.pow((accx-u),2)+Math.pow((accy-v),2));
		// 'R' -> calculates the phase_length from the mid transmitter to the focal point
		double sin_phi=Math.abs(accx-u)/R;
		/* 'sin_phi' -> calculates the sine of the angle by which 'R' is inclined to the plane
		    perpendicular to the plane of arrangement of the transmitters*/
		text.append("Phase delay for transmitters\n At Position"+u+" , "+v+"\n");
		if(u<accx)
		{
			tog=1;
		}
		else
		{
			tog=-1;
		}
		/* The 'tog' part as explained in the "WORKING OF THE ALGORITHM" is required to account for the
		   fact that the emitter as to be either advanced or set back depending on its location relative 
		   to mid transmitter and the focal point*/
		for(int i=0;i<loc.size();i++)
		{
			
			if(loc.get(i).x<accx)
			{
				phase_length.add(i,Math.sqrt(R*R+Math.pow((accx-loc.get(i).x),2)-2*R*Math.abs(accx-loc.get(i).x)*sin_phi*tog));
			}
			else
			{
				phase_length.add(i,Math.sqrt(R*R+Math.pow((accx-loc.get(i).x),2)+2*R*Math.abs(accx-loc.get(i).x)*sin_phi*tog));
			}
			phase_del.add(i,(R-phase_length.get(i))/330000);
		}
		order=new int[loc.size()];//instantiate order with number of transmitters
		for(int i=0;i<loc.size();i++)
		{
			order[i]=i;
		}
		
		for(int i=0;i<loc.size()-1;i++)
		{
			/* Bubble sort the contents of order depending 
			 * on the contents of phase_del in ascending order
			 * ,therefore order now holds the index of the 
			 * transmitters whose phase delays are in 
			 * ascending order*/
			for(int j=0;j<loc.size()-1-i;j++)
			{
				if(phase_del.get(j)>phase_del.get(j+1))
				{
					int t=order[j];
					order[j]=order[j+1];
					order[j+1]=t;
				}
			}
		}
		double sm=phase_del.get(order[0]);
		/* Here we encounter another problem , since there are tramsitters both to left and right of mid-transmitter
		   therefore any phase_delay causing an advance in excitation of the nth transmitter will be negative
		   since (R-Lx)<0, therefore we find the smallest negative value (sm) in the phase_delay array  and subtract 
		   it from each phase_delay array element , making the smallest one zero and everything else >0, this should
		   make sense intuitively since after all we are dealing with continuum concepts and hence the phase itself
		   has no significance ,its the phase delay that matters whcih remains unchanged by incrementing each
		   element through the same value*/
		for(int j=0;j<loc.size();j++)
		{
			double g=phase_del.get(j);
			phase_del.remove(j);
			phase_del.add(j,g-sm);// Subtarcting sm(negative smallest value) from each phase_delay element
			text.append("Position = "+loc.get(j).x+"  "+loc.get(j).y+" is ="+phase_del.get(j)+"\n");
			check=true;
			/* 'check' -> Guard that enables rendering the canvas accounting for the phase differences
			   if not set to true, the canvas will be rendered assuming no phase difference*/
		}
		/*for(int i=0;i<transmitters;i++)   //for transmitting data to arduino ,still buggy
		{
			String h="";
			h=h+phase_del.get(i)+","+loc.get(i).x+"\n";
			blink(h);
		}*/
	}
	public void time_millis(double del)
	{
		if(del<0)
		{
			return;
		}
		long t=System.currentTimeMillis();
		while((System.currentTimeMillis()-t)<=del)
		{
			//do nothing
		}
	}
	public void removeEmitters(int x,int y) 
	{
		for(int i=0;i<loc.size();i++)
		{
			if(Math.abs(loc.get(i).getx()-x)<4)
			{
				if(Math.abs(loc.get(i).gety()-y)<4)
				{
					clearWave();
					loc.remove(i);
					transmitters--;
				}
			}
			/* Check if the differences in the (x,y) co-ordinates of the mouse click value 
			   intending to delete an emitter and the (x,y) co-ordinates of the emitter is < 4
			   This implies that, that is the emitter being referred to hence delete it*/
		}
		
	}
	public void connect_serial()
	{
		if(!OS_Check)
		{
			try
			{
				System.load(path_to_jar+"/jSSC-2.8.dll");
				PApplet=papplet.newInstance();
				com=(String[])List.invoke(serial);
				connect=JOptionPane.showConfirmDialog(this,"Connecting to arduino on "+com[0])==JOptionPane.YES_OPTION?true:false;
				if(connect)
				{
					Serial=serial.getDeclaredConstructor(PApplet.getClass(),String.class,int.class).newInstance(PApplet,com[0],baudrate);
					time_millis(2000);
				}
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
		}
	}
	public static void disconnect_serial()
	{
		try
		{
			Method Close=serial.getMethod("stop");
			JOptionPane.showMessageDialog(can,"Closing Serial Communication on Port "+com[0]);
			connect=false;
			Close.invoke(Serial);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	public void paintComponent(Graphics g)
	{
		can.repaint();
	}
	public void addEmitters(int x,int y) 
	{
	    transmitters++;
	    Emitter_loc E=new Emitter_loc();
	    E.setx(x);
	    E.sety(y);
	    loc.add(E);
	    if(loc.size()==1)
	    {
	    	can.repaint();
	    }
	}

	public void mouseClicked(MouseEvent arg0) 
	{
		if(arg0.getSource()==can)
		{
			switch(add) // As we can see, we switch the earlier add value here 
			{
				case 1:
					int x=(((arg0.getX()*ww)-(can.getWidth()/2))/can.getWidth())+wox;
					int y=(((arg0.getY()*wh)-(can.getHeight()/2))/can.getHeight())+woy;
					/* The same process here as well, the click is converted to the co-ordinate
					   system corresponding to the canvas and then added*/
					addEmitters(x,y);// add the emitters at the newly calculated locations
					add=0;
					/* reset 'add' to prevent anyother accidental mouse clicks from being
					   processed as an intention to add emitters unless and until the actionListener
					   on 'AddSource' as been invoked*/
						
					break;
					
				case 2:
					removeEmitters(arg0.getX(),arg0.getY());
					add=0;// Same 
					break;
				
				case 3:
					phase_delay_cal(arg0); // Calls phase_delay_cal with the mouseEvent
					add=0;//Same
					break;
			}
		}
		
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

    	public void mousePressed(MouseEvent arg0) 
	{
		
    	}

	public void mouseReleased(MouseEvent arg0) 
    	{
	
	}
	public void addLayoutComponent(String arg0, Component arg1) 
	{
		
		
	}
	public void layoutContainer(Container arg0) //Sets the desired Layout
	{
		int w = arg0.getSize().width; // get the width of the container
		int cw = w* 7/10; //set the limit for partition between the canvas and the other compoenents(JScrollbar etc)
		int h = arg0.getSize().height;// get the height of the container
	    	arg0.getComponent(0).setSize(cw, h);// Refers to the canvas which is set to width 'cw' and height 'h'
		int barwidth = w - cw;//Width of the remaining part of container excluding the canvas
		h=0;
		int c=0;
		for (int i = 1; i < arg0.getComponentCount(); i++) 
		{
		    	Component m = arg0.getComponent(i);
		  	Dimension d = m.getPreferredSize();
		  	if(arg0.getComponent(i) instanceof JButton)
			{
				if(c%2==0)
				{
					d.width=barwidth/2;
					m.setSize(d);
					m.setLocation(cw, h);
				
				}
				else
				{
					d.width=barwidth/2;
					m.setSize(d);
					m.setLocation(cw+d.width,h);
					h+=d.height;
				}
				c++;
			}
			if (arg0.getComponent(i) instanceof JScrollBar)
			{
			    	d.width = barwidth;
			    	m.setLocation(cw,h+50);
				m.setSize(d);
			    	h += d.height;
			}
			if (arg0.getComponent(i) instanceof JLabel)
			{
			    	h += d.height/5;
			    	d.width = barwidth;
			    	h += d.height;
			    	m.setLocation(cw,h+50);
				m.setSize(d);
				h += d.height;
			}
			
			//Basically set the width,height,location fro different components excluding the canvas
		 }
	}
	public Dimension minimumLayoutSize(Container arg0) 
	{
		return new Dimension(100,100);
	}
	public Dimension preferredLayoutSize(Container arg0) 
	{
		return new Dimension(500, 500);
	}
	public void removeLayoutComponent(Component arg0) 
	{
		// TODO Auto-generated method stub
		
	}
	public void field(Graphics g1) 
	{
		for (int i = 0;i<1 && !stop.getState(); i++)
		{
			/* The above for loop is presently useless however should we ever feel the need to
			 * speed up the simulation it can be updated through a JScrollbar, stop the simulation
			 * if the check box is selected */
			int js, je, ji;
			if (md) 
			{
			    js = 1; je = gy-1; ji = 1; md = false;
			}
			else 
			{
			    js = gy-2; je = 0; ji = -1; md = true;
			}
			mr = md;
			/* As seen above each alternative rendering cycle sets and resets md,mr, this switches the 
			 * index values(js,je,ji) from starting index to end index meaning, the canvas gets rendered
			 * alternatively in up down to maintain uniformity*/
			for (int j = js; j != je; j += ji) 
			{
			    int is, ie, ii;
			    if (mr) 
			    {
					ii = 1; is = 1; ie =gx-1; mr = false;
			    } 
			    else 
			    {
					ii = -1; is = gx-2; ie = 0; mr = true;
			    }
			    /* The same procedure is carried out for left right rendering to maintain uniformity and
			     * avoid any directional bias in rendering*/
			    int gi = j*gy+is;//gives the absolute pixel location similar to (x-coor+y-coor*width)
			    int gie = j*gy+ie;
			    for (; gi != gie; gi += ii) 
			    {
			    	float b =(buf1[gi-1]+buf1[gi+1]+buf1[gi-gy]+buf1[gi+gy])*0.25f;
			    	/* With each passing index 'gi' this disturbance is transferred to the next group 
			    	 * of pixels, therefore eventually spreading the dirturbance across the entire
			    	 * medium*/
			    	buf1[gi]*=damp[gi];
			    	buf2[gi]*=damp[gi];
			    	/* Since 'buf1' and 'buf2' basically hold the perturbed values or in other words the
			    	 * velocity information of the propogating waves, we multiply it with damping factor
			    	 * to simulate exponential decay*/
			    	buf1[gi]-=b;
			    	// We update 'buf1' with the distrubance propogating through 'b' 
			    	float x=(float)(Math.sin(0.25)*buf2[gi]+Math.cos(0.25)*buf1[gi]+b);
			    	float y=(float)(Math.cos(0.25)*buf2[gi]-Math.sin(0.25)*buf1[gi]);
			    	buf1[gi]=x;
			    	buf2[gi]=y;
			    }
			}
			l += 0.25;
			if (loc.size()> 0) 
			{
			    double w = frequency.getValue()*l*0.0233;
			    double v = Math.cos(w);
			    for (int j = 0; j<loc.size(); j++)
			    {
			    	// If check is not set to true then render without phase difference 
			    	if(!check)
			    	{
			    		loc.get(j).v = (float) (v);
			    	}
			    	else
			    	{
			    		loc.get(j).v=(float)Math.cos(2*Math.PI*frequency.getValue()*phase_del.get(j)*1000+w);
			    	}
			    }
			    for (i = 0; i<loc.size(); i++) 
			    {
				    buf1[loc.get(i).x+gy*loc.get(i).y] =loc.get(loc.size()-i-1).v;
				    buf2[loc.get(i).x+gy*loc.get(i).y] = 0;
		            }
			}
		}
		

		if (view_phase_plane.getState()&&(!viewreal.getState()))
		{
		    set=true;
		    surfacev();//for future
		}
		else if(viewreal.getState()&&(!view_phase_plane.getState()))
		{
			//real();
		}
		else
		{
		    set=true;
		    planarv();
		}

		if (source != null)
		    source.newPixels(); // Use the 'pixels' array to update the pixels of source

		g1.drawImage(im, 0, 0, this);
		int x=((((dsX*ww)-(can.getWidth()/2))/can.getWidth())+wox); 
		int y=((((dsY*ww)-(can.getHeight()/2))/can.getHeight())+woy);
		// obtain the x,y co-ordinates of the mouse location on canvas 
		String s = "(" + x + "," + y +")";
		String f=Static_Pressure_Compute(x,y);
		String s1="Force : "+f.substring(0,f.indexOf(","));
		String s2="Pressure : "+f.substring(f.indexOf(",")+1,f.length());
		g1.setColor(Color.white);
		g1.setFont(new Font("TimesRoman",Font.PLAIN,12));
		g1.drawString(s,gy/18,gy/10);
		g1.drawString(s1,gy/18,(gy/10)+10);
		g1.drawString(s2,gy/18,(gy/10)+20);
		
	}
	public String Static_Pressure_Compute(int x, int y) 
	{
		double wave_length=330000/(frequency.getValue()*1000);
		double midy;
		double SPL=0.02;
		midy=0;
		for(int i=0;i<loc.size();i++)
		{
			midy+=loc.get(i).y;
		}
		midy=midy/loc.size();
		double z=y-midy;
		double W=(Math.pow(wave_length, 2)*Math.pow(z, 2)*Math.pow(SPL, 2)*(10^6))/(2*Math.PI*1.2*330000*Math.pow(em_size.getValue(),2));
		double F=(2*loc.size()*W)/(330000);
		return F+","+W;
		
	}
	private void planarv() 
	{
		int ix = 0;
		int i, j, k, l;
		for (j = 0; j != wh; j++) 
		{
		    int y = j*can.getHeight()/wh;
		    ix = can.getWidth()*(y);
		    int j2 = j+woy;
		    int gi = j2*gy+wox;
		    int y2 = (j+1)*can.getHeight()/wh;
		    for (i = 0; i != ww; i++, gi++) 
		    {
			int x = i*can.getWidth()/ww;
			int x2 = (i+1)*can.getWidth()/ww;
			int i2 = i+wox;
			double dy = buf1[gi]*5;
			if (dy < -1)
			     dy = -1;
		        if (dy > 1)
			     dy = 1;
		        int col = 0;
		        int R = 0, G = 0, B = 0;
		        double d1 = dy;
		        double d3 =(1/255.01);
			double a1 = d1*(1-d3); 
			double a2 = (1-d1)*(1-d3);
			double a3 = d1*d3; 
			double a4 = (1-d1)*d3;
			R = (int) (med2[1]. getRed()*a1 +med2[3].getRed()*a2 + med2[4].getRed()*a3 +med2[6].getRed()*a4);
			G = (int) (med2[1]. getGreen()*a1 +med2[3].getGreen()*a2 +med2[4].getGreen()*a3 +med2[6].getGreen()*a4);
			B = (int) (med2[1]. getBlue()*a1 +med2[3].getBlue()*a2+med2[4].getBlue()*a3 +med2[6].getBlue()*a4);
			col = (255<<24) | (R<<16) | (G<<8) | (B);
			for (k = 0; k != x2-x; k++, ix++)
			{
			   for (l = 0; l != y2-y; l++)
			    {
			    	pixels[ix+l*can.getWidth()] = col;
			    }
			}
		     }
		}
		for (i = 0; i<loc.size(); i++) 
		{
		    int xx = loc.get(i).getx();
		    int yy = loc.get(i).gety();
		    emitter(i, xx, yy);
		}
		
	}

	public void emitter(int i, int xx, int yy) 
	{
		int j;
		int col = (med2[7].getRed()<<16)|(med2[7].getGreen()<<8)|(med2[7].getBlue())| 0xFF000000;
		if (i == selemitter)
		{
		  col ^= 0xFFFFFF;
		}
		for (j = 0; j <= em_size.getValue(); j++) 
		{
		    int k = (int) (Math.sqrt(em_size.getValue()-j*j)+.5);
		    /* Since the frame is refreshed pixel by pixel that is by updating
		     * the pixel handle to the canvas ,therefore the transmitters need 
		     * to be drawn pixel by pixel rather tahn using built in methods like
		     * fillOval etc*/
		    plotPixel(xx+j, yy+k, col);
		    plotPixel(xx+k, yy+j, col);
		    plotPixel(xx+j, yy-k, col);
		    plotPixel(xx-k, yy+j, col);
		    plotPixel(xx-j, yy+k, col);
		    plotPixel(xx+k, yy-j, col);
		    plotPixel(xx-j, yy-k, col);
		    plotPixel(xx-k, yy-j, col);
		    plotPixel(xx, yy+j, col);
		    plotPixel(xx, yy-j, col);
		    plotPixel(xx+j, yy, col);
		    plotPixel(xx-j, yy, col);
		}
		
	}

	private void plotPixel(int i, int j, int col) 
	{
		if (i < 0 || i>= can.getWidth() || j<0 || j>can.getHeight())
		    return;
		try 
		{ 
			pixels[i+j*can.getWidth()] =col; 
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	
		
	}

	private void surfacev() 
	{
		//For future 
		
	}
	 void loc_edit(MouseEvent e)
	 {
			int x = e.getX();
			int y = e.getY();
			if (selemitter != -1) 
			{
			    x = x*ww/can.getWidth();
			    y = y*wh/can.getHeight();
			    if (x >= 0 && y >= 0 && x < ww && y <wh) 
			    {
			    	loc.get(selemitter).x=x+wox;
			    	loc.get(selemitter).y=y+woy;
			    }
			    // Update the position of the emitter if changed
			    return;
			}
	}
	class Emitter_loc implements java.io.Serializable
	{
		//Class, basically containing info about position of the emitters
		int x,y;
		float v;
		/* We use java Beans structure to construct this class ,since if at a later
		 * point we require to use this data in servlets or send them through a 
		 * network, it makes things easier and cleaner*/
		void setx(int xx)
		{
			x=xx;
			/* set x position of the transmitter relative to canvas and not container(Frame)*/
		}
		void sety(int yy)
		{
			y=yy;
			/* set y position of the transmitter relative to canvas and not container(Frame)*/
		}
		int getx()
		{
			return ((x-wox) * can.getWidth()+can.getWidth()/2)/ww;
		}
		int gety()
		{
			return ((y-woy) * can.getHeight()+can.getHeight()/2)/wh;
		}
		// returns the absolute x and y positions of the emitters relative to the container(Frame)
	}
	class Serial_Handler implements Runnable
	{
		/* Bifuricate the painting of canvas and porting data to prevent hanging of the program.
		 * This class handles the job of sorting the order in which the transmitters should be
		 * excited and also takes care of sending the data to arduino */
		Thread t;
		/* t->  Make the process of sending data run along its own thread*/
		double time=0.0;
		/* time-> required for holding phase difference values*/
		Node point;
		/* point-> Required for initiating the start of the list while sending data*/
	        Serial_Handler()
		{
			new list();
			/* create the singularly linked circular list which will contain the order
			 * in which the transmitters should be excited*/
			t=new Thread(this);
			t.start();
			/* Instantiate the thread and start it*/
		}
		class Node
		{
			/* This class defines the fundamental parameters possessed by each transmitter
			 * and helps in setting appropriate values to these parameters */
			double delay;
			Node emitter;
			int count;// index count 
			Node()
			{
				delay=0.0;
				emitter=null;
			}
			Node(double del,Node e)
			{
				delay=del;
				emitter=e;
			}
			void setDelay(double del)
			{
				delay=del;
			}
			void setEmitter(Node e)
			{
				emitter=e;
			}
			void setCount(int c)
			{
				count =c;
			}
			Node getEmitter()
			{
				return emitter;
			}
			double getDelay()
			{
				return delay;
			}
			int getCount()
			{
				return count;
			}
		}
		class list
		{
			/* This class handles the process of populating the list int he order
			 * defined by the array 'order' which has been sorted bearing the indices 
			 * of each transmitter in ascending order of Phase_delays*/
			Node iterator=null;
			/* Required to iterate through the list*/
			Node begin=null;
			list()
			{
				for(int i=0;i<loc.size();i++)
				{
					if(i<loc.size()-1)
					{
						/* The if condition makes sure that every node or 
						 * transmitter holds the address to the next until
						 * it encounters the last one */
						Node n=new Node(phase_del.get(order[i+1]),null);
						n.setCount(i);
						if(iterator==null)
						{
							iterator=n;
							begin=n;
							point =n;
							/* Make point hold the address of the beginning
							 * transmitter*/
							
						}
						else
						{
							iterator.setEmitter(n);
							iterator=n;
						}
						System.out.println(i+" h "+n.getDelay());
					}
					else
					{
						/* The last transmitter or node is made to hold the address
						 * of the first one thereby making the list circular*/
						Node n=new Node(phase_del.get(order[0]),null);
						n.setCount(i);
						n.setEmitter(begin);
						System.out.println(i+"  g  "+n.getDelay());
					}
					
				}
			}
			void display()
			{
				Node counter=begin;
				for(int i=0;i<loc.size();i++)
				{
					System.out.println(i+"   "+counter.getDelay());
					counter=counter.getEmitter();
				}
			
				
			}	
		}
		public void run()
		{
			while(!check2)
			{
				/* check2 makes sure to stop any serial communication thereby suspending the 
				 * thread before the serial port is closed */
				time=point.getDelay()-time;
				/* The phase differences calculated are relative to the transmitter with lowest
				 * delay value i.e 0.0, Therefore since the transmitters are being excited in
				 * sequential manner ,to prevent any added delay, we subtract them from the delay
				 * value he;d by previous node to offset the relation*/
				System.out.println(point.getDelay()+"   "+point.getEmitter()+"  "+time+"  "+point.getCount());
				if(t.isInterrupted())
				{
					break;
				}
				try
				{
					if(!OS_Check)
					{
						Write_Serial(point.getCount()+"");
					}
					else
					{
						write(point.getCount()+"");
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				time_millis(time);
				/* generate the delay , could have used sleep but would have sacrificed accuracy
				 * since it only accepts long*/
				time=point.getDelay(); // get the old delay required for subtraction
				point=point.getEmitter(); //get the new transmitter
			}
		}
	}
	public static synchronized void Write_Serial(String h)
	{
		if(!OS_Check)
		{
			try
			{
				Write.invoke(Serial,h);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void componentResized(ComponentEvent arg0) 
	{
		defineRaster();
		can.repaint(100);
	}
	public void componentShown(ComponentEvent arg0)
	{
		can.repaint();
	}

    	public void em_select(MouseEvent e) 
    	{
	    	int x = e.getX();
	    	int y = e.getY();
	    	int i;
	    	for (i = 0; i<loc.size(); i++)
	    	{
    			int x2=loc.get(i).getx();
    			int y2=loc.get(i).gety();
    			if (Math.pow(em_size.getValue(), 2)>( Math.pow((x2-x),2)*Math.pow(y2-y,2))) 
    			{
	    			selemitter= i;
    				return;
    				/* basically checks if the mouse which has been moved is hovering in the vicinity 
    				   of the ith transmitter, if yes then update 'selemitter' with i and return*/
    			}
	    	}
        	selemitter = -1;
    	}
    	public double Field_Length()
    	{
    		/* Calculates the length of the near field and returns it*/
    		double N=(Math.pow(em_size.getValue(),2)*frequency.getValue())/(4*330);
    		return N;
    	}
    	public double Beam_Angle()
    	{
    		/* Calculates the angle of inlcination of the beam relative to the principle normal*/
    		double a=Math.asin(0.514*330)/(frequency.getValue()*em_size.getValue());
    		return a;
    	}
    	public void field1(Graphics g)
    	{
    		g.setFont(new Font("Monotype Corsiva",Font.BOLD,64));
    		g.setColor(new Color(0));
    		g.fillRect(0,0,can.getWidth(),can.getHeight());
    		g.setColor(new Color(255,255,255));
    		g.drawString("1sand0s",gy,gy);
    		g.drawString("Coding sets you free",gy-150,gy+100);
    	}
    	static class Integral_Rayleigh_Sommerfeld
    	{
    		static double k=(2*Math.PI*frequency.getValue()*1000)/(330000);
        	static double alpha=0.00656;
        	static double rho=1.22*Math.pow(10,-6);
        	static double r;
        	static Complex p;
        	/* 'k'-> wave number
        	 * 'alpha'-> attenuation coefficient
        	 * 'rho'->density of medium of propogation
        	 * 'r'->point in space
        	 * 'p'->complex to hold the integrated value*/
    		public static Complex func(double x,int div,int mul)
    		{
    			/* Method to insert the step values into the function 
    			 * and hence compute the respective integral values 
    			 * Basically since the width is divided into smaller steps
    			 * x0,x1,x2..xn ,this method computes the corresponding 
    			 * y0,y1,y2..yn and returns it as a complex object*/
    			/* 'x'->Step width
    			 * 'div'->Divisor for the integral 'y' depending on the term 
    			 * 'mul'->Multiplier for the integral 'y' depending on the term*/
	    		double dist=Math.abs(x-r);
    			double cos=Math.cos(Math.toRadians(k*dist));
    			double sin=Math.sin(Math.toRadians(k*dist));
    			double deno=dist*Math.exp(alpha*dist);
    			Complex comp=new Complex(cos,sin);
    			if(!(deno==0||div==0))
    			{
    				comp.div(deno*div);
    				comp.mul(mul*dist*x);
    			}
	    		return comp;
	    		/* 'dist'-> distance from the surface of transducer to the point in space
	    		 * 'cos'-> real part of complex number
	    		 * 'sin'->imaginary part of complex number
	    		 * 'deno'-> holds the denominator
	    		 *
	    		 *  function= (exp((jk-alpha)*mod(x-r))*x)/(mod(x-r)
	    		 * For the sake of simplicity , we use euler's theorem to convert this
	    		 * complex exponential into cosines and sines*/
	    	}
    	
    	}
    	static class Complex
	 {
	 	/* Class used for creating and operating and executing complex operations
	 	 * Allows easy manipulations of complex numbers and their maintainance */
	    	double real;
	    	double imag;
	    	/* 'real'-> Real part of complex object
	    	 * 'imag'-> Imaginary part of complex object */
	    	Complex(double r,double i)
	    	{
    			real=r;
	    		imag=i;
	    		/* Constructor to define the real and imaginary part of the complex number*/
    		}	
    		void div(double i)
    		{
	    		real/=i;
    			imag/=i;
    			/* Method to divide the complex number by certain real number*/
    		}
    		void mul(double m)
    		{
	    		real*=m;
    			imag*=m;
    			/* Method to multiply the complex number by real number*/
    		}
    		void mul(String h)
    		{
	    		double t=real;
    			real=-imag;
    			imag=t;
    			/* Method to multiply the complex number by complex value 'i'*/
	    	}
	    	double abs()
    		{	
	    		return Math.sqrt(Math.pow(real,2)+Math.pow(imag,2));
	    		/* Method to calculate and return the magnitude of the complex number*/
	    	}
    	}
    	static class Numerical_Integration
    	{
    		/* This class performs the task of computing the Rayleigh Sommerfeld Integral through
    		 * numerical integration.It acheives this through either the trapezoidal,simpson's 1/3
    		 * and simpson's 3/8 depending on the number of terms,accuracy,efficiency and performance
    		 * required. For certain features of the integrand certain methods converge faster thereby
    		 * making them the preferred choice*/
    		 
    		/* 'n'-> Step Count
    		 * 'a'-> Lower Limit of Integration
    		 * 'b'-> Upper Limit of Integration */
    		static Complex res[];
    		static double r,im;
    		/* 'res'->The complex object which is used to store the complex numbers pertaining to each
    		 *        iteration
    		 * 'r'-> Accumulator to sum up the real part of 'res'
    		 * 'im'->Accumulator to sum up the imaginary part of 'res'*/
    		public static Complex Trapezoidal(int n,double a,double b)
    		{
    			/* This method of numerical integration uses trapezoids to approximate the function
    			 * curve. It is most preferred when the step count is small.*/
    			double del_x=(b-a)/n;
    			res=new Complex[n+1];
    			r=0.0;
    			im=0.0;
    			
    			/* 'del_x'-> The width of the trapezoid
    			 * 'res'-> The accumulator to store the result after each iteration*/
    			for(int i=0;i<=n;i++)
    			{
	    			double x=a+i*del_x;
    				res[i]=i==0||i==n?Integral_Rayleigh_Sommerfeld.func(x,2,1):Integral_Rayleigh_Sommerfeld.func(x,1,1);
	    			r+=res[i].real;
    				im+=res[i].imag;
    				/* I have execute the condition using ternary operator for compactness and efficiency
    				 * but the logic is as follows
    				 *  
    				 *  if(i==0||i==n)
    				 *  {
    				 *  	res[i]=Integral_Rayleigh_Sommerfeld.func(x,2,1);
    				 *  }
    				 *  else
    				 *  {
    				 *  	res[i]=Integral_Rayleigh_Sommerfeld.func(x,1,1);
    				 *  }
    				 * Basically if the term corressponding to the iterator is either the first or last
    				 * term then divide it by two and add or else add it as it is*/
	    		}
    			r*=del_x;
    			im*=del_x;
    			/* Multiply the result with del_x and return*/ 
    			return new Complex(r,im);
    			
    			/* Code Logic: 
    			 * The area of trapezoid is ((sum of parallel sides)*distance between the parallel sides)/2
    			 * The result returned by the 'func' corressponds to the parallel sides of the trapezoid
    			 * The del_x corresponds to the width */
    		}	
    		public static Complex Simpsons_one_third(int n,double a,double b)
    		{
    			/* This method employs quadratic curves(parabolas) to approximate the function curve. It is 
    			 * the most preferred method when the number of terms is even */
	    		double del_x=(b-a)/n;
    			res=new Complex[n+1];
	    		r=0.0;
    			im=0.0;
    			for(int i=0;i<=n;i++)
    			{
	    			double x=a+i*del_x;
    				res[i]=i==0||i==n?(Integral_Rayleigh_Sommerfeld.func(x,1,1)):(i%2==0?(Integral_Rayleigh_Sommerfeld.func(x,1,2)):(Integral_Rayleigh_Sommerfeld.func(x,1,4)));
	    			r+=res[i].real;
    				im+=res[i].imag;
    				/* I have execute the condition using ternary operator for compactness and efficiency
    				 * but the logic is as follows
    				 *  
    				 *  if(i==0||i==n)
    				 *  {
    				 *  	res[i]=Integral_Rayleigh_Sommerfeld.func(x,1,1);
    				 *  }
    				 *  else if(i%2==0)
    				 *  {
    				 *  	res[i]=Integral_Rayleigh_Sommerfeld.func(x,1,2);
    				 *  }
    				 *  else
    				 *  {
    				 *	res[i]=Integral_Rayleigh_Sommerfeld.func(x,1,4);
    				 *  }
    				 * Basically if the term corressponding to the iterator is either the first or last
    				 * term then add as it is, if they are even terms excluding the first and last then 
    				 * multiply by '2' and add or else if they are odd terms excluding the first and last
    				 * multiply by '4' and add */
    			}
    			r*=(del_x/3);
    			im*=(del_x/3);
    			return new Complex(r,im);
    			/* Multiply result by del_x and divide by '3' and return*/
	    	
	    		
	    		/* Code Logic
	    		 * The expression for any parabola is 'y=ax^2+bx+c' and given three points each to determine
	    		 * 'a','b'and 'c' constants we can find the parabola that passes through those points,therefore
	    		 * the code works by finding the regions of many such parabolas computed which best fit the given 
	    		 * curve*/
	    	}
	    	public static Complex Simpsons_Three_Eighth(int n,double a,double b)
    		{
    			/* This method employs cubic polynomials to approximate the function curve. It is the 
    			 * most preferred method when the number of terms or step count is multiple of 3*/
	    		double del_x=(b-a)/n;
    			res=new Complex[n+1];
    			r=0.0;
    			im=0.0;
    			for(int i=0;i<=n;i++)
    			{
	    			double x=a+i*del_x;
    				res[i]=i==0||i==n?(Integral_Rayleigh_Sommerfeld.func(x,1,1)):(i%3==0?(Integral_Rayleigh_Sommerfeld.func(x,1,2)):(Integral_Rayleigh_Sommerfeld.func(x,1,2)));
    				r+=res[i].real;
    				im+=res[i].imag;
    				/* I have execute the condition using ternary operator for compactness and efficiency
    				 * but the logic is as follows
    				 *  
    				 *  if(i==0||i==n)
    				 *  {
    				 *  	res[i]=Integral_Rayleigh_Sommerfeld.func(x,1,1);
    				 *  }
    				 *  else if(i%3==0)
    				 *  {
    				 *  	res[i]=Integral_Rayleigh_Sommerfeld.func(x,1,2);
    				 *  }
    				 *  else
    				 *  {
    				 *	res[i]=Integral_Rayleigh_Sommerfeld.func(x,1,3);
    				 *  }
    				 * Basically if the term corressponding to the iterator is either the first or last
    				 * term then add as it is, if they are multiples of 3 excluding the first and last then 
    				 * multiply by '2' and add or else if they are non-multiples of 3 excluding the first and last
    				 * multiply by '3' and add */
    			}
    			r*=((del_x*3)/8);
    			im*=((del_x*3)/8);
    			return new Complex(r,im);
	    		/* Fairly the same logic as Simpson's 1/3 rule but instead employs cubic polynomials*/
    			
    		}
    	}
    	public void itemStateChanged(ItemEvent arg0) 
	{
		if(arg0.getSource()==stop)
		{
			if(!stop.getState())
			{
				canvas2.stopped();
			}
		}
		
	}
	class Spatial_Impulse_Response
	{
		static double x;
		static double y;
		static double t0;
		static double t1;
		static double t2;
		static double rho;
		
		
	}
}
class Ultra_canvas extends Canvas
{
	Ultra_virtual m2;
	Ultra_canvas(Ultra_virtual m)
	{
		m2=m;
	}
	public void paint(Graphics g)
	{
		
		if(Ultra_virtual.loc.size()==0)
		{
			m2.field1(g);
		}
		else
		{
			m2.field(g);
		}
	}
	public void update(Graphics g)
	{
		if(Ultra_virtual.loc.size()==0)
		{
			m2.field1(g);
		}
		else
		{
			m2.field(g);
		}
		
	}
}
class Ultra_real extends JInternalFrame
{
	Ultra_real(Ultra_virtual v)
	{
		add(new canvas2(v));
		setSize(800,640);
		setResizable(false);
		setVisible(true);
	}
	
}
class canvas2 extends JPanel implements ActionListener,Runnable
{
	/* This class as of now is of no significance but will add functionality later
	   This was added to shed more light on the working of the algorithm, as here we 
	   are dealing with the pixels concerning an actual image hence demonstrating
	   how the algorithm affects it*/
	static int wi=800,hi=640,hw=wi/2,hh=hi/2,fir=wi,sec=wi*(hi+3);
	static int [] pixel,pixelupdate,buf;
	static ArrayList<String>up=new ArrayList<String>();
	static PixelGrabber p;
	static Ultra_virtual v2;
	static Thread engine;
	static MemoryImageSource s;
	static Image im,im2;
	static JLabel l;
	static canvas2 c;
	static Object lock;
	static boolean check3=true;
	public Dimension getMinimumSize()
	{
        	return new Dimension(100, 100);
    	}

	canvas2(Ultra_virtual v)
    	{
	    	v2=v;
	    	c=this;
	    	configure();
    	}	
    	public void configure()
    	{
		im=new ImageIcon("C:/Users/Public/Pictures/Sample Pictures/Koala.jpg").getImage();
		l=new JLabel();
		l.setIcon(new ImageIcon(im));
		add(l);
		pixel=new int[wi*hi];
		pixelupdate=new int[wi*hi];
		buf=new int[wi*(hi+2)*2];
		p=new PixelGrabber(im,0,0,wi,hi,pixel,0,wi);
		try
		{
			p.grabPixels(); //Grab the pixels from the image for modification
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();	
		}
		s=new MemoryImageSource(wi,hi,pixelupdate,0,wi);
		/* Make the array pixelupdate the handle to the pixels of 's'*/
		s.setAnimated(true);
		s.setFullBufferUpdates(true);
		im2=createImage(s);
		engine=new Thread(this);
		check3=true;
		lock=new Object();
		engine.start();
		Timer t=new Timer(100,this);
		/* For triggering ActionListener to cause disturbance */ 
		t.start();
    	}
    	public Dimension getPreferredSize()
    	{
	        return new Dimension(800, 640);
    	}

  
    	public Dimension getMaximumSize() 
    	{
	        return new Dimension(800, 640);
    	}
	public void run() 
	{
		synchronized(lock)
		{
			while(Thread.currentThread()==engine && check3)
			{
				try
				{
					
					if(v2.stop.getState())
					{
						lock.wait();
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				updategrid();
				s.newPixels();
				repaint();
			}
		}
		
	}
	public void paint(Graphics g)
	{
		g.drawImage(im2,0,0,this);
	}
	public void update(Graphics g)
	{
		g.drawImage(im2,0,0,this);
	}
	private void updategrid()
	{
		fir=fir+sec;
		sec=fir-sec;
		fir=fir-sec;
		int ind=fir;
		int c=0;
		/* Swap the indices pointing to the two halves of the array rather than 
		 * swap the arrays themselves*/
		for (int y=1;y<hi;y++) 
	        {
	       		for (int x=1;x<wi;x++) 
	       		{
		         	short d = (short)(((buf[ind-wi]+buf[ind+wi]+buf[ind-1]+buf[ind+1])/2)-buf[sec+c]);
		         	d -= d/1024;
	         		buf[sec+c]=d;
		         	d= (short)(1024-d);
	         		int a=((x-hw)*d/1024)+hw;
	         		int b=((y-hh)*d/1024)+hh;
	         		if (a>=wi) a=wi-1;
	         		if (a<0) a=0;
	         		if (b>=hi) b=hi-1;
	         		if (b<0) b=0;
	         		pixelupdate[c]=pixel[a+(b*wi)];	         
		         	ind++;
	         		c++;		
	       		}
	     	}
		
	}
	public void actionPerformed(ActionEvent arg0) 
	{
		if(!v2.loc.isEmpty())
		{
			for(int i=0;i<v2.loc.size();i++)
			{
				emit(v2.loc.get(i).getx(),v2.loc.get(i).gety());
			}
		}
		
	}
	private void emit(int x, int y) 
	{
		for (int i=y-v2.em_size.getValue();i<y+v2.em_size.getValue();i++) 
		{
		   for (int j=x-v2.em_size.getValue();j<x+v2.em_size.getValue();j++) 
		   {
		       if (i>=0 && i<hi && j>=0 && j<wi) 
		       {
		    	        buf[fir+i*wi+j]+=1024;       
		       } 
		   }
	    }
	}
	public static void clear()
	{
		check3=false;
		try
		{
			engine.join();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		c.configure();
	}
	public static void stopped()
	{
		synchronized(lock)
		{
			lock.notify();
		}
	}
}
abstract class Frequency extends JFrame implements ActionListener
{
	abstract void set_value(int n);
	abstract int get_value();
	abstract String[] get_list();
	abstract void select();
	String[] list;
	int Freq;
	JList l;
	JScrollPane p;
	JButton Add;
}
class Carrier extends Frequency
{

	static final int CAR_F1=20;
	static final int CAR_F2=40;
	static final int CAR_F3=75;
	static final int CAR_F4=1000;
	static final int CAR_F5=2000;
	static final int CAR_F6=4000;
	static final int CAR_F7=6000;
	Carrier()
	{
		setSize(200,200);
		list=new String[7];
		list[0]="CAR_F1 : 20KHZ";
		list[1]="CAR_F2 : 40KHZ";
		list[2]="CAR_F3 : 75KHZ";
		list[3]="CAR_F4 : 1MHZ";
		list[4]="CAR_F5 : 2MHZ";
		list[5]="CAR_F6 : 4MHZ";
		list[6]="CAR_F7 : 6MHZ";
		setLayout(new FlowLayout());
		setVisible(true);
	}
	void set_value(int n) 
	{
		Freq=n;
	}
	int get_value()
	{
		return Freq;
	}
	String[] get_list()
	{
		return list;
	}
	void select()
	{
		l=new JList(get_list());
		l.setLayoutOrientation(JList.VERTICAL);
		p=new JScrollPane(l);
		Add=new JButton("ADD");
		Add.addActionListener(this);
		add(p);
		add(Add);
		
	}
	public void actionPerformed(ActionEvent arg0) 
	{
		if(arg0.getSource()==Add)
		{
			Object c=l.getSelectedValue();
			System.out.println(c);
			setVisible(false);
			Ultra_virtual.Write_Serial(c.toString());
		}
		
	}
	
}
class Modulated extends Frequency
{

	static final int MOD_F1=50;
	static final int MOD_F2=70;
	static final int MOD_F3=90;
	static final int MOD_F4=100;
	static final int MOD_F5=120;
	static final int MOD_F6=140;
	static final int MOD_F7=160;
	static final int MOD_F8=180;
	static final int MOD_F9=200;
	Modulated()
	{
		setSize(200,200);
		list=new String[9];
		list[0]="MOD_F1 : 50HZ";
		list[1]="MOD_F2 : 70HZ";
		list[2]="MOD_F3 : 90HZ";
		list[3]="MOD_F4 : 100HZ";
		list[4]="MOD_F5 : 120HZ";
		list[5]="MOD_F6 : 140HZ";
		list[6]="MOD_F7 : 160HZ";
		list[7]="MOD_F8 : 180HZ";
		list[8]="MOD_F9 : 200HZ";
		setLayout(new FlowLayout());
		setVisible(true);
	}
	void set_value(int n) 
	{
		Freq=n;
	}

	int get_value() 
	{
		
		return Freq;
	}
	String[] get_list()
	{
		return list;
	}
	void select()
	{
		l=new JList(get_list());
		l.setLayoutOrientation(JList.VERTICAL);
		p=new JScrollPane(l);
		Add=new JButton("ADD");
		Add.addActionListener(this);
		add(p);
		add(Add);
		
	}
	public void actionPerformed(ActionEvent arg0) 
	{
		if(arg0.getSource()==Add)
		{
			Object c=l.getSelectedValue();
			System.out.println(c);
			setVisible(false);
			Ultra_virtual.Write_Serial(c.toString());
		}
		
	}
}
