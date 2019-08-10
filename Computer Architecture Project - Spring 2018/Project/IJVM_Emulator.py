import sys
import time
import random
import PyQt5
from PyQt5.QtWidgets import QApplication, QWidget, QPushButton, QMainWindow, QBoxLayout, QHBoxLayout, QVBoxLayout, \
    QLabel, QLineEdit, QScrollBar, QScrollArea, QTextEdit, QMessageBox, QComboBox
from PyQt5 import QtCore

global enabled_signals, registers, memory, RTL, opcode_encoder, clock, IR, MAR, MDR, PC, MBR, TOS, SP, LV, CPP, TR, A, alu

opcode_encoder = {'bipush': 0x10, 'goto': 0xa7, 'iadd': 0x60, 'ifeq': 0x99, 'iflt': 0x9b, 'if_icmpeq': 0x9f,
              'iinc': 0x84, 'iload': 0x15, 'istore': 0x36, 'isub': 0x64, 'nop': 0x00}

IR = None
MAR = None
MDR = None
PC = None
MBR = None
TOS = None
SP = None
LV = None
CPP = None
TR = None
A = None
RTL = {}
enabled_signals = []
registers = {}
clock = 0
code_length = 0
memory_size = 256
cache_size = 16
throughput_clocks = 0
throughput_instructions = 0
utilization_delay = 0


class CacheCell:
    def __init__(self):
        self.data = 0
        self.address = 0
        self.tag = 0
        self.valid = False
        self.dirty = False


class Cache:
    def __init__(self, size):
        self.size = size
        self.cache_array = [CacheCell() for x in range(size)]
        self.cacheArch = 0  # 0:Direct Mapped 1:2way associative 2:4way associative
        self.replacementPolicy = 0  # 0:LRU 1:MRU 2:FIFO 3:Random 4:LIP
        self.writePolicy = 0  # 0:write around 1:write allocate
        self.time_in = [[] for x in range(self.cacheLength())]
        self.last_used = [[] for x in range(self.cacheLength())]
        self.hit = 0
        self.miss = 0
        self.cache_box = None

    def cacheLength(self):
        if self.cacheArch == 0:
            x = 1
        else:
            x = 2 * self.cacheArch
        return self.size // x

    def isInCache(self, m_addr):
        index = self.cacheAddressMap(m_addr)
        tag = m_addr // self.cacheLength()
        while index < self.size:
            if self.cache_array[index].tag == tag and self.cache_array[index].valid:
                self.cache_box[index].setStyleSheet("QLineEdit { background-color: rgba(0, 255, 0, 0.5); }")
                return True, index
            index += self.cacheLength()
        return False, -1

    def cacheAddressMap(self, m_addr):
        return m_addr % self.cacheLength()

    def mainMemoryAddress(self, index, tag):
        return tag * self.cacheLength() + self.cache_array[index].address

    def getTag(self, m_addr):
        return m_addr // self.cacheLength()

    #################################################

    def update_last_used(self, index, line):
        if self.last_used[line].__contains__(index):
            self.last_used[line].remove(index)
        self.last_used[line].append(index)

    def update_time_in(self, index, line):
        if self.time_in[line].__contains__(index):
            self.time_in[line].remove(index)
        self.time_in[line].append(index)

    def writeCacheCell(self, m_addr, index, data_in):
        tag = self.getTag(m_addr)
        line = self.cacheAddressMap(m_addr)
        self.cache_array[index].tag = tag
        self.cache_array[index].data = data_in
        self.cache_array[index].valid = True
        self.cache_array[index].address = line
        self.cache_box[index].setText(str(hex(data_in)))
        self.cache_box[index].setStyleSheet("QLineEdit { background-color: rgba(255, 0, 0, 0.5); }")
        self.update_time_in(index, line)

    def writeToMemory(self, index):
        m_addr = self.mainMemoryAddress(index, self.cache_array[index].tag)
        memory.memory_write(m_addr * 4, self.cache_array[index].data)

    ##################################################

    def evictionLRU(self, line):
        index = self.last_used[line][0]
        self.last_used[line].remove(index)
        if self.cache_array[index].dirty:
            memory.memory_write(self.mainMemoryAddress(index, self.cache_array[index].tag),
                                self.cache_array[index].data)
        self.cache_array[index].valid = False
        return index

    def evictionMRU(self, line):
        index = self.last_used[line].pop()
        if self.cache_array[index].dirty:
            memory.memory_write(self.mainMemoryAddress(index, self.cache_array[index].tag),
                                self.cache_array[index].data)
        self.cache_array[index].valid = False
        return index

    def evictionFIFO(self, line):
        index = self.time_in[line][0]
        self.time_in[line].remove(index)
        if self.cache_array[index].dirty:
            memory.memory_write(self.mainMemoryAddress(index, self.cache_array[index].tag),
                                self.cache_array[index].data)
        self.cache_array[index].valid = False
        return index

    def evictionRandom(self, line):
        r = random.randint(0, 2 * self.cacheArch - 1)
        index = line + self.cacheLength() * r
        if self.cache_array[index].dirty:
            memory.memory_write(self.mainMemoryAddress(index, self.cache_array[index].tag),
                                self.cache_array[index].data)
        self.cache_array[index].valid = False
        return index

    def evictionLIP(self, line, E):
        r = random.random()
        if E > r:
            return self.evictionMRU(line)
        else:
            return self.evictionLRU(line)

    def eviction(self, line):
        E = 0.3
        if self.replacementPolicy == 0:
            return self.evictionLRU(line)
        if self.replacementPolicy == 1:
            return self.evictionMRU(line)
        if self.replacementPolicy == 2:
            return self.evictionFIFO(line)
        if self.replacementPolicy == 3:
            return self.evictionRandom(line)
        if self.replacementPolicy == 4:
            return self.evictionLIP(line, E)
        return -1
    ############################################

    def reset_color(self):
        for i in range(len(self.cache_box)):
            self.cache_box[i].setStyleSheet("QLineEdit { background-color: rgba(255, 255, 255, 1); }")

    def cache_reset(self):
        for i in range(self.size):
            self.cache_array[i] = CacheCell()
            self.cache_box[i].setText('')
            self.cache_box[i].setStyleSheet("QLineEdit { background-color: rgba(0, 255, 0, 0.5); }")


cache = Cache(cache_size)

######################################


class Memory:
    def __init__(self, size):
        self.size = size
        self.mem_array = [0] * size
        self.mem_textBox = None
        self.rwn_signal = None
        self.ready = True
        self.ready_label = None
        self.start = False
        self.start_label = None
        self.address_t = 0
        self.din_t = 0
        self.counter = 0
        self.d_out = 0

    def set_start(self, rwn, address, din):
        self.start_label.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
        self.address_t = address
        if not rwn:
            self.rwn_signal.setStyleSheet("QLabel { background-color: rgba(255, 0, 0, 0.5); }")
            self.din_t = din
        else:
            self.rwn_signal.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
        self.counter = address % 4 + 1

    def memory_delay(self):
        global utilization_delay
        if self.counter > 0:
            self.counter -= 1
            utilization_delay += 1
            return False
        return True

    def read(self):
        search_res = cache.isInCache(self.address_t // 4)
        index = cache.cacheAddressMap(self.address_t // 4)
        line = index
        self.start = False
        self.start_label.setStyleSheet("QLabel { background-color: rgba(255, 0, 0, 0.5); }")
        self.rwn_signal.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
        if search_res[0]:
            cache.hit += 1
            index = search_res[1]
            cache.update_last_used(index, line)
            self.ready = True
            memory.ready_label.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
            self.d_out = self.mem_array[self.address_t % memory_size] + \
                         self.mem_array[(self.address_t + 1) % memory_size] * 256 + \
                         self.mem_array[(self.address_t + 2) % memory_size] * 256 * 256 + \
                         self.mem_array[(self.address_t + 3) % memory_size] * 256 * 256 * 256
            return self.d_out
        else:
            self.ready = False
            memory.ready_label.setStyleSheet("QLabel { background-color: rgba(255, 0, 0, 0.5); }")
            if cache.cacheArch > 0:
                while index < cache.size:
                    if not cache.cache_array[index].valid:
                        if self.memory_delay():
                            self.ready = True
                            cache.miss += 1
                            memory.ready_label.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
                            self.d_out = self.mem_array[self.address_t % memory_size] + \
                                         self.mem_array[(self.address_t + 1) % memory_size] * 256 + \
                                         self.mem_array[(self.address_t + 2) % memory_size] * 256 * 256 + \
                                         self.mem_array[(self.address_t + 3) % memory_size] * 256 * 256 * 256

                            cache_block = self.mem_array[(self.address_t//4) % memory_size] + \
                                         self.mem_array[(self.address_t//4 + 1) % memory_size] * 256 + \
                                         self.mem_array[(self.address_t//4 + 2) % memory_size] * 256 * 256 + \
                                         self.mem_array[(self.address_t//4 + 3) % memory_size] * 256 * 256 * 256
                            cache.writeCacheCell(self.address_t // 4, index, cache_block)
                            cache.update_last_used(index, line)
                            return self.d_out
                        else:
                            return self.d_out
                    index += cache.cacheLength()

                index = cache.eviction(line)
                if self.memory_delay():
                    self.ready = True
                    cache.miss += 1
                    memory.ready_label.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
                    self.d_out = self.mem_array[self.address_t % memory_size] + \
                                 self.mem_array[(self.address_t + 1) % memory_size] * 256 + \
                                 self.mem_array[(self.address_t + 2) % memory_size] * 256 * 256 + \
                                 self.mem_array[(self.address_t + 3) % memory_size] * 256 * 256 * 256

                    cache_block = self.mem_array[(self.address_t // 4) % memory_size] + \
                                  self.mem_array[(self.address_t // 4 + 1) % memory_size] * 256 + \
                                  self.mem_array[(self.address_t // 4 + 2) % memory_size] * 256 * 256 + \
                                  self.mem_array[(self.address_t // 4 + 3) % memory_size] * 256 * 256 * 256
                    cache.writeCacheCell(self.address_t // 4, index, cache_block)
                    cache.update_last_used(index, line)
                    return self.d_out
            else:
                if cache.cache_array[index].valid and cache.cache_array[index].dirty:
                    cache.writeToMemory(index)

                if self.memory_delay():
                    self.ready = True
                    cache.miss += 1
                    memory.ready_label.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
                    self.d_out = self.mem_array[self.address_t % memory_size] + \
                                 self.mem_array[(self.address_t + 1) % memory_size] * 256 + \
                                 self.mem_array[(self.address_t + 2) % memory_size] * 256 * 256 + \
                                 self.mem_array[(self.address_t + 3) % memory_size] * 256 * 256 * 256

                    cache_block = self.mem_array[(self.address_t // 4) % memory_size] + \
                                  self.mem_array[(self.address_t // 4 + 1) % memory_size] * 256 + \
                                  self.mem_array[(self.address_t // 4 + 2) % memory_size] * 256 * 256 + \
                                  self.mem_array[(self.address_t // 4 + 3) % memory_size] * 256 * 256 * 256
                    cache.writeCacheCell(self.address_t // 4, index, cache_block)
                    return self.d_out

        return self.d_out

    def write(self):
        index = cache.cacheAddressMap(self.address_t // 4)
        line = index
        search_res = cache.isInCache(self.address_t // 4)
        self.rwn_signal.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
        self.start = False
        self.start_label.setStyleSheet("QLabel { background-color: rgba(255, 0, 0, 0.5); }")
        if search_res[0]:
            cache.hit += 1
            index = search_res[1]
            cache.cache_array[index].data = self.din_t
            cache.writeCacheCell(self.address_t // 4, index, self.din_t)
            cache.cache_array[index].dirty = True
            cache.update_last_used(index, line)
            self.ready = True
            memory.ready_label.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
        else:
            self.ready = False
            memory.ready_label.setStyleSheet("QLabel { background-color: rgba(255, 0, 0, 0.5); }")
            if cache.writePolicy:
                if cache.cacheArch > 0:
                    while index < cache.size:
                        if not cache.cache_array[index].valid:
                            if self.memory_delay():
                                self.ready = True
                                cache.miss += 1
                                memory.ready_label.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
                                self.d_out = self.mem_array[self.address_t % memory_size] + \
                                             self.mem_array[(self.address_t + 1) % memory_size] * 256 + \
                                             self.mem_array[(self.address_t + 2) % memory_size] * 256 * 256 + \
                                             self.mem_array[(self.address_t + 3) % memory_size] * 256 * 256 * 256

                                cache_block = self.mem_array[(self.address_t // 4) % memory_size] + \
                                              self.mem_array[(self.address_t // 4 + 1) % memory_size] * 256 + \
                                              self.mem_array[(self.address_t // 4 + 2) % memory_size] * 256 * 256 + \
                                              self.mem_array[(self.address_t // 4 + 3) % memory_size] * 256 * 256 * 256
                                cache.writeCacheCell(self.address_t // 4, index, cache_block)
                                cache.cache_array[index].data = self.din_t
                                cache.cache_array[index].dirty = True
                                cache.update_last_used(index, line)
                                return
                        index += cache.cacheLength()

                    index = cache.eviction(line)
                    if self.memory_delay():
                        self.ready = True
                        cache.miss += 1
                        memory.ready_label.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
                        self.d_out = self.mem_array[self.address_t % memory_size] + \
                                     self.mem_array[(self.address_t + 1) % memory_size] * 256 + \
                                     self.mem_array[(self.address_t + 2) % memory_size] * 256 * 256 + \
                                     self.mem_array[(self.address_t + 3) % memory_size] * 256 * 256 * 256

                        cache_block = self.mem_array[(self.address_t // 4) % memory_size] + \
                                      self.mem_array[(self.address_t // 4 + 1) % memory_size] * 256 + \
                                      self.mem_array[(self.address_t // 4 + 2) % memory_size] * 256 * 256 + \
                                      self.mem_array[(self.address_t // 4 + 3) % memory_size] * 256 * 256 * 256
                        cache.writeCacheCell(self.address_t // 4, index, cache_block)
                        cache.cache_array[index].data = self.din_t
                        cache.cache_array[index].dirty = True
                        cache.update_last_used(index, line)
                        return
                else:
                    if cache.cache_array[index].valid and cache.cache_array[index].dirty:
                        cache.writeToMemory(index)
                    if self.memory_delay():
                        self.ready = True
                        cache.miss += 1
                        memory.ready_label.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
                        self.d_out = self.mem_array[self.address_t % memory_size] + \
                                     self.mem_array[(self.address_t + 1) % memory_size] * 256 + \
                                     self.mem_array[(self.address_t + 2) % memory_size] * 256 * 256 + \
                                     self.mem_array[(self.address_t + 3) % memory_size] * 256 * 256 * 256

                        cache_block = self.mem_array[(self.address_t // 4) % memory_size] + \
                                      self.mem_array[(self.address_t // 4 + 1) % memory_size] * 256 + \
                                      self.mem_array[(self.address_t // 4 + 2) % memory_size] * 256 * 256 + \
                                      self.mem_array[(self.address_t // 4 + 3) % memory_size] * 256 * 256 * 256
                        cache.writeCacheCell(self.address_t // 4, index, cache_block)
                        cache.cache_array[index].data = self.din_t
                        cache.cache_array[index].dirty = True
                        return
            else:
                if self.memory_delay():
                    self.ready = True
                    cache.miss += 1
                    memory.ready_label.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
                    self.memory_write(self.address_t, self.din_t)

    def memory_write(self, addr, din):
        self.mem_array[addr % memory_size] = din & 0xff
        self.mem_array[(addr + 1) % memory_size] = din & 0xff00
        self.mem_array[(addr + 2) % memory_size] = din & 0xff0000
        self.mem_array[(addr + 3) % memory_size] = din & 0xff000000

        self.mem_textBox[(addr % memory_size) // 8][(addr % memory_size) % 8].setText(
            str(hex(din & 0xff)))
        self.mem_textBox[(addr % memory_size) // 8][
            (addr % memory_size) % 8].setStyleSheet(
            "QLineEdit { background-color: rgba(0, 255, 255, 0.5); }")
        self.mem_textBox[((addr + 1) % memory_size) // 8][
            ((addr + 1) % memory_size) % 8].setText(
            str(hex(din & 0xff00)))
        self.mem_textBox[((addr + 1) % memory_size) // 8][
            ((addr + 1) % memory_size) % 8].setStyleSheet(
            "QLineEdit { background-color: rgba(0, 255, 255, 0.5); }")
        self.mem_textBox[((addr + 2) % memory_size) // 8][
            ((addr + 2) % memory_size) % 8].setText(
            str(hex(din & 0xff0000)))
        self.mem_textBox[((addr + 2) % memory_size) // 8][
            ((addr + 2) % memory_size) % 8].setStyleSheet(
            "QLineEdit { background-color: rgba(0, 255, 255, 0.5); }")
        self.mem_textBox[((addr + 3) % memory_size) // 8][
            ((addr + 3) % memory_size) % 8].setText(
            str(hex(din & 0xff000000)))
        self.mem_textBox[((addr + 3) % memory_size) // 8][
            ((addr + 3) % memory_size) % 8].setStyleSheet(
            "QLineEdit { background-color: rgba(0, 255, 255, 0.5); }")

    def reset_array(self):
        self.mem_array = [0] * self.size

    def reset(self):
        self.reset_array()
        for i in range(memory_size // 8):
            for j in range(8):
                self.mem_textBox[i][j].setText(str(0))
                self.mem_textBox[i][j].setReadOnly(0)
                self.mem_textBox[i][j].setStyleSheet("QLineEdit { background-color: rgba(0, 255, 0, 0.5); }")
        self.start = False
        self.start_label.setStyleSheet("QLabel { background-color: rgba(255, 0, 0, 0.5); }")
        self.rwn_signal.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
        self.ready = True
        memory.ready_label.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")

    def reset_color(self):
        self.start_label.setStyleSheet("QLabel { background-color: rgba(255, 0, 0, 0.5); }")
        self.rwn_signal.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
        for i in range(memory_size // 8):
            for j in range(8):
                self.mem_textBox[i][j].setStyleSheet("QLineEdit { background-color: rgba(255, 255, 255, 1); }")


memory = Memory(memory_size)


class RegLayout(QHBoxLayout):
    def __init__(self, key):
        super().__init__()
        self.setDirection(1)
        self.addStretch(1)

        clear_label = QLabel("Clear")
        registers[key].clr_box = clear_label
        clear_label.setAlignment(QtCore.Qt.AlignCenter)
        clear_label.setFixedSize(75, 30)
        self.addWidget(clear_label)

        clear_label.setAutoFillBackground(True)
        clear_label.setStyleSheet("QLabel { background-color: rgba(255, 0, 0, 0.5); }")

        load_label = QLabel("Load")
        registers[key].ld_box = load_label
        load_label.setFixedSize(75, 30)
        load_label.setAlignment(QtCore.Qt.AlignCenter)
        self.addWidget(load_label)

        load_label.setAutoFillBackground(True)
        load_label.setStyleSheet("QLabel { background-color: rgba(255, 0, 0, 0.5); }")

        value = QLineEdit()
        value.setAlignment(QtCore.Qt.AlignCenter)
        value.setFixedSize(75, 30)
        self.addWidget(value)
        registers[key].value_box = value
        value.setText(str(hex(registers[key].value)))

        value.setAutoFillBackground(True)
        # value.setStyleSheet("QLineEdit { background-color: rgba(0, 255, 255, 0.5); }")
        value.setReadOnly(True)

        label = QLabel(registers[key].name + ":")
        label.setFixedSize(75, 30)
        label.setAlignment(QtCore.Qt.AlignCenter)
        self.addWidget(label)


class App(QWidget):
    def __init__(self):
        super().__init__()
        self.title = "IJVM"
        self.left = 600
        self.top = 100
        self.width = 900
        self.height = 800
        self.mem_array = []
        self.next = None
        self.mem_start = None
        self.ready = None
        self.rwn = None
        self.code = QTextEdit()
        self.signal_list = ["rwn", "start", "ready", "n", "z"]
        self.cache_widget = None

        self.initUI()

    def initUI(self):
        self.move(self.left, self.top)
        self.setFixedSize(self.width, self.height)
        self.setWindowTitle(self.title)

        vlayout = QVBoxLayout(self)
        hlayout = QHBoxLayout()
        hlayout_2 = QHBoxLayout()
        vlayout.setDirection(2)
        self.initRegistersUI(hlayout)
        self.initSignals(hlayout)
        self.initCode(vlayout, hlayout)
        self.initMemUI(hlayout_2)
        self.initCache(hlayout_2, vlayout)
        self.initButtons(vlayout)
        vlayout.addStretch(1)

        self.show()

    def initRegistersUI(self, hlayout):
        layout = QVBoxLayout()
        layout.setDirection(2)

        counter = 0
        for i in registers:
            reg = RegLayout(i)
            layout.addLayout(reg)
            counter += 1

        layout.addStretch(1)
        hlayout.addLayout(layout)

    def initSignals(self, h_layout):
        global alu
        layout = QVBoxLayout()

        label = QLabel("rwn")
        label.setFixedSize(150, 30)
        label.setAlignment(QtCore.Qt.AlignCenter)
        self.rwn = label
        self.rwn.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
        layout.addWidget(label)
        memory.rwn_signal = self.rwn

        label = QLabel("start")
        label.setFixedSize(150, 30)
        label.setAlignment(QtCore.Qt.AlignCenter)
        self.mem_start = label
        self.mem_start.setStyleSheet("QLabel { background-color: rgba(255, 0, 0, 0.5); }")
        layout.addWidget(label)
        memory.start_label = self.mem_start

        label = QLabel("ready")
        label.setFixedSize(150, 30)
        label.setAlignment(QtCore.Qt.AlignCenter)
        self.ready = label
        memory.ready_label = self.ready
        memory.ready_label.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
        layout.addWidget(label)

        label = QLabel("N")
        label.setFixedSize(150, 30)
        label.setAlignment(QtCore.Qt.AlignCenter)
        self.n = label
        layout.addWidget(label)
        alu.N_label = label
        label.setStyleSheet("QLabel { background-color: rgba(255, 0, 0, 0.5); }")

        label = QLabel("Z")
        label.setFixedSize(150, 30)
        label.setAlignment(QtCore.Qt.AlignCenter)
        self.z = label
        layout.addWidget(label)
        alu.Z_label = label
        label.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")

        label = QLabel("Shift")
        label.setFixedSize(150, 30)
        label.setAlignment(QtCore.Qt.AlignCenter)
        self.shift = label
        layout.addWidget(label)
        alu.shift_label = label
        label.setStyleSheet("QLabel { background-color: rgba(255, 0, 0, 0.5); }")

        hlayout = QHBoxLayout()
        label = QLabel("ALU:")
        label.setFixedSize(75, 30)
        label.setAlignment(QtCore.Qt.AlignCenter)
        hlayout.addWidget(label)
        alu_box = QLineEdit()
        alu_box.setFixedSize(75, 30)
        alu_box.setAlignment(QtCore.Qt.AlignCenter)
        alu_box.setReadOnly(1)
        hlayout.addWidget(alu_box)
        layout.addLayout(hlayout)

        hlayout = QHBoxLayout()
        label = QLabel("Bus Control:")
        label.setFixedSize(75, 30)
        label.setAlignment(QtCore.Qt.AlignCenter)
        hlayout.addWidget(label)
        bus_box = QLineEdit()
        bus_box.setFixedSize(75, 30)
        bus_box.setAlignment(QtCore.Qt.AlignCenter)
        bus_box.setReadOnly(1)
        hlayout.addWidget(bus_box)
        layout.addLayout(hlayout)
        alu.bus_box = bus_box

        layout.addStretch(1)
        h_layout.addLayout(layout)
        alu.control_box = alu_box

    def initCode(self, vlayout, hlayout):
        self.code.setFixedSize(350, 400)
        hlayout.addStretch(1)
        hlayout.addWidget(self.code)

        vlayout.addLayout(hlayout)

    def initMemUI(self, hlayout):
        widget = QWidget()

        main_layout = QVBoxLayout()
        main_layout.setDirection(2)
        widget.setLayout(main_layout)

        for i in range(memory_size // 8):
            lineedit_list = []
            layout = QHBoxLayout()
            layout.setDirection(0)

            row_label = QLabel(str(hex(8 * i)))
            row_label.setFixedSize(65, 30)
            row_label.setAlignment(QtCore.Qt.AlignCenter)
            layout.addWidget(row_label)

            for j in range(8):
                lineedit = QLineEdit("0")
                lineedit.setFixedSize(65, 30)
                lineedit.setAlignment(QtCore.Qt.AlignCenter)
                lineedit_list += [lineedit]
                layout.addWidget(lineedit)

            layout.addStretch(1)
            main_layout.addLayout(layout)
            self.mem_array += [lineedit_list]

        main_layout.addStretch(1)
        widget.setLayout(main_layout)

        scroll = QScrollArea()
        scroll.setVerticalScrollBarPolicy(QtCore.Qt.ScrollBarAlwaysOn)
        scroll.setHorizontalScrollBarPolicy(QtCore.Qt.ScrollBarAlwaysOff)
        scroll.setWidgetResizable(False)
        scroll.setWidget(widget)

        memory_label = QLabel("MEMORY")
        memory_label.setAlignment(QtCore.Qt.AlignCenter)

        offset_tags = QHBoxLayout()
        null_label = QLabel("Offset")
        null_label.setFixedSize(75, 30)
        null_label.setAlignment(QtCore.Qt.AlignCenter)
        offset_tags.addWidget(null_label)
        for i in range(8):
            label = QLabel(str(i))
            label.setFixedSize(65, 30)
            label.setAlignment(QtCore.Qt.AlignCenter)
            offset_tags.addWidget(label)
        offset_tags.addStretch(1)

        main_main_layout = QVBoxLayout()
        main_main_layout.addWidget(memory_label)
        main_main_layout.addLayout(offset_tags)
        main_main_layout.addWidget(scroll)

        hlayout.addLayout(main_main_layout)
        memory.mem_textBox = self.mem_array

    def initCache(self, hlayout, vlayout):
        vertical = QVBoxLayout()
        vertical.addStretch(1)

        self.cache_arch = QComboBox()
        self.cache_arch.addItem("Direct Map")
        self.cache_arch.addItem("2Way Associative")
        self.cache_arch.addItem("4Way Associative")
        vertical.addWidget(self.cache_arch)

        self.replacement = QComboBox()
        self.replacement.addItem("LRU")
        self.replacement.addItem("MRU")
        self.replacement.addItem("FIFO")
        self.replacement.addItem("Random")
        self.replacement.addItem("LIP")
        vertical.addWidget(self.replacement)

        self.write_policy = QComboBox()
        self.write_policy.addItem("Write Around")
        self.write_policy.addItem("Write Allocate")
        vertical.addWidget(self.write_policy)

        vertical.addStretch(1)
        self.cache_button = QPushButton("Cache")
        self.cache_button.setDisabled(1)
        self.cache_button.clicked.connect(self.open_cache_widget)
        vertical.addWidget(self.cache_button)

        hlayout.addLayout(vertical)
        vlayout.addLayout(hlayout)

    def initButtons(self, vlayout):
        layout = QHBoxLayout()

        self.start_button = QPushButton("Start")
        self.start_button.clicked.connect(self.start)
        self.next = QPushButton("Next")
        self.next.clicked.connect(lambda: code_execute())
        self.next.setDisabled(1)
        reset = QPushButton("Reset")
        reset.clicked.connect(lambda: self.app_reset())

        layout.addWidget(self.start_button)
        layout.addWidget(self.next)
        layout.addWidget(reset)

        layout.addStretch(1)

        vlayout.addLayout(layout)

    def start(self):
        global throughput_instructions, throughput_clocks, utilization_delay
        run_flag = True
        if not self.memory_allocate():
            run_flag = False
        elif not self.compile_code():
            run_flag = False

        if run_flag:
            self.lock_memory()
            self.code.setReadOnly(1)
            self.next.setEnabled(1)
            self.start_button.setDisabled(1)
        self.cache_button.setDisabled(0)

        if self.cache_arch.currentText() == "Direct Map":
            cache.cacheArch = 0
        elif self.cache_arch.currentText() == "2Way Associative":
            cache.cacheArch = 1
        elif self.cache_arch.currentText() == "4Way Associative":
            cache.cacheArch = 2

        if self.replacement.currentText() == "LRU":
            cache.replacementPolicy = 0
        elif self.replacement.currentText() == "MRU":
            cache.replacementPolicy = 1
        elif self.replacement.currentText() == "FIFO":
            cache.replacementPolicy = 2
        elif self.replacement.currentText() == "Random":
            cache.replacementPolicy = 3
        elif self.replacement.currentText() == "LIP":
            cache.replacementPolicy = 4

        if self.write_policy.currentText() == "Write Around":
            cache.writePolicy = 0
        elif self.write_policy.currentText() == "Write Allocate":
            cache.writePolicy = 1

        widget = CacheWidget(cache.cacheArch)
        self.cache_widget = widget

        self.cache_arch.setDisabled(1)
        self.replacement.setDisabled(1)
        self.write_policy.setDisabled(1)

        throughput_clocks = 0
        throughput_instructions = 0
        utilization_delay = 0
        cache.miss = 0
        cache.hit = 0


    def memory_allocate(self, alert_box=None):
        global memory
        k = 0
        for i in range(memory_size // 8):
            for j in range(8):
                try:
                    memory.mem_array[k] = int(self.mem_array[i][j].text())
                    k += 1
                except:
                    print(self.mem_array[i][j].text())
                    alert_msg = "Wrong value in memory block number " + str(i + j)
                    QMessageBox.about(alert_box, 'Error', alert_msg)
                    print(alert_msg)
                    memory.reset_array()
                    return False
        return True

    def compile_code(self):
        global code_length
        code_text = str(self.code.toPlainText())
        for char in '\t':
            code_text = code_text.replace(char, ' ')
        code_line = code_text.split('\n')
        code_length = len(code_line)
        ind = registers['PC'].value
        for i in range(len(code_line)):
            if len(code_line[i]) == 0:
                continue
            code_line[i] = code_line[i].lower()
            code_line[i] = code_line[i].split(' ')
            ind = self.code_to_memory(ind, code_line[i], i + 1)
            if not ind:
                return False
        return True

    def code_to_memory(self, ind, code_line, line_number, alert_box=None):
        try:
            memory.mem_array[ind] = opcode_encoder[code_line[0]]
            self.mem_array[ind // 8][ind % 8].setText(str(hex(opcode_encoder[code_line[0]])))
        except:
            alert_msg = 'syntax error in line:' + str(line_number)
            QMessageBox.about(alert_box, 'Error', alert_msg)
            print(code_line)
            return False
        ind += 1
        for i in range(1, len(code_line)):
            memory.mem_array[ind] = int(code_line[i])
            self.mem_array[ind // 8][ind % 8].setText(str(hex(int(code_line[i]))))
            ind += 1
        return ind

    def lock_memory(self):
        for i in range(memory_size // 8):
            for j in range(8):
                self.mem_array[i][j].setReadOnly(1)

    def app_reset(self):
        global  fetch_done, clock, throughput_clocks, throughput_instructions, code_length, cache, code_line_counter, alu
        color_reset()
        memory.reset()
        cache.cache_reset()
        for key in registers:
            registers[key].clear()
        self.code.setText('')
        self.code.setReadOnly(0)
        self.next.setDisabled(1)
        self.start_button.setDisabled(0)
        self.cache_button.setDisabled(1)
        self.replacement.setDisabled(0)
        self.cache_arch.setDisabled(0)
        self.write_policy.setDisabled(0)
        clock = 0
        throughput_clocks = 0
        throughput_instructions = 0
        code_length = 0
        alu.SC = 0
        cache.hit = 0
        cache.miss = 0
        fetch_done = False
        code_line_counter = 0

    def open_cache_widget(self):
        global cache
        self.cache_widget.show()


class ALU:
    def __init__(self):
        self.control_signal = '011000'  # textbox
        self.control_box = None
        self.Z = False
        self.Z_label = None
        self.N = False
        self.N_label = None
        self.result = A.value
        self.shift = False
        self.shift_label = None
        self.bus_box = None
        self.save_z = False
        self.save_n = False
        self.SC = 0

    def set_N_Z(self):
        alu.control_box.setText(self.control_signal)
        self.Z = (self.result == 0)
        if self.Z:
            self.Z_label.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
        else:
            self.Z_label.setStyleSheet("QLabel { background-color: rgba(255, 0, 0, 0.5); }")
        self.N = (self.result < 0)
        if self.N:
            self.N_label.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
        else:
            self.N_label.setStyleSheet("QLabel { background-color: rgba(255, 0, 0, 0.5); }")
        if self.shift:
            self.shift_label.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")
            self.shift = False
            self.result *= 256

    def pass_A(self):
        self.result = A.value
        self.control_signal = '011000'
        self.set_N_Z()
        return self.result

    def pass_reg(self, register):
        self.bus_box.setText(register.bus_num)
        self.result = register.value
        self.control_signal = '010100'
        self.set_N_Z()
        return self.result

    def not_A(self):
        self.result = ~A.value
        self.control_signal = '011010'
        self.set_N_Z()
        return self.result

    def not_reg(self, register):
        self.bus_box.setText(register.bus_num)
        self.result = ~register.value
        self.control_signal = '101100'
        self.set_N_Z()
        return self.result

    def add(self, register):
        self.bus_box.setText(register.bus_num)
        self.result = A.value + register.value
        self.control_signal = '111100'
        self.set_N_Z()
        return self.result

    def add_increase(self, register):
        self.bus_box.setText(register.bus_num)
        self.result = A.value + register.value + 1
        self.control_signal = '111101'
        self.set_N_Z()
        return self.result

    def increase_A(self):
        self.result = A.value + 1
        self.control_signal = '111001'
        self.set_N_Z()
        return self.result

    def increase_reg(self, register):
        self.bus_box.setText(register.bus_num)
        self.result = register.value + 1
        self.control_signal = '110101'
        self.set_N_Z()
        return self.result

    def sub(self, register):
        self.bus_box.setText(register.bus_num)
        self.result = register.value - A.value
        self.control_signal = '111111'
        self.set_N_Z()
        return self.result

    def decrease_reg(self, register):
        self.bus_box.setText(register.bus_num)
        self.result = register.value - 1
        self.control_signal = '110110'
        self.set_N_Z()
        return self.result

    def neg_A(self):
        self.result = -A.value
        self.control_signal = '111011'
        self.set_N_Z()
        return self.result

    def AND(self, register):
        self.bus_box.setText(register.bus_num)
        self.result = A.value & register.value
        self.control_signal = '001100'
        self.set_N_Z()
        return self.result

    def OR(self, register):
        self.bus_box.setText(register.bus_num)
        self.result = A.value | register.value
        self.control_signal = '011100'
        self.set_N_Z()
        return self.result

    def zero(self):
        self.result = 0
        self.control_signal = '010000'
        self.set_N_Z()
        return self.result

    def one(self):
        self.result = 1
        self.control_signal = '110001'
        self.set_N_Z()
        return self.result

    def minus_one(self):
        self.result = -1
        self.control_signal = '110010'
        self.set_N_Z()
        return self.result


class CacheWidget(QWidget):
    global cache_size

    def __init__(self, cache_arch):
        super().__init__()
        global cache
        self.cache_cells = [None] * cache_size
        self.setWindowTitle("Cache")
        self.setFixedSize((2 ** cache_arch + 1) * 100, cache_size // (2 ** cache_arch) * 50)

        vlayout = QVBoxLayout()

        hlayout = QHBoxLayout()
        label = QLabel("Tag")
        hlayout.addWidget(label)
        label.setFixedSize(75, 30)
        label.setAlignment(QtCore.Qt.AlignCenter)
        for i in range(2 ** cache_arch):
            label = QLabel(str(i))
            label.setFixedSize(75, 30)
            label.setAlignment(QtCore.Qt.AlignCenter)
            hlayout.addWidget(label)

        vlayout.addLayout(hlayout)

        for i in range(cache_size // (2 ** cache_arch)):
            hlayout = QHBoxLayout()
            label = QLabel(str(str(i)))
            label.setFixedSize(75, 30)
            label.setAlignment(QtCore.Qt.AlignCenter)
            hlayout.addWidget(label)
            for j in range(2 ** cache_arch):
                cache_cell = QLineEdit()
                cache_cell.setAlignment(QtCore.Qt.AlignCenter)
                cache_cell.setReadOnly(1)
                cache_cell.setFixedSize(75, 30)
                # cache_cell.setText(str(i + j * (2 ** (4 - cache_arch))))
                self.cache_cells[i + j * (2 ** (4 - cache_arch))] = cache_cell
                hlayout.addWidget(cache_cell)

            # hlayout.addStretch(1)
            vlayout.addLayout(hlayout)

        self.setLayout(vlayout)
        # vlayout.addStretch(1)
        # print("i'm here", cache_arch)
        cache.cache_box = self.cache_cells
        self.show()


fetch_done = False

fetch = ["memory.set_start(1, PC.value, 0)",
         "IR.load(memory.read() % 256)",
         "PC.load(alu.increase_reg(PC))"]

bipush = ["memory.set_start(1, PC.value, 0)\nMAR.load(alu.increase_reg(SP))\nSP.load(alu.increase_reg(SP))",
          "MBR.load(memory.read() % 256)",
          "MDR.load(alu.pass_reg(MBR))\nTOS.load(alu.pass_reg(MBR))",
          "memory.set_start(0, MAR.value * 4, MDR.value)\nPC.load(alu.increase_reg(PC))",
          "memory.write()"]

goto = ["TR.load(alu.decrease_reg(PC))",
        "memory.set_start(1, PC.value, 0)\nPC.load(alu.increase_reg(PC))",
        "MBR.load(memory.read() % 256)",
        "memory.set_start(1, PC.value, 0)\nalu.shift =  True\nA.load(alu.pass_reg(MBR))",
        "MBR.load(memory.read() % 256)",
        "A.load(alu.OR(MBR))",
        "PC.load(alu.add(TR))"]

iadd = ["MAR.load(alu.decrease_reg(SP)), SP.load(alu.decrease_reg(SP))",
        "memory.set_start(1, MAR.value * 4, 0)\nA.load(alu.pass_reg(TOS))",
        "MDR.load(memory.read())",
        "TOS.load(alu.add(MDR))\nMDR.load(alu.add(MDR))",
        "memory.set_start(0, MAR.value * 4, MDR.value)",
        "memory.write()"]

ifeq = ["MAR.load(alu.decrease_reg(SP))\nSP.load(alu.decrease_reg(SP))",
        "memory.set_start(1, MAR.value * 4, 0)\nTR.load(alu.pass_reg(TOS))",
        "MDR.load(memory.read())",
        "TOS.load(alu.pass_reg(MDR))",
        "alu.pass_reg(TR)\nalu.save_z = alu.Z",
        "if alu.save_z:\n\tTR.load(alu.decrease_reg(PC))\nelse:\n\tPC.load(alu.increase_reg(PC))",
        "if alu.save_z:\n\tmemory.set_start(1, PC.value, 0)\n\tPC.load(alu.increase_reg(PC))\n"
        "else:\n\tPC.load(alu.increase_reg(PC))\n\talu.SC = 100",
        "MBR.load(memory.read() % 256)",
        "memory.set_start(1, PC.value, 0)\nalu.shift = True\nA.load(alu.pass_reg(MBR))",
        "MBR.load(memory.read() % 256)",
        "A.load(alu.OR(MBR))",
        "PC.load(alu.add(TR))"]

iflt = ["MAR.load(alu.decrease_reg(SP))\nSP.load(alu.decrease_reg(SP))",
        "memory.set_start(1, MAR.value * 4, 0)\nTR.load(alu.pass_reg(TOS))",
        "MDR.load(memory.read())",
        "TOS.load(alu.pass_reg(MDR))",
        "alu.pass_reg(TR)\nalu.save_n = alu.N\nprint(alu.save_n)",
        "if alu.save_n:\n\tTR.load(alu.decrease_reg(PC))\nelse:\n\tPC.load(alu.increase_reg(PC))",
        "if alu.save_n:\n\tmemory.set_start(1, PC.value, 0)\n\tPC.load(alu.increase_reg(PC))\n"
        "else:\n\tPC.load(alu.increase_reg(PC))\n\talu.SC = 100",
        "MBR.load(memory.read() % 256)",
        "memory.set_start(1, PC.value, 0)\nalu.shift = True\nA.load(alu.pass_reg(MBR))",
        "MBR.load(memory.read() % 256)",
        "A.load(alu.OR(MBR))",
        "PC.load(alu.add(TR))"]

if_icmpeq = ["MAR.load(alu.decrease_reg(SP))\nSP.load(alu.decrease_reg(SP))",
             "memory.set_start(1, MAR.value * 4, 0)\nMAR.load(alu.decrease_reg(SP))\n"
             "SP.load(alu.decrease_reg(SP))",
             "MDR.load(memory.read())\nTR.load(alu.pass_reg(TOS))",
             "memory.set_start(1, MAR.value * 4, 0)\nA.load(alu.pass_reg(MDR))",
             "MDR.load(memory.read())",
             "TOS.load(alu.pass_reg(MDR))",
             "alu.sub(TR)\nalu.save_z = alu.Z",
             "if alu.save_z:\n\tTR.load(alu.decrease_reg(PC))\nelse:\n\tPC.load(alu.increase_reg(PC))",
             "if alu.save_z:\n\tmemory.set_start(1, PC.value, 0)\n\tPC.load(alu.increase_reg(PC))\n"
             "else:\n\tPC.load(alu.increase_reg(PC))\n\talu.SC = 100",
             "MBR.load(memory.read() % 256)",
             "memory.set_start(1, PC.value, 0)\nalu.shift = True\nA.load(alu.pass_reg(MBR))",
             "MBR.load(memory.read() % 256)",
             "A.load(alu.OR(MBR))",
             "PC.load(alu.add(TR))"]

iinc = ["memory.set_start(1, PC.value, 0)\nPC.load(alu.increase_reg(PC))",
        "MBR.load(memory.read() % 256)\n A.load(alu.pass_reg(LV))",
        "memory.set_start(1, PC.value, 0)\nMAR.load(alu.add(MBR))",
        "MBR.load(memory.read() % 256)",
        "memory.set_start(1, MAR.value * 4, 0)",
        "MDR.load(memory.read())",
        "A.load(alu.pass_reg(MDR))",
        "MDR.load(alu.add(MBR))",
        "memory.set_start(0, MAR.value * 4, MDR)\nPC.load(alu.increase_reg(PC))",
        "memory.write()"]

iload = ["memory.set_start(1, PC.value, 0)\nA.load(alu.pass_reg(LV))",
         "MBR.load(memory.read() % 256)",
         "MAR.load(alu.add(MBR))",
         "memory.set_start(1, MAR.value * 4, 0)\nMAR.load(alu.increase_reg(SP))\nSP.load(alu.increase_reg(SP))",
         "MDR.load(memory.read())",
         "memory.set_start(0, MAR.value * 4, MDR.value)\nPC.load(alu.increase_reg(PC))",
         "memory.write()\nTOS.load(MDR.value)"]

istore = ["memory.set_start(1, PC.value, 0)\nA.load(alu.pass_reg(LV))",
          "MBR.load(memory.read() % 256)",
          "MDR.load(alu.pass_reg(TOS))",
          "MAR.load(alu.add(MBR))",
          "memory.set_start(0, MAR.value * 4, MDR.value)\nMAR.load(alu.decrease_reg(SP))\nSP.load(alu.decrease_reg(SP))",
          "memory.write()",
          "memory.set_start(1, MAR.value * 4, 0)\nPC.load(alu.increase_reg(PC))",
          "MDR.load(memory.read())",
          "TOS.load(alu.pass_reg(MDR))"]

isub = ["MAR.load(alu.decrease_reg(SP)), SP.load(alu.decrease_reg(SP))",
        "memory.set_start(1, MAR.value * 4, 0)\nA.load(alu.pass_reg(TOS))",
        "MDR.load(memory.read())",
        "TOS.load(alu.sub(MDR))\nMDR.load(alu.sub(MDR))",
        "memory.set_start(0, MAR.value * 4, MDR.value)",
        "memory.write()"]

nop = [" "]

opcode_decoder = {0x15: iload, 0x10: bipush, 0xa7: goto, 0x60: iadd, 0x99: ifeq, 0x9b: iflt,
                  0x9f: if_icmpeq, 0x84: iinc, 0x36: istore, 0x64: isub, 0x00: nop}


code_line_counter = 0


def code_execute(alert_box=None):
    global throughput_instructions, throughput_clocks, code_line_counter, code_length
    color_reset()
    global clock, fetch_done, fetch, alu
    # print(clock)
    clock += 1
    throughput_clocks += 1
    if not fetch_done:
        exec(fetch[alu.SC])
        if memory.ready:
            alu.SC += 1
        if alu.SC >= len(fetch):
            alu.SC = 0
            fetch_done = True
            # print("fetch done!")
            throughput_instructions += 1
    else:
        function = opcode_decoder[IR.value]
        print(function[alu.SC])
        exec(function[alu.SC])
        print(alu.SC)
        if memory.ready:
            alu.SC += 1
        if alu.SC >= len(function):
            alu.SC = 0
            fetch_done = False
            # print("execute done")
            code_line_counter += 1
            if code_line_counter >= code_length:
                alert_msg = "Average Throughput: " + str(throughput_instructions / throughput_clocks) +\
                            "\nCache Misses: " + str(cache.miss) + "\tCache Hits: " + str(cache.hit) + \
                            "\nCache HitRate: " + str(cache.hit/(cache.hit + cache.miss)) + \
                            "\n Utilization: " + str(100 - (throughput_clocks - utilization_delay)/ throughput_clocks * 100)
                QMessageBox.about(alert_box, 'LOG', alert_msg)

    # print("clocks ", throughput_clocks)
    # print("instructions ", throughput_instructions)


def color_reset():
    for key in registers:
        registers[key].reset_color()

    memory.reset_color()
    alu.shift_label.setStyleSheet("QLabel { background-color: rgba(255, 0, 0, 0.5); }")
    cache.reset_color()


class Register:
    global enabled_signals

    def __init__(self, name, initial_value, bus_num):
        self.name = name
        self.initial_value = initial_value
        self.value = initial_value
        self.ld = False
        self.clr = False
        self.value_box = None
        self.ld_box = None
        self.clr_box = None
        self.bus_num = bus_num

    def clear(self):
        self.value = self.initial_value
        self.value_box.setText(str(hex(self.value)))
        self.clr = True
        self.clr_box.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")

    def load(self, value):
        self.value = value
        self.value_box.setText(str(hex(value)))
        self.value_box.setStyleSheet("QLineEdit { background-color: rgba(0, 255, 255, 0.5); }")
        self.ld = True
        self.ld_box.setStyleSheet("QLabel { background-color: rgba(0, 255, 0, 0.5); }")

    def reset_color(self):
        self.value_box.setStyleSheet("QLineEdit { background-color: rgba(255, 255, 255, 1); }")
        self.ld_box.setStyleSheet("QLabel { background-color: rgba(255, 0, 0, 0.5); }")
        self.clr_box.setStyleSheet("QLabel { background-color: rgba(255, 0, 0, 0.5); }")


def init_registers():
    global registers, MAR, MDR, PC, MBR, TOS, SP, LV, CPP, TR, A, IR

    IR = Register("IR", 0, '000')
    MAR = Register("MAR", 0, '000')
    MDR = Register("MDR", 0, '111')
    PC = Register("PC", 0, '110')
    MBR = Register("MBR", 0, '101')
    TOS = Register("TOS", 0, '100')
    SP = Register("SP", 48, '011')
    LV = Register("LV", 32, '010')
    CPP = Register("CPP", 16, '001')
    TR = Register("TR", 0, '000')
    A = Register("A", 0, '000')

    registers = {'IR': IR, 'MAR': MAR, 'MDR': MDR, 'PC': PC, 'MBR': MBR, 'TOS': TOS, 'SP': SP, 'LV': LV, 'CPP': CPP,
                 'TR': TR,
                 'A': A}


if __name__ == '__main__':
    init_registers()
    alu = ALU()
    app = QApplication(sys.argv)
    main_win = App()
    sys.exit(app.exec_())
