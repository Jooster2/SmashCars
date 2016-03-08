/**
 * DualArduino.cpp
 */

#include "DualArduino.h"

DualArduino::DualArduino(int A, int B, int ACK, int data, bool receiver) {
  a_pin = A;
  b_pin = B;
  ack_pin = ACK;
  data_pin = data;
  this->receiver = receiver;

  amountTransferred = 0;
  usePinA = true;
  waitingForAck = false;
  waitingForNak = false;

  if(receiver) {
    pinMode(a_pin, INPUT);
    pinMode(b_pin, INPUT);
    pinMode(ack_pin, OUTPUT);
    pinMode(data_pin, INPUT);
  } else {
    pinMode(a_pin, OUTPUT);
    pinMode(b_pin, OUTPUT);
    pinMode(ack_pin, INPUT);
    pinMode(data_pin, OUTPUT);
  }
}

DualArduino::~DualArduino() {
  
}

void DualArduino::putData(char data) {
  output_data.push_back(data);
}

char DualArduino::getData() {
  if(input_data.size() >= 1) {
    char temp = input_data.back();
    input_data.pop_back();
    return temp;
  } else {
    return (char)0;
  }
}

int DualArduino::checkIncoming() {
  return input_data.size();
}

int DualArduino::checkOutgoing() {
  return output_data.size();
}

bool DualArduino::send() {
  if(receiver == true) {
    throw "Configured as receiver, cannot send";
    return false;
  }
  if(output_data.size() <= 0)
    return false;
    
  if(waitingForAck) {
    if(digitalRead(ack_pin) == HIGH) {
      digitalWrite(a_pin, LOW);
      digitalWrite(b_pin, LOW);
      waitingForNak = true;
      waitingForAck = false;
    }
  } else if(waitingForNak) {
    if(digitalRead(ack_pin) == LOW) {
      waitingForNak = false;
    }
  } else if(!(waitingForAck || waitingForNak)) {
    if(amountTransferred == 0) {
      dataInTransfer = output_data.front();
    }

    digitalWrite(data_pin, dataInTransfer & 128);
    dataInTransfer << 1;
    if(usePinA) {
      digitalWrite(a_pin, HIGH);
    } else {
      digitalWrite(b_pin, HIGH);
    }
    waitingForAck = true;
    amountTransferred++;
    usePinA = !usePinA;
    
    if(amountTransferred >= 8) {
      output_data.erase(0);
      amountTransferred = 0;
    }
    
  }
}

bool DualArduino::recv() {
  if(receiver == false) {
    throw "Configured as sender, cannot receive";
    return false;
  }
  if(!waitingForAck) {
    if((usePinA && digitalRead(a_pin) == HIGH) || (!usePinA && digitalRead(b_pin) == HIGH)) {
      dataInTransfer << 1;
      if(digitalRead(data_pin) == HIGH) {
        dataInTransfer |= 1;
        digitalWrite(ack_pin, HIGH);
        waitingForAck = true;
        amountTransferred++;
      }
    }
  } else {
    if((usePinA && digitalRead(a_pin) == LOW) || (!usePinA && digitalRead(b_pin) == LOW)) {
      waitingForAck = false;
      digitalWrite(ack_pin, LOW);
      usePinA = !usePinA;
    }
  }

  if(amountTransferred >= 8) {
    input_data.push_back(dataInTransfer);
    dataInTransfer &= 0;
    amountTransferred = 0;
  }
  
}



