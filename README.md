# Duke

A lightweight, fast and fun personal assistant chatbot built with Java&nbsp;11. Duke helps you organise todos, deadlines and events through a conversational interface available in both the command line and a modern JavaFX GUI.

## Table of Contents

- [Duke](#duke)
  - [Table of Contents](#table-of-contents)
  - [Features](#features)
  - [Quick Start](#quick-start)
  - [Usage](#usage)
  - [Contributing](#contributing)
  - [License](#license)
  - [Acknowledgements](#acknowledgements)

## Features

- [x] Add **Todo**, **Deadline** and **Event** tasks
- [x] Mark tasks as done or delete them
- [x] Edit existing tasks in-place
- [x] Natural-language date parsing (e.g.&nbsp;"next Friday 5&nbsp;pm")
- [x] Persistent storage — your data is saved between sessions
- [x] Powerful keyword and date search
- [x] Smart auto-complete and contextual help
- [x] WhatsApp-style modern GUI
- [x] Extensive unit and integration test-suite

## Quick Start

1. Ensure you have **Java&nbsp;11** installed.
2. Clone this repository:

   ```bash
   git clone https://github.com/<your-username>/duke.git
   cd duke
   ```
3. Build and run Duke using Gradle:

   ```bash
   ./gradlew run
   ```
4. Type `help` in the Duke prompt to see the full list of available commands.

## Usage

| Command | Example | Description |
|---------|---------|-------------|
| `todo TASK_DESCRIPTION` | `todo read book` | Adds a todo task |
| `deadline TASK_DESCRIPTION /by DATE` | `deadline submit report /by 2025-03-15` | Adds a deadline task |
| `event TASK_DESCRIPTION /at DATE` | `event project meeting /at 2025-03-10 14:00` | Adds an event task |
| `list` | | Lists all tasks |
| `done INDEX` | `done 2` | Marks the given task as completed |
| `delete INDEX` | `delete 3` | Deletes the given task |
| `find KEYWORD` | `find book` | Finds tasks containing the keyword |

Run `help` inside Duke for the complete command reference and advanced usage examples.

## Contributing

We welcome pull requests and issues!

1. Fork the repository and create your branch from **`master`**.
2. Follow the [Conventional Commits](https://www.conventionalcommits.org/) style.
3. Ensure the tests pass via `./gradlew test` before submitting.
4. Create a pull request using the provided template.

## License

Duke is released under the [MIT License](LICENSE).

## Acknowledgements

This project is based on the excellent teaching materials from [se-education](https://se-education.org/) and is maintained by our open-source contributors.
