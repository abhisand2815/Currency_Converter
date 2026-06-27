<div align="center">

# Global Currency Exchange Management System

**A Java AWT desktop application for real-time currency conversion, trend analysis, and multi-currency comparison — powered by live exchange-rate APIs.**

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.8%2B-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![JFreeChart](https://img.shields.io/badge/JFreeChart-1.5.4-4A90D9?style=for-the-badge)](http://www.jfree.org/jfreechart/)
[![Gson](https://img.shields.io/badge/Gson-2.10.1-4285F4?style=for-the-badge&logo=google&logoColor=white)](https://github.com/google/gson)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

</div>

---

## Overview

The **Global Currency Exchange Management System** is a feature-rich desktop application built entirely with **Java AWT** (no Swing, no JavaFX). It provides real-time currency conversion across **170+ ISO 4217 currencies**, interactive trend charts via JFreeChart, and a multi-currency comparison panel — all backed by live data from the ExchangeRate-API.

All HTTP calls run on background threads with results posted back to the AWT EDT via `EventQueue.invokeLater()`, ensuring the UI never blocks. When offline, the app falls back gracefully to the last fetched in-memory rates without crashing.

---

## Features

| Feature | Description |
|---|---|
| **Live Conversion** | Real-time rates from ExchangeRate-API for 170+ currencies |
| **Instant Swap** | Flip From/To currencies with a single click |
| **Trend Charts** | 7-day exchange rate trend visualization using JFreeChart |
| **Multi-Currency Compare** | Side-by-side rate comparison for up to 5 currencies |
| **Auto-Refresh** | Configurable background rate refresh every 60 seconds |
| **Status Bar** | Live internet and API connectivity indicators |
| **Offline Mode** | Graceful fallback to last fetched in-memory rates |
| **Precision Control** | Configurable decimal precision from 2 to 6 decimal places |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| UI Framework | Java AWT (pure — zero `javax.swing.*`) |
| Build Tool | Apache Maven 3.8+ |
| HTTP Client | `java.net.http.HttpClient` (built-in) |
| JSON Parsing | Gson 2.10.1 |
| Chart Rendering | JFreeChart 1.5.4 |
| Exchange Rate API | [ExchangeRate-API](https://www.exchangerate-api.com/) (free tier) |
| Currency Data | 170+ ISO 4217 currencies via bundled `currencies.json` |

---

## Prerequisites

- **Java JDK 17+** — [Download here](https://adoptium.net/)
- **Apache Maven 3.8+** — [Download here](https://maven.apache.org/download.cgi)
- A free API key from [exchangerate-api.com](https://www.exchangerate-api.com/)
- A machine with a display (AWT requires a graphical environment — not a headless server)

---

## Quick Start

**1. Clone the repository**

```bash
git clone https://github.com/abhisand2815/currency-converter.git
cd currency-converter
```

**2. Add your API key**

Open `src/main/resources/config.properties` and replace the placeholder:

```properties
api.url=https://api.exchangerate-api.com/v4/latest/
api.key=YOUR_REAL_API_KEY_HERE
```

Get a free key at [exchangerate-api.com](https://www.exchangerate-api.com/) — no credit card required.

**3. Build the project**

```bash
mvn clean package -DskipTests
```

This produces a fat JAR in `target/` with all dependencies bundled.

**4. Run the application**

```bash
# Recommended
java -jar target/currency-converter-1.0-SNAPSHOT-jar-with-dependencies.jar

# Using the run script
chmod +x run.sh
./run.sh
```

---

## Application Panels

### Converter Panel
The main view. Select source and target currencies from searchable dropdowns, enter an amount, and click **Convert**. The live rate label shows `1 FROM = X.XXXX TO` with a last-updated timestamp. Toggle **Auto-Refresh** to keep rates current automatically every 60 seconds.

### Chart Panel
Select any currency pair to render a 7-day trend line chart via JFreeChart. Displays the current rate, 7-day high, 7-day low, and percentage change over the period.

### Compare Panel
Choose a base currency and up to 5 target currencies. A live table shows each currency code, full name, current rate, and a directional indicator. Hit **Refresh** to re-fetch all rates at once.

### Status Bar
Always visible at the bottom. Shows real-time internet connectivity and API status (Live / Offline) with the last successful update timestamp.

---

## Configuration

All settings are loaded at runtime from `src/main/resources/config.properties`:

```properties
# ExchangeRate-API Configuration
api.url=https://api.exchangerate-api.com/v4/latest/
api.key=YOUR_API_KEY_HERE
```

The API key is never hardcoded in source files. It is always read from this config file via `ClassLoader.getResourceAsStream()`.

---

## Troubleshooting

| Problem | Likely Cause | Fix |
|---|---|---|
| `ClassNotFoundException` on launch | Wrong JAR used | Run the `-jar-with-dependencies.jar`, not the plain JAR |
| `NullPointerException` on startup | `currencies.json` missing from JAR | Ensure it is in `src/main/resources/`, then rebuild |
| No conversion result / API error | Dummy API key | Replace `api.key` in `config.properties` with a real key |
| Chart panel blank | JFreeChart not bundled | Rebuild with `mvn clean package` |
| `HeadlessException` | No display available | Run on a local desktop machine, not a server |
| `UnsupportedClassVersionError` | Java version mismatch | Ensure `java -version` shows 17+ |

---

## Key Engineering Concepts Demonstrated

- **OOP** — Encapsulation, abstraction, and inheritance across model, service, and UI layers
- **MVC Architecture** — Strict separation between data, logic, and presentation; no business logic inside any Panel class
- **REST API Integration** — Live HTTP requests via `java.net.http.HttpClient`
- **JSON Parsing** — Gson-based deserialization of API responses
- **Multithreading** — Background API calls with EDT-safe UI updates via `EventQueue.invokeLater()`
- **Data Visualization** — JFreeChart line charts for 7-day trend analysis
- **Offline Resilience** — In-memory rate caching with graceful degradation on network failure
- **Resource Bundling** — `ClassLoader.getResourceAsStream()` for JAR-safe resource loading

---

## License

This project is licensed under the **MIT License**. See [LICENSE](LICENSE) for details.

---

## Author

**Abhimanyu**
B.Tech Computer Science & Engineering — VIT Bhopal University

[![GitHub](https://img.shields.io/badge/GitHub-abhisand2815-181717?style=flat-square&logo=github)](https://github.com/abhisand2815)

---

<div align="center">
  <sub>Built with Java AWT &nbsp;&middot;&nbsp; Powered by ExchangeRate-API &nbsp;&middot;&nbsp; Visualized with JFreeChart</sub>
</div>
