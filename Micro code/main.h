#ifndef MAIN_H_
#define MAIN_H_

#include <stdbool.h>
#include <stdint.h>
#include "stdlib.h"
#include "string.h"
#include "inc/hw_ints.h"
#include "inc/hw_memmap.h"
#include "inc/hw_types.h"
#include "inc/hw_timer.h"

#include "driverlib/timer.h"
#include "driverlib/debug.h"
#include "driverlib/fpu.h"
#include "driverlib/gpio.h"
#include "driverlib/interrupt.h"
#include "driverlib/pin_map.h"
#include "driverlib/rom.h"
#include "driverlib/sysctl.h"
#include "driverlib/systick.h"
#include "driverlib/uart.h"

#include "utils/cpu_usage.h"

#include "inc/hw_gpio.h"
#include "inc/hw_ints.h"
#include "utils/sine.h"
#include "math.h"

#include "utils/uartstdio.h"
//#include "utils/uartstdio.c"
//******************************************************************

#define MAX_BODY_CNT (50)


typedef struct init_params{
	int mode;
	int no_of_bodies;
	int compute_time_w;
	int compute_time_d;
}init_params;

//Structure for Ball Parameters
typedef struct body_params{
	float mass;
	float x_pos;
	float y_pos;
	float x_vel;
	float y_vel;
}body_params;

typedef union send_params{
	float xc;
	char p[4];
}send_params;


void init(void);

#endif //  MAIN_H_
