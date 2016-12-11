
obj-m += virtual_gamepad_driver.o
ccflags-y := -std=gnu99

all:
	make -C /lib/modules/$(shell uname -r)/build M=$(shell pwd) modules

clean:
	rm *.o *.ko *.order *.symvers *.mod.c
