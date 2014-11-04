dif
===


DIF stands for Device Integration Framework.

I built this as part of a larger system to automate network labs.
The DIF was responsible for managing device specific behaviors.
Behaviors were encoded in an XML based domain specific language and 
executed as needed to perform tasks required by the rest of the system.
Such tasks included applying a configuration, changing parameters on an
interface port, or initiating a reboot.

Apart from its normal mode of running embedded within the rest of the
larger system, the DIF could run as a standalone command line tool
for development and testing.

The DIF largely corresponds to the "lab management subsystem" referred to
in the following  
  US Patent 7133906 - System and method for remotely configuring testing laboratories  
  http://patft.uspto.gov/netacgi/nph-Parser?Sect1=PTO1&Sect2=HITOFF&d=PALL&p=1&u=%2Fnetahtml%2FPTO%2Fsrchnum.htm&r=1&f=G&l=50&s1=7133906.PN.&OS=PN/7133906&RS=PN/7133906

