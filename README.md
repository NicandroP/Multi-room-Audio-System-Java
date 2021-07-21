# Multi-room-Audio-System-Java
Project for the course of Context-Aware Systems


A Context-Aware platform of multi-room audiostreaming, that can reproduce musical file audio in the indoor location where the user is.
The platform includes features of location-awareness and neighbor-awareness.  
This system is composed of 3 logical components:


#### • User's device (smartphone or PC);
#### • Audio speaker (RA): one for each room, it can be dedicated hardware (traditional speaker) or from a PC;
#### • Localization and adaptment of contents Server (SLAC).



Specifically, the system: 
###### • Localize the user in an indoor environment, by recognizing the room in which he is located;
###### • Activate the streaming audio on the room's respective RA;
###### • Handle the migration of service, based on the user's mobility.
                          
                          
![](https://github.com/DaniMe98/Multi-room-Audio-System-Java-/blob/c2e7b8f25af2744b25e43f91569e3a9734c27176/multiroom_audio_system.png)

                          
Through the user's device, the user can select the audio file that he want to reproduce, among the ones stored in the SLAC.
Then, at regular intervals, the device sample constantly the Wi-Fi Access Point data present in the environment (es.  SSID and Received Power), then transfer the corrispondent feature at the SLAC.

The SLAC localize the user indoor (by recognizing the room) by using triangulation techniques and radio-fingerprinting together with some Machine Learning algorithms (such as Decision Tree, SVM...), then returns the results on the user's device.

Based on the location indoor, the SLAC determine which device is in the room where the user is, then run the streaming audio.
In case the user change room, the SLAC interrupt the streaming on the current RA, and redirect it to the new RA.
