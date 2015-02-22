import java.io.*;
import java.util.*;
import java.lang.Math.*;

public class awesome1 {
	public static displayClass ex = new displayClass();
	static double tgtInitStr = 11.11;
	static char hstInitStartStr = '<';
	static char hstInitStopStr = '>';
	static char hstStartChar = 'S';
	static char hstStopChar = 't';
        public static float max_pos = 0;
        public static float factor = 0;
        public static float global_factor = 0;
	static boolean tgtInitSts = false;
        static List<Float> num = new ArrayList<Float>();
        public volatile static List<Float> x_pos = new ArrayList<Float>();
        public volatile static List<Float> y_pos = new ArrayList<Float>();
        public volatile static List<Float> mass = new ArrayList<Float>();
        public volatile static List<Float> x_vel = new ArrayList<Float>();
        public volatile static List<Float> y_vel = new ArrayList<Float>();
        public volatile static int mode, no_of_bodies, compute_time_w, compute_time_d;
    	public volatile static List<String> cur_ball_color = new ArrayList<String>();
    	public volatile static List<Integer> cur_x_pos = new ArrayList<Integer>();
    	public volatile static List<Integer> cur_y_pos = new ArrayList<Integer>();
    	public volatile static int buffersize;
    	public volatile static int buffersize1;
    	static PrintWriter writer = null;
    	static String[] ColourValues = new String[] { 
            "#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF", 
            "#808000", "#008000", "#000080", "#808000", "#800080", "#008080", "#808080", 
            "#C00000", "#00C000", "#0000C0", "#C0C000", "#C000C0", "#00C0C0", "#C0C0C0", 
            "#400000", "#004000", "#000040", "#404000", "#400040", "#004040", "#404040", 
            "#200000", "#002000", "#000020", "#202000", "#200020", "#002020", "#202020", 
            "#600000", "#006000", "#000060", "#606000", "#600060", "#006060", "#606060", 
            "#A00000", "#00A000", "#0000A0", "#A0A000", "#A000A0", "#00A0A0", "#A0A0A0", 
            "#E00000", "#00E000", "#0000E0", "#E0E000", "#E000E0", "#00E0E0", "#E0E0E0", 
        };
public static void main(String[] args) {
        awesome1 main = new awesome1();
    num = main.readFile("input.txt");
    main.setFileParams();
    
    System.out.println("mode value is" + mode);
    for(int m=0;m<no_of_bodies; m++)
    {
    	System.out.println("x postion is" + x_pos.get(m));
    }
    for(int m=0;m<no_of_bodies; m++)
    {
    	System.out.println("y postion is" + y_pos.get(m));
    }
    if(mode == 1)
    	displayClass.modeStsString.setText("Visualization");
    else if(mode == 0)
    {
    	displayClass.modeStsString.setText("Computation");
		try {
			writer = new PrintWriter("output.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }
    for(int i=0;i<awesome1.no_of_bodies;i++){
    	cur_ball_color.add(ColourValues[i%56]);
    	float temp, temp1;
    	temp = awesome1.x_pos.get(i);
    	temp1 = awesome1.y_pos.get(i);
    	cur_x_pos.add((int)temp);
        cur_y_pos.add((int)temp1);
    }
    
    // Calculation for the global division factor 
    for (int i = 0; i < awesome1.no_of_bodies; i++) {
        if (awesome1.x_pos.get(i) > awesome1.max_pos) {
            awesome1.max_pos = awesome1.x_pos.get(i);
        }
        if (awesome1.y_pos.get(i) > awesome1.max_pos) {
            awesome1.max_pos = awesome1.y_pos.get(i);
        }
    }

        awesome1.factor = awesome1.max_pos / 300;


   	serialPortClass serial = new serialPortClass();
    serial.SerialPort_Init("COM3");
   	
    /* Display operations */
    ex.setVisible(true);
    }
    


public List<Float> readFile(String filename)
{
        List<Float> num_loc = new ArrayList<Float>();
        try {    
        Scanner fileScanner = new Scanner(new FileReader(filename));    
        while (fileScanner.hasNextLine()) {    
                         while (fileScanner.hasNext()) { //Check if there is anything in the line  
                                 if (fileScanner.hasNextFloat()) { //Extract just numbers from the current line  
                                 num_loc.add((Float) fileScanner.nextFloat());
                             }  
                             else {  
                                 fileScanner.next();  
                             }  
                         }          
        }
        fileScanner.close();
    }    
    catch (FileNotFoundException e){    
        System.out.println("Sorry! This file is not found");    
    }
        return num_loc;
}

public void setFileParams(){
   		//	temp_mode		= (num.get(0));
   			mode			= (int)(float)(num.get(0));
   			no_of_bodies		= (int)(float)(num.get(1));
           compute_time_w     = (int)(float)(num.get(2));
           compute_time_d     = (int)(float)(num.get(3));
           for (int i=4; i < (4 + no_of_bodies*5); i=i+5)
           {
                   mass.add((Float)num.get(i));
           }
           for (int i=5; i < (5 + no_of_bodies*5); i=i+5)
           {
                   x_pos.add((Float)num.get(i));
           }
           for (int i=6; i < (6 + no_of_bodies*5); i=i+5)
           {
                   y_pos.add((Float)num.get(i));
           }
           for (int i=7; i < (7 + no_of_bodies*5); i=i+5)
           {
                   x_vel.add((Float)num.get(i));
           }
           for (int i=8; i < (8 + no_of_bodies*5); i=i+5)
           {
                   y_vel.add((Float)num.get(i));
           }
}
}