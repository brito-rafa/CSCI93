library ieee;
use ieee.std_logic_1164.all;
--use ieee.numeric_std.all;
library cscie93;
use cscie93.all;
library vpcAlu;
use work.vpc_defs.all; -- all definitions of vpc here
entity vpcMemMux is
   port (
	PC_addr, register_addr: in IMem_addr;
	clk, memmux_enable: in std_logic;
	-- IR/Instruction
	ExecInstruction: in vpcword;
	-- output
	MemMux_out: out IMem_addr
   ); end vpcMemMux;

architecture default of vpcMemMux is
begin
  MemMuxInst:
    process is
    begin
	wait until falling_edge(clk);
	if memmux_enable = '1' then
		case ExecInstruction.instr.opcode is
			when LW|SW =>
				MemMux_out <= ExecInstruction.instr.immediate;
			when LR|WSR  =>
				MemMux_out <= register_addr;
			when others =>
				MemMux_out <= PC_addr;
		end case;
	end if;
    end process; 
end;
