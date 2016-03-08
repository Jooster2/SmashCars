/**
 * DualArduino.h
 */

#ifndef _dualArduino_h
#define _dualArduino_h

#include <iostream>
#include <Arduino.h>
#include <vector>

class DualArduino {
  public:
    DualArduino(int A, int B, int ACK, int data, bool receiver);
    ~DualArduino();

    void putData(char data);
    char getData();
    int checkIncoming();
    int checkOutgoing();
    
    bool send();
    bool recv();
  private:
    int a_pin;
    int b_pin;
    int ack_pin;
    int data_pin;
    
    bool receiver;
    bool usePinA;
    bool waitingForAck;
    bool waitingForNak;
    int amountTransferred;
    char dataInTransfer;
    
    std::vector<char> output_data;
    std::vector<char> input_data;

    
};



#endif
