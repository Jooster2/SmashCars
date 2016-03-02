#include <Adafruit_MotorShield.h>
#include <Wire.h>

//BLÅ 11, grön 10, servo sju
#include <Servo.h>
#include <SoftwareSerial.h>  
//#include <VirtualWire.h>

//#define FORWARD 0
//#define BACKWARD 1
//#define SERVO 2
//#define BRAKE 3
#define CRASH_SENSOR_PIN 12

bool sensorIsActive;
boolean driveForward;
boolean driveBackwards;
boolean servoTurn;
boolean motorBrake;
int bluetoothTx = 2;  // TX-O pin of bluetooth mate, Arduino D2
int bluetoothRx = 3;  // RX-I pin of bluetooth mate, Arduino D3


Adafruit_MotorShield AFMS = Adafruit_MotorShield();
Adafruit_DCMotor *dcMotor = AFMS.getMotor(1);


int servoPin = 10;

int lostCon;
Servo servo;
SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);

 
void setup() {
  //Serial.begin(9600);// Debugging only
  //Serial.println("setup");
 
  bluetooth.begin(115200);  // The Bluetooth Mate defaults to 115200bps
  bluetooth.print("$$$");  // Print three times individually
 // Enter command mode
  AFMS.begin();

// MOTOR STUFF
  dcMotor->setSpeed(0);
   dcMotor->run(FORWARD);
   dcMotor->run(RELEASE);
  
  delay(100);  // Short delay, wait for the Mate to send back CMD
  bluetooth.println("U,9600,N");  // Temporarily Change the baudrate to 9600, no parity
  // 115200 can be too fast at times for NewSoftSerial to relay the data reliably
  bluetooth.begin(9600);  // Start bluetooth serial at 9600

  pinMode(CRASH_SENSOR_PIN, INPUT);
  sensorIsActive = true;
  
  servo.attach (servoPin);
  servo.write (90);
  
  driveForward = false;
  driveBackwards = false;
  servoTurn = false;
  motorBrake= true;
  lostCon = 0;
  // Initialise RF receiver)
  //receiveRF.enableReceive(5);
 /* 
  vw_set_rx_pin(RF_RECEIVE_PIN);
  vw_setup(2000);  // Bits per sec
  vw_rx_start();   // Start the receiver PLL running
*/
 
}

void loop() {  
  int cmd;
  int val;
  
  if( bluetooth.available() ){       // if data is available to read
    cmd = readData();
    Serial.println("COMMANDS : ");
    Serial.println(cmd);
    val = readData();
    Serial.println("VALUES : ");
    Serial.println(val);
    demask(cmd);
    if(servoTurn){  
     turnServo(val);
    }else if (driveForward){
      dcMotor->setSpeed(val);
      dcMotor->run(FORWARD);
    }else if (driveBackwards){
      dcMotor->setSpeed(val);
      dcMotor->run(BACKWARD);
      
    }else if (motorBrake){
    }
    delay (50);
    lostCon = 0;
  }
  else {
    if (lostCon == 20)
    {
      dcMotor->setSpeed(0);
      dcMotor->run(FORWARD);
      dcMotor->run(RELEASE);
      servo.write (90);
    }
    delay (50);
    lostCon++;
  }
  /*
  //RF stuff
  if (receiveRF.available())
  {
    unsigned long id = receiveRF.getReceivedValue();
    uint8_t message = (uint8_t) receiveRF.getReceivedValue();
    if (message != 0 )
    {
      bluetooth.write(message);
    }
  }
  */
  
  /*
  uint8_t buf[5];  //index 4 is message, 0-3 is ID. 
  uint8_t buflen = 5;
  if (vw_get_message(buf, &buflen)) //TODO: Check ID and see if message is relevant. 
    bluetooth.write(buf[4]);
*/
  //Crash sensor stuff
  if(sensorIsActive){
    if(digitalRead(CRASH_SENSOR_PIN) == HIGH){
      //Serial.print(sensorIsActive);
      bluetooth.write('L');
      //Serial.print("L");
      sensorIsActive = false;
    }
  }else
    if(digitalRead(CRASH_SENSOR_PIN) == LOW)
      sensorIsActive = true;
}

int readData(){
  int data = (int)bluetooth.read();
  return data;
}

void brake(){
  dcMotor->run(RELEASE);
}
void turnServo(int val){
  //servo.write ((int)(val*8.333333)+750); 
  servo.write(val);
  Serial.println("Servo vrid");
  Serial.println(val);
}

void demask(int cmd){
  int temp = cmd & 0b00011111;
  if (temp == 1){
    driveForward =false;
    driveBackwards = true;
    motorBrake = false;
    servoTurn = false;
  }else if (temp == 3){
    driveForward =true;
    driveBackwards = false;
    motorBrake = false;
    servoTurn = false;
  }else if (temp == 0){
    driveForward =false;
    driveBackwards = false;
    motorBrake = false;
    servoTurn = true;
  }else if (temp == 4){
    driveForward =false;
    driveBackwards = false;
    motorBrake = true;
    servoTurn = false;
  }
  /*else if (temp == 8)
    {
      bluetooth.print("$$$");  // Print three times individually // Enter command mode
      bluetooth.println("K,");
    }
    */
    
    }
  

  
