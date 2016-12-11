#include <linux/input.h>
#include <asm/uaccess.h>
#include <linux/fs.h>
#include <linux/pci.h>
#include <linux/platform_device.h>
#include <linux/module.h>


#define GPD_DPAD_UP_BTN         0
#define GPD_DPAD_DOWN_BTN       1
#define GPD_DPAD_RIGHT_BTN      2
#define GPD_DPAD_LEFT_BTN       3
#define GPD_ACT_UP_BTN          4
#define GPD_ACT_DOWN_BTN        5
#define GPD_ACT_RIGHT_BTN       6
#define GPD_ACT_LEFT_BTN        7
#define GPD_R1_BTN              8
#define GPD_R2_BTN              9
#define GPD_L1_BTN              10
#define GPD_L2_BTN              11
#define GPD_START_BTN           12
#define GPD_SELECT_BTN          13

#define GPD_BTN_COUNT           14
#define GPD_PREFIX      "virtual gamepad: "


static int gpd_btn[] = {
    GPD_DPAD_UP_BTN, GPD_DPAD_DOWN_BTN, GPD_DPAD_LEFT_BTN, GPD_DPAD_RIGHT_BTN,
    GPD_ACT_UP_BTN, GPD_ACT_DOWN_BTN, GPD_ACT_LEFT_BTN, GPD_ACT_RIGHT_BTN,
    GPD_L1_BTN, GPD_L2_BTN, GPD_R1_BTN, GPD_R2_BTN,
    GPD_START_BTN, GPD_SELECT_BTN,
};

static int linux_btn[] = {
    BTN_DPAD_UP, BTN_DPAD_DOWN, BTN_DPAD_LEFT, BTN_DPAD_RIGHT,
    BTN_NORTH, BTN_SOUTH, BTN_WEST, BTN_EAST,
    BTN_TL, BTN_TL2, BTN_TR, BTN_TR2,
    BTN_START, BTN_SELECT,
};

struct input_dev *gpd_input_device;
static struct platform_device *gpd_device;


static ssize_t write_gamepad(struct device *dev, 
                             struct device_attribute *attr, 
                             const char *buf, size_t count)
{
    int btn = 0, cmd = 0;

    sscanf(buf, "%d%d", &btn, &cmd);

    if (cmd < 0 || cmd > 1) {
        printk(GPD_PREFIX"event code - %d\n", cmd);
        return count;
    }

    int is_reported = 0;
    for (int i = 0; i < GPD_BTN_COUNT && !is_reported; i++) {
        if (gpd_btn[i] == btn) {
            input_report_key(gpd_input_device, linux_btn[i], cmd);
            is_reported = 1;
        }
    }

    if (!is_reported) {
        printk(GPD_PREFIX"invalid button code - %d\n", btn);
        return count;
    }

    input_sync(gpd_input_device);
    return count;
}


DEVICE_ATTR(commands, 0644, NULL, write_gamepad);

static struct attribute *gpd_attrs[] = {
    &dev_attr_commands.attr,
    NULL
};


static struct attribute_group gpd_attr_group = {
    .attrs = gpd_attrs,
};

static int __init init_gamepad(void)
{
    gpd_device = platform_device_register_simple("virtual_gamepad", -1, NULL, 0);
    if (IS_ERR(gpd_device)) {
        printk(GPD_PREFIX"platform register error\n");
        return PTR_ERR(gpd_device);
    }

    sysfs_create_group(&gpd_device->dev.kobj, &gpd_attr_group);

    gpd_input_device = input_allocate_device();
    if (!gpd_input_device) {
        printk(GPD_PREFIX"invalid input_allocate_device\n");
        return -ENOMEM;
    }

    set_bit(EV_KEY, gpd_input_device->evbit);
    for (int i = 0; i < GPD_BTN_COUNT; i++) {
        set_bit(linux_btn[i], gpd_input_device->keybit);
    }

    gpd_input_device->name = "Virtual Gamepad";
    gpd_input_device->id.bustype = BUS_VIRTUAL;
    gpd_input_device->id.vendor  = 0x0000;
    gpd_input_device->id.product = 0x0000;
    gpd_input_device->id.version = 0x0001;

    input_register_device(gpd_input_device);

    printk(GPD_PREFIX"kernel module was initialized sucessful\n");
    return 0;
}

static void cleanup_gamepad(void)
{
    input_unregister_device(gpd_input_device);

    sysfs_remove_group(&gpd_device->dev.kobj, &gpd_attr_group);

    platform_device_unregister(gpd_device);
}

module_init(init_gamepad);
module_exit(cleanup_gamepad);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Titov Artem <pbi62007@yandex.ru>");
