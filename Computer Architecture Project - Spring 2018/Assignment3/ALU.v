module ALU(Data_out,Z,N,Data1,Data2,control);
	
	output reg[31:0] Data_out;
	output reg N,Z;
	input[31:0] Data1,Data2;
	input[5:0] control;
	

	always @(Data1,Data2,control)
	begin
		N =1'b0;
		Z=1'b0;

		case(control)
			6'b011000: Data_out = Data1;
			6'b010100: Data_out = Data2;
			6'b011010: Data_out = ~Data1;
			6'b101100: Data_out = ~Data2;
			6'b111100: Data_out = Data1 + Data2;
			6'b111101: Data_out = Data1 + Data2 + 1;
			6'b111001: Data_out = Data1 + 1;
			6'b110101: Data_out = Data2 + 1;
			6'b111111: Data_out = Data2 - Data1;
			6'b110110: Data_out = Data2 - 1;
			6'b111011: Data_out = -Data1;
			6'b001100: Data_out = Data1 & Data2;
			6'b011100: Data_out = Data1 | Data2;
			6'b010000: Data_out = 0;
			6'b110001: Data_out = 1;
			6'b110010: Data_out = -1;
		endcase
		
		Z = ~|Data_out;
		N = Data_out[31];
	end

endmodule

