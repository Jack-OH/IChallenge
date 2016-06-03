#include <SPI.h>
#include <WiFi.h>
#include <Servo.h> 

// -----------------------------------
// CONFIGURATION
// -----------------------------------

// WiFi
//#define WAREHOUSE_SERVER_IP_ADDR    128,237,228,7  // Set Warehouse Server IP Address (kideok.kim) CMU
//#define WAREHOUSE_SERVER_IP_ADDR    10,255,99,254  // Set Warehouse Server IP Address
//#define WAREHOUSE_SERVER_IP_ADDR    128,237,231,105  // Set Warehouse Server IP Address (dongju.kim) CMU
#define WAREHOUSE_SERVER_IP_ADDR    192,168,0,8  // Set Warehouse Server IP Address (dongju.kim) iptime
//#define WAREHOUSE_SERVER_IP_ADDR    10,255,98,100  // Set Warehouse Server IP Address (dongju.kim) ShadySide Inn


#define SERVER_PORT_ID          (9003)

//char wifiSsid[] = "CMU";
char wifiSsid[] = "iptime";
//char wifiSsid[] = "Shadyside Inn";
//char wifiPass[] = "hotel5405";

// -----------------------------------
// GLOBAL VARIABLES
// -----------------------------------
#define MSG_INVALID_MSG_ID		(0xFF)

// Message header: message id between controller and robot
#define MSG_BOT_MOVE_TO_NEXT_ST		(0x11)
#define MSG_BOT_NOTI_BOT_STATUS		(0x12)
#define MSG_BOT_NOTI_ERR		(0x13)
#define MSG_BOT_MANUAL_MODE_ON_OFF	(0x14)
#define MSG_BOT_MANUAL_MODE_CTRL	(0x15)
#define MSG_BOT_READY                   (0x16)
#define MSG_BOT_ACK_TO_READY            (0x17)
#define MSG_BOT_LINK_PING               (0x18)
#define MSG_BOT_LINK_ECHO               (0x19)

#define MANUAL_MODE_ON		(0x00)
#define MANUAL_MODE_OFF		(0x01)

#define MOVE_TO_FORWARD			(0x00)
#define MOVE_TO_SLIGHT_RIGHT_TURN	(0x01)
#define MOVE_TO_SLIGHT_LEFT_TURN	(0x02)
#define MOVE_TO_BACKWARD		(0x03)

#define ERROR_CAUSE_WIFI_DISCONECT    (0x00)
#define ERROR_CAUSE_ROBOT_FLOATING    (0x01)
#define ERROR_CAUSE_UNKNOWN           (0x10)

// -------------------
#define MAX_LEN_OUTPUT_CMD_STRING  (40)
#define MAX_LEN_OF_MSG             (20)

IPAddress     warehouseServerIp(WAREHOUSE_SERVER_IP_ADDR);
WiFiClient    warehouseClient;
//#define MAX_BYTE_NUM_OF_MAC_ID          (6)
//byte          wifiMacAddr[MAX_BYTE_NUM_OF_MAC_ID];
unsigned char msgSeqNum = 0;

unsigned char	cmdData[MAX_LEN_OF_MSG];
char            outputCmdString[MAX_LEN_OUTPUT_CMD_STRING];
char            lenString;
int             gtimecount = 0;

#define INVALID_STATION_ID        (0xFF)
unsigned char  savedWarehouseId = 0;
unsigned char  savedRobotId = 0;
unsigned char  savedStationId = 0;
unsigned char  savedManualMode = MANUAL_MODE_OFF;
unsigned char  savedManualDirMove = 0xFF;

unsigned char  prevStationId = 0xFF;

//////// 2. For Robot Movement  ///////////////
#define QTI_LEFT   A0              // 220 or 1k resistor connected to this pin
#define QTI_CENTER A1
#define QTI_RIGHT  A2
boolean qti_L;
boolean qti_C;
boolean qti_R;
long qti_L_raw = 0;
long qti_C_raw = 0;
long qti_R_raw = 0;
#define QTI_L_BW_THRESHOLD     150 //150    //////////////////////////// TUNE //////////////////////////////
#define QTI_C_BW_THRESHOLD     200 //200    //////////////////////////// TUNE //////////////////////////////
#define QTI_R_BW_THRESHOLD     200 //200    //////////////////////////// TUNE //////////////////////////////
#define QTI_FLOATING_THRESHOLD 4000
//////// 3. Servo Setup ///////////////
//----------- ROBOT MOVEMENT SET ----------------//
//----------- ALGORITHM -------------//
#define ROBOT_ALGORITHM_GO_TO_NEXT_STATION            20
#define ROBOT_ALGORITHM_TURN_LEFT_AT_STATION          21
#define ROBOT_ALGORITHM_TURN_RIGHT_AT_STATION         22
#define ROBOT_ALGORITHM_FIND_WAY1                     23
//----------- STATIC MOVEMENT -------//
#define ROBOT_STOP                                    0
#define ROBOT_GO_FORWARD                              1
#define ROBOT_GO_BACKWARD                             2
#define ROBOT_GO_SLIDE_LEFT                           3
#define ROBOT_GO_SLIDE_RIGHT                          4
#define ROBOT_TURN_LEFT                               5
#define ROBOT_TURN_RIGHT                              6
#define ROBOT_TURN_BACKWARD_LEFT                      7
#define ROBOT_TURN_BACKWARD_RIGHT                     8
// ---------- ROBOT STATUS MACHINE ----------//
#define ROBOT_PARKED_ANYWARE                          100 // unknown
#define ROBOT_STATUS_RIGHT_BEFORE_LINE                101 // L,R BLACK (FINISHED TURN)
#define ROBOT_STATUS_ON_THE_WAY_LINE                  102 // L,R sensing
#define ROBOT_STATUS_ENTER_THE_STATION                103 // R BLACK
#define ROBOT_STATUS_ARRIVED_STATION                  104 // C,R WHITE (BEFORE TURN)
#define ROBOT_STATUS_LOST_WAY                         110
#define ROBOT_STATUS_LOST_WAY_ALL_WHITE               111 // ALL WHITE
#define ROBOT_STATUS_LOST_WAY_ALL_BLACK               112 // ALL BLACK

#define ROBOT_SPEED_L                                   112//180//103//123 //103   // FULL SPEED (180,0)  //  MID SPEED(113,70)     SLOW (103,80) 
#define ROBOT_SPEED_R                                   72//0//80//61  // 80    
#define ROBOT_MOVEMENT_SLICE0                           30  // FOR Turn
#define ROBOT_MOVEMENT_SLICE1                           30//50//30 (Good)  // At the coner or before line
#define ROBOT_MOVEMENT_SLICE2                           130//150//100 (Good) // ON THE LINE Speed up!
#define LTSERVOPIN  5          // Left servo pin
#define RTSERVOPIN  6          // Right servo pin
Servo LtServo;                 // Left servo object
Servo RtServo;                 // Right servo object

//////// 4. ETC Setup ///////////////
int robot_status_machine = ROBOT_PARKED_ANYWARE;

// -----------------------------------
// FUNCTIONS START
// -----------------------------------
//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
void setup() 
{
   // Initialize serial port. This is used for debug.
   Serial.begin(9600);

   // Attempt to connect to WIfI network indicated by wifi ssid.
   WiFi_Connect();

   // Print connection information to the debug terminal
   WiFi_PrintConnectionStatus();

   // Start warehouse server connection
   Serial.println("Initial Server WiFi Connection!");
   UTIL_ConnectWarehouseServer();
   Serial.println("-------");

   // Initialize servor motor and IR sensor
   // ROBOT_Initialize();

   // Send the Ready message to Server
   WC_SendReadyMsg();
   
   ROBOT_Initialize();
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************

void loop()
{
  gtimecount ++;
  // Scheduling
  
  // Following function is wait input message for specific time
  // When timeout is occured, follwoing function is returned.
  //Serial.println("----------- Waiting Input from server ---------");
  
  WC_WaitAndProcInputMsg();
  
  
  //if(((millis()/1000) % 3) == 0)   // ping every 10 sec.
  if((gtimecount % 30) == 0)
  {
    //Serial.println("----------- Ping to Server I'm waiting ---------");
 //   if (warehouseClient.connected())
      WC_SendRobotLinkPingMsg();
    //WC_SendRobotStausMsg();
    //WC_SendRobotErrorMsg(1);
    //delay(1000);
    gtimecount = 0;
  }
  /*
  if(gtimecount >= 100000)
  {
    gtimecount = 0;
    Serial.println("---[TEST]---robot status-");
    WC_SendRobotStausMsg();
  }
  */
  //Serial.println(gtimecount);
  delay(100);
  
  //robot_mov_by_algorithm(ROBOT_ALGORITHM_GO_TO_NEXT_STATION);

// ADD_0625
    // Check client connection
    if (!warehouseClient.connected())
    {
      UTIL_ReconnectWarehouseServer();
    }
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
void WC_WaitAndProcInputMsg() 
{
  unsigned char msgId = MSG_INVALID_MSG_ID;

  if (warehouseClient.available())
  {
    msgId = UTIL_ReadCommandData();

    //Serial.print("                   0x");
    //Serial.println(msgId, HEX);
    //Serial.println(")");
    //Serial.println(savedManualMode);

    switch (msgId)
    {
      case MSG_BOT_ACK_TO_READY:
        Serial.println("<-(0x17 ACK)");
          // Need to Send ACK MSG?
      break;

      case MSG_BOT_MOVE_TO_NEXT_ST:
        Serial.println("<-(0x11 MOVE TO NEXT)");

        WC_SendAckMoveToNext();

        if (prevStationId != savedStationId)
        {  
          if(robot_mov_by_algorithm(ROBOT_ALGORITHM_GO_TO_NEXT_STATION))
          {
            WC_SendRobotStausMsg();
          }else
          { // Lost Way.
            WC_SendRobotErrorMsg(ERROR_CAUSE_ROBOT_FLOATING);
          }
        }
        else
        {
          Serial.println("ignore MOVE TO NEXT !");
        }

        prevStationId = savedStationId;
      break;

      case MSG_BOT_MANUAL_MODE_ON_OFF:
        Serial.println("<-(0x14 MANUAL ONOFF)");
        if (savedManualMode == MANUAL_MODE_OFF) {
          if(robot_mov_by_algorithm(ROBOT_ALGORITHM_FIND_WAY1))
          {
            WC_SendRobotStausMsg();
          }else
          { // Lost Way.
            WC_SendRobotErrorMsg(ERROR_CAUSE_ROBOT_FLOATING);
          }
        }
      break;

      case MSG_BOT_MANUAL_MODE_CTRL:
        Serial.println("<-(0x15 MANUAL CONTROL)");      
        if (savedManualMode == MANUAL_MODE_ON)
        {
          //Serial.print("MANUAL_MODE_ON :"); Serial.println(savedManualDirMove);
          if (savedManualDirMove == MOVE_TO_FORWARD) {

            robot_mov_by_static(ROBOT_GO_FORWARD,300);
            // TODO
          }
          else if (savedManualDirMove == MOVE_TO_SLIGHT_RIGHT_TURN) {
            robot_mov_by_static(ROBOT_TURN_RIGHT,200);
            // TODO
          }
          else if (savedManualDirMove == MOVE_TO_SLIGHT_LEFT_TURN) {
            robot_mov_by_static(ROBOT_TURN_LEFT,200);
            // TODO
          }
          else if (savedManualDirMove == MOVE_TO_BACKWARD) {
            robot_mov_by_static(ROBOT_GO_BACKWARD,300);            
            // TODO
          }
        }
      break;

      case MSG_BOT_LINK_ECHO:
        Serial.println("<-(0x19 ECHO)");      
        // Noting in this version
      break;
    }
  }
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//
//*********************************************************
unsigned char UTIL_ReadCommandData(void)
{
  unsigned char inputMsgId = MSG_INVALID_MSG_ID;
  char character = ' ';
  unsigned char cmdStringIndex = 0;
  int inputCmdStringLen;
  
//  Serial.println("Receive Msg From Warehouse Server: ");
  Serial.print("[ROBOT <- SERVER] ");
  inputCmdStringLen = 0;

  while (character != '\n')
  {
    character = warehouseClient.read();

    if (character != '\n')
    {
      Serial.print(character);

      inputCmdStringLen++;

// ADD_0625
      if (inputCmdStringLen >= (MAX_LEN_OF_MSG*2))
      {
        Serial.print("F_ERR: Input Cmd Len: ");
        Serial.println(inputCmdStringLen);
        warehouseClient.flush();

        return MSG_INVALID_MSG_ID;
      }
 
      if (cmdStringIndex == 5)  // message id
      {
        if (character == '1') {
          inputMsgId = MSG_BOT_MOVE_TO_NEXT_ST;
        }
        else if (character == '4') {
          inputMsgId = MSG_BOT_MANUAL_MODE_ON_OFF;
        }
        else if (character == '5') {
          inputMsgId = MSG_BOT_MANUAL_MODE_CTRL;
        }
        else if (character == '7') {
          inputMsgId = MSG_BOT_ACK_TO_READY;
        }
        else if (character == '9') {
          inputMsgId = MSG_BOT_LINK_ECHO;
        }
      }

      switch (inputMsgId)
      {
        case MSG_BOT_ACK_TO_READY:
          if (cmdStringIndex == 9)  // warehouse id
            savedWarehouseId = character - '0';
          else if (cmdStringIndex == 11)  // robot id
            savedRobotId = character - '0';
        break;

        case MSG_BOT_MOVE_TO_NEXT_ST:
          if (cmdStringIndex == 13)  // station id
            savedStationId = character - '0';
        break;

        case MSG_BOT_MANUAL_MODE_ON_OFF:
          if (cmdStringIndex == 13)  // manaual on/off
            savedManualMode = character - '0';
        break;
        
        case MSG_BOT_MANUAL_MODE_CTRL:
          if (cmdStringIndex == 13)  // manaual direction  (init: 0xFF)
            savedManualDirMove = character - '0';
        break;
      }

      cmdStringIndex++;
    }
    else
    {
      Serial.println("");
    }
  }

  return inputMsgId;
}
//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
void WC_SendAckMoveToNext() 
{
  char ackMove[] = "0001110400000000\n";
  char lenAckMove = 17;

  //lenString = strlen(readyMsg);
  //strcpy(outputCmdString, readyMsg);
  memcpy(outputCmdString, ackMove, lenAckMove);

  outputCmdString[9] = '0' + savedWarehouseId;
  outputCmdString[11] = '0' + savedRobotId;
  outputCmdString[13] = '0' + savedStationId;

  WC_SendMsgToServer(lenAckMove);
  Serial.println("->(0x11 ACK to Mov)");
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
void WC_SendReadyMsg() 
{
  char readyMsg[] = "000216073885010EC47800\n";
  char lenReadyMsg = 23;

  //lenString = strlen(readyMsg);
  //strcpy(outputCmdString, readyMsg);
  memcpy(outputCmdString, readyMsg, lenReadyMsg);

  WC_SendMsgToServer(lenReadyMsg);
  Serial.println("->(0x16 Ready)");
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
void WC_SendRobotStausMsg() 
{
  char robotStatusMsg[] = "0002120400000000\n";
  char lenStatusMsg = 17;

  //lenString = strlen(robotStatusMsg);
  //strcpy(outputCmdString, robotStatusMsg);
  memcpy(outputCmdString, robotStatusMsg, lenStatusMsg);

  outputCmdString[9] = '0' + savedWarehouseId;
  outputCmdString[11] = '0' + savedRobotId;
  outputCmdString[13] = '0' + savedStationId;

  WC_SendMsgToServer(lenStatusMsg);
  Serial.println("->(0x12 Robot status)");
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
void WC_SendRobotErrorMsg(unsigned char errorCause) 
{
  char robotErrorMsg[] = "0002130400000000\n";
  char lenErrorMsg = 17;

  //lenString = strlen(robotErrorMsg);
  //strcpy(outputCmdString, robotErrorMsg);
  memcpy(outputCmdString, robotErrorMsg, lenErrorMsg);

  outputCmdString[9] = '0' + savedWarehouseId;
  outputCmdString[11] = '0' + savedRobotId;
  outputCmdString[13] = '0' + errorCause;

  WC_SendMsgToServer(lenErrorMsg);
  Serial.println("->(0x13 Robot Error)");
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
void WC_SendRobotLinkPingMsg() 
{
  char robotPingMsg[] = "00021803000000\n";
  char lenPingMsg = 17;

  //lenString = strlen(robotPingMsg);
  //strcpy(outputCmdString, robotPingMsg);
  memcpy(outputCmdString, robotPingMsg, lenPingMsg);

  outputCmdString[9] = '0' + savedWarehouseId;
  outputCmdString[11] = '0' + savedRobotId;

  //Serial.println("----------- Ping to Server I'm alive ---------");
  WC_SendMsgToServer(lenPingMsg);
  Serial.println("->(0x18 Ping)");
}
 //*********************************************************
 // FUNCTION DESCRIPTION
 //	 Function description
 //*********************************************************
 void WC_SendMsgToServer(char strLength) 
 {
    char loopStr = 0;
    
   if (!warehouseClient.connected())
    {
      UTIL_ReconnectWarehouseServer();
    }
    
// ADD_0625 
    if (strLength >= MAX_LEN_OUTPUT_CMD_STRING)
    {
      Serial.print("F_ERR: Output Cmd Len: ");
      Serial.println(strLength);

      return;
    }

    Serial.print("[ROBOT -> SERVER] ");
    outputCmdString[strLength] = 0;
    warehouseClient.print(outputCmdString);
    Serial.print(outputCmdString);

/*
//    Serial.println("Send Msg To Warehouse Server: ");
    Serial.print("[ROBOT -> SERVER] ");
    for (loopStr = 0; loopStr < strLength; loopStr++)
    {
      warehouseClient.write(outputCmdString[loopStr]);
  
      if (loopStr != (strLength - 1))
        Serial.print(outputCmdString[loopStr]);  // '/n' character
    }
    //Serial.println("");
  
   warehouseClient.flush();
*/
 }

 //*********************************************************
 // FUNCTION DESCRIPTION
 //	 Function description
 //*********************************************************
 void UTIL_ConnectWarehouseServer() 
 {
   int serverConnectStatus = 0;
   int serverConnectCount = 0;

   // Start warehouse server connection
   Serial.println("Waiting Server Connection!");
/*
   Serial.print("- Server IP Addr: ");
   Serial.println(warehouseServerIp);
   Serial.print("- Server Port Number: ");
   Serial.println(SERVER_PORT_ID);
*/

   while (serverConnectStatus == 0)
   {
      serverConnectStatus = warehouseClient.connect(warehouseServerIp, SERVER_PORT_ID);

     if (serverConnectStatus)
     {
       warehouseClient.flush();
       Serial.println("Server Connected!! ");
     }
// ADD_0625
     else
     {
       serverConnectCount++;
       if (serverConnectCount > 2)
       {
         WiFi.disconnect();
         delay(200);
         WiFi_Connect();
         WiFi_PrintConnectionStatus();

         serverConnectCount = 0;
       }
       else
       {
         Serial.println("Try Conn Again!");
       }
     }
   }
 }

  //*********************************************************
  // FUNCTION DESCRIPTION
  //	 Function description
  //*********************************************************
  // ADD_0625
  void UTIL_ReconnectWarehouseServer()
  {
    warehouseClient.flush();
    warehouseClient.stop();
    Serial.println("F_ERR: Socket Disconnect!! ");
    delay(200);
  
    UTIL_ConnectWarehouseServer();
    // Send to READY message again
    WC_SendReadyMsg();
  }

 //*********************************************************
 // FUNCTION DESCRIPTION
 //	 Function description
 //*********************************************************
 void WiFi_Connect() 
 {
    int wifiStatus = WL_IDLE_STATUS;
   
     // Attempt to connect to WIfI network indicated by wifi ssid.
     while ( wifiStatus != WL_CONNECTED) 
     {
       Serial.print("Start WiFi Connect : ");
       Serial.println(wifiSsid);
      
       wifiStatus = WiFi.begin(wifiSsid);
       //wifiStatus = WiFi.begin(wifiSsid, wifiPass);  // For WPA/WPA2 network: 
     }
 }
 
 //*********************************************************
 // FUNCTION DESCRIPTION
 //	 Function description
 //*********************************************************
 void WiFi_PrintConnectionStatus() 
 {
   IPAddress wifiIp;
   long wifiRssi;

   // Print the basic connection and network information: Network, IP, and Subnet mask
   wifiIp = WiFi.localIP();
   Serial.print("WiFi IP Addr:: ");
   Serial.println(wifiIp);

/*
   // Print our MAC address.
   WiFi.macAddress(wifiMacAddr);
   Serial.print("WiFi MAC Addr: ");

   Serial.print(wifiMacAddr[5],HEX);
   Serial.print(":");
   Serial.print(wifiMacAddr[4],HEX);
   Serial.print(":");
   Serial.print(wifiMacAddr[3],HEX);
   Serial.print(":");
   Serial.print(wifiMacAddr[2],HEX);
   Serial.print(":");
   Serial.print(wifiMacAddr[1],HEX);
   Serial.print(":");
   Serial.println(wifiMacAddr[0],HEX);
*/

   // Print the wireless signal strength:
   wifiRssi = WiFi.RSSI();
   Serial.print("WiFi RSSI: ");
   Serial.print(wifiRssi);
   Serial.println(" dBm");
 }

///////////////////////////////////////////////////////////////////


void ROBOT_Initialize()
{
   // QTI Sensor Setup
   // Update 3 QTI Black/White value First Time
  //if(QTI_Update_BW_value() == 0) 
    //Serial.println("[ERROR] Sensor has wrong value !!");  // Error Check Point !!
  QTI_Update_BW_value();
  
  if(qti_L==1 && qti_C==0 && qti_R==1) // BWB
  {
      robot_status_machine = ROBOT_STATUS_RIGHT_BEFORE_LINE;   // Starting Point;
      Serial.println("[ROBOT] Ready At BEFORE_LINE !!");
  }else if(qti_L==1 && qti_C==0 && qti_R==0) // BWW
  {
      robot_status_machine = ROBOT_STATUS_ARRIVED_STATION;   // Starting Point;
      Serial.println("[ROBOT] Ready At ARRIVED_STATION !!");   
  }
  else
  {
      robot_status_machine = ROBOT_PARKED_ANYWARE;
      Serial.println("[ERROR_ROBOT] Where am I !!");
      WC_SendRobotErrorMsg(ERROR_CAUSE_ROBOT_FLOATING);
  }
    
   // Servo Setup
   LtServo.attach(LTSERVOPIN);
   RtServo.attach(RTSERVOPIN); 
  
  robot_mov_by_static(ROBOT_STOP,1); 
   
}



// For QTI Sensor reading. 
long RCtime(int sensPin){
   long result = 0;
   pinMode(sensPin, OUTPUT);       // make pin OUTPUT
   digitalWrite(sensPin, HIGH);    // make pin HIGH to discharge capacitor - study the schematic
   delay(1);                       // wait a  ms to make sure cap is discharged
   pinMode(sensPin, INPUT);        // turn pin into an input and time till pin goes low
   digitalWrite(sensPin, LOW);     // turn pullups off - or it won't work
   while(digitalRead(sensPin)){    // wait for pin to go low
     result++;
   }
   return result;                   // report results   
} 

int QTI_Update_BW_value()
{  // QTI sensoring value range 0 ~ 3000
    //long sensed_result = 0;
    
    qti_L_raw = RCtime(QTI_LEFT); 
    if(qti_L_raw > 2200) { 
      qti_L = -1; qti_C = -1; qti_R = -1; 
      robot_mov_by_static(ROBOT_STOP, 1);
      Serial.println("[ROBOT] STOP. On the air!!"); 
      return 0; 
    }
    //Serial.print("IR Value L : ");
    if(qti_L_raw > QTI_L_BW_THRESHOLD)  
    {
      qti_L = 1; //Serial.print("BLACK ("); Serial.print(qti_L_raw); Serial.print(")");
    }
    else 
    {
      qti_L = 0; //Serial.print("WHITE ("); Serial.print(qti_L_raw); Serial.print(")");
    }
    
    qti_C_raw = RCtime(QTI_CENTER);
    if(qti_C_raw > 3200) { 
      qti_L = -1; qti_C = -1; qti_R = -1; 
      robot_mov_by_static(ROBOT_STOP, 1);
      Serial.println("[ROBOT] STOP. On the air!!"); 
      return 0; 
    } 
    //Serial.print(" C : ");
    if(qti_C_raw > QTI_C_BW_THRESHOLD)  
    {
      qti_C = 1; //Serial.print("BLACK ("); Serial.print(qti_C_raw); Serial.print(")");
    }
    else 
    {
      qti_C = 0; //Serial.print("WHITE ("); Serial.print(qti_C_raw); Serial.print(")");
    }
    
    qti_R_raw = RCtime(QTI_RIGHT);
    if(qti_R_raw > 3200) { 
      qti_L = -1; qti_C = -1; qti_R = -1; 
      robot_mov_by_static(ROBOT_STOP, 1);
      Serial.println("[ROBOT] STOP. On the air!!"); 
      return 0;
    } 
    //Serial.print(" R : ");
    if(qti_R_raw > QTI_R_BW_THRESHOLD)  
    {
      qti_R = 1; //Serial.print("BLACK ("); Serial.print(qti_R_raw); Serial.println(")");
    }
    else 
    {
      qti_R = 0; //Serial.print("WHITE ("); Serial.print(qti_R_raw); Serial.println(")");
    }
    return 1;  
}


void check_robot_status()
{
    if(QTI_Update_BW_value() == 0) 
    {
      Serial.println("[ERROR_SENSOR] Wrong value !!");  // Error Check Point !!
      return;
    }
    
    if(robot_status_machine == ROBOT_PARKED_ANYWARE || robot_status_machine==ROBOT_STATUS_LOST_WAY)   // initial status checking again.
    {
//      if(QTI_Update_BW_value() == 0) Serial.println("[ERROR] QTI Sensor has wrong value !!");  // Error Check Point !!
      if(qti_L==1 && qti_C==0 && qti_R==1) // BWB
      {
          robot_status_machine = ROBOT_STATUS_RIGHT_BEFORE_LINE;   // Starting Point;
          Serial.println("[ROBOT] Ready At BEFORE_LINE !!");
          return;
       }
       else if(qti_L==1 && qti_C==0 && qti_R==0) // BWW
       {
          robot_status_machine = ROBOT_STATUS_ARRIVED_STATION;   // Starting Point;
          Serial.println("[ROBOT] Ready At ARRIVED_STATION !!");
          return;
       }
     }
       
     if(robot_status_machine == ROBOT_STATUS_ON_THE_WAY_LINE)
     {
         return;  // DUMMY For error status checking
     }
   
}


int command_execute(char command)   // FOR TEST
{
     switch ( command )
     {
        case '0':
             QTI_Update_BW_value();  // For Debug , checking the value 
             robot_mov_by_static(ROBOT_STOP,1);  break;
        case '1':
             robot_mov_by_static(ROBOT_GO_FORWARD,500);  break;
        case '2':
             robot_mov_by_static(ROBOT_GO_BACKWARD,500);  break;
        case '3':
             robot_mov_by_static(ROBOT_GO_SLIDE_LEFT,500);  break;
        case '4':
             robot_mov_by_static(ROBOT_GO_SLIDE_RIGHT,500);  break;
        case '5':
             robot_mov_by_static(ROBOT_TURN_LEFT,500);  break;
        case '6':
             robot_mov_by_static(ROBOT_TURN_RIGHT,500);  break;
        case '7':
             robot_mov_by_static(ROBOT_TURN_BACKWARD_LEFT,500);  break;
        case '8':
             robot_mov_by_static(ROBOT_TURN_BACKWARD_RIGHT,500);  break;
        case 'K':
             robot_mov_by_algorithm(ROBOT_ALGORITHM_GO_TO_NEXT_STATION);  break;
        case 'L':
             robot_mov_by_algorithm(ROBOT_ALGORITHM_TURN_LEFT_AT_STATION);  break;
        case 'M':
             robot_mov_by_algorithm(ROBOT_ALGORITHM_TURN_RIGHT_AT_STATION);  break;
        case 'N':
             robot_mov_by_algorithm(ROBOT_ALGORITHM_FIND_WAY1);  break;
        case 'X':
             robot_mov_by_static(ROBOT_STOP,1);  break;
        default: // If we don't know what the command is, we just say so and exit.
             Serial.print( "[ERROR] Unknown cmd from server:" ); Serial.println( command );
             return 0;
             break;
    
     } // switch
     return 1;
}


int robot_mov_by_algorithm(int command_algo)
{
  switch (command_algo)
  {
    case ROBOT_ALGORITHM_GO_TO_NEXT_STATION:
      check_robot_status();

      if(!robot_turn_to_right_hand())  // pending function
      {
          robot_mov_by_static(ROBOT_STOP, 1);
          Serial.println( "[ERROR_ROBOT] Turn Right Algo" );
          notify_status_to_server(1801); 
          return 0; 
          
      }
      
      //WC_SendRobotLinkPingMsg(); ///////////////////
      if(!robot_mov_to_next_station())   // pending function
      { 
          robot_mov_by_static(ROBOT_STOP, 1);
          Serial.println( "[ERROR_ROBOT] GO Next Algo" );
          notify_status_to_server(1800); 
          return 0; 
      }
      
      notify_status_to_server(100);  // Arrived notify
      return 1;
      
    case ROBOT_ALGORITHM_FIND_WAY1:

      robot_status_machine = ROBOT_STATUS_ON_THE_WAY_LINE;
      if(!robot_mov_to_next_station())
      { 
          Serial.println( "[ERROR] On go to next station Algorithm " );
          notify_status_to_server(1800); 
          return 0; 
      }
    
      return 1;

  }
  
}

//#define LOST_WAY_CNT_MOV_TO_NEXT_STATION 1000
int robot_mov_to_next_station()   // precondition : ROBOT_STATUS_RIGHT_BEFORE_LINE
{
    int count = 0;
    int old_L = 0;
    int old_C = 0;
    int old_R = 0;
    int findline = 0;
    int findline2 = 0;
    int findline3 = 0;
    int movecnt_before_line = 0;
    int movecnt_on_the_way = 0;
    int movecnt_enter_station = 0;


    // line trace algorithm //
    while(robot_status_machine == ROBOT_STATUS_RIGHT_BEFORE_LINE || robot_status_machine == ROBOT_STATUS_ON_THE_WAY_LINE || robot_status_machine == ROBOT_STATUS_ENTER_THE_STATION)
    {
        
        //if(QTI_Update_BW_value() == 0) Serial.println("[ERROR] QTI Sensor has wrong value !!");  // Error Check Point !!
        QTI_Update_BW_value();
        
        if(robot_status_machine == ROBOT_STATUS_RIGHT_BEFORE_LINE)
        {
          
          while(qti_C == 0 && qti_R == 1 && findline == 0) // Till the center Black
          {
            QTI_Update_BW_value();
            robot_mov_by_static(ROBOT_TURN_RIGHT, ROBOT_MOVEMENT_SLICE1);
            movecnt_before_line++;
            if(movecnt_before_line > 50)
              return 0; // lost way
          }
          findline = 1;
          
          if(qti_C == 1 && qti_R == 1)
          {
            robot_mov_by_static(ROBOT_TURN_LEFT, ROBOT_MOVEMENT_SLICE1);
            robot_mov_by_static(ROBOT_GO_FORWARD, ROBOT_MOVEMENT_SLICE1);
            movecnt_before_line = movecnt_before_line + 2;
            //robot_mov_by_static(ROBOT_TURN_RIGHT, ROBOT_MOVEMENT_SLICE1);
            //robot_mov_by_static(ROBOT_TURN_LEFT, ROBOT_MOVEMENT_SLICE1);
          }
          if(qti_C == 0 && qti_R == 1)
          {
            robot_mov_by_static(ROBOT_TURN_RIGHT, ROBOT_MOVEMENT_SLICE1);
            robot_mov_by_static(ROBOT_GO_FORWARD, ROBOT_MOVEMENT_SLICE1);
            movecnt_before_line = movecnt_before_line + 2;
            //robot_mov_by_static(ROBOT_TURN_RIGHT, ROBOT_MOVEMENT_SLICE1);
            //robot_mov_by_static(ROBOT_TURN_LEFT, ROBOT_MOVEMENT_SLICE1);
          }
          // STRAT LINE 
          
          if(qti_R == 0)  // XXW
          {
                robot_mov_by_static(ROBOT_GO_FORWARD, ROBOT_MOVEMENT_SLICE1);
                robot_status_machine = ROBOT_STATUS_ON_THE_WAY_LINE;  
                movecnt_before_line ++;
                Serial.println("[ALGO] ROBOT_STATUS_ON_THE_WAY_LINE !! ");
                Serial.println(movecnt_before_line);   // 777
                //Serial.println(movecnt_before_line);
                //delay(300);
                robot_ping_send_and_receive();
          }
          else
          {
            robot_mov_by_static(ROBOT_GO_FORWARD, ROBOT_MOVEMENT_SLICE1);
            movecnt_before_line ++;
          }
          if(movecnt_before_line > 60)     // Actual :: 17
            return 0; // lost way
        }        
 
        if(robot_status_machine == ROBOT_STATUS_ON_THE_WAY_LINE)
        {
     
          while(qti_C == 1 && qti_R == 0 && findline3 == 0) // XBW Till the center White
          {
            QTI_Update_BW_value();
            robot_mov_by_static(ROBOT_TURN_RIGHT, ROBOT_MOVEMENT_SLICE1);
            movecnt_on_the_way ++;
            if(movecnt_on_the_way > 25)
              return 0; // lost way
          }
          findline3 = 1;
          
          if(qti_C == 0 && qti_R == 0)
          {
            robot_mov_by_static(ROBOT_TURN_LEFT, ROBOT_MOVEMENT_SLICE1);
            robot_mov_by_static(ROBOT_GO_FORWARD, ROBOT_MOVEMENT_SLICE1);
            movecnt_on_the_way ++;
          }
          if(qti_C == 1 && qti_R == 0)
          {
            robot_mov_by_static(ROBOT_TURN_RIGHT, ROBOT_MOVEMENT_SLICE1);
            robot_mov_by_static(ROBOT_GO_FORWARD, ROBOT_MOVEMENT_SLICE1);
            movecnt_on_the_way ++;            
          }
          // STRAT LINE 
          
          if(qti_R == 1)  // XXB
          {
                robot_mov_by_static(ROBOT_GO_FORWARD, ROBOT_MOVEMENT_SLICE1);
                robot_mov_by_static(ROBOT_GO_FORWARD, ROBOT_MOVEMENT_SLICE1);
                movecnt_on_the_way ++;
                robot_status_machine = ROBOT_STATUS_ENTER_THE_STATION;  
                Serial.println("[ALGO] ROBOT_STATUS_ENTER_THE_STATION !! "); 
                Serial.println(movecnt_on_the_way);   // 777
                //Serial.println(movecnt_on_the_way);
                //delay(300);
                robot_ping_send_and_receive();
          }
          else
          {
            robot_mov_by_static(ROBOT_GO_FORWARD, ROBOT_MOVEMENT_SLICE2);
            movecnt_on_the_way ++;
            if((movecnt_on_the_way % 20) == 0) // Need to Change //////////////////////////////////////////////////////////////////////
            {
              // Ping send and receive
              robot_ping_send_and_receive();
            }
          }
          if(movecnt_on_the_way > 90)  // Need to Tune. // Actual :: 59
            return 0; // lost way          
   
        }
        
        if(robot_status_machine == ROBOT_STATUS_ENTER_THE_STATION)
        {
          while(qti_C == 0 && qti_R == 1 && findline2 == 0) // XWB Till the center Black
          {
            QTI_Update_BW_value();
            robot_mov_by_static(ROBOT_TURN_RIGHT, ROBOT_MOVEMENT_SLICE1);
            movecnt_enter_station ++;
            if(movecnt_enter_station > 40)
              return 0; // lost way
          }
          findline2 = 1;
          
          if(qti_C == 1 && qti_R == 1)
          {
            robot_mov_by_static(ROBOT_TURN_LEFT, ROBOT_MOVEMENT_SLICE1);
            movecnt_enter_station ++;
          }
          if(qti_C == 0 && qti_R == 1)
          {
            robot_mov_by_static(ROBOT_TURN_RIGHT, ROBOT_MOVEMENT_SLICE1);
            movecnt_enter_station ++;
          }
          // STRAT LINE 
          
          if(qti_C == 0 && qti_R == 0)  // XWW
          {
                robot_mov_by_static(ROBOT_GO_FORWARD, ROBOT_MOVEMENT_SLICE1);
                movecnt_enter_station ++;
                robot_status_machine = ROBOT_STATUS_ARRIVED_STATION;  
                Serial.println("[ALGO] ROBOT_STATUS_ARRIVED_STATION !! "); 
                Serial.println(movecnt_enter_station);   // 777
                //Serial.println(movecnt_enter_station);                                
                //delay(300);
                robot_ping_send_and_receive();
          }
          else
          {
            robot_mov_by_static(ROBOT_GO_FORWARD, ROBOT_MOVEMENT_SLICE1);
            movecnt_enter_station ++;
          }          
         if(movecnt_enter_station > 100) // Actual :: 63
            return 0; // lost way       
   
        }
        //WC_SendRobotLinkPingMsg(); ///////////////////
//        if(robot_check_lost_way() == 1) 
//          return 0;
       //delay(500);
    }
//    Serial.print("[SUCCESS] Goto Next Station Postcondition :: "); Serial.println(robot_status_machine);
    return 1;
} // postcondition : ROBOT_STATUS_ARRIVED_STATION

//#define LOST_WAY_CNT_TURN_RIGHT 1000
int robot_turn_to_right_hand()    // precondition : ROBOT_STATUS_ARRIVED_STATION
{
        int movecnt = 0;

        //if(QTI_Update_BW_value() == 0) Serial.println("[ERROR_SENSOR] QTI Sensor has wrong value !!");  // Error Check Point !!
        QTI_Update_BW_value();
        
        if(robot_status_machine == ROBOT_STATUS_ARRIVED_STATION)
        {
          Serial.println("[ALGO_TURN] 1.GO BLACK");
          while(qti_C == 0 || qti_R == 0) // XBB Till the center Black
          {
            QTI_Update_BW_value();
            robot_mov_by_static(ROBOT_GO_FORWARD, ROBOT_MOVEMENT_SLICE1);
            movecnt ++; 
            if(movecnt > 15) // 20 reduce the number 9
              return 0;  // Lostway cnt tune 
          }
          //Serial.print("[TURN] GO TO BLACK AREA !! count : ");
          Serial.println(movecnt);
          //delay(1000);
          
          Serial.println("[ALGO_TURN] 2.Right R White");
          while(qti_L == 1 && qti_R == 1)
          {
            QTI_Update_BW_value();
            robot_mov_by_static(ROBOT_TURN_RIGHT, 100);
            movecnt ++; 
            if(movecnt > 35) // 40 reduce the number 27
              return 0;  // Lostway cnt tune             
          }
          //Serial.print("[TURN] Turning right till R goes white !! count :");
          Serial.println(movecnt);
          robot_ping_send_and_receive(); ///////////////////////////////////////
          //delay(1000);
          robot_mov_by_static(ROBOT_TURN_BACKWARD_LEFT, 250);   // TUNE 
          robot_mov_by_static(ROBOT_GO_BACKWARD, 200);           // TUNE
          movecnt = movecnt + 2;
          //robot_mov_by_static(ROBOT_TURN_BACKWARD_LEFT, 100);
          //delay(5000);
          Serial.println("[ALGO_TURN] 3.Back R Black");
          do
          {  
            QTI_Update_BW_value();
            robot_mov_by_static(ROBOT_GO_BACKWARD, 50);    // TUNE
            robot_mov_by_static(ROBOT_TURN_BACKWARD_RIGHT, 10);   // TUNE
            movecnt = movecnt + 2;
//            robot_mov_by_static(ROBOT_TURN_BACKWARD_LEFT, 100);
//            robot_mov_by_static(ROBOT_TURN_BACKWARD_RIGHT, 100);
//            robot_mov_by_static(ROBOT_GO_BACKWARD, 50);
            if(movecnt > 57) // 65 reduce the number 49
              return 0;  // Lostway cnt tune              
            //delay(1000);
            
          }while(qti_R == 0);
          //delay(1000);
          //Serial.print("[TURN] GO Backward till R goes Black !! : count : ");
          Serial.println(movecnt);
          //robot_mov_by_static(ROBOT_TURN_BACKWARD_RIGHT, ROBOT_MOVEMENT_SLICE0);
          
          Serial.println("[ALGO_TURN] 4.Right C White");
          if(qti_C == 1)  // if C is on the other side of the black, Turn right till goes 0
          {
            while(qti_C == 1)
            {
              robot_mov_by_static(ROBOT_TURN_RIGHT, 50);
              QTI_Update_BW_value();
              movecnt ++;

              if(movecnt > 63) // 70 reduce the number 54
                return 0;  // Lostway cnt tune  
            }
          }
          //Serial.print("[TURN] GO Right For Center Cal till R goes white. !! count : ");
          Serial.println(movecnt);
          
          Serial.println("[ALGO_TURN] 5.Right C Black");
          while(qti_C == 0)
          {
            QTI_Update_BW_value();
            robot_mov_by_static(ROBOT_TURN_RIGHT, 100);
            movecnt ++;           
            if(movecnt > 72) // 80 reduce the number 61
              return 0;  // Lostway cnt tune           
          }
          //Serial.print("[TURN] Turning right till C goes Black !! count : ");
          Serial.println(movecnt);
          
          Serial.println("[ALGO_TURN] 6.LEFT C White");
          while(qti_C == 1)
          {
            QTI_Update_BW_value();
            robot_mov_by_static(ROBOT_TURN_LEFT, ROBOT_MOVEMENT_SLICE0);
            movecnt ++;            
            if(movecnt > 90) // 90 reduce the number 65 ~ 75
              return 0;  // Lostway cnt tune             
          }
          //Serial.print("[TURN] Turning LEFT till C goes White again !! count : ");
          Serial.println(movecnt);

          
          if(qti_C == 0 && qti_R == 1)  // XWW
          {
                robot_mov_by_static(ROBOT_GO_FORWARD, ROBOT_MOVEMENT_SLICE1);
                robot_status_machine = ROBOT_STATUS_RIGHT_BEFORE_LINE;  
                Serial.println("[ALGO] ROBOT_STATUS_RIGHT_BEFORE_LINE !!"); 
                //delay(300);
                robot_ping_send_and_receive();
          }
          else
          {
            Serial.println("[ERROR_ROBOT] LOSTWAY AT Turning !!");
            return 0;
            //robot_mov_by_static(ROBOT_GO_FORWARD, ROBOT_MOVEMENT_SLICE1);
          }          
   
        }
 //       if(robot_check_lost_way() == 1) 
 //         return 0;
 

//    Serial.print("[SUCCESS] Goto Next Station Postcondition :: "); Serial.println(robot_status_machine);
    return 1;     

} // postcondition : ROBOT_STATUS_RIGHT_BEFORE_LINE


int robot_check_lost_way()
{

  if((qti_L_raw > 400 && qti_C_raw > 400 && qti_R_raw > 400 ) || (qti_L_raw < 90 && qti_C_raw < 90 && qti_R_raw < 90 ) )
  {
    
    Serial.print("[ERROR] ROBOT LOSTWAY::status: "); Serial.print(robot_status_machine); 
    Serial.print(", Color :: Left:"); Serial.print(qti_L); Serial.print(" raw:"); Serial.print(qti_L_raw); Serial.print(", Center:");
    Serial.print(qti_C); Serial.print(" raw:"); Serial.print(qti_C_raw); Serial.print(", Right:");
    Serial.print(qti_R); Serial.print(" raw:"); Serial.println(qti_R_raw);

    robot_status_machine = ROBOT_STATUS_LOST_WAY;
    robot_mov_by_static(ROBOT_STOP, 1);
//    QTI_Update_BW_value();  // For Debug. 
    return 1;
   }
   return 0;
}


int robot_mov_by_static(int command_mov, int delay_ms)
{
  if(delay_ms <= 0 || delay_ms > 5000)
    delay_ms = 500;
   switch ( command_mov )
   {
      case ROBOT_STOP:
           LtServo.write(90);  
           RtServo.write(90);
           break;
        break;
      case ROBOT_GO_FORWARD:
           LtServo.write(ROBOT_SPEED_L); //LtServo.write(103);  //LtServo.write(180);  
           RtServo.write(ROBOT_SPEED_R); //RtServo.write(80); //RtServo.write(0); 
        break;
      case ROBOT_GO_BACKWARD:
           LtServo.write(ROBOT_SPEED_R);  //LtServo.write(0);  
           RtServo.write(ROBOT_SPEED_L); //RtServo.write(180); 
        break;
      case ROBOT_GO_SLIDE_LEFT:
           LtServo.write(100);  
           RtServo.write(0); 
        break;
      case ROBOT_GO_SLIDE_RIGHT:
           LtServo.write(180);  
           RtServo.write(80); 
        break;
      case ROBOT_TURN_LEFT:
           LtServo.write(90);  //LtServo.write(90);  
           RtServo.write(ROBOT_SPEED_R); //RtServo.write(0); 
        break;
      case ROBOT_TURN_RIGHT:
           LtServo.write(ROBOT_SPEED_L);  //LtServo.write(180);  
           RtServo.write(90); //RtServo.write(90); 
        break;
      case ROBOT_TURN_BACKWARD_LEFT:
           LtServo.write(ROBOT_SPEED_R); //LtServo.write(0);  
           RtServo.write(90); 
        break;
      case ROBOT_TURN_BACKWARD_RIGHT:
           LtServo.write(90);  
           RtServo.write(ROBOT_SPEED_L); //RtServo.write(180); 
        break;
      default:
           LtServo.write(90);  
           RtServo.write(90);       
        break;
   }
   delay(delay_ms);   // STOP all wheel after delay.
   LtServo.write(90);  
   RtServo.write(90);   
   return 1;
}

void robot_ping_send_and_receive()
{
       
  /*
    int loopcnt = 0;
    warehouseClient.flush();  // needed ??
    WC_WaitAndProcInputMsg(); // Check if there's anything
    Serial.println("[ROBOT] Inter. Ping and Receive.");
    WC_SendRobotLinkPingMsg();// Ping to the server
    delay(100);               // need to tune ////////////////////////////////////////
    for (loopcnt = 0; loopcnt < 2; loopcnt++)
    {
      WC_WaitAndProcInputMsg(); // get ping ack. 
      delay(50);
    }
    
    if (!warehouseClient.connected())
    {
      UTIL_ReconnectWarehouseServer();
    }
    */
}

int notify_status_to_server(int ack_type)
{
 // need to write.
 return 1; 
  
}


