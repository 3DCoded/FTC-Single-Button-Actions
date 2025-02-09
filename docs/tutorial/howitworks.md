---
icon: fontawesome/solid/wrench
---

# How it Works (in detail)

On the homepage, a brief explanation of how SBA's work was given. Here, a deeper and more technical explanation will be given.

!!! note "Don't be intimidated by the length of this page! There's a lot of useful information here that will help you debug and optimize your SBA's"

## The `SBA` Interface

All SBA actions are their own class, implementing the `SBA` interface below:

```java
public interface SBA {
    boolean sanity();
    void init();
    void loop();
    boolean isBusy();
}
```

It has four functions: `sanity()`, `init()`, `loop()`, and `isBusy()`.

Firstly, the `sanity()` function is run as a safety feature. If `sanity()` returns `false`, the entire SBA is aborted. You can use `sanity()` to make sure your robot doesn't break in the middle of SBA.

Next, the `init()` function is run. This is where you set up your SBA.

Then, the `loop()` function is run. This is repeated until `isBusy()` returns `false`, when your SBA completes (e.g. your motor encoder position hits your target position (1)).
{.annotate}

1. You shouldn't wait until your encoder position is **equal** to your target position, as this will make your motor move back and forth almost indefinitely, freezing your robot. Instead, you can calculate the difference from your current position to the target position, then check if it is below a certain tolerance value.
```java
return Math.abs(curPos - targetPos) > tolerance;
```

## Prebuilt SBA's

While you can jump right in and create your own class implementing the `SBA` interface, you might want to start with a pre-built SBA to learn how to use SBA's.

Here are the built-in SBA's.

### MotorSBA

This will drive a `DcMotorEx` to a target position using an encoder. It has the following required parameters:

- `DcMotorEx motor` The motor to drive
- `double power` The power to drive the motor with
- `int targetPos` The target position to drive the motor to

and optional parameters:

- `int tolerance` The tolerance to drive the motor to the target position with
- `int minPos` The minimum position the encoder may be at for `sanity()` to pass. Ignored if not set.
- `int maxPos` The maximum position the encoder may be at for `sanity()` to pass. Ignored if not set.

Usage example:

```java
// Move arm to position 1000 with power 0.5
new MotorSBA(armMotor,  0.5,     1000);
//           Motor      Power     Target

// Move linear slide to position 2000 with
// power 0.6, but only if it is currently
// between position 1000 and 1500.
new MotorSBA(slideMotor, 0.6,   2000,   1000,           1500);
//           Motor       Power  Target  Minimum Start   Maximum Start
```

### ServoSBA

This will drive a `Servo` to a target position. It has the following required parameters:

- `Servo servo` The servo to drive
- `double targetPos` The target position to drive the servo to

Usage example:

```java
// Move claw servo to position 0.85
new ServoSBA(clawServo, 0.85);
//           Servo      Target
```

### WaitSBA

This will wait a given number of milliseconds (1).
{.annotate}

1. `WaitSBA` is legal in FTC, as it doesn't use `sleep()`. If your OpMode uses `sleep()`, stopping won't work and you won't pass the robot inspection.

It takes in one required parameter:

- `double waitMs` The number of milliseconds to wait for

Usage example:

```java
// Wait for 500ms (.5s)
new WaitSBA(500);
//          Milliseconds
```

## Running SBA's

Now that you understand the basics of how SBA's work, it's time to learn how to actually run one!

### The SBARunner

Running a SBA is controlled by the `SBARunner` class. To run an SBA, you create an instance of the `SBARunner` class, and use the `runSBAs()` function.

Example:

```java
// Create the runner
SBARunner runner = new SBARunner();

// Create our SBA
MotorSBA sba = new MotorSBA(armMotor, 0.5, 1000);

// Run the SBA
runner.runSBAs(sba);
```

It's that simple...almost.

### Mainloop

We've told the `SBARunner` what SBA to run, but we haven't told it to run it yet. To run it, you'll need to use the `loop()` function. You can place this in the loop of your TeleOp OpMode, in a state machine loop, or simply in a while loop in autonomous. 

Example for TeleOp.

```java
@TeleOp(name="MyTeleOp")
public class MyTeleOp extends OpMode {
    // Variables, initialization, etc.
    @Override
    public void loop() {
        // Controller handling, telemetry, etc.

        // Run the SBARunner loop
        runner.loop();
    }
}
```

### Multiple SBA's

Now, like the name suggests, `runSBAs()` isn't limited just to a single SBA. You can pass in any number of SBA's to `runSBAs()`, and it will execute each one sequentially.

Example:

```java
// Create the runner
SBARunner runner = new SBARunner();

// Create our SBA's
MotorSBA sba1 = new MotorSBA(armMotor, 0.5, 1000);
ServoSBA sba2 = new ServoSBA(clawServo, 0.85);

// Run the SBA's
runner.runSBAs(sba1, sba2);
```

We can also create our SBA's inline. Example:

```java
// Create the runner
SBARunner runner = new SBARunner();

// Run the SBA's
runner.runSBAs(
    new MotorSBA(armMotor, 0.5, 1000),
    new ServoSBA(clawServo, 0.85)
);
```

### Aborting an SBA

If an SBA goes wrong, you can use `runner.stop()`. An example is provided in the TeleOp sample below.

!!! warning "Motors and Encoders"
    If you abort an SBA that uses motors, the motors will still try to run to their target positions. To stop this, you can set the target position of each motor to its current position, and set its power to zero.

    ```java
    // Abort our SBA's
    runner.stop();

    // Stop moving our arm motor by setting its
    // target position to its current position
    armMotor.setTargetPosition(armMotor.getCurrentPosition());

    // Set arm power to 0
    armMotor.setPower(0);
    ```

### Sample TeleOp

???+ note "Sample TeleOp"
    ```java
    @TeleOp(name="MyTeleOp")
    public class MyTeleOp extends OpMode {
        private DcMotorEx armMotor;
        private Servo clawServo;
        private SBARunner runner;

        @Override
        public void init() {
            armMotor = hardwareMap.get(DcMotorEx.class, "armMotor");
            clawServo = hardwareMap.get(Servo.class, "clawServo");
            runner = new SBARunner();
        }

        @Override
        public void loop() {
            if (gamepad1.a) {
                // If A is pressed, run our SBA's
                runner.runSBAs(
                    new MotorSBA(armMotor, 0.5, 1000),
                    new ServoSBA(clawServo, 0.85)
                );
            } else if (gamepad1.b) {
                // If B is pressed, abort our SBA's
                runner.stop();

                // Stop moving our arm motor by setting its
                // target position to its current position
                armMotor.setTargetPosition(armMotor.getCurrentPosition());

                // Set arm power to 0
                armMotor.setPower(0);
            }

            // Run the SBARunner loop
            runner.loop();
        }
    }
    ```