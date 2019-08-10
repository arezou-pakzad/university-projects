all: wc

wc: wc.o map.o main.o
	gcc wc.o -o wc
	gcc map.o -o map
	gcc main.o -o main

wc.o: wc.c
	gcc -c -g wc.c

map.o: map.c
	gcc -c map.c

main.o: main.c
	gcc -c main.c

clean:
	rm -f *o wc

