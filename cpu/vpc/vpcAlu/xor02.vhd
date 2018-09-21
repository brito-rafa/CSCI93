library ieee;
use ieee.std_logic_1164.all;

entity xor02 is
  port (
    a, b : in  std_logic;
    q    : out std_logic);
end entity xor02;

architecture dataflow of xor02 is
begin
  q <= a xor b;
end architecture dataflow;
