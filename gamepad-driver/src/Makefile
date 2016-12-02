CONFIG_MODULE_SIG=n

obj-m += gamepad_km.o

all:
	make -C /lib/modules/$(shell uname -r)/build M=$(shell pwd) modules

clean:
	rm *.o *.ko *.order *.symvers *.mod.c
