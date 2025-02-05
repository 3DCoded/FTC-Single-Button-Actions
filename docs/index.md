---
nocomments: true
---

# FTC Single Button Actions

Use the SAME code for repeated actions between your TeleOp and Autonomous.

## What is it?

FTC Single Button Actions (SBA's) is a framework designed to execute several tasks on your robot sequentially, with minimal effort.

SBA's helped us, FTC Bruinbots #13599, to win 1st place Control Award twice in a row in the 2024-2025 Into the Deep season.

## How does it work?

FTC SBA's work by breaking down complex motion sequences into frozen snapshots, called frames. Think of this like a stop-motion recording of your robot, except intead of being stuck on a screen, this recording can happen in reality.

First, you break your sequence down into steps. For example, to pick up something from the ground with a claw, you'll need to:

1. Open claw
1. Lower arm
1. Close claw
1. Lift arm

These can be represented as motor and servo movements (positions are for illustration purposes only):

1. Move `clawServo` to position `1`
1. Move `armMotor` to position `500`
1. Move `clawServo` to position `0.5`
1. Move `armMotor` to position `1000`

These can then be translated into SBA's (more on that later).

Now, to actually run these SBA's, the actions are simply played back, with each action waiting for the previous one to finish before starting itself.

## Installation

...

## Tutorial

[:fontawesome-solid-graduation-cap: Tutorial](toc.md){.md-button}