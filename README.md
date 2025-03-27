# AudioKeepAlive

The program generates a low-amp, nearly inaudible tone to keep Bluetooth headphones, DACs, or other audio devices from going into sleep or idle mode due to inactivity.

The tone is generated at a frequency of 22 kHz, which is typically beyond the human hearing range, ensuring that the device stays active without any audible noise, unless external interference causes issue.

---

### Requirements:
- Java installed on your system.

### Running the Program:

1. **Compile the Java Program**:
   - Download or clone the repository containing the program.
   - Compile via command line:

     ```bash
     javac AudioKeepAlive.java
     ```

2. **Run the Program**:
   - Once compiled, run the program with:

     ```bash
     java AudioKeepAlive
     ```

   - The program will continuously generate a very quiet (sometimes inaudible) sine wave to keep the audio device (BT-devices or DACs with sleep thresholds) active.

---

## Run on Startup

You can configure **AudioKeepAlive** to run automatically when Windows starts if wanted, by creating a batch script and adding it to the Windows startup folder. 
Alternatively by compiling it to an executable and adding that instead.
