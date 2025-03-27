import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioSystem;

/**
 * The class generates a constant inaudible tone to keep a DAC or BT-device awake.
 * It intends to be used for audio devices that tend to "turn off" or 
 * sleep at even the slightest audio inactivity.
 * 
 * Uses sine wave signal at 22 kHz to in ideal conditions not be audible.
 */
public class AudioKeepAlive {

    /**
     * Initializes and starts playback of a very quiet (in some cases inaudible) tone.
     * It writes a very low-amplitude sine wave to the audio output line in loop.
     */
    public static void main(String[] args) {
        
        // =========================== //
        // ===== Local Variables ===== //
        // =========================== //
        
        float sampleRate = 44100.0f; // 44.1 kHz
        // Low amplitude to avoid being audible, but still "enable" most audio devices.
        double amplitude = 0.00005;
        // Frequency of the tone (22 kHz, ultrasonic if no noticable interference occurs)
        double frequency = 22000.0;     
        int bufferSize = 2048;

        try {
            
            // ============================ //
            // ===== Audio Formatting ===== //
            // ============================ //
            
            AudioFormat format = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED, // Pulse-code modulation encoding
                    sampleRate,                      
                    16,            // 16 bits per sample
                    1,             // Mono channel
                    2,             // 2 bytes per frame (16-bit per channel)
                    sampleRate,    // Frame rate (== sample rate)
                    false          // Little-endian bytes!
            );
            
            // ====================================== //
            // ===== Construct Audio OutputLine ===== //
            // ====================================== //
            
            // Get a line to play the sound
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            // SourceDataLine for playback: 
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, bufferSize);
            line.start();  // Start playback
            
            // =========================================== //
            // ===== Continously construct sine-wave ===== //
            // =========================================== //
            
            // Phase setup for sine wave generation
            double phase = 0.0;
            // Phase increment for selected frequency
            double phaseIncrement = 2 * Math.PI * frequency / sampleRate;
            byte[] buffer = new byte[bufferSize];  // Buffer for audio samples

            while (true) {  // Infinite loop to generating tone.
                for (int i = 0; i < buffer.length; i += 2) {

                    // Calculate the sine wave sample using the formula:
                    // w(t) = sin(2pi*f*t + k)
                    // Where: 
                    //   - A is the amplitude (we scale it by Short.MAX_VALUE 
                    //       for 16-bit output since sinewav is between -1 and 1)
                    //   - f is the frequency of the tone
                    //   - t is the time (we use sample index, scaled by the sample rate)
                    //   - k is the phase, we just leave it at 0.
                    short sample = (short) (Math.sin(phase) * amplitude * Short.MAX_VALUE);
                    
                    // Write the low byte and high byte of the sample to the buffer
                    buffer[i] = (byte) (sample & 0xFF);
                    buffer[i + 1] = (byte) ((sample >> 8) & 0xFF);
                    
                    // Update phase for next sample and reduce phase
                    // to prevent overflow since it adds indefinitely.
                    phase += phaseIncrement;
                    if (phase >= 2 * Math.PI) {
                        phase -= 2 * Math.PI;
                    }
                }
                // Send generated buffer to output line
                line.write(buffer, 0, buffer.length);
            }

        } catch (LineUnavailableException e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }
    }
}
