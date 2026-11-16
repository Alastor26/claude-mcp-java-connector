# Claude Java Telemetry & MCP Starter

A lightweight, open-source **Java 17+** starter kit for inspecting Claude API usage, structuring organizational activity logs, and building Model Context Protocol (MCP) integrations in JVM environments.

Designed for Java backend and platform engineers who need clean, typed telemetry pipelines for Anthropic APIs without heavy external dependencies.

## Key Features
- **Zero-Bloat HTTP Client:** Uses Java's native `java.net.http.HttpClient` and Jackson for fast, reliable Anthropic API integration.
- **Typed Telemetry Models:** Strongly typed Java records (`record`) for token usage, model metadata, and normalized audit events.
- **Privacy-First Logging:** Built-in metadata-only mode strips prompt and completion payloads by default, capturing only timestamps, model IDs, and token counts.
- **MCP Ready:** Structured to plug directly into Java-based MCP servers and enterprise observability pipelines.

## Prerequisites
- **Java 17** or higher
- **Maven 3.8+**
- An Anthropic API key (`ANTHROPIC_API_KEY`)

## Quick Start

### 1. Clone the repository
```bash
git clone https://github.com
cd claude-mcp-java-connector
```

### 2. Build the project
```bash
mvn clean compile
```

### 3. Run the telemetry formatter
```bash
mvn exec:java -Dexec.mainClass="com.opensource.claude.TelemetryStarter"
```

## Roadmap
- [x] Java 17 record models & JSON telemetry serializer
- [ ] Async pagination wrapper for Anthropic organizational usage endpoints
- [ ] Spring Boot Starter auto-configuration module
- [ ] Java MCP SDK server example for querying token budgets

## Contributing
Contributions, issues, and pull requests are welcome! Feel free to open an issue or submit a PR to help grow the Java ecosystem around Claude.

## License
Distributed under the **MIT License**. See `LICENSE` for more information.
