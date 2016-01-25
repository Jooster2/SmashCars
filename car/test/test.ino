#include <Servo.h>

Servo servo;
int input = 7;
int button = 0;
int angle = 20;

void setup() {
  // put your setup code here, to run once:
  servo.attach(2);
  pinMode(input, INPUT);
  servo.write(10);

}

void loop() {
  // put your main code here, to run repeatedly:
  /*int value = 100;
  analogWrite(fanPin, value);
  delay(30);
*/
  button = digitalRead(input);
  if(button == HIGH) {
    servo.write(angle);
    angle += 10;
  }
  delay(50);
  if(angle == 180) {
    angle = 10;
    servo.write(angle);
    delay(250);
  }
  
}
