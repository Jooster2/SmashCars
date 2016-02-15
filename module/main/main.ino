

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
#define TRANSMIT_PIN 12
#define TRANSMIT_EN_PIN 3
#define POWERUP_SLEEP 15
#define TRAP_SLEEP 5

#define NR_OF_POWERUPS 8 //Including 'T' (trap). Make sure 'T' is always LAST in the array, never add anything after 'T'!
#define NR_OF TRAPS  6
uint8_t powerUps [NR_OF_POWERUPS] = {'B', 'I', 'N', 'S', 'R', 'W', 'b', 'T'}; //Boost speed, Immortality, New life point, Slow others, Reverse others, Wall others, Tripple speed boost, Trap 
uint8_t traps [NR_OF_TRAPS] = {'0', '1', '2', '3', '4', '5'}; //Slow, Lose life point, Self reverse, Wall self, Lock steering, Lock motor
uint8_t currentPowerUp;

MFRC522 rfid(SS_PIN, RST_PIN); // Instance of the class

MFRC522::MIFARE_Key key; 

// Init array that will store new NUID 
byte nuidPICC[4];

//**** MAIN FUNCTIONS ****//

void setup() {
  Serial.begin(9600);
  SPI.begin(); // Init SPI bus
  rfid.PCD_Init(); // Init MFRC522 
  rfid.PCD_SetAntennaGain(rfid.RxGain_max);
  // RF-module code----
  vw_set_tx_pin(TRANSMIT_PIN);
  //vw_set_rx_pin(receive_pin);
  vw_set_ptt_pin(TRANSMIT_EN_PIN);
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
  digitalWrite(GREENLED, HIGH);
  
  currentPowerUp = powerUps[random(NR_OR_POWERUPS - 1)]; //Generate any power up, but not trap. 
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

  //Do important stuff
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
 * Gives power up to the car, generates new power up, then sleeps for a while. 
 */
void carPassed(){
  sendPowerUpToCar();
  digitalWrite(GREENLED, LOW);
  digitalWrite(REDLED, HIGH);

  //Generate new power up or trap, then sleep. 
  currentPowerUp = powerUps[random(NR_OR_POWERUPS)];
  if(currentPowerUp == 'T'){
    currentPowerUp = traps[random(NR_OF_TRAPS)];
    delay(TRAP_SLEEP * 1000);
  }else{
    delay(POWERUP_SLEEP * 1000);
  }
  
  digitalWrite(REDLED, LOW);
  digitalWrite(GREENLED, HIGH);
}

void sendPowerUpToCar(){
  //Send effect 'currentPowerUp' to car with ID 'nuidPICC'.
  uint8_t  msg[5] = {nuidPICC[0], nuidPICC[1], nuidPICC[2], nuidPICC[3], currentPowerUp};

  vw_send(msg, 5);
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
