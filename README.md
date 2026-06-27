<div align="center">

# 💱 Global Currency Exchange Management System

**A professional Java AWT desktop application for real-time currency conversion, trend analysis, and multi-currency comparison — powered by live exchange-rate APIs.**

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.8%2B-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![JFreeChart](https://img.shields.io/badge/JFreeChart-1.5.4-4A90D9?style=for-the-badge)](http://www.jfree.org/jfreechart/)
[![Gson](https://img.shields.io/badge/Gson-2.10.1-4285F4?style=for-the-badge&logo=google&logoColor=white)](https://github.com/google/gson)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

</div>

---

## 📌 Overview

The **Global Currency Exchange Management System** is a feature-rich desktop application built entirely with **Java AWT** (no Swing, no JavaFX). It provides real-time currency conversion across **170+ ISO 4217 currencies**, interactive trend charts via JFreeChart, and a multi-currency comparison panel — all backed by live data from the ExchangeRate-API.

Designed with **MVC architecture**, clean package separation, and off-EDT threading, this project demonstrates core Java engineering concepts including REST API integration, JSON parsing, multithreading, data visualization, and graceful offline fallback.

---

## ✨ Features

| Feature | Description |
|---|---|
| 💹 **Live Conversion** | Real-time rates from ExchangeRate-API for 170+ currencies |
| 🔄 **Instant Swap** | Flip From/To currencies with a single click |
| 📈 **Trend Charts** | 7-day exchange rate trend visualization using JFreeChart |
| 📊 **Multi-Currency Compare** | Side-by-side rate comparison for up to 5 currencies |
| ⏱️ **Auto-Refresh** | Configurable background rate refresh (60s intervals) |
| 📡 **Status Bar** | Live internet + API connectivity indicators |
| 🔌 **Offline Mode** | Graceful fallback to last fetched in-memory rates |
| 🎯 **Precision Control** | Configurable decimal precision (2–6 decimal places) |

---

## 🏗️ Architecture

This project follows a strict **MVC (Model-View-Controller)** pattern with no business logic inside any AWT panel class.

```
com.currencyapp/
├── ui/              ← AWT panels and main window (View)
│   ├── MainWindow.java
│   ├── ConverterPanel.java
│   ├── ChartPanel.java
│   └── ComparePanel.java
│
├── service/         ← API calls, rate calculation (Controller)
│   └── ExchangeRateService.java
│
├── model/           ← Data structures (Model)
│   ├── Currency.java
│   ├── ConversionResult.java
│   └── RateSnapshot.java
│
└── util/            ← JSON parsing, number formatting
    ├── JsonParser.java
    └── NumberFormatter.java
```

> **Threading rule:** All HTTP calls run on a background thread. Results post back to the AWT EDT via `EventQueue.invokeLater()`. The UI never blocks.

---

## 🛠️ Tech Stack

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

## 📋 Prerequisites

Before running the project, ensure the following are installed:

- **Java JDK 17+** — [Download here](https://adoptium.net/)
- **Apache Maven 3.8+** — [Download here](https://maven.apache.org/download.cgi)
- A free API key from [exchangerate-api.com](https://www.exchangerate-api.com/)
- A machine with a **display** (AWT requires a graphical environment — not a headless server)

---

## ⚡ Quick Start

### 1. Clone the repository

```bash
git clone https://github.com/your-username/currency-converter.git
cd currency-converter
```

### 2. Add your API key

Open `src/main/resources/config.properties` and replace the placeholder:

```properties
api.url=https://api.exchangerate-api.com/v4/latest/
api.key=YOUR_REAL_API_KEY_HERE
```

Get a free key (no credit card required) at [exchangerate-api.com](https://www.exchangerate-api.com/).

### 3. Build the project

```bash
mvn clean package -DskipTests
```

This produces a fat JAR in `target/` with all dependencies bundled.

### 4. Run the application

```bash
# Option A — recommended
java -jar target/currency-converter-1.0-SNAPSHOT-jar-with-dependencies.jar

# Option B — using the run script
chmod +x run.sh
./run.sh
```

---

## 📁 Project Structure

```
currency-converter/
├── pom.xml                        ← Maven build configuration
├── run.sh                         ← Launch script
├── src/
│   └── main/
│       ├── java/
│       │   └── com/currencyapp/   ← All Java source files
│       └── resources/
│           ├── currencies.json    ← 170+ ISO 4217 currency definitions
│           └── config.properties  ← API key and base URL
├── target/
│   └── currency-converter-1.0-SNAPSHOT-jar-with-dependencies.jar
└── lib/                           ← Optional local JAR fallback
    ├── gson-2.10.1.jar
    └── jfreechart-1.5.4.jar
```

---

## 🖥️ Application Panels

### Converter Panel
The main view. Select source and target currencies from searchable dropdowns, enter an amount, and hit **Convert**. The live rate label shows `1 FROM = X.XXXX TO` with a last-updated timestamp. Toggle **Auto-Refresh** to keep rates current automatically every 60 seconds.

### Chart Panel
Select any currency pair to render a **7-day trend line chart** via JFreeChart. Displays current rate, 7-day high, 7-day low, and percentage change over the period.

### Compare Panel
Choose a base currency and up to **5 target currencies**. A live table shows each currency code, full name, current rate, and a directional indicator (▲▼). Hit **Refresh** to re-fetch all rates at once.

### Status Bar (always visible)
Shows real-time internet connectivity (green/red indicator) and API status (**Live** / **Offline**) with the last successful update timestamp.

---

## ⚙️ Configuration

All app settings are stored in `src/main/resources/config.properties`:

```properties
# ExchangeRate-API Configuration
api.url=https://api.exchangerate-api.com/v4/latest/
api.key=YOUR_API_KEY_HERE
```

> The API key is **never hardcoded** in source files. It is always loaded at runtime from this config file.

---

## 🐛 Troubleshooting

| Problem | Likely Cause | Fix |
|---|---|---|
| `ClassNotFoundException` on launch | Wrong JAR used | Run the `-jar-with-dependencies.jar`, not the plain JAR |
| `NullPointerException` on startup | `currencies.json` missing from JAR | Ensure it's in `src/main/resources/`, then rebuild |
| No conversion result / API error | Dummy API key | Replace `api.key` in `config.properties` with a real key |
| Chart panel blank | JFreeChart not bundled | Rebuild with `mvn clean package` |
| `HeadlessException` | Running on a server without display | Run on a local desktop machine |
| `UnsupportedClassVersionError` | Java version mismatch | Ensure `java -version` shows 17+ |

---

## 🔑 Key Engineering Concepts Demonstrated

- **OOP** — Encapsulation, abstraction, and inheritance across model/service/UI layers
- **MVC Architecture** — Strict separation between data, logic, and presentation
- **REST API Integration** — Live HTTP requests via `java.net.http.HttpClient`
- **JSON Parsing** — Gson-based deserialization of API responses
- **Multithreading** — Background API calls with EDT-safe UI updates via `EventQueue.invokeLater()`
- **Data Visualization** — JFreeChart line charts for trend analysis
- **Offline Resilience** — In-memory rate caching with graceful degradation on network failure
- **Resource Bundling** — `ClassLoader.getResourceAsStream()` for JAR-safe resource loading

---

## 📜 License

This project is licensed under the **MIT License**. See [LICENSE](LICENSE) for details.

---

## 👤 Author

**Abhimanyu**
B.Tech Computer Science & Engineering — VIT Bhopal University

[![GitHub](https://img.shields.io/badge/GitHub-abhisand2815-181717?style=flat-square&logo=github)](https://github.com/abhisand2815)

---

<div align="center">
  <sub>Built with Java AWT · Powered by ExchangeRate-API · Visualized with JFreeChart</sub>
</div>
