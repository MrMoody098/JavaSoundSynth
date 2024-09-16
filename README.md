# JavaSoundSynth

JavaSoundSynth is a simple Java-based sound synthesizer application. It uses the Java Swing framework for the graphical user interface and generates sound based on key presses using the openAL API.

## Features

- **Sound Synthesis**: Generates sound using oscillators.
- **Key Mapping**: Maps keyboard keys to specific frequencies.
- **Graphical Interface**: Provides a simple GUI to interact with the synthesizer.

## Requirements

- Java 8 or higher
- Maven

## Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/MrMoody098/JavaSoundSynth.git
    cd JavaSoundSynth
    ```

2. Build the project using Maven:
    ```sh
    mvn clean install
    ```

## Usage

1. Run the application:
    ```sh
    mvn exec:java -Dexec.mainClass="org.example.JavaSoundSynth"
    ```

2. Use the keyboard to play sounds. The following keys are mapped to frequencies:
    ```
    zxcvbnm,./asdfghjkl;'#qwertyuiop[]
    ```
    
## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.

## Contact

For any questions or feedback, please contact the repository owner.
