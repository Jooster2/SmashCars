#include <SoftwareSerial.h>

#define smashButton 7
#define bluetoothTx 2
#define bluetoothRx 3

//SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);
volatile int state = LOW;

void setup() {
  Serial.begin(9600);
  //bluetooth.begin(9600);
  pinMode(smashButton, OUTPUT);
  attachInterrupt(digitalPinToInterrupt(smashButton), smash, RISING);

}

void loop() {
  digitalWrite(smashButton, state);
  delay(50);

}

void smash() {
  //bluetooth.write('L');
  //Serial.println("walla");
  Serial.write('L');
}

