library ieee;
use ieee.std_logic_1164.all;

entity and02 is
  port (
    a, b : in  std_logic;
    q    : out std_logic);
end entity and02;

architecture dataflow of and02 is
begin
  q <= a and b;
end architecture dataflow;
