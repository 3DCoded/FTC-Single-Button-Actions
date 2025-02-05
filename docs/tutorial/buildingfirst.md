# Building Your First SBA

Now that you know how SBA's work, it's time to create your own!

On our robot, we used SBA's to curl and uncurl our arm, which required several coordinated motions of both our arm and wrist to prevent damaging our robot.

![](https://placehold.co/600x400/?text=Image+Or+Video+Of+Robot)

For this example, we'll create a simpler SBA sequence for a simpler arm. 

![](a5cc2fc5.png)

This arm has only one motor to move the arm, and one servo to open or close the claw. 

## Perform the Motion in Real Life

First, we need to perform the motion in real life. To do this, you'll need to add telemetry to your OpMode so we can gather positioning data. 

Here's an example for the arm motor and claw servo:

```java
@TeleOp(name="Main TeleOp")
public class MainTeleOp extends OpMode {
    ...
    @Override
    public void loop() {
        ...

        telemetry.addData("armMotor", armMotor.getCurrentPosition()+"=>"+armMotor.getTargetPosition());
        telemetry.addData("clawServo", clawServo.getPosition());
        telemetry.update();
    }
}
```

Now, using your existing controls, move the arm to the starting position, like in the above diagram.

Your telemetry should look something like this:

```
armMotor: 101=>100
clawServo: 1
```

In this case, that means that the starting position is:

1. `armMotor` at `100`
2. `clawServo` at `1`

Now, continue with the motion. In this case, the next step is to move the arm down.

![](56f5e097.png)

Check your telemetry.

```
armMotor: 199=>200
clawServo: 1
```

The next step of our motion is:

1. `armMotor` to `200`

The next step is to close the claw.

![](c4c6f46f.png)

Telemetry:

```
armMotor: 199=>200
clawServo: 0.5
```

The motion is:

1. `clawServo` to `0.5`

Finally, we have to lift our arm back up to its starting position.

![](6fd76214.png)

Telemetry:

```
armMotor: 99=>100
clawServo: 0.5
```

The motion is:

1. `armMotor` to `100`

Now we that we recorded the motion in real life, it's time to turn it into code!

## Converting to SBA's

In the previous section, we recorded the motion of the robot in real life. Now, we have to convert it to SBA's. 

First, create a table like this:

| Step | `armMotor` | `clawServo` |
| - | - | - |
| Start | 100 | 1 |
| Lower Arm | 200 | |
| Close Claw | | 0.5 |
| Lift Arm | 100 | |

Any blanks in the table represent no change from the previous step.

Now, we'll go through each row and convert it to SBA's:

| Step | `armMotor` | `clawServo` | SBA |
| - | - | - | - |
| Start | 100 | 1 | `#!java new MotorSBA(armMotor, 0.5, 100)`<br />`#!java new ServoSBA(clawServo, 1)` |
| Lower Arm | 200 | | `#!java new MotorSBA(armMotor, 0.5, 200)` |
| Close Claw | | 0.5 | `#!java new ServoSBA(clawServo, 0.5)` |
| Lift Arm | 100 | | `#!java new MotorSBA(armMotor, 0.5, 100)` |

For our arm power, we're using `0.5`. 

Finally, put these together in a list, and pass it to `runSBAs()`

```java
// Modified from https://3dcoded.github.io/FTC-Single-Button-Actions/tutorial/howitworks/#__codelineno-10-18
runner.runSBAs(
    new MotorSBA(armMotor, 0.5, 100), // Starting arm
    new ServoSBA(clawServo, 1), // Starting claw
    new MotorSBA(armMotor, 0.5, 200), // Lower arm
    new ServoSBA(clawServo, 0.5), // Close claw
    new MotorSBA(armMotor, 0.5, 100) // Lift arm
);
```

You can attach this to a button on your controller, and...

![](2f3ef73b.png)

It went through the motions, but the arm came up before the claw had a chance to close!

## Waiting

Here's the issue: We command the claw to close (move to `0.5`), but have no way of knowing when the claw actually finishes closing. To work around this, we need to add a wait after we close the claw. Luckly, we have a prebuilt `WaitSBA` ready to go!

```java
// Modified from https://3dcoded.github.io/FTC-Single-Button-Actions/tutorial/howitworks/#__codelineno-10-18
runner.runSBAs(
    new MotorSBA(armMotor, 0.5, 100),
    new ServoSBA(clawServo, 1),
    new MotorSBA(armMotor, 0.5, 200),
    new ServoSBA(clawServo, 0.5), // Close claw
    new WaitSBA(500), // <-- Give the claw 500ms to close
    new MotorSBA(armMotor, 0.5, 100)
);
```