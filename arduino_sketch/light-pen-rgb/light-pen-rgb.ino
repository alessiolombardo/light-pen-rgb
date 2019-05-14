
#define S2 12
#define S3 11
#define sensorOut 13

char buffer[24];
int frequencyR=0;
int frequencyG=0;
int frequencyB=0;
int frequencyW=0;
int pausa=2;
void setup() {
  pinMode(S2, OUTPUT);
  pinMode(S3, OUTPUT);
  pinMode(sensorOut, INPUT); 
  Serial.begin(9600);
}

void loop() {
  
  //******** ROSSO ********
  digitalWrite(S2,LOW);
  digitalWrite(S3,LOW);
  frequencyR = pulseIn(sensorOut, LOW);
  if(frequencyR==0){ //Se il Rosso è 0, è inutile proseguire oltre. Trasmissione immediata.
    Serial.print("R00000G00000B00000W00000");
    return;
  }
  delay(pausa);
  //******** VERDE ********
  digitalWrite(S2,HIGH);
  digitalWrite(S3,HIGH);
  frequencyG = pulseIn(sensorOut, LOW); 
  delay(pausa);
  //******** BLU ********
  digitalWrite(S2,LOW);
  digitalWrite(S3,HIGH);
  frequencyB = pulseIn(sensorOut, LOW);  
  delay(pausa);
  //******** BIANCO ********
  digitalWrite(S2,HIGH);
  digitalWrite(S3,LOW);
  frequencyW = pulseIn(sensorOut, LOW); 
  delay(pausa); 
  //******** TRASMISSIONE ********
  sprintf(buffer, "R%05dG%05dB%05dW%05d", frequencyR,frequencyG,frequencyB,frequencyW);
  Serial.print(buffer);
}
