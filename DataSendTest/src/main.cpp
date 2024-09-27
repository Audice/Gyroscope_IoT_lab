#include <Arduino.h>
#include "connection/bluetooth.h"

void setup()
{
  Serial.begin(115200);
  setupBluetooth();
}

void loop()
{
  loopBluetooth();
}