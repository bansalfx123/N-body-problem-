/*********************************
 Project Name: N-body Simulation

 Authors: Samir Senapati Polobody
 	 	: Mohit Mahesh Bansal

 Professor: Dr. Roozbeh Jafari
 *********************************/

#include "main.h"

init_params ip;
body_params bp[MAX_BODY_CNT];
send_params sp;

char rx_char;
char rx_buff[15];
unsigned int rx_cnt=0;
unsigned int rx_param=0;
float val=0;
int i=0;
int j=0;
char temp[15];
int compute_time = 0;

int p1 = 0;
int p2 = 0;                //particle IDs
double dx = 0.0;
double dy = 0.0;         //distance between particles along axes
double D = 0.0;                  //resultant distance between particles
double A = 0.0;                  //resultant acceleration
double pax = 0.0;      //acceleration along each axis
double pay = 0.0;      //acceleration along each axis
float g = 6.673E-11;
int period1=86400;
char flag = 'n';

unsigned int cnt = 0;


void
UARTSend(const uint8_t *pui8Buffer, uint32_t ui32Count)
{
    while(ui32Count--)
    {
        ROM_UARTCharPut(UART0_BASE, *pui8Buffer++);
    }
}

int main(void) {
	init();
	sp.xc =11.11;
	UARTSend((uint8_t *)sp.p, 4);
	while(1)
	{
		if(flag == 'y')
		{
			   for (p1 = 0; p1 < ip.no_of_bodies; p1++)
			   {
			      pax = pay =0.0;
			      for (p2 = 0; p2 < ip.no_of_bodies; p2++)            //determine acceleration
			      {
			    	  if (p1 != p2)
			    	  {
			    		  dx = bp[p2].x_pos - bp[p1].x_pos;
			    		  dy = bp[p2].y_pos - bp[p1].y_pos;
			    		  D = sqrt(dx*dx + dy*dy);
			    		  A = g * bp[p2].mass / (D*D);
			    		  pax += dx * A / D;
			    		  pay += dy * A / D;
			    	  }
			      }
		 	     bp[p1].x_vel += pax * period1;            //accelerate
		 	     bp[p1].y_vel += pay * period1;
		   	   }

			   flag = 'n';
		}
	}
}

void init()
{
	ROM_FPUEnable();
	ROM_FPULazyStackingEnable();
	ROM_SysCtlClockSet(SYSCTL_SYSDIV_1 | SYSCTL_USE_OSC | SYSCTL_OSC_MAIN | SYSCTL_XTAL_16MHZ);
	ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_GPIOF);
	ROM_GPIOPinTypeGPIOOutput(GPIO_PORTF_BASE, GPIO_PIN_2);
	ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_UART0);
	ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_GPIOA);
	ROM_IntMasterEnable();
    ROM_GPIOPinConfigure(GPIO_PA0_U0RX);
    ROM_GPIOPinConfigure(GPIO_PA1_U0TX);
	ROM_GPIOPinTypeUART(GPIO_PORTA_BASE, GPIO_PIN_0 | GPIO_PIN_1);
	ROM_UARTConfigSetExpClk(UART0_BASE, ROM_SysCtlClockGet(), 115200,
	(UART_CONFIG_WLEN_8 | UART_CONFIG_STOP_ONE |
	UART_CONFIG_PAR_NONE));
	ROM_IntEnable(INT_UART0);
	ROM_UARTIntEnable(UART0_BASE, UART_INT_RX | UART_INT_RT);
}

void timer_stop()
{
    TimerDisable(TIMER0_BASE, TIMER_A);
    TimerDisable(TIMER1_BASE, TIMER_A);
}

void Timer1BIntHandler(void)
{
    TimerIntClear(TIMER1_BASE, TIMER_TIMA_TIMEOUT);
//	t_stop = SysTickValueGet();
	   for (p1 = 0; p1 < ip.no_of_bodies; p1++)
	   {
	   bp[p1].x_pos += bp[p1].x_vel * period1;          //move
	   bp[p1].y_pos += bp[p1].y_vel * period1;
	   sp.xc = p1;
	   UARTSend((uint8_t *)sp.p, 4);
	   sp.xc = bp[p1].x_pos;
	   UARTSend((uint8_t *)sp.p, 4);
	   sp.xc = bp[p1].y_pos;
	   UARTSend((uint8_t *)sp.p, 4);
	   sp.xc = bp[p1].x_vel;
	   UARTSend((uint8_t *)sp.p, 4);
	   sp.xc = bp[p1].y_vel;
	   UARTSend((uint8_t *)sp.p, 4);
	   }
    timer_stop();

}

void Timer0AIntHandler(void)
{

	//t_start = SysTickValueGet();
	TimerIntClear(TIMER0_BASE, TIMER_TIMA_TIMEOUT);
	flag = 'y';
	cnt++;
}


void timer_init()
{
    SysCtlPeripheralEnable(SYSCTL_PERIPH_TIMER0);
    SysCtlPeripheralEnable(SYSCTL_PERIPH_TIMER1);

    TimerDisable(TIMER0_BASE, TIMER_A);
    TimerDisable(TIMER1_BASE, TIMER_A);

    TimerConfigure(TIMER0_BASE, TIMER_CFG_A_PERIODIC);
    TimerConfigure(TIMER1_BASE, TIMER_CFG_A_PERIODIC);

    TimerLoadSet(TIMER0_BASE, TIMER_A, 50 * (SysCtlClockGet() / 1000));
    TimerLoadSet(TIMER1_BASE, TIMER_A, ((compute_time) * (SysCtlClockGet()/20)));

    TimerIntRegister(TIMER0_BASE, TIMER_A, &Timer0AIntHandler);
    TimerIntRegister(TIMER1_BASE, TIMER_A, &Timer1BIntHandler);

    IntEnable(INT_TIMER0A);
    IntEnable(INT_TIMER1A);

	TimerIntClear(TIMER0_BASE, TIMER_TIMA_TIMEOUT);
	TimerIntClear(TIMER1_BASE, TIMER_TIMA_TIMEOUT);

    TimerIntEnable(TIMER0_BASE, TIMER_TIMA_TIMEOUT);
    TimerIntEnable(TIMER1_BASE, TIMER_TIMA_TIMEOUT);
    IntMasterEnable();

    TimerEnable(TIMER0_BASE, TIMER_A);
    if(ip.mode == 0)
    {
    TimerEnable(TIMER1_BASE, TIMER_A);
    }

}
void UARTIntHandler(void)
{
	unsigned long ulStatus;
	ulStatus = ROM_UARTIntStatus(UART0_BASE, true);
	ROM_UARTIntClear(UART0_BASE, ulStatus);

	while(ROM_UARTCharsAvail(UART0_BASE))
	{
		rx_char=ROM_UARTCharGetNonBlocking(UART0_BASE);
		// Initialization values from host
		switch (rx_char)
		{
			case '<':
			{
				break;
			}
			case '>':
			{
//				UARTprintf(">");
			}
			case ',':
			{

				for(i=0;i<rx_cnt;i++)
				{
					temp[i]=rx_buff[i];
				}
				val = atof(temp);
				switch(rx_param)
				{
					case 0:
						ip.mode = val;
					break;
					case 1:
						ip.no_of_bodies = val;
						break;
					case 2:
						ip.compute_time_w = val;
						break;
					case 3:
						ip.compute_time_d = val;
						break;
					default:
					{
							if(rx_param < (4 + ip.no_of_bodies))
							{
								bp[j].mass = val;
								j++;
							}
							else if(rx_param < (4 + (ip.no_of_bodies * 2)))
							{
								if(j == (ip.no_of_bodies))
								{
									j=0;
								}
								bp[j].x_pos = val;
								j++;
							}
							else if(rx_param < (4 + (ip.no_of_bodies * 3)))
							{
								if(j == (ip.no_of_bodies))
								{
									j=0;
								}
								bp[j].y_pos = val;
								j++;
							}
							else if(rx_param < (4 + (ip.no_of_bodies * 4)))
							{
								if(j == (ip.no_of_bodies))
								{
									j=0;
								}
								bp[j].x_vel = val;
								j++;
							}
							else if(rx_param < (4 + (ip.no_of_bodies * 5)))
							{
								if(j == (ip.no_of_bodies))
								{
									j=0;
								}
								bp[j].y_vel = val;
								j++;

							}
							break;
					}

				}
				for(i=0;i<rx_cnt;i++)
				{
					temp[i]='.';
				}
				rx_param++;
				rx_cnt=0;
				val=0;
				break;
			}
			case 'S':
			{
				compute_time = (ip.compute_time_w*7 + ip.compute_time_d);
				timer_init();
				break;
			}
			case 't':
			{
			    TimerDisable(TIMER0_BASE, TIMER_A);
			    TimerDisable(TIMER1_BASE, TIMER_A);
				break;
			}
			default:
			{
				rx_buff[rx_cnt] = rx_char;
				rx_cnt++;
				break;
			}
		}
	}
}
