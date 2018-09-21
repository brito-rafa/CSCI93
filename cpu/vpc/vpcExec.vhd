library ieee;
use ieee.std_logic_1164.all;
--use ieee.numeric_std.all;
library cscie93;
use cscie93.all;
library vpcAlu;
use work.vpc_defs.all; -- all definitions of vpc here
entity vpcExec is
   port (
	-- CLOCK
	clk50mhz : in std_logic;
	sysclk1 : in std_logic;
	exec_enable: in std_logic;
	-- PC
	PriorExecPC: in IMem_addr;
	AfterExecPC: out IMem_addr;
	-- memory
	mem_addr: in IMem_addr;
	mem_data_read_in : in vpcregister;
	-- IR/Instruction
	ExecInstruction: in vpcword;
	-- Registers
	ExecRegisters: in RegisterFile;
	AfterExecRegisters: out RegisterFile;
	InstructionExecuted: out std_ulogic
   ); end vpcExec;

architecture default of vpcExec is

  signal PCIncr, Alu_Out: std_logic_vector(word_width-1 downto 0);  
  signal inputreg_a: vpcregister;  
  signal inputreg_b: vpcregister;  
  signal funct: alu_functs;  
  signal Result: std_ulogic;  

begin

   PCadd: vpcAlu.vpcAlu port map (
	Reg_a => x"0000" & PriorExecPC,
	Reg_b => x"00000004",
	funct => RADD,
	Alu_Out => PCIncr,
	Result => Result
	 );

   -- this will run all the operations
   AluOpr: vpcAlu.vpcAlu port map (
	Reg_a => inputreg_a,
	Reg_b => inputreg_b,
	funct => funct,
	Alu_Out => Alu_Out,
	Result => Result
	 );

  ExecInst:
    process is
	variable myvpcregisters: RegisterFile := ExecRegisters;
	variable myPC: IMem_addr := PriorExecPC;
	variable myInstructionExec : std_ulogic  := '0';
    begin
	wait until falling_edge(sysclk1);
	-- setting the signals first
	myInstructionExec := '0';
	myvpcregisters(z0) := x"00000000"; -- there is no protection to avoid z0 get values, so we are enforcing every interaction
	case ExecInstruction.instr.opcode is
		when ALU =>
			-- shifters are a r-type instruction with small immediate (5 bits)
			if (ExecInstruction.instr.alu_funct = SRSRL or ExecInstruction.instr.alu_funct = SRSLL) then
				inputreg_a <= myvpcregisters(ExecInstruction.instr.vR2);
				inputreg_b <= x"000000"&'0'&'0'&'0'&ExecInstruction.raw_instruction(10 downto 6);
				funct <= ExecInstruction.instr.alu_funct;
			else			
				inputreg_a <= myvpcregisters(ExecInstruction.instr.vR2);
				inputreg_b <= myvpcregisters(ExecInstruction.instr.vR3);
				funct <= ExecInstruction.instr.alu_funct;
			end if;
		when IADD =>
			inputreg_a <= myvpcregisters(ExecInstruction.instr.vR2);
			inputreg_b <= x"0000" & ExecInstruction.instr.immediate;
			funct <= RADD;
		when ISUB =>
			inputreg_a <= myvpcregisters(ExecInstruction.instr.vR2);
			inputreg_b <= x"0000" & ExecInstruction.instr.immediate;
			funct <= RSUB;
		when IAND =>
			inputreg_a <= myvpcregisters(ExecInstruction.instr.vR2);
			inputreg_b <= x"0000" & ExecInstruction.instr.immediate;
			funct <= RAND;
		when IOR =>
			inputreg_a <= myvpcregisters(ExecInstruction.instr.vR2);
			inputreg_b <= x"0000" & ExecInstruction.instr.immediate;
			funct <= VROR;
		when INOR =>
			inputreg_a <= myvpcregisters(ExecInstruction.instr.vR2);
			inputreg_b <= x"0000" & ExecInstruction.instr.immediate;
			funct <= RNOR;
		when IXOR =>
			inputreg_a <= myvpcregisters(ExecInstruction.instr.vR2);
			inputreg_b <= x"0000" & ExecInstruction.instr.immediate;
			funct <= RXOR;
		when ISLT =>
			inputreg_a <= myvpcregisters(ExecInstruction.instr.vR2);
			inputreg_b <= x"0000" & ExecInstruction.instr.immediate;
			funct <= RSLT;
		when others =>
			myInstructionExec := '0';
	end case;
	if exec_enable = '1' then
		case ExecInstruction.instr.opcode is
			when ALU =>
				myPC := PCIncr(IMem_addr_width-1 downto 0);
--				myvpcregisters(ExecInstruction.instr.vR1) := Alu_Out(word_width-1 downto 0);
				myvpcregisters(ExecInstruction.instr.vR1) := Alu_Out;
			when IADD|ISUB|IAND|IOR|INOR|ISLT =>
				myPC := PCIncr(IMem_addr_width-1 downto 0);
				myvpcregisters(ExecInstruction.instr.vR1) := Alu_Out;
				--myvpcregisters(ExecInstruction.instr.vR1) := Alu_Out(word_width-1 downto 0);
			when SILU =>
				myPC := PCIncr(IMem_addr_width-1 downto 0);
				myvpcregisters(ExecInstruction.instr.vR1)(word_width-1 downto word_width-16) := ExecInstruction.instr.immediate;
			when SIBEQ =>
				if myvpcregisters(ExecInstruction.instr.vR1) = myvpcregisters(ExecInstruction.instr.vR2) then
					myvpcregisters(ra) := PCIncr(word_width-1 downto 0);
					myPC := ExecInstruction.instr.immediate;
				else	
					myPC := PCIncr(IMem_addr_width-1 downto 0);
				end if;
			when SIBNEQ =>
				if myvpcregisters(ExecInstruction.instr.vR1) = myvpcregisters(ExecInstruction.instr.vR2) then
					myPC := PCIncr(IMem_addr_width-1 downto 0);
				else	
					myvpcregisters(ra) := PCIncr(word_width -1 downto 0);
					myPC := ExecInstruction.instr.immediate;
				end if;
			when SRJAL =>
					myvpcregisters(ra) := PCIncr(word_width -1 downto 0);
					myvpcregisters(ExecInstruction.instr.vR1) := PCIncr(word_width -1 downto 0);
					myvpcregisters(ExecInstruction.instr.vR2) := PCIncr(word_width -1 downto 0);
					myPC := ExecInstruction.instr.immediate;
			when JEQ =>
				if myvpcregisters(ExecInstruction.instr.vR2) = myvpcregisters(ExecInstruction.instr.vR3) then
					myPC := myvpcregisters(ExecInstruction.instr.vR1)(15 downto 0);
				else	
					myPC := PCIncr(IMem_addr_width-1 downto 0);
				end if;
			when JNEQ =>
				if myvpcregisters(ExecInstruction.instr.vR2) = myvpcregisters(ExecInstruction.instr.vR3) then
					myPC := PCIncr(IMem_addr_width-1 downto 0);
				else	
					myPC := myvpcregisters(ExecInstruction.instr.vR1)(15 downto 0);
				end if;
			when JR =>
					myPC := myvpcregisters(ExecInstruction.instr.vR1)(15 downto 0);
			when LW =>
				myvpcregisters(ExecInstruction.instr.vR1) := mem_data_read_in;
				myvpcregisters(ExecInstruction.instr.vR2) := x"0000"&mem_addr;
				myPC := PCIncr(IMem_addr_width-1 downto 0);
			when SW =>
				-- data was already written in memory
				myvpcregisters(ExecInstruction.instr.vR2) := x"0000"&mem_addr;
				myPC := PCIncr(IMem_addr_width-1 downto 0);
			when LR =>
				myvpcregisters(ExecInstruction.instr.vR1) := mem_data_read_in;
				myPC := PCIncr(IMem_addr_width-1 downto 0);
			when WSR =>
				-- data already written
				myPC := PCIncr(IMem_addr_width-1 downto 0);
			when UNDEF =>
				--myPC := PCIncr(IMem_addr_width-1 downto 0);
				myPC := x"FFFF";
			when others =>
				--myPC := PCIncr(IMem_addr_width-1 downto 0);
				myPC := x"FFFF";
				
		end case;
		myvpcregisters(z0) := x"00000000"; -- there is no protection on compiler to avoid z0 get values, so we are enforcing every interaction
		AfterExecPC <= myPC;
		AfterExecRegisters <= myvpcregisters;
		myInstructionExec := '1';
	end if;
	InstructionExecuted <= myInstructionExec;
    end process; 
end;
