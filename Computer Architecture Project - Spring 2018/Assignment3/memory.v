module memory(clk,reset,address,data_in,data_out,rwn,start,ready,address_test1,address_test2,address_test3,data_test1,data_test2,data_test3);
	input clk,reset,start,rwn;
	input [31:0] address,address_test1,address_test2,address_test3;
	input [31:0] data_in;
	output [31:0] data_test1,data_test2,data_test3;
	output reg [31:0] data_out;
	output ready;
	reg [7:0] array[255:0];
	reg state;
	reg [15:0] ad_t;
	reg [31:0] data_t;
	reg [1:0] counter;
	reg rwn_t;
	integer i;
	assign ready=~state;
	assign data_test1={array[address_test1[15:0]+3][7:0], array[address_test1[15:0]+2][7:0], array[address_test1[15:0]+1][7:0], array[address_test1[15:0]][7:0]};
	assign data_test2={array[address_test2[15:0]+3][7:0], array[address_test2[15:0]+2][7:0], array[address_test2[15:0]+1][7:0], array[address_test2[15:0]][7:0]};
	assign data_test3={array[address_test3[15:0]+3][7:0], array[address_test3[15:0]+2][7:0], array[address_test3[15:0]+1][7:0], array[address_test3[15:0]][7:0]};
	always @(posedge clk or posedge reset)
	begin
		if(reset) begin
			for(i=0; i<65536; i=i+1) begin
				array[i] <= 8'b0000_0000;
			end
			state=0;
		end
		else if(start & ~state) begin
			ad_t=address[15:0];
			rwn_t=rwn;
			data_t=data_in;
			counter=address[1:0];
			state=1;
		end
		else if(|counter && state)
			counter=counter-1;
		else if(state) begin
			if(rwn_t)
				data_out={array[ad_t+3], array[ad_t+2], array[ad_t+1], array[ad_t]};
			else begin
				array[ad_t]   <= data_t[7:0];
				array[ad_t+1] <= data_t[15:8];
				array[ad_t+2] <= data_t[23:16];
				array[ad_t+3] <= data_t[31:24];
			end
			state=0;
		end
	end
endmodule
