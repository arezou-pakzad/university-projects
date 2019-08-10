import sys
registers1 = {} #registers' value from previous clock cycle
registers2 = {} # registers' value from current clock cycle
print_registers = {} #registers' value from current clock cycle to be printed(inout values not included)
blocks = {} #a dictionary to keep ASM Blocks
clk = 0
clk_limit = 1000
output_list = [] #a list to check every output line to determine the ending of the code
output_str = ''
currentBlock = ''
x = False #used in decision boxes
#******************************************

#Greater than
def G(a, b):
    global x
    if registers1.__contains__(a):
        if registers1.__contains__(b):
            x = (registers1[a] > registers1[b])
        else:
            if registers1[a] > b:
                x = True
            else:
                x = False
    elif registers1.__contains__(b):
        if a > registers1[b]:
            x = True
        else:
            x = False
    else:
        if a > b:
            x = True
        else:
            x = False

#Less than
def L(a, b):
    global x
    if registers1.__contains__(a):
        if registers1.__contains__(b):
            x = (registers1[a] < registers1[b])
        else:
            if registers1[a] < b:
                x = True
            else:
                x = False
    elif registers1.__contains__(b):
        if a < registers1[b]:
            x = True
        else:
            x = False
    else:
        if a < b:
            x = True
        else:
            x = False

#Equal to
def E(a, b):
    global x
    if registers1.__contains__(a):
        if registers1.__contains__(b):
            x = (registers1[a] == registers1[b])
        else:
            if registers1[a] == b:
                x = True
            else:
                x = False
    elif registers1.__contains__(b):
        if a == registers1[b]:
            x = True
        else:
            x = False
    else:
        if a == b:
            x = True
        else:
            x = False

#Shift Left
def shl(a, offset):
    b = registers1[a]
    if registers1.__contains__(offset):
        b *= pow(2, registers1[offset])
    else:
        b *= pow(2, int(offset))
    return b

#Shift Right
def shr(a, offset):
    b = registers1[a]
    if registers1.__contains__(offset):
        b /= pow(2, registers1[offset])
    else:
        b /= pow(2, int(offset))
    return b

#negetive
def neg(a):
    b = registers1[a]
    b = -1 * b
    return b

#bitwise NOT
def NOT(a):
    b = registers1[a]
    b = ~(b)
    return b

#Access a certain bit of a register
def getBit(a, offset):
    global x
    mask = 1 << offset
    if registers1.__contains__(a):
        b = registers1[a]
    else:
        b = a
    if (b & int(mask)) != 0:
        x = 1
    else:
        x = 0
    
#Access a certain register(used in decision boxes)
def check(a):
    global x
    if registers1.__contains__(a):
        b = registers1[a]
    else:
        b = a
    if b > 0:
        x = True
    else:
        x = False

#Reduction OR
def OR(a):
    global x
    b = registers1[a]
    if b != 0:
        x = 1
    else:
        x = 0

#Reduction AND
def AND(a):
    global x
    b = registers1[a]
    if b == 0:
        x = 0
    result = b.bit_length()
    while b != 0:
        result -= b % 2
        b >>= 1
    if result == 0:
        x = 1
    else:
        x = 0

#Load a register
def load(a, b):
    if registers1.__contains__(b):
        registers2[a] = registers1[b]
    else:
        registers2[a] = b

#add des, r1, r2
def add(a, b, c):
    if registers1.__contains__(b):
        if registers1.__contains__(c):
            registers2[a] = registers1[b] + registers1[c]
        else:
            registers2[a] = registers1[b] + c
    elif registers1.__contains__(c):
        registers2[a] = b + registers1[c]
    else:
        registers2[a] = b + c

#sub des, r1, r2
def sub(a, b, c):
    if registers1.__contains__(b):
        if registers1.__contains__(c):
            registers2[a] = registers1[b] - registers1[c]
        else:
            registers2[a] = registers1[b] - c
    elif registers1.__contains__(c):
        registers2[a] = b - registers1[c]
    else:
        registers2[a] = b - c
#************************************************************

#get input values from user
def get_inputs():
    for i in registers1.keys():
        str = "Please enter " + i + ": "
        registers1[i] = float(input(str))

#find all the registers in the code
def find_registers_inputs():
    for i in blocks.values():
        elements = i
        for j in range(len(elements)):
            if elements[j].startswith('load'):
                k = 6
                regName = ''
                while (elements[j][k] != "'"):
                    regName = regName + elements[j][k]
                    k += 1
                registers1[regName] = '-'
                print_registers[regName] = '-'
#**********************************************************

def run_stateBox():
    global clk
    global currentBlock
    global clk_limit
    print_newLine()
    clk += 1
    if clk > clk_limit:
        exit_code()
    code_lines = blocks[currentBlock]
    if code_lines[0] == 'NOP/':
        run_decisionBox(1)
    else:
        run_conditionalBox(0)

def run_conditionalBox(lineNum):
    global currentBlock
    code_lines = blocks[currentBlock]
    if code_lines[lineNum] == 'NOP/':
        lineNum += 1
        run_decisionBox(lineNum)
    while not(code_lines[lineNum].endswith('/') or code_lines[lineNum].__contains__('->')):
        exec(code_lines[lineNum])
        lineNum += 1
    if code_lines[lineNum].endswith('/'):
        code_str = code_lines[lineNum][:-1]
        exec(code_str)
        lineNum += 1
        run_decisionBox(lineNum)
    else:
        code_str = ''
        i = 0
        for i in range(len(code_lines[lineNum])):
            if code_lines[lineNum][i] == '-' and code_lines[lineNum][i + 1] == '>':
                break
            code_str += code_lines[lineNum][i]
        exec(code_str)
        currentBlock = code_lines[lineNum][i + 2:]
        run_stateBox()

def run_decisionBox(lineNum):
    global x
    global currentBlock
    code_lines = blocks[currentBlock]
    code_str = code_lines[lineNum][:-1]
    exec(code_str)
    if not x:
        lineNum += 1
        if code_lines[lineNum].endswith('?'):
            run_decisionBox(lineNum)
        elif code_lines[lineNum].startswith('NOP->'):
            currentBlock = code_lines[lineNum][5:]
            run_stateBox()
        else:
            run_conditionalBox(lineNum)
    else:
        level_counter = 1
        lineNum += 1
        while level_counter != 0:
            if code_lines[lineNum].endswith('?'):
                level_counter += 1
            elif code_lines[lineNum].__contains__('->'):
                level_counter -= 1
            lineNum += 1
        if code_lines[lineNum].endswith('?'):
            run_decisionBox(lineNum)
        elif code_lines[lineNum].startswith('NOP->'):
            currentBlock = code_lines[lineNum][5:]
            run_stateBox()
        else:
            run_conditionalBox(lineNum)
#**********************************************************

#after every clock cycle register2 values must be moved to register1
def move_registers():
    for key in registers2.keys():
        registers1[key] = registers2[key]
    for key2 in print_registers.keys():
        print_registers[key2] = registers1[key2]
    registers2.clear()

#prints the result of every clock cycle in console
def print_newLine():
    global clk
    global output_str

    move_registers()

    new_list = []
    print_str = 'Clock: ' + str(clk) + '\t'
    for key in print_registers.keys():
        print_str += str(key) + ': ' +  str(print_registers[key]) + '\t'
        new_list.append(print_registers[key])
    print_str += 'ASM Block: ' + currentBlock
    new_list.append(currentBlock)
    print(print_str)
    output_str += print_str + '\n'
    check_ending(new_list)

#checks if the simulation is finished
def check_ending(new_list):
    global output_list

    for i in range(len(output_list)):
        if check_equal(new_list, output_list[i]):
            exit_code()
    output_list.append(new_list)

#check twe lines in simulation table to be equal
def check_equal(new_list, output):
    if output == new_list:
        return True
    if not(output.__contains__('-')):
        return False
    for i in range(len(output)):
        if output[i] != new_list[i]:
            if output[i] == '-' or new_list[i] == '-':
                continue
            else:
                return False
    return True

import datetime
#writes the simulation results in a text file and exits the program
def exit_code():
    global path
    global output_str
    answer = input("Do you want to export output as a text file?(Y/N): ")
    if answer == 'Y':
        path = input("Enter output_file address:  ")
        output = open(path, 'w')
        now = datetime.datetime.now()
        now_time = now.strftime("%Y-%m-%d %H:%M")
        output.write('ASM Simulation Result\t' + now_time + '\n\n')
        output.write(output_str)
    sys.exit()
#******************************************************

path = input("Enter file address: ")
clk2 = input("Enter the number of clock cycles(enter -1 for default): ")
if int(clk2) != -1:
    clk_limit = int(clk2)
ASM_file = open(path, "r")

ASM_code = ASM_file.read()
ASM_code = str(ASM_code)
# print(ASM_code)

for char in '\t ':
    ASM_code = ASM_code.replace(char, '')

ASM_line = ASM_code.split('\n')

#putting blocks in a dictionary
for i in range(len(ASM_line)):
    if ASM_line[i].endswith(":"):
        blockName = ASM_line[i][:-1]
        insideBlock = []
        for j in range(i + 1, len(ASM_line)):
            if ASM_line[j].endswith(":"):
                break
            insideBlock.append(ASM_line[j])
        blocks[blockName] = insideBlock

#putting inputs in registers1 dictionary
for i in range(len(ASM_line)):
    if ASM_line[i].startswith("<<"):
        for j in range(i + 1, len(ASM_line)):
            if ASM_line[j].endswith(">>"):
                break
            registers1[ASM_line[j]] = '-'
        break
#finding the first ASM_Block to enter
for i in range(len(ASM_line)):
    if ASM_line[i].startswith("*"):
        currentBlock = ASM_line[i][1:]
        break

get_inputs()
find_registers_inputs()
run_stateBox()

