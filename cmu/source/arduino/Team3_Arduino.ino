/****************************************************************
* File: LEDTest
* Project: LG Exec Ed Program
* Copyright: Copyright (c) 2016 Anthony J. Lattanze
* Versions:
* 1.0 May 2016 - Initial version
*
* Description:
*
* This program simply turns on and off all the LEDs on the parking
* lot game board, Note that the entry and exit LEDs can be both
* green and red (blue as well, but the blue pin bas been cliped).
*
* Parameters: None
*
* Internal Methods: None
***************************************************************/
#include <SPI.h>
#include <WiFi.h>
#include <Servo.h> 
#include <string.h> 
#include <stdlib.h> 
#include <avr/wdt.h>

#define Open  90
#define Close 0
#define PORTID 5001             // IP socket port#

char ssid[] = "LGArchi";           // The network SSID for CMU unsecure network
//char ssid[] = "LGArchi_Guest1";
char wifiPass[] = "16swarchitect";
char spID[6] = {'$','0','0','0','0'};
int status = WL_IDLE_STATUS;   // Network connection status
WiFiServer server(PORTID);     // Server connection and port
char inChar;                   // Character read from client
IPAddress ip;                  // The IP address of the shield
IPAddress subnet;              // The IP address of the shield
long rssi;                     // Wifi shield signal strength
byte mac[6];                   // Wifi shield MAC address
 
#define EntryGateServoPin 5
#define ExitGateServoPin 6
#define EntryGateGreenLED 26
#define EntryGateRedLED   27
#define ExitGateGreenLED  28
#define ExitGateRedLED    29
#define ParkingStall1LED  22
#define ParkingStall2LED  23
#define ParkingStall3LED  24
#define ParkingStall4LED  25
#define Stall1SensorPin 30
#define Stall2SensorPin 31
#define Stall3SensorPin 32
#define Stall4SensorPin 33
#define EntryBeamRcvr  34 
#define ExitBeamRcvr   35

#define	STALL_LED 0;
#define	EXIT_LED 1;
#define	ENTRY_LED 2;
#define	EXIT_SERVO 3;
#define	ENTRY_SERVO 4;

#define	STALL_SENSOR 0;
#define	EXIT_SENSOR 1;
#define	ENTRY_SENSOR 2;


long  StallSensorVal[4];
long  StallSensorValOffset[4];


int	heartBitCount;
unsigned long	timer_save;
int	ExitTimer;

Servo EntryGateServo;
Servo ExitGateServo;
long ProximityVal(int);

typedef	struct {	/* optimize control bit-field definition */
	unsigned char	deviceType; //0 stall led, 1 exit led, 2 entry led, 3 exit servo, 4 entry servo
	unsigned char	state; //1 ON, 0 OFF
	unsigned char	oldState; //1 ON, 0 OFF
} SP_NON_FB_DEVICE;
SP_NON_FB_DEVICE sp_non_fb_devices[10];

typedef	struct {	/* optimize control bit-field definition */
	unsigned char	deviceType; //0 stall sensor, 1 exit sensor, 2 entry sensor
	unsigned char	state; //1 : gate broken, stall occupied, 0 : gate not broken, stall not occupied
	unsigned char	oldState; //1 : gate broken, stall occupied, 0 : gate not broken, stall not occupied
} SP_FB_DEVICE;
SP_FB_DEVICE sp_fb_devices[10];

//typedef	struct {	/* optimize control bit-field definition */
//	unsigned char	s0;
//	unsigned char	s1;
//	unsigned char	s2;
//	unsigned char	s3;
//	unsigned char	s4;
//	unsigned char	s5;
//	unsigned char	s6;
//	unsigned char	s7;
//} SP_STALL_NUM;
//SP_STALL_NUM sp_stall_num;
//sp_fb_devices[0].state

typedef	struct {	/* optimize control bit-field definition */
	unsigned char	s[5];
} SP_STALL_STATE;
SP_STALL_STATE sp_stall_state;     
SP_STALL_STATE sp_stall_state_old;     

typedef	struct {	/* optimize control bit-field definition */
	unsigned char	entry;
	unsigned char	exit;
} SP_GATE_STATE;
SP_GATE_STATE sp_gate_state;

typedef	struct {	/* optimize control bit-field definition */
	unsigned char	s0; //ON(1), OFF(0)
	unsigned char	s1;
	unsigned char	s2;
	unsigned char	s3;
	unsigned char	s4;
} SP_STALL_LED;
SP_STALL_LED sp_stall_led;

long StallSensorValAvg[4]={0,};
char f_stallStateChange = 0; 
char f_stallStateChange_save = 0;
unsigned char ParkingState = 0;
char stallNum = 0;

void setup() 
{ 
int delayvalue = 1000;
  pinMode(EntryGateGreenLED, OUTPUT);    // This section makes all the LED pins outputs.
  pinMode(EntryGateRedLED, OUTPUT);
  pinMode(ExitGateGreenLED, OUTPUT);
  pinMode(ExitGateRedLED, OUTPUT);
  pinMode(ParkingStall1LED, OUTPUT);
  pinMode(ParkingStall2LED, OUTPUT);
  pinMode(ParkingStall3LED, OUTPUT);
  pinMode(ParkingStall4LED, OUTPUT);

  digitalWrite(EntryGateGreenLED, HIGH);  // The gate LEDs are turned off by setting their pins
  digitalWrite(EntryGateRedLED, HIGH);    // high. The reason for this is that they are
  digitalWrite(ExitGateGreenLED, HIGH);   // 3 color LEDs with a common annode (+). So setting
  digitalWrite(ExitGateRedLED, HIGH);     // any of the other 3 legs low turns on the LED.
  
  digitalWrite(ParkingStall1LED, LOW);    // Standard LEDs are used for the parking stall
  digitalWrite(ParkingStall2LED, LOW);    // LEDs. Set the pin high and they light.
  digitalWrite(ParkingStall3LED, LOW);
  digitalWrite(ParkingStall4LED, LOW);

/*=================================================================*/
  InitEntryExitLEDs();   // You have to do this to turn off the 
                         // entry LEDs
                         
  pinMode(EntryBeamRcvr, INPUT);     // Make entry IR rcvr an input
  digitalWrite(EntryBeamRcvr, HIGH); // enable the built-in pullup

  pinMode(ExitBeamRcvr, INPUT);      // Make exit IR rcvr an input
  digitalWrite(ExitBeamRcvr, HIGH);  // enable the built-in pullup
  
/*=================================================================*/
  
  // Map servo to pin
  EntryGateServo.attach(EntryGateServoPin);
  ExitGateServo.attach(ExitGateServoPin);
 /*=================================================================*/
  Serial.println( "Close Both Gates" );  //Here we close both gates
  EntryGateServo.write(Close); 
  ExitGateServo.write(Close);  
  delay( delayvalue );    
  StallSensorOffset();   
  StallSensorValOffset[0] = 216;
  StallSensorValOffset[1] = 151;
  StallSensorValOffset[2] = 129;
  StallSensorValOffset[3] = 72; 
    // Debug terminal
  Serial.begin(9600);
  
   // Attempt to connect to Wifi network.
   while ( status != WL_CONNECTED) 
   { 
     Serial.print("Attempting to connect to SSID: ");
     Serial.println(ssid);
     status = WiFi.begin(ssid);
     //status = WiFi.begin(ssid, wifiPass);
   }  
   
   // Print the basic connection and network information.
   printConnectionStatus();
   
   // Start the server and print and message for the user.
   server.begin();
   Serial.println("The Server is started.");
   wdt_enable(WDTO_8S);
} 

void loop() 
{ 
	wdt_reset(); 
	//while(1){};	
	
	Comm(); 
	//heartBit();	//heartbit send every about 3sec
	//delay(100); 
	timer();
} 

/*********************************************************************
 (void) InitEntryExitLEDs()

 Parameters: None           
 
 Description:
 The entry and exit LEDs are 3 way LEDs with a common annode. This means
 that you pull the other legs low to lite the appropriate colored LED.
 The problem is that when you turn on the CPU, the pins are typically low
 meaning that the LEDs will be on. This method, simply ensures they are 
 off.
***********************************************************************/    
void InitEntryExitLEDs()
{
  int i;
  for (i=26; i<=29; i++)
  {
    pinMode(i, OUTPUT);
    digitalWrite(i, HIGH);
  }
}

    
    
char EntryCheck(void)
{
	int EntryBeamState;
	
 	EntryBeamState = digitalRead(EntryBeamRcvr);  // Here we read the state of the
                                                // entry beam.

  if (EntryBeamState == LOW)  // if EntryBeamState is LOW the beam is broken
  {   
    Serial.println("Entry beam broken");
      return 1;
  } else {
    Serial.println("Entry beam is not broken.");
    return 0;
  }
  

}

char ExitCheck(void)
{
	int ExitBeamState;
	
	 ExitBeamState = digitalRead(ExitBeamRcvr);  // Here we read the state of the
                                              // exit beam.  
  if (ExitBeamState == LOW)  // if ExitBeamState is LOW the beam is broken
  {     
    Serial.println("Exit beam broken");
    return 1;
  } else {
    Serial.println("Exit beam is not broken.");
    return 0;
  }
}

void LED_ControlEntry(void)
{
	//int delayvalue = 0;

	if(EntryCheck()==1 && sp_gate_state.entry == 1){ //detected customer!!
		Serial.println( "Turn on entry green LED" );
		digitalWrite(EntryGateGreenLED, LOW);
		//delay( delayvalue );
		digitalWrite(EntryGateRedLED, HIGH);
	}
	else
	{
		Serial.println( "Turn on entry red LED" );
		digitalWrite(EntryGateRedLED, LOW);
		//delay( delayvalue );
		digitalWrite(EntryGateGreenLED, HIGH);
	}
}

void LED_ControlExit(void)
{
	//int delayvalue = 1;
	if(ExitCheck()==1){	
		Serial.println( "Turn on exit green LED" );
		digitalWrite(ExitGateGreenLED, LOW);
		//delay( delayvalue );
		digitalWrite(ExitGateRedLED, HIGH);
	}
	else
	{
		Serial.println( "Turn on exit red LED" );
		digitalWrite(ExitGateRedLED, LOW);
		//delay( delayvalue );
		digitalWrite(ExitGateGreenLED, HIGH);
	}
}

void LED_ControlStall(void)
{
	//int delayvalue = 100;
	
	if(sp_fb_devices[0].state == 1)
	{
		Serial.println( "Turn on stall 1 LED" );
		digitalWrite(ParkingStall1LED, HIGH);
		//delay( delayvalue );
	}
	else
	{
		digitalWrite(ParkingStall1LED, LOW);
	}

	if(sp_fb_devices[1].state == 1)
	{
		Serial.println( "Turn on stall 2 LED" );
		digitalWrite(ParkingStall2LED, HIGH);
		//delay( delayvalue );
	}
	else
	{
		digitalWrite(ParkingStall2LED, LOW);
	}
	if(sp_fb_devices[2].state == 1)
	{
		Serial.println( "Turn on stall 3 LED" );
		digitalWrite(ParkingStall3LED, HIGH);
		//delay( delayvalue );
	}
	else
	{
		digitalWrite(ParkingStall3LED, LOW);
	}
	if(sp_fb_devices[3].state == 1)
	{	
		Serial.println( "Turn on stall 4 LED" );
		digitalWrite(ParkingStall4LED, HIGH);
		//delay( delayvalue );
	}
	else
	{
		digitalWrite(ParkingStall4LED, LOW);
	}
}

void StallSensor(void)
{
	int i;
	long sum[4]={0,};

	for(i=0;i<16;i++)
	{
	    StallSensorVal[0] = ProximityVal(Stall1SensorPin); //Check parking space 1    
	    StallSensorVal[1] = ProximityVal(Stall2SensorPin); //Check parking space 2
	    StallSensorVal[2] = ProximityVal(Stall3SensorPin); //Check parking space 3
	    StallSensorVal[3] = ProximityVal(Stall4SensorPin); //Check parking space 4
	    
	    sum[0] = sum[0] + StallSensorVal[0];
	    sum[1] = sum[1] + StallSensorVal[1];
	    sum[2] = sum[2] + StallSensorVal[2];
	    sum[3] = sum[3] + StallSensorVal[3];
	}
	StallSensorValAvg[0] = sum[0]>>4;
	StallSensorValAvg[1] = sum[1]>>4;
	StallSensorValAvg[2] = sum[2]>>4;
	StallSensorValAvg[3] = sum[3]>>4;
	
	Serial.print("  Stall 1 avg = ");
	Serial.print(StallSensorValAvg[0]);    
	Serial.print("  Stall 1 offset = ");
	Serial.print(StallSensorValOffset[0]);
	Serial.print("  Stall 2 avg = ");
	Serial.print(StallSensorValAvg[1]);  
	Serial.print("  Stall 2 offset = ");
	Serial.print(StallSensorValOffset[1]);  
	Serial.print("  Stall 3 avg = ");
	Serial.print(StallSensorValAvg[2]); 
	Serial.print("  Stall 3 offset = ");
	Serial.print(StallSensorValOffset[2]);   
	Serial.print("  Stall 4 avg = ");
	Serial.print(StallSensorValAvg[3]); 
	Serial.print("  Stall 4 offset = ");
	Serial.println(StallSensorValOffset[3]);  
	
	 
	if(StallSensorValOffset[0] - StallSensorValAvg[0] >= 30) 
	{
		sp_stall_state.s[0] = 1;  
		
	}
	else sp_stall_state.s[0] = 0;  
	
	if(StallSensorValOffset[1] - StallSensorValAvg[1] >= 30) 
	{
		sp_stall_state.s[1] = 1; 
	}
	else sp_stall_state.s[1] = 0;   
	
	if(StallSensorValOffset[2] - StallSensorValAvg[2] >= 30) 
	{
		sp_stall_state.s[2] = 1;  
	}
	else sp_stall_state.s[2] = 0;  
	
	if(StallSensorValOffset[3] - StallSensorValAvg[3] >= 13) 
	{
		sp_stall_state.s[3] = 1;  
	}
	else sp_stall_state.s[3] = 0;      
		
	if(((sp_stall_state_old.s[0] != sp_stall_state.s[0])&& stallNum >= 1) || ((sp_stall_state_old.s[1] != sp_stall_state.s[1])&& stallNum >= 2) || ((sp_stall_state_old.s[2] != sp_stall_state.s[2])&& stallNum >= 3) || ((sp_stall_state_old.s[3] != sp_stall_state.s[3])&& stallNum >= 4))
	{
		f_stallStateChange = 1;
		sp_fb_devices[0].state = 0; //led
		sp_fb_devices[1].state = 0;
		sp_fb_devices[2].state = 0;
		sp_fb_devices[3].state = 0;
		sp_gate_state.entry = 0;
	}  
	sp_stall_state_old = sp_stall_state;
}


void StallSensorOffset(void)
{
	int i;
	long sum[4]={0,};
	for(i=0;i<50;i++)
	{
		StallSensorVal[0] = ProximityVal(Stall1SensorPin); //Check parking space 1    
	    StallSensorVal[1] = ProximityVal(Stall2SensorPin); //Check parking space 2
	    StallSensorVal[2] = ProximityVal(Stall3SensorPin); //Check parking space 3
	    StallSensorVal[3] = ProximityVal(Stall4SensorPin); //Check parking space 4
	    
	    sum[0] = sum[0] + StallSensorVal[0];
	    sum[1] = sum[1] + StallSensorVal[1];
	    sum[2] = sum[2] + StallSensorVal[2];
	    sum[3] = sum[3] + StallSensorVal[3];
	}
	StallSensorValOffset[0] = sum[0]/50;
	StallSensorValOffset[1] = sum[1]/50;
	StallSensorValOffset[2] = sum[2]/50;
	StallSensorValOffset[3] = sum[3]/50;
	Serial.print("  Stall 1 offset = ");
    Serial.print(StallSensorValOffset[0]);
    Serial.print("  Stall 2 offset = ");
    Serial.print(StallSensorValOffset[1]);
    Serial.print("  Stall 3 offset = ");
    Serial.print(StallSensorValOffset[2]);
    Serial.print("  Stall 4 offset = ");
    Serial.println(StallSensorValOffset[3]);
    
    StallSensorValAvg[0]=StallSensorValOffset[0];
    StallSensorValAvg[1]=StallSensorValOffset[1];
    StallSensorValAvg[2]=StallSensorValOffset[2];
    StallSensorValAvg[3]=StallSensorValOffset[3];
}

void EntryServo(void)
{
	static int delayvalue = 0;
	if(EntryCheck()==1 && sp_gate_state.entry == 1){
		
		Serial.println( "Open Entry Gate" );   //Here we open the entry gate
		EntryGateServo.write(Open);
		//delayvalue = 1000;
		//delay( delayvalue );
	}
	else if(f_stallStateChange == 1)
	{
		//delay( delayvalue );
		//if(delayvalue == 1000)	sp_gate_state.entry = 0;
		//delayvalue = 0;
		Serial.println( "Close Entry Gate" );  //Here we close the entry gate
		EntryGateServo.write(Close);		
	}
	else
	{
		
	}
}

void ExitServo(void)
{
	static char f_closeAfterOpen = 0;
	//static int ExitTimer = 0;
	if(ExitCheck()==1)	
	{
		Serial.println( "Open Exit Gate" );    //Here we open the exit gate
		ExitGateServo.write(Open);
		//delay( delayvalue );
		f_closeAfterOpen = 1;
		EntryGateServo.write(Close);
		sp_gate_state.entry = 0;
		ExitTimer = 0;
	}
	else
	{ 
		//Serial.println( "*************************" );
		//Serial.println( f_closeAfterOpen+'0');    //Here we open the exit gate
		//Serial.println( f_stallStateChange_save+'0');    //Here we open the exit gate
		//Serial.println( stallNum+'0');    //Here we open the exit gate
		
		//Serial.println( "*************************" );
		
			//delay( delayvalue ); 
			if(f_closeAfterOpen == 1)
			{
				if(f_stallStateChange_save == 1)
				{
					ParkingState = 1; //normal parking
				}	
				else
				{
					ParkingState = 2; //bypass
				}
				f_stallStateChange_save = 0;
				f_closeAfterOpen = 0;
				
			}
		if(ExitTimer >= 3)
		{
			Serial.println( "Close Exit Gate" );   //Here we close the exit gate
			ExitGateServo.write(Close);
			//delay( delayvalue );
		}
		//ExitTimer++;
	}
}

void Comm(void)
{
	// Wait for a new client:
	WiFiClient client = server.available();
	static char byteCount = 0;
	static char byte6th = 0;
	static char readEnable = 0;
	
	//char head[9]="$0001S";
	char temp[12]={0,};
	static char firstConnect = 0;

	unsigned char clientStringCount;
	DevieControl();
	Observer();
   // When the client connects we send them a couple of messages.
   if (client) 
   {
     // Clear the input buffer.
     client.flush();
 
     // We first write a debug message to the terminal then send
     // the data to the client.
     Serial.println("Sending data to client...");
     //client.println("Hello there client!"); 
     //client.println("I am an Arduio Server!"); 
     
     // Now we switch to read mode...
     Serial.println("Reading data from client..."); 
     
     char inChar = ' ';      
     /*inChar = client.read();
     while ( inChar != -1 && inChar != '\n')
     {
       // Checks to see if data is available from the client
       if (client.available())     
       {
         // We read a character then we write on to the terminal
         inChar = client.read();   
         Serial.println(inChar);
         //Serial.println("");
       }
     }*/
    //clientStringCount = client.available();
	if (client.available())    //while( client.available())
	{
		while( true )
	    {
			if(readEnable == 0)
			{
				// We read a character then we write on to the terminal
				inChar = client.read(); // if value is null return -1
				//Serial.write(inChar);
				if(inChar == '$')	
				{
					readEnable = 1;
					byteCount++;
					//Serial.println(inChar);  
				}
			}
			if(readEnable == 1) //receive
			{
				inChar = client.read();
				if(firstConnect == 0){
					if(byteCount == 1) spID[1] = inChar;
					if(byteCount == 2) spID[2] = inChar;
					if(byteCount == 3) spID[3] = inChar;
					if(byteCount == 4) 
					{
						firstConnect = 1;
						spID[4] = inChar;
					}
				}
				
				if(byteCount == 5)
				{
					byte6th = inChar; // I, L, G, S
					Serial.println("============Command==================");
					Serial.print("Command : ");
					Serial.write(inChar);
					Serial.println("");
					Serial.println("============Command==================");
	
					Serial.println("");
				}
			    if(byteCount == 6)
				{
			       switch (byte6th)
			       {
			       	case 'I':
			       		Serial.println("==============================");                    
			  			Serial.println("Received : Information"); 		  			
			  			Serial.print(spID);
			  			Serial.print('S');
			  			Serial.println(inChar);
			  			Serial.println("=============================="); 
			  			stallNum = inChar - '0';
			  			
			  			strcpy(temp,spID); //initial stall state
			        	temp[5] = 'S';
			        	temp[6] = '0'+sp_stall_state.s[0];
			        	temp[7] = '0'+sp_stall_state.s[1];
			        	temp[8] = '0'+sp_stall_state.s[2];
			        	temp[9] = '0'+sp_stall_state.s[3];
			        	temp[10] = '\n';
			        	client.println(temp);
			        	
			        	break; 
			        
			        case 'L':    
			        	if(inChar - '0' == 0)	sp_fb_devices[0].state = 1;
			        	if(inChar - '0' == 1)	sp_fb_devices[1].state = 1;
			        	if(inChar - '0' == 2)	sp_fb_devices[2].state = 1;
			        	if(inChar - '0' == 3)	sp_fb_devices[3].state = 1;
			        	Serial.println("==============================");
			        	Serial.println("Received Stall LED Command");
			        	Serial.println("==============================");
			        	Serial.println(sp_fb_devices[0].state);
			        	Serial.println(sp_fb_devices[1].state);
			        	Serial.println(sp_fb_devices[2].state);
			        	Serial.println(sp_fb_devices[3].state);
			       		break; 
			        
			        case 'E': //Entry Gate Commend
			        	Serial.println("==============================");
			        	if(inChar - '0' == 1)	sp_gate_state.entry = 1;
			        	if(sp_gate_state.entry == 1) 	Serial.println("Received : Entry Gate Open");
						Serial.println("==============================");
			        	break;
			        	
			        case 'S':
			        	Serial.println("=============================="); 
			        	//client.println("$0001S0001\n"); 
			        	Serial.println("Sent : stall info");
			        	//Serial.print("$0001S");
			        	Serial.print(sp_stall_state.s[0]);
			        	Serial.print(sp_stall_state.s[1]);
			        	Serial.print(sp_stall_state.s[2]);
			        	Serial.println(sp_stall_state.s[3]);
			        	strcpy(temp,spID);
			        	temp[5] = 'S';
			        	temp[6] = '0'+sp_stall_state.s[0];
			        	temp[7] = '0'+sp_stall_state.s[1];
			        	temp[8] = '0'+sp_stall_state.s[2];
			        	temp[9] = '0'+sp_stall_state.s[3];
			        	temp[10] = '\n';
			        	client.println(temp);
			        	Serial.println(temp); 
			        	Serial.println("==============================");
			        	break;
			        
			        default :
			        	break;
					}
				}
				//Serial.println("");
		     	byteCount++;
		     	//delay( 1000 );
		    }
		    //if(byteCount >= 9 || inChar == '\n'){
	     	if(inChar == '\n'){
				readEnable = 0;
				byteCount = 0;
				inChar = ' ';
			     Serial.println("");  
			     Serial.println("Done!");
			     Serial.println(".....................");
			     break;   
			}
		}
     	
    }    

	//if(ExitCheck() == 1) // broken
	if(ParkingState > 0)
	{
		strcpy(temp,spID);
		temp[5] = 'X';
		if(ParkingState == 1)	
		{
			temp[6] = '1'; //exitgate OPEN 0 close, 1 open, 2 bypass open
		}
		else	temp[6] = '2';
		temp[7] = '0';
		temp[8] = '0';
		temp[9] = '0';
		temp[10] = '0';	
		
		client.println(temp);
		Serial.println("===========Gatechange===================");
		Serial.println(temp); 
		Serial.println("===========Gatechange===================");
		
		temp[5] = 'S';
		temp[6] = '0'+sp_stall_state.s[0];
		temp[7] = '0'+sp_stall_state.s[1];
		temp[8] = '0'+sp_stall_state.s[2];
		temp[9] = '0'+sp_stall_state.s[3];
		temp[10] = '\n';		
		
		client.println(temp);
		Serial.println("===========statechange or heartbit===================");
		Serial.println(temp); 
		Serial.println("===========statechange or heartbit===================");
		
		sp_fb_devices[0].state = 0; //led
		sp_fb_devices[1].state = 0;
		sp_fb_devices[2].state = 0;
		sp_fb_devices[3].state = 0;
		ParkingState = 0;
		heartBitCount = 0;
	}
	if(f_stallStateChange == 1 || heartBitCount >= 8)//heartbit send every about 10sec & send data
	{
		//client.flush();
		//Serial.println(temp);
		strcpy(temp,spID);
		//Serial.println(temp); 
		if(f_stallStateChange == 1)
		{
			temp[5] = 'S';
			temp[6] = '0'+sp_stall_state.s[0];
			temp[7] = '0'+sp_stall_state.s[1];
			temp[8] = '0'+sp_stall_state.s[2];
			temp[9] = '0'+sp_stall_state.s[3];
			temp[10] = '\n';
			EntryServo();
			f_stallStateChange_save = f_stallStateChange;
			f_stallStateChange = 0;			
		}
		else
		{
			temp[5] = '\n';
			temp[6] = 0;
			temp[7] = 0;
			temp[8] = 0;
			temp[9] = 0;
			temp[10] = 0;
		}

		client.println(temp);
		Serial.println("===========statechange or heartbit===================");
		Serial.println(temp); 
		Serial.println("===========statechange or heartbit===================");
		//Serial.println(heartBitCount); 
		heartBitCount = 0;
	}
	
   } // if we are connected
}
/*char* ParkingState(void)
{
	char temp[12];
	
}*/
void DevieControl(void)
{
	LED_ControlEntry();
	LED_ControlExit();
	LED_ControlStall();	
	EntryServo();
	ExitServo();    
}

void Observer(void)
{
	StallSensor();
}
/*void heartBit(void)
{
	heartBitCount++;
	if(heartBitCount>=100)	heartBitCount = 0;
	
	//ExitTimer++;
	//if(ExitTimer>=100)	ExitTimer = 0;
	
}*/


void timer(void)
{
	unsigned long tempTime;

	tempTime = millis();
	//Serial.println(tempTime);
	//Serial.println(timer_save);
	if(tempTime - timer_save >= 10)
	{
   		timer_10ms((tempTime - timer_save)/10);
	}
   	timer_save = tempTime;
   //prints time since program started   
}

void	timer_10ms(unsigned long temp_10msTime)
{
	static unsigned long ms10Timer;
	ms10Timer = ms10Timer + temp_10msTime;
	if(ms10Timer >= 100)
	{
		ms10Timer = 0;
		timer_1second();
	}
}

void	timer_1second(void)
{
	//static unsigned long time1sec;
	//time1sec++;
	//if(time1sec >= 100000) time1sec = 100000;
	
	heartBitCount++;
	if(heartBitCount >= 1000) heartBitCount = 1000;
	
	ExitTimer++;
	if(ExitTimer >= 1000) ExitTimer = 1000;
	
	//Serial.print("Time: ");
	//Serial.println(time1sec);
}

/*********************************************************************
 (long) ProximityVal(int Pin)
 Parameters:            
 int pin - the pin on the Arduino where the QTI sensor is connected.

 Description:
 QTI schematics and specs: http://www.parallax.com/product/555-27401
 This method initalizes the QTI sensor pin as output and charges the
 capacitor on the QTI. The QTI emits IR light which is reflected off 
 of any surface in front of the sensor. The amount of IR light 
 reflected back is detected by the IR resistor on the QTI. This is 
 the resistor that the capacitor discharges through. The amount of 
 time it takes to discharge determines how much light, and therefore 
 the lightness or darkness of the material in front of the QTI sensor.
 Given the closeness of the object in this application you will get
 0 if the sensor is covered
***********************************************************************/
long ProximityVal(int Pin)
{
    long duration = 0;
    pinMode(Pin, OUTPUT);         // Sets pin as OUTPUT
    digitalWrite(Pin, HIGH);      // Pin HIGH
    delay(1);                     // Wait for the capacitor to stabilize

    pinMode(Pin, INPUT);          // Sets pin as INPUT
    digitalWrite(Pin, LOW);       // Pin LOW
    while(digitalRead(Pin))       // Count until the pin goes
    {                             // LOW (cap discharges)
       duration++;                
    }   
    return duration;              // Returns the duration of the pulse
}

/***************************************************************
* The following method prints out the connection information
****************************************************************/

 void printConnectionStatus() 
 {
     // Print the basic connection and network information: 
     // Network, IP, and Subnet mask
     ip = WiFi.localIP();
     Serial.print("Connected to ");
     Serial.print(ssid);
     Serial.print(" IP Address:: ");
     Serial.println(ip);
     subnet = WiFi.subnetMask();
     Serial.print("Netmask: ");
     Serial.println(subnet);
   
     // Print our MAC address.
     WiFi.macAddress(mac);
     Serial.print("WiFi Shield MAC address: ");
     Serial.print(mac[5],HEX);
     Serial.print(":");
     Serial.print(mac[4],HEX);
     Serial.print(":");
     Serial.print(mac[3],HEX);
     Serial.print(":");
     Serial.print(mac[2],HEX);
     Serial.print(":");
     Serial.print(mac[1],HEX);
     Serial.print(":");
     Serial.println(mac[0],HEX);
   
     // Print the wireless signal strength:
     rssi = WiFi.RSSI();
     Serial.print("Signal strength (RSSI): ");
     Serial.print(rssi);
     Serial.println(" dBm");

 } // printConnectionStatus
