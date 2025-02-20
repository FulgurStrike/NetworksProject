# NetworksProject

Through testing our assumptions for DatagramSockets2,3 & 4 :

## DatagramSocket2 

- Random Packet loss : random occasional gaps seem to be appearing where packets are being lost, as an inconsistent rate
which may be due to network congestion or temporary network issues
- Burst Packet loss : whilst random, multiple occurrences of consecutive "-1" values of packets being lost are occurring 
in sequence such as [-1,-1],[-1,-1,-1], or [-1,-1,-1]. This may represent long periods of network congestion or problems
with the pathing that may result in multiple packets being dropped in a short time. 
- Possible buffer overflow : multiple "-1" values in varying parts of the array suggest possible buffer issues that may 
have rose when their buffers are full. This may be due to being to small to handle traffic or heavy network conditions.

- scattered "-1" throughout
- losses appear in short "bursts"
- irregular random packet loss 

## DatagramSocket3 

- consistent short bursts of successful packet sends 
- occasional intermittent losses in packets 
- delays and potential congestion occurs due to sending packets to the back of the "queue"/being dropped

- delays of packets 

## DatagramSocket4 

- constant changes in packets at random intervals, e.g. bursts of random changes may occur causing short bursts in 
the transmission where parts are modified 
- packets sent are completely unpredictable meaning packets may sometimes contain valid data and other times contain 
completely different byte values - simulating corruption 
- unlike DatagramSocket3 this doesn't really simulate "loss of packets" more corrupted 
- no specific delay however due to the randomness of modification it could result in an increase of time per packet
resulting leading to delays
- "randomness" seems to be linear with packet size - larger packet sizes seem to result in more random modifications, 
whereas smaller packets only see partial modifications 

- potential short bursts 
- packet loss in the form of corruption 
- random delays 
- unpredictable 