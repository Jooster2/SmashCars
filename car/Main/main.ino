#define FORWARD 0
#define BACKWARD 1
#define SERVO 2
#define BRAKE 3
#define RF_RECEIVE_PIN 11

#include <Servo.h>
#include <SoftwareSerial.h>  
#include <VirtualWire.h>

boolean driveForward;
boolean driveBackwards;
boolean servoTurn;
boolean motorBrake;
int bluetoothTx = 2;  // TX-O pin of bluetooth mate, Arduino D2
int bluetoothRx = 3;  // RX-I pin of bluetooth mate, Arduino D3
int motorDir = 13;
int motor = 11;
int motorBrakePin = 8;
int servoPin = 7;
Servo servo;
SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);

 
void setup() {
  Serial.begin(9600);// Debugging only
  Serial.println("setup");
 
  bluetooth.begin(115200);  // The Bluetooth Mate defaults to 115200bps
  bluetooth.print("$");  // Print three times individually
  bluetooth.print("$");
  bluetooth.print("$");  // Enter command mode
  delay(100);  // Short delay, wait for the Mate to send back CMD
  bluetooth.println("U,9600,N");  // Temporarily Change the baudrate to 9600, no parity
  // 115200 can be too fast at times for NewSoftSerial to relay the data reliably
  bluetooth.begin(9600);  // Start bluetooth serial at 9600
  pinMode (motorDir, OUTPUT);
  pinMode (motor, OUTPUT);
  pinMode (motorBrakePin, OUTPUT);
  
  servo.attach (servoPin);
  servo.write (90);
  
  driveForward = false;
  driveBackwards = false;
  servoTurn = false;
  motorBrake= true;

  // Initialise RF receiver)
  vw_set_rx_pin(RF_RECEIVE_PIN);
  vw_setup(2000);  // Bits per sec
  vw_rx_start();   // Start the receiver PLL running
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
      motorDrive(FORWARD,val);
    }else if (driveBackwards){
      motorDrive(BACKWARD ,val);
    }else if (motorBrake){
    }
    delay (50);
  }
  else {
  }

  //RF stuff
  uint8_t buf[5];  //index 4 is message, 0-3 is ID. 
  uint8_t buflen = 5;
  if (vw_get_message(buf, &buflen)) //TODO: Check ID and see if message is relevant. 
    bluetooth.write(buf[4]);
}

int readData(){
  int data = (int)bluetooth.read();
  return data;
}

void motorDrive(int dir, int val){
  if (dir = 0){
    digitalWrite (motorDir, HIGH); 
  }
  else {
    digitalWrite (motorDir, LOW);
  }
  digitalWrite (motorBrake, LOW);
  analogWrite(motor, val);
}

void brake(){
  digitalWrite (motorBrakePin, HIGH);
}
void turnServo(int val){
  servo.write (val); 
  Serial.println("Servo vrid");
  Serial.println(val);
}

void demask(int cmd){
  int temp = cmd & 0b00000111;
  if (temp == 1){
    driveForward =true;
    driveBackwards = false;
    motorBrake = false;
    servoTurn = false;
  }else if (temp == 3){
    driveForward =false;
    driveBackwards = true;
    motorBrake = false;
    servoTurn = false;
  }else if (temp == 0){
    driveForward =false;
    driveBackwards = false;
    motorBrake = false;
    servoTurn = true;
  }else if (temp = 4){
    driveForward =false;
    driveBackwards = false;
    motorBrake = true;
    servoTurn = false;
  }
}
  
