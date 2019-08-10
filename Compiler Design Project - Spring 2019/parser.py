# from lexical_analyser import get_token_one_by_one
from intermediate_code import *
from routines import *

input_file = open("input.txt", 'r')
code = input_file.read()

output_file = open("scanner.txt", 'w+')
first_output = True

lexical_error_file = open("lexical_errors.txt", 'w+')
first_lexical_error = True

parser_file_dir = 'parser.txt'
parser_file = open(parser_file_dir, 'w+')

parser_error_file = open("parser_errors.txt", 'w+')
first_parser_error = True

all_errors_file = open("errors.txt", 'w+')

semantic_error_file = open('semantic_error.txt', 'w+')


start_ind = 0
end_ind = 0
ind = -1
current_char = ''
next_char = ''
line_num = 1
line_changed = True
error_line_changed = True

TOKEN_INITIAL = 0
TOKEN_NUM = 1
TOKEN_KEYWORD = 2
TOKEN_ID = 3
TOKEN_SYMBOL = 4
TOKEN_COMMENT = 5
TOKEN_WHITESPACE = 6
TOKEN_EOF = 7
EOF = "@EOF@"
state = TOKEN_INITIAL
symbols = [';', ':', ',', '[', ']', '(', ')', '{', '}', '+', '-', '*', '=', '<', '==', '>']
whitespace = ['\n', '\r', '\t', ' ', '\v', '\f']
digit = [str(i) for i in range(10)]
alphabet = [chr(i) for i in range(65, 91)] + [chr(i) for i in range(97, 123)]
keywords = ['if', 'else', 'void', 'int', 'while', 'break', 'continue', 'switch', 'default', 'case', 'return', 'int']
comments = ['/', '*']
all_letters = symbols + whitespace + digit + alphabet + keywords + comments
previous_token_string = ''
current_token_type = current_token_string = ''


def get_string():
    global start_ind, end_ind, code
    return code[start_ind:end_ind]  # TODO- end_int + 1 ????


def get_char():
    global ind, code, current_char, next_char
    ind += 1
    if ind < len(code):
        current_char = code[ind]
        if ind + 1 < len(code):
            next_char = code[ind + 1]
        return current_char

    current_char = None
    return EOF


def num():
    global end_ind
    while current_char is not None:
        if current_char not in digit:
            if current_char in symbols or current_char in whitespace or current_char in all_letters:
                return True, get_string(), 'NUM'
            else:
                return False, get_string() + current_char, 'invalid input'
        get_char()
        end_ind = ind
    return True, get_string(), 'NUM'


def symbol():
    global end_ind
    if current_char == '=':
        if next_char == '=':
            get_char()
            end_ind = ind

    get_char()
    end_ind = ind
    return True, get_string(), 'SYMBOl'


def skip_whitespace():
    global line_num, line_changed, error_line_changed
    global end_ind
    while current_char in whitespace:
        if current_char == '\n':
            line_num += 1
            line_changed = True
            error_line_changed = True
        get_char()
        end_ind = ind


def id():
    global end_ind, ind
    while current_char is not None:

        if (current_char not in digit) and (current_char not in alphabet):
            if current_char in symbols or current_char in whitespace:
                if get_string() in keywords:
                    return True, get_string(), 'KEYWORD'

                return True, get_string(), 'ID'
            else:
                return False, get_string() + current_char, 'invalid input'
        get_char()
        end_ind = ind

    return True, get_string(), 'ID'


def comment():
    global end_ind
    if next_char == '/':
        get_char()
        end_ind = ind
        while current_char is not '\n' and current_char is not None:
            get_char()
            end_ind = ind

        return True, None, 'COMMENT'
    if next_char == '*':
        get_char()
        end_ind = ind

        while current_char is not None and (current_char + next_char != '*/'):
            get_char()
            end_ind = ind

        get_char()
        get_char()
        end_ind = ind

        return True, None, 'COMMENT'

    get_char()
    return False, get_string() + current_char, 'invald input'


def get_token_one_by_one():
    global state, end_ind, start_ind, previous_token_string
    # get_char()
    error_flag = False
    token_string = token_type = ''
    previous_token_string = current_token_string
    while current_char is not None and not error_flag:
        error_flag = False
        skip_whitespace()
        start_ind = ind
        if current_char in digit:
            state = TOKEN_NUM
            is_token, token_string, token_type = num()

        elif current_char in symbols:
            state = TOKEN_SYMBOL
            is_token, token_string, token_type = symbol()

        elif current_char in alphabet:
            is_token, token_string, token_type = id()

        elif current_char == '/':
            state = TOKEN_COMMENT
            is_token, token_string, token_type = comment()

        else:
            is_token = False
            token_type = 'invalid input'
            token_string = current_char

        print('token string:          ', token_string, '   index:', start_ind)
        if is_token and state != TOKEN_COMMENT:
            print_token(token_type, token_string)
            start_ind = ind
            state = TOKEN_INITIAL
            return token_type, token_string
        elif not is_token:
            get_char()
            error_flag = True
            print_error(token_type, token_string)

        start_ind = ind
        state = TOKEN_INITIAL
    return token_type, token_string


def get_next_token():
    global state, end_ind, start_ind
    get_char()
    is_token = False
    token_string = token_type = ''
    while current_char is not None:
        skip_whitespace()
        start_ind = ind
        if current_char in digit:
            state = TOKEN_NUM
            is_token, token_string, token_type = num()

        elif current_char in symbols:
            state = TOKEN_SYMBOL
            is_token, token_string, token_type = symbol()

        elif current_char in alphabet:
            is_token, token_string, token_type = id()

        elif current_char == '/':
            state = TOKEN_COMMENT
            is_token, token_string, token_type = comment()

        else:
            is_token = False
            token_type = 'invalid input'
            token_string = current_char

        if is_token and state != TOKEN_COMMENT:
            print_token(token_type, token_string)
        elif not is_token:
            get_char()
            print_error(token_type, token_string)

        start_ind = ind
        state = TOKEN_INITIAL


def print_token(token_type, token_string):
    global output_file, line_changed, first_output

    if line_changed and not first_output:
        output_file.write('\n')
    if line_changed:

        output_file.write(str(line_num) + '. (' + token_type + ', ' + token_string + ')')
        line_changed = False
    else:

        output_file.write(' (' + token_type + ', ' + token_string + ')')
    first_output = False


def print_error(token_type, token_string):
    global lexical_error_file, error_line_changed, first_lexical_error, line_num
    if error_line_changed and not first_lexical_error:
        lexical_error_file.write('\n')

    if error_line_changed:
        lexical_error_file.write(str(line_num) + '. (' + token_string + ', ' + token_type + ')')
        error_line_changed = False

    else:
        lexical_error_file.write(' (' + token_string + ', ' + token_type + ')')

    first_lexical_error = False


def write_parser_error(error_message):
    global parser_error_file, error_line_changed, first_parser_error, line_num
    if error_line_changed and not first_parser_error:
        parser_error_file.write('\n')

    if error_line_changed:
        parser_error_file.write(str(line_num) + '. (' + error_message + ')')
        error_line_changed = False

    else:
        parser_error_file.write(' (' + error_message + ')')

    first_parser_error = False

def write_semantic_error_file(error_message):
    global line_num
    semantic_error_file.write(str(line_num) + '.' + error_message + '\n')


def combine_errors():
    global parser_error_file, lexical_error_file, all_errors_file
    all_errors_file.write('Lexical Errors:\n')
    f1 = open('lexical_errors.txt', 'r')
    f2 = open('parser_errors.txt', 'r')
    f1 = f1.readlines()
    f2 = f2.readlines()
    for line in f1:
        all_errors_file.write(line)
    all_errors_file.write('\n\nParser Errors:\n')
    for line in f2:
        all_errors_file.write(line)


class Non_terminal:
    def __init__(self, name, first_set, follow_set):
        self.name = name
        self.first_set = first_set
        self.follow_set = follow_set
        self.transition_dictionary = dict()
        self.final_state = 0
        self.initial_state = 0

    def set_transition_dictionary(self, transition_dictionary, initial_state, final_state):
        self.transition_dictionary = transition_dictionary
        self.final_state = final_state
        self.initial_state = initial_state


def write_to_parser_file(height, leaf):
    for i in range(height):
        parser_file.write('| ')
    parser_file.write(leaf + '\n')



def get_new_token():
    global current_token_type, current_token_string
    current_token_type, current_token_string = get_token_one_by_one()
    if current_token_type == 'SYMBOl':
        current_token_type = current_token_string
    elif current_token_type == '':
        current_token_type = '$'
    elif current_token_type == 'KEYWORD':
        current_token_type = current_token_string
    print(current_token_type, '     ', current_token_string)


def parser(non_terminal, height):
    global current_token_type, current_token_string

    print('Non_terminal: ', non_terminal.name)
    error_flag = False
    write_to_parser_file(height=height, leaf=non_terminal.name)
    s = non_terminal.initial_state
    while s != non_terminal.final_state and parser.running:
        flag = False
        this_state = {}
        for key, value in non_terminal.transition_dictionary.items():
            if key[0] == s:
                this_state[key] = value
        print('this state:', this_state)
        print('current state and token type: ', (s, current_token_type))
        print('len this state: ', len(this_state))

        if isinstance(list(this_state.keys())[0][1], Routine):  # TODO !!!
            code_gen(list(this_state.keys())[0][1])
            s = non_terminal.transition_dictionary[(s, list(this_state.keys())[0][1])]
            continue

        if len(this_state) == 1:
            terminal_edge = list(this_state.keys())[0][1]
            if isinstance(terminal_edge, str) and terminal_edge != current_token_type:
                if current_token_type != '$':
                    write_parser_error('Syntax Error! Missing ' + str(terminal_edge))
                    print('error #LINE_NUM : Syntax Error! Missing #TERMINAL_NAME')
                    current_token_type = terminal_edge

                else:
                    print('LINE_NUM : Syntax Error! Malformed Input')
                    write_parser_error('Syntax Error! Malformed Input')
                    parser.running = False

        if (s, current_token_type) in this_state:  # .items added
            s = non_terminal.transition_dictionary[(s, current_token_type)]
            print('next state: ', s)
            write_to_parser_file(height + 1, current_token_type)
            print()
            get_new_token()
            if s == non_terminal.final_state:
                print(non_terminal.name, ' finished')
            flag = True

        elif len(this_state) > 0:
            for key, value in this_state.items():
                if isinstance(key[1], Non_terminal) and (current_token_type in key[1].first_set or
                                                         (('EPSILON' in key[
                                                             1].first_set or error_flag) and current_token_type in key[
                                                              1].follow_set)):
                    if error_flag and 'EPSILON' not in key[1].first_set and current_token_type not in key[1].first_set:
                        print('Syntax Error! Missing #NON_TERMINAL_DESCRIPTION')
                        write_parser_error('Syntax Error! Missing ' + key[1].name)
                        s = value
                        error_flag = False
                        flag = True
                        break

                    print('inside')
                    print((key[0], key[1].name), value)
                    res = parser(key[1], height + 1)
                    s = value
                    flag = True
                    if not res:
                        return False
                    break
        if not flag and (s, 'EPSILON') in this_state.keys():  # what is this?
            flag = True
            write_to_parser_file(height + 1, 'EPSILON')
            s = non_terminal.transition_dictionary[(s, 'EPSILON')]
            print()
            if s == non_terminal.final_state:
                print(non_terminal.name, ' finished')
        if not flag:
            if error_flag:
                for key in this_state.keys():
                    write_parser_error('Syntax Error! Missing ' + str(key[1].name))
                    print('Syntax Error! Missing ' + str(key[1].name))
                    get_new_token()
                    break
            if current_token_type == '$':
                print('Syntax Error! Unexpected EndOfFile')
                write_parser_error('Syntax Error! Unexpected EndOfFile')
                parser.running = False

            error_flag = True

    return True


ss = Stack()
goto_ss = Stack()
PB = Program_block()
DB = Data_block()
function_activation_record_stack = Stack()
scope_activation_record_stack = Stack()
function_activatior = {}
first_scope = Activation_record(name='first scope', PB_index=0,
                                DB_index=0)  # the first scope that has eveeeerything in it
scope_activation_record_stack.push(first_scope)
all_function = []
seen_void = 0  # -1 unknow, 1  seen, 0 unseen
new_scope_name = 'new scope'


def code_gen(routine):
    action = routine.func
    print('routine:', action)
    if action == '#save':
        _save()
    elif action == '#label':
        _label()
    elif action == '#while':
        _while()
    elif action == '#push_one':
        _push_one()
    elif action == '#push_zero':
        _push_zero()
    elif action == '#jp_save':
        _jp_save()
    elif action == '#jp':
        _jp()
    elif action == '#push_string':
        _push_string()
    elif action == '#pid':
        _pid()
    elif action == '#array_element':
        _array_element()

    elif action == '#get_array_with_index':
        _get_array_with_index()
    elif action == '#make_id':
        _make_id()

    elif action == '#make_array':
        _make_array()

    elif action == '#new_scope':
        _new_scope()

    elif action == '#new_while_scope':
        _new_while_scope()

    elif action == '#new_switch_scope':
        _new_switch_scope()

    elif action == '#expression_end':
        _expression_end()

    elif action == '#assignment':
        _assignment()
    elif action == '#minus_factor':
        _minus_factor()
    elif action == '#mult':
        _mult()

    elif action == '#push_number':
        _push_number()

    elif action == '#addop':
        _addop()

    elif action == '#relop':
        _relop()

    elif action == '#push_arg':
        _push_arg()

    elif action == '#call':
        _call()

    elif action == '#make_function':
        _make_function()

    elif action == '#add_symbol_param':
        _add_symbol_param()

    elif action == '#add_array_param':
        _add_array_param()

    elif action == '#return':
        _return()

    elif action == '#return_value':
        _return_value()

    elif action == '#void':
        _void()

    elif action == '#unvoid':
        _unvoid()

    elif action == '#void_main_check':
        _void_main_check()

    elif action == '#tmp_save':
        _tmp_save()

    elif action == '#jp_switch':
        _jp_switch()

    elif action == '#cmp_save':
        _cmp_save()

    elif action == '#cmp_save_1':
        _cmp_save_1()

    elif action == '#break':
        _break()

    elif action == '#continue':
        _continue()

    elif action == '#main_param_check_not_int':
        _main_param_check_not_int()

    elif action == '#func_param_check_not_void':
        _func_param_check_not_void()

    elif action == '#push_pre_string':
        _push_pre_string()

    elif action == '#non_void_checker':
        _non_void_checker()

    elif action == '#pop_scope':
        _pop_scope()

    elif action == '#check_main_exists':
        _check_main_exists()

    elif action == '#check_break':
        _check_break()

    elif action == '#check_continue':
        _check_continue()

    elif action == '#default':
        _default()


def _label():
    ss.push(PB.index)


def _save():
    ss.push(PB.index)
    PB.increase_index()


def _while():
    global new_scope_name
    PB.write(ss.get_item(0), assembly_gen('JPF', ss.get_item(1), PB.index + 1))
    PB.write(PB.index, assembly_gen('JP', ss.get_item(2)))
    PB.increase_index()
    ss.pop(3)
    PB.write(ss.get_item(0), PB.index)
    scope_activation_record_stack.pop(1)
    ss.pop(1)
    new_scope_name = 'new_scope'


def _output():
    PB.write(PB.index, assembly_gen('PRINT', ss.get_item(0)))
    PB.increase_index()
    ss.pop(2)


def _push_pre_string():
    print('previous token:', previous_token_string)
    ss.push(previous_token_string)


def _push_one():
    t = DB.get_temp()
    PB.write(PB.index, assembly_gen('ASSIGN', _hashtag('1'), t))
    PB.increase_index()
    DB.write(1, t)
    ss.push(t)


def _push_zero():
    t = DB.get_temp()
    DB.write(0, t)
    PB.write(PB.index, assembly_gen('ASSIGN', _hashtag('0'), t))
    PB.increase_index()
    ss.push(t)


def _jp_save():
    PB.write(ss.get_item(0), statement=assembly_gen('JPF', ss.get_item(1), PB.index + 1))
    ss.pop(2)
    ss.push(PB.index)
    PB.increase_index()


def _jp():
    PB.write(ss.get_item(0), assembly_gen('JP',PB.index))
    ss.pop(1)


def _pid():
    print('symbol: ', ss.get_item(0))
    symbol_addr = find_the_symbol(activation_record_stack= scope_activation_record_stack, symbol= ss.get_item(0))
    if symbol_addr is None:
        write_semantic_error_file(ss.get_item(0) + 'is not defined.')
        print('error ID’ is not defined')
    ss.pop(1)
    print('symbol address:' , symbol_addr)
    ss.push(symbol_addr)


def _array_element():
    id = ss.get_item(1)
    array_index = ss.get_item(0)
    array_element_index = find_the_array_element(activation_record_stack=scope_activation_record_stack, array_name=id,
                                                 index=array_index, DB=DB, is_address=True)
    ss.pop(2)
    ss.push(array_element_index)


def _push_string():
    print('string', current_token_string)
    ss.push(current_token_string)


def _get_array_with_index():
    id = ss.get_item(1)
    index = ss.get_item(0)
    print('array:' , id, ' index: ' , index)
    array_element_index = find_the_array_element(activation_record_stack=scope_activation_record_stack, array_name=id,
                                                 index=index, DB=DB, is_address=True)
    ss.pop(2)
    ss.push(array_element_index)


def _make_id():  # TODO works with scope cause who knows
    id = ss.get_item(0)
    print('id: ', id)
    scope_activation_record_stack.get_item(0).add_symbol(id, DB)
    ss.pop(1)


def _make_array():
    array_name = ss.get_item(1)
    num_of_elements = ss.get_item(0)
    scope_activation_record_stack.get_item(0).add_array(int(num_of_elements), array_name, DB)
    ss.pop(2)


def _new_scope():
    scope_activation_record_stack.push(Activation_record(name=new_scope_name , PB_index=PB.index, DB_index=DB.get_index()))


def _new_while_scope():
    global new_scope_name
    new_scope_name = 'while'


def _new_switch_scope():
    scope_activation_record_stack.push(Activation_record(name='switch', PB_index=PB.index, DB_index=DB.get_index()))


def _expression_end():
    ss.pop(1)


def _assignment():
    print('pb index:', PB.index)
    PB.write(PB.index, assembly_gen('ASSIGN', s1=ss.get_item(0), s2=ss.get_item(1)))
    PB.increase_index()
    ss.pop(1)


def _minus_factor():
    PB.write(PB.index, assembly_gen('MULT', s1=ss.get_item(0), s2=_hashtag(1), d=ss.get_item(0)))
    PB.increase_index()


def _mult():
    if  DB.get_type(ss.get_item(1)) == 1 or DB.get_type(ss.get_item(0)) == 1:
        print('Type mismatch in operands')
        write_semantic_error_file('Type mismatch in operands')
        return
    t = DB.get_temp()
    PB.write(PB.index, statement=assembly_gen('MULT', s1=ss.get_item(0), s2=ss.get_item(1), d=t))
    PB.increase_index()
    ss.pop(2)
    ss.push(t)


def _addop():
    if  DB.get_type(ss.get_item(2)) == 1 or DB.get_type(ss.get_item(0)) == 1:
        print('Type mismatch in operands')
        write_semantic_error_file('Type mismatch in operands')
        return
    PB.write(PB.index, assembly_gen('JPF', s1=ss.get_item(1), s2=PB.index + 3))
    PB.increase_index()
    t = DB.get_temp()
    PB.write(PB.index, assembly_gen('ADD', s1=ss.get_item(2), s2=ss.get_item(0), d=t))
    PB.increase_index()
    PB.write(PB.index, assembly_gen('JP', s1=PB.index + 2))
    PB.increase_index()
    PB.write(PB.index, assembly_gen('SUB', s1=ss.get_item(2), s2=ss.get_item(0), d=t))
    PB.increase_index()
    ss.pop(3)
    ss.push(t)


def _relop():
    if  DB.get_type(ss.get_item(2)) == 1 or DB.get_type(ss.get_item(0)) == 1:
        print('Type mismatch in operands')
        write_semantic_error_file('Type mismatch in operands')
        return
    PB.write(PB.index, assembly_gen('JPF', s1=ss.get_item(1), s2=PB.index + 3))
    PB.increase_index()
    t = DB.get_temp()
    PB.write(PB.index, assembly_gen('EQ', s1=ss.get_item(2), s2=ss.get_item(0), d=t))
    PB.increase_index()
    PB.write(PB.index, assembly_gen('JP', s1=PB.index + 2))
    PB.increase_index()
    PB.write(PB.index, assembly_gen('LT', s1=ss.get_item(2), s2=ss.get_item(0), d=t))
    PB.increase_index()
    ss.pop(3)
    ss.push(t)


def _push_number():
    t = DB.get_temp()
    print('number:', previous_token_string)
    PB.write(PB.index, assembly_gen('ASSIGN', s1=_hashtag(previous_token_string), s2=t))
    PB.increase_index()
    DB.write(previous_token_string, t)
    ss.push(t)


def _pop_scope():
    scope_activation_record_stack.pop(1)


def _push_arg():
    t1 = DB.get_temp()
    t2 = DB.get_temp()
    print('t1: ', t1, ' t2: ', t2)
    print('ss(top)): ', DB.read(ss.get_item(0)))
    print('ss(top - 1)):', DB.read(ss.get_item(1)))
    PB.write(PB.index, assembly_gen('ASSIGN', s1=ss.get_item(0), s2=t1))
    PB.increase_index()
    PB.write(PB.index, assembly_gen('ASSIGN', s1=ss.get_item(0), s2=t1))
    PB.increase_index()
    PB.write(PB.index, assembly_gen('ASSIGN', s1=ss.get_item(1), s2=t2))
    PB.increase_index()
    PB.write(PB.index, assembly_gen('ADD', s1=t2, s2=_hashtag('1'), d=t2))
    DB.write(item=DB.read(ss.get_item(1)) + 1, addr=t2)
    ss.pop(2)
    ss.push(t1)
    ss.push(t2)


def _call():
    number_of_args = DB.read(ss.get_item(0))
    print('number of args', number_of_args)

    function_name = ss.get_item(number_of_args + 1)
    print('function name:', function_name)
    address = -1
    function_AR = None
    for i in range(len(all_function)):
        print('all_function[i].name', all_function[i].name)
        if all_function[i].name == function_name:
            address = all_function[i].PB_index
            print('function pb index:', address)
            function_AR = all_function[i]
            break
    if address == -1:

        if function_name != 'output':
            write_semantic_error_file((function_name + ' not found.'))
            #TODO print function not found error

        else:
            if number_of_args != 1:
                pass
                print('number of args error')
                write_semantic_error_file('Mismatch in numbers of arguments of '  + function_name)
                # TODO error
            else:
                _output()

    if function_AR.arguments_num != number_of_args:
        write_semantic_error_file('Mismatch in numbers of arguments of ' + function_name)

        print('number of args error')
        pass
        # TODO error

    arguments_names = function_AR.arguments_name
    print('arguments names:', arguments_names)

    for i in reversed(range(number_of_args)):
        id = arguments_names[i]
        print(ss.get_item(number_of_args - i))
        value = DB.read(ss.get_item(number_of_args - i))

        if id in function_AR.symbol_dict.keys():
            function_AR.update_symbol(id, value, DB)
            symbol_adr = function_AR.get_symbol(id)
            PB.write(PB.index, assembly_gen('ASSIGN', ss.get_item(number_of_args - i), symbol_adr))
            PB.increase_index()
        else:
            print(id , ' is array', 'value = ' , value)
            function_AR.update_array_address(id, value, DB)
            array_addr = function_AR.get_array(id)
            PB.write(PB.index, assembly_gen('ASSIGN',ss.get_item(number_of_args - i) , array_addr))
            PB.increase_index()

    PB.write(PB.index, assembly_gen('JP', s1=address))
    PB.increase_index()
    ss.pop(number_of_args + 2)  #one for number_of_args and one for the function name
    PB.write(address + 1, PB.index) #second line of a function is its return address
    ss.push(PB.program[address + 2])


def _void():
    global seen_void
    seen_void = 1



def _non_void_checker():
    if seen_void == 1:
        if scope_activation_record_stack.get_item(0).name != 'main':
            print(scope_activation_record_stack.get_item(0).name)
            print('illegal type of void')
            write_semantic_error_file('illegal type of void')
            return #TODO return error : illegal type of void
    else:
        if scope_activation_record_stack.get_item(0).name == 'main':
            print('void main(int) error')
            write_semantic_error_file('main function not found!')
            return #Todo error



def _unvoid():
    global seen_void
    seen_void = 0


def _void_main_check():
    id = ss.get_item(0)
    print('id: ', id)
    if seen_void == 1:
        if id != 'main':
            print('void func error')
            write_semantic_error_file('Illegal type of function.')
            return #TODo error
    elif id == 'main':
        print('int main error')
        write_semantic_error_file('main function not found!')
        return  #TODO error


def _make_function():
    id = ss.get_item(0)
    if seen_void == 1:
        if id != 'main':
            print('void func error')
            write_semantic_error_file('Illegal type of function!')
            #TODO error
            return
    else:
        if id == 'main':
            print('int main error')
            write_semantic_error_file('main functino not found!')
            return #TODO error

    function_AR = Activation_record(name=id, PB_index= PB.index, DB_index= DB.index)
    print('function name = ' , id, 'start address:', function_AR.PB_index, 'DB start:', DB.index)
    function_activation_record_stack.push(function_AR)
    scope_activation_record_stack.push(function_AR)
    all_function.append(function_AR)
    PB.write(PB.index, assembly_gen('JP', PB.index + 3))
    # PB[i + 1] = return_address
    # PB[i + 2] = return value
    PB.increase_index()
    PB.increase_index()
    PB.increase_index()
    ss.pop(1)


def _add_symbol_param():
    id = ss.get_item(0)
    print('symbol: ', id)
    function_AR = function_activation_record_stack.get_item(0)
    function_AR.add_arg_symbol(id, DB)
    ss.pop(1)


def _add_array_param():
    id = ss.get_item(0)
    function_AR = function_activation_record_stack.get_item(0)
    function_AR.add_arg_array(id, DB)
    ss.pop(1)


def _return():
    function_AR = function_activation_record_stack.get_item(0)
    function_start_address = function_AR.PB_index
    PB.write(PB.index, assembly_gen('JP', s1 = _at(function_start_address + 1)))
    PB.increase_index()
    scope_activation_record_stack.pop(1)
    function_activation_record_stack.pop(1)

def _return_value():
    function_AR = function_activation_record_stack.get_item(0)
    function_start_address = function_AR.PB_index
    print(ss.get_item(0), '    ', DB.read(addr=ss.get_item(0)))
    print('function start address:' , function_start_address, ' f name:' , function_AR.name, ' PB:' , PB.index)
    PB.write(PB.index, assembly_gen('JP', s1 = _at(function_start_address + 1)))
    PB.increase_index()
    PB.write(function_start_address + 2, ss.get_item(0))
    ss.pop(1)
    scope_activation_record_stack.pop(1)
    function_activation_record_stack.pop(1)


def _main_param_check_not_int():
    if scope_activation_record_stack.get_item(0).name == 'main':
        print('void main(int) error')
        write_semantic_error_file('main function not found!')
        return #TODO error


def _func_param_check_not_void():
    if scope_activation_record_stack.get_item(0).name != 'main':
        print('function has void attrb error')
        write_semantic_error_file('Illegal type of void.')
        #TODO error


def _tmp_save():
    PB.write(PB.index, assembly_gen('JP', PB.index + 2))
    PB.increase_index()
    ss.push(PB.index)
    PB.increase_index()


def _jp_switch():
    PB.write(ss.get_item(0), PB.index)
    ss.pop(1)
    scope_activation_record_stack.pop(1)


def _cmp_save():
    t = DB.get_temp()
    PB.write(PB.index, assembly_gen('EQ', _hashtag(ss.get_item(0)), ss.get_item(1), t))
    PB.increase_index()
    ss.pop(1)
    ss.push(t)
    ss.push(PB.index)
    PB.increase_index()


def _cmp_save_1():
    PB.write(ss.get_item(2), assembly_gen('JPF', ss.get_item(3), PB.index))
    t = DB.get_temp()
    PB.write(PB.index, assembly_gen('EQ', _hashtag(ss.get_item(0)), ss.get_item(4), t))
    PB.increase_index()
    PB.write(ss.get_item(1), assembly_gen('JP', PB.index + 1))
    ss.pop(4)
    ss.push(t)
    ss.push(PB.index)
    PB.increase_index()


def _default():
    ss.pop(1)
    PB.decrease_index()
    PB.write(ss.get_item(0), ('JPF', ss.get_item(1), PB.index))
    ss.pop(3)


def _break():
    PB.write(PB.index, assembly_gen('JP', _at(scope_activation_record_stack.get_item(0).PB_index + 1)))
    PB.increase_index()


def _continue():
    PB.write(PB.index, assembly_gen('JP', scope_activation_record_stack.get_item(0).PB_index))


def _main_one_param_check():
    if scope_activation_record_stack.get_item(0).name == 'main':
        ss.pop(1)
        return  # TODO error


def _check_main_exists():
    for ar in all_function:
        if ar.name == 'main':
            print('found main at:', ar.PB_index)
            PB.write(0, assembly_gen('JP', ar.PB_index))
            PB.write(ar.PB_index + 1, PB.index)
            return

    print('could not find main')


def _check_continue():
    if scope_activation_record_stack.get_item(0).name != 'while':
        print('No \'while\' found for \'continue\'') #TODO: error No ’while’ found for ’continue’.
        write_semantic_error_file('No \'while\' found for \'continue\'')


def _check_break():
    if scope_activation_record_stack.get_item(0).name != 'while' and scope_activation_record_stack.get_item(0).name != 'switch':
        print(scope_activation_record_stack.get_item(0).name)
        print('No \'while\' or \'switch\'found for \'break\'') #TODO: error No ’while’ or ’switch’ found for ’break’.
        write_semantic_error_file('No \'while\' or \'switch\' found for \'break\'')


def _at(s):
    return '@' + str(s)


def _hashtag(s):
    return '#' + str(s)


def assembly_gen(cmd='', s1='', s2='', d=''):
    return '(' + cmd + ', ' + str(s1) + ', ' + str(s2) + ', ' + str(d) + ')'


program = Non_terminal(name='program', first_set=['$', 'int', 'void'], follow_set=[])

DeclarationList = Non_terminal(name='declaration_list', first_set=['EPSILON', 'int', 'void'],
                               follow_set=['$', '{', 'continue', 'break', ';', 'if',
                                           'while', 'return', 'switch', 'ID', '+', '-', '(', 'NUM', '}'])

DeclarationList1 = Non_terminal(name='declaration_list1', first_set=['EPSILON', 'int', 'void'],
                                follow_set=['$', '{', 'continue', 'break', ';', 'if',
                                            'while', 'return', 'switch', 'ID', '+', '-', '(', 'NUM', '}'])

Declaration = Non_terminal(name='declatation', first_set=['int', 'void'],
                           follow_set=['int', 'void', '$', '{', 'continue', 'break', ';', 'if', 'while', 'return',
                                       'switch'
                               , 'ID', '+', '-', '(', 'NUM', '}'])

TypeSpecifier_function = Non_terminal(name='TypeSpecifier_function', first_set=['ID'],
                               follow_set=['int', 'void', '$', '{', 'continue', 'break', ';', 'if', 'while', 'return',
                                           'switch',
                                           'ID', '+', '-', '(', 'NUM', '}'])

ID_Function = Non_terminal(name='ID_Function', first_set=['(', ';', '['],
                     follow_set=['int', 'void', '$', '{', 'continue', 'break', ';', 'if', 'while', 'return',
                                 'switch',
                                 'ID', '+', '-', '(', 'NUM', '}'])

ID_Array_define = Non_terminal(name='ID_Array_define', first_set=[';', '['],
                     follow_set=['int', 'void', '$', '{', 'continue', 'break', ';', 'if', 'while', 'return',
                                 'switch',
                                 'ID', '+', '-', '(', 'NUM', '}'])

TypeSpecifier = Non_terminal(name='TypeSpecifier', first_set=['int', 'void'],
                             follow_set=['ID'])

Params = Non_terminal(name='params', first_set=['int', 'void'], follow_set=[')'])

void_Params = Non_terminal(name='void_Params', first_set=['EPSILON', 'ID'], follow_set=[')'])

ParamList_1 = Non_terminal(name='param_list1', first_set=[',', 'EPSILON'], follow_set=[')'])

Param = Non_terminal(name='param', first_set=['int', 'void'], follow_set=[',' ')'])

Param_type_specifier = Non_terminal(name='Param_type_specifier', first_set=['ID'], follow_set=[',', ')'])

ID_array_param = Non_terminal(name='ID_array_param', first_set=['EPSILON', '['], follow_set=[',', ')'])

CompoundStmt = Non_terminal(name='CompoundStmt', first_set=['{'],
                            follow_set=['int', 'void', '$', '{', 'continue',
                                        'break', ';', 'if', 'while', 'return', 'switch', 'ID', '(', 'NUM', '-', '+',
                                        '}',
                                        'else', 'case', 'default'])

StatementList = Non_terminal(name='StatementList', first_set=['EPSILON', '{', 'continue',
                                                              'break', ';', 'if', 'while', 'return', 'switch', 'ID',
                                                              '+', '-', '(', 'NUM'],
                             follow_set=['}', 'case', 'default'])

StatementList1 = Non_terminal(name='StatementList1',
                              first_set=['EPSILON', '{', 'continue',
                                         'break', ';', 'if', 'while', 'return', 'switch', 'ID', '+', '-', '(', 'NUM'],
                              follow_set=['}', 'case', 'default'])

Statement = Non_terminal(name='Statement',
                         first_set=['{', 'continue', 'break', ';', 'if', 'while', 'return', 'switch', 'ID', '+', '-',
                                    '(', 'NUM'],
                         follow_set=['{', 'continue', 'break', ';', 'if', 'while',
                                     'return', 'switch', 'ID', '+', '-', '(', 'NUM', '}', 'else', 'case',
                                     'default'])

ExpressionStmt = Non_terminal(name='ExpressionStmt', first_set=['continue', 'break', ';', 'ID', '+', '-', '(', 'NUM'],
                              follow_set=['{', 'continue', 'break', ';', 'if', 'while',
                                          'return', 'switch', 'ID', '+', '-', '(', 'NUM', '}', 'else', 'case',
                                          'default'])

SelectionStmt = Non_terminal(name='SelectionStmt', first_set=['if'],
                             follow_set=['{', 'continue', 'break', ';', 'if', 'while',
                                         'return', 'switch', 'ID', '+', '-', '(', 'NUM', '}', 'else', 'case',
                                         'default'])
IterationStmt = Non_terminal(name='IterationStmt', first_set=['while'],
                             follow_set=['{', 'continue', 'break', ';', 'if', 'while',
                                         'return', 'switch', 'ID', '+', '-', '(', 'NUM', '}', 'else', 'case',
                                         'default'])

ReturnStmt = Non_terminal(name='ReturnStmt', first_set=['return'],
                          follow_set=['{', 'continue', 'break', ';', 'if', 'while',
                                      'return', 'switch', 'ID', '+', '-', '(', 'NUM', '}', 'else', 'case', 'default'])

Function_return = Non_terminal(name='Function_return', first_set=[';', 'ID', '+', '-', '(', 'NUM'],
                       follow_set=['{', 'continue', 'break', ';', 'if', 'while',
                                   'return', 'switch', 'ID', '+', '-', '(', 'NUM', '}', 'else', 'case', 'default'])

SwitchStmt = Non_terminal(name='SwitchStmt', first_set=['switch'],
                          follow_set=['{', 'continue', 'break', ';', 'if', 'while',
                                      'return', 'switch', 'ID', '+', '-', '(', 'NUM', '}', 'else', 'case', 'default'])

CaseStmts = Non_terminal(name='CaseStmts', first_set=['EPSILON', 'case'], follow_set=['default', '}'])
CaseStmts1 = Non_terminal(name='CaseStmts1', first_set=['EPSILON', 'case'], follow_set=['default', '}'])
CaseStmt = Non_terminal(name='CaseStmt', first_set=['case'], follow_set=['case', 'default', '}'])

DefaultStmt = Non_terminal(name='DefaultStmt', first_set=['default', 'EPSILON'], follow_set=['}'])

Expression = Non_terminal(name='Expression', first_set=['ID', '+', '-', '(', 'NUM'],
                          follow_set=[';', ')', ']', ','])

Expression_id_start = Non_terminal(name='Expression_id_start', first_set=['(', '[', '=', '*', 'EPSILON', '+', '-', '<', '=='],
                     follow_set=[';', ')', ']', ','])

Expression_id_start_1 = Non_terminal(name='Expression_id_start_1', first_set=['=', '*', 'EPSILON', '+', '-', '<', '=='],
                       follow_set=[';', ')', ']', ','])

ID_array = Non_terminal(name='ID_array', first_set=['EPSILON', '['],
                   follow_set=['=', '*', '+', '-', '<', '==', ';', ')', ']', ','])

Expression_relop = Non_terminal(name='Expression_relop', first_set=['EPSILON', '<', '=='],
                                   follow_set=[';', ')', ']', ','])
Relop = Non_terminal(name='Relop', first_set=['<', '=='], follow_set=['+', '-', '(', 'ID', 'NUM'])
Expression_addop = Non_terminal(name='Expression_addop', first_set=['+', '-', '(', 'ID', 'NUM'],
                                  follow_set=[';', ')', ']', ','])

Expression_addop_op_start = Non_terminal(name='Expression_addop_op_start', first_set=['EPSILON', '+', '-'],
                                   follow_set=['<', '==', ';', ')', ']', ','])

Addop = Non_terminal(name='Addop', first_set=['+', '-'], follow_set=['+', '-', '(', 'ID', 'NUM'])
Term = Non_terminal(name='Term', first_set=['+', '-', '(', 'ID', 'NUM'],
                    follow_set=['+', '-', ';', ')', '<', '==', ']', ','])

Term1 = Non_terminal(name='term1', first_set=['*', 'EPSILON'], follow_set=['+', '-', ';', ')', '<', '==', ']', ','])

Term_2 = Non_terminal(name='Term2', first_set=['+', '-', '(', 'NUM'],
                      follow_set=['+', '-', ';', ')', '<', '==', ']', ','])

SignedFactor = Non_terminal(name='SignedFactor', first_set=['+', '-', '(', 'ID', 'NUM'],
                            follow_set=['*', '+', '-', ';', ')', '<', '==', ']', ','])

SignedFactor_no_id = Non_terminal(name='SignedFactor2', first_set=['+', '-', '(', 'NUM'],
                              follow_set=['*', '+', '-', 'ID', '(', 'NUM', '<', '==', ';', ')', ']', ','])

Factor = Non_terminal(name='Factor', first_set=['(', 'ID', 'NUM'],
                      follow_set=['*', '+', '-', ';', ')', '<', '==', ']', ','])

Factor_no_id = Non_terminal(name='Factor_no_id', first_set=['(', 'NUM'],
                        follow_set=['*', '+', '-', ';', ')', '<', '==', ']', ','])

ID_array_3 = Non_terminal(name='ID_array_3', first_set=['[', 'EPSILON', '('],
                     follow_set=['*', '+', '-', ';', ')', '<', '==', ']', ','])

Args = Non_terminal(name='Args', first_set=['EPSILON', 'ID', '+', '-', '(', 'NUM'], follow_set=[')'])
ArgList = Non_terminal(name='ArgList', first_set=['ID', '+', '-', '(', 'NUM'], follow_set=[')'])
ArgList1 = Non_terminal(name='ArgList1', first_set=[',', 'EPSILON'], follow_set=[')'])

program_dictionary = {(0, DeclarationList): 1, (1, check_main_exists_routine): 2, (2, '$'): 3}
program.set_transition_dictionary(program_dictionary, 0, 2)

DeclarationList_dictionary = {(0, DeclarationList1): 1}
DeclarationList.set_transition_dictionary(DeclarationList_dictionary, 0, 1)

DeclarationList1_dictionary = {(0, Declaration): 1, (1, DeclarationList1): 2, (0, 'EPSILON'): 2}
DeclarationList1.set_transition_dictionary(DeclarationList1_dictionary, 0, 2)

Declaration_dictionary = {(0, TypeSpecifier): 1, (1, TypeSpecifier_function): 2}
Declaration.set_transition_dictionary(Declaration_dictionary, 0, 2)

TypeSpecifier_function_dictionary = {(0, push_string_routine): 1, (1, 'ID'): 2, (2, ID_Function): 3}
TypeSpecifier_function.set_transition_dictionary(TypeSpecifier_function_dictionary, 0, 3)

ID_Functionictionary = {(0, ID_Array_define): 7, (7, non_void_checker_routine): 1,
                    (0, '('): 5, (5, void_main_check_routine): 6, (6, make_function_routine): 2, (2, Params): 3,
                    (3, ')'): 4, (4, CompoundStmt): 1}

ID_Function.set_transition_dictionary(ID_Functionictionary, 0, 1)

ID_Array_define_dictionary = {(0, ';'): 5, (5, make_id_routine): 1,
                    (0, '['): 2, (2, push_string_routine): 6, (6, 'NUM'): 3, (3, make_array_routine): 7, (7, ']'): 4,
                    (4, ';'): 1}
ID_Array_define.set_transition_dictionary(ID_Array_define_dictionary, 0, 1)

TypeSpecifier_dictionary = {(0, 'int'): 1, (1, unvoid_routine): 2, (0, 'void'): 3, (3, void_routine): 2}
TypeSpecifier.set_transition_dictionary(TypeSpecifier_dictionary, 0, 2)

Params_dictionary = {(0, 'int'): 1, (1, main_param_check_not_int_routine): 5, (5, Param_type_specifier): 2,
                     (2, ParamList_1): 3,
                     (0, 'void'): 4, (4, func_param_check_not_void_routine): 6, (6, void_Params): 3}
Params.set_transition_dictionary(Params_dictionary, 0, 3)

void_Params_dictionary = {(0, TypeSpecifier_function): 1, (1, main_one_param_check_routine): 2, (2, ParamList_1): 3,
                     (0, 'EPSILON'): 3}
void_Params.set_transition_dictionary(void_Params_dictionary, 0, 3)

ParamList_1_dictionary = {
    (0, ','): 1,
    (1, Param): 2,
    (2, ParamList_1): 3,
    (0, 'EPSILON'): 3
}
ParamList_1.set_transition_dictionary(ParamList_1_dictionary, 0, 3)

Param_dictionary = {
    (0, TypeSpecifier): 1,
    (1, Param_type_specifier): 2
}
Param.set_transition_dictionary(Param_dictionary, 0, 2)

Param_type_specifier_dictionary = {
    (0, push_string_routine): 1,
    (1, 'ID'): 2,
    (2, ID_array_param): 3
}
Param_type_specifier.set_transition_dictionary(Param_type_specifier_dictionary, 0, 3)

ID_array_param_dictionary = {(0, 'EPSILON'): 3,
                   (3, add_symbol_param_routine): 1,
                   (0, '['): 2, (2, ']'): 7, (7, add_array_param_routine): 1}
ID_array_param.set_transition_dictionary(ID_array_param_dictionary, 0, 1)

CompoundStmt_dictionary = {(0, '{'): 1, (1, new_scope_routine): 5, (5, DeclarationList): 2, (2, StatementList): 3,
                           (3, '}'): 4, (4, pop_scope_routine): 6}
CompoundStmt.set_transition_dictionary(CompoundStmt_dictionary, 0, 6)

StatementList_dictionary = {(0, StatementList1): 1}
StatementList.set_transition_dictionary(StatementList_dictionary, 0, 1)

StatementList1_dictionary = {(0, Statement): 1, (1, StatementList1): 2, (0, 'EPSILON'): 2}
StatementList1.set_transition_dictionary(StatementList1_dictionary, 0, 2)

Statement_dictionary = {(0, ExpressionStmt): 1, (0, CompoundStmt): 1, (0, SelectionStmt): 1,
                        (0, IterationStmt): 1,
                        (0, ReturnStmt): 1,
                        (0, SwitchStmt): 1}

Statement.set_transition_dictionary(Statement_dictionary, 0, 1)
ExpressionStmt_dictionary = {  # TODO
    (0, Expression): 1, (1, ';'): 5, (5, expression_end_routine): 2,
    (0, 'continue'): 7, (7, check_continue_routine): 8, (8, continue_routine): 3, (3, ';'): 2,
    (0, 'break'): 6, (6, check_break_routine): 9, (9, break_routine): 4, (4, ';'): 2,
    (0, ';'): 2
}

ExpressionStmt.set_transition_dictionary(ExpressionStmt_dictionary, 0, 2)

Expression_dictionary = {
    (0, 'ID'): 1, (1, push_previous_string_routine): 2, (2, Expression_id_start): 3,
    (0, Term_2): 4, (4, Expression_addop_op_start): 5, (5, Expression_relop): 3,
    (0, push_string_routine): 1, (1, 'ID'): 1, (2, Expression_id_start): 3
}
Expression.set_transition_dictionary(Expression_dictionary, 0, 3)

Expression_id_start_dictionary = {
    (0, ID_array): 1, (1, Expression_id_start_1): 2,
    (0, '('): 3, (3, push_zero_routine): 9, (9, Args): 4, (4, ')'): 5, (5, call_routine): 8, (8, Term1): 6,
    (6, Expression_addop_op_start): 7, (7, Expression_relop): 2
}

Expression_id_start.set_transition_dictionary(Expression_id_start_dictionary, 0, 2)

Expression_id_start_1_dictionary = {
    (0, '='): 1, (1, Expression): 5, (5, assignment_routine): 2,
    (0, Term1): 3, (3, Expression_addop_op_start): 4, (4, Expression_relop): 2
}
Expression_id_start_1.set_transition_dictionary(Expression_id_start_1_dictionary, 0, 2)

ID_array_dictionary = {(0, 'EPSILON'): 4, (4, pid_routine): 1,
                  (0, '['): 2, (2, Expression): 3, (3, ']'): 5, (5, get_array_with_index_routine): 1}
ID_array.set_transition_dictionary(ID_array_dictionary, 0, 1)

SelectionStmt_dictionary = {(0, 'if'): 1, (1, '('): 2,
                            (2, Expression): 3, (3, ')'): 4, (4, save_routine): 8, (8, Statement): 5,
                            (5, 'else'): 6, (6, jp_save_routine): 9, (9, Statement): 10, (10, jp_routine): 7}

SelectionStmt.set_transition_dictionary(SelectionStmt_dictionary, 0, 7)

IterationStmt_dictionary = {(0, 'while'): 9, (9, new_while_scope_routine): 10, (10, tmp_save_routine): 1, (1, '('): 6,
                            (6, label_routine): 2, (2, Expression): 3, (3, ')'): 7,
                            (7, save_routine): 4, (4, Statement): 5, (5, while_routine): 8}
IterationStmt.set_transition_dictionary(IterationStmt_dictionary, 0, 8)

ReturnStmt_dictionary = {(0, 'return'): 1, (1, Function_return): 2}
ReturnStmt.set_transition_dictionary(ReturnStmt_dictionary, 0, 2)
Function_return_dictionary = {(0, ';'): 3, (3, return_routine): 1,
                      (0, Expression): 2, (2, return_value_routine) : 4, (4, ';'): 1}

Function_return.set_transition_dictionary(Function_return_dictionary, 0, 1)

SwitchStmt_dictionary = {(0, 'switch'): 9, (9, new_switch_scope_routine): 1, (1, '('): 10, (10, tmp_save_routine): 2,
                         (2, Expression): 3,
                         (3, ')'): 4, (4, '{'): 5, (5, CaseStmts): 6,
                         (6, DefaultStmt): 11, (11, jp_switch_routine): 7, (7, '}'): 8}
SwitchStmt.set_transition_dictionary(SwitchStmt_dictionary, 0, 8)

CaseStmts_dictionary = {(0, 'case'): 1, (1, push_string_routine): 2, (2, 'num'): 3, (3, cmp_save_routine): 4,
                        (4, ":"): 5, (5, StatementList): 6, (6, save_routine): 7, (7, CaseStmts1): 8, (0, 'EPSILON'): 8}
CaseStmts.set_transition_dictionary(CaseStmts_dictionary, 0, 8)

CaseStmts1_dictionary = {(0, CaseStmt): 1, (1, CaseStmts1): 2, (0, 'EPSILON'): 2}
CaseStmts1.set_transition_dictionary(CaseStmts1_dictionary, 0, 2)

CaseStmt_dictionary = {(0, 'case'): 5, (5, push_string_routine): 1, (1, 'NUM'): 6, (6, cmp_save_1_routine): 2,
                       (2, ':'): 3, (3, StatementList): 4, (4, save_routine): 7}
CaseStmt.set_transition_dictionary(CaseStmt_dictionary, 0, 7)

DefaultStmt_dictionary = {(0, 'default'): 4, (4, default_routine): 1, (1, ':'): 2, (2, StatementList): 3,
                          (0, 'EPSILON'): 3}
DefaultStmt.set_transition_dictionary(DefaultStmt_dictionary, 0, 3)

Expression_relop_dictionary = {(0, Relop): 1, (1, Expression_addop): 3, (3, relop_routine): 2, (0, 'EPSILON'): 2}
Expression_relop.set_transition_dictionary(Expression_relop_dictionary, 0, 2)

Relop_dictionary = {(0, '=='): 3, (3, push_one_routine): 1, (0, '<'): 2, (2, push_zero_routine): 1}
Relop.set_transition_dictionary(Relop_dictionary, 0, 1)

Expression_addop_dictionary = {(0, Term): 1, (1, Expression_addop_op_start): 2}
Expression_addop.set_transition_dictionary(Expression_addop_dictionary, 0, 2)

Expression_addop_op_start_dictionary = {(0, Addop): 1, (1, Term): 2, (2, addop_routine): 4, (4, Expression_addop_op_start): 3,
                                  (0, 'EPSILON'): 3}
Expression_addop_op_start.set_transition_dictionary(Expression_addop_op_start_dictionary, 0, 3)

Addop_dictionary = {(0, '+'): 2, (2, push_one_routine): 1, (0, '-'): 3, (3, push_zero_routine): 1}
Addop.set_transition_dictionary(Addop_dictionary, 0, 1)

Term_dictionary = {(0, SignedFactor): 1, (1, Term1): 2}
Term.set_transition_dictionary(Term_dictionary, 0, 2)

Term1_dictionary = {(0, '*'): 1, (1, SignedFactor): 2, (2, mult_routine): 4, (4, Term1): 3, (0, 'EPSILON'): 3}
Term1.set_transition_dictionary(Term1_dictionary, 0, 3)

Term_2_dictionary = {(0, SignedFactor_no_id): 1, (1, Term1): 2}
Term_2.set_transition_dictionary(Term_2_dictionary, 0, 2)

SignedFactor_dictionary = {(0, Factor): 1, (0, '+'): 2, (2, Factor): 1, (0, '-'): 3, (3, Factor): 4,
                           (4, minus_factor_routine): 1}

SignedFactor.set_transition_dictionary(SignedFactor_dictionary, 0, 1)

SignedFactor_no_id_dictionary = {(0, Factor_no_id): 1, (0, '+'): 2, (2, Factor): 1, (0, '-'): 3, (3, Factor): 4,
                             (4, minus_factor_routine): 1}
SignedFactor_no_id.set_transition_dictionary(SignedFactor_no_id_dictionary, 0, 1)

Factor_dictionary = {(0, '('): 1, (1, Expression): 2, (2, ')'): 3,
                     (0, 'ID'): 4, (4, push_previous_string_routine): 6, (6, ID_array_3): 3,
                     (0, 'NUM'): 5, (5, push_number_routine): 3}
Factor.set_transition_dictionary(Factor_dictionary, 0, 3)

Factor_no_id_dictionary = {(0, '('): 1, (1, Expression): 2, (2, ')'): 3, (0, 'NUM'): 4, (4, push_number_routine): 3}
Factor_no_id.set_transition_dictionary(Factor_no_id_dictionary, 0, 3)

Args_dictionary = {
    (0, ArgList): 1, (0, 'EPSILON'): 1}
Args.set_transition_dictionary(Args_dictionary, 0, 1)

ID_array_3_dictionary = {(0, ID_array): 1, (0, '('): 2, (2, Args): 3, (3, ')'): 4, (4, call_routine): 1}
ID_array_3.set_transition_dictionary(ID_array_3_dictionary, 0, 1)

ArgList_dictionary = {(0, Expression): 1, (1, push_arg_routine): 2, (2, ArgList1): 3}
ArgList.set_transition_dictionary(ArgList_dictionary, 0, 3)

ArgList1_dictionary = {(0, ','): 1, (1, Expression): 2, (2, push_arg_routine): 3, (3, ArgList1): 4,
                       (0, 'EPSILON'): 4}
ArgList1.set_transition_dictionary(ArgList1_dictionary, 0, 4)


get_char()
get_new_token()
parser.running = True
parser(program, height=0)
print('**************************************************')
PB.print_self()
output_file.close()
lexical_error_file.close()
parser_error_file.close()
input_file.close()
combine_errors()
semantic_error_file.close()
