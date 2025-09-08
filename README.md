# CodeVoice

**Real-time speech-to-text dictation plugin for JetBrains IDEs**

CodeVoice is a JetBrains IDE plugin that enables developers to write code and documentation more efficiently by converting spoken words into text directly inside their favorite IDE. The plugin provides hands-free coding capabilities, reduces repetitive typing, and improves workflow efficiency through real-time speech recognition.

## 🎯 Vision

CodeVoice helps developers **write code and documentation more efficiently** by converting spoken words into text directly inside JetBrains IDEs. The plugin enables hands-free coding, reduces repetitive typing, and improves workflow efficiency.

## ✨ Features

- **Real-time speech-to-text**: Live transcription with minimal latency
- **Interim results support**: See partial transcriptions as you speak
- **Editor integration**: Seamless text insertion at cursor position
- **Toggle functionality**: Easy start/stop dictation control
- **Thread-safe operations**: Reliable performance in IDE environment
- **Multiple IDE support**: Works with IntelliJ, PyCharm, Rider, and other JetBrains IDEs

## 🚀 Quick Start

### Prerequisites

- JetBrains IDE (IntelliJ IDEA, PyCharm, Rider, etc.)
- Java 21 or higher
- Microphone access

### Installation

1. Clone this repository:
   ```bash
   git clone <repository-url>
   cd codevoice
   ```

2. Build the plugin:
   ```bash
   ./gradlew buildPlugin
   ```

3. Install the plugin in your IDE:
   - Go to `File > Settings > Plugins`
   - Click the gear icon and select `Install Plugin from Disk...`
   - Select the built plugin file from `build/distributions/`

### Usage

1. **Start Dictation**: Right-click in any editor and select "Toggle Dictation" from the context menu, or use the assigned keyboard shortcut
2. **Speak**: Begin speaking - you'll see interim results appear as you talk
3. **Stop Dictation**: Right-click and select "Toggle Dictation" again to stop listening

The plugin will insert transcribed text at your current cursor position, replacing interim results with final transcriptions as speech recognition completes.

## 🏗️ Architecture

### Core Components

```
src/main/kotlin/com/example/codevoice/
├── actions/
│   └── ToggleDictationAction.kt    # Main UI action for starting/stopping dictation
└── stt/
    ├── ISTTService.kt              # Speech-to-text service interface
    └── DummyISTTService.kt         # Demo implementation for testing
```

### Key Classes

- **`ToggleDictationAction`**: Main action class that handles UI interaction and editor integration
- **`ISTTService`**: Interface defining the speech-to-text service contract
- **`DummyISTTService`**: Test implementation that provides sample text for development

### Technology Stack

- **Kotlin**: Primary development language
- **JetBrains Platform SDK**: IDE integration framework
- **Google Cloud Speech-to-Text**: Planned STT service integration
- **Gradle**: Build system and dependency management

## 🛠️ Development

### Setup

1. **Prerequisites**:
   - JDK 21
   - IntelliJ IDEA with Kotlin plugin
   - Gradle 7.x or higher

2. **Development Environment**:
   ```bash
   git clone <repository-url>
   cd codevoice
   ./gradlew runIde
   ```

3. **Running Tests**:
   ```bash
   ./gradlew test
   ```

### Project Structure

```
├── build.gradle.kts              # Build configuration
├── src/main/
│   ├── kotlin/                   # Source code
│   └── resources/META-INF/       # Plugin configuration
├── gradle/                       # Gradle wrapper
└── README.md                     # This file
```

### Code Style

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use ktlint for code formatting
- Maintain >80% test coverage for core logic
- Document public APIs with KDoc

### Threading Model

- Audio capture runs on background threads
- Editor modifications use `WriteCommandAction` for thread safety
- UI updates are dispatched to the EDT using `invokeLater`

## 🔧 Configuration

### Plugin Configuration

The plugin is configured via `src/main/resources/META-INF/plugin.xml`:

```xml
<idea-plugin>
    <id>com.example.speechtotext</id>
    <name>CodeVoice</name>
    <description>Real-time speech-to-text dictation in JetBrains IDEs</description>
    <!-- ... -->
</idea-plugin>
```

### Build Configuration

Key build settings in `build.gradle.kts`:

- **Kotlin Version**: 2.2.0
- **Target JVM**: Java 21
- **IDE Platform**: PyCharm Professional 2025.2
- **Plugin SDK**: IntelliJ Platform 2.9.0

## 🎨 UI Guidelines

### Design Principles

- **Lightweight and unobtrusive**: Minimal impact on IDE workflow
- **Theme consistency**: Adapts to Darcula and light IDE themes
- **Keyboard shortcuts**: Support for quick toggle operations
- **Visual feedback**: Clear indication of listening state

### Status Indicators

- 🟢 **Active**: Microphone is listening (green)
- ⚫ **Inactive**: Dictation is off (gray)
- 🔴 **Error**: Service error or unavailable (red)

## 🔮 Roadmap

### Current Status (MVP)

- ✅ Basic dictation toggle functionality
- ✅ Real-time text insertion
- ✅ Interim and final result handling
- ✅ Thread-safe editor operations

### Planned Features

- 🔄 Google Cloud Speech-to-Text integration
- 🔄 Local Whisper STT fallback
- 🔄 Voice commands for IDE actions
- 🔄 Multi-language support
- 🔄 Punctuation correction
- 🔄 Custom keyboard shortcuts

## 🤝 Contributing

We welcome contributions! Please see our contribution guidelines:

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/amazing-feature`
3. **Commit** changes: `git commit -m 'feat: add amazing feature'`
4. **Push** to branch: `git push origin feature/amazing-feature`
5. **Open** a Pull Request

### Commit Message Format

We follow [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` New features
- `fix:` Bug fixes
- `docs:` Documentation updates
- `style:` Code style changes
- `refactor:` Code refactoring
- `test:` Test additions/updates

### Code Review Process

- All PRs require passing CI checks
- Code must be formatted with ktlint
- Unit tests required for new functionality
- Documentation updates for user-facing changes

## 🔒 Security & Privacy

- **Audio Capture**: Only active when dictation is toggled on
- **Data Storage**: No audio or transcripts stored locally
- **External Services**: User consent required for cloud STT services
- **API Keys**: Never stored in source code - use environment variables

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- **Documentation**: Check this README and inline code documentation
- **Issues**: Report bugs via GitHub Issues
- **Discussions**: Join our GitHub Discussions for questions

## 🙏 Acknowledgments

- **JetBrains**: For the excellent Platform SDK and IDE framework
- **Kotlin Team**: For the amazing programming language
- **Open Source Community**: For inspiration and best practices

---

**Made with ❤️ for the developer community**# CodeVoice
