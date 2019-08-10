class While_stmt:
    def __init__(self, number, start_index):
        self.number = number
        self.start_index = start_index

    def set_goto(self, next_stmt):
        self.next_stmt = next_stmt

    def get_number(self):
        return self.number

    def get_start_index(self):
        return self.start_index


class While_pointer:
    def __init__(self, stmt):
        self.stmt = stmt

    def get_stmt(self):
        return self.stmt

    def set_stmt(self, stmt):
        self.stmt = stmt


class Stack:
    def __init__(self):
        self.stack = []

    def push(self, item):
        self.stack.append(item)

    def pop(self, number):
        for i in range(number):
            self.stack.pop()

    def get_item(self, index):
        top = len(self.stack) - 1

        return self.stack[top - index]

    def get_top_index(self):
        return len(self.stack) - 1

    def get_len(self):
        return len(self.stack)


class Program_block:
    def __init__(self):
        self.program = ['' for i in range(10000)]
        self.index = 1


    def write(self, index, statement):
        self.program[index] = statement

    def increase_index(self):
        self.index += 1

    def decrease_index(self):
        self.index -= 1

    def print_self(self):
        file = 'output.txt'
        f = open(file, 'w+')
        for i in range(self.index):
            f.write((str(i) +  '    ' + str(self.program[i]) + '\n'))

        f.close()

class Data_block:
    def __init__(self):
        self.index = 0
        self.memory = [0 for i in range(10000)]
        self.temp_start = 8000
        self.temp_index = self.temp_start
        self.array_start = 5000
        self.array_index = self.array_start
        self.type_checker = [0 for i in range(10000)]


    def write(self, item):
        self.memory[self.index] = item
        self.index += 4
        return self.index - 4

    def get_index(self):
        return self.index

    def write(self, item, addr):
        self.memory[addr] = item

    def write_array(self, array, size):
        addr = self.write(array[0])
        for i in range(1, size):
            self.write(array[i])
        return addr

    def read(self, addr):
        return self.memory[addr]

    def get_temp(self):
        return_value = self.temp_index
        self.temp_index = (self.temp_index + 4) % self.temp_start + self.temp_start
        return return_value


    def write_array(self, array, size): #TODO!!!

        addr = self.array_index
        self.write(addr) #TODO
        for i in range(size):
            self.write(item= array[i], addr= self.array_index)
            self.array_index += 4
        return addr


    def write_array(self, size):
        addr = self.array_index
        self.array_index += 4 * size
        return addr

    def set_type(self, address, type):
        self.type_checker[address] = type

    def get_type(self, address):
        return self.type_checker[address]



class Activation_record:   #first argnums of the symbol_counter are the arguments
    def __init__(self, name, PB_index, DB_index):
        self.name = name
        self.PB_index = PB_index
        self.DB_index = DB_index
        self.symbol_counter = 0
        self.array_counter = 0
        self.symbol_dict = {}
        self.array_dict = {}
        self.arguments_num = 0
        self.arguments_name = []



    def add_arg_symbol(self, argument_name, DB):
        self.arguments_num += 1
        self.arguments_name.append(argument_name)
        self.add_symbol(argument_name,DB)

    def is_item_int(self, item_name):
        if item_name in self.symbol_dict.keys():
            return True
        return False


    def add_arg_array(self, array_name, DB): #TODO
        self.arguments_num += 1
        self.arguments_name.append(array_name)
        self.assign_array_place(array_name, DB)




    def add_symbol(self, symbol_str, DB):
        if symbol_str not in self.symbol_dict.keys():
            print('here adds correctly, has never seen', symbol_str, 'before')
            DB.write(0, self.DB_index + 4 * self.symbol_counter)
            DB.index = self.DB_index + 4 * self.symbol_counter + 4
            self.symbol_dict[symbol_str] = (self.DB_index + 4 * self.symbol_counter, self.symbol_counter)
            print('address:', self.DB_index + 4 * self.symbol_counter)
            self.symbol_counter += 1
        else:
            pass
            print('has already seen' , symbol_str)
            #TODO ERROR


    def update_symbol(self, symbol_str, symbol_value, DB):
        if symbol_str not in self.symbol_dict.keys():
            #TODO print error
            return
        DB.write(symbol_value, self.symbol_dict[symbol_str][0])


    def add_array(self, array_size, array_name, DB):
        if array_name not in self.array_dict.keys():
            address = DB.write_array(size = array_size)  # ye tike ja be araye ekhtesas mide ye jaye hafeze ke malum nist ama mige koja behemoon
            DB.write(address, self.DB_index + 4 * self.symbol_counter)  # ma tooye khube DB_index + 4 * symbol_counter mirim in addressi ke behemoon dadeh ro minevisim
            DB.index = self.DB_index + 4 * self.symbol_counter + 4
            print('array: ', array_name, ' address:' , address, ' address addr: ' ,self.DB_index + 4 * self.symbol_counter )
            self.array_dict[array_name] = (address, array_size, self.symbol_counter)
            DB.set_type(self.DB_index + 4 * self.symbol_counter, 1)
            self.symbol_counter += 1
            self.array_counter += 1

    def assign_array_place(self, array_name, DB):
        if array_name not in self.array_dict.keys():
            DB.write(0, self.DB_index + 4 * self.symbol_counter) #assigns a place for the array's address in DB, what's the address? someone else wil decide later!!!
            DB.index = self.DB_index + 4 * self.symbol_counter + 4
            self.array_dict[array_name] = (0, -1 , self.symbol_counter)
            self.symbol_counter += 1


    def get_symbol(self, symbol_str):
        if symbol_str not in self.symbol_dict.keys():
            return None
        return self.symbol_dict[symbol_str][0]

    def get_array_element(self, array_str, index, DB):
        if array_str not in self.array_dict.keys():
            return None
        return DB.read(self.array_dict[array_str][0] + 4 * index)

    def get_array_element_address(self,array_str, index, DB):
        if array_str not in self.array_dict.keys():
            return None
        return self.array_dict[array_str][0] + 4 * int(index)


    def get_array(self, array_name):
        if array_name not in self.array_dict.keys():
            return None
        return self.array_dict[array_name][2] * 4 + self.DB_index

    def update_array(self, array_str,value ,index, DB):
        if array_str not in self.array_dict.keys():
            #TODO print error
            return

        DB.write(value, self.array_dict[array_str][0] + 4 * index)


    def update_array_address(self, array_name, new_address, DB, array_size = -1):
        if array_name not in self.array_dict.keys():
            return
            #TODO error

        id_number = self.array_dict[array_name][2]
        self.array_dict[array_name] = (new_address, array_size, id_number)
        DB.write(item = new_address, addr = self.DB_index + 4 * id_number)



def find_the_symbol(activation_record_stack, symbol):  #finds both symbol address and array adress
    n = activation_record_stack.get_len()
    print('n: ', n)
    for i in range(n):
        print(i)
        scope = activation_record_stack.get_item(i)
        symbol_addr = scope.get_symbol(symbol)
        if symbol_addr is not None:
            print('address of ', symbol , ' : ' , symbol_addr)
            return symbol_addr

    for i in range(n):
        print(i)
        scope = activation_record_stack.get_item(i)
        array_addr = scope.get_array(symbol)
        if array_addr is not None:
            print('address of ', symbol , ' : ' , array_addr)
            return array_addr

    return None

def find_the_array_element(activation_record_stack, array_name, index, DB, is_address):
    if is_address:
        index = DB.read(index)
    print('index: ' , index)
    n = activation_record_stack.get_len()
    print('n: ' , n)
    for i in range(n):
        scope = activation_record_stack.get_item(i)
        addr = scope.get_array_element_address(array_name, index, DB)
        if addr is not None:
            return addr

    #TODO print error ID’ is not defined
    return None
















