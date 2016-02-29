/* This is the receiving end of the dual micro protocol.
 * 
 * 
 */

#define pinA 5
#define pinB 6
#define ackPin 7
#define dataPin 8

boolean receiveOnA;
char dataOne;
long dataTwo;
int amountReceived;

void setup() {
  receiveOnA = true;
  pinMode(pinA, INPUT);
  pinMode(pinB, INPUT);
  pinMode(ackPin, OUTPUT);
  pinMode(dataPin, INPUT);
  amountReceived = 0;

}

void loop() {
  if(digitalRead(pinA) == HIGH || digitalRead(pinB) == HIGH) {
    dualMicroReceive();
  }
  delay(50);
  
}

void dualMicroReceive() {
  if(amountReceived >= 48) {
    Serial.print("DataOne: ");
    Serial.println(dataOne);
    Serial.print("DataTwo: ");
    Serial.println(dataTwo);
    return;
  }
  if(receiveOnA) {
    if(digitalRead(pinA) == HIGH) {
      if(amountReceived >= 40) { 
        //receiving effect
        dataOne << 1;
        dataOne &= digitalRead(dataPin);
      } else {
        //receiving ID
        dataTwo << 1;
        dataTwo &= digitalRead(dataPin);
      }
      amountReceived++;
      Serial.println("Received somethingA");
      digitalWrite(ackPin, HIGH);
      
    } else if(digitalRead(pinA) == LOW) {
      digitalWrite(ackPin, LOW);
      receiveOnA = false;
    }
  } else { //if receiveOnA == false
     if(digitalRead(pinB) == HIGH) {
      if(amountReceived >= 40) { 
        //receiving effect
        dataOne << 1;
        dataOne &= digitalRead(dataPin);
      } else {
        //receiving ID
        dataTwo << 1;
        dataTwo &= digitalRead(dataPin);
      }
      amountReceived++;
      Serial.println("Received somethingB");
      digitalWrite(ackPin, HIGH);
      
    } else if(digitalRead(pinB) == LOW) {
      digitalWrite(ackPin, LOW);
      receiveOnA = false;
    }
  }
}


