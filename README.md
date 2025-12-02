# Advent Of Code — Kotlin Runner

This repository contains a minimal Kotlin setup and an `InputReader` utility to load Advent of Code inputs.

How input is resolved (in this order):

- Pass a file path as the first program argument: `./gradlew run --args "path/to/myinput.txt"`
- Use a resource file at `src/main/resources/inputs/day{N}{P}.txt` (example: `day1a.txt`).
- If neither is provided, the program reads from stdin (useful for piping).
