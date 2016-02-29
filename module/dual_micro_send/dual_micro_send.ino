/*
 * This is the sending part of the dual micro protocol
 */

#define pinA 5
#define pinB 6
#define ackPin 7
#define dataPin 8

boolean sendOnA;
boolean getAck;
boolean receivedAck;
//uint8_t msg[5];
int msgIndexToSend;
int bitToSend;

//testing
uint8_t msg[] = {25, 65, 76, 45, 87};

void setup() {
  sendOnA = true;
  getAck = true;
  receivedAck = false;
  msgIndexToSend = 0;
  bitToSend = 128;
  pinMode(pinA, OUTPUT);
  pinMode(pinB, OUTPUT);
  pinMode(ackPin, INPUT);
  pinMode(dataPin, OUTPUT);
  

}

void loop() {
  dualMicroSend();
  delay(50);

}

void dualMicroSend() {
  if(bitToSend == 0) {
    msgIndexToSend++;
    bitToSend = 128;
  }
  if(msgIndexToSend >= 5)
    return;
    
  if(msg[msgIndexToSend] & bitToSend == 0)
    digitalWrite(dataPin, LOW);
  else
    digitalWrite(dataPin, HIGH);
    
  if(sendOnA)
    digitalWrite(pinA, HIGH);
  else
    digitalWrite(pinB, HIGH);
  //now we wait for ACK
  getAck = true;

  if(getAck && digitalRead(ackPin) == HIGH) {
    //we get here while waiting for ACK
    if(sendOnA)
      digitalWrite(pinA, LOW);
    else
      digitalWrite(pinB, LOW);
    getAck = false;
    receivedAck = true;
  } else if(!getAck && receivedAck && digitalRead(ackPin) == LOW) {
    //we get here after ACK has been reset
    receivedAck = false;
    //invert sendOnA
    sendOnA = !sendOnA;
    //get next bit to send
    bitToSend >> 1;
  }
    
}

