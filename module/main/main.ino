

/*Typical pin layout used:
 * -----------------------------------------------------------------------------------------
 *             MFRC522      Arduino       Arduino   Arduino    Arduino          Arduino
 *             Reader/PCD   Uno           Mega      Nano v3    Leonardo/Micro   Pro Micro
 * Signal      Pin          Pin           Pin       Pin        Pin              Pin
 * -----------------------------------------------------------------------------------------
 * RST/Reset   RST          9             5         D9         RESET/ICSP-5     RST
 * SPI SS      SDA(SS)      10            53        D10        10               10
 * SPI MOSI    MOSI         11 / ICSP-4   51        D11        ICSP-4           16
 * SPI MISO    MISO         12 / ICSP-1   50        D12        ICSP-1           14
 * SPI SCK     SCK          13 / ICSP-3   52        D13 
*/

#include <SPI.h>
#include <MFRC522.h>
#include <VirtualWire.h>

#define SS_PIN 53
#define RST_PIN 5
#define GREENLED 3
#define REDLED 2

//**** VARIABLES ***//
 
MFRC522 rfid(SS_PIN, RST_PIN); // Instance of the class

MFRC522::MIFARE_Key key; 

// Init array that will store new NUID 
byte nuidPICC[3];

const int transmit_pin = 12;
const int transmit_en_pin = 3;

//TODO: Change effects to chars or whatever to save space. 
//Using strings for readability for now. 
//String effects[5] = {"Speed boost", "New lifepoint", "Immortality", "Slow others", "Reverse others"};
//String currentEffect = "";


uint8_t effects [5] = {'B','N','I','S','R'};
uint8_t currentEffect;

//**** MAIN FUNCTIONS ****//

void setup() {
  Serial.begin(9600);
  SPI.begin(); // Init SPI bus
  rfid.PCD_Init(); // Init MFRC522 
  rfid.PCD_SetAntennaGain(rfid.RxGain_max);
    // RF-module code----
   vw_set_tx_pin(transmit_pin);
   //vw_set_rx_pin(receive_pin);
   vw_set_ptt_pin(transmit_en_pin);
   vw_set_ptt_inverted(true); // Required for DR3100
   vw_setup(2000);       // Bits per sec
  //-----------
  for (byte i = 0; i < 6; i++) {
    key.keyByte[i] = 0xFF;
  }

  /*Serial.println(F("This code scan the MIFARE Classsic NUID."));
  Serial.print(F("Using the following key:"));
  printHex(key.keyByte, MFRC522::MF_KEY_SIZE);*/

  randomSeed(analogRead(0));
  pinMode(REDLED, OUTPUT);
  pinMode(GREENLED, OUTPUT);
  digitalWrite(REDLED, LOW);
  digitalWrite(GREENLED, LOW);
  pinMode(13, OUTPUT);
  digitalWrite (13, LOW);
  //Wait a while before becoming active
  delay(5000);
  currentEffect = effects[random(5)];
  digitalWrite(GREENLED, HIGH);  
}

void loop() {
 checkRFID();
}

/**
 * Checks whether a car has passed over the module. Stores the NUID in an array.
 */
void checkRFID(){
  // Look for new cards
  if ( ! rfid.PICC_IsNewCardPresent())
    return;

  // Verify if the NUID has been readed
  if ( ! rfid.PICC_ReadCardSerial())
    return;

  Serial.print(F("PICC type: "));
  MFRC522::PICC_Type piccType = rfid.PICC_GetType(rfid.uid.sak);
  Serial.println(rfid.PICC_GetTypeName(piccType));

  // Check is the PICC of Classic MIFARE type
  if (piccType != MFRC522::PICC_TYPE_MIFARE_MINI &&  
    piccType != MFRC522::PICC_TYPE_MIFARE_1K &&
    piccType != MFRC522::PICC_TYPE_MIFARE_4K) {
    Serial.println(F("Your tag is not of type MIFARE Classic."));
    return;
  }
  
  // Store NUID into nuidPICC array
  for (byte i = 0; i < 4; i++) {
    nuidPICC[i] = rfid.uid.uidByte[i];
  }

  //Do stuff
  carPassed();
 
  /*Serial.println(F("The NUID tag is:"));
  Serial.print(F("In hex: "));
  printHex(nuidPICC, rfid.uid.size);
  Serial.println();
  Serial.print(F("In dec: "));
  printDec(rfid.uid.uidByte, rfid.uid.size);
  Serial.println();*/
    
  // Halt PICC (Stops the reading)
  rfid.PICC_HaltA();

  // Stop encryption on PCD
  rfid.PCD_StopCrypto1();
}

/**
 * Gives an effect to the car, sleeps for a while, then generates new effect. 
 */
void carPassed(){
  sendEffectToCar();
  digitalWrite(GREENLED, LOW);
  digitalWrite(REDLED, HIGH);
  delay(5000);
  
  currentEffect = effects[random(5)];
  digitalWrite(REDLED, LOW);
  digitalWrite(GREENLED, HIGH);
}

/** 
 *  TODO: use RF, XBee or whatever to send an effect to the car. 
 */
void sendEffectToCar(){
  //Send effect 'currentEffect' to car with ID 'nuidPICC'.
  uint8_t  msg[1] = {currentEffect};

  
  vw_send(msg, 1);
  vw_wait_tx();
  
}


//**** HELPER FUNCTIONS ****//

/**
 * Helper routine to dump a byte array as hex values to Serial. 
 */
void printHex(byte *buffer, byte bufferSize) {
  for (byte i = 0; i < bufferSize; i++) {
    Serial.print(buffer[i] < 0x10 ? " 0" : " ");
    Serial.print(buffer[i], HEX);
  }
}

/**
 * Helper routine to dump a byte array as dec values to Serial.
 */
void printDec(byte *buffer, byte bufferSize) {
  for (byte i = 0; i < bufferSize; i++) {
    Serial.print(buffer[i] < 0x10 ? " 0" : " ");
    Serial.print(buffer[i], DEC);
  }
}
