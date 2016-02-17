

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

#define NR_OF_EFFECTS 13 //Update this as we add more effects
uint8_t* effects; //An array containing all effects, power ups first, then traps
int trapIndex; //Indicates where in the array the traps start occurring. Initialized on setup.
int arrayLength;
uint8_t currentEffect;
bool isArmed; //Used for traps

//These are all effects, in order:
//Boost speed, Immortality, New life point, Slow others, Reverse others, Wall others, Tripple speed boost
//Slow, Lose life point, Self reverse, Wall self, Lock steering, Lock motor
uint8_t effectDatabase[] = {'B', 'I', 'N', 'S', 'R', 'W', 'b', '0', '1', '2', '3', '4', '5'}; //Make sure all traps are put LAST in the array!
byte effectPriority []    = {1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1}; //Higher number means higher chance o occurring.


MFRC522 rfid(SS_PIN, RST_PIN); // Instance of the class

MFRC522::MIFARE_Key key; 

// Init array that will store new NUID 
byte nuidPICC[4];

//**** MAIN FUNCTIONS ****//

void setup() {
  Serial.begin(9600);
  Serial.print("s");
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

  //Generate new array containing all effects, but with each char occurring as many times as its priority. 
  generateEffectsArray();
  isArmed = false;
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
  digitalWrite(GREENLED, LOW);
  digitalWrite(REDLED, HIGH);
  
  if(isArmed){ //If module contains a trap, send it to car, then sleep. 
    sendEffectToCar(currentEffect);      
    delay(POWERUP_SLEEP * 1000);    
    isArmed = false;
  }else{ //If module doesn't contain a trap, randomize new effect and send to car, then sleep
    currentEffect = effects[random(arrayLength)]; 

    if(isTrap(currentEffect)){ //If randomized effect is a trap, send only a 'T' to car, sleep for a short while, then mark module as armed. 
      sendEffectToCar('T');
      delay(TRAP_SLEEP * 1000);
      isArmed = true;
    }else{
      sendEffectToCar(currentEffect);
      delay(POWERUP_SLEEP * 1000);
    }
  } 
  digitalWrite(REDLED, LOW);
  digitalWrite(GREENLED, HIGH);
}

/**
 * Broadcast current effect and ID of the car that just passed . 
 */
void sendEffectToCar(uint8_t effect){
  //Send effect to car with ID 'nuidPICC'.
  uint8_t  msg[5] = {nuidPICC[0], nuidPICC[1], nuidPICC[2], nuidPICC[3], effect};

  vw_send(msg, 5);
  vw_wait_tx();
}

/*
 * Generates new array containing all effects, but with each char occurring as many times as its priority. 
 * Function is called only on startup.
 */
void generateEffectsArray(){
  byte i, j, k = 0, arraySize = 0;
  
  //Calculate how large the array has to be.
  for(i = 0; i < NR_OF_EFFECTS; i++)
    arraySize += effectPriority[i];
    
  //Create effects array
  effects = (uint8_t*)malloc(arraySize);

  trapIndex = 0;
  for(i = 0; i < NR_OF_EFFECTS;i++){ //For each effect...
    for(j = 0; j < effectPriority[i]; j++){
      effects[k++] = effectDatabase[i]; //...add that char to tmpArray as many times as its priority
      
      if(trapIndex == 0 && isTrap(effectDatabase[i])) //Set value of trapIndex, indicating where traps start occurring in the array. 
        trapIndex = k-1; 
    }
  }  
  arrayLength = arraySize; //Set size of effects array in global variable.
}

/**
 * Checks if an effect is a trap
 * TODO: If we add traps having other symbols than 0-9, change this function accordingly. 
 */
bool isTrap(uint8_t effect){
  return ((char)effect >= 0 && (char) effect <= 9 ); 
  //Alternative code: return -1 != "0123456789".indexOf((char)effect);
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
