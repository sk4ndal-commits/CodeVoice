# CodeVoice – Coding Guidelines

This document defines the **vision**, **project idea**, **target group**, **styling rules**, *
*coding standards**, **constraints**, and **non-goals** for the development of **JetDictate**, a *
*real-time speech-to-text plugin** for JetBrains IDEs using **Kotlin** and **Google Cloud
Speech-to-Text** (or other STT services).

---

## Vision

JetDictate helps developers **write code and documentation more efficiently** by converting spoken
words into text directly inside JetBrains IDEs. The plugin enables hands-free coding, reduces
repetitive typing, and improves workflow efficiency.

---

## Project Idea

JetDictate is a **real-time dictation plugin** that:

- Captures audio from the microphone.
- Streams speech to a cloud or local STT engine.
- Inserts live-transcribed text into the editor with minimal latency.
- Supports interim results, final results, and basic editing commands.

The MVP focuses on **smooth, real-time dictation** with Google Cloud STT streaming, a toggle button,
and handling of interim results.

---

## Target Group & Usage

- **Target Group:**
    - Developers, coders, and writers using JetBrains IDEs.
    - Professionals who prefer voice input for coding, documentation, or note-taking.
    - Users with accessibility needs who require hands-free interaction.

- **Primary Usage:**
    - Most users will run JetDictate inside **IntelliJ, PyCharm, Rider**, or other JetBrains IDEs.
    - Ideal for writing comments, documentation, or boilerplate code without typing.
    - Usage scenarios:
        - Real-time dictation while coding.
        - Quick voice input for multi-line comments or docstrings.
        - Voice-activated IDE actions (future feature).

---

## UI / Styling Guidelines

### Plugin UI

- Toolbar icon for **toggle on/off** dictation.
- Status indicator to show when the microphone is active.
- Optional floating mini-widget for **“Listening…”** or interim transcript display.

### Color Palette

- Minimalistic and consistent with JetBrains **Darcula** or default IDE theme.
- Status indicators:
    - Active: **#10B981 (green)**
    - Inactive: **#6B7280 (gray)**
    - Error: **#EF4444 (red)**

### UX Principles

- Keep UI **lightweight and unobtrusive**.
- Ensure **keyboard shortcuts** are supported for quick toggling.
- Make interim results **replaceable** instead of duplicating text.
- Support multiple caret insertion points in the editor.

---

## General Principles

- Prioritize **readability, maintainability, and stability**.
- Follow **Kotlin coding standards** and JetBrains plugin development best practices.
- Use **thread-safe audio streaming** and editor updates.
- Design for **low latency and real-time feedback**.

---

## Plugin Structure & Conventions

### Code Structure

- `src/main/kotlin/com/example/codevoice/`
    - `actions/` → Editor actions (toggle dictation).
    - `stt/` → Speech-to-text integration modules (Google STT, local STT).
    - `ui/` → Toolbar icons, indicators, optional widgets.
- Keep business logic in services, not directly in action classes.

### Conventions

- **Kotlin style guide:** https://kotlinlang.org/docs/coding-conventions.html
- Use **type safety**, nullable handling, and `runWriteAction` for editor modifications.
- Follow **threading best practices** for audio capture and API streaming.

### Audio / STT

- Audio capture using `javax.sound.sampled`.
- Real-time streaming to **Google Cloud STT** or fallback local engine (Whisper).
- Interim results should be replaceable; final results persist in editor.
- Buffer size and sample rate should balance **latency and recognition accuracy**.

---

## Testing

- Unit test audio capture, STT integration, and editor insertion logic.
- Use **JUnit5** or Kotlin test frameworks.
- Aim for **>80% coverage** for core logic.
- Mock STT API for CI tests.

---

## Git & Collaboration

- Branch naming: `feature/`, `fix/`, `chore/`.
- PRs must pass **linting and tests** before merging.
- Commit messages follow **Conventional Commits** (e.g., feat: add interim text replacement).

---

## Tooling

- **Kotlin linting**: ktlint.
- **Formatting**: IntelliJ code formatter.
- **CI/CD**: GitHub Actions for linting, unit tests, and build verification.

---

## Security & Privacy

- Do not store API keys or credentials in code; use `.env` or IDE environment variables.
- Only capture audio while dictation is active.
- Respect user privacy: no storage or sharing of audio or transcripts without consent.

---

## Constraints

### Scope Constraints

- MVP focuses on **real-time dictation** and basic editor insertion.
- Advanced features like **voice-to-code commands**, **punctuation correction**, or multi-language
  support are **future improvements**.

### Technical Constraints

- JetBrains Plugin SDK + Kotlin only.
- STT: Google Cloud API (primary), optional Whisper local fallback.
- Supports major JetBrains IDEs: IntelliJ, PyCharm, Rider.

### Design Constraints

- Minimal UI, consistent with JetBrains IDE themes.
- Avoid disrupting the coding workflow.

### Performance Constraints

- Dictation latency < 500ms (interim text).
- Minimal CPU and memory footprint.

### Security & Privacy Constraints

- Microphone only active when dictation is toggled on.
- Do not send audio to external servers without user consent.

---

## Non-Goals (MVP Will Not Include)

- AI-assisted code generation or intelligent voice commands.
- Complex IDE automation (refactoring via voice, macros).
- Multi-language translation or real-time transcription for multiple languages.
- Cloud storage of transcripts or audio files.
- Full accessibility suite beyond basic hands-free text input.