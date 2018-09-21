library ieee;
use ieee.std_logic_1164.all, ieee.numeric_std.all;

package vpc_defs is

   constant IMem_addr_width : positive := 16;

   subtype IMem_addr is std_logic_vector(IMem_addr_width -1 downto 0);

   subtype immediate_number is std_logic_vector(IMem_addr_width -1 downto 0);

   constant word_width : positive := 32;

   constant user_registers : positive := 32;

   subtype vpcregister is std_logic_vector(word_width - 1 downto 0);

   -- register file
   type registerFile is array(0 to user_registers - 1) of vpcregister;

   -- registers
        constant z0 : integer := 0;
        constant a0 : integer := 1;
        constant a1 : integer := 2;
        constant g0 : integer := 3;
        constant g1 : integer := 4;
        constant g2 : integer := 5;
        constant g3 : integer := 6;
        constant g4 : integer := 7;
        constant g5 : integer := 8;
        constant g6 : integer := 9;
        constant g7 : integer := 10;
        constant sp : integer := 11;
        constant fp : integer := 12;
        constant r0 : integer := 13;
        constant r1 : integer := 14;
        constant r2 : integer := 15;
        constant r3 : integer := 16;
        constant r4 : integer := 17;
        constant r5 : integer := 18;
        constant r6 : integer := 19;
        constant r7 : integer := 20;
        constant t0 : integer := 21;
        constant t1 : integer := 22;
        constant t2 : integer := 23;
        constant t3 : integer := 24;
        constant s0 : integer := 25;
        constant s1 : integer := 26;
        constant s2 : integer := 27;
        constant s3 : integer := 28;
        constant s4 : integer := 29;
        constant gp : integer := 30;
        constant ra : integer := 31;

   -- opcodes
   type opcodes is (ALU, LR, WSR, IADD, ISUB, IAND, IOR, INOR, IXOR, ISLT, SILU, LW, SW, SIBEQ, SIBNEQ, SRJAL, JR, JEQ, JNEQ, UNDEF);
   -- alu_funct
   type alu_functs is (RADD, RSUB, RAND, VROR, RNOR, RXOR, RXNOR, RSLT, SRSRL, SRSLL, UNDEF);

   type instruction is record
        opcode : opcodes;
        vR1, vR2, vR3 : integer range 0 to user_registers - 1;
        immediate : immediate_number;
	alu_funct : alu_functs;
   end record instruction;

   type vpcword is record
        instr : instruction;
        raw_instruction : vpcregister;
   end record vpcword;

   -- FSM states
   type States is (Initial, FetchAwaitMemoryInstruction, FetchReadingInstruction, ParseInstruction, ExecuteInstruction, ExecuteInstruction2, WriteBackAwaitMemory, WriteBackWriting, Written, FetchAwaitMemoryUserData, FetchReadingUserData, FetchedUserData);

   -- Functions
   function get_hex(constant number: std_logic_vector(3 downto 0)) return std_ulogic_vector;
   function get_opcode(constant number: std_logic_vector(5 downto 0)) return opcodes;
   function get_alufunct(constant number: std_logic_vector(5 downto 0)) return alu_functs;
   function zero_vpcregister(myregisterFile: registerFile) return registerFile;
   function return_int(constant number: std_logic_vector(4 downto 0)) return integer;

   -- components
   component regstd
      port(
	  clk, en, clear: in std_logic;
        d: in std_logic_vector(31 downto 0);
        q: out std_logic_vector(31 downto 0));
   end component regstd;


end vpc_defs;

package body vpc_defs is
   function get_hex(constant number: in std_logic_vector(3 downto 0)) return std_ulogic_vector is
	variable myhex_out: std_ulogic_vector(0 to 6); -- local variable
	begin
		case number is
			when x"0"=> myhex_out := "0000001";  -- '0'
			when x"1"=> myhex_out :="1001111";  -- '1'
			when x"2"=> myhex_out :="0010010";  -- '2'
			when x"3"=> myhex_out :="0000110";  -- '3'
			when x"4"=> myhex_out :="1001100";  -- '4' 
			when x"5"=> myhex_out :="0100100";  -- '5'
			when x"6"=> myhex_out :="0100000";  -- '6'
			when x"7"=> myhex_out :="0001111";  -- '7'
			when x"8"=> myhex_out :="0000000";  -- '8'
			when x"9"=> myhex_out :="0000100";  -- '9'
			when x"a"=> myhex_out :="0001000";  -- 'a'
			when x"b"=> myhex_out :="1100000";  -- 'b'
			when x"c"=> myhex_out :="0110001";  -- 'c'
			when x"d"=> myhex_out :="1000010";  -- 'd'
			when x"e"=> myhex_out :="0110000";  -- 'e'
			when x"f"=> myhex_out :="0111000";  -- 'f' 
			when others=> myhex_out :="1111111"; 
		end case;
		return myhex_out;
	end; -- function funct_hex

   function get_opcode(constant number: in std_logic_vector(5 downto 0)) return opcodes is
	variable myopcode_out: opcodes; -- local variable
	variable mynumber: std_logic_vector(7 downto 0);
	begin
		mynumber := "00" & number; -- padding two extra zeros to make number divisible by 8
		case mynumber is
			when x"00"=> myopcode_out := UNDEF; 
			when x"01"=> myopcode_out := IADD ; 
			when x"02"=> myopcode_out := ISUB ; 
			when x"03"=> myopcode_out := IAND ; 
			when x"04"=> myopcode_out := IOR ; 
			when x"05"=> myopcode_out := INOR ; 
			when x"06"=> myopcode_out := IXOR ; 
			when x"07"=> myopcode_out := ISLT ; 
			when x"08"=> myopcode_out := ALU ; -- ALU: will need to get the funct code 
			when x"0A"=> myopcode_out := SRJAL ; 
			when x"0B"=> myopcode_out := JR ; 
			when x"0C"=> myopcode_out := JEQ ; 
			when x"0D"=> myopcode_out := JNEQ ; 
			when x"10"=> myopcode_out := SILU; 
			--when x"11"=> myopcode_out := SILOAD; --deprecated
			--when x"12"=> myopcode_out := SISTORE; --deprecated
			when x"13"=> myopcode_out := SIBEQ; 
			when x"14"=> myopcode_out := SIBNEQ; 
			when x"15"=> myopcode_out := LW; 
			when x"16"=> myopcode_out := SW; 
			when x"17"=> myopcode_out := LR ; 
			when x"18"=> myopcode_out := WSR ; 
			when others=> myopcode_out := UNDEF; 
		end case;
		return myopcode_out;
	end; -- function get_opcode

   function get_alufunct(constant number: in std_logic_vector(5 downto 0)) return alu_functs is
	variable myalufunct_out: alu_functs; -- local variable
	variable mynumber: std_logic_vector(7 downto 0);
	begin
		mynumber := "00" & number; -- padding two extra zeros to make number divisible by 8
		case mynumber is
			when x"01"=> myalufunct_out := RADD ;
			when x"02"=> myalufunct_out := RSUB ;
			when x"03"=> myalufunct_out := RAND ;
			when x"04"=> myalufunct_out := VROR ; -- had to add "V" to this type.. ROR is a reserved word
			when x"05"=> myalufunct_out := RNOR ;
			when x"06"=> myalufunct_out := RXOR ;
			when x"07"=> myalufunct_out := RSLT ;
			when x"08"=> myalufunct_out := SRSRL ;
			when x"09"=> myalufunct_out := SRSLL ;
			when x"0A"=> myalufunct_out := RXNOR ;
			when others=> myalufunct_out := UNDEF; 
		end case;
		return myalufunct_out;
	end; -- function get_alufunct;
   function zero_vpcregister(myregisterFile: in registerFile) return registerFile is
	variable myotherregfile: registerFile;
   begin
	for n in 0 to user_registers - 1 loop
		myotherregfile(n) := x"00000000";
	end loop; 
	return myotherregfile;
   end; -- function zero_vpcregister

   function return_int(constant number: in std_logic_vector(4 downto 0)) return integer is
	variable myint : integer;
	variable mynumber: std_logic_vector(7 downto 0);
   begin
		mynumber := "000" & number; -- padding two extra zeros to make number divisible by 8
		case mynumber is
			when x"00"=> myint := 0 ;
			when x"01"=> myint := 1 ;
			when x"02"=> myint := 2 ;
			when x"03"=> myint := 3 ;
			when x"04"=> myint := 4 ;
			when x"05"=> myint := 5 ;
			when x"06"=> myint := 6 ;
			when x"07"=> myint := 7 ;
			when x"08"=> myint := 8 ;
			when x"09"=> myint := 9 ;
			when x"0A"=> myint := 10 ;
			when x"0B"=> myint := 11 ;
			when x"0C"=> myint := 12 ;
			when x"0D"=> myint := 13 ;
			when x"0E"=> myint := 14 ;
			when x"0F"=> myint := 15 ;
			when x"10"=> myint := 16 ;
			when x"11"=> myint := 17 ;
			when x"12"=> myint := 18 ;
			when x"13"=> myint := 19 ;
			when x"14"=> myint := 20 ;
			when x"15"=> myint := 21 ;
			when x"16"=> myint := 22 ;
			when x"17"=> myint := 23 ;
			when x"18"=> myint := 24 ;
			when x"19"=> myint := 25 ;
			when x"1A"=> myint := 26 ;
			when x"1B"=> myint := 27 ;
			when x"1C"=> myint := 28 ;
			when x"1D"=> myint := 29 ;
			when x"1E"=> myint := 30 ;
			when x"1F"=> myint := 31 ;
			when others => myint := 12; -- compiler complained for lack of others. Adding 12, which is a register not currently used
		end case;
		return myint;
   end; -- funtion return_int

end vpc_defs;
	
