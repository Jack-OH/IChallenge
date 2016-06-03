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

#include <Servo.h> 

#define Open  90
#define Close 0

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
int EntryBeamState;
int ExitBeamState;

Servo EntryGateServo;
Servo ExitGateServo;

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
    // Debug terminal
  Serial.begin(9600);
} 

void loop() 
{ 
    
	//EntryCheck();
	ExitCheck();
	//LED_ControlEntry();
	//LED_ControlExit();
	//LED_ControlStall();
	//StallSensor();
	//EntryServo();
	//ExitServo();     
	delay(1000); 
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
  } else {
    Serial.println("Entry beam is not broken.");
  }
  
  return 1;
}

char ExitCheck(void)
{
	 ExitBeamState = digitalRead(ExitBeamRcvr);  // Here we read the state of the
                                              // exit beam.  
  if (ExitBeamState == LOW)  // if ExitBeamState is LOW the beam is broken
  {     
    Serial.println("Exit beam broken");
  } else {
    Serial.println("Exit beam is not broken.");
  }
	
	return 1;
}

void LED_ControlEntry(void)
{
	int delayvalue = 100;
	
	Serial.println( "Turn on entry red LED" );
  digitalWrite(EntryGateRedLED, LOW);
  delay( delayvalue );
  digitalWrite(EntryGateRedLED, HIGH);

  Serial.println( "Turn on entry green LED" );
  digitalWrite(EntryGateGreenLED, LOW);
  delay( delayvalue );
  digitalWrite(EntryGateGreenLED, HIGH);
}

void LED_ControlExit(void)
{
	int delayvalue = 100;
	
	Serial.println( "Turn on exit red LED" );
  digitalWrite(ExitGateRedLED, LOW);
  delay( delayvalue );
  digitalWrite(ExitGateRedLED, HIGH);

  Serial.println( "Turn on exit green LED" );
  digitalWrite(ExitGateGreenLED, LOW);
  delay( delayvalue );
  digitalWrite(ExitGateGreenLED, HIGH);
}

void LED_ControlStall(void)
{
	int delayvalue = 100;
	
	Serial.println( "Turn on stall 1 LED" );
  digitalWrite(ParkingStall1LED, HIGH);
  delay( delayvalue );
  digitalWrite(ParkingStall1LED, LOW);

  Serial.println( "Turn on stall 2 LED" );
  digitalWrite(ParkingStall2LED, HIGH);
  delay( delayvalue );
  digitalWrite(ParkingStall2LED, LOW);
  
  Serial.println( "Turn on stall 3 LED" );
  digitalWrite(ParkingStall3LED, HIGH);
  delay( delayvalue );
  digitalWrite(ParkingStall3LED, LOW);
  
  Serial.println( "Turn on stall 4 LED" );
  digitalWrite(ParkingStall4LED, HIGH);
  delay( delayvalue );
  digitalWrite(ParkingStall4LED, LOW);
}

int StallSensor(void)
{
	 Stall1SensorVal = ProximityVal(Stall1SensorPin); //Check parking space 1
    Serial.print("  Stall 1 = ");
    Serial.print(Stall1SensorVal);
    
    Stall2SensorVal = ProximityVal(Stall2SensorPin); //Check parking space 2
    Serial.print("  Stall 2 = ");
    Serial.print(Stall2SensorVal);

    Stall3SensorVal = ProximityVal(Stall3SensorPin); //Check parking space 3
    Serial.print("  Stall 3 = ");
    Serial.print(Stall3SensorVal);

    Stall4SensorVal =  ProximityVal(Stall4SensorPin); //Check parking space 4
    Serial.print("  Stall 4 = ");
    Serial.println(Stall4SensorVal);
    
	return 1;
}

void EntryServo(void)
{
	int delayvalue = 1000;
	
	Serial.println( "Open Entry Gate" );   //Here we open the entry gate
  EntryGateServo.write(Open);
  delay( delayvalue );

  Serial.println( "Close Entry Gate" );  //Here we close the entry gate
  EntryGateServo.write(Close);
  delay( delayvalue );
}

void ExitServo(void)
{
	int delayvalue = 1000;
	
	Serial.println( "Open Exit Gate" );    //Here we open the exit gate
  ExitGateServo.write(Open);
  delay( delayvalue );
  
  Serial.println( "Close Exit Gate" );   //Here we close the exit gate
  ExitGateServo.write(Close);
  delay( delayvalue );
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
