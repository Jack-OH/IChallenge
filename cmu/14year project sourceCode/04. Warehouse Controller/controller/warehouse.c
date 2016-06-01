#include <SPI.h>
#include <WiFi.h>
#include <Servo.h>

// -----------------------------------
// CONFIGURATION
// -----------------------------------

// Room Light ON~~~
#define PHOTO_SENSOR_THRESHOULD_VALUE   (650)

// Room Light OFF~~
//#define PHOTO_SENSOR_THRESHOULD_VALUE   (350)

// WiFi: Set Warehouse Server IP Address
#define WAREHOUSE_SERVER_IP_ADDR    192,168,0,8
//#define WAREHOUSE_SERVER_IP_ADDR    128,237,231,105
//#define WAREHOUSE_SERVER_IP_ADDR    128,237,236,81

char wifiSsid[] = "iptime";            // Local AP SSID
//char wifiSsid[] = "CMU";             // CMU AP SSID
//char wifiSsid[] = "Shadyside Inn";   // Hotel AP SSID
//char wifiPass[] = "hotel5405";

#define SERVER_PORT_ID                  (9002)

// --------------------------------------------------------------------------------
//	Protocol Definition
// --------------------------------------------------------------------------------

#define SUCCESS              (0)
#define FAILURE              (1)

#define TRUE                 (1)
#define FALSE                (0)

// Command data buffer
#define MAX_LEN_OF_MSG	     (20)

#define PARSING_SUCCESS			(0x00)
#define PARSING_FAILURE			(0x01)

// Message header
typedef struct
{
  unsigned char	msgSeqNum;
  unsigned char	msgSrc;
  unsigned char	msgId;
  unsigned char	msgBodyLen;
} S_MSG_HEADER;

#define MSG_BODY_START			(sizeof(S_MSG_HEADER))

// Message header: message source
#define MSG_SOURCE_WH_SERVER		(0x01)
#define MSG_SOURCE_ROBOT		(0x02)
#define MSG_SOURCE_WH_CTRL 		(0x03)

#define MSG_INVALID_MSG_ID		(0xFF)

// Message header: message id between controller and robot
#define MSG_WC_READY                    (0x01)
#define MSG_WC_ACK_TO_READY             (0x02)
#define MSG_WC_NOTI_ROBOT_ARRIVAL       (0x03)
#define MSG_WC_NOTI_ST_JOB_COMPLETION   (0x04)
#define MSG_WC_LINK_PING                (0x05)
#define MSG_WC_LINK_ECHO                (0x06)
#define MSG_WC_SEND_ERROR               (0x07)

//----------------------------------------
// Message Body: Server and Controller

// Message: Ready
#define MAX_BYTE_NUM_OF_MAC_ID    (6)

typedef struct
{
  S_MSG_HEADER  msgHdr;
  unsigned char	macId[MAX_BYTE_NUM_OF_MAC_ID];
  unsigned char	crc;
} S_WC_READY;

// Message: Move to Next Station
typedef struct
{
  S_MSG_HEADER  msgHdr;
  unsigned char	warehouseId;
  unsigned char	crc;
} S_ACK_TO_READY;

// Message: Notify the Robot Arrival
typedef struct
{
  S_MSG_HEADER  msgHdr;
  unsigned char	warehouseId;
  unsigned char	stationId;  // shipping station: 0
  unsigned char	crc;
} S_ROBOT_ARRIVAL;

// Message: Notify the Station Job Completion
typedef struct
{
  S_MSG_HEADER  msgHdr;
  unsigned char	warehouseId;
  unsigned char	stationId;  // shipping station: 0
  unsigned char	crc;
} S_ST_JOB_COMPLETIION;

// Message: Notify Warehouse Controller Error
#define WC_ERROR_CAUSE_WIFI_DISCONECT  (0x00)
#define WC_ERROR_CAUSE_STATION_DETECT  (0x01)
#define WC_ERROR_CAUSE_SWITCH_DETECT   (0x02)
#define WC_ERROR_CAUSE_UNKNOWN         (0x10)

typedef struct
{
  S_MSG_HEADER  msgHdr;
  unsigned char	warehouseId;
  unsigned char	errorCause;
  unsigned char	crc;
} S_NOTI_WC_ERR;

// Message: Warehouse Controller Link Ping
typedef struct
{
  S_MSG_HEADER  msgHdr;
  unsigned char	warehouseId;
  unsigned char	crc;
} S_WC_LINK_PING;

// Message: Warehouse Controller Link Echo
typedef struct
{
  S_MSG_HEADER  msgHdr;
  unsigned char	warehouseId;
  unsigned char	crc;
} S_WC_LINK_ECHO;

typedef union
{
  S_ACK_TO_READY     ackReady;
  S_WC_LINK_ECHO     linkEcho;
} U_INPUT_MSG;

// -----------------------------------
// GLOBAL VARIABLES
// -----------------------------------
#define MAX_LEN_OUTPUT_CMD_STRING  (40)

IPAddress     warehouseServerIp(WAREHOUSE_SERVER_IP_ADDR);
WiFiClient    warehouseClient;
byte          wifiMacAddr[MAX_BYTE_NUM_OF_MAC_ID];
unsigned char msgSeqNum = 0;

char		hexCh[2];
unsigned char	cmdDataByteLen;
unsigned char	cmdData[MAX_LEN_OF_MSG];

U_INPUT_MSG   inputCmdMsg;

char          inputCmdStringLen;
char          inputCmdString[MAX_LEN_OF_MSG*2 + 1];
char          outputCmdString[MAX_LEN_OUTPUT_CMD_STRING + 1];
char          warehouseInitFlag = FALSE;
unsigned char  savedWarehouseId = 0xFF;

#define STATION_ID_SHIPPING_DOCK    (0)
#define INVALID_STATION_ID          (0xFF)
unsigned char  prevSensorStId = INVALID_STATION_ID;
unsigned char  prevSwitchStId = INVALID_STATION_ID;

unsigned char msgSendCount = 0;

int photocellPin1 = 0;
volatile int photocellReading1;
int photocellPin2 = 1;
volatile int photocellReading2;
int photocellPin3 = 2;
volatile int photocellReading3;
int photocellPin4 = 3;  // shipping dock (station id: 0)
volatile int photocellReading4;

int inSwitchPin1 = 3;
volatile int switchVal1;
int inSwitchPin2 = 5;
volatile int switchVal2;
int inSwitchPin3 = 6;
volatile int switchVal3;
int inSwitchPin4 = 9;  // shipping dock
volatile int switchVal4;

int  timer_counter = 0;
char timer_ready_flag = FALSE;

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

   // Initialize switch
   WC_InitSwitch();

   // Attempt to connect to WIfI network indicated by wifi ssid.
   WiFi_Connect();

   // Print connection information to the debug terminal
   WiFi_PrintConnectionStatus();

   // Start warehouse server connection
   Serial.println("-----");
   Serial.println("Initial Server WiFi Connect!");
   UTIL_ConnectWarehouseServer();
   Serial.println("-----");

   // Send the Ready message to Server
   WC_SendReadyMsg();

  // Init timer
//  WC_InitTimer();  // not use in this version
}

#define MAX_DURATION_OF_MSG_CHECK  (40)  // about 10 second
int checkRcvMsgDuration = MAX_DURATION_OF_MSG_CHECK;

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
void loop()
{
  if (warehouseInitFlag == FALSE)
  {
    // Only wait one message from server (Ack to Ready message)
    if (WC_WaitAndProcInputMsg() == MSG_WC_ACK_TO_READY)
    {
      warehouseInitFlag = TRUE;
    }
  }
  else
  {
    // check photo sensor and switch
    WC_CheckPhotoSensor();
    WC_CheckStationSwitch();

    // Check client connection
    if (!warehouseClient.connected())
    {
      UTIL_ReconnectWarehouseServer();
    }

//    if ((checkRcvMsgDuration%(MAX_DURATION_OF_MSG_CHECK/3)) == (MAX_DURATION_OF_MSG_CHECK/3 - 3))
    WC_WaitAndProcInputMsg();

    checkRcvMsgDuration--;

    if (checkRcvMsgDuration <= 0)
    {
// Send Ping Message
      WC_SendLinkPing();
      checkRcvMsgDuration = MAX_DURATION_OF_MSG_CHECK;
    }
  }
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
ISR(TIMER1_OVF_vect)
{
  TCNT1 = timer_counter;

  timer_ready_flag = TRUE;
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
void WC_InitTimer()
{
  noInterrupts();  // disable all interrupts
  TCCR1A = 0;
  TCCR1B = 0;

  timer_counter = 64286;

  TCNT1 = timer_counter;
  TCCR1B |= (1 << CS12);
  TIMSK1 |= (1 << TOIE1);

  interrupts();  // enable all interrupts
}

#define MAX_PHOTO_SENSING_LOOP  (3)

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
void WC_CheckPhotoSensor()
{
  unsigned char photoStationId;
  int findPhotoStation;
  int photoLoop = 0;

  photocellReading1 = 0;
  photocellReading2 = 0;
  photocellReading3 = 0;
  photocellReading4 = 0;  // shipping dock

  for (photoLoop = 0; photoLoop < MAX_PHOTO_SENSING_LOOP; photoLoop++)
  {
    photocellReading1 += analogRead(photocellPin1);
    photocellReading2 += analogRead(photocellPin2);
    photocellReading3 += analogRead(photocellPin3);
    photocellReading4 += analogRead(photocellPin4);

    delay(100);
  }

  photocellReading1 = photocellReading1/MAX_PHOTO_SENSING_LOOP;
  photocellReading2 = photocellReading2/MAX_PHOTO_SENSING_LOOP;
  photocellReading3 = photocellReading3/MAX_PHOTO_SENSING_LOOP;
  photocellReading4 = photocellReading4/MAX_PHOTO_SENSING_LOOP;

/*
  Serial.print("Ava Photo1 = ");
  Serial.println(photocellReading1);
  Serial.print("Ava Photo2 = ");
  Serial.println(photocellReading2);
  Serial.print("Ava Photo3 = ");
  Serial.println(photocellReading3);
  Serial.print("Ava Photo4 = ");
  Serial.println(photocellReading4);
  Serial.println("");
*/

  findPhotoStation = FALSE;
  photoStationId = INVALID_STATION_ID;

  switch(prevSensorStId)
  {
    case 0:  // shipping dock
      if (photocellReading1 < PHOTO_SENSOR_THRESHOULD_VALUE) {
        photoStationId = 1;
        findPhotoStation = TRUE;
      }
    break;

    case 1:
      if (photocellReading2 < PHOTO_SENSOR_THRESHOULD_VALUE) {
        photoStationId = 2;
        findPhotoStation = TRUE;
      }
    break;

    case 2:
      if (photocellReading3 < PHOTO_SENSOR_THRESHOULD_VALUE) {
        photoStationId = 3;
        findPhotoStation = TRUE;
      }
    break;

    case 3:
      if (photocellReading4 < PHOTO_SENSOR_THRESHOULD_VALUE) {
        photoStationId = 0;
        findPhotoStation = TRUE;
      }
    break;
  }

  if (findPhotoStation != TRUE)
  {
    findPhotoStation = TRUE;
    if (photocellReading1 < PHOTO_SENSOR_THRESHOULD_VALUE)
        photoStationId = 1;
    else if (photocellReading2 < PHOTO_SENSOR_THRESHOULD_VALUE)
        photoStationId = 2;
    else if (photocellReading3 < PHOTO_SENSOR_THRESHOULD_VALUE)
        photoStationId = 3;
    else if (photocellReading4 < PHOTO_SENSOR_THRESHOULD_VALUE)
        photoStationId = 0;
    else
      findPhotoStation = FALSE;
  }

  if (findPhotoStation == TRUE)
  {
    if (prevSensorStId == INVALID_STATION_ID || prevSensorStId != photoStationId)
    {
      prevSensorStId = photoStationId;
      WC_SendRobotArrival(photoStationId);
    }
  }
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
void WC_CheckStationSwitch()
{
  unsigned char switchStationId = INVALID_STATION_ID;
  unsigned char findSwitchStation;

  findSwitchStation = FALSE;

  switchVal1 = digitalRead(inSwitchPin1);
  if (switchVal1 == 0 && prevSwitchStId != 1) {
    switchStationId = 1;
    findSwitchStation = TRUE;
  }

  switchVal2 = digitalRead(inSwitchPin2);
  if (switchVal2 == 0 && prevSwitchStId != 2) {
    switchStationId = 2;
    findSwitchStation = TRUE;
  }

  switchVal3 = digitalRead(inSwitchPin3);
  if (switchVal3 == 0 && prevSwitchStId != 3) {
    switchStationId = 3;
    findSwitchStation = TRUE;
  }

  switchVal4 = digitalRead(inSwitchPin4);
  if (switchVal4 == 0 && prevSwitchStId != 0) {
    switchStationId = 0;
    findSwitchStation = TRUE;
  }

  if (findSwitchStation == TRUE)
  {
    prevSwitchStId = switchStationId;
    WC_SendStJobCompletion(switchStationId);
  }
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
void WC_InitSwitch()
{
  pinMode(inSwitchPin1, INPUT);
  pinMode(inSwitchPin2, INPUT);
  pinMode(inSwitchPin3, INPUT);
  pinMode(inSwitchPin4, INPUT);
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
unsigned char WC_WaitAndProcInputMsg()
{
  unsigned char rcvMsgId = MSG_INVALID_MSG_ID;

  if (warehouseClient.available())
  {
    rcvMsgId = WC_ProcessInputMsgMsg();
  }

  return rcvMsgId;
}

 //*********************************************************
 // FUNCTION DESCRIPTION
 //	 Function description
 //*********************************************************
 char WC_ProcWsCmdMsg(unsigned char msgId, unsigned char msgBodyLen, unsigned char *pCmdData)
 {
  char result = SUCCESS;

  switch (msgId)
  {
    case MSG_WC_ACK_TO_READY:
      WC_ParseAckToReady(cmdData, msgBodyLen);
      break;
    case MSG_WC_LINK_ECHO:
      WC_ParseWarehouseControllerLinkEcho(cmdData, msgBodyLen);
      break;
    default:
      result = FAILURE;
      Serial.print("F_ERR: Invalid Msg: ");
      Serial.println(msgId);
      break;
   }

  return result;
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//
//*********************************************************
char WC_ParseAckToReady(unsigned char *pCmdData, unsigned char cmdBodyLen)
{
  char idx = MSG_BODY_START;

  inputCmdMsg.ackReady.warehouseId = pCmdData[idx++];
  inputCmdMsg.ackReady.crc = pCmdData[idx++];

  Serial.println("[S->C] Ack To Ready: ");
  Serial.print("- Warehouse Id: ");
  Serial.println(inputCmdMsg.ackReady.warehouseId);

  savedWarehouseId = inputCmdMsg.ackReady.warehouseId;

  return PARSING_SUCCESS;
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//
//*********************************************************
char WC_ParseWarehouseControllerLinkEcho(unsigned char *pCmdData, unsigned char cmdBodyLen)
{
  char idx = MSG_BODY_START;

  inputCmdMsg.linkEcho.warehouseId = pCmdData[idx++];
  inputCmdMsg.linkEcho.crc = pCmdData[idx++];

  Serial.println("[S->C] Link Echo: -");

  return PARSING_SUCCESS;
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
void WC_SendReadyMsg()
{
  S_WC_READY  wcReady;

  WC_MakeMsgHdrInfo(&wcReady, MSG_WC_READY, sizeof(S_WC_READY));

  memcpy(wcReady.macId, wifiMacAddr, MAX_BYTE_NUM_OF_MAC_ID);
  wcReady.crc = 0;

  Serial.println("[C->S] Ready");

  WC_SendMsgToServer(&wcReady, sizeof(S_WC_READY));
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
void WC_SendRobotArrival(unsigned char stationId)
{
  S_ROBOT_ARRIVAL  robotArrival;

  WC_MakeMsgHdrInfo(&robotArrival, MSG_WC_NOTI_ROBOT_ARRIVAL, sizeof(S_ROBOT_ARRIVAL));

  robotArrival.warehouseId = savedWarehouseId;
  robotArrival.stationId = stationId;
  robotArrival.crc = 0;

  Serial.print("\n----- Send Msg Cnt = ");
  Serial.print(msgSendCount);
  Serial.println("-----");

  Serial.println("[C->S] Robot Arrival: ");
  Serial.print("- Station Id: ");
  Serial.println(robotArrival.stationId);

  WC_SendMsgToServer(&robotArrival, sizeof(S_ROBOT_ARRIVAL));

  prevSensorStId = stationId;

  msgSendCount++;
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
void WC_SendStJobCompletion(unsigned char stationId)
{
  S_ST_JOB_COMPLETIION  jobCompletion;

  WC_MakeMsgHdrInfo(&jobCompletion, MSG_WC_NOTI_ST_JOB_COMPLETION, sizeof(S_ST_JOB_COMPLETIION));

  jobCompletion.warehouseId = savedWarehouseId;
  jobCompletion.stationId = stationId;
  jobCompletion.crc = 0;

  Serial.print("\n----- Send Msg Cnt = ");
  Serial.print(msgSendCount);
  Serial.println("-----");

  Serial.println("[C->S] Work Completion");
  Serial.print("- Station Id: ");
  Serial.println(jobCompletion.stationId);

  WC_SendMsgToServer(&jobCompletion, sizeof(S_ST_JOB_COMPLETIION));

  prevSwitchStId = stationId;

  msgSendCount++;
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
void WC_SendWcErr(unsigned char errorCause)
{
  S_NOTI_WC_ERR  wcError;

  WC_MakeMsgHdrInfo(&wcError, MSG_WC_SEND_ERROR, sizeof(S_NOTI_WC_ERR));

  wcError.warehouseId = savedWarehouseId;
  wcError.errorCause = errorCause;
  wcError.crc = 0;

  Serial.println("-----");
  Serial.println("[C->S] Error:");
  Serial.print("- Error Cause: ");
  Serial.println(wcError.errorCause);

  WC_SendMsgToServer(&wcError, sizeof(S_NOTI_WC_ERR));
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//*********************************************************
void WC_SendLinkPing()
{
  S_WC_LINK_PING  linkPing;

  WC_MakeMsgHdrInfo(&linkPing, MSG_WC_LINK_PING, sizeof(S_WC_LINK_PING));

  linkPing.warehouseId = savedWarehouseId;
  linkPing.crc = 0;

  Serial.println("-----");
  Serial.println("[C->S] Link Ping: -");

  WC_SendMsgToServer(&linkPing, sizeof(S_WC_LINK_PING));
}

 //*********************************************************
 // FUNCTION DESCRIPTION
 //	 Function description
 //*********************************************************
 void WC_MakeMsgHdrInfo(void *pMsg, unsigned char msgId, unsigned char msgLen)
 {
   S_MSG_HEADER *pMsgHdr = (S_MSG_HEADER *)pMsg;

   // for test: fixed number
   pMsgHdr->msgSeqNum = 0;
//   pMsgHdr->msgSeqNum = msgSeqNum++;

   pMsgHdr->msgSrc = MSG_SOURCE_WH_CTRL;
   pMsgHdr->msgId = msgId;
   pMsgHdr->msgBodyLen = msgLen - sizeof(S_MSG_HEADER);
 }

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//
//*********************************************************
char WC_ParseMsgHdr(unsigned char *pCmdData, char cmdLen)
{
  char idx = 0;
  S_MSG_HEADER *pMsgHdr =  (S_MSG_HEADER *)&inputCmdMsg;

  if (cmdLen < sizeof(S_MSG_HEADER))
  {
    Serial.print("\nInvalid Cmd Msg Len: ");
    Serial.println(cmdLen);
    return PARSING_FAILURE;
  }

  pMsgHdr->msgSeqNum = pCmdData[idx++];
  pMsgHdr->msgSrc = pCmdData[idx++];
  pMsgHdr->msgId = pCmdData[idx++];
  pMsgHdr->msgBodyLen = pCmdData[idx++];

  if (pMsgHdr->msgId != MSG_WC_LINK_ECHO)
  {
    Serial.println("Input Cmd Msg: ");
    Serial.print("- Msg Id: ");
    Serial.println(pMsgHdr->msgId, HEX);
/*
    Serial.print("- Msg Body Len: ");
    Serial.println(pMsgHdr->msgBodyLen);
*/
  }

  return PARSING_SUCCESS;
}

 //*********************************************************
 // FUNCTION DESCRIPTION
 //	 Function description
 //*********************************************************
 unsigned char WC_ProcessInputMsgMsg()
 {
   S_MSG_HEADER *pMsgHdr = (S_MSG_HEADER *)&inputCmdMsg;

   cmdDataByteLen = 0;
   pMsgHdr->msgId = MSG_INVALID_MSG_ID;

   cmdDataByteLen = UTIL_ReadCommandData(cmdData);

   if (cmdDataByteLen > 0)
   {
     if (WC_ParseMsgHdr(cmdData, cmdDataByteLen) == PARSING_SUCCESS)
     {
       WC_ProcWsCmdMsg(pMsgHdr->msgId, pMsgHdr->msgBodyLen, cmdData);
     }
   }

//   WC_PrintInputStringMsg(pMsgHdr->msgId);

  if (pMsgHdr->msgId == MSG_INVALID_MSG_ID)
  {
     Serial.print("\nIgnore Input Msg: ");
     Serial.println(pMsgHdr->msgId, HEX);
  }

   return pMsgHdr->msgId;
 }

 //*********************************************************
 // FUNCTION DESCRIPTION
 //	 Function description
 //*********************************************************
 void WC_PrintInputStringMsg(unsigned char inMsgId)
 {
   unsigned char inStringLoop = 0;

//   if (inMsgId != MSG_WC_LINK_ECHO)
   {
     Serial.println("Rcv Msg From Server:");

     for (inStringLoop = 0; inStringLoop < inputCmdStringLen; inStringLoop++)
     {
       Serial.print(inputCmdString[inStringLoop]);
     }

     Serial.println("");
   }
 }

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//
//*********************************************************
unsigned char UTIL_ReadCommandData(unsigned char *pCmdData)
{
  char ch = ' ';
  char toggle = 0;
  unsigned char cmdStringLen = 0;
  unsigned char cmdLen = 0;

  inputCmdStringLen = 0;

  Serial.println("-----");
  Serial.println("Rcv Msg From Server:");

  while (ch != '\n')
  {
    ch = warehouseClient.read();
    if (ch != '\n')
    {
      Serial.print(ch);

      inputCmdString[inputCmdStringLen] = ch;
      inputCmdStringLen++;

      if (inputCmdStringLen >= (MAX_LEN_OF_MSG*2))
      {
        Serial.print("F_ERR: Input Cmd Length: ");
        Serial.println(inputCmdStringLen);
        Serial.println("SHALL BE IGNORED!!");
        warehouseClient.flush();

        return 0;
      }

      if ((cmdStringLen % 2) == 0)
      {
        hexCh[toggle++] = ch;
      }
      else
      {
        hexCh[toggle--] = ch;
        pCmdData[cmdLen++] = UTIL_ConvertStrToHex(hexCh);
      }

      cmdStringLen++;
    }
    else
    {
      if (cmdStringLen % 2 == 1)
      {
        Serial.print('0');
        hexCh[toggle] = '0';
        pCmdData[cmdLen++] = UTIL_ConvertStrToHex(hexCh);

        inputCmdString[inputCmdStringLen] = '0';
        inputCmdStringLen++;
      }
      Serial.println("");
    }
  }

  return cmdLen;
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//
//*********************************************************
char UTIL_ConvertCmdDataToStr(unsigned char *pHexData, char hexLen, char *pCmdString)
{
  char i, strIndex = 0;

  for (i = 0; i < hexLen; i++)
  {
    if (UTIL_ConvertHexToStr(pHexData[i], &pCmdString[strIndex]))
    {
      strIndex += 2;
    }
    else
    {
      return 0;
    }
  }

  pCmdString[strIndex++] = '\n';
  pCmdString[strIndex] = 0;

  return strIndex;
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//
//*********************************************************
unsigned char UTIL_ConvertStrToChar(char ch)
{
  unsigned char data = 0;

  if (ch >= '0' && ch <= '9')
  {
    data = ch - '0';
  }
  else if (ch >= 'A' && ch <= 'F')
  {
    data = ch - 'A' + 10;
  }
  else if (ch >= 'a' && ch <= 'f')
  {
    data = ch - 'a' + 10;
  }

  return data;
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//
//*********************************************************
unsigned char UTIL_ConvertStrToHex(char *pCh)
{
  unsigned char hex = 0x00;

  hex = UTIL_ConvertStrToChar(pCh[0]) * 16;
  hex |= UTIL_ConvertStrToChar(pCh[1]);

  return hex;
}

//*********************************************************
// FUNCTION DESCRIPTION
//	 Function description
//
//*********************************************************
char UTIL_ConvertHexToStr(unsigned char hex, char *pCh)
{
  char upperDecimal, lowerDecimal, charIdx = 0;

  upperDecimal = hex >> 4;
  lowerDecimal = hex & 0x0F;

  if (upperDecimal <= 0x09)
  {
    pCh[charIdx] = upperDecimal + '0';
  }
  else if (0xA <= upperDecimal && upperDecimal <= 0xF)
  {
    pCh[charIdx] = (upperDecimal - 0xA) + 'A';
  }
  else
  {
    return 0;
  }

  charIdx++;

  if (lowerDecimal <= 0x09)
  {
    pCh[charIdx] = lowerDecimal + '0';
  }
  else if (0xA <= lowerDecimal && lowerDecimal <= 0xF)
  {
    pCh[charIdx] = (lowerDecimal - 0xA) + 'A';
  }
  else
  {
    return 0;
  }

  return ++charIdx;
}

 //*********************************************************
 // FUNCTION DESCRIPTION
 //	 Function description
 //*********************************************************
 void WC_SendMsgToServer(void *pOutCmdData, char cmdLen)
 {
    char strLen, i;

    if (cmdLen >= (MAX_LEN_OUTPUT_CMD_STRING/2))
    {
      Serial.print("F_ERR: Output Cmd Len: ");
      Serial.println(cmdLen);
      Serial.println("SHALL BE IGNORED!!");

      return;
    }

    // Check client connection before message sending
    if (!warehouseClient.connected())
    {
      UTIL_ReconnectWarehouseServer();
    }

    strLen = UTIL_ConvertCmdDataToStr((unsigned char *)pOutCmdData, cmdLen, outputCmdString);

    outputCmdString[strLen] = 0;
    warehouseClient.print(outputCmdString);
    Serial.print(outputCmdString);

#if 0 // Old version (Only write 1 character step by step)
//    Serial.println("Send Msg To Warehouse Server: ");
    for (i = 0; i < strLen; i++)
    {
      warehouseClient.write(outputCmdString[i]);

      if (i != (strLen - 1))
        Serial.print(outputCmdString[i]);  // '/n' character
    }
    Serial.println("");

#endif
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
   Serial.print("- IP Addr: ");
   Serial.println(warehouseServerIp);
   Serial.print("- Port Number: ");
   Serial.println(SERVER_PORT_ID);

   while (serverConnectStatus == 0)
   {
      serverConnectStatus = warehouseClient.connect(warehouseServerIp, SERVER_PORT_ID);

     if (serverConnectStatus)
     {
       warehouseClient.flush();
       Serial.println("Server Connected!!");
     }
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
         Serial.println("Try to Connect Again!!");
       }
     }
   }
 }

  //*********************************************************
  // FUNCTION DESCRIPTION
  //	 Function description
  //*********************************************************
  void UTIL_ReconnectWarehouseServer()
  {
    warehouseClient.flush();
    warehouseClient.stop();
    Serial.println("F_ERR: Socket Disconnected!! ");
    Serial.println("WAIT WAIT WAIT!!");
    delay(100);

    prevSensorStId = INVALID_STATION_ID;
    prevSwitchStId = INVALID_STATION_ID;

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
       Serial.print("Start WiFi Connection: ");
       Serial.println(wifiSsid);

       wifiStatus = WiFi.begin(wifiSsid);
 //      wifiStatus = WiFi.begin(wifiSsid, wifiPass);  // For WPA/WPA2 network:
     }
 }

 //*********************************************************
 // FUNCTION DESCRIPTION
 //	 Function description
 //*********************************************************
 void WiFi_PrintConnectionStatus()
 {
   IPAddress ip;
   long rssi;

   // Print the basic connection and network information: Network, IP, and Subnet mask
   ip = WiFi.localIP();
   Serial.print("WiFi IP Addr:: ");
   Serial.println(ip);

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

   // Print the wireless signal strength:
   rssi = WiFi.RSSI();
   Serial.print("WiFi RSSI: ");
   Serial.print(rssi);
   Serial.println(" dBm");
 }

