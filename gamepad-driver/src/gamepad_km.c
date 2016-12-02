#include <linux/input.h>
#include <asm/uaccess.h>
#include <linux/fs.h>
#include <linux/pci.h>
#include <linux/platform_device.h>
#include <linux/module.h>


#define GPD_DPAD_UP_BTN 0
#define GPD_DPAD_DOWN_BTN 1
#define GPD_DPAD_RIGHT_BTN 2
#define GPD_DPAD_LEFT_BTN 3
#define GPD_ACT_UP_BTN 4
#define GPD_ACT_DOWN_BTN 5
#define GPD_ACT_RIGHT_BTN 6
#define GPD_ACT_LEFT_BTN 7
#define GPD_R1_BTN 8
#define GPD_R2_BTN 9
#define GPD_L1_BTN 10
#define GPD_L2_BTN 11
#define GPD_START_BTN 12
#define GPD_SELECT_BTN 13


struct input_dev *gpd_input_device;         // I/O device
static struct platform_device *gpd_device;  // Device structure


static ssize_t write_gamepad(struct device *dev, 
                             struct device_attribute *attr, 
                             const char *buf, size_t count)
{
    int btn = 0, cmd = 0;

    sscanf(buf, "%d%d", &btn, &cmd);
    printk("> GAMEPAD: %d %d\n", btn, cmd);

    if (cmd < 0 || cmd > 1) {
        return count;
    }

    switch (btn) {
        case GPD_DPAD_UP_BTN:
            input_report_key(gpd_input_device, BTN_DPAD_UP, cmd);
            break;
        case GPD_DPAD_DOWN_BTN:
            input_report_key(gpd_input_device, BTN_DPAD_DOWN, cmd);
            break;
        case GPD_DPAD_LEFT_BTN:
            input_report_key(gpd_input_device, BTN_DPAD_LEFT, cmd);
            break;
        case GPD_DPAD_RIGHT_BTN:
            input_report_key(gpd_input_device, BTN_DPAD_RIGHT, cmd);
            break;
        case GPD_ACT_UP_BTN:
            input_report_key(gpd_input_device, BTN_NORTH, cmd);
            break;
        case GPD_ACT_DOWN_BTN:
            input_report_key(gpd_input_device, BTN_SOUTH, cmd);
            break;
        case GPD_ACT_LEFT_BTN:
            input_report_key(gpd_input_device, BTN_WEST, cmd);
            break;
        case GPD_ACT_RIGHT_BTN:
            input_report_key(gpd_input_device, BTN_EAST, cmd);
            break;
        case GPD_L1_BTN:
            input_report_key(gpd_input_device, BTN_TL, cmd);
            break;
        case GPD_L2_BTN:
            input_report_key(gpd_input_device, BTN_TL2, cmd);
            break;
        case GPD_R1_BTN:
            input_report_key(gpd_input_device, BTN_TR, cmd);
            break;
        case GPD_R2_BTN:
            input_report_key(gpd_input_device, BTN_TR2, cmd);
            break;
        case GPD_SELECT_BTN:
            input_report_key(gpd_input_device, BTN_SELECT, cmd);
            break;
        case GPD_START_BTN:
            input_report_key(gpd_input_device, BTN_START, cmd);
            break;
        default:
            printk("virtual gamepad: invalid button code - %d\n", btn);
    }

    input_sync(gpd_input_device);
    return count;
}


DEVICE_ATTR(gamepad, 0644, NULL, write_gamepad);

static struct attribute *gpd_attrs[] = {
    &dev_attr_gamepad.attr,
    NULL
};


static struct attribute_group gpd_attr_group = {
    .attrs = gpd_attrs,
};

static int __init init_gamepad(void)
{
    gpd_device = platform_device_register_simple("gamepad", -1, NULL, 0);
    if (IS_ERR(gpd_device)) {
        printk("init_gamepad: error\n");
        return PTR_ERR(gpd_device);
    }

    sysfs_create_group(&gpd_device->dev.kobj, &gpd_attr_group);

    gpd_input_device = input_allocate_device();
    if (!gpd_input_device) {
        printk("input_gamepad: invalid input_allocate_device\n");
        return -ENOMEM;
    }

    set_bit(EV_KEY, gpd_input_device->evbit);
    set_bit(BTN_DPAD_UP, gpd_input_device->keybit);
    set_bit(BTN_DPAD_DOWN, gpd_input_device->keybit);
    set_bit(BTN_DPAD_RIGHT, gpd_input_device->keybit);
    set_bit(BTN_DPAD_LEFT, gpd_input_device->keybit);

    set_bit(BTN_SOUTH, gpd_input_device->keybit);
    set_bit(BTN_NORTH, gpd_input_device->keybit);
    set_bit(BTN_WEST, gpd_input_device->keybit);
    set_bit(BTN_EAST, gpd_input_device->keybit);

    set_bit(BTN_TL, gpd_input_device->keybit);
    set_bit(BTN_TR, gpd_input_device->keybit);
    set_bit(BTN_TL2, gpd_input_device->keybit);
    set_bit(BTN_TR2, gpd_input_device->keybit);

    set_bit(BTN_SELECT, gpd_input_device->keybit);
    set_bit(BTN_START, gpd_input_device->keybit);

    gpd_input_device->name = "Virtual Android Gamepad";
    gpd_input_device->id.bustype = BUS_VIRTUAL;
    gpd_input_device->id.vendor  = 0x0000;
    gpd_input_device->id.product = 0x0000;
    gpd_input_device->id.version = 0x0000;

    input_register_device(gpd_input_device);

    printk("virtual gamepad: kernel module was initialized sucessful\n");
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
MODULE_AUTHOR("Titov Artem BMSTU 2016");