
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import gnu.io.*;

public class serialPortClass implements SerialPortEventListener{
	static String initStrTempCopy = "";
    SerialPort port = null;
	InputStream inputStream;
	static OutputStream outputStream;
	
	char[] rx_buff = new char[10];
	int rx_cnt=0;
	int rx_param=0;
	double val=0;
	int i=0;
	String s = "";
	int dispBallNo;
	float dispXPos;
	float dispYPos;
	float dispXvel;
	float dispYvel;
    float temp;
    float temp1;
    int store_cnt=0;
    int set_cnt = 0;
    float factor = 1;
    
	public void SerialPort_Init(String portname){
		System.out.println("some event happened1");
        String wantedPortName = portname;
        System.setSecurityManager(null);
        String driverName = "com.sun.comm.Win32Driver"; // or get as a JNLP property
        CommPortIdentifier portId = null;  // will be set if port found

 
        //
        // Get an enumeration of all ports known to JavaComm
        //
        Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
        //
        // Check each port identifier if 
        //   (a) it indicates a serial (not a parallel) port, and
        //   (b) matches the desired name.
        //

        while (portIdentifiers.hasMoreElements())
        {
            CommPortIdentifier pid = (CommPortIdentifier) portIdentifiers.nextElement();
            if(pid.getPortType() == CommPortIdentifier.PORT_SERIAL &&
               pid.getName().equals(wantedPortName)) 
            {
                System.out.println("Port used : " + pid.getName());
                portId = pid;
                break;
            }
        }
        if(portId == null)
        {
            System.err.println("Could not find serial port " + wantedPortName);
            System.exit(1);
        }

        try {
            port = (SerialPort) portId.open(
                "SerialPort_Display", // Name of the application asking for the port 
                10000   // Wait max. 10 sec. to acquire port
            );
        } catch(PortInUseException e) {
            System.err.println("Port already in use: " + e);
            System.exit(1);
        }
        try{port.setSerialPortParams(
            115200,
            SerialPort.DATABITS_8,
            SerialPort.STOPBITS_1,
            SerialPort.PARITY_NONE);
            }catch(UnsupportedCommOperationException e){}
		try {
            inputStream = port.getInputStream();
        } catch (IOException e) {}
        try {
            outputStream = port.getOutputStream();
        } catch (IOException e) {}
		try {
            port.addEventListener(this);
		} catch (TooManyListenersException e) {}
        port.notifyOnDataAvailable(true);
        System.out.println("Inputstream : " + inputStream);
        System.out.println("Outputstream : " + outputStream);
    }
	public void serialEvent(SerialPortEvent event) {
        switch(event.getEventType()) {
	        case SerialPortEvent.BI:
	        case SerialPortEvent.OE:
	        case SerialPortEvent.FE:
	        case SerialPortEvent.PE:
	        case SerialPortEvent.CD:
	        case SerialPortEvent.CTS:
	        case SerialPortEvent.DSR:
	        case SerialPortEvent.RI:
	        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
	            break;
	        case SerialPortEvent.DATA_AVAILABLE:
		        byte[] rx = new byte[4];
		        try {
		        	awesome1.buffersize1 = inputStream.available();
				while(inputStream.available() >= 4)
		        {
		        	awesome1.buffersize = inputStream.read(rx, 0, 4);
		        	if(awesome1.tgtInitSts == false)
		        	{
	    		        temp1= ByteBuffer.wrap(rx).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	    		        System.out.println(temp1);
				    	if(temp1>11.1)
				    	{
				    		awesome1.tgtInitSts = true;
				        	displayClass.updateUartStsString();
				        	System.out.println("\nUART Initialized");
			                uartInithdlr();
				    	}
		        	}
		        	else
		        	{
	    		        temp= ByteBuffer.wrap(rx).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	    		//        System.out.println(temp);
	    	    		System.out.println("temp is" + temp + " , store_cnt is" + store_cnt + ", set_cnt is" + set_cnt);
	    		        if(store_cnt % 5 == 0)
	    		        {
	    		        	dispBallNo = (int) temp;
	        		        store_cnt++;
	    		        }
	    		        else if(store_cnt % 5 == 1)
	    		        {
	    		        	dispXPos = temp;
	        				awesome1.x_pos.set(dispBallNo, dispXPos);
	        		        store_cnt++;
	    		        }
	    		        else if(store_cnt % 5 == 2)
	    		        {
	    		        	dispYPos = temp;
	        				awesome1.y_pos.set(dispBallNo, dispYPos);
	        		        store_cnt++;
	    		        }
	    		        else if(store_cnt % 5 == 3)
	    		        {
	    		        	dispXvel = temp;
	        				awesome1.x_vel.set(dispBallNo, dispXvel);
	        		        store_cnt++;
	    		        }
	    		        else if(store_cnt % 5 == 4)
	    		        {
	    		        	dispYvel = temp;
	        				awesome1.y_vel.set(dispBallNo, dispYvel);
	        				store_cnt = 0;
	        				set_cnt++;
	    		        }
                                if(set_cnt % awesome1.no_of_bodies == 1 && awesome1.mode == 1)
                                {
                                	displayClass.updateBufferSize();
                                	awesome1.ex.repaint();
                                }
		        	}
		        }

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		        break;
        }
	}
	
	public void uartInithdlr(){
		      	try {
                outputStream.write((char)awesome1.hstInitStartStr);
                char [] chars = String.valueOf(awesome1.mode).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                }
                outputStream.write(',');
                chars = String.valueOf(awesome1.no_of_bodies).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                }
                
                outputStream.write(',');
                chars = String.valueOf(awesome1.compute_time_w).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                }
                
                outputStream.write(',');
                chars = String.valueOf(awesome1.compute_time_d).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                }

                for(i=0; i<awesome1.no_of_bodies; i++)
                {
                outputStream.write(',');
                chars = String.valueOf(awesome1.mass.get(i)).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                	}
                }
                
                for(i=0; i<awesome1.no_of_bodies; i++)
                {
                outputStream.write(',');
                chars = String.valueOf(awesome1.x_pos.get(i)).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                	}
                }
                
                for(i=0; i<awesome1.no_of_bodies; i++)
                {
                outputStream.write(',');
                chars = String.valueOf(awesome1.y_pos.get(i)).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                	}
                }

                for(i=0; i<awesome1.no_of_bodies; i++)
                {
                outputStream.write(',');
                chars = String.valueOf(awesome1.x_vel.get(i)).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                	}
                }
                
                for(i=0; i<awesome1.no_of_bodies; i++)
                {
                outputStream.write(',');
                chars = String.valueOf(awesome1.y_vel.get(i)).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                	}
                }
                
                outputStream.write((char)awesome1.hstInitStopStr);
            } catch (IOException e) {}
    	}

	}

