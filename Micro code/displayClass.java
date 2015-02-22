import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;

public class displayClass extends JDialog
implements ActionListener {

private static final long serialVersionUID = 6227185700883851501L;
private JButton startButton;
private JButton stopButton;
private volatile static JLabel uartTitleStsString = new JLabel("UART STATUS:", JLabel.CENTER);
private volatile static JLabel modeTitleStsString = new JLabel("Mode:", JLabel.CENTER);
public volatile static JLabel modeStsString = new JLabel("", JLabel.CENTER);
private volatile static JLabel uartStsString = new JLabel("UART NOT INITIALIZED", JLabel.CENTER);
private volatile static JLabel buffer_size = new JLabel("Buffer size", JLabel.CENTER);
private JPanel display;
public static int red=0,green=0,blue=0;

public displayClass() {
    initUI();
}

private void initUI() {
    JPanel bottom = new JPanel();
    bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
    bottom.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    JPanel leftPanel = new JPanel();
    leftPanel.setFocusable(false);
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

    startButton = new JButton("Start");
    startButton.setFocusPainted(false);
    startButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
        	try {
				serialPortClass.outputStream.write((char)awesome1.hstStartChar);
			} catch (IOException e) {
				e.printStackTrace();
			}
       }
    });
    
    stopButton = new JButton("Stop");
    stopButton.setFocusPainted(false);
    stopButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
        	startButton.setText("Resume");
        	try {
				serialPortClass.outputStream.write((char)awesome1.hstStopChar);
			} catch (IOException e) {
				e.printStackTrace();
			}
			awesome1.writer.println(String.valueOf(awesome1.num.get(0)));
			awesome1.writer.println(String.valueOf(awesome1.num.get(1)));
			awesome1.writer.println(String.valueOf(awesome1.num.get(2)));
			awesome1.writer.println(String.valueOf(awesome1.num.get(3)));
			for(int l =0; l<awesome1.no_of_bodies;l++)
			{
				awesome1.writer.println();
				awesome1.writer.println(String.valueOf(awesome1.mass.get(l)));
				awesome1.writer.println(String.valueOf(awesome1.x_pos.get(l)));
				awesome1.writer.println(String.valueOf(awesome1.y_pos.get(l)));
				awesome1.writer.println(String.valueOf(awesome1.x_vel.get(l)));
				awesome1.writer.println(String.valueOf(awesome1.y_vel.get(l)));
			}
			awesome1.writer.close();
       }
    });
    
    startButton.setMaximumSize(new Dimension(125,22));
    stopButton.setMaximumSize(new Dimension(125,22));
    startButton.setMinimumSize(new Dimension(125,22));
    stopButton.setMinimumSize(new Dimension(125,22));
    
    leftPanel.add(modeTitleStsString);
    leftPanel.add(modeStsString);
    leftPanel.add(Box.createRigidArea(new Dimension(25, 21)));
    leftPanel.add(uartTitleStsString);
    uartStsString.setForeground(Color.RED);
    leftPanel.add(uartStsString);
    leftPanel.add(Box.createRigidArea(new Dimension(25, 21)));
    leftPanel.add(startButton);
    leftPanel.add(Box.createRigidArea(new Dimension(25, 7)));
    leftPanel.add(stopButton);
    leftPanel.add(Box.createRigidArea(new Dimension(25, 7)));
    leftPanel.add(buffer_size);
    leftPanel.add(Box.createRigidArea(new Dimension(25, 21)));

    bottom.add(leftPanel);
    bottom.add(Box.createRigidArea(new Dimension(20, 0)));
    
    display = new panel();

    bottom.add(display);
    add(bottom);
    pack();
    
    setTitle("n-body gui");
    setResizable(false);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
    }
    
    public static void updateUartStsString(){
    	uartStsString.setForeground(Color.GREEN);
    	uartStsString.setText("UART INITIALIZED");
    }
    public static void updateBufferSize(){
    	buffer_size.setForeground(Color.GREEN);
    	buffer_size.setText(String.valueOf(awesome1.buffersize1));
    }
}
class panel extends JPanel
{
	private static final long serialVersionUID = -2184467143511359816L;
	private int dmnsn = 700;

	public panel(){
		super.setMinimumSize(new Dimension(dmnsn,dmnsn));
		super.setPreferredSize(new Dimension(dmnsn, dmnsn));
		super.setMaximumSize(new Dimension(dmnsn,dmnsn));
	    super.setBorder(LineBorder.createBlackLineBorder());
	}
    
	@Override
	public void paintComponent(Graphics g){
		if(awesome1.mode == 1)
		{
		    super.paintComponent(g);
		    this.setOpaque(true);
		    this.setBackground(Color.black);
	

		    Graphics2D g2 = (Graphics2D)g;
		    AffineTransform at = g2.getTransform();
	        at.translate(0, getHeight());
	        at.scale(1, -1);
	        g2.setTransform(at);
                

                    for (int i = 0; i < awesome1.no_of_bodies; i++) {
                        if (Math.abs(awesome1.x_pos.get(i)) > awesome1.max_pos) {
                            awesome1.max_pos =  Math.abs(awesome1.x_pos.get(i));
                        }
                        if (Math.abs(awesome1.y_pos.get(i)) >  awesome1.max_pos) {
                            awesome1.max_pos =  Math.abs(awesome1.y_pos.get(i));
                        }
                    }

                        awesome1.factor = awesome1.max_pos / 320;

                    for (int i = 0; i < awesome1.no_of_bodies; i++) {

                        double temp_x_pos = awesome1.x_pos.get(i) / awesome1.factor;
                        double temp_y_pos = awesome1.y_pos.get(i) / awesome1.factor;
                        
                        int int_x_pos = ((int)temp_x_pos) + 350;
                        int int_y_pos = ((int)temp_y_pos) + 350;
 
                        awesome1.cur_x_pos.set(i, int_x_pos);
                        awesome1.cur_y_pos.set(i, int_y_pos);
                    }
                
                    
	        for(int i=0;i<awesome1.no_of_bodies;i++){
	        	Color test = Color.decode(awesome1.cur_ball_color.get(i));
	        	g.setColor(Color.BLACK);
	    	    g.drawOval(awesome1.cur_x_pos.get(i), awesome1.cur_y_pos.get(i), 12, 12);
	        	g.setColor(test);
	    	    g.fillOval(awesome1.cur_x_pos.get(i), awesome1.cur_y_pos.get(i), 12, 12);
	        }
		}
		else if(awesome1.mode == 0)
		{
		    super.paintComponent(g);
		    this.setOpaque(true);
		    this.setBackground(Color.black);
		}
	}
}