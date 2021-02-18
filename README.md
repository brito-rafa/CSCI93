
![alt text](https://github.com/brito-rafa/CSCI93/blob/master/VPC.png)


# My academia code

In 2017 I decided (for pure passion) to take a hard core Computer Architecture software engineering course at Harvard Extension School.
The most recent syllabus of this class - now called CSCIE93 - is [here](https://cscie93.dce.harvard.edu/spring2020/index.html).

Vulture Platform Computer (VPC) is a project that creates a computer from scratch that produces the product of two numbers. Yeah, that's it. But it was the most difficult IT project that I have ever done.

Why?

It has its own chip architecture and instructions (largely based on MIPS), it has its own assembler language, a compiler and an emulator.
Once compiled, the binary code generated is runnable an [Intel Altera DE2-115 Development and Education FPGA board](https://www.terasic.com.tw/cgi-bin/page/archive.pl?Language=English&CategoryNo=165&No=502) with VHDL. All this in less than 3 months while working on a demanding full time job (lead engineering  one world's biggest banks in application containerization).

VPC Components:
- [compiler](https://github.com/brito-rafa/CSCI93/blob/master/asm/vpc-assembler/src/main/java/vpc/assembler/Assembler.java)
- [emulator](https://github.com/brito-rafa/CSCI93/blob/master/emu/vpc-emulator/src/main/java/vpc/emulator/Emulator.java)
- [Example of a VPC assembly code](https://github.com/brito-rafa/CSCI93/blob/master/asm/vpc-assembler/9-program.asm)
- [FPGA code in VHDL](https://github.com/brito-rafa/CSCI93/blob/master/cpu/vpc/vpc.vhd). 

You will find the full instructions of the VPC on the Operational here:
https://github.com/brito-rafa/CSCI93/blob/master/VPC-v22-Brito.pdf

Final Project (video) of the course is here:
https://www.youtube.com/watch?v=gvAxTfBhVyo


My final grade was A- :)


(*) Why Vulture? It is the mascot of my brazilian soccer team, [Flamengo](https://en.wikipedia.org/wiki/Clube_de_Regatas_do_Flamengo). The picture of king vulture on this README is from [O'Reilly SpamAssassin Book](https://www.oreilly.com/library/view/spamassassin/0596007078/) - this project has nothing to do with Spam, and I took the liberty to use its art - thank you so much!


