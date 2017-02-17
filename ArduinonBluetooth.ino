//Lisätään arduino genuino 101:sen bluetooth kirjasto
#include <CurieBLE.h>
//Lisätään lämpömittarin oma kirjasto
#include <dht11.h>
dht11 DHT;
#define DHT11_PIN 4 //Määritellään lämpömittarin pinni

// create peripheral instance
BLEPeripheral blePeripheral; 

//Luodaan palvelu ja parametrinä uuid
BLEService bluetoothService("19B10010-E8F2-537E-4F6C-D104768A1214");

//Luodaan palvelulle ominaisuus käyttää int tyyppisiä arvoja ja annetaan muille mahdollisuus lukea ja kirjoittaa tietoa palveluun
BLEIntCharacteristic intCharacteristic("19B10011-E8F2-537E-4F6C-D104768A1214", BLERead |  BLEWrite);

int lampotila = 0; //Mitattu lämpötila
int syotettyArvo = 0; //Voi syottää oman arvon tarvittaessa

void setup() {
  Serial.begin(9600);

  // Määritellään laitteen nimi
  blePeripheral.setLocalName("Sininenhammas");

  // Luodaan palvelu ja sille ominaisuus
  blePeripheral.addAttribute(bluetoothService);
  blePeripheral.addAttribute(intCharacteristic);

  intCharacteristic.setValue(0);

  // Aloitetaan palvelu
  blePeripheral.begin();

  Serial.println("Bluetooth device active, waiting for connections...");
}

void loop() {
  Serial.println("Odotetaan yhteytta...");
  delay(1000);
  
  //Alkaa kuuntelemaan haluavatko muut laitteet yhdistää arduinoon
  BLECentral central = blePeripheral.central();

  //Yhdisttiin laitteeseen
  if(central){
    Serial.print("Yhdistettiin laitteeseen: ");
    // tulostetaan yhdistetyn laitteen laiteosoite
    Serial.println(central.address());

    //Loopataan niin kauan kun laite on yhdistettynä
    while(central.connected()){     
      DHT.read(DHT11_PIN);
      
      lampotila = DHT.temperature,1;
      
      intCharacteristic.setValue(lampotila);
      Serial.print("Lampotila: ");
      Serial.println(lampotila);

      if(Serial.available() > 0){
        syotettyArvo = Serial.parseInt();
        
        intCharacteristic.setValue(syotettyArvo);
        Serial.println(syotettyArvo);
      }
      delay(5000);
    }  
  }   
}
