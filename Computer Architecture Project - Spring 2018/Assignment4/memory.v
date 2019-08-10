module memory(clk,reset,address,data_in,data_out,rwn,start,ready,address_test1,address_test2,address_test3,data_test1,data_test2,data_test3);
	input clk,reset,start,rwn;
	input [31:0] address,address_test1,address_test2,address_test3;
	input [31:0] data_in;
	output [31:0] data_test1,data_test2,data_test3;
	output reg [31:0] data_out;
	output ready;
	reg [7:0] array[255:0];
	reg state;
	reg [7:0] ad_t;
	reg [31:0] data_t;
	reg [1:0] counter;
	reg rwn_t;
	integer i;
	assign ready=~state;
	assign data_test1={array[(address_test1[7:0]+3)%256][7:0], array[(address_test1[7:0]+2)%256][7:0], array[(address_test1[7:0]+1)%256][7:0], array[address_test1[7:0]][7:0]};
	assign data_test2={array[(address_test2[7:0]+3)%256][7:0], array[(address_test2[7:0]+2)%256][7:0], array[(address_test2[7:0]+1)%256][7:0], array[address_test2[7:0]][7:0]};
	assign data_test3={array[(address_test3[7:0]+3)%256][7:0], array[(address_test3[7:0]+2)%256][7:0], array[(address_test3[7:0]+1)%256][7:0], array[address_test3[7:0]][7:0]};
	always @(posedge clk or posedge reset)
	begin
		if(reset) begin
			for(i=12; i<256; i=i+1) begin
				array[i] <= 8'b0000_0000;
			end
			//PC
			array[0] <= 8'hC4;
			array[1] <= 8'h15;
			array[2] <= 8'h00;
			array[3] <= 8'h01;
			array[4] <= 8'h10;
			array[5] <= 8'h00;
			array[6] <= 8'h10;
			array[7] <= 8'h02;
			array[8] <= 8'h10;
			array[9] <= 8'h01;
			array[10] <= 8'hB6;
			array[11] <= 8'h00;
			array[12] <= 8'h01;
			array[13] <= 8'h60;
			array[14] <= 8'h36;
			array[15] <= 8'h03;
			
			array[20] <= 8'h00;
			array[21] <= 8'h03;
			array[22] <= 8'h00;
			array[23] <= 8'h02;
			array[24] <= 8'h15;
			array[25] <= 8'h01;
			array[26] <= 8'h15;
			array[27] <= 8'h02;
			array[28] <= 8'h80;
			array[29] <= 8'hAC;
			
			//CPP
			array[64] <= 8'h14;
			array[68] <= 8'h14;
			
			//LV
			array[128] <= 8'h01;
			array[132] <= 8'h0A;
			
			//SP
			array[192] <= 8'h01;
			
			
			//for(i=67; i<256; i=i+1) begin
				//array[i] <= 8'b0000_0000;
			//end
			
			state=0;
		end
		else if(start & ~state) begin
			ad_t=address[7:0];
			rwn_t=rwn;
			data_t=data_in;
			counter=address[1:0];
			state=1;
		end
		else if(|counter && state)
			counter=counter-1;
		else if(state) begin
			if(rwn_t)
				data_out={array[(ad_t+3)%256], array[(ad_t+2)%256], array[(ad_t+1)%256], array[ad_t]};
			else if(~rwn_t) begin
				array[ad_t]   <= data_t[7:0];
				array[(ad_t+1)%256] <= data_t[15:8];
				array[(ad_t+2)%256] <= data_t[23:16];
				array[(ad_t+3)%256] <= data_t[31:24];
			end
			state=0;
		end
	end
endmodule
