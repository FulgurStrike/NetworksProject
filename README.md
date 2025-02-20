# NetworksProject

Through testing our assumptions for DatagramSockets2,3 & 4 :

DatagramSocket2 

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