---
icon: fontawesome/solid/scroll
---

# Using Scripts

By now you know how to define an SBA sequence. In the last example, we created a sequence to pick up an item from the floor.

Here's what that code looked like:

```java
runner.runSBAs(
    new MotorSBA(armMotor, 0.5, 100),
    new ServoSBA(clawServo, 1),
    new MotorSBA(armMotor, 0.5, 200),
    new ServoSBA(clawServo, 0.5),
    new WaitSBA(500),
    new MotorSBA(armMotor, 0.5, 100)
);
```

However, this SBA framework has its own scripting language. Read on to learn how to use it.

## What is a Scripting Language?

A scripting language is essentially a simplified programming language. This also means the syntax and logic are similar. Scripting languages are good for focused languages, like with SBA's.

## The SBA Scripting Language

The SBA Scripting language (SBASL) syntax is structured like below:

```
COMMAND param1 param2 param3 etc
```

Each line consists of a command, then parameters, each separated by spaces.

Here are the supported commands:

```asm
MOTOR <motor> <power> <target>
MOTOR <motor> <target>
SERVO <servo> <target>
WAIT <ms>
```

Here's the sequence from the last example in SBASL:

```asm
MOTOR armMotor 0.5 100
SERVO clawServo 1
MOTOR armMotor 0.5 200
SERVO clawServo 0.5
WAIT 500
MOTOR armMotor 0.5 100
```

This is a lot simpler than the Java version!

## The SBALexer

Now, to run the script we just created, we'll need a `SBALexer`. 

Let's modify the sample TeleOp from [How it Works](howitworks.md):

```java hl_lines="5 11 18-23 26 37"
@TeleOp(name="MyTeleOp")
public class MyTeleOp extends OpMode {
    private DcMotorEx armMotor;
    private Servo clawServo;
    private SBALexer lexer;

    @Override
    public void init() {
        armMotor = hardwareMap.get(DcMotorEx.class, "armMotor");
        clawServo = hardwareMap.get(Servo.class, "clawServo");
        lexer = new SBALexer();
    }

    @Override
    public void loop() {
        if (gamepad1.a) {
            // If A is pressed, run our SBA script
            lexer.runScript("MOTOR armMotor 0.5 100\n" +  
            "SERVO clawServo 1\n" + 
            "MOTOR armMotor 0.5 200\n" + 
            "SERVO clawServo 0.5\n" + 
            "WAIT 500\n" + 
            "MOTOR armMotor 0.5 100");
        } else if (gamepad1.b) {
            // If B is pressed, abort our SBA's
            lexer.stop();

            // Stop moving our arm motor by setting its
            // target position to its current position
            armMotor.setTargetPosition(armMotor.getCurrentPosition());

            // Set arm power to 0
            armMotor.setPower(0);
        }

        // Run the SBALexer loop
        lexer.loop();
    }
}
```

## Motors and Servos

To change which motors and servos are included in the `SBALexer`, you'll need to modify the initializer in `SBALexer.java`. 

```java title="SBALexer.java" hl_lines="7 14"
public class SBALexer {
    ...
    public SBALexer() {
        runner = new SBARunner();

        // Populate motor dictionary
        motorMap.put("armMotor", bot.armMotor);
        // TODO: Figure out how to handle two motors at once (maybe a 2MOTOR operation)

        // Default motor powers
        motorPowers.put("armMotor", 0.5);

        // Populate servo dictionary
        servoMap.put("clawServo", bot.clawServo);
        ...
    }
    ...
}
```

The highlighted lines indicate where motors and servos are setup with the `SBALexer`. Say you have a linear slide on your robot, and you want to use it with `SBALexer`. 

Assuming it's declared as `bot.slideMotor`, you can add this line to the initializer:

```java title="SBALexer.java"
motorMap.put("slideMotor", bot.slideMotor);
```

You can also specifiy a default power to run the motor with.

```java title="SBALexer.java"
motorPowers.put("slideMotor", 0.5);
```

Similarly, you can add a new servo `bot.wristServo` with:

```java title="SBALexer.java"
servoMap.put("wristServo", bot.wristServo);
```

## Constants

You can also make use of constants in `SBALexer`. Also setup in the initializer, you can use constants in place of numbers anywhere in a SBA script.

```java title="SBALexer.java" hl_lines="6-9"
public class SBALexer {
    ...
    public SBALexer() {
        ...
        // Populate constants
        constants.put("clawOpenPos", 1);
        constants.put("clawClosePos", 0.5);
        constants.put("armUpPos", 100);
        constants.put("armDownPos", 200);
        ...
    }
    ...
}
```

You can use these constants in your SBA scripts like in this example that will open the claw:

```asm
SERVO clawServo clawOpenPos
```

Using constants, we can refactor our existing SBA script routine to use constants:

```asm
MOTOR armMotor armUpPos
SERVO clawServo clawOpenPos
MOTOR armMotor armDownPos
SERVO clawServo clawClosePos
WAIT 500
MOTOR armMotor armUpPos
```

## Using the Dashboard

Here's where the real advantage of using SBASL comes in. If you use [FTC Dashboard](https://acmerobotics.github.io/ftc-dashboard/), you can edit static variables in the browser, and view their results live **without changing code and restarting the robot**. This is a huge timesaver during testing.

To make use of the FTC Dashboard with SBASL, let's modify our TeleOp:

```java hl_lines="1 5 12"
@Config
@TeleOp(name="MyTeleOp")
public class MyTeleOp extends OpMode {
    ...
    public static String script = "";
    ...

    @Override
    public void loop() {
        if (gamepad1.a) {
            // If A is pressed, run our SBA script
            lexer.runScript(script);
        }
        ...
    }
}
```

Now you can edit your script from the browser! 

## Newlines in the Dashboard

Unfortunately, you can't enter multiline strings on the FTC Dashboard. To work around this, you can use a substitute newline, like `#!java "/"`. To implement, simply modify one line in your OpMode:

```java
lexer.runScript(script.replace("/", "\n"));
```