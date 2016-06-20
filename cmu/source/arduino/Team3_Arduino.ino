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

#define Open  90
#define Close 0
#define PORTID 5001             // IP socket port#

char ssid[] = "LGArchi";           // The network SSID for CMU unsecure network
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


long  Stall1SensorVal;
long  Stall2SensorVal;
long  Stall3SensorVal;
long  Stall4SensorVal;
long  Stall1SensorValOffset;
long  Stall2SensorValOffset;
long  Stall3SensorValOffset;
long  Stall4SensorValOffset;
int EntryBeamState;
int ExitBeamState;
int	heartBitCount;
//int	ExitTimer;

Servo EntryGateServo;
Servo ExitGateServo;
long ProximityVal(int);
typedef	struct {	/* optimize control bit-field definition */
	unsigned char	s0;
	unsigned char	s1;
	unsigned char	s2;
	unsigned char	s3;
	unsigned char	s4;
	unsigned char	s5;
	unsigned char	s6;
	unsigned char	s7;
} SP_STALL_NUM;
SP_STALL_NUM sp_stall_num;

typedef	struct {	/* optimize control bit-field definition */
	unsigned char	s0;
	unsigned char	s1;
	unsigned char	s2;
	unsigned char	s3;
	unsigned char	s4;
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
  Stall1SensorValOffset = 216;
  Stall2SensorValOffset = 151;
  Stall3SensorValOffset = 129;
  Stall4SensorValOffset = 72; 
    // Debug terminal
  Serial.begin(9600);
  
   // Attempt to connect to Wifi network.
   while ( status != WL_CONNECTED) 
   { 
     Serial.print("Attempting to connect to SSID: ");
     Serial.println(ssid);
     status = WiFi.begin(ssid);
   }  
   
   // Print the basic connection and network information.
   printConnectionStatus();
   
   // Start the server and print and message for the user.
   server.begin();
   Serial.println("The Server is started.");
} 

void loop() 
{ 
	LED_ControlEntry();
	LED_ControlExit();
	LED_ControlStall();
	StallSensor();
	EntryServo();
	ExitServo();    
	Comm(); 
	heartBit();	//heartbit send every about 3sec
	//delay(100); 
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
	
	if(sp_stall_num.s1 == 1)
	{
		Serial.println( "Turn on stall 1 LED" );
		digitalWrite(ParkingStall1LED, HIGH);
		//delay( delayvalue );
	}
	else
	{
		digitalWrite(ParkingStall1LED, LOW);
	}

	if(sp_stall_num.s2 == 1)
	{
		Serial.println( "Turn on stall 2 LED" );
		digitalWrite(ParkingStall2LED, HIGH);
		//delay( delayvalue );
	}
	else
	{
		digitalWrite(ParkingStall2LED, LOW);
	}
	if(sp_stall_num.s3 == 1)
	{
		Serial.println( "Turn on stall 3 LED" );
		digitalWrite(ParkingStall3LED, HIGH);
		//delay( delayvalue );
	}
	else
	{
		digitalWrite(ParkingStall3LED, LOW);
	}
	if(sp_stall_num.s4 == 1)
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
		Stall1SensorVal = ProximityVal(Stall1SensorPin); //Check parking space 1   	    
	    Stall2SensorVal = ProximityVal(Stall2SensorPin); //Check parking space 2	
	    Stall3SensorVal = ProximityVal(Stall3SensorPin); //Check parking space 3	
	    Stall4SensorVal =  ProximityVal(Stall4SensorPin); //Check parking space 4
	    	    
	    sum[0] = sum[0] + Stall1SensorVal;
	    sum[1] = sum[1] + Stall2SensorVal;
	    sum[2] = sum[2] + Stall3SensorVal;
	    sum[3] = sum[3] + Stall4SensorVal;
	}
	StallSensorValAvg[0] = sum[0]>>4;
	StallSensorValAvg[1] = sum[1]>>4;
	StallSensorValAvg[2] = sum[2]>>4;
	StallSensorValAvg[3] = sum[3]>>4;
	
	Serial.print("  Stall 1 avg = ");
	Serial.print(StallSensorValAvg[0]);    
	Serial.print("  Stall 1 offset = ");
	Serial.print(Stall1SensorValOffset);
	Serial.print("  Stall 2 avg = ");
	Serial.print(StallSensorValAvg[1]);  
	Serial.print("  Stall 2 offset = ");
	Serial.print(Stall2SensorValOffset);  
	Serial.print("  Stall 3 avg = ");
	Serial.print(StallSensorValAvg[2]); 
	Serial.print("  Stall 3 offset = ");
	Serial.print(Stall3SensorValOffset);   
	Serial.print("  Stall 4 avg = ");
	Serial.print(StallSensorValAvg[3]); 
	Serial.print("  Stall 4 offset = ");
	Serial.println(Stall4SensorValOffset);  
	 
	if(Stall1SensorValOffset - StallSensorValAvg[0] >= 30) 
	{
		sp_stall_state.s1 = 1;  
		
	}
	else sp_stall_state.s1 = 0;  
	
	if(Stall2SensorValOffset - StallSensorValAvg[1] >= 30) 
	{
		sp_stall_state.s2 = 1; 
	}
	else sp_stall_state.s2 = 0;   
	
	if(Stall3SensorValOffset - StallSensorValAvg[2] >= 30) 
	{
		sp_stall_state.s3 = 1;  
	}
	else sp_stall_state.s3 = 0;  
	
	if(Stall4SensorValOffset - StallSensorValAvg[3] >= 13) 
	{
		sp_stall_state.s4 = 1;  
	}
	else sp_stall_state.s4 = 0;      
	
	
	if(sp_stall_state_old.s1 != sp_stall_state.s1 || sp_stall_state_old.s2 != sp_stall_state.s2 || sp_stall_state_old.s3 != sp_stall_state.s3 || sp_stall_state_old.s4 != sp_stall_state.s4)
	{
		f_stallStateChange = 1;
		sp_stall_num.s1 = 0; //led
		sp_stall_num.s2 = 0;
		sp_stall_num.s3 = 0;
		sp_stall_num.s4 = 0;
	}  
	sp_stall_state_old = sp_stall_state;
}


void StallSensorOffset(void)
{
	int i;
	long sum[4]={0,};
	for(i=0;i<50;i++)
	{
		Stall1SensorVal = ProximityVal(Stall1SensorPin); //Check parking space 1    
	    Stall2SensorVal = ProximityVal(Stall2SensorPin); //Check parking space 2
	    Stall3SensorVal = ProximityVal(Stall3SensorPin); //Check parking space 3
	    Stall4SensorVal =  ProximityVal(Stall4SensorPin); //Check parking space 4
	    
	    sum[0] = sum[0] + Stall1SensorVal;
	    sum[1] = sum[1] + Stall2SensorVal;
	    sum[2] = sum[2] + Stall3SensorVal;
	    sum[3] = sum[3] + Stall4SensorVal;
	}
	Stall1SensorValOffset = sum[0]/50;
	Stall2SensorValOffset = sum[1]/50;
	Stall3SensorValOffset = sum[2]/50;
	Stall4SensorValOffset = sum[3]/50;
	Serial.print("  Stall 1 offset = ");
    Serial.print(Stall1SensorValOffset);
    Serial.print("  Stall 2 offset = ");
    Serial.print(Stall2SensorValOffset);
    Serial.print("  Stall 3 offset = ");
    Serial.print(Stall3SensorValOffset);
    Serial.print("  Stall 4 offset = ");
    Serial.println(Stall4SensorValOffset);
    
    StallSensorValAvg[0]=Stall1SensorValOffset;
    StallSensorValAvg[1]=Stall2SensorValOffset;
    StallSensorValAvg[2]=Stall3SensorValOffset;
    StallSensorValAvg[3]=Stall4SensorValOffset;
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
	static int ExitTimer = 0;
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
		if(ExitTimer >= 5)
		{
			//delay( delayvalue ); 
			if(f_closeAfterOpen == 1)	f_stallStateChange_save = 0;
			f_closeAfterOpen = 0;
			Serial.println( "Close Exit Gate" );   //Here we close the exit gate
			ExitGateServo.write(Close);
			//delay( delayvalue );
		}
		ExitTimer++;
	}
}

void Comm(void)
{
	// Wait for a new client:
	WiFiClient client = server.available();
	static char byteCount = 0;
	static char byte6th = 0;
	static char readEnable = 0;
	static char spID[6] = {'$','0','0','0','0'};
	//char head[9]="$0001S";
	char temp[12]={0,};
	static char firstConnect = 0;
	static char stallNum;
	unsigned char clientStringCount;
	
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
			        	temp[6] = '0'+sp_stall_state.s1;
			        	temp[7] = '0'+sp_stall_state.s2;
			        	temp[8] = '0'+sp_stall_state.s3;
			        	temp[9] = '0'+sp_stall_state.s4;
			        	temp[10] = '\n';
			        	client.println(temp);
			        	
			        	break; 
			        
			        case 'L':    
			        	if(inChar - '0' == 0)	sp_stall_num.s1 = 1;
			        	if(inChar - '0' == 1)	sp_stall_num.s2 = 1;
			        	if(inChar - '0' == 2)	sp_stall_num.s3 = 1;
			        	if(inChar - '0' == 3)	sp_stall_num.s4 = 1;
			        	Serial.println("==============================");
			        	Serial.println("Received Stall LED Command");
			        	Serial.println("==============================");
			        	Serial.println(sp_stall_num.s1);
			        	Serial.println(sp_stall_num.s2);
			        	Serial.println(sp_stall_num.s3);
			        	Serial.println(sp_stall_num.s4);
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
			        	Serial.print(sp_stall_state.s1);
			        	Serial.print(sp_stall_state.s2);
			        	Serial.print(sp_stall_state.s3);
			        	Serial.println(sp_stall_state.s4);
			        	strcpy(temp,spID);
			        	temp[5] = 'S';
			        	temp[6] = '0'+sp_stall_state.s1;
			        	temp[7] = '0'+sp_stall_state.s2;
			        	temp[8] = '0'+sp_stall_state.s3;
			        	temp[9] = '0'+sp_stall_state.s4;
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

	if(ExitCheck() == 1) // broken
	{
		strcpy(temp,spID);
		temp[5] = 'X';
		if(f_stallStateChange_save == 1)	
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
		temp[6] = '0'+sp_stall_state.s1;
		temp[7] = '0'+sp_stall_state.s2;
		temp[8] = '0'+sp_stall_state.s3;
		temp[9] = '0'+sp_stall_state.s4;
		temp[10] = '\n';		
		
		client.println(temp);
		Serial.println("===========statechange or heartbit===================");
		Serial.println(temp); 
		Serial.println("===========statechange or heartbit===================");
		
		sp_stall_num.s1 = 0; //led
		sp_stall_num.s2 = 0;
		sp_stall_num.s3 = 0;
		sp_stall_num.s4 = 0;
	}
	if(f_stallStateChange == 1 || heartBitCount >= 17)//heartbit send every about 10sec & send data
	{
		//client.flush();
		//Serial.println(temp);
		strcpy(temp,spID);
		//Serial.println(temp); 
		if(f_stallStateChange == 1)
		{
			temp[5] = 'S';
			temp[6] = '0'+sp_stall_state.s1;
			temp[7] = '0'+sp_stall_state.s2;
			temp[8] = '0'+sp_stall_state.s3;
			temp[9] = '0'+sp_stall_state.s4;
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

void heartBit(void)
{
	heartBitCount++;
	if(heartBitCount>=100)	heartBitCount = 0;
	
	//ExitTimer++;
	//if(ExitTimer>=100)	ExitTimer = 0;
	
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
