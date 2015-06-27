package IS_UltraSonic;

import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.*;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.*;

public class Ultra_main extends JFrame
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
           setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
class Ultra_virtual extends JInternalFrame implements MouseMotionListener,MouseListener,ActionListener,AdjustmentListener,ComponentListener,LayoutManager
{
	static JScrollBar frequency,Resolution,em_size; // For controlling Frequency,Resolution and emitter size
	static Ultra_canvas can; // For drawing the ultrasonic waves
	static int transmitters=0; // For Kepping count of number of transmitters
	static int dsX = -1, dsY; // For keeping track of x and y co-ordinates of Mouse click on the canvas
	static int selemitter; // For checking if any transmitter has been selected to be dragged,deleted etc,occurs when mouse hovers over the emitter
	static int add=0; // For switching between clear,add emitter,delete emitter blocks on mouse clicks in the canvas
	static int accx=0,accy=0; // Points to the mid-value(x,y)of the linearly arranged transmitters
	static int tog; //Required to toggle + or - depending on whether the phase_cal point is < or > accx respectively
	static JButton AddSource,DeleteSource; //For adding and deleting transmitters
	static JButton Clear,PhaseCalc;  //For clearing the canvas and calculating the phase delay
	int ww,wh,wox,woy,gx,gy,gxy,wi,hi,si;// window width,window height,window offset x, window offset y, 
	static String med[]={"#800000","#ffffff","#000000","#808080","#0000ff","#000000","#000080","#00ff00"}; //The color values to be drawn
	static Color med2[]; // Color array soon to be instantiated with med[]
	static Image im,im2,im3; // Images for updating the canvas with the modified pixels
	static JTextArea text; // For displaying the Phase calculations for each individual transmitter 
	static int[] pixels,surface,pixel; // pixels and pixel are used for grabbing the pixels or modifying the pixels of the images
	//surface not yet implemented, for future use
	static float[] buf1,buf2,damp; // buf1,buf2 hold the perturbed values after disturbance whereas damp holds the values to dampen the waves
	static boolean md,mr; // To process the grid switching between left right and top bottom to obtain perfectly spherical rendering
	static ArrayList<Emitter_loc>loc; // Holds the x,y on canvas and on screen values of each individual transmitter
	static ArrayList<Double>phase_length,phase_del; // holds the Lx(length from nth transmitter to focal point)and time delay to counteract (R-Lx)
	static Checkbox view_phase_plane,viewreal;
	static MemoryImageSource source;
	static double l,va=22/7,vh=-38,vds,vhds,vs=0,vc=-1;
	static PixelGrabber p;
	static boolean dr,dc,ds,set=true,check=false;
	/* static     //data to arduino ,code still buggy
	{
		System.loadLibrary("blink");
	}
	public static native blink(String h);
	*/
    Ultra_virtual()
    {
	   can=new Ultra_canvas(this);
	   AddSource=new JButton("Add Emitters");
	   PhaseCalc=new JButton("Phase Calculation");
	   text=new JTextArea(5,10);
	   phase_length=new ArrayList<Double>();
	   phase_del=new ArrayList<Double>();
	   md=mr=true;
	   Clear=new JButton("Clear");
	   DeleteSource=new JButton("Delete Emitters");
	   JLabel L=new JLabel("Frequency",JLabel.CENTER);
	   JLabel L1=new JLabel("Resolution",JLabel.CENTER);
	   JLabel L2=new JLabel("Emitter Size");
	   frequency=new JScrollBar(JScrollBar.HORIZONTAL,15,1,1,30);
	   Resolution=new JScrollBar(JScrollBar.HORIZONTAL,110,5,5,400);
	   em_size=new JScrollBar(JScrollBar.HORIZONTAL,7,4,7,20);
	   med2=new Color[8];
	   view_phase_plane=new Checkbox("Phase Plane Plot");
	   viewreal=new Checkbox("view Real mode");
	   loc=new ArrayList();
	   can.addComponentListener(this);
	   can.addMouseMotionListener(this);
	   can.addMouseListener(this);
	   AddSource.addActionListener(this);
	   PhaseCalc.addActionListener(this);
	   Clear.addActionListener(this);
	   DeleteSource.addActionListener(this);
	   frequency.addAdjustmentListener(this);
	   Resolution.addAdjustmentListener(this);
	   em_size.addAdjustmentListener(this);
	   setLayout(this);
	   add(can);
	   add(AddSource);
	   add(PhaseCalc);
	   add(Clear);
	   add(DeleteSource);
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
		   med2[i]=Color.decode(med[i]);
	   }
	   settings();
	   setSize(800,640);
	   defineRaster();
	   setVisible(true);
	}
	public void settings() 
	{
		gxy = gx*gy;
		buf1  = new float[gxy];
		buf2 = new float[gxy];
		damp = new float[gxy];
		surface = new int[gxy];
        	l=0;
		int i, j;
		for (i = 0; i<gxy; i++)
		{
		    damp[i] = 1f;
		}
		for (i = 0; i<wox; i++)
		{
		    for (j = 0; j<gx; j++)
		    {
			    damp[i+j*gy] = damp[gx-1-i+gy*j] =damp[j+gy*i] = damp[j+(gy-1-i)*gy] =(float) (1-(wox-i) * .002);
		    }
		}
		if(Resolution.getValue()<32)
		{
			Resolution.setValue(32);
			setResolution();
			settings();
		}
		clearWave();
	}
	public void clearWave() 
	{
		for(int i=0;i<gxy;i++)
		{
			buf1[i]=buf2[i]=0;
		}
	}

	public void defineRaster()
	{
		 int w,h;
		 w=can.getWidth();
		 h=can.getHeight();
		 pixels = new int[w*h];
		 System.out.println(w+"h"+h);
		 source = new MemoryImageSource(w,h, pixels, 0,w);
		 source.setAnimated(true);
		 source.setFullBufferUpdates(true);
		 im = can.createImage(source);
	}

	public void mouseDragged(MouseEvent arg0) 
	{
		dr= true;
		loc_edit(arg0);
		can.repaint();
	}

	public void mouseMoved(MouseEvent arg0) 
	{
		int x = arg0.getX();
		int y = arg0.getY();
		dsX=x;
		dsY=y;
		em_select(arg0);
	}

	public void adjustmentValueChanged(AdjustmentEvent arg0) 
	{
	  
	  if(arg0.getSource()==Resolution)
	  {
		  setResolution();
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
		
		if(arg0.getSource()==Clear)
		{
			clearWave();
			check=false;
			set=true;
			can.repaint();
		}
		else if(arg0.getSource()==AddSource)
		{
			add=1;
		}
		else if(arg0.getSource()==DeleteSource)
		{
			add=2;
		}
		else if(arg0.getSource()==PhaseCalc)
		{
			add=3;
		}
	}

	public void phase_delay_cal(MouseEvent e) 
	{
		for(int i=0;i<loc.size();i++)
		{
			accx+=loc.get(i).x;
			accy+=loc.get(i).y;
		}
		accx=accx/transmitters;
		accy=accy/transmitters;
		int u=((((e.getX()*ww)-(can.getWidth()/2))/can.getWidth())+wox);
		int v=((((e.getY()*wh)-(can.getHeight()/2))/can.getHeight())+woy);
		double R=Math.sqrt(Math.pow((accx-u),2)+Math.pow((accy-v),2));
		double sin_phi=Math.abs(accx-u)/R;
		text.append("Phase delay for transmitters\n At Position"+u+" , "+v+"\n");
		if(u<accx)
		{
			tog=1;
		}
		else
		{
			tog=-1;
		}
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
			phase_del.add(i,(R-phase_length.get(i))*frequency.getValue());
		}
		double sm=phase_del.get(0);
		for(int i=1;i<loc.size();i++)
		{
			if(phase_del.get(i)<sm)
			{
				sm=phase_del.get(i);
			}
		}
		for(int j=0;j<loc.size();j++)
		{
			double g=phase_del.get(j);
			phase_del.remove(j);
			phase_del.add(j,g-sm);
			text.append("Position = "+loc.get(j).x+"  "+loc.get(j).y+" is ="+phase_del.get(j)+"\n");
			check=true;
		}
		/*for(int i=0;i<transmitters;i++)   //for transmitting data to arduino ,still buggy
		{
			String h="";
			h=h+phase_del.get(i)+","+loc.get(i).x;
			blink(h);
		}*/
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
		}
		
	}
	
	public void paintComponent(Graphics g)
	{
		can.repaint();
	}
	public void addEmitters(int x,int y) 
	{
		transmitters++;
	    loc.add(new Emitter_loc(x,y));
	}

	public void mouseClicked(MouseEvent arg0) 
	{
		if(arg0.getSource()==can)
		{
			switch(add)
			{
				case 1:
					int x=(((arg0.getX()*ww)-(can.getWidth()/2))/can.getWidth())+wox;
					int y=(((arg0.getY()*wh)-(can.getHeight()/2))/can.getHeight())+woy;
					System.out.println(x+"  hoi");
					addEmitters(x,y);
					add=0;
					break;
					
				case 2:
					removeEmitters(arg0.getX(),arg0.getY());
					add=0;
					break;
				
				case 3:
					phase_delay_cal(arg0);
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
	public void layoutContainer(Container arg0)
	{
		int arg0w = arg0.getSize().width;
		int cw = arg0w* 7/10;
		int arg0h = arg0.getSize().height;
	    	arg0.getComponent(0).setSize(cw, arg0h);
		int barwidth = arg0w - cw;
		int h=0;
		for (int i = 1; i < arg0.getComponentCount(); i++) 
		{
		    	Component m = arg0.getComponent(i);
		  	Dimension d = m.getPreferredSize();
			if (arg0.getComponent(i) instanceof JScrollBar)
			{
			    d.width = barwidth;
			}
			if (arg0.getComponent(i) instanceof JLabel)
			{
			    h += d.height/5;
			    d.width = barwidth;
			}
			m.setLocation(cw,h);
			m.setSize(d);
			h += d.height;
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
		int mxx = gx-1;
		int mxy = gy-1;
		for (int i = 0;i<1; i++) 
		{
			int js, je, ji;
			if (md) 
			{
			    js = 1; je = mxy; ji = 1; md = false;
			}
			else 
			{
			    js = mxy-1; je = 0; ji = -1; md = true;
			}
			mr = md;
			
			for (int j = js; j != je; j += ji) 
			{
			    int is, ie, ii;
			    if (mr) 
			    {
					ii = 1; is = 1; ie = mxx; mr = false;
			    } 
			    else 
			    {
					ii = -1; is = mxx-1; ie = 0; mr = true;
			    }
			    int gi = j*gy+is;
			    int gie = j*gy+ie;
			    for (; gi != gie; gi += ii) 
			    {
			    	float b =(buf1[gi-1]+buf1[gi+1]+buf1[gi-gy]+buf1[gi+gy])*0.25f;
			    	buf1[gi]*=damp[gi];
			    	buf2[gi]*=damp[gi];
			    	buf1[gi]-=b;
			    	float x=(float)(Math.sin(0.25)*buf2[gi]+Math.cos(0.25)*buf1[gi]+b);
			    	float y=(float)(Math.cos(0.25)*buf2[gi]-Math.sin(0.25)*buf1[gi]);
			    	buf1[gi]=x;
			    	buf2[gi]=y;
			    }
			}
			l += 0.25;
			if (transmitters> 0) 
			{
			    double w = frequency.getValue()*l*0.0233;
			    double v = 0;
			    v = Math.cos(w);
			    for (int j = 0; j<transmitters; j++)
			    {
			    	if(!check)
			    	{
			    		loc.get(j).v = (float) (v);
			    	}
			    	else
			    	{
			    		loc.get(j).v=(float)Math.cos((2*Math.PI*frequency.getValue()*phase_del.get(j)+Math.PI*0.5)+w);
			    	}
			    }
			    for (i = 0; i<transmitters; i++) 
			    {
				    buf1[loc.get(i).x+gy*loc.get(i).y] =loc.get(i).v;
				    buf2[loc.get(i).x+gy*loc.get(i).y] = 0;
		            }
			}
		}
		

		if (view_phase_plane.getState()&&(!viewreal.getState()))
		{
		    set=true;
		    surfacev();
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
		    source.newPixels();

		g1.drawImage(im, 0, 0, this);
		int x=((((dsX*ww)-(can.getWidth()/2))/can.getWidth())+wox);
		int y=((((dsY*ww)-(can.getHeight()/2))/can.getHeight())+woy);
		String s = "(" + x + "," + y +")";
		g1.setColor(Color.white);
		g1.drawString(s,gy/18,gy/10);
	
		
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
		for (i = 0; i<transmitters; i++) 
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
		if (i < 0 || i>= can.getWidth())
		    return;
		try 
		{ 
			pixels[i+j*can.getWidth()] =col; 
			
		} catch (Exception e) {}
	
		
	}

	private void surfacev() 
	{
		//For future 
		
	}
	 void loc_edit(MouseEvent e)
	 {
			if (view_phase_plane.getState())
			    return;
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
			    return;
			}
	}
	class Emitter_loc
	{
		int x,y;
		float v;
		Emitter_loc(int xx,int yy)
		{
			System.out.println(x+"   hi   "+y);
			x=xx;
			y=yy;
		}
		int getx()
		{
			return ((x-wox) * can.getWidth()+can.getWidth()/2)/ww;
		}
		int gety()
		{
			return ((y-woy) * can.getHeight()+can.getHeight()/2)/wh;
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
    	for (i = 0; i<transmitters; i++)
    	{
    		Emitter_loc f=loc.get(i);
    		int x2 = f.getx();
    		int y2 = f.gety();
    		if (Math.pow(em_size.getValue(), 2)>( Math.pow((x2-x),2)*Math.pow(y2-y,2))) 
    		{
    			selemitter= i;
    			return;
    		}
	    }
        selemitter = -1;
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
		m2.field(g);
	}
	public void update(Graphics g)
	{
		m2.field(g);
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
	int wi=800,hi=640,hw=wi/2,hh=hi/2,fir=wi,sec=wi*(hi+3);
	int [] pixel,pixelupdate,buf;
	ArrayList<String>up=new ArrayList<String>();
	PixelGrabber p;
	Ultra_virtual v2;
	Thread engine;
	MemoryImageSource s;
	Image im,im2;
	JLabel l;
	public Dimension getMinimumSize()
	{
        return new Dimension(100, 100);
    }

    canvas2(Ultra_virtual v)
    {
    	v2=v;
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
			p.grabPixels();
		}catch(InterruptedException e)
		{
			
		}
		s=new MemoryImageSource(wi,hi,pixelupdate,0,wi);
		s.setAnimated(true);
		s.setFullBufferUpdates(true);
		im2=createImage(s);
		if(engine==null)
		{
			engine=new Thread(this);
		}
		engine.start();
		Timer t=new Timer(100,this);
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
		while(Thread.currentThread()==engine)
		{
			updategrid();
			s.newPixels();
			repaint();
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
}
